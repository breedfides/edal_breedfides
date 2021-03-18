/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.backend.lucene.lowlevel.index.impl.IndexAccessor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.hibernate.search.mapper.orm.work.SearchWorkspace;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class HibernateIndexWriterThread extends IndexWriterThread {

	protected HibernateIndexWriterThread(final SessionFactory sessionFactory, final Path indexDirectory,
			final Logger implementationProviderLogger) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		
		final Session session = this.sessionFactory.openSession();
		try {			
			File folder = new File(this.indexDirectory.toString());
			File[] listOfFiles = folder.listFiles();
			Directory directory = null;
			IndexReader reader = null;
			int numberDocs = 0;
//			for (File file : listOfFiles) {
//				  if (file.isDirectory()) {
//						try {					
//							directory = FSDirectory.open(Paths.get(this.indexDirectory.toString(),file.getName()));
//							reader = DirectoryReader.open( directory );
//							numberDocs += reader.numDocs();
//							reader.close();
//							directory.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//				  }
//			}
//			this.implementationProviderLogger.info("Number of docs after index rebuild: " + numberDocs);

			

		} finally {
			//readerProvider.close(reader);
			session.close();
		}
		Path path = Paths.get(this.indexDirectory.toString(), "last_id.dat");

		if (Files.exists(path)) {

			try {
				FileInputStream fis = new FileInputStream(path.toFile());
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.lastIndexedID = (int) ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.indexWriterThreadLogger.debug("Last indexed ID : " + this.lastIndexedID);
	}

	protected void executeIndexing() {
		long executeIndexingStart = System.currentTimeMillis();
		if (!this.sessionFactory.isClosed()) {
			final Session session = this.sessionFactory.openSession();

			session.setDefaultReadOnly(true);

			/** high value fetch objects faster, but more memory is needed */
			final int fetchSize = (int) Math.pow(10, 4);

//			fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//			fullTextSession.setCacheMode(CacheMode.NORMAL);

			final long queryStartTime = System.currentTimeMillis();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

			CriteriaQuery<MyUntypedData> criteria = criteriaBuilder.createQuery(MyUntypedData.class);
			Root<MyUntypedData> root = criteria.from(MyUntypedData.class);
			criteria.where(criteriaBuilder.gt(root.get("id"), this.lastIndexedID))
					.orderBy(criteriaBuilder.asc(root.get("id")));

			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setMaxResults(fetchSize)
					.scroll(ScrollMode.FORWARD_ONLY);

			int indexedObjects = 0;
			int flushedObjects = 0;

			final long queryTime = System.currentTimeMillis() - queryStartTime;

			final long indexStartTime = System.currentTimeMillis();
			final SearchSession searchFactory = Search.session(session);
			indexingPlan = searchFactory.indexingPlan(); 
			workspace = searchFactory.workspace(MyUntypedData.class);
			session.getTransaction().begin();
			while (results.next()) {
				indexedObjects++;
				/** index each element */
				MyUntypedData data = (MyUntypedData) results.get(0);
				indexingPlan.addOrUpdate((results.get(0)));

				if (indexedObjects % fetchSize == 0) {

					try {
						/** apply changes to indexes */
						workspace.flush();
						/** free memory since the queue is processed */
						//fullTextSession.clear();
						flushedObjects += fetchSize;
					} catch (Exception e) {
						throw new Error("Unable to read/write index files");
					}
					if (data.getId() > this.lastIndexedID) {
						this.lastIndexedID = data.getId() ;
					}
				}
			}
			session.getTransaction().commit();
			results.close();
			session.close();

			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			//this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects: "+indexedObjects+" flushed: "+flushedObjects);

			DateFormat df = new SimpleDateFormat("mm:ss:SSS");

			if (indexedObjects > 0 || flushedObjects > 0) {
				this.indexWriterThreadLogger
						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects
								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
								+ df.format(new Date(queryTime)));
			}

			if (flushedObjects != 0) {
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), "last_id.dat").toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(this.lastIndexedID);
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * HibernateIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								HibernateIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						HibernateIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			if (flushedObjects != indexedObjects) {
				indexRestObjects();
			}
			long executeIndexingFinishTime = System.currentTimeMillis()-executeIndexingStart;
			//this.indexLogger.info("ExecuteIndexingTime(ms): "+executeIndexingFinishTime+" Amount_of_indexed_objects: "+indexedObjects+" flushedObjects: "+flushedObjects);
		}
	}
	
	public void waitForFinish() {
		final long time = System.currentTimeMillis();
		this.indexWriterThreadLogger.debug("Wait for finish current indexing...");
		this.implementationProviderLogger.info("NATIVELUCINDEXER VOR LOCK()");
		this.lock.lock();
		this.implementationProviderLogger.info("NATIVELUCINDEXER NACH LOCK()");
		this.indexWriterThreadLogger.debug("Got lock for last indexing...");
		this.indexWriterThreadLogger.info("FINALZE indexing...");
		this.executeIndexing();
		this.implementationProviderLogger.info("NATIVELUCINDEXER NACH EXECUTEINDEXING");

		/** close SessionFactory so no indexing again */
		/** executeIndexing() runs only with open SessionFactory */

		this.sessionFactory.close();
		this.lock.unlock();
		this.implementationProviderLogger.info("NATIVELUCINDEXER NACH UNLOCK()");
		this.indexWriterThreadLogger
				.debug("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexLogger.info("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexWriterThreadLogger.debug("unlock Lock");
	}

	protected void indexRestObjects() {

		if (!this.sessionFactory.isClosed()) {
			long indexRestStart = System.currentTimeMillis();
			final Session session = this.sessionFactory.openSession();

			session.setDefaultReadOnly(true);

			//final FullTextSession fullTextSession = Search.getFullTextSession(session);

//			fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//			fullTextSession.setCacheMode(CacheMode.NORMAL);

			final long queryStartTime = System.currentTimeMillis();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

			CriteriaQuery<MyUntypedData> criteria = criteriaBuilder.createQuery(MyUntypedData.class);
			Root<MyUntypedData> root = criteria.from(MyUntypedData.class);
			criteria.where(criteriaBuilder.gt(root.get("id"), this.lastIndexedID))
					.orderBy(criteriaBuilder.asc(root.get("id")));

			final ScrollableResults results = session.createQuery(criteria).scroll(ScrollMode.FORWARD_ONLY);

			int indexedObjects = 0;
			int flushedObjects = 0;

			final long queryTime = System.currentTimeMillis() - queryStartTime;
			final long indexStartTime = System.currentTimeMillis();
			final SearchSession searchFactory = Search.session(session);
			indexingPlan = searchFactory.indexingPlan(); 
			workspace = searchFactory.workspace(MyUntypedData.class);
			session.getTransaction().begin();
			while (results.next()) {
				/** index each element */
				//fullTextSession.index(results.get(0));
				MyUntypedData data = (MyUntypedData) results.get(0);
				indexingPlan.addOrUpdate((results.get(0)));

				if (data.getId() > this.lastIndexedID) {
					this.lastIndexedID = data.getId() ;
				}
				indexedObjects++;
				flushedObjects++;
			}
			try {
				/** apply changes to indexes */
				workspace.flush();
				/** free memory since the queue is processed */
				//fullTextSession.clear();
			} catch (Exception e) {
				throw new Error("Unable to read/write index files");
			}
			session.getTransaction().commit();
			results.close();
			session.close();

			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			//this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects: "+indexedObjects+" flushed: "+ flushedObjects);

			DateFormat df = new SimpleDateFormat("mm:ss:SSS");

			if (indexedObjects > 0 || flushedObjects > 0) {
				this.indexWriterThreadLogger
						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects
								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
								+ df.format(new Date(queryTime)));
			}

			if (flushedObjects != 0) {
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), "last_id.dat").toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(this.lastIndexedID);
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * HibernateIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								HibernateIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						HibernateIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			long executeIndexingFinishTime = System.currentTimeMillis()-indexRestStart;
			//this.indexLogger.info("IndexRestObejects(ms): "+executeIndexingFinishTime+" Amount_of_indexed_objects: "+indexedObjects+ "flushedObjects: "+flushedObjects);
		}
	}


	protected void resetIndexThread() {

		this.requestForReset = true;

		this.indexWriterThreadLogger.debug("Reseting index structure...");

		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.implementationProviderLogger.info("Start reseting index structure...");

		final Session session = this.sessionFactory.openSession();
		session.setDefaultReadOnly(true);
		File folder = new File(this.indexDirectory.toString());
		File[] listOfFiles = folder.listFiles();
		Directory directory = null;
		IndexReader reader = null;
		int numberDocs = 0;
		for (File file : listOfFiles) {
			  if (file.isDirectory()) {
					try {					
						directory = FSDirectory.open(Paths.get(this.indexDirectory.toString(),file.getName()));
						reader = DirectoryReader.open( directory );
						numberDocs += reader.numDocs();
					} catch (IOException e) {
						e.printStackTrace();
					}
			  }
		}
		this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + numberDocs);
		//final FullTextSession fullTextSession = Search.getFullTextSession(session);

//		fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//		fullTextSession.setCacheMode(CacheMode.NORMAL);

		workspace.purge();
		//fullTextSession.flushToIndexes();
//
//		final SearchFactory searchFactory = Search.getFullTextSession(session).getSearchFactory();
//		final IndexReaderAccessor readerProvider = searchFactory.getIndexReaderAccessor();
//		final IndexReader reader = readerProvider.open(MyUntypedData.class);

		//this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + reader.numDocs());

		//readerProvider.close(reader);
		session.close();

		this.lastIndexedID = 0;

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}
}