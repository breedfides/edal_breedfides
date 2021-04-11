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
import java.io.FileNotFoundException;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class NativeLuceneIndexWriterThread extends IndexWriterThread {

	//StandardAnalyzer analyzer;
	IndexWriter writer = null;
	private Path pathToLastId = Paths.get(this.indexDirectory.toString(), "last_id.dat");

	protected NativeLuceneIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory, Logger implementationProviderLogger,
			IndexWriter writer) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.writer = writer;
		int numberDocs = 0;
		try {
			IndexReader reader = DirectoryReader.open(writer);
			numberDocs += reader.numDocs();
			reader.close();
			reader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.implementationProviderLogger.info("Number of docs at Startup: " + numberDocs);

		Path parent = this.pathToLastId.getParent();

		if (!Files.exists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				this.indexWriterThreadLogger.debug("Error while creating Index Directory" + e.getMessage());
			}
		}
		if (Files.exists(this.pathToLastId)) {

			try {
				FileInputStream fis = new FileInputStream(this.pathToLastId.toFile());
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.lastIndexedID = (int) ois.readObject();
				ois.close();
				fis.close();
				this.implementationProviderLogger.info("last IndexedID: "+this.lastIndexedID);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.indexWriterThreadLogger.debug("Last indexed ID : " + this.lastIndexedID);


		
	}

	protected void executeIndexing() {

		if (!this.sessionFactory.isClosed() && !this.isFinishIndexing()) {
			long executeIndexingStart = System.currentTimeMillis();
			final Session session = this.sessionFactory.openSession();
			session.setDefaultReadOnly(true);
			
			/** caching not neededm, also disabled to prevent memory leaks */
			session.setCacheMode(CacheMode.IGNORE);
			
			/** high value fetch objects faster, but more memory is needed */
			final int fetchSize = (int) Math.pow(10, 4);
			final long queryStartTime = System.currentTimeMillis();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder
					.createQuery(PrimaryDataEntityVersionImplementation.class);
			Root<PrimaryDataEntityVersionImplementation> root = criteria.from(PrimaryDataEntityVersionImplementation.class);
			criteria.where(criteriaBuilder.gt(root.get("id"), this.lastIndexedID)).orderBy(criteriaBuilder.asc(root.get("id")));
			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setMaxResults(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

			int indexedObjects = 0;
			int flushedObjects = 0;

			final long queryTime = System.currentTimeMillis() - queryStartTime;

			final long indexStartTime = System.currentTimeMillis();

			PrimaryDataEntityVersionImplementation version = null;
			while (results.next()) {
				version = (PrimaryDataEntityVersionImplementation) results.get(0);
				try {
					/** index each element */
					this.indexVersion(writer, version);
					indexedObjects++;
					//this.implementationProviderLogger.info("(exec)Indexed Version: " + version.getId()+" indexedversions: "+indexedObjects+" fetchsize: "+fetchSize);
				} catch (MetaDataException e) {
					e.printStackTrace();
				}
				if (indexedObjects % fetchSize == 0) {
					try {
						this.implementationProviderLogger.info("(exec)NativeLuceneIndexer_Commit() ");
						writer.commit();
						session.clear();
						flushedObjects += fetchSize;
						if (indexedObjects > 0 && version.getId() > this.lastIndexedID) {
							this.lastIndexedID = version.getId();
						}
					} catch (IOException e) {
						this.indexWriterThreadLogger.debug("Error while commiting changes to Index" + e.getMessage());
					}
				}
			}
			results.close();
			session.close();
			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			// this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects:
			// "+indexedObjects+" flushed: "+flushedObjects);
			DateFormat df = new SimpleDateFormat("mm:ss:SSS");

			if (indexedObjects > 0 || flushedObjects > 0) {
				try {
					this.lastIndexedID = version.getId();
					writer.commit();
				} catch (IOException e) {
					this.indexWriterThreadLogger.debug("Error while commiting changes to Index" + e.getMessage());
				}
				this.indexWriterThreadLogger.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects + " | "
						+ flushedObjects + " | " + df.format(new Date(indexingTime)) + " | " + df.format(new Date(queryTime)));
			}
			updateLastIndexedID(indexedObjects);
			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			if (flushedObjects != indexedObjects) {
				indexRestObjects();
			}
			// this.implementationProviderLogger.info("Finished execute Index task:
			// lastIndexedID: " + lastIndexedID);
			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
			this.indexLogger.debug("NativeLuceneIndexWriterThread time: "+executeIndexingFinishTime);
			// this.indexLogger.info("ExecuteIndexingTime(ms): "+executeIndexingFinishTime+"
			// Amount_of_indexed_objects: "+indexedObjects+" flushedObjects:
			// "+flushedObjects);
		}
	}

	private void updateLastIndexedID(int indexedObjects) {
		if (indexedObjects != 0) {
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
	}

	private void indexVersion(IndexWriter writer, PrimaryDataEntityVersionImplementation version) throws MetaDataException {
		MetaData metadata = version.getMetaData();
		Document doc = new Document();
		doc.add(new TextField(MetaDataImplementation.TITLE, getString(metadata.getElementValue(EnumDublinCoreElements.TITLE)), Store.YES));
		doc.add(new TextField(MetaDataImplementation.DESCRIPTION, getString(metadata.getElementValue(EnumDublinCoreElements.DESCRIPTION)),
				Store.YES));
		doc.add(new TextField(MetaDataImplementation.COVERAGE, getString(metadata.getElementValue(EnumDublinCoreElements.COVERAGE)), Store.YES));
		doc.add(new TextField(MetaDataImplementation.IDENTIFIER,
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getID()), Store.YES));
		doc.add(new TextField(MetaDataImplementation.SIZE,
				Long.toString(((DataSize) metadata.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize()), Store.YES));
		doc.add(new TextField(MetaDataImplementation.LANGUAGE,
				getString(((EdalLanguage) metadata.getElementValue(EnumDublinCoreElements.LANGUAGE)).getLanguage().toString()), Store.YES));
		Persons naturalPersons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CREATOR);
		Persons persons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
		persons.addAll(naturalPersons);
		LegalPerson legalPerson = (LegalPerson) metadata.getElementValue(EnumDublinCoreElements.PUBLISHER);
		persons.add(legalPerson);
		for (Person currentPerson : persons) {
			if (currentPerson instanceof NaturalPerson) {
				doc.add(new TextField(MetaDataImplementation.GIVENNAME, ((NaturalPerson) currentPerson).getGivenName(), Store.YES));
				doc.add(new TextField(MetaDataImplementation.SURENAME, ((NaturalPerson) currentPerson).getSureName(), Store.YES));
			}
			doc.add(new TextField(MetaDataImplementation.ADDRESSLINE, currentPerson.getAddressLine(), Store.YES));
			doc.add(new TextField(MetaDataImplementation.ZIP, currentPerson.getZip(), Store.YES));
			doc.add(new TextField(MetaDataImplementation.COUNTRY, currentPerson.getCountry(), Store.YES));
		}
		doc.add(new TextField(MetaDataImplementation.LEGALNAME, getString(legalPerson.getLegalName()), Store.YES));
		CheckSum checkSums = (CheckSum) metadata.getElementValue(EnumDublinCoreElements.CHECKSUM);
		for (CheckSumType checkSum : checkSums) {
			doc.add(new TextField(MetaDataImplementation.ALGORITHM, checkSum.getAlgorithm(), Store.YES));
			doc.add(new TextField(MetaDataImplementation.CHECKSUM, checkSum.getCheckSum(), Store.YES));
		}
		Subjects subjects = (Subjects) metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
		for (UntypedData subject : subjects) {
			doc.add(new TextField(MetaDataImplementation.SUBJECT, subject.getString(), Store.YES));
		}
		IdentifierRelation relations = (IdentifierRelation) metadata.getElementValue(EnumDublinCoreElements.RELATION);
		for (Identifier identifier : relations) {
			doc.add(new TextField(MetaDataImplementation.RELATION, identifier.getID(), Store.YES));
		}
		DateEvents events = (DateEvents) metadata.getElementValue(EnumDublinCoreElements.DATE);
		for (EdalDate date : events) {
			doc.add(new LongPoint(MetaDataImplementation.STARTDATE,
					date.getStartDate().getTimeInMillis()));
			if (date instanceof EdalDateRange) {
				doc.add(new LongPoint(MetaDataImplementation.ENDDATE,
						((EdalDateRange) date).getEndDate().getTimeInMillis()));
			}
		}
		if (metadata.getElementValue(EnumDublinCoreElements.FORMAT) instanceof EmptyMetaData) {
			doc.add(new TextField(MetaDataImplementation.MIMETYPE, "none", Store.YES));
			doc.add(new TextField(MetaDataImplementation.TYPE, "none", Store.YES));
		} else {
			doc.add(new TextField(MetaDataImplementation.MIMETYPE,
					getString(((DataFormat) metadata.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType()), Store.YES));
			doc.add(new TextField(MetaDataImplementation.TYPE, metadata.getElementValue(EnumDublinCoreElements.TYPE).toString(), Store.YES));
		}
		doc.add(new TextField(MetaDataImplementation.VERSIONID, Integer.toString(version.getId()), Store.YES));
		doc.add(new TextField(MetaDataImplementation.PRIMARYENTITYID, version.getPrimaryEntityId(),Store.YES));
		
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Error when adding Document to IndexWriter" + e.getMessage());
		}
		doc = null;
	}

	private String getString(UntypedData data) {
		String string = data.getString();
		return string == null ? "" : string;
	}

	private String getString(String string) {
		return string == null ? "" : string;
	}

	protected void indexRestObjects() {
		long executeIndexingStart = System.currentTimeMillis();
//
//		if (!this.sessionFactory.isClosed() && !this.isFinishIndexing()) {
//			final Session session = this.sessionFactory.openSession();
//
//			session.setDefaultReadOnly(true);
//
//			final long queryStartTime = System.currentTimeMillis();
//
//			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//			// this.implementationProviderLogger.info("Starting execute Index task:
//			// lastIndexedID: " + lastIndexedID);
//			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder
//					.createQuery(PrimaryDataEntityVersionImplementation.class);
//			Root<PrimaryDataEntityVersionImplementation> root = criteria.from(PrimaryDataEntityVersionImplementation.class);
//			criteria.where(criteriaBuilder.gt(root.get("id"), this.lastIndexedID)).orderBy(criteriaBuilder.asc(root.get("id")));
//			final ScrollableResults results = session.createQuery(criteria).scroll(ScrollMode.FORWARD_ONLY);
//
//			int indexedObjects = 0;
//			int flushedObjects = 0;
//
//			final long queryTime = System.currentTimeMillis() - queryStartTime;
//			final long indexStartTime = System.currentTimeMillis();
//			PrimaryDataEntityVersionImplementation version = null;
//			while (results.next()) {
//				/** index each element */
//				version = (PrimaryDataEntityVersionImplementation) results.get(0);
//				try {
//					this.indexVersion(writer, version);
//					this.implementationProviderLogger.info("(rest)Indexed Version: " + version.getId()+" indexedversions: "+indexedObjects+" fetchsize: "+0);
//				} catch (MetaDataException e) {
//					e.printStackTrace();
//				}
//				indexedObjects++;
//				flushedObjects++;
//			}
//			results.close();
//			session.close();
//			try {
//				if (!writer.isOpen()) {
//					this.implementationProviderLogger.info("\n WRITER IS CLOSED BUT TRYING TO COMMIT/ADD DOCS");
//				} else {
//					writer.flush();
//				}
//				if (version != null && version.getId() > this.lastIndexedID) {
//					this.lastIndexedID = version.getId();
//				}
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//
//			final long indexingTime = System.currentTimeMillis() - indexStartTime;
//			// this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects:
//			// "+indexedObjects+" flushed: "+flushedObjects);
//			DateFormat df = new SimpleDateFormat("mm:ss:SSS");
//
//			if (indexedObjects > 0 || flushedObjects > 0) {
//				this.indexWriterThreadLogger.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects + " | "
//						+ flushedObjects + " | " + df.format(new Date(indexingTime)) + " | " + df.format(new Date(queryTime)));
//			}
//
//			updateLastIndexedID(flushedObjects);
//
//			try {
//				Thread.sleep(Math.min(
//						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
//								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
//						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
//			} catch (final InterruptedException e) {
//				e.printStackTrace();
//			}
//			// this.implementationProviderLogger.info("Finished execute Index task:
//			// lastIndexedID: " + lastIndexedID);
//			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
//			this.indexLogger.debug("NativeLuceneIndexWriterThread indexRest time: "+executeIndexingFinishTime);
//			// this.indexLogger.info("indexRestObjects(ms): "+executeIndexingFinishTime+"
//			// Amount_of_indexed_objects: "+indexedObjects+" flushedObjects:
//			// "+flushedObjects);
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
			reader = DirectoryReader.open(writer);
			numberDocs += reader.numDocs();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + numberDocs);

		this.lastIndexedID = 0;

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}


	@Override
	public void run() {
		super.run();
		this.implementationProviderLogger.info("finished (NativeluceneIndexThread), now Counting Down Latch");
		this.countDownLatch.countDown();
	}
}