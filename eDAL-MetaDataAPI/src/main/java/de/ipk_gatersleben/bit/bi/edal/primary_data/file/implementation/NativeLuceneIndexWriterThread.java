/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

	public static final String NATIVE_INDEXER_LAST_ID = "last_id_publicreference.dat";
	private static final String ID = "id";
	private static final String DELIMITER = " ";
	private static final String NONE = "none";
	private static final char HYPHEN = '-';
	protected Boolean lastIDChanged = false;
	private IndexWriter writer = null;
	protected int lastIndexedID = 0;

	private final Path PATH_TO_LAST_ID = Paths.get(this.indexDirectory.toString(), NativeLuceneIndexWriterThread.NATIVE_INDEXER_LAST_ID);
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
			this.indexLogger.debug("Error opening and closing the IndexReader: "+e.getMessage());
		}
		this.implementationProviderLogger.info("Number of docs at Startup: " + numberDocs);
		Path parent = this.PATH_TO_LAST_ID.getParent();

		if (!Files.exists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				this.indexLogger.debug("Error while creating Index Directory" + e.getMessage());
			}
		}
		if (Files.exists(this.PATH_TO_LAST_ID)) {

			try {
				FileInputStream fis = new FileInputStream(this.PATH_TO_LAST_ID.toFile());
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.setLastID((int) ois.readObject());
				ois.close();
				fis.close();
			} catch (IOException | ClassNotFoundException e) {
				this.indexLogger.debug("Error reading the last indexed ID: " + e.getMessage());
			}
		}
		this.indexLogger.debug("Last indexed ID : " + this.getLastID());	
	}
	/**
	 * Checks if there are recent stored versions, loads them and indexes the data
	 */
	protected void executeIndexing() {

		if (!this.sessionFactory.isClosed() && !this.isFinishIndexing()) {
			long executeIndexingStart = System.currentTimeMillis();
			final Session session = this.sessionFactory.openSession();
			session.setDefaultReadOnly(true);
			
			/** this feature isn't needed for indexing and disabled to prevent oom errors */
			session.setCacheMode(CacheMode.IGNORE);		
			final long queryStartTime = System.currentTimeMillis();
			
			/** Load all PrimaryDataEntityVersionImplementations that haven't been indexed yet	 */
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder
					.createQuery(PrimaryDataEntityVersionImplementation.class);
			Root<PrimaryDataEntityVersionImplementation> root = criteria.from(PrimaryDataEntityVersionImplementation.class);
			criteria.where(criteriaBuilder.gt(root.get(ID), this.getLastID())).orderBy(criteriaBuilder.asc(root.get(ID)));
			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setMaxResults(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

			int indexedObjects = 0;
			int flushedObjects = 0;
			final long queryTime = System.currentTimeMillis() - queryStartTime;
			final long indexStartTime = System.currentTimeMillis();

			while (results.next()) {
				PrimaryDataEntityVersionImplementation version = (PrimaryDataEntityVersionImplementation) results.get(0);
				try {
					this.indexVersion(writer, version);
					indexedObjects++;
					this.setLastID(version.getId());
				} catch (MetaDataException e) {
					this.indexLogger.debug("Unable to load a metadata value: "+e.getMessage());	
				}
				if (indexedObjects % fetchSize == 0) {
					flushedObjects += fetchSize;
				}
			}
			session.clear();
			results.close();
			session.close();
			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");
			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
			if (indexedObjects > 0 || flushedObjects > 0) {
				try {
					writer.commit();				
				} catch (IOException e) {
					this.indexWriterThreadLogger.debug("Error while commiting changes to Index" + e.getMessage());
				}
				this.indexWriterThreadLogger.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects + " | "
						+ flushedObjects + " | " + dateFormat.format(new Date(indexingTime)) + " | " + dateFormat.format(new Date(queryTime)));
				this.implementationProviderLogger.debug("NativeLuceneIndexWriterThread Indexing Time: "+executeIndexingFinishTime);
			}
			try {
				FileOutputStream fos = new FileOutputStream(
						Paths.get(this.indexDirectory.toString(), PublicVersionIndexWriterThread.PUBLIC_LAST_ID).toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(PublicVersionIndexWriterThread.getLastID());
				oos.close();
				fos.close();
			} catch (IOException e) {
				this.indexLogger.debug("Error writing the last indexed ID "+e.getMessage());	
			}
			updateLastIndexedID(indexedObjects);
			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				this.indexLogger.debug("NativeLuceneIndexWriterThread got interrupted: " +e.getMessage());	
			}
			this.indexLogger.debug("NativeLuceneIndexWriterThread time: "+executeIndexingFinishTime);
		}
	}

	/**
	 * Updates the stored last ID
	 * @param indexedObjects ID of the last indexed version
	 */
	private void updateLastIndexedID(int indexedObjects) {
		if (indexedObjects != 0) {
			try {
				FileOutputStream fos = new FileOutputStream(
						Paths.get(this.indexDirectory.toString(), NativeLuceneIndexWriterThread.NATIVE_INDEXER_LAST_ID).toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(this.getLastID());
				oos.close();
				fos.close();
			} catch (IOException e) {
				this.indexLogger.debug("Error writing the last indexed ID: "+e.getMessage());
			}
		}
	}

	/**
	 * Creates a Document with the information of the provided PrimaryDataEntityVersionImplementation and adds it to the index
	 * @param writer The given IndexWriter
	 * @param version The given PrimaryDataEntityVersionImplementation
	 * @throws MetaDataException
	 */
	private void indexVersion(IndexWriter writer, PrimaryDataEntityVersionImplementation version) throws MetaDataException {
		MetaData metadata = version.getMetaData();
		Document doc = new Document();
		doc.add(new TextField(EnumIndexField.TITLE.value(), getString(metadata.getElementValue(EnumDublinCoreElements.TITLE)), Store.YES));
		doc.add(new TextField(EnumIndexField.DESCRIPTION.value(), getString(metadata.getElementValue(EnumDublinCoreElements.DESCRIPTION)),
				Store.YES));
		doc.add(new TextField(EnumIndexField.COVERAGE.value(), getString(metadata.getElementValue(EnumDublinCoreElements.COVERAGE)), Store.NO));
		StringBuilder builder = new StringBuilder();
		doc.add(new TextField(EnumIndexField.IDENTIFIER.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()), Store.NO));		
		doc.add(new TextField(EnumIndexField.RELATEDIDENTIFIERTYPE.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelatedIdentifierType().value()), Store.NO));
		doc.add(new TextField(EnumIndexField.RELATIONTYPE.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()), Store.NO));
		
		doc.add(new TextField(EnumIndexField.SIZE.value(),
				String.format("%014d",((DataSize) metadata.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize()),Store.YES));
		
		doc.add(new TextField(EnumIndexField.LANGUAGE.value(),
				getString(((EdalLanguage) metadata.getElementValue(EnumDublinCoreElements.LANGUAGE)).getLanguage().toString()), Store.NO));
		Persons creators = (Persons) metadata.getElementValue(EnumDublinCoreElements.CREATOR);
		for (Person currentPerson : creators) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(DELIMITER);
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(DELIMITER);
				doc.add(new TextField(EnumIndexField.CREATORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append(( currentPerson).getAddressLine());
			builder.append(DELIMITER);
			builder.append(( currentPerson).getZip());
			builder.append(DELIMITER);
			builder.append(( currentPerson).getCountry());
			doc.add(new TextField(EnumIndexField.PERSON.value(), builder.toString(), Store.YES));
		}
		Persons persons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
		LegalPerson legalPerson = (LegalPerson) metadata.getElementValue(EnumDublinCoreElements.PUBLISHER);
		builder.setLength(0);
		builder.append(( legalPerson).getLegalName());
		builder.append(DELIMITER);
		builder.append(( legalPerson).getAddressLine());
		builder.append(DELIMITER);
		builder.append(( legalPerson).getZip());
		builder.append(DELIMITER);
		builder.append(( legalPerson).getCountry());
		doc.add(new TextField(EnumIndexField.LEGALPERSON.value(), builder.toString(), Store.YES));
		/** 
		 * Stringbuilder to combine multiple Values into one large String to store the text in one field per categopry
		 * Not used for Relations and dates, because these values occur rarely more than once per Version/Document
		 *  */
		for (Person currentPerson : persons) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(DELIMITER);
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(DELIMITER);
				doc.add(new TextField(EnumIndexField.CONTRIBUTORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append(( currentPerson).getAddressLine());
			builder.append(DELIMITER);
			builder.append(( currentPerson).getZip());
			builder.append(DELIMITER);
			builder.append(( currentPerson).getCountry());
			doc.add(new TextField(EnumIndexField.CONTRIBUTOR.value(), builder.toString(), Store.YES));
		}
		CheckSum checkSums = (CheckSum) metadata.getElementValue(EnumDublinCoreElements.CHECKSUM);
		builder.setLength(0);
		if(checkSums.size() > 1) {
			for (CheckSumType checkSum : checkSums) {
				builder.append(checkSum.getAlgorithm());
				builder.append(DELIMITER);
				builder.append(checkSum.getCheckSum());
				builder.append(", ");
				doc.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getAlgorithm(), Store.NO));
			}
		}else if(checkSums.size() == 1) {
			CheckSumType checkSum = checkSums.iterator().next();
			doc.add(new TextField(EnumIndexField.ALGORITHM.value(), checkSum.getAlgorithm(), Store.NO));
			doc.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getCheckSum(), Store.NO));
		}
		Subjects subjects = (Subjects) metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
		builder.setLength(0);
		for (UntypedData subject : subjects) {
			builder.append(subject.getString());
			builder.append(DELIMITER);
		}
		doc.add(new TextField(EnumIndexField.SUBJECT.value(), builder.toString(), Store.YES));
		builder.setLength(0);
		builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()));
		builder.append(DELIMITER);
		builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelatedIdentifierType().value()));
		builder.append(DELIMITER);
		builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()));
		builder.append(", ");
		doc.add(new TextField(EnumIndexField.RELATION.value(), builder.toString(), Store.NO));
		DateEvents events = (DateEvents) metadata.getElementValue(EnumDublinCoreElements.DATE);
		for (EdalDate date : events) {
			doc.add(new TextField(EnumIndexField.STARTDATE.value(),Integer.toString(date.getStartDate().get(Calendar.YEAR)),Store.YES));
			if (date instanceof EdalDateRange) {
				doc.add(new TextField(EnumIndexField.ENDDATE.value(),
						Integer.toString(((EdalDateRange) date).getEndDate().get(Calendar.YEAR)),Store.YES));
			}
		}
		if (metadata.getElementValue(EnumDublinCoreElements.FORMAT) instanceof EmptyMetaData) {
			doc.add(new TextField(EnumIndexField.MIMETYPE.value(), NONE, Store.YES));
			doc.add(new TextField(EnumIndexField.TYPE.value(), NONE, Store.YES));
		} else {
			doc.add(new TextField(EnumIndexField.MIMETYPE.value(),
					getString(((DataFormat) metadata.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType()), Store.YES));
			doc.add(new TextField(EnumIndexField.TYPE.value(), metadata.getElementValue(EnumDublinCoreElements.TYPE).toString(), Store.YES));
		}		
		StringJoiner allFieldsJoiner = new StringJoiner(DELIMITER);
		for(IndexableField field : doc.getFields()){	
			allFieldsJoiner.add(field.stringValue());
		}
		doc.add(new TextField(EnumIndexField.ALL.value(), allFieldsJoiner.toString(),Store.YES));
		doc.add(new StringField(EnumIndexField.VERSIONID.value(), Integer.toString(version.getId()), Store.YES));
		doc.add(new StringField(EnumIndexField.PRIMARYENTITYID.value(), version.getPrimaryEntityId(),Store.YES));
		doc.add(new StringField(EnumIndexField.REVISION.value(),Long.toString(version.getRevision()), Store.YES));
		builder.setLength(0);
		Calendar cd = version.getCreationDate();
		//important to access the related local file for content indexing if this Version belongs to a file
		doc.add(new StringField(EnumIndexField.CREATION_DATE.value(), 
				builder.append(cd.get(Calendar.YEAR)).append(HYPHEN).append(cd.get(Calendar.MONTH))
				.append(HYPHEN).append(cd.get(Calendar.DAY_OF_MONTH)).append(HYPHEN).append(cd.get(Calendar.HOUR_OF_DAY))
				.append(HYPHEN).append(cd.get(Calendar.MINUTE)).toString(),Store.YES));
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Error when adding Document to IndexWriter" + e.getMessage());
		}
		doc = null;		
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * @param data The given UntypedData
	 * @return The extracted String
	 */
	private String getString(UntypedData data) {
		String string = data.getString();
		return string == null ? "" : string;
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * @param data The given UntypedData
	 * @return The extracted String
	 */
	private String getString(String string) {
		return string == null ? "" : string;
	}

	protected void resetIndexThread() {

		this.requestForReset = true;

		this.indexWriterThreadLogger.debug("Reseting index structure...");

		try {
			this.latch.await();
		} catch (InterruptedException e) {
			this.indexWriterThreadLogger.debug("Thread interrupted while waiting for latch count down: "+e.getMessage());
		}

		this.implementationProviderLogger.info("Start reseting index structure...");

		IndexReader reader = null;
		int numberDocs = 0;
		try {
			reader = DirectoryReader.open(writer);
			numberDocs += reader.numDocs();
			reader.close();
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Attempt to obtain the number of indexed Documents failed: "+e.getMessage());
		}
		this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + numberDocs);

		this.setLastID(0);

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}
	
	/**
	 * Setter for the last indexed ID of this Thread
	 * @param val version ID
	 */
	protected void setLastID(int val) {
		this.lastIndexedID = val;
	}
	
	/**
	 * Getter for the last indexed ID of this Thread
	 */
	public int getLastID() {
		return this.lastIndexedID;
	}

	@Override
	public void run() {
		super.run();
		this.countDownLatch.countDown();
	}	
}