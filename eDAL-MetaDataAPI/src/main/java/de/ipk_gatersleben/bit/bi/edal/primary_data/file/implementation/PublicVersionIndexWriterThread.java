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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
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

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class PublicVersionIndexWriterThread extends IndexWriterThread {

	private Path indexPath;
	IndexWriter writer = null;
	StandardAnalyzer analyzer;
	IndexSearcher searcher = null;
	IndexReader reader = null;
	int indexedVersions = 0;
	int flushedObjects = 0;
	public static final String INDEX_NAME = "Master_Index";
	final int fetchSize = (int) Math.pow(10, 4);
	
	protected PublicVersionIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory,
			Logger implementationProviderLogger, IndexWriter writer) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.writer = writer;
		int numberDocs = 0;
		try {					
			reader = DirectoryReader.open( writer );
			searcher = new IndexSearcher(reader);
			numberDocs += reader.numDocs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.implementationProviderLogger.info("Number of docs at Startup: " + numberDocs);
		Path path = Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat");

		if (Files.exists(path)) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(path.toFile());
				ois = new ObjectInputStream(fis);
				this.lastIndexedID = (int) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}finally {
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.implementationProviderLogger.info("Last indexed public reference: " + this.lastIndexedID);
		this.indexWriterThreadLogger.debug("Last indexed public reference: " + this.lastIndexedID);
		indexPath = Paths.get(indexDirectory.toString(),INDEX_NAME);
		
		//Create IndexSearcher from IndexDirectory
	}



	protected void executeIndexing() {

		if (!this.sessionFactory.isClosed()) {
			final Session session = this.sessionFactory.openSession();
			indexedVersions = 0;
			flushedObjects = 0;
			long executeIndexingStart = System.currentTimeMillis();

			session.setDefaultReadOnly(true);

			/** high value fetch objects faster, but more memory is needed */
			final int fetchSize = (int) Math.pow(10, 4);

//			fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//			fullTextSession.setCacheMode(CacheMode.NORMAL);

			final long queryStartTime = System.currentTimeMillis();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			//this.implementationProviderLogger.info("Starting execute Index task: lastIndexedID: " + lastIndexedID);
			CriteriaQuery<PublicReferenceImplementation> criteria = criteriaBuilder.createQuery(PublicReferenceImplementation.class);
			Root<PublicReferenceImplementation> root = criteria.from(PublicReferenceImplementation.class);
			Predicate predicateId = criteriaBuilder.gt(root.get("id"), this.lastIndexedID);
			Predicate predicateAccepted = criteriaBuilder.equal(root.get("publicationStatus"), PublicationStatus.ACCEPTED);
			Predicate predicateType = criteriaBuilder.equal(root.get("identifierType"), PersistentIdentifier.DOI);
			criteria.where(criteriaBuilder.and(predicateId,predicateAccepted,predicateType))
					.orderBy(criteriaBuilder.asc(root.get("id")));

			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setMaxResults(fetchSize)
					.scroll(ScrollMode.FORWARD_ONLY);

			final long queryTime = System.currentTimeMillis() - queryStartTime;

			final long indexStartTime = System.currentTimeMillis();
			//this.implementationProviderLogger.info("Indexing Path: ___: " + indexPath.toString());
			PublicReferenceImplementation publicRef = null;
			//session.getTransaction().begin();
			while (results.next()) {
				publicRef = (PublicReferenceImplementation) results.get(0);
				//this.indexVersion(writer, publicRef);
				this.updateIndex(publicRef, session);
			}
			try {
				writer.commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				if (indexedVersions > 0 && publicRef.getId() > this.lastIndexedID) {
					this.lastIndexedID = publicRef.getId() ;
					this.implementationProviderLogger.info("NEW indexed public reference: " + this.lastIndexedID);
				}
			//session.getTransaction().commit();
			results.close();
			session.close();
			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			//this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects: "+indexedObjects+" flushed: "+flushedObjects);
			DateFormat df = new SimpleDateFormat("mm:ss:SSS");

			if (indexedVersions > 0 || flushedObjects > 0) {
				this.indexWriterThreadLogger
						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedVersions
								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
								+ df.format(new Date(queryTime)));
			}
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				fos = new FileOutputStream(
						Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat").toFile());
				oos = new ObjectOutputStream(fos);
				oos.writeObject(this.lastIndexedID);
			} catch (IOException e) {
				e.printStackTrace();
			}finally  {
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			if (flushedObjects != indexedVersions) {
				//indexRestObjects();
			}
			//this.implementationProviderLogger.info("Finished execute Index task: lastIndexedID: " + lastIndexedID);
			long executeIndexingFinishTime = System.currentTimeMillis()-executeIndexingStart;
			//this.indexLogger.info("ExecuteIndexingTime(ms): "+executeIndexingFinishTime+" Amount_of_indexed_objects: "+indexedObjects+" flushedObjects: "+flushedObjects);
		}
	}
	
	private void updateIndex(PublicReferenceImplementation publicRef, Session session) {
		this.implementationProviderLogger.info("UPDATE VERSION PrefId:  "+publicRef.getId());
		String hql = "from PrimaryDataFileImplementation where id = :fileId";
		PrimaryDataFileImplementation test = session.createQuery(hql,PrimaryDataFileImplementation.class).setParameter("fileId", publicRef.getVersion().getPrimaryEntityId()).uniqueResult();
		if(!test.isDirectory()) {
			this.updateVersion(test, session, publicRef);
			indexedVersions++;
		}
		else {
			hql = "from PrimaryDataFileImplementation s where s.parentDirectory = :dir";
			List<PrimaryDataFileImplementation> directory = session.createQuery(hql,PrimaryDataFileImplementation.class)
					.setParameter("dir", publicRef.getVersion().getEntity())
					.list();
			Stack<PrimaryDataFileImplementation> stack = new Stack<>();
			for(PrimaryDataFileImplementation file : directory) {
				if(file.isDirectory()) {
					stack.add(file);
				}else {
					this.updateVersion(file, session, publicRef);
					indexedVersions++;
				}
			}
			while(!stack.isEmpty()) {
				PrimaryDataFileImplementation dir = stack.pop();
				
				
				this.updateVersion(dir, session, publicRef);
				indexedVersions++;
				List<PrimaryDataFileImplementation> files = session.createQuery(hql)
						.setParameter("dir", dir)
						.list();
				for(PrimaryDataFileImplementation file : files) {
					if(file.isDirectory()) {
						stack.add(file);
					}else {
						this.updateVersion(file, session, publicRef);
						indexedVersions++;
					}
				}
			}
		}
	}
	
	public void waitForFinish() {
		final long time = System.currentTimeMillis();
		this.indexWriterThreadLogger.debug("Wait for finish current indexing...");
		this.implementationProviderLogger.info("PUBLIC VOR LOCK()");
		this.lock.lock();
		this.implementationProviderLogger.info("PUBLIC NACH LOCK()");
		this.indexWriterThreadLogger.debug("Got lock for last indexing...");
		this.indexWriterThreadLogger.info("FINALZE indexing...");
		this.executeIndexing();
		this.implementationProviderLogger.info("PUBLIC NACH EXECUTEINDEXING");

		/** close SessionFactory so no indexing again */
		/** executeIndexing() runs only with open SessionFactory */

		this.sessionFactory.close();
		this.lock.unlock();
		this.implementationProviderLogger.info("PUBLIC NACH UNLOCK()");
		this.indexWriterThreadLogger
				.debug("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexLogger.info("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexWriterThreadLogger.debug("unlock Lock");
	}
	
	private void updateVersion(PrimaryDataFileImplementation file, Session session, PublicReferenceImplementation publicRef) {
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PrimaryDataEntityVersionImplementation> versionCriteria = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
		Root<PrimaryDataEntityVersionImplementation> versionRoot = versionCriteria.from(PrimaryDataEntityVersionImplementation.class);
		versionCriteria.where(criteriaBuilder.equal(versionRoot.get("primaryEntityId"), file.getID()));
		versionCriteria.orderBy(criteriaBuilder.desc(versionRoot.get("revision")));
		List<PrimaryDataEntityVersionImplementation> versions = session.createQuery(versionCriteria).setMaxResults(1).list();
		QueryParser parser = new QueryParser(MetaDataImplementation.VERSIONID, new StandardAnalyzer());
		for(PrimaryDataEntityVersionImplementation version : versions) {
		    org.apache.lucene.search.Query luceneQuery = null;
			try {
				luceneQuery = parser.parse(Integer.toString(version.getId()));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		    ScoreDoc[] hits2;
			try {
				hits2 = searcher.search(luceneQuery, 1).scoreDocs;
		        for(int i = 0; i < hits2.length; i++) {
		        	Document doc = searcher.doc(hits2[i].doc);
		        	writer.deleteDocuments(new Term(MetaDataImplementation.VERSIONID,doc.get(MetaDataImplementation.VERSIONID)));
		        	doc.add(new TextField(MetaDataImplementation.ENTITYID,publicRef.getVersion().getPrimaryEntityId(),Store.YES));
		        	writer.addDocument(doc);
		        	indexedVersions++;
					this.implementationProviderLogger.info("UPDATED VERSION: "+doc.get(MetaDataImplementation.VERSIONID));
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		if (indexedVersions % fetchSize == 0) {
			try {
				writer.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
				flushedObjects += fetchSize;
				if (indexedVersions > 0 && publicRef.getId() > this.lastIndexedID) {
					this.lastIndexedID = publicRef.getId() ;
				}
		}
	}


	protected void indexRestObjects() {
//		long executeIndexingStart = System.currentTimeMillis();
//
//		if (!this.sessionFactory.isClosed()) {
//			final Session session = this.sessionFactory.openSession();
//
//			session.setDefaultReadOnly(true);
//
//			//final FullTextSession fullTextSession = Search.getFullTextSession(session);
//
////			fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
////			fullTextSession.setCacheMode(CacheMode.NORMAL);
//
//			final long queryStartTime = System.currentTimeMillis();
//
//			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//			//this.implementationProviderLogger.info("Starting execute Index task: lastIndexedID: " + lastIndexedID);
//			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
//			Root<PrimaryDataEntityVersionImplementation> root = criteria.from(PrimaryDataEntityVersionImplementation.class);
//			criteria.where(criteriaBuilder.gt(root.get("id"), this.lastIndexedID))
//					.orderBy(criteriaBuilder.asc(root.get("id")));
//
//			final ScrollableResults results = session.createQuery(criteria).scroll(ScrollMode.FORWARD_ONLY);
//
//			int indexedObjects = 0;
//			int flushedObjects = 0;
//
//			final long queryTime = System.currentTimeMillis() - queryStartTime;
//			final long indexStartTime = System.currentTimeMillis();
//		    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//			PrimaryDataEntityVersionImplementation version = null;
//			while (results.next()) {
//				/** index each element */
//				//fullTextSession.index(results.get(0));
//				version = (PrimaryDataEntityVersionImplementation) results.get(0);
////				try {
////					this.indexVersion(writer, version);
////				} catch (MetaDataException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				indexedObjects++;
////				flushedObjects++;
//			}
//			results.close();
//			session.close();
//			try {
//				writer.commit();
//				if(version != null)
//				//this.implementationProviderLogger.info("indexedObjects: "+indexedObjects+" version.getID= "+version.getId()+" lastIndexed; "+this.lastIndexedID);
//				if (indexedObjects > 0 && version.getId() > this.lastIndexedID) {
//					this.lastIndexedID = version.getId() ;
//				}
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//
//			final long indexingTime = System.currentTimeMillis() - indexStartTime;
//		//	this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects: "+indexedObjects+" flushed: "+flushedObjects);
//			DateFormat df = new SimpleDateFormat("mm:ss:SSS");
//
//			if (indexedObjects > 0 || flushedObjects > 0) {
//				this.indexWriterThreadLogger
//						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects
//								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
//								+ df.format(new Date(queryTime)));
//			}
//
//			try {
//				FileOutputStream fos = new FileOutputStream(
//						Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat").toFile());
//				ObjectOutputStream oos = new ObjectOutputStream(fos);
//				oos.writeObject(this.lastIndexedID);
//				oos.close();
//				//fos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			try {
//				Thread.sleep(Math.min(
//						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
//								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
//						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
//			} catch (final InterruptedException e) {
//				e.printStackTrace();
//			}
//			//this.implementationProviderLogger.info("Finished execute Index task: lastIndexedID: " + lastIndexedID);
//			long executeIndexingFinishTime = System.currentTimeMillis()-executeIndexingStart;
//			//this.indexLogger.info("indexRestObjects(ms): "+executeIndexingFinishTime+" Amount_of_indexed_objects: "+indexedObjects+" flushedObjects: "+flushedObjects);
//		}
	}

	protected void resetIndexThread() {

		this.implementationProviderLogger.info("#####\n######  Reset has been called");
		this.requestForReset = true;

		this.indexWriterThreadLogger.debug("Reseting index structure...");

		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.implementationProviderLogger.info("Start reseting index structure...");
		
		IndexReader reader = null;
		int numberDocs = 0;
		try {					
			reader = DirectoryReader.open( writer );
			numberDocs += reader.numDocs();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + numberDocs);
		//final FullTextSession fullTextSession = Search.getFullTextSession(session);

//		fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//		fullTextSession.setCacheMode(CacheMode.NORMAL);

		//fullTextSession.flushToIndexes();
//
//		final SearchFactory searchFactory = Search.getFullTextSession(session).getSearchFactory();
//		final IndexReaderAccessor readerProvider = searchFactory.getIndexReaderAccessor();
//		final IndexReader reader = readerProvider.open(MyUntypedData.class);

		//this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + reader.numDocs());

		//readerProvider.close(reader);

		this.lastIndexedID = 0;

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}
	
	@Override
	public void run() {
		super.run();
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try {
//			if(writer != null && writer.isOpen())
//				this.writer.close();
//		} catch (IOException e) {
//			try {
//				this.writer.rollback();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}