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
import java.util.Date;
import java.util.Stack;
import java.util.StringJoiner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * IndexWriterThread class to realize manual indexing strategy
 * 
 * @author arendd
 */
public class PublicVersionIndexWriterThread extends IndexWriterThread {

	private static final String LAST_ID = "last_id_publicreference.dat";

	/** The max number of bytes for the indexing of file content **/
	private final int MAX_DOC_SIZE = 100 * 1024 * 1024;
	
	/** The revision type determines the url/path ending and is needed to
	 *  resolve the correct "URL" at search time **/
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
	/** Used for indexing of Facets, holds information about the indexed Facet-dimensions **/
	private final FacetsConfig config = new FacetsConfig();

	/** high value fetch objects faster, but more memory is needed */
	private final int FETCH_SIZE = (int) Math.pow(10, 5);
	private final int DIRECTORY_FETCH_SIZE = (int) Math.pow(10, 5);

	private DirectoryReader directoryReader;
	public static final String INDEX_NAME = "Master_Index";
	private Path pathToLastId = Paths.get(this.indexDirectory.toString(), LAST_ID);

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
			this.indexLogger.debug("Error occured while starting the PublicVersionIndexWriterThread (opening Lucene IO tools): "+e.getMessage());
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
		this.indexLogger
				.debug("Last indexed public reference: " + PublicVersionIndexWriterThread.getLastID());

	}

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
					reader.close();
					reader = newReader;
					searcher = new IndexSearcher(reader);
				}
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			final Session session = this.sessionFactory.openSession();
			session.setDefaultReadOnly(true);

			/**
			 * Caching for Hibernate entities disabled because they are only used once and
			 * to prevent running out of memory
			 */
			session.setCacheMode(CacheMode.IGNORE);
			indexedVersions = 0;
			flushedObjects = 0;
			executeIndexingStart = System.currentTimeMillis();

			final long queryStartTime = System.currentTimeMillis();

			countNewReferences(session);
			/** Load all new PublicReference.IDs */
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Object[]> criteria = criteriaBuilder.createQuery(Object[].class);
			Root<PublicReferenceImplementation> root = criteria.from(PublicReferenceImplementation.class);
			criteria.multiselect(root.get("id"), root.get("internalID"));
			Predicate predicateId = criteriaBuilder.gt(root.get("id"), PublicVersionIndexWriterThread.getLastID());
			Predicate predicateAccepted = criteriaBuilder.equal(root.get("publicationStatus"),
					PublicationStatus.ACCEPTED);
			Predicate predicateType = criteriaBuilder.equal(root.get("identifierType"), PersistentIdentifier.DOI);
			if (DataManager.getConfiguration().isInTestMode()) {
				criteria.where(criteriaBuilder.and(predicateId, predicateType))
						.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteria.where(criteriaBuilder.and(predicateId, predicateAccepted, predicateType))
						.orderBy(criteriaBuilder.asc(root.get("id")));
			}

			/** Transaction is needed for ScrollableResults */
			session.getTransaction().begin();

			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setCacheable(false).setMaxResults(FETCH_SIZE)
					.scroll(ScrollMode.FORWARD_ONLY);

			final long queryTime = System.currentTimeMillis() - queryStartTime;

			final long indexStartTime = System.currentTimeMillis();
			Integer publicRef = null;
			String internalId = null;
			long startTime = System.currentTimeMillis();
			this.filesCounter = 0;
			while (results.next()) {
				Object[] objs = (Object[]) results.get(0);
				publicRef = (Integer) objs[0];
				internalId = (String) objs[1];
				/** index all associated Files/Directories */
				updateIndex(publicRef, internalId, session);
				this.setLastID(publicRef);
				try {
					DataManager.getSearchManager().maybeRefresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.filesCounter > 0) {
				DataManager.getImplProv().getLogger().info("[PublicVersionIndexWriterThread] Indexing time: "
						+ (System.currentTimeMillis() - startTime) + "ms");
				indexedData = true;
			} else {
				if (indexedData) {
					DataManager.getImplProv().getLogger().info("[PublicVersionIndexWriterThread] Indexing finished");
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
					try {
						FileOutputStream fos = new FileOutputStream(
								Paths.get(this.indexDirectory.toString(), "NativeLucene_last_id.dat").toFile());
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(((NativeLuceneIndexWriterThread)((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexThread()).getLastID());
						oos.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.indexWriterThreadLogger
						.debug("INDEXING SUCCESSFUL : indexed objects|flushed objects|Index|Query : " + indexedVersions
								+ " | " + flushedObjects + " | " + df.format(new Date(indexingTime)) + " | "
								+ df.format(new Date(queryTime)));
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(PublicVersionIndexWriterThread.getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (newReader != null)
					newReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(Math.min(
						Math.max(indexingTime * NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
								NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
						NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
			} catch (final InterruptedException e) {
				e.printStackTrace();
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
	 * Checks if there are 
	 * @param session
	 */
	private void countNewReferences(Session session) {
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaLong = criteriaBuilder.createQuery(Long.class);
		Root<PublicReferenceImplementation> root = criteriaLong.from(PublicReferenceImplementation.class);
		criteriaLong.select(criteriaBuilder.count(root));
		Predicate predicateId = criteriaBuilder.gt(root.get("id"), PublicVersionIndexWriterThread.getLastID());
		Predicate predicateAccepted = criteriaBuilder.equal(root.get("publicationStatus"), PublicationStatus.ACCEPTED);
		Predicate predicateType = criteriaBuilder.equal(root.get("identifierType"), PersistentIdentifier.DOI);
		criteriaLong.where(criteriaBuilder.and(predicateId, predicateAccepted, predicateType));
		long l = session.createQuery(criteriaLong).getSingleResult();
		if (l > 0) {
			DataManager.getImplProv().getLogger().info("Publicreferences that still have to be indexed " + l);
		}
	}

	/**
	 * Traverses all files/directories that belong to a PublicReference with a stack to index data.
	 * @param pubRef The PublicReference of which the files are to be indexed
	 * @param internalId The InternalId of the publicreference
	 * @param session
	 */
	private void updateIndex(Integer pubRef, String internalId, Session session) {
		this.filesCounter = 0;
		NativeQuery<Object[]> nativeQuery = session.createNativeQuery(
				"SELECT entities.id, entities.type FROM PUBLICREFERENCES p, entity_versions ev, entities\r\n"
						+ "where p.id =:publicID and ev.id = p.version_id and entities.id = ev.primaryentityid");
		nativeQuery.setParameter("publicID", pubRef);
		Object[] typeId = nativeQuery.getSingleResult();
		String parentDirFile = (String) typeId[0];

		Stack<String> stack = new Stack<>();
		if (((Character) typeId[1]).equals('D')) {
			stack.add(parentDirFile);
			while (!stack.isEmpty()) {
				String dir = stack.pop();
				if (parentDirFile.equals(dir)) {
					this.updateVersions(dir, session, pubRef, internalId,
							IndexSearchConstants.PUBLICREFERENCE, REVISION_TYPE_PUBLICREFERENCE);
				} else {
					this.updateVersions(dir, session, pubRef, internalId, IndexSearchConstants.INDIVIDUALFILE,
							REVISION_TYPE_DIRECTORY);
				}
				//get all children of the current directory
				nativeQuery = session.createNativeQuery("SELECT id, type FROM ENTITIES \r\n"
						+ "where parentdirectory_id =:parentdir\r\n" + "order by id");
				ScrollableResults results = nativeQuery.setParameter("parentdir", dir).setMaxResults(DIRECTORY_FETCH_SIZE)
						.scroll(ScrollMode.FORWARD_ONLY);
				int count = 0;
				String tempFileId = null;
				//add child directories to the stack, process child files immediately
				while (results.next()) {
					tempFileId = (String) results.get(0);
					if (((Character) results.get(1)).equals('D')) {
						stack.add(tempFileId);
					} else {
						this.updateVersions(tempFileId, session, pubRef, internalId,
								IndexSearchConstants.INDIVIDUALFILE, REVISION_TYPE_FILE);
					}
					count++;
				}
				results.close();
				session.clear();
				nativeQuery = session.createNativeQuery("SELECT id, type FROM ENTITIES \r\n"
						+ "where parentdirectory_id =:parentdir and id >:last\r\n" + "order by id");
//				hql = "from PrimaryDataFileImplementation s "
//						+ "where s.parentDirectory.id = :id AND s.id > :lastid order by s.id";
				while (count == DIRECTORY_FETCH_SIZE) {
					count = 0;
					results = nativeQuery.setParameter("parentdir", dir).setParameter("last", tempFileId)
							.setMaxResults(DIRECTORY_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
					// results = session.createQuery(hql).setParameter("id",
					// dir.getID()).setParameter("lastid",
					// tempFile.getID()).setMaxResults(directoryFetchSize).scroll(ScrollMode.FORWARD_ONLY);
					while (results.next()) {
						tempFileId = (String) results.get(0);
						if (((Character) results.get(1)).equals('D')) {
							stack.add(tempFileId);
						} else {
							this.updateVersions(tempFileId, session, pubRef, internalId,
									IndexSearchConstants.INDIVIDUALFILE, REVISION_TYPE_FILE);
						}
						count++;
					}
					results.close();
					session.clear();
				}
			}
		} else {
			this.updateVersions(parentDirFile, session, pubRef, internalId,
					IndexSearchConstants.PUBLICREFERENCE, REVISION_TYPE_FILE);
		}
	}

	private void updateVersions(String file, Session session, Integer pubRef, String internalId, String entityType,
			int revision) {
		NativeQuery nativeQuery = session.createNativeQuery("SELECT id FROM ENTITY_VERSIONS \r\n"
				+ "where primaryentityid =:file and revision = "
				+ "(select max(revision) from entity_versions where  primaryentityid =:file group by primaryentityid )");
		Integer version = (Integer) nativeQuery.setParameter("file", file).getSingleResult();
		try {
			ScoreDoc[] hits2 = searcher.search(
					new TermQuery(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version))), 1).scoreDocs;
			if (hits2 == null || hits2.length == 0) {
				do {
					try {
						Thread.sleep(Math.min(
								Math.max(NativeLuceneIndexWriterThread.SLEEP_RUNTIME_FACTOR,
										NativeLuceneIndexWriterThread.MIN_THREAD_SLEEP_MILLISECONDS),
								NativeLuceneIndexWriterThread.MAX_THREAD_SLEEP_MILLISECONDS));
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
					try {
						IndexReader newReader = DirectoryReader.openIfChanged(directoryReader);
						if (newReader != null) {
							reader.close();
							reader = newReader;
							searcher = new IndexSearcher(reader);
						}
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					hits2 = searcher.search(
							new TermQuery(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version))),
							1).scoreDocs;
				} while (hits2 == null || hits2.length == 0);
			}
			try {
				Document doc = searcher.doc(hits2[0].doc);
				String filetype = FilenameUtils.getExtension(doc.get(IndexSearchConstants.TITLE));
				addFacets(doc);
				writer.deleteDocuments(new Term(IndexSearchConstants.VERSIONID, Integer.toString(version)));
				doc.add(new StringField(IndexSearchConstants.INTERNALID, internalId, Store.YES));
				// test docid with revision instead of pubRef
				StringBuilder docIDBuilder = new StringBuilder(doc.get(IndexSearchConstants.PRIMARYENTITYID))
						.append("-").append(revision);
				doc.add(new StringField(IndexSearchConstants.DOCID, docIDBuilder.toString(), Store.YES));
				if (entityType.equals(IndexSearchConstants.INDIVIDUALFILE)) {
					if (revision == REVISION_TYPE_FILE) {
						doc.add(new StringField(IndexSearchConstants.ENTITYTYPE, IndexSearchConstants.FILE,
								Store.YES));
						// skip this field, if file has no extension
						if (filetype != null && filetype.length() > 0) {
							doc.add(new TextField(IndexSearchConstants.FILETYPE, filetype, Store.YES));
							doc.add(new FacetField(IndexSearchConstants.FILETYPE, filetype));
						}
						int fileSize;
						try {
							fileSize = Integer.parseInt(doc.get(IndexSearchConstants.SIZE));
							String mimeType[] = doc.get(IndexSearchConstants.MIMETYPE).split("/");
							if (mimeType[0].toLowerCase().equals("text") && mimeType[1].toLowerCase().equals("plain")
									&& fileSize <= MAX_DOC_SIZE) {
								String[] dateValues = doc.get("VersionCreationDate").split("-");
								if (dateValues.length == 5) {
//									DataManager.getImplProv().getLogger().info("indexing content for:_ "
//											+ doc.get("Title") + " size: " + doc.get(IndexSearchConstants.SIZE));
									Path pathToFile = Paths.get(
											((FileSystemImplementationProvider) DataManager.getImplProv()).getDataPath()
													.toString(),
											dateValues[0], dateValues[1], dateValues[2], dateValues[3], dateValues[4],
											file + "-" + doc.get(IndexSearchConstants.REVISION) + ".dat");
									indexFileContent(doc, pathToFile.toFile());
								}

							}
						} catch (NumberFormatException e) {
							fileSize = -1;
						}
					} else if (revision == REVISION_TYPE_DIRECTORY) {
						doc.add(new StringField(IndexSearchConstants.ENTITYTYPE,
								IndexSearchConstants.DIRECTORY, Store.YES));
					}
				} else {
					doc.add(new StringField(IndexSearchConstants.ENTITYTYPE, entityType, Store.YES));
				}

				doc.add(new StringField(IndexSearchConstants.PUBLICID, String.valueOf(pubRef), Store.YES));
				writer.addDocument(config.build(taxoWriter, doc));
				indexedVersions++;
				this.filesCounter++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Querry Error: " + e.getMessage());
		}

		if (indexedVersions != 0 && indexedVersions % FETCH_SIZE == 0) {
			try {
				writer.commit();
				this.taxoWriter.commit();
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), "NativeLucene_last_id.dat").toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(((NativeLuceneIndexWriterThread)((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexThread()).getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			flushedObjects += FETCH_SIZE;
			if (indexedVersions > 0 && pubRef > PublicVersionIndexWriterThread.getLastID()) {
				try {
					FileOutputStream fos = new FileOutputStream(
							Paths.get(this.indexDirectory.toString(), LAST_ID).toFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(PublicVersionIndexWriterThread.getLastID());
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
						doc.add(new TextField(IndexSearchConstants.CONTENT, builderString.substring(0, pos), Store.YES));
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
		TokenStream ts = analyzer.tokenStream(IndexSearchConstants.DESCRIPTION,
				doc.get(IndexSearchConstants.DESCRIPTION));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(IndexSearchConstants.DESCRIPTION, ta.toString()));
		}
		ts.close();
		ts = analyzer.tokenStream(IndexSearchConstants.TITLE, doc.get(IndexSearchConstants.TITLE));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(IndexSearchConstants.TITLE, ta.toString()));
		}
		ts.close();
		ts = analyzer.tokenStream(IndexSearchConstants.SUBJECT, doc.get(IndexSearchConstants.SUBJECT));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(IndexSearchConstants.SUBJECT, ta.toString()));
		}
		ts.close();		

		String[] strings = doc.getValues(IndexSearchConstants.CREATORNAME);
		for (String s : strings) {
			ts = analyzer.tokenStream(IndexSearchConstants.CREATORNAME, s);
			ts.reset();
			StringJoiner creator = new StringJoiner(" ");
			while (ts.incrementToken()) {
				CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
				String token = ta.toString();
				if (token.length() > 1)
					creator.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			ts.close();
			if (creator.length() > 0) {
				doc.add(new FacetField(IndexSearchConstants.CREATORNAME, creator.toString()));
			}
		}
		strings = doc.getValues(IndexSearchConstants.CONTRIBUTORNAME);
		for (String s : strings) {
			ts = analyzer.tokenStream(IndexSearchConstants.CONTRIBUTORNAME, s);
			ts.reset();
			StringJoiner contributor = new StringJoiner(" ");
			while (ts.incrementToken()) {
				CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
				String token = ta.toString();
				if (token.length() > 1)
					contributor.add(token.substring(0, 1).toUpperCase() + token.substring(1));
			}
			ts.close();
			if (contributor.length() > 0) {
				doc.add(new FacetField(IndexSearchConstants.CONTRIBUTORNAME, contributor.toString()));
			}
		}
		strings = doc.getValues(IndexSearchConstants.STARTDATE);
		for(String s : strings) {
			doc.add(new FacetField(IndexSearchConstants.STARTDATE, s));
		}
		doc.add(new FacetField(IndexSearchConstants.SIZE, doc.get(IndexSearchConstants.SIZE)));
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

	protected void setLastID(int val) {
		PublicVersionIndexWriterThread.lastIndexedID = val;
	}

	protected static int getLastID() {
		return PublicVersionIndexWriterThread.lastIndexedID;
	}

	public IndexReader getReader() {
		return reader;
	}

	@Override
	public void run() {
		super.run();
		try {
			taxoWriter.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
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