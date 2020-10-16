/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
public class IndexWriterThread extends EdalThread {

	private static final int SLEEP_RUNTIME_FACTOR = 2;
	private static final long MIN_THREAD_SLEEP_MILLISECONDS = 500;
	private static final long MAX_THREAD_SLEEP_MILLISECONDS = 2000;

	private SessionFactory sessionFactory;

	private int lastIndexedID = 0;
	private Path indexDirectory;
	private Logger indexWriterThreadLogger = null;
	private Logger implementationProviderLogger = null;
	SearchIndexingPlan indexingPlan = null;
	SearchWorkspace workspace = null;

	private boolean requestForReset = false;

	/** create Lock with fairness parameter true */
	private final ReentrantLock lock = new ReentrantLock(true);

	/** create {@link CountDownLatch} to wait for finishing index */
	private CountDownLatch latch = new CountDownLatch(1);

	/**
	 * Constructor for IndexWriterThread
	 * 
	 * @param sessionFactory
	 *            the current {@link SessionFactory} object.
	 * @param indexDirectory
	 *            the mount path of the running instance
	 * @param implementationProviderLogger
	 *            the logger of the used {@link ImplementationProvider}
	 */

	protected IndexWriterThread(final SessionFactory sessionFactory, final Path indexDirectory,
			final Logger implementationProviderLogger) {

		super();
		this.indexWriterThreadLogger = LogManager.getLogger("IndexWriterThread");
		this.implementationProviderLogger = implementationProviderLogger;

		this.indexDirectory = indexDirectory;

		this.sessionFactory = sessionFactory;

		final Session session = this.sessionFactory.openSession();

		//final IndexAccessor readerProvider = searchFactory

		//final IndexReader reader = readerProvider.open(MyUntypedData.class);

		try {
			try {
				Directory directory = FSDirectory.open( Paths.get(this.indexDirectory.toString(),"UntypedData"));
				IndexReader reader = DirectoryReader.open( directory );
				this.implementationProviderLogger.info("Number of docs after index rebuild: " + reader.numDocs());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			this.implementationProviderLogger
//					.info("Starting IndexWriterThread (current number of documents : " + reader.numDocs() + ")");

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

		} finally {
			//readerProvider.close(reader);
			session.close();
		}
	}

	private void executeIndexing() {

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
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * IndexWriterThread.SLEEP_RUNTIME_FACTOR,
								IndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						IndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			if (flushedObjects != indexedObjects) {
				indexRestObjects();
			}

		}
	}

	private void indexRestObjects() {

		if (!this.sessionFactory.isClosed()) {
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * IndexWriterThread.SLEEP_RUNTIME_FACTOR,
								IndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						IndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the lock
	 */
	private ReentrantLock getLock() {
		return lock;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {

		while (!this.sessionFactory.isClosed()) {
			this.indexWriterThreadLogger.debug("Wait for Reseting the index structure: " + this.requestForReset);

			if (!this.requestForReset) {
				this.indexWriterThreadLogger.debug("try lock run method");
				this.getLock().lock();
				latch = new CountDownLatch(1);
				this.indexWriterThreadLogger.debug("locked run method");
				this.executeIndexing();
				this.indexWriterThreadLogger.debug("unlock run method");
				this.getLock().unlock();
				latch.countDown();
			}
		}

	}

	/**
	 * wait until the indexing method is finished
	 */
	void waitForFinish() {

		final long time = System.currentTimeMillis();
		this.indexWriterThreadLogger.debug("Wait for finish current indexing...");
		this.lock.lock();
		this.indexWriterThreadLogger.debug("Got lock for last indexing...");
		this.indexWriterThreadLogger.info("FINALZE indexing...");
		this.executeIndexing();

		/** close SessionFactory so no indexing again */
		/** executeIndexing() runs only with open SessionFactory */

		this.sessionFactory.close();
		this.lock.unlock();
		this.indexWriterThreadLogger
				.debug("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexWriterThreadLogger.debug("unlock Lock");
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
		try {
			Directory directory = FSDirectory.open( Paths.get(this.indexDirectory.toString(),"UntypedData"));
			IndexReader reader = DirectoryReader.open( directory );
			this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + reader.numDocs());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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