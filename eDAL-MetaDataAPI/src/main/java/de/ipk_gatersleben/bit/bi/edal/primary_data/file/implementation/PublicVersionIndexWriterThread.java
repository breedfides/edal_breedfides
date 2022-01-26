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
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.StringJoiner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class PublicVersionIndexWriterThread extends IndexWriterThread {

	private static final String INTERNAL_ID = "internalID";

	private static final String SEPERATOR = "/";

	private static final String HYPHEN = "-";

	private static final String PARENTDIR = "parentdir";

	private static final String IDENTIFIER_TYPE = "identifierType";

	private static final String PUBLICATION_STATUS = "publicationStatus";

	private static final String ID = "id";

	/** The max number of bytes for the indexing of file content **/
	private final int MAX_DOC_SIZE = 100 * 1024 * 1024;

	/**
	 * The revision type determines the url/path ending and is needed to resolve the
	 * correct "URL" at search time
	 **/
	private final short REVISION_TYPE_FILE = 1;
	private final short REVISION_TYPE_DIRECTORY = 0;
	private final short REVISION_TYPE_PUBLICREFERENCE = 2;

	protected static int lastIndexedID = 0;
	private Analyzer analyzer = null;
	private int indexedVersions = 0;
	private int flushedObjects = 0;
	private int filesCounter = 0;
	private Boolean indexedData = false;
	private IndexWriter writer = null;
	private IndexSearcher searcher = null;
	private IndexReader reader = null;
	private DirectoryTaxonomyWriter taxoWriter = null;
	/**
	 * Used for indexing of Facets, holds information about the indexed
	 * Facet-dimensions
	 **/
	private final FacetsConfig config = new FacetsConfig();

	/** high value fetch objects faster, but more memory is needed */
	private final int FETCH_SIZE = (int) Math.pow(10, 5);
	private final int DIRECTORY_FETCH_SIZE = (int) Math.pow(10, 5);

	private DirectoryReader directoryReader;
	private Path pathToLastId = Paths.get(this.indexDirectory.toString(), IndexSearchConstants.PUBLIC_LAST_ID);

	protected PublicVersionIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory,
			Logger implementationProviderLogger, IndexWriter writer, DirectoryTaxonomyWriter taxoWriter) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.writer = writer;
		this.analyzer = writer.getAnalyzer();
		try {
			this.taxoWriter = taxoWriter;
			this.config.setMultiValued(IndexSearchConstants.CREATORNAME, true);
			this.config.setMultiValued(IndexSearchConstants.CONTRIBUTORNAME, true);
			this.config.setMultiValued(IndexSearchConstants.TITLE, true);
			this.config.setMultiValued(IndexSearchConstants.SUBJECT, true);
			this.config.setMultiValued(IndexSearchConstants.DESCRIPTION, true);
			this.config.setMultiValued(IndexSearchConstants.STARTDATE, true);
			this.reader = DirectoryReader.open(writer);
			this.searcher = new IndexSearcher(reader);
			this.directoryReader = DirectoryReader.open(writer);
		} catch (IOException e) {
			this.indexLogger
					.debug("Error occured while starting the PublicVersionIndexWriterThread (opening Lucene IO tools): "
							+ e.getMessage());
		}

		if (Files.exists(pathToLastId)) {
			try {
				FileInputStream fis = new FileInputStream(this.pathToLastId.toFile());
				ObjectInputStream ois = new ObjectInputStream(fis);
				this.setLastID((int) ois.readObject());
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.indexLogger.debug("Last indexed public reference: " + PublicVersionIndexWriterThread.getLastID());

	}

	/**
	 * Loads freshly stored versions and indexes them with additional Metadata, to
	 * enable the datasets/files can be retrieved via other e!DAL search functions
	 */
	protected void executeIndexing() {
		long indexingTime = 1;
		long executeIndexingStart = 1;
		if (!this.sessionFactory.isClosed() && !this.isFinishIndexing()) {

			/**
			 * open new Indexreader and Indexsearcher, if the index changed since last
			 * executeIndexing() call
			 **/
			IndexReader newReader = null;
			try {
				newReader = DirectoryReader.openIfChanged(directoryReader);
				if (newReader != null) {
					this.reader.close();
					this.reader = newReader;
					this.searcher = new IndexSearcher(reader);
				}
			} catch (IOException e) {
				this.indexLogger.debug("There was an error when opening a new DirectoryReader: " + e.getMessage());
			}
			final Session session = this.sessionFactory.openSession();
			session.setDefaultReadOnly(true);

			/**
			 * Caching for Hibernate entities disabled because they are only used once and
			 * to prevent running out of memory
			 */
			session.setCacheMode(CacheMode.IGNORE);
			this.indexedVersions = 0;
			this.flushedObjects = 0;
			executeIndexingStart = System.currentTimeMillis();

			final long queryStartTime = System.currentTimeMillis();

			countNewReferences(session);
			/** Load all new PublicReference.IDs */
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Object[]> criteria = criteriaBuilder.createQuery(Object[].class);
			Root<PublicReferenceImplementation> root = criteria.from(PublicReferenceImplementation.class);
			criteria.multiselect(root.get(ID), root.get(INTERNAL_ID));
			Predicate predicateId = criteriaBuilder.gt(root.get(ID), PublicVersionIndexWriterThread.getLastID());
			Predicate predicateAccepted = criteriaBuilder.equal(root.get(PUBLICATION_STATUS),
					PublicationStatus.ACCEPTED);
			Predicate predicateType = criteriaBuilder.equal(root.get(IDENTIFIER_TYPE), PersistentIdentifier.DOI);
			if (DataManager.getConfiguration().isInTestMode()) {
				criteria.where(criteriaBuilder.and(predicateId, predicateType))
						.orderBy(criteriaBuilder.asc(root.get(ID)));
			} else {
				criteria.where(criteriaBuilder.and(predicateId, predicateAccepted, predicateType))
						.orderBy(criteriaBuilder.asc(root.get(ID)));
			}

			/** Transaction is needed for ScrollableResults */
			session.getTransaction().begin();

			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setCacheable(false)
					.setMaxResults(FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);

			final long queryTime = System.currentTimeMillis() - queryStartTime;

			final long indexStartTime = System.currentTimeMillis();
			long startTime = System.currentTimeMillis();
			this.filesCounter = 0;
			while (results.next()) {
				Object[] objs = (Object[]) results.get(0);
				Integer publicRef = (Integer) objs[0];
				String internalId = (String) objs[1];
				/** index all associated Files/Directories */
				indexPublicReference(publicRef, internalId, session);
				this.setLastID(publicRef);
				try {
					DataManager.getSearchManager().maybeRefresh();
				} catch (IOException e) {
					this.indexLogger.debug("Error while refreshing the SearcherManager: " + e.getMessage());
				}
			}
			if (this.filesCounter > 0) {
				DataManager.getImplProv().getLogger().info("PublicVersionIndexWriterThread Indexing time: "
						+ (System.currentTimeMillis() - startTime) + "ms");
				indexedData = true;
			} else {
				if (indexedData) {
					DataManager.getImplProv().getLogger().info("PublicVersionIndexWriterThread Indexing finished");
				}
				indexedData = false;
			}
			results.close();
			session.getTransaction().commit();
			session.close();
			indexingTime = System.currentTimeMillis() - indexStartTime;
			DateFormat df = new SimpleDateFormat("mm:ss:SSS");

			if (indexedVersions > 0 || flushedObjects > 0) {
				try {
					this.writer.commit();
					this.taxoWriter.commit();
				} catch (IOException e) {
					this.indexLogger.debug("Error while commiting Index/Taxonomy writer " + e.getMessage());
				}
				try {
					FileOutputStream fos = new FileOutputStream(Paths
							.get(this.indexDirectory.toString(), IndexSearchConstants.NATIVE_INDEXER_LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(((NativeLuceneIndexWriterThread) ((FileSystemImplementationProvider) DataManager
							.getImplProv()).getIndexThread()).getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					this.indexLogger.debug("Error writing the Native_last_id: " + e.getMessage());
				}
				this.indexWriterThreadLogger
						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedVersions
								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
								+ df.format(new Date(queryTime)));
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), IndexSearchConstants.PUBLIC_LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(PublicVersionIndexWriterThread.getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					this.indexLogger.debug("Error writing the Public_last_id: " + e.getMessage());
				}
			}
			try {
				if (newReader != null)
					newReader.close();
			} catch (IOException e) {
				this.indexLogger.debug("Error closing the IndexReader: " + e.getMessage());
			}
			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				this.indexLogger.debug("PublicVersionIndexWriterThread got interrupted: " + e.getMessage());
			}
			// this.implementationProviderLogger.info("Finished execute Index task:
			// lastIndexedID: " + lastIndexedID);
			long executeIndexingFinishTime = System.currentTimeMillis() - executeIndexingStart;
			this.indexLogger.debug("PublicVersionIndexWriteThread time: " + executeIndexingFinishTime);
			// this.indexLogger.info("ExecuteIndexingTime(ms): "+executeIndexingFinishTime+"
			// Amount_of_indexed_objects: "+indexedObjects+" flushedObjects:
			// "+flushedObjects);
		}
	}

	/**
	 * Checks if there are new stored PublicReferences with a higher ID than the
	 * stored
	 * 
	 * @param session Hibernate session for reading the database
	 */
	private void countNewReferences(Session session) {
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaLong = criteriaBuilder.createQuery(Long.class);
		Root<PublicReferenceImplementation> root = criteriaLong.from(PublicReferenceImplementation.class);
		criteriaLong.select(criteriaBuilder.count(root));
		Predicate predicateId = criteriaBuilder.gt(root.get(ID), PublicVersionIndexWriterThread.getLastID());
		Predicate predicateAccepted = criteriaBuilder.equal(root.get(PUBLICATION_STATUS), PublicationStatus.ACCEPTED);
		Predicate predicateType = criteriaBuilder.equal(root.get(IDENTIFIER_TYPE), PersistentIdentifier.DOI);
		criteriaLong.where(criteriaBuilder.and(predicateId, predicateAccepted, predicateType));
		long l = session.createQuery(criteriaLong).getSingleResult();
		if (l > 0) {
			DataManager.getImplProv().getLogger().info("Publicreferences that still have to be indexed " + l);
		}
	}

	private ScrollableResults getPrimaryDataFileIds(String primId, Session session, Class entityClass,
			String lowerBound) {
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<String> primaryEntityCriteria = criteriaBuilder.createQuery(String.class);
		Root<?> fileRoot = primaryEntityCriteria.from(entityClass);
		//primaryEntityCriteria.select(fileRoot.get("type"));
		primaryEntityCriteria.select(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID));
		if (lowerBound != null) {
			primaryEntityCriteria.where(criteriaBuilder.and(
					criteriaBuilder.equal(
							fileRoot.get("parentDirectory").get(PrimaryDataDirectoryImplementation.STRING_ID), primId),
					criteriaBuilder.equal(fileRoot.type(), entityClass), criteriaBuilder
							.greaterThan(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID), lowerBound)));
		} else {
			primaryEntityCriteria.where(criteriaBuilder.and(
					criteriaBuilder.equal(
							fileRoot.get("parentDirectory").get(PrimaryDataDirectoryImplementation.STRING_ID), primId),
					criteriaBuilder.equal(fileRoot.type(), entityClass)));
		}
		primaryEntityCriteria.orderBy(criteriaBuilder.asc(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID)));
		return session.createQuery(primaryEntityCriteria).setMaxResults(DIRECTORY_FETCH_SIZE)
				.scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Traverses all files/directories that belong to a PublicReference with a stack
	 * to index data.
	 * 
	 * @param pubRef     The PublicReference of which the files are to be indexed
	 * @param internalId The InternalId of the publicreference
	 * @param session
	 */
	@SuppressWarnings("unchecked")
	private void indexPublicReference(Integer pubRef, String internalId, Session session) {
		/**
		 * Discriminator value kann nicht mit criteria geholt werden -> man müsste das
		 * ganze Object aus der Datenbank holen..
		 **/
		long start = System.currentTimeMillis();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<String> primaryEntityCriteria = criteriaBuilder.createQuery(String.class);
		Root<PrimaryDataDirectoryImplementation> fileRoot = primaryEntityCriteria
				.from(PrimaryDataDirectoryImplementation.class);
		Subquery<String> sub = primaryEntityCriteria.subquery(String.class);
		Root<PublicReferenceImplementation> subRoot = sub.from(PublicReferenceImplementation.class);
		Join<PublicReferenceImplementation, PrimaryDataEntityVersionImplementation> join = subRoot.join("version");
		sub.select(join.get("primaryEntityId"));
		sub.where(criteriaBuilder.equal(subRoot.get(ID), pubRef));
		primaryEntityCriteria.select(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID));
		primaryEntityCriteria.where(fileRoot.in(sub));
		String parentDirectoryId = session.createQuery(primaryEntityCriteria).getSingleResult();
		this.filesCounter = 0;
//		@SuppressWarnings("unchecked")
//		NativeQuery<Object[]> nativeQuery = session.createNativeQuery(
//				"SELECT entities.id, entities.type FROM PUBLICREFERENCES p, entity_versions ev, entities\r\n"
//						+ "where p.id =:publicID and ev.id = p.version_id and entities.id = ev.primaryentityid");
//		nativeQuery.setParameter("publicID", pubRef);
//		Object[] typeId = nativeQuery.getSingleResult();
//		String parentDirFile = (String) typeId[0];
//		DataManager.getImplProv().getLogger().info(end+" "+(System.currentTimeMillis()-start)+" "+primId.getID()+" "+parentDirFile+" "+(primId.getID() == parentDirFile)+" nr1 publicReference "+pubRef);
//		this.indexLogger.debug(end+" "+(System.currentTimeMillis()-start)+" "+primId.getID()+" "+parentDirFile+" "+(primId.getID() == parentDirFile)+" nr1 publicReference "+pubRef);

		// index a new PublicReference version
		this.indexEntityVersion(parentDirectoryId, session, pubRef, internalId, IndexSearchConstants.PUBLICREFERENCE,
				REVISION_TYPE_PUBLICREFERENCE);
		Stack<String> stack = new Stack<>();
		stack.add(parentDirectoryId);
		while (!stack.isEmpty()) {
			String directory = stack.pop();

			// index all child directories and add them to the stack
			String lastId = null;
			int count;
			do {
				/**
				 * Get child directories, first iteration -> lastID == null because we dont want
				 * a lowerbound, if there are more directories to process the lowerbound will
				 * have a use
				 **/
				ScrollableResults primDirs = this.getPrimaryDataFileIds(directory, session,
						PrimaryDataDirectoryImplementation.class, lastId);
				count = 0;
				while (primDirs.next()) {
					lastId = primDirs.getString(0);
					stack.add(lastId);
					this.indexEntityVersion(lastId, session, pubRef, internalId, IndexSearchConstants.INDIVIDUALFILE,
							REVISION_TYPE_DIRECTORY);
					count++;
				}
				primDirs.close();
				session.clear();
			} while (count == DIRECTORY_FETCH_SIZE);
			// index all child files
			lastId = null;
			do {
				ScrollableResults primFiles = this.getPrimaryDataFileIds(directory, session,
						PrimaryDataFileImplementation.class, lastId);
				count = 0;
				while (primFiles.next()) {
					lastId = primFiles.getString(0);
					this.indexEntityVersion(lastId, session, pubRef, internalId, IndexSearchConstants.INDIVIDUALFILE,
							REVISION_TYPE_FILE);
					count++;
				}
				primFiles.close();
				session.clear();
			} while (count == DIRECTORY_FETCH_SIZE);
		}
	}

	private void indexEntityVersion(String file, Session session, Integer pubRef, String internalId, String entityType,
			int revision) {
		long start = System.currentTimeMillis();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<PrimaryDataEntityVersionImplementation> root = criteriaQuery
				.from(PrimaryDataEntityVersionImplementation.class);
		criteriaQuery.select(root.get(ID));
		Subquery<Long> sub = criteriaQuery.subquery(Long.class);
		Root<PrimaryDataEntityVersionImplementation> subRoot = sub.from(PrimaryDataEntityVersionImplementation.class);
		sub.select(criteriaBuilder.max(subRoot.get("revision")));
		sub.where(criteriaBuilder.equal(subRoot.get("primaryEntityId"), file));
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("revision"), sub),
				criteriaBuilder.equal(root.get("primaryEntityId"), file)));
		Integer version = session.createQuery(criteriaQuery).getSingleResult();
		long end = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
//		NativeQuery nativeQuery = session.createNativeQuery("SELECT id FROM ENTITY_VERSIONS \r\n"
//				+ "where primaryentityid =:file and revision = "
//				+ "(select max(revision) from entity_versions where  primaryentityid =:file group by primaryentityid )");
//		Integer version2 = (Integer) nativeQuery.setParameter("file", file).getSingleResult();
		this.indexLogger.debug(System.currentTimeMillis() - start + " " + end + " nr3");
		try {
			ScoreDoc[] versionHit = searcher.search(
					new TermQuery(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version))), 1).scoreDocs;
			while (versionHit == null || versionHit.length == 0) {
				try {
					Thread.sleep(Math.min(
							Math.max(NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
									NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
							NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
				} catch (final InterruptedException e) {
					this.indexLogger.debug("PublicVersionIndexWriterThread was interrupted: " + e.getMessage());
				}
				try {
					IndexReader newReader = DirectoryReader.openIfChanged(directoryReader);
					if (newReader != null) {
						reader.close();
						reader = newReader;
						searcher = new IndexSearcher(reader);
					}
				} catch (IOException e) {
					this.indexLogger.debug("IO Error when opening or closing IndexReader: " + e.getMessage());
				}
				versionHit = searcher.search(
						new TermQuery(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version))),
						1).scoreDocs;
			}
			try {
				Document doc = searcher.doc(versionHit[0].doc);
				String filetype = FilenameUtils.getExtension(doc.get(IndexSearchConstants.TITLE));
				addFacets(doc);
				writer.deleteDocuments(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version)));
				doc.add(new StringField(IndexSearchConstants.INTERNALID, internalId, Store.YES));
				StringBuilder docIDBuilder = new StringBuilder(doc.get(IndexSearchConstants.PRIMARYENTITYID))
						.append(HYPHEN).append(revision);
				doc.add(new StringField(IndexSearchConstants.DOCID, docIDBuilder.toString(), Store.YES));
				if (entityType.equals(IndexSearchConstants.INDIVIDUALFILE)) {
					if (revision == REVISION_TYPE_FILE) {
						doc.add(new StringField(IndexSearchConstants.ENTITYTYPE, IndexSearchConstants.FILE, Store.YES));
						// skip this field, if file has no extension
						if (filetype != null && !filetype.isEmpty()) {
							doc.add(new TextField(IndexSearchConstants.FILETYPE, filetype, Store.YES));
							doc.add(new FacetField(IndexSearchConstants.FILETYPE, filetype));
						}
						long fileSize;
						try {
							fileSize = Long.parseLong(doc.get(IndexSearchConstants.SIZE));
							String mimeType[] = doc.get(IndexSearchConstants.MIMETYPE).split(SEPERATOR);
							if (mimeType[0].toLowerCase().equals("text") && mimeType[1].toLowerCase().equals("plain")
									&& fileSize <= MAX_DOC_SIZE) {
								String[] dateValues = doc.get(IndexSearchConstants.CREATION_DATE).split(HYPHEN);
								if (dateValues.length == 5) {
//									DataManager.getImplProv().getLogger().info("indexing content for:_ "
//											+ doc.get("Title") + " size: " + doc.get(IndexSearchConstants.SIZE));
									Path pathToFile = Paths.get(
											((FileSystemImplementationProvider) DataManager.getImplProv()).getDataPath()
													.toString(),
											dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4],
											file + HYPHEN + doc.get(IndexSearchConstants.REVISION) + ".dat");
									indexFileContent(doc, pathToFile.toFile());
								}

							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							this.indexLogger.debug("String conversion failed: " + e.getMessage());
							fileSize = -1;
						}
					} else if (revision == REVISION_TYPE_DIRECTORY) {
						doc.add(new StringField(IndexSearchConstants.ENTITYTYPE, IndexSearchConstants.DIRECTORY,
								Store.YES));
					}
				} else {
					doc.add(new StringField(IndexSearchConstants.ENTITYTYPE, entityType, Store.YES));
				}
				doc.add(new StringField(IndexSearchConstants.PUBLICID, String.valueOf(pubRef), Store.YES));
				this.writer.addDocument(config.build(taxoWriter, doc));
				this.indexedVersions++;
				this.filesCounter++;
			} catch (IOException e) {
				this.indexLogger.debug("PublicVersionIndexWriterThread was interrupted: " + e.getMessage());
			}
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug(e.getMessage());
		}

		if (indexedVersions != 0 && indexedVersions % FETCH_SIZE == 0) {
			try {
				writer.commit();
				this.taxoWriter.commit();
				try {
					FileOutputStream fos = new FileOutputStream(Paths
							.get(this.indexDirectory.toString(), IndexSearchConstants.NATIVE_INDEXER_LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(((NativeLuceneIndexWriterThread) ((FileSystemImplementationProvider) DataManager
							.getImplProv()).getIndexThread()).getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					this.indexLogger.debug("Error writing the native_last_id: " + e.getMessage());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			flushedObjects += FETCH_SIZE;
			if (indexedVersions > 0 && pubRef > PublicVersionIndexWriterThread.getLastID()) {
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), IndexSearchConstants.PUBLIC_LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(PublicVersionIndexWriterThread.getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					this.indexLogger.debug("Error writing the public_last_id: " + e.getMessage());

				}
			}
		}
	}

	/**
	 * Indexes the content of text files
	 * 
	 * @param doc  The Document to which the text will be added
	 * @param file The text file
	 * @throws IOException
	 */
	private void indexFileContent(Document doc, File file) throws IOException {
		if (file != null && file.exists()) {
			// reading and appending single chars for best index and search performance
			BufferedReader in = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();

			int ch;
			int pos = -1;
			while ((ch = in.read()) != -1) {
				char theChar = (char) ch;
				if ((builder.length() + 1) == IndexWriter.MAX_STORED_STRING_LENGTH) {
					// if last appended char is a whitespace or if no whitespace found.. just store
					// the string in a content field
					if (pos == builder.length() - 1 || pos < 1) {
						doc.add(new TextField(IndexSearchConstants.CONTENT, builder.toString(), Store.YES));
						builder = new StringBuilder(IndexWriter.MAX_STORED_STRING_LENGTH);
						pos = -1;
					} else {
						// split up the last part of the string to the last whitespace for a clean cut
						// between stored Fields
						String builderString = builder.toString();
						doc.add(new TextField(IndexSearchConstants.CONTENT, builderString.substring(0, pos),
								Store.YES));
						builder = new StringBuilder(IndexWriter.MAX_STORED_STRING_LENGTH);
						builder.append(builderString.substring(pos));
						pos = -1;
					}
				}
				builder.append(theChar);
				if (Character.isWhitespace(theChar)) {
					pos = builder.length() - 1;
				}
			}
			if (builder.length() > 0) {
				doc.add(new TextField(IndexSearchConstants.CONTENT, builder.toString(), Store.YES));
			}
			in.close();
		}
	}

	/**
	 * Uses and analyzes existing Lucene fields of a Document to add FacetFields to
	 * the Doc that allow faceted searching.
	 * 
	 * @param doc The Lucene document that must contain fields like (Description,
	 *            title..)
	 * @throws IOException
	 */
	private void addFacets(Document doc) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream(IndexSearchConstants.DESCRIPTION,
				doc.get(IndexSearchConstants.DESCRIPTION));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(IndexSearchConstants.DESCRIPTION, termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = analyzer.tokenStream(IndexSearchConstants.TITLE, doc.get(IndexSearchConstants.TITLE));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(IndexSearchConstants.TITLE, termAttribute.toString()));
			}
		}
		tokenStream.close();
		tokenStream = analyzer.tokenStream(IndexSearchConstants.SUBJECT, doc.get(IndexSearchConstants.SUBJECT));
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
			if (termAttribute != null && termAttribute.toString().length() > 1) {
				doc.add(new FacetField(IndexSearchConstants.SUBJECT, termAttribute.toString()));
			}
		}
		tokenStream.close();

		String[] strings = doc.getValues(IndexSearchConstants.CREATORNAME);
		for (String s : strings) {
			tokenStream = analyzer.tokenStream(IndexSearchConstants.CREATORNAME, s);
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
				doc.add(new FacetField(IndexSearchConstants.CREATORNAME, creator.toString()));
			}
		}
		strings = doc.getValues(IndexSearchConstants.CONTRIBUTORNAME);
		for (String s : strings) {
			tokenStream = analyzer.tokenStream(IndexSearchConstants.CONTRIBUTORNAME, s);
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
				doc.add(new FacetField(IndexSearchConstants.CONTRIBUTORNAME, contributor.toString()));
			}
		}
		strings = doc.getValues(IndexSearchConstants.STARTDATE);
		for (String s : strings) {
			doc.add(new FacetField(IndexSearchConstants.STARTDATE, s));
		}
		doc.add(new FacetField(IndexSearchConstants.SIZE, doc.get(IndexSearchConstants.SIZE)));
	}

	/**
	 * 
	 */
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
		// final FullTextSession fullTextSession = Search.getFullTextSession(session);

//		fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);
//		fullTextSession.setCacheMode(CacheMode.NORMAL);

		// fullTextSession.flushToIndexes();
//
//		final SearchFactory searchFactory = Search.getFullTextSession(session).getSearchFactory();
//		final IndexReaderAccessor readerProvider = searchFactory.getIndexReaderAccessor();
//		final IndexReader reader = readerProvider.open(MyUntypedData.class);

		// this.indexWriterThreadLogger.debug("Number of docs after index rebuild: " +
		// reader.numDocs());

		// readerProvider.close(reader);

		this.setLastID(0);

		this.requestForReset = false;

		this.indexWriterThreadLogger.debug("Index structure deleted, restart index calculating...");
		this.implementationProviderLogger.info("Index structure deleted, restart index calculating...");

	}

	/**
	 * Set the last indexed Publicreference ID
	 * 
	 * @param val The last ID to set
	 */
	protected void setLastID(int val) {
		PublicVersionIndexWriterThread.lastIndexedID = val;
	}

	/**
	 * Getter for the last indexed Publicreference ID
	 * 
	 * @return
	 */
	protected static int getLastID() {
		return PublicVersionIndexWriterThread.lastIndexedID;
	}

	/**
	 * Getter for the IndexReader
	 * 
	 * @return The IndexReader
	 */
	public IndexReader getReader() {
		return reader;
	}

	@Override
	public void run() {
		super.run();
		try {
			this.taxoWriter.close();
			this.reader.close();
		} catch (IOException e) {
			this.indexLogger.debug("Error closing the Indexwriter/Taxonomywriter: " + e.getMessage());
		}
		this.implementationProviderLogger.info("finished (PublicVersionIndexWriter), now counting Down Latch");
		this.countDownLatch.countDown();
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