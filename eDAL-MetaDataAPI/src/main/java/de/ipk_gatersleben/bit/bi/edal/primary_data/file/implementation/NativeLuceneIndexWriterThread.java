/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.util.Calendar;
import java.util.Date;
import java.util.StringJoiner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd, ralfs, arendd
 */
public class NativeLuceneIndexWriterThread extends IndexWriterThread {

//	private static final String ID = "id";
//	private static final String DELIMITER = " ";
//	private static final String NONE = "none";
//	private static final String HYPHEN = "-";
	private IndexWriter indexWriter = null;
	private DirectoryTaxonomyWriter taxonomyWriter = null;
	
	protected int lastIndexedID = 0;
	private final Path pathToLastId = Paths.get(this.indexDirectory.toString(), "last_id_native.dat");
	
	/** high value fetch objects faster, but more memory is needed */
	final int FETCH_SIZE = 10000;

	protected NativeLuceneIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory,
			Logger implementationProviderLogger, IndexWriter indexWriter, DirectoryTaxonomyWriter taxonomyWriter) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.indexWriter = indexWriter;
		this.taxonomyWriter = taxonomyWriter;
		int numberDocs = 0;
		try {
			IndexReader reader = DirectoryReader.open(indexWriter);
			numberDocs = reader.numDocs();
			reader.close();
			reader = null;
		} catch (IOException e) {
			this.indexLogger.debug("Error opening and closing the IndexReader: " + e.getMessage());
		}
		this.implementationProviderLogger.info("Number of docs in the Lucene Index at Startup: " + numberDocs);
		Path parent = this.pathToLastId.getParent();

		if (!Files.exists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				this.indexLogger.debug("Error while creating Index Directory" + e.getMessage());
			}
		}
		if (Files.exists(this.pathToLastId)) {

			try {
				FileInputStream fis = new FileInputStream(this.pathToLastId.toFile());
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.setLastID((int) ois.readObject());
				ois.close();
				fis.close();
				this.implementationProviderLogger
						.info("Loaded LastID from NativeLuceneIndexWriterThread: " + this.getLastID());

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

			/**
			 * Load all PrimaryDataEntityVersionImplementations that haven't been indexed
			 * yet
			 */
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PrimaryDataEntityVersionImplementation> criteria = criteriaBuilder
					.createQuery(PrimaryDataEntityVersionImplementation.class);
			Root<PrimaryDataEntityVersionImplementation> root = criteria
					.from(PrimaryDataEntityVersionImplementation.class);
			criteria.where(criteriaBuilder.gt(root.get(EnumIndexing.ID.value()), this.getLastID()))
					.orderBy(criteriaBuilder.asc(root.get(EnumIndexing.ID.value())));

			Transaction transaction = session.beginTransaction();
			final ScrollableResults results = session.createQuery(criteria).setMaxResults(FETCH_SIZE)
					.scroll(ScrollMode.FORWARD_ONLY);

			int indexedObjects = 0;
			int flushedObjects = 0;
			final long queryTime = System.currentTimeMillis() - queryStartTime;
			final long indexStartTime = System.currentTimeMillis();

			while (results.next()) {
				PrimaryDataEntityVersionImplementation version = (PrimaryDataEntityVersionImplementation) results
						.get(0);

//				System.out.println("LastID NativeLuceneIndexForQuery: " +this.getLastID() + "\t"+ version);

				try {
					this.indexVersion(version);
					indexedObjects++;
					this.setLastID(version.getId());
				} catch (MetaDataException e) {
					this.indexLogger.debug("Unable to load a metadata value: " + e.getMessage());
				}
				if (indexedObjects % FETCH_SIZE == 0) {
					flushedObjects += FETCH_SIZE;
				}
			}
			transaction.commit();
			session.clear();
			results.close();
			session.close();
			final long indexingTime = System.currentTimeMillis() - indexStartTime;
			DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");
			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
			if (indexedObjects > 0 || flushedObjects > 0) {
				
				try {
					
					this.indexWriter.commit();
					this.taxonomyWriter.commit();
				} catch (IOException e) {
					this.implementationProviderLogger.warn("Error while commiting changes to Index" + e.getMessage());
				}
				this.implementationProviderLogger
						.info("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedObjects
								+ " | " + flushedObjects + " | " + dateFormat.format(new Date(indexingTime)) + " | "
								+ dateFormat.format(new Date(queryTime)));
				this.implementationProviderLogger
						.debug("NativeLuceneIndexWriterThread Indexing Time: " + executeIndexingFinishTime);
			}
//			try {
//				FileOutputStream fos = new FileOutputStream(
//						Paths.get(this.indexDirectory.toString(), PublicVersionIndexWriterThread.PUBLIC_LAST_ID).toFile());
//				ObjectOutputStream oos = new ObjectOutputStream(fos);
//				oos.writeObject(PublicVersionIndexWriterThread.getLastID());
//				oos.close();
//				fos.close();
//			} catch (IOException e) {
//				this.indexLogger.debug("Error writing the last indexed ID "+e.getMessage());	
//			}
			updateLastIndexedID(this.getLastID());
			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				this.indexLogger.debug("NativeLuceneIndexWriterThread got interrupted: " + e.getMessage());
			}
		}
	}

	/**
	 * Updates the stored last ID
	 * 
	 * @param indexedObjects ID of the last indexed version
	 */
	private void updateLastIndexedID(int indexedObjects) {
		if (indexedObjects != 0) {
			try {
				FileOutputStream fos = new FileOutputStream(pathToLastId.toFile());
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(this.getLastID());
				oos.close();
				fos.close();
			} catch (IOException e) {
				this.indexLogger.debug("Error writing the last indexed ID: " + e.getMessage());
			}
		}
	}

	/**
	 * Creates a Document with the information of the provided
	 * PrimaryDataEntityVersionImplementation and adds it to the index
	 * 
	 * @param version The given PrimaryDataEntityVersionImplementation
	 * @throws MetaDataException
	 */
	private void indexVersion(PrimaryDataEntityVersionImplementation version)
			throws MetaDataException {
		MetaData metadata = version.getMetaData();
		Document document = new Document();
		document.add(new TextField(EnumIndexField.TITLE.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.TITLE)), Store.YES));
		document.add(new TextField(EnumIndexField.DESCRIPTION.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.DESCRIPTION)), Store.YES));
		document.add(new TextField(EnumIndexField.COVERAGE.value(),
				getString(metadata.getElementValue(EnumDublinCoreElements.COVERAGE)), Store.NO));
		StringBuilder builder = new StringBuilder();
		document.add(new TextField(EnumIndexField.IDENTIFIER.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()),
				Store.NO));
		document.add(new TextField(EnumIndexField.RELATEDIDENTIFIERTYPE.value(),
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER))
						.getRelatedIdentifierType().value()),
				Store.NO));
		document.add(new TextField(EnumIndexField.RELATIONTYPE.value(), getString(
				((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()),
				Store.NO));

		document.add(new TextField(EnumIndexField.SIZE.value(), String.format("%014d",
				((DataSize) metadata.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize()), Store.YES));

		document.add(new TextField(EnumIndexField.LANGUAGE.value(), getString(
				((EdalLanguage) metadata.getElementValue(EnumDublinCoreElements.LANGUAGE)).getLanguage().toString()),
				Store.NO));
		Persons creators = (Persons) metadata.getElementValue(EnumDublinCoreElements.CREATOR);
		for (Person currentPerson : creators) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName() + EnumIndexing.DELIMITER.value());
				builder.append(((NaturalPerson) currentPerson).getSureName() + EnumIndexing.DELIMITER.value());
				document.add(new TextField(EnumIndexField.CREATORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append((currentPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getZip() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getCountry());
			document.add(new TextField(EnumIndexField.CREATOR.value(), builder.toString(), Store.YES));
		}
		Persons persons = (Persons) metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
		LegalPerson legalPerson = (LegalPerson) metadata.getElementValue(EnumDublinCoreElements.PUBLISHER);
		builder.setLength(0);
		builder.append((legalPerson).getLegalName() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getZip() + EnumIndexing.DELIMITER.value());
		builder.append((legalPerson).getCountry());
		document.add(new TextField(EnumIndexField.LEGALPERSON.value(), builder.toString(), Store.YES));
		/**
		 * Stringbuilder to combine multiple Values into one large String to store the
		 * text in one field per categopry Not used for Relations and dates, because
		 * these values occur rarely more than once per Version/Document
		 */
		for (Person currentPerson : persons) {
			builder.setLength(0);
			if (currentPerson instanceof NaturalPerson) {
				builder.append(((NaturalPerson) currentPerson).getGivenName());
				builder.append(EnumIndexing.DELIMITER.value());
				builder.append(((NaturalPerson) currentPerson).getSureName());
				builder.append(EnumIndexing.DELIMITER.value());
				document.add(new TextField(EnumIndexField.CONTRIBUTORNAME.value(), builder.toString(), Store.YES));
			}
			builder.append((currentPerson).getAddressLine() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getZip() + EnumIndexing.DELIMITER.value());
			builder.append((currentPerson).getCountry());
			document.add(new TextField(EnumIndexField.CONTRIBUTOR.value(), builder.toString(), Store.YES));
		}
		CheckSum checkSums = (CheckSum) metadata.getElementValue(EnumDublinCoreElements.CHECKSUM);
		builder.setLength(0);
		if (checkSums.size() > 1) {
			for (CheckSumType checkSum : checkSums) {
				builder.append(checkSum.getAlgorithm() + EnumIndexing.DELIMITER.value());
				builder.append(checkSum.getCheckSum());
				builder.append(", ");
				document.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getAlgorithm(), Store.NO));
			}
		} else if (checkSums.size() == 1) {
			CheckSumType checkSum = checkSums.iterator().next();
			document.add(new TextField(EnumIndexField.ALGORITHM.value(), checkSum.getAlgorithm(), Store.NO));
			document.add(new TextField(EnumIndexField.CHECKSUM.value(), checkSum.getCheckSum(), Store.NO));
		}
		Subjects subjects = (Subjects) metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
		builder.setLength(0);
		for (UntypedData subject : subjects) {
			builder.append(subject.getString() + EnumIndexing.DELIMITER.value());
		}
		document.add(new TextField(EnumIndexField.SUBJECT.value(), builder.toString(), Store.YES));
		builder.setLength(0);
		builder.append(
				getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getIdentifier()));
		builder.append(EnumIndexing.DELIMITER.value());
		builder.append(getString(((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER))
				.getRelatedIdentifierType().value()));
		builder.append(EnumIndexing.DELIMITER.value());
		builder.append(getString(
				((Identifier) metadata.getElementValue(EnumDublinCoreElements.IDENTIFIER)).getRelationType().value()));
		builder.append(", ");
		document.add(new TextField(EnumIndexField.RELATION.value(), builder.toString(), Store.NO));
		DateEvents events = (DateEvents) metadata.getElementValue(EnumDublinCoreElements.DATE);
		for (EdalDate date : events) {
			document.add(new TextField(EnumIndexField.STARTDATE.value(),
					Integer.toString(date.getStartDate().get(Calendar.YEAR)), Store.YES));
			if (date instanceof EdalDateRange) {
				document.add(new TextField(EnumIndexField.ENDDATE.value(),
						Integer.toString(((EdalDateRange) date).getEndDate().get(Calendar.YEAR)), Store.YES));
			}
		}
		if (metadata.getElementValue(EnumDublinCoreElements.FORMAT) instanceof EmptyMetaData) {
			document.add(new TextField(EnumIndexField.MIMETYPE.value(), EnumIndexing.NONE.value(), Store.YES));
			document.add(new TextField(EnumIndexField.TYPE.value(), EnumIndexing.NONE.value(), Store.YES));
		} else {
			document.add(new TextField(EnumIndexField.MIMETYPE.value(),
					getString(((DataFormat) metadata.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType()),
					Store.YES));
			document.add(new TextField(EnumIndexField.TYPE.value(),
					metadata.getElementValue(EnumDublinCoreElements.TYPE).toString(), Store.YES));
		}
		StringJoiner allFieldsJoiner = new StringJoiner(EnumIndexing.DELIMITER.value());
		for (IndexableField field : document.getFields()) {
			allFieldsJoiner.add(field.stringValue());
		}
		document.add(new TextField(EnumIndexField.ALL.value(), allFieldsJoiner.toString(), Store.YES));
		document.add(new StringField(EnumIndexField.VERSIONID.value(), Integer.toString(version.getId()), Store.YES));
		document.add(new StringField(EnumIndexField.PRIMARYENTITYID.value(), version.getPrimaryEntityId(), Store.YES));
		document.add(new StringField(EnumIndexField.REVISION.value(), Long.toString(version.getRevision()), Store.YES));
		builder.setLength(0);
		Calendar cd = version.getCreationDate();
		// needed to access the related local file for content indexing if this Version
		// belongs to a file
		document.add(new StringField(EnumIndexField.CREATION_DATE.value(),
				builder.append(cd.get(Calendar.YEAR)).append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.MONTH)).append(EnumIndexing.HYPHEN.value())
						.append(cd.get(Calendar.DAY_OF_MONTH)).append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.HOUR_OF_DAY))
						.append(EnumIndexing.HYPHEN.value()).append(cd.get(Calendar.MINUTE)).toString(),
				Store.YES));
		try {
			////////////////////////////////////////////////////////////////////////////
//			System.out.println("Native1: "+ document);

//			String entityType = null;
//			
//			if(version.getEntity().isDirectory()) {
//				entityType = "directory";
//			}
//			else {
//				entityType = "file";
//			}
			
//			try {
				addFacets(document);
//				this.indexWriter.deleteDocuments(new Term(EnumIndexField.VERSIONID.value(), Integer.toString(version.getId())));
//				doc.add(new StringField(EnumIndexField.ENTITYTYPE.value(), entityType, Store.YES));
//				StringBuilder docIDBuilder = new StringBuilder(doc.get(EnumIndexField.PRIMARYENTITYID.value()))
//						.append(HYPHEN);
//				if (entityType.equals(PublicVersionIndexWriterThread.FILE)) {
//					String filetype = FilenameUtils.getExtension(doc.get(EnumIndexField.TITLE.value()));
//					docIDBuilder.append(1);
//					// skip this field, if file has no extension
//					if (filetype != null && !filetype.isEmpty()) {
//						doc.add(new TextField(EnumIndexField.FILETYPE.value(), filetype, Store.YES));
//						doc.add(new FacetField(EnumIndexField.FILETYPE.value(), filetype));
//					}
//					long fileSize;
//					try {
//						fileSize = Long.parseLong(doc.get(EnumIndexField.SIZE.value()));
//						String mimeType[] = doc.get(EnumIndexField.MIMETYPE.value()).split(SEPERATOR);
//						if (mimeType[0].toLowerCase().equals(TEXT) && mimeType[1].toLowerCase().equals(PLAIN)
//								&& fileSize <= MAX_DOC_SIZE) {
//							String[] dateValues = doc.get(EnumIndexField.CREATION_DATE.value()).split(HYPHEN);
////							if (dateValues.length == 5) {
////								Path pathToFile = Paths.get(
////										((FileSystemImplementationProvider) DataManager.getImplProv()).getDataPath()
////												.toString(),
////										dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4],
////										file + HYPHEN + doc.get(EnumIndexField.REVISION.value()) + DAT);
////								indexFileContent(doc, pathToFile.toFile());
////							}
//
//						}
//					} catch (NumberFormatException e) {
//						this.indexLogger.debug("String conversion failed: " + e.getMessage());
//						fileSize = -1;
//					}
//				} else if (entityType.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
//					docIDBuilder.append(0);
//				} else if (entityType.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
//					docIDBuilder.append(2);
//				}
//				// docID will be needed to resolve the correct link from a found version to the
//				// associated PrimaryDataFile landing page
//				doc.add(new StringField(EnumIndexField.DOCID.value(), docIDBuilder.toString(), Store.YES));
//			} catch (IOException e) {
//				this.indexLogger.debug(
//						"Return NULL Document.. PublicVersionIndexWriterThread was interrupted: " + e.getMessage());
//			}
			
			/////////////////////////////////////////////////////////////////////////////
			
			
//			System.out.println("Native2: "+ document);

			this.indexWriter.addDocument(((FileSystemImplementationProvider)DataManager.getImplProv()).getFacetsConfig().build(this.taxonomyWriter, document));

			
			
//			this.indexWriter.addDocument(doc);
			
			
			
			
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Error when adding Document to IndexWriter" + e.getMessage());
		}
		document = null;
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * 
	 * @param data The given UntypedData
	 * @return The extracted String
	 */
	private String getString(UntypedData data) {
		String string = data.getString();
		return string == null ? new String() : string;
	}

	/**
	 * Helper function to retrieve the contained String of an UntypedData object
	 * 
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
			this.indexWriterThreadLogger
					.debug("Thread interrupted while waiting for latch count down: " + e.getMessage());
		}

		this.implementationProviderLogger.info("Start reseting index structure...");

		IndexReader reader = null;
		int numberDocs = 0;
		try {
			reader = DirectoryReader.open(indexWriter);
			numberDocs += reader.numDocs();
			reader.close();
		} catch (IOException e) {
			this.indexWriterThreadLogger
					.debug("Attempt to obtain the number of indexed Documents failed: " + e.getMessage());
		}
		this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " + numberDocs);

		this.setLastID(0);

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}

	/**
	 * Setter for the last indexed ID of this Thread
	 * 
	 * @param val version ID
	 */
	private void setLastID(int val) {
		this.lastIndexedID = val;
	}

	/**
	 * Getter for the last indexed ID of this Thread
	 * 
	 * @return The last indexed version id
	 */
	private int getLastID() {
		return this.lastIndexedID;
	}

	@Override
	public void run() {
		super.run();
		this.countDownLatch.countDown();
	}
	
	private void addFacets(Document doc) throws IOException {
		TokenStream tokenStream = this.indexWriter.getAnalyzer().tokenStream(EnumIndexField.DESCRIPTION.value(),
				doc.get(EnumIndexField.DESCRIPTION.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.DESCRIPTION.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = this.indexWriter.getAnalyzer().tokenStream(EnumIndexField.TITLE.value(), doc.get(EnumIndexField.TITLE.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.TITLE.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = this.indexWriter.getAnalyzer().tokenStream(EnumIndexField.SUBJECT.value(), doc.get(EnumIndexField.SUBJECT.value()));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(EnumIndexField.SUBJECT.value(), termAttribute.toString()));
			}
		}
		tokenStream.close();

		String[] strings = doc.getValues(EnumIndexField.CREATORNAME.value());
		for (String s : strings) {
			tokenStream = this.indexWriter.getAnalyzer().tokenStream(EnumIndexField.CREATORNAME.value(), s);
			tokenStream.reset();
			StringJoiner creator = new StringJoiner(" ");
			while (tokenStream.incrementToken()) {
				CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
				String token = termAttribute == null ? "" : termAttribute.toString();
				if (token.length() > 1)
					creator.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			tokenStream.close();
			if (creator.length() > 0) {
				doc.add(new FacetField(EnumIndexField.CREATORNAME.value(), creator.toString()));
			}
		}
		strings = doc.getValues(EnumIndexField.CONTRIBUTORNAME.value());
		for (String s : strings) {
			tokenStream = this.indexWriter.getAnalyzer().tokenStream(EnumIndexField.CONTRIBUTORNAME.value(), s);
			tokenStream.reset();
			StringJoiner contributor = new StringJoiner(" ");
			while (tokenStream.incrementToken()) {
				CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
				String token = termAttribute == null ? "" : termAttribute.toString();
				if (token.length() > 1)
					contributor.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			tokenStream.close();
			if (contributor.length() > 0) {
				doc.add(new FacetField(EnumIndexField.CONTRIBUTORNAME.value(), contributor.toString()));
			}
		}
		strings = doc.getValues(EnumIndexField.STARTDATE.value());
		for (String s : strings) {
			doc.add(new FacetField(EnumIndexField.STARTDATE.value(), s));
		}
		doc.add(new FacetField(EnumIndexField.SIZE.value(), doc.get(EnumIndexField.SIZE.value())));
	}
}