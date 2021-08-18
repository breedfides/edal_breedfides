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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
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
	protected Boolean lastIDChanged = false;

	

	IndexWriter writer = null;
	protected static int lastIndexedID = 0;

	private Path pathToLastId = Paths.get(this.indexDirectory.toString(), "NativeLucene_last_id.dat");
	/** high value fetch objects faster, but more memory is needed */
	final int fetchSize = (int) Math.pow(12, 5);

	protected NativeLuceneIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory, Logger implementationProviderLogger,
			IndexWriter writer) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.writer = writer;
		int numberDocs = 0;
		try {
			IndexReader reader = DirectoryReader.open(writer);
			numberDocs = reader.numDocs();
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
				this.setLastID((int) ois.readObject());
				ois.close();
				fis.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.indexWriterThreadLogger.debug("Last indexed ID : " + NativeLuceneIndexWriterThread.getLastID());


		
	}

	protected void executeIndexing() {

		if (!this.sessionFactory.isClosed() && !this.isFinishIndexing()) {
			long executeIndexingStart = System.currentTimeMillis();
			final Session session = this.sessionFactory.openSession();
			session.setDefaultReadOnly(true);
			
			/** caching not neededm, also disabled to prevent memory leaks */
			session.setCacheMode(CacheMode.IGNORE);
			
			final long queryStartTime = System.currentTimeMillis();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder
					.createQuery(PrimaryDataEntityVersionImplementation.class);
			Root<PrimaryDataEntityVersionImplementation> root = criteria.from(PrimaryDataEntityVersionImplementation.class);
			criteria.where(criteriaBuilder.gt(root.get("id"), NativeLuceneIndexWriterThread.getLastID())).orderBy(criteriaBuilder.asc(root.get("id")));
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
					this.setLastID(version.getId());
				} catch (MetaDataException e) {
					e.printStackTrace();
				}
				if (indexedObjects % fetchSize == 0) {
					flushedObjects += fetchSize;
				}
			}
			session.clear();
			results.close();
			session.close();
			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			// this.indexLogger.info("indexingTime: "+indexingTime+ " amount_of_objects:
			// "+indexedObjects+" flushed: "+flushedObjects);
			DateFormat df = new SimpleDateFormat("mm:ss:SSS");
			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
			if (indexedObjects > 0 || flushedObjects > 0) {
				try {
					writer.commit();				
				} catch (IOException e) {
					this.indexWriterThreadLogger.debug("Error while commiting changes to Index" + e.getMessage());
				}
				this.indexWriterThreadLogger.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects + " | "
						+ flushedObjects + " | " + df.format(new Date(indexingTime)) + " | " + df.format(new Date(queryTime)));
				this.implementationProviderLogger.debug("[NativeLuceneIndexWriterThread] Indexing Time: "+executeIndexingFinishTime);
			}
			try {
				FileOutputStream fos = new FileOutputStream(
						Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat").toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(PublicVersionIndexWriterThread.getLastID());
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
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
						Paths.get(this.indexDirectory.toString(), "NativeLucene_last_id.dat").toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(NativeLuceneIndexWriterThread.getLastID());
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
		StringBuilder builder = new StringBuilder();
		doc.add(new TextField(MetaDataImplementation.IDENTIFIER,
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()), Store.YES));		
		doc.add(new TextField(MetaDataImplementation.RELATEDIDENTIFIERTYPE,
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelatedIdentifierType().value()), Store.YES));
		doc.add(new TextField(MetaDataImplementation.RELATIONTYPE,
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()), Store.YES));
		
		doc.add(new TextField(MetaDataImplementation.SIZE,
				String.format("%014d",((DataSize) metadata.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize()),Store.YES));
		
		doc.add(new TextField(MetaDataImplementation.LANGUAGE,
				getString(((EdalLanguage) metadata.getElementValue(EnumDublinCoreElements.LANGUAGE)).getLanguage().toString()), Store.YES));
		Persons creators = (Persons) metadata.getElementValue(EnumDublinCoreElements.CREATOR);
		for (Person currentPerson : creators) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(" ");
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(" ");
				doc.add(new TextField(MetaDataImplementation.CREATORNAME, builder.toString(), Store.YES));
			}
			builder.append(( currentPerson).getAddressLine());
			builder.append(" ");
			builder.append(( currentPerson).getZip());
			builder.append(" ");
			builder.append(( currentPerson).getCountry());
			doc.add(new TextField(MetaDataImplementation.PERSON, builder.toString(), Store.YES));
		}
		Persons persons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
		LegalPerson legalPerson = (LegalPerson) metadata.getElementValue(EnumDublinCoreElements.PUBLISHER);
		builder.setLength(0);
		builder.append(( legalPerson).getLegalName());
		builder.append(" ");
		builder.append(( legalPerson).getAddressLine());
		builder.append(" ");
		builder.append(( legalPerson).getZip());
		builder.append(" ");
		builder.append(( legalPerson).getCountry());
		doc.add(new TextField(MetaDataImplementation.LEGALPERSON, builder.toString(), Store.YES));
		/** 
		 * Stringbuilder to combine multiple Values into one large String to store the text in one field per categopry
		 * Not used for Relations and dates, because these values occur rarely more than once per Version/Document
		 *  */
		for (Person currentPerson : persons) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(" ");
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(" ");
				doc.add(new TextField(MetaDataImplementation.CONTRIBUTORNAME, builder.toString(), Store.YES));
			}
			builder.append(( currentPerson).getAddressLine());
			builder.append(" ");
			builder.append(( currentPerson).getZip());
			builder.append(" ");
			builder.append(( currentPerson).getCountry());
			doc.add(new TextField(MetaDataImplementation.CONTRIBUTOR, builder.toString(), Store.YES));
		}
		CheckSum checkSums = (CheckSum) metadata.getElementValue(EnumDublinCoreElements.CHECKSUM);
		builder.setLength(0);
		if(checkSums.size() > 1) {
			for (CheckSumType checkSum : checkSums) {
				builder.append(checkSum.getAlgorithm());
				builder.append(" ");
				builder.append(checkSum.getCheckSum());
				builder.append(", ");
				doc.add(new TextField(MetaDataImplementation.CHECKSUM, checkSum.getAlgorithm(), Store.YES));
			}
		}else if(checkSums.size() == 1) {
			CheckSumType checkSum = checkSums.iterator().next();
			doc.add(new TextField(MetaDataImplementation.ALGORITHM, checkSum.getAlgorithm(), Store.YES));
			doc.add(new TextField(MetaDataImplementation.CHECKSUM, checkSum.getCheckSum(), Store.YES));
		}
		Subjects subjects = (Subjects) metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
		builder.setLength(0);
		for (UntypedData subject : subjects) {
			builder.append(subject.getString());
			builder.append(" ");
		}
		doc.add(new TextField(MetaDataImplementation.SUBJECT, builder.toString(), Store.YES));
		IdentifierRelation relations = (IdentifierRelation) metadata.getElementValue(EnumDublinCoreElements.RELATION);
		for (Identifier identifier : relations) {
			builder.setLength(0);
			builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()));
			builder.append(" ");
			builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelatedIdentifierType().value()));
			builder.append(" ");
			builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()));
			builder.append(", ");
			doc.add(new TextField(MetaDataImplementation.RELATION, builder.toString(), Store.YES));
		}
		DateEvents events = (DateEvents) metadata.getElementValue(EnumDublinCoreElements.DATE);
		for (EdalDate date : events) {
			doc.add(new TextField(MetaDataImplementation.STARTDATE,
					DateTools.timeToString(date.getStartDate().getTimeInMillis(), Resolution.DAY),Store.YES));
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
		StringJoiner allFieldsJoiner = new StringJoiner(" ");
		for(IndexableField field : doc.getFields()){	
			allFieldsJoiner.add(field.stringValue());
		}
		doc.add(new StringField(MetaDataImplementation.ALL, allFieldsJoiner.toString(),Store.YES));
		doc.add(new StringField(MetaDataImplementation.VERSIONID, Integer.toString(version.getId()), Store.YES));
		doc.add(new StringField(MetaDataImplementation.PRIMARYENTITYID, version.getPrimaryEntityId(),Store.YES));
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
//		long executeIndexingStart = System.currentTimeMillis();
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

		this.setLastID(0);

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}
	
	protected void commitRequired() {
		this.lastIDChanged = true;
	}
	
	protected void setLastID(int val) {
		NativeLuceneIndexWriterThread.lastIndexedID = val;
	}
	protected static int getLastID() {
		return NativeLuceneIndexWriterThread.lastIndexedID;
	}


	@Override
	public void run() {
		super.run();
		this.countDownLatch.countDown();
	}	
}