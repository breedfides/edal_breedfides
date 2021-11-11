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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.math.BigDecimal;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHits.Relation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.io.IOUtils;
import org.hibernate.CacheMode;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
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
	/* constants for indexed lucene fields that are used for public searching */
	public static final String PUBLICID = "PublicReference";
	public static final String INTERNALID = "internalId";
	public static final String REVISION = "revision";
	public static final String PUBLICREFERENCE = "dataset";
	public static final String INDIVIDUALFILE = "singledata";
	public static final String DIRECTORY = "directory";
	public static final String FILE = "file";
	public static final String DOCID = "docid";
	
	public static final int MAXDOCSIZE = Integer.MAX_VALUE;
	int docCount = 0;
	private final short REVISIONFILE = 1;
	private final short REVISIONDIRECTORY = 0;
	private final short REVISIONPUBLICREFERENCE = 2;
	protected static int lastIndexedID = 0;
	private Analyzer analyzer = null;
	int counterValue = 0;
	int indexedVersions = 0;
	int flushedObjects = 0;
	int filesCounter = 0;
	Boolean indexedData = false;
	IndexWriter writer = null;
	IndexSearcher searcher = null;
	IndexReader reader = null;
	DirectoryTaxonomyWriter taxoWriter = null;
	private final FacetsConfig config = new FacetsConfig();

	Directory index;
	/** high value fetch objects faster, but more memory is needed */
	final int fetchSize = (int) Math.pow(10, 5);
	final int directoryFetchSize = (int) Math.pow(10, 5);

	DirectoryReader directoryReader;
	public static final String INDEX_NAME = "Master_Index";
	private Path pathToLastId = Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat");

	protected PublicVersionIndexWriterThread(SessionFactory sessionFactory, Path indexDirectory,
			Logger implementationProviderLogger, IndexWriter writer, DirectoryTaxonomyWriter taxoWriter) {
		super(sessionFactory, indexDirectory, implementationProviderLogger);
		this.writer = writer;
		this.analyzer = writer.getAnalyzer();
		int numberDocs = 0;
		try {
			this.taxoWriter = taxoWriter;
			config.setMultiValued(MetaDataImplementation.CREATORNAME, true);
			config.setMultiValued(MetaDataImplementation.CONTRIBUTORNAME, true);
			config.setMultiValued(MetaDataImplementation.TITLE, true);
			config.setMultiValued(MetaDataImplementation.SUBJECT, true);
			config.setMultiValued(MetaDataImplementation.DESCRIPTION, true);
			index = FSDirectory.open(Paths.get(this.indexDirectory.toString(), "Master_Index"));
			reader = DirectoryReader.open(writer);
			searcher = new IndexSearcher(reader);
			directoryReader = DirectoryReader.open(writer);
			numberDocs += reader.numDocs();
		} catch (IOException e) {
			e.printStackTrace();
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
		this.indexWriterThreadLogger
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
			criteria.where(criteriaBuilder.and(predicateId, predicateAccepted, predicateType))
					.orderBy(criteriaBuilder.asc(root.get("id")));

			/** Transaction is needed for ScrollableResults */
			session.getTransaction().begin();

			/**
			 * ScrollableResults will avoid loading too many objects in memory
			 */
			final ScrollableResults results = session.createQuery(criteria).setCacheable(false).setMaxResults(fetchSize)
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
						oos.writeObject(NativeLuceneIndexWriterThread.getLastID());
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
							Paths.get(this.indexDirectory.toString(), "last_id_publicreference.dat").toFile());
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

	private void updateIndex(Integer pubRef, String internalId, Session session) {
		this.filesCounter = 0;
		// Alternative way to fetch the PrimaryDataFileImplementation associated with
		// the publicReference (requieres entire publicReference)
		// PrimaryDataFileImplementation parentDirFile =
		// session.get(PrimaryDataFileImplementation.class,
		// pubRef.getVersion().getPrimaryEntityId());

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
							PublicVersionIndexWriterThread.PUBLICREFERENCE, REVISIONPUBLICREFERENCE);
				} else {
					this.updateVersions(dir, session, pubRef, internalId, PublicVersionIndexWriterThread.INDIVIDUALFILE,
							REVISIONDIRECTORY);
				}
				long dirStart = System.currentTimeMillis();
				nativeQuery = session.createNativeQuery("SELECT id, type FROM ENTITIES \r\n"
						+ "where parentdirectory_id =:parentdir\r\n" + "order by id");
				ScrollableResults results = nativeQuery.setParameter("parentdir", dir).setMaxResults(directoryFetchSize)
						.scroll(ScrollMode.FORWARD_ONLY);
//				String hql = "from PrimaryDataFileImplementation s "
//						+ "where s.parentDirectory.id = :id order by s.id";
//				ScrollableResults results = session.createQuery(hql).setParameter("id", dir.getID()).setMaxResults(directoryFetchSize).scroll(ScrollMode.FORWARD_ONLY);
				int count = 0;
				String tempFileId = null;
				while (results.next()) {
					tempFileId = (String) results.get(0);
					if (((Character) results.get(1)).equals('D')) {
						stack.add(tempFileId);
					} else {
						this.updateVersions(tempFileId, session, pubRef, internalId,
								PublicVersionIndexWriterThread.INDIVIDUALFILE, REVISIONFILE);
					}
					count++;
				}
				results.close();
				session.clear();
				nativeQuery = session.createNativeQuery("SELECT id, type FROM ENTITIES \r\n"
						+ "where parentdirectory_id =:parentdir and id >:last\r\n" + "order by id");
//				hql = "from PrimaryDataFileImplementation s "
//						+ "where s.parentDirectory.id = :id AND s.id > :lastid order by s.id";
				while (count == directoryFetchSize) {
					count = 0;
					results = nativeQuery.setParameter("parentdir", dir).setParameter("last", tempFileId)
							.setMaxResults(directoryFetchSize).scroll(ScrollMode.FORWARD_ONLY);
					// results = session.createQuery(hql).setParameter("id",
					// dir.getID()).setParameter("lastid",
					// tempFile.getID()).setMaxResults(directoryFetchSize).scroll(ScrollMode.FORWARD_ONLY);
					while (results.next()) {
						tempFileId = (String) results.get(0);
						if (((Character) results.get(1)).equals('D')) {
							stack.add(tempFileId);
						} else {
							this.updateVersions(tempFileId, session, pubRef, internalId,
									PublicVersionIndexWriterThread.INDIVIDUALFILE, REVISIONFILE);
						}
						count++;
					}
					results.close();
					session.clear();
				}
			}
		} else {
			this.updateVersions(parentDirFile, session, pubRef, internalId,
					PublicVersionIndexWriterThread.PUBLICREFERENCE, REVISIONFILE);
		}
	}

	private void updateVersions(String file, Session session, Integer pubRef, String internalId, String entityType,
			int revision) {
		NativeQuery nativeQuery = session.createNativeQuery("SELECT id FROM ENTITY_VERSIONS \r\n"
				+ "where primaryentityid =:file and revision = "
				+ "(select max(revision) from entity_versions where  primaryentityid =:file group by primaryentityid )");
		long hibernateQueryStart = System.currentTimeMillis();
		Integer version = (Integer) nativeQuery.setParameter("file", file).getSingleResult();
		hibernateQueryStart = System.currentTimeMillis() - hibernateQueryStart;
		ScoreDoc[] hits2 = null;
		try {
			Term term = new Term(MetaDataImplementation.VERSIONID, Integer.toString(version));
			hits2 = searcher.search(new TermQuery(term), 1).scoreDocs;
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
					hits2 = searcher.search(new TermQuery(term), 1).scoreDocs;
				} while (hits2 == null || hits2.length == 0);
			}
		} catch (IOException e) {
			this.indexWriterThreadLogger.debug("Querry Error: " + e.getMessage());
		}

		long indexingTimeForOneDocument = 0;
		try {
			Document doc = searcher.doc(hits2[0].doc);
			String filetype = FilenameUtils.getExtension(doc.get(MetaDataImplementation.TITLE));
			addFacets(doc);
			writer.deleteDocuments(new Term(MetaDataImplementation.VERSIONID, Integer.toString(version)));
			String pID = String.valueOf(pubRef);
			doc.add(new StringField(PublicVersionIndexWriterThread.INTERNALID, internalId, Store.YES));
			StringBuilder docIDBuilder = new StringBuilder(doc.get(MetaDataImplementation.PRIMARYENTITYID)).append("-")
					.append(pID);
			doc.add(new StringField(PublicVersionIndexWriterThread.DOCID, docIDBuilder.toString(), Store.YES));
			String title = doc.get(MetaDataImplementation.TITLE);
			if (entityType.equals(PublicVersionIndexWriterThread.INDIVIDUALFILE)) {
				if (revision == REVISIONFILE) {
					doc.add(new StringField(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.FILE,
							Store.YES));
					if (title != null) {
						// skip this field, if file has no extension
						if (filetype != null && filetype.length() > 0) {
							doc.add(new TextField(MetaDataImplementation.FILETYPE, filetype, Store.YES));
							doc.add(new FacetField(MetaDataImplementation.FILETYPE, filetype));
						}
						int fileSize;
						try {
							fileSize = Integer.parseInt(doc.get(MetaDataImplementation.SIZE));
						}
						catch (NumberFormatException e)
						{
							fileSize = PublicVersionIndexWriterThread.MAXDOCSIZE;
						}
						String mimeType[] = doc.get(MetaDataImplementation.MIMETYPE).split("/");
						DataManager.getImplProv().getLogger().info(title+" size: "+fileSize);
						if (mimeType[0].toLowerCase().equals("text") && mimeType[1].toLowerCase().equals("plain") && fileSize < PublicVersionIndexWriterThread.MAXDOCSIZE) {						
							DataManager.getImplProv().getLogger().info("Indexing Content for "+title);
							PrimaryDataFileImplementation pdfile = session.get(PrimaryDataFileImplementation.class,
									file);
							if(pdfile.getPathToLocalFile(pdfile.getCurrentVersion()).toFile().exists()) {
								
								char[] myBuffer = new char[512];
								BufferedReader in = new BufferedReader(new FileReader(pdfile.getPathToLocalFile(pdfile.getCurrentVersion()).toFile()));
								StringBuilder builder = new StringBuilder();
								
								int ch;
								char lastSeperator;
								while((ch = in.read()) != -1) {
								    char theChar = (char) ch;
								    if(Character.isWhitespace(theChar)) {
								    	//in.mar
								    }
								    if((builder.length()+1) == IndexWriter.MAX_STORED_STRING_LENGTH) {
								    	DataManager.getImplProv().getLogger().info("1datasize: "+builder.length());

								    	doc.add(new TextField("Content",builder.toString(),Store.YES));
								    	builder = new StringBuilder(IndexWriter.MAX_STORED_STRING_LENGTH);
								    }
							    	builder.append(theChar);
								}
								if(builder.length() > 0) {
									String c = builder.toString();
							    	DataManager.getImplProv().getLogger().info("2datasize: string length: "+c.length() +" vs builder.length: "+builder.length()+"last part: "+c.substring(c.length()-70,c.length()-1));
							    	doc.add(new TextField("Content",c,Store.YES));
								}
								in.close();
								
//								int bytesRead = in.read(myBuffer,0,myBuffer.length);
//								while (bytesRead != -1)
//								{
//									String string= new String(myBuffer, 0, bytesRead);
//								    if((builder.length() + string.length()) < MAX_FIELD_SIZE) {
//									    builder.append(string);
//								    }else {
//								    	DataManager.getImplProv().getLogger().info("1datasize: "+builder.length());
//								    	doc.add(new TextField("Content",builder.toString(),Store.YES));
//								    	builder.setLength(0);
//								    	builder.append(string);
//								    }
//								    bytesRead = in.read(myBuffer,0,myBuffer.length);
//								}
//								String c = builder.toString();
//						    	DataManager.getImplProv().getLogger().info("2datasize: string length: "+c.length() +" vs builder.length: "+builder.length()+"last part: "+c.substring(c.length()-70,c.length()-1));
//								doc.add(new TextField("Content",builder.toString(),Store.YES));
//								in.close();
								// needed to not slow down hibernate session
							}
							session.evict(pdfile);
						}
					}
				} else if (revision == REVISIONDIRECTORY) {
					doc.add(new StringField(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.DIRECTORY,
							Store.YES));
				}
			} else {
				doc.add(new StringField(MetaDataImplementation.ENTITYTYPE, entityType, Store.YES));
			}

			doc.add(new StringField(PublicVersionIndexWriterThread.PUBLICID, pID, Store.YES));
			indexingTimeForOneDocument = System.currentTimeMillis();
			writer.addDocument(config.build(taxoWriter, doc));
			long finishedAdding = System.currentTimeMillis();
			indexingTimeForOneDocument = finishedAdding - indexingTimeForOneDocument;
			DataManager.getImplProv().getLogger().info("DocNr. " + (++docCount) + " ms:" + indexingTimeForOneDocument
					+ " hibernteQuery: " + hibernateQueryStart + "BYTES: " + writer.ramBytesUsed());
			indexedVersions++;
			this.filesCounter++;
//				this.implementationProviderLogger
//						.info("UPDATED VERSION: " + doc.get(MetaDataImplementation.TITLE)
//								+ " ID:" + doc.get(MetaDataImplementation.VERSIONID));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (indexedVersions != 0 && indexedVersions % fetchSize == 0) {
			try {
				writer.commit();
				this.taxoWriter.commit();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
			flushedObjects += fetchSize;
			if (indexedVersions > 0 && pubRef > PublicVersionIndexWriterThread.getLastID()) {
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
			}
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
		TokenStream ts = analyzer.tokenStream(MetaDataImplementation.DESCRIPTION,
				doc.get(MetaDataImplementation.DESCRIPTION));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(MetaDataImplementation.DESCRIPTION, ta.toString()));
		}
		ts.close();
		ts = analyzer.tokenStream(MetaDataImplementation.TITLE, doc.get(MetaDataImplementation.TITLE));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(MetaDataImplementation.TITLE, ta.toString()));
		}
		ts.close();
		ts = analyzer.tokenStream(MetaDataImplementation.SUBJECT, doc.get(MetaDataImplementation.SUBJECT));
		ts.reset();
		while (ts.incrementToken()) {
			CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
			if (ta.toString().length() > 1)
				doc.add(new FacetField(MetaDataImplementation.SUBJECT, ta.toString()));
		}
		ts.close();

		String[] strings = doc.getValues(MetaDataImplementation.CREATORNAME);
		for (String s : strings) {
			ts = analyzer.tokenStream(MetaDataImplementation.CREATORNAME, s);
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
				doc.add(new FacetField(MetaDataImplementation.CREATORNAME, creator.toString()));
			}
		}
		strings = doc.getValues(MetaDataImplementation.CONTRIBUTORNAME);
		for (String s : strings) {
			ts = analyzer.tokenStream(MetaDataImplementation.CONTRIBUTORNAME, s);
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
				doc.add(new FacetField(MetaDataImplementation.CONTRIBUTORNAME, contributor.toString()));
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