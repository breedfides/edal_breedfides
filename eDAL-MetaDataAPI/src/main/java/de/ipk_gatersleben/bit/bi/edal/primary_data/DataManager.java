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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Policy;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.auth.Subject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.TotalHits.Relation;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.velocity.VelocityContext;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ALLPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CalculateDirectorySizeThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CleanBrokenEntitiesThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ListThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.NativeLuceneIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.RebuildIndexThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.CheckReviewStatusThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalCompositePolicy;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPolicy;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * Central entry point for the clients to make use of the API provided storage
 * functionality.
 * 
 * @author lange
 * @author arendd
 */
public class DataManager {

	public static final String TEST_DATA_STRING = "eDAL";

	/**
	 * Private class to provide a intercepter that control a graceful shutdown of
	 * API if some external kill the JVM.
	 * 
	 * @author lange
	 */
	private static class ShutdownInterceptor extends Thread {

		@Override
		public void run() {

			if (!DataManager.alreadyClosed) {
				DataManager.getImplProv().getLogger().warn("eDAL-JVM got external kill signal - graceful shut down");
				DataManager.shutdown();
			}
		}
	}

	private static boolean alreadyClosed = false;

	private static CheckReviewStatusThread checkReviewStatusThread;
	private static CalculateDirectorySizeThread calculateThread;
	private static CleanBrokenEntitiesThread cleanBrokenEntitiesThread;
	private static RebuildIndexThread rebuildIndexThread;

	private static EdalConfiguration configuration;
	/**
	 * Constant map with all initial default permissions.
	 * <p>
	 * 
	 * {@link ALLPrincipal}:
	 * <p>
	 * Methods.listPrimaryDataEntities
	 * <p>
	 * Methods.listPrimaryDataEntitiesByDate
	 * <p>
	 * Methods.getPrimaryDataEntity
	 * <p>
	 * Methods.read
	 * <p>
	 * Methods.exist
	 * <p>
	 * Methods.getParentDirectory
	 * <p>
	 * Methods.getVersions
	 * <p>
	 * Methods.getCurrentVersion
	 * <p>
	 * Methods.searchByDublinCoreElement
	 * <p>
	 * Methods.searchByMetaData
	 */
	public static final Map<Principal, List<Methods>> DEFAULT_PERMISSIONS = new HashMap<Principal, List<Methods>>();

	private static ImplementationProvider implementationprovider;

	private static boolean isConfigurationValid = false;
	/**
	 * Time to wait between every check for root user confirmation
	 */
	private static final int OPT_IN_INTERVAL = 5000;
	/**
	 * The timeout to stop the root user confirmation
	 */
	private static final int OPT_IN_TIMEOUT = 1800000;
	/**
	 * eMail address for the ROOT CHEAT
	 */
	private static final String ROOT_CHEAT = "eDAL0815@ipk-gatersleben.de";
	/**
	 * Internal cheat to avoid the root login process.
	 */
	private static InternetAddress rootCheat;
	private static EdalHttpServer server;
	private static InheritableThreadLocal<Map<Principal, List<Methods>>> threadlocaldefaultpermissions;

	private static CountDownLatch stopLatch;

	/**
	 * Store the {@link Subject} and the {@link ImplementationProvider} of the
	 * getRootDirectory caller for later use, who is the working user of the eDAL
	 * data structure and what are the implementing classes. <br/>
	 * Store the defined userPermissions as thread local.
	 */
	private static InheritableThreadLocal<Subject> threadlocalsubject;

	private static ExecutorService jettyExecutorService = null;
	private static ExecutorService listExecutorService = null;
	private static ExecutorService velocityExecutorService = null;

	/**
	 * Manages the reopining and sharing of IndexSearcher instances
	 */
	private static SearcherManager searchManager = null;
	
	private static TaxonomyReader taxoReader = null;
	private static FacetsConfig config = new FacetsConfig();

	public static SearcherManager getSearchManager() {
		return searchManager;
	}

	static {

		DataManager.jettyExecutorService = Executors.newCachedThreadPool(new EdalThreadFactory("jettyThread"));
		DataManager.listExecutorService = Executors.newCachedThreadPool(new EdalThreadFactory("listThread"));
		DataManager.velocityExecutorService = Executors.newCachedThreadPool(new EdalThreadFactory("velocityThread"));

		DataManager.threadlocalsubject = new InheritableThreadLocal<>();
		DataManager.implementationprovider = null;
		DataManager.threadlocaldefaultpermissions = new InheritableThreadLocal<>();
		DataManager.configuration = null;
		DataManager.checkReviewStatusThread = new CheckReviewStatusThread();
		DataManager.calculateThread = new CalculateDirectorySizeThread();
		DataManager.cleanBrokenEntitiesThread = null;
		DataManager.rebuildIndexThread = new RebuildIndexThread();

		DataManager.server = null;

		// set initial default permissions
		final List<Methods> methods = new ArrayList<Methods>();

		methods.add(Methods.listPrimaryDataEntities);
		methods.add(Methods.getPrimaryDataEntity);
		methods.add(Methods.read);
		methods.add(Methods.exist);
		methods.add(Methods.getParentDirectory);
		methods.add(Methods.searchByDublinCoreElement);
		methods.add(Methods.searchByMetaData);

		DataManager.DEFAULT_PERMISSIONS.put(new ALLPrincipal(), methods);

		DataManager.resetDefaultPermissions();
		
		config.setMultiValued(MetaDataImplementation.CREATORNAME, true);
		config.setMultiValued(MetaDataImplementation.CONTRIBUTORNAME, true);
		config.setMultiValued(MetaDataImplementation.SUBJECT, true);
		config.setMultiValued(MetaDataImplementation.TITLE, true);
		config.setMultiValued(MetaDataImplementation.DESCRIPTION, true);

		try {
			DataManager.rootCheat = new InternetAddress(ROOT_CHEAT);
		} catch (AddressException e) {
			e.printStackTrace();
		}
		DataManager.stopLatch = new CountDownLatch(1);

	}
	/**
	 * Getter for the available space in the mount path of eDAL.
	 * 
	 * @return available space
	 * @throws EdalException if no path is specified.
	 */
	public static Long getAvailableStorageSpace() throws EdalException {
		if (DataManager.getImplProv() == null) {
			throw new EdalException("No ImplementationProvider set --> run getRootDirectory()");
		}
		try {
			return DataManager.getImplProv().getServiceProvider().getDeclaredConstructor().newInstance()
					.getAvailableStorageSpace();
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate ServiceProvider");
		}
	}

	/**
	 * Getter for the {@link EdalConfiguration}.
	 * 
	 * @return the current {@link EdalConfiguration}.
	 */
	public static EdalConfiguration getConfiguration() {
		return DataManager.configuration;
	}

	/**
	 * @return the current set default permissions.
	 */
	public static Map<Principal, List<Methods>> getDefaultPermissions() {
		return DataManager.threadlocaldefaultpermissions.get();
	}

	/**
	 * Getter for the current {@link ImplementationProvider}.
	 * 
	 * @return ImplementationProvider with all implementing classes
	 */
	public static ImplementationProvider getImplProv() {
		return DataManager.implementationprovider;
	}

	/**
	 * Get a specified {@link PrimaryDataEntity} for the request of a HTTPServer,
	 * but only if a PublicReference is defined.
	 * 
	 * @param uuid          the {@link UUID} of the {@link PrimaryDataEntity}.
	 * @param versionNumber the version number of the
	 *                      {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *                      .
	 * @throws EdalException if there is no {@link PrimaryDataEntity} with the
	 *                       specified values or the eDAL system is not started.
	 * @return the specified {@link PrimaryDataEntity}
	 */
	static PrimaryDataEntity getPrimaryDataEntityByID(String uuid, long versionNumber) throws EdalException {

		if (getImplProv() == null || getSubject() == null) {
			throw new EdalException("Unable to load entity : start eDAL system first");
		}
		try {
			return DataManager.getImplProv().getHttpServiceProvider().getDeclaredConstructor().newInstance()
					.getPrimaryDataEntityByID(uuid, versionNumber);
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate HttpServiceProvider");

		}
	}

	static PrimaryDataEntity getPrimaryDataEntityForPersistenIdentifier(String uuid, long versionNumber,
			PersistentIdentifier persistentIdentifier) throws EdalException {

		if (getImplProv() == null || getSubject() == null) {
			throw new EdalException("unable to load entity : start eDAL system first");
		}
		try {
			return DataManager.getImplProv().getHttpServiceProvider().getDeclaredConstructor().newInstance()
					.getPrimaryDataEntityForPersistentIdentifier(uuid, versionNumber, persistentIdentifier);
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate HttpServiceProvider");
		}
	}

	static PrimaryDataEntity getPrimaryDataEntityRekursiveForPersistenIdentifier(PrimaryDataEntity entity,
			long versionNumber, PersistentIdentifier persistentIdentifier) throws EdalException {
		if (getImplProv() == null || getSubject() == null) {
			throw new EdalException("unable to load entity : start eDAL system first");
		}
		try {
			return DataManager.getImplProv().getHttpServiceProvider().getDeclaredConstructor().newInstance()
					.getPrimaryDataEntityRekursiveForPersistenIdentifier(entity, versionNumber, persistentIdentifier);
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate HttpServiceProvider");
		}
	}

	/**
	 * Getter for a {@link PrimaryDataEntity} for the review process to present it
	 * to a reviewer.
	 * 
	 * @param uuid          the ID of the searched {@link PrimaryDataEntity}.
	 * @param versionNumber the version number of the {@link PrimaryDataEntity}.
	 * @param internalId    the internal ID of the corresponding
	 *                      {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @param reviewerCode  the ID to identify the reviewer.
	 * @return the {@link PrimaryDataEntity}
	 * @throws EdalException if unable to load the {@link PrimaryDataEntity}.
	 */
	static PrimaryDataEntity getPrimaryDataEntityForReviewer(String uuid, long versionNumber, String internalId,
			int reviewerCode) throws EdalException {
		if (getImplProv() == null || getSubject() == null) {
			throw new EdalException("unable to load Entity : start eDAL system first");
		}

		try {
			return DataManager.getImplProv().getHttpServiceProvider().getDeclaredConstructor().newInstance()
					.getPrimaryDataEntityForReviewer(uuid, versionNumber, internalId, reviewerCode);
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate HttpServiceProvider");
		}
	}

	/**
	 * Static function to get the root {@link PrimaryDataDirectory} of the
	 * eDAL-System.
	 * 
	 * @param implementationProvider must provide the implementing classes the
	 *                               implementation, which will be used. The call
	 *                               pass the current logged in JAAS subject.For
	 *                               example
	 * 
	 *                               <pre>
	 * ImplementationProvider myImpl = new MyEDALImplementation();
	 * LoginContext CTX = new LoginContext(...);
	 *        CTX();
	 *        Subject mySubject = CTX.getSubject();
	 * PrimaryDataDirectory root_dir = DataManager.getRootDirectory(myImpl, mySubject);
	 *                               </pre>
	 * 
	 * @param subject                the authenticated subject
	 * @return the root {@link PrimaryDataDirectory} for the passed implementation
	 * @throws PrimaryDataDirectoryException if unable to create
	 *                                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData}
	 *                                       instance or if unable to initialize
	 *                                       security system.
	 */
	public static PrimaryDataDirectory getRootDirectory(final ImplementationProvider implementationProvider,
			final Subject subject) throws PrimaryDataDirectoryException {

		if (subject == null) {
			throw new PrimaryDataDirectoryException("Not allowed to use a null subject !");

		}

		Principal principal = null;

		for (Principal p : subject.getPrincipals()) {
			principal = p;
			break;
		}

		if (DataManager.stopLatch.getCount() == 0) {
			DataManager.stopLatch = new CountDownLatch(1);
		}
		IndexWriter writer = ((FileSystemImplementationProvider)implementationProvider).getWriter();
		try {
			searchManager = new SearcherManager(writer,new SearcherFactory());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		/**
		 * set the current subject and implementation provider in ThreadLocal variable
		 * to bound both to the thread
		 */
		DataManager.threadlocalsubject.set(subject);
		DataManager.implementationprovider = implementationProvider;
		DataManager.configuration = implementationProvider.getConfiguration();

		try {
			/** always add ALLPrincipal as supported Principal */
			implementationProvider.getConfiguration().getSupportedPrincipals().add(ALLPrincipal.class);

			if (!implementationProvider.getConfiguration().getSupportedPrincipals().contains(principal.getClass())) {

				DataManager.shutdown();
				throw new PrimaryDataDirectoryException("Your used principal class '" + principal.getClass()
						+ "' is not in the supported principal list of your configuration");
			}

		} catch (EdalConfigurationException e) {
			/**
			 * shut down the DataManager if no supported principal is defined
			 */
			DataManager.shutdown();
			throw new PrimaryDataDirectoryException(e);
		}

		/**
		 * check if the current ImplementationProvider can create a MetaDataInstance;
		 * check modifier of default constructor
		 */
		try {
			implementationProvider.createMetaDataInstance().getClass().getConstructor((Class<?>[]) null);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new PrimaryDataDirectoryException(
					"Can not create a MetaData instance; change modifier of default constructor", e);
		}

		try {
			DataManager.initSecuritySystem(implementationProvider);
		} catch (SecurityException e) {
			throw new PrimaryDataDirectoryException("Can not initialize security system : " + e.getMessage(), e);
		}

		/**
		 * start HTTP service to enable external URL access to public entities
		 */
		try {
			DataManager.initHTTPService();
		} catch (EdalException e) {
			throw new PrimaryDataDirectoryException("Can not initialize the HTTP Service : " + e.getMessage(), e);
		}
		if (!isConfigurationValid) {
			initOptInProcess();
		}

		/** register graceful shutdown hook at JVM */
		Runtime.getRuntime().addShutdownHook(new ShutdownInterceptor());

		try {
			PrimaryDataDirectory root = PrimaryDataDirectory
					.getRootDirectory(implementationProvider.getConfiguration().getSupportedPrincipals());

			if (!isConfigurationValid) {
				DataManager.checkReviewStatusThread.run();
				DataManager.calculateThread.run();
				if (configuration.isCleanBrokenEntities()) {
					DataManager.cleanBrokenEntitiesThread = new CleanBrokenEntitiesThread(root);
					DataManager.cleanBrokenEntitiesThread.run();
				}
				DataManager.rebuildIndexThread.run();
			}
			isConfigurationValid = true;
			return root;

		} catch (EdalConfigurationException e) {
			throw new PrimaryDataDirectoryException(e.getCause());
		}

	}

	/**
	 * In order to know, who is working with an eDAL data structure instance. We
	 * bind in the
	 * {@link DataManager#getRootDirectory(ImplementationProvider, Subject)}
	 * function the subject as ThreadLocal object to the current thread
	 * 
	 * @return Subject the subject working in its Thread with a eDAL mount
	 */
	public static Subject getSubject() {

		return DataManager.threadlocalsubject.get();
	}

	/**
	 * 
	 * @return the cuurent {@link EdalHttpServer}
	 */
	public static EdalHttpServer getHttpServer() {

		return DataManager.server;
	}

	/**
	 * Getter all supported {@link Principal}s of the current eDAL system.
	 * 
	 * @return the list of supported {@link Principal}s
	 * @throws EdalException if unable to load {@link Principal}s.
	 */
	public static List<Class<? extends Principal>> getSupportedPrincipals() throws EdalException {
		if (DataManager.getImplProv() == null) {
			throw new EdalException("No ImplementationProvider set -> run getRootDirectory()");
		}
		try {
			return DataManager.getImplProv().getPermissionProvider().getDeclaredConstructor().newInstance()
					.getSupportedPrincipals();
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate PermissionProvider");

		}
	}

	/**
	 * Getter for the used space in the mount path of eDAL.
	 * 
	 * @return used space
	 * @throws EdalException if no path is specified.
	 */
	public static Long getUsedStorageSpace() throws EdalException {
		if (DataManager.getImplProv() == null) {
			throw new EdalException("No ImplementationProvider set -> run getRootDirectory()");
		}
		try {
			return DataManager.getImplProv().getServiceProvider().getDeclaredConstructor().newInstance()
					.getUsedStorageSpace();
		} catch (ReflectiveOperationException e) {
			throw new EdalException("Unable to initiate ServiceProvider");
		}
	}

	/**
	 * Start the HTTP-Service to make eDAL object available over a HTTP/HTTPS.
	 * 
	 * @throws EdalException if unable to initialize the HTTP server.
	 */
	private static void initHTTPService() throws EdalException {

		if (server == null) {
			server = new EdalHttpServer(getConfiguration());
			server.start();
		}
	}

	private static void initOptInProcess() throws PrimaryDataDirectoryException {
		try {
			InternetAddress newRootUser = null;
			try {
				newRootUser = DataManager.configuration.getRootUser();
			} catch (EdalConfigurationException e) {
				DataManager.getImplProv().getLogger().warn("Unable to get Root User: " + e.getMessage());
				System.exit(0);
			}

			PermissionProvider permissionProvider = null;
			try {
				permissionProvider = DataManager.getImplProv().getPermissionProvider().getDeclaredConstructor()
						.newInstance();
			} catch (ReflectiveOperationException e) {
				DataManager.getImplProv().getLogger().warn("Unable to initiate PermissionProvider: " + e.getMessage());
				System.exit(0);
			}

			InternetAddress previousRootUser = permissionProvider.getCurrentRootUser();

			/** already ROOT CHEAT used to avoid the double-log-in process */
			if (newRootUser.equals(previousRootUser)) {

				DataManager.getImplProv().getLogger().info("ALREADY ROOT CHEAT USED");

				configuration.setErrorEmailAddress(permissionProvider.getCurrentRootUser());
			}

			/** new user uses ROOT CHEAT */
			else if (newRootUser.equals(rootCheat)) {
				UUID uuid = UUID.randomUUID();
				DataManager.getImplProv().getLogger().info("ROOT CHEAT");
				try {
					permissionProvider.storeRootUser(DataManager.getSubject(), new InternetAddress(ROOT_CHEAT), uuid);
					permissionProvider.validateRootUser(new InternetAddress(ROOT_CHEAT), uuid);

					configuration.setErrorEmailAddress(permissionProvider.getCurrentRootUser());

				} catch (AddressException e) {
					DataManager.getImplProv().getLogger().warn("unable to use ROOT CHEAT:" + e.getMessage());
					System.exit(0);
				}
			}

			/** new root user without ROOT CHEAT */
			else {

				if (previousRootUser == null) {
					DataManager.getImplProv().getLogger().info("no root user defined ! ");

					UUID uuid = UUID.randomUUID();

					permissionProvider.storeRootUser(DataManager.getSubject(), newRootUser, uuid);

					VeloCityHtmlGenerator veloCityHtmlGenerator = EdalHttpHandler.velocityHtmlGenerator;

					configuration.setErrorEmailAddress(newRootUser);

					/** send confirmation email */
					sendEmail(veloCityHtmlGenerator.generateEmailForDoubleOptIn(newRootUser, uuid).toString(),
							"[eDAL-Service]: Double-Opt-In", newRootUser.getAddress());

					Long startTime = System.currentTimeMillis();
					DataManager.getImplProv().getLogger()
							.info("Waiting for confirmation of root user '" + newRootUser.getAddress() + "'...");
					while (!permissionProvider.isRootValidated(newRootUser)) {
						DataManager.getImplProv().getLogger()
								.debug("Waiting for confirmation of root user '" + newRootUser.getAddress() + "'...");

						try {
							Thread.sleep(OPT_IN_INTERVAL);
						} catch (InterruptedException e) {
							throw new PrimaryDataDirectoryException("error while waiting for user opt-in", e);
						}
						if (System.currentTimeMillis() - startTime > OPT_IN_TIMEOUT) {
							DataManager.getImplProv().getLogger().error("Timeout for root user confirmation");
							System.exit(0);
						}
					}
				} else {

					if (!newRootUser.equals(previousRootUser)) {

						UUID uuid = UUID.randomUUID();

						permissionProvider.storeRootUser(DataManager.getSubject(), newRootUser, uuid);

						VeloCityHtmlGenerator veloCityHtmlGenerator = EdalHttpHandler.velocityHtmlGenerator;

						configuration.setErrorEmailAddress(newRootUser);

						/** send confirmation email */
						sendEmail(veloCityHtmlGenerator.generateEmailForDoubleOptIn(newRootUser, uuid).toString(),
								"[eDAL-Service]: Double-Opt-In", newRootUser.getAddress());

						Long startTime = System.currentTimeMillis();
						DataManager.getImplProv().getLogger()
								.info("Waiting for confirmation of root user '" + newRootUser.getAddress() + "'...");
						while (!permissionProvider.isRootValidated(newRootUser)) {
							DataManager.getImplProv().getLogger().debug(
									"Waiting for confirmation of root user '" + newRootUser.getAddress() + "'...");
							try {
								Thread.sleep(OPT_IN_INTERVAL);
							} catch (InterruptedException e) {
								throw new PrimaryDataDirectoryException("error while waiting for user opt-in", e);

							}
							if (System.currentTimeMillis() - startTime > OPT_IN_TIMEOUT) {
								DataManager.getImplProv().getLogger().error("Timeout for root user confirmation");
								System.exit(0);
							}
						}
						/** send email to the old root user; */
						if (!previousRootUser.getAddress().equals(ROOT_CHEAT)) {
							sendEmail(
									veloCityHtmlGenerator.generateEmailForChangedRootUser(newRootUser, previousRootUser)
											.toString(),
									"[eDAL-Server]: notice - root user transfered to " + newRootUser.getAddress(),
									previousRootUser.getAddress());
						}

					} else {
						DataManager.getImplProv().getLogger().info("Root user already registered !");
						configuration.setErrorEmailAddress(previousRootUser);
					}

				}
			}
		} catch (EdalException e) {
			throw new PrimaryDataDirectoryException("unable to load root user, please check database", e);
		}
	}

	/**
	 * Initialize the security system of eDAL.
	 * 
	 * @param implementationProvider an {@link ImplementationProvider} that provide
	 *                               all implementation classes.
	 * @throws SecurityException if unable to find policy file or unable to create a
	 *                           new
	 *                           {@link de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider}
	 *                           instance.
	 */
	private static void initSecuritySystem(final ImplementationProvider implementationProvider)
			throws SecurityException {

		if (System.getProperty("java.security.policy") == null) {

			try {
				final String policy = DataManager.class.getResource("policy.txt").toString();
				System.setProperty("java.security.policy", policy);
			} catch (final Exception e) {
				throw new SecurityException("unable to find policy file", e);
			}

			/** start SecurityManager */
			System.setSecurityManager(new SecurityManager());

			EdalPolicy edalPolicy = null;
			try {
				edalPolicy = new EdalPolicy(
						implementationProvider.getPermissionProvider().getDeclaredConstructor().newInstance());
			} catch (final Exception e) {
				throw new SecurityException("unable to create new PermissionProvider", e);
			}
			final List<Policy> policies = new ArrayList<Policy>(2);

			policies.add(edalPolicy);
			policies.add(Policy.getPolicy());

			Policy.setPolicy(new EdalCompositePolicy(policies));
		}

	}

	/**
	 * Reload all initial default permission in
	 * {@link DataManager#DEFAULT_PERMISSIONS}.
	 */
	public static void resetDefaultPermissions() {
		DataManager.setDefaultPermissions(DataManager.DEFAULT_PERMISSIONS);
	}

	/**
	 * Function to send an eMail to the given recipient.
	 * 
	 * @param message      the message to send
	 * @param subject      the subject of the eMail
	 * @param emailAddress the eMail address of the recipient.
	 */
	public static void sendEmail(final String message, final String subject, final String emailAddress) {

		final Properties props = new Properties();
		props.put("mail.smtp.host", DataManager.getConfiguration().getMailSmtpHost());

		final javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);

		final Message mail = new MimeMessage(session);

		try {
			InternetAddress addressFrom = new InternetAddress(DataManager.getConfiguration().getEdalEmailAddress(),
					"eDAL-Service <" + DataManager.getConfiguration().getEdalEmailAddress() + ">");

			mail.setFrom(addressFrom);
			final InternetAddress addressTo = new InternetAddress(emailAddress);
			mail.setRecipient(Message.RecipientType.TO, addressTo);
			mail.setSubject(subject);
			mail.setContent(message, "text/html; charset=utf-8");
			Transport.send(mail);

		} catch (MessagingException | UnsupportedEncodingException e) {
			DataManager.getConfiguration().getErrorLogger().fatal(emailAddress + " : " + e.getMessage());
		}
	}

	/**
	 * Function to send an eMail with attachment to the given recipient.
	 * 
	 * @param message      the message to send
	 * @param subject      the subject of the eMail
	 * @param emailAddress the eMail address of the recipient.
	 * @param attachment   the attached {@link File}
	 */
	public static void sendEmail(final String message, final String subject, final String emailAddress,
			final URL attachment) {

		final Properties props = new Properties();
		props.put("mail.smtp.host", DataManager.getConfiguration().getMailSmtpHost());

		final javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);

		final Message mail = new MimeMessage(session);

		try {
			InternetAddress addressFrom = new InternetAddress(DataManager.getConfiguration().getEdalEmailAddress(),
					"eDAL-Service <" + DataManager.getConfiguration().getEdalEmailAddress() + ">");

			mail.setFrom(addressFrom);
			final InternetAddress addressTo = new InternetAddress(emailAddress);
			mail.setRecipient(Message.RecipientType.TO, addressTo);
			mail.setSubject(subject);

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html; charset=utf-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			BodyPart attachBodyPart = new MimeBodyPart();
			DataSource source = new URLDataSource(attachment);
			attachBodyPart.setDataHandler(new DataHandler(source));
			attachBodyPart.setFileName(source.getName());
			multipart.addBodyPart(attachBodyPart);
			mail.setContent(multipart);

			Transport.send(mail);

		} catch (MessagingException | UnsupportedEncodingException e) {
			DataManager.getConfiguration().getErrorLogger().fatal(emailAddress + " : " + e.getMessage());
		}
	}

	/**
	 * Overrides the current default permissions of the current user with the new
	 * permissions.
	 * 
	 * @param newUserPermissions the user permissions to set to the default
	 *                           permissions.
	 */
	public static void setDefaultPermissions(final Map<Principal, List<Methods>> newUserPermissions) {
		DataManager.threadlocaldefaultpermissions.set(newUserPermissions);
	}

	/**
	 * Setter for the current {@link Subject}.
	 * 
	 * @param subject a {@link Subject} object.
	 */
	public static void setSubject(final Subject subject) {
		DataManager.threadlocalsubject.set(subject);
	}

	/**
	 * Convenience function to shutdown the eDAL system.
	 */
	public static void shutdown() {
		long start = System.currentTimeMillis();
		implementationprovider.getLogger().info("Trying to shutdown eDAL instance...");
		if (!DataManager.checkReviewStatusThread.getState().equals(Thread.State.TERMINATED)) {
			implementationprovider.getLogger().info("Trying to shutdown checkReviewStatusThread");
			DataManager.checkReviewStatusThread.done();
		}
		long finish = System.currentTimeMillis();
		implementationprovider.getLogger().info("\nTime: " + (finish - start));
		start = System.currentTimeMillis();
		if (!DataManager.calculateThread.getState().equals(Thread.State.TERMINATED)) {
			implementationprovider.getLogger().info("Trying to shutdown calculateThread");
			DataManager.calculateThread.done();
		}
		finish = System.currentTimeMillis();
		implementationprovider.getLogger().info("\nTime: " + (finish - start));
		implementationprovider.getLogger().info("Trying to shutdown ImplProvider");
		start = System.currentTimeMillis();
		DataManager.getImplProv().shutdown();
		finish = System.currentTimeMillis();
		implementationprovider.getLogger().info("\nTime: " + (finish - start));
		if (DataManager.server != null) {
			DataManager.server.stop();
			DataManager.server = null;
		}
		DataManager.isConfigurationValid = false;
		implementationprovider.getLogger().info("Trying to shutdown Serviceses:jetty");
		DataManager.getJettyExecutorService().shutdown();
		implementationprovider.getLogger().info("Trying to shutdown Serviceses:ListExecutor");
		DataManager.getListExecutorService().shutdown();
		implementationprovider.getLogger().info("Trying to shutdown Serviceses:Velocity");
		DataManager.getVelocityExecutorService().shutdown();

		DataManager.stopLatch.countDown();

		System.setSecurityManager(null);
		System.clearProperty("java.security.manager");
		System.clearProperty("java.security.policy");

		implementationprovider.getLogger().info("eDAL instance successfully closed !");

		DataManager.alreadyClosed = true;

		waitForConnectionClose();
		waitForSessionFactoryClose();
		// checkLastIds();
		// checkDb();
	}

	public static void waitForShutDown() {

		try {
			DataManager.stopLatch.await();
		} catch (InterruptedException e) {
			implementationprovider.getLogger().error("error while count down stopLatch:" + e.getMessage(), e);
		}

	}

	/**
	 * Getter for the {@link ThreadPool} for {@link EdalHttpServer}.
	 * 
	 * @return ExecutorService
	 */
	public static ExecutorService getJettyExecutorService() {

		if (jettyExecutorService.isShutdown() || jettyExecutorService.isTerminated()) {
			jettyExecutorService = Executors.newCachedThreadPool();
		}

		return jettyExecutorService;
	}

	/**
	 * Getter for the {@link ThreadPool} for {@link EdalHttpServer}.
	 * 
	 * @return ExecutorService
	 */
	public static ExecutorService getVelocityExecutorService() {

		if (velocityExecutorService.isShutdown() || velocityExecutorService.isTerminated()) {
			velocityExecutorService = Executors.newCachedThreadPool();
		}

		return velocityExecutorService;
	}

	/**
	 * Getter for the {@link ThreadPool} for {@link ListThread}
	 * 
	 * @return ExecutorService
	 */
	public static ExecutorService getListExecutorService() {

		if (listExecutorService.isShutdown() || listExecutorService.isTerminated()) {
			listExecutorService = Executors.newCachedThreadPool();
		}

		return listExecutorService;
	}

	/**
	 * Send an {@link OutputStream} containing a short {@link String} to test server
	 * connectivity
	 * 
	 * @param outputStream the {@link OutputStream} to fill in the {@link String}
	 * @throws IOException if unable to send
	 */
	public static void receiveTestData(OutputStream outputStream) throws IOException {
		outputStream.write(TEST_DATA_STRING.getBytes());
		outputStream.flush();
	}

	public static void waitForConnectionClose() {
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider) getImplProv());
		try {
			while (!((FileSystemImplementationProvider) getImplProv()).getConnection().isClosed()) {
				implProvi.getLogger().info("DB STILL OPEN!");
				Thread.sleep(1000);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void waitForSessionFactoryClose() {
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider) getImplProv());
		while (((FileSystemImplementationProvider) getImplProv()).getSessionFactory().isOpen()) {
			try {
				implProvi.getLogger().info("SESSIONFACTORY STILL OPEN");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void checkDb() {
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider) getImplProv());
		Path path = implProvi.getConfiguration().getMountPath();
		try {
			if (Files.exists(Paths.get(path.toString(), "edaldb.mv.db")))
				Files.delete(Paths.get(path.toString(), "edaldb.mv.db"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (Files.exists(Paths.get(path.toString(), "edaldb.trace.db")))
				Files.delete(Paths.get(path.toString(), "edaldb.trace.db"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static public HashSet<Integer> searchByKeyword(String keyword, boolean fuzzy, String entityType) {
		final long startTime = System.currentTimeMillis();
		IndexReader reader = null;
		try {
			Directory indexDirectory = FSDirectory.open(Paths.get(
					((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
					"Master_Index"));
			reader = DirectoryReader.open(indexDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BooleanQuery.setMaxClauseCount(10000);
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		Analyzer standardAnalyzer = new StandardAnalyzer(stopSet);
		String[] fields = { MetaDataImplementation.TITLE, MetaDataImplementation.DESCRIPTION,
				MetaDataImplementation.COVERAGE, MetaDataImplementation.IDENTIFIER, MetaDataImplementation.SIZE,
				MetaDataImplementation.TYPE, MetaDataImplementation.LANGUAGE, MetaDataImplementation.PERSON,
				MetaDataImplementation.LEGALPERSON, MetaDataImplementation.ALGORITHM, MetaDataImplementation.CHECKSUM,
				MetaDataImplementation.SUBJECT, MetaDataImplementation.RELATION, MetaDataImplementation.MIMETYPE,
				MetaDataImplementation.STARTDATE, MetaDataImplementation.ENDDATE, MetaDataImplementation.RELATIONTYPE,
				MetaDataImplementation.RELATEDIDENTIFIERTYPE };
		org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,
				standardAnalyzer);
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		org.apache.lucene.search.Query luceneQuery = null;
		if (fuzzy) {
			keyword += "~";
		}
		try {
			luceneQuery = parser.parse(keyword);
		} catch (ParseException e2) {
			((FileSystemImplementationProvider) getImplProv()).getLogger().debug("Was not able to Parse: \n" + keyword);
			return new HashSet<>();
		}
		if (luceneQuery == null) {
			return new HashSet<>();
		}

		/** A possible way to search for Files AND rootDirectories */
//		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
//		BooleanQuery.Builder subbooleanQuery = new BooleanQuery.Builder();
//		QueryParser queryType = new QueryParser(MetaDataImplementation.CHECKSUM,standardAnalyzer);
//		queryType.setDefaultOperator(QueryParser.OR_OPERATOR);
//		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
//		try {
//			subbooleanQuery.add(queryType.parse(new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, entityType)).toString()), Occur.SHOULD);
//			subbooleanQuery.add(queryType.parse(new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.INDIVIDUALFILE)).toString()), Occur.SHOULD);
//			booleanQuery.add(queryType.parse(subbooleanQuery.build().toString()),Occur.MUST);
//		} catch (ParseException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}		
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser queryType = new QueryParser(MetaDataImplementation.CHECKSUM, standardAnalyzer);
		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
		booleanQuery.add(new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, entityType)), Occur.FILTER);
		try {
			IndexSearcher searcher = searchManager.acquire();
			ScoreDoc[] hits2 = searcher.search(booleanQuery.build(), 50000).scoreDocs;
			HashSet<Integer> ids = new HashSet<>();
			for (int i = 0; i < hits2.length; i++) {
				Document doc = null;
				try {
					doc = searcher.doc(hits2[i].doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ids.add(Integer.parseInt((doc.get(PublicVersionIndexWriterThread.PUBLICID))));
			}
			return ids;
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return new HashSet<Integer>();
	}

	@SuppressWarnings("unchecked")
	static public JSONObject advancedSearch(JSONObject requestObject) {
		JSONObject result = new JSONObject();
		Query buildedQuery = buildQueryFromJSON(requestObject, result);
		CountDownLatch internalCountDownLatch = new CountDownLatch(1);
		class DrillDownThread extends Thread {
			JSONArray facets = new JSONArray();
			public void run(){
				try {
					facets = DataManager.drillDown(buildedQuery.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				internalCountDownLatch.countDown();
			}
			public JSONArray getFacets(){
				return facets;
			}
		}
		DrillDownThread innerThread = new DrillDownThread();
		innerThread.start();
		IndexSearcher searcher = null;
		try {
			searcher = searchManager.acquire();		
		} catch (IOException e2) {
			e2.printStackTrace();
			return result;
		}
		DataManager.getImplProv().getLogger().debug(buildedQuery.toString());
		TopDocs topDocs = null;
		int currentPageNumber = ((int) (long) requestObject.get("displayedPage"));
		int pageArraySize = ((int) (long) requestObject.get("pageArraySize"));
		int pageIndex = ((int) (long) requestObject.get("pageIndex"));
		int pageSize = ((int) (long) requestObject.get("pageSize"));
		try {
			if (pageArraySize == 0 || currentPageNumber == 1) {
				DataManager.getImplProv().getLogger().debug("Builded QUery advanced search: " + buildedQuery.toString());
				topDocs = searcher.search(buildedQuery, pageSize*3);
			} else {
				ScoreDoc bottomScoreDoc = null;
				bottomScoreDoc = searcher.search(new TermQuery(
						new Term(PublicVersionIndexWriterThread.DOCID, (String) requestObject.get("bottomResultId"))),
						5).scoreDocs[0];
				bottomScoreDoc.score = ((float) (double) requestObject.get("bottomResultScore"));
				topDocs = searcher.searchAfter(bottomScoreDoc, buildedQuery, pageSize*3);
			}
			if(requestObject.get("hitSize") == null) {
				DataManager.getImplProv().getLogger().info("Calculatin number of hits");
				TotalHitCountCollector collector = new TotalHitCountCollector();
				searcher.search(buildedQuery, collector);
				result.put("hitSize", collector.getTotalHits());
			}else {
				DataManager.getImplProv().getLogger().info("NOT calculating number of hits, already there");
				result.put("hitSize", requestObject.get("hitSize"));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put("hitSizeDescription","");
		result.put("displayedPage", (long) requestObject.get("displayedPage"));
		result.put("pageIndex", pageIndex);
		//DataManager.getImplProv().getLogger().info("Finished Query with: "+topDocs.totalHits.value);
		String whereToSearch = (String) requestObject.get("whereToSearch");
		Highlighter highlighter = null;
		Analyzer analyzer = null;
		if(whereToSearch != null && whereToSearch.equals(PublicVersionIndexWriterThread.CONTENT) && buildedQuery.toString().contains(PublicVersionIndexWriterThread.CONTENT)) {
			//prepare highlighting
			analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter()
					.getAnalyzer();
			highlighter = new Highlighter(new QueryScorer(buildedQuery));
			highlighter.setTextFragmenter(new SimpleFragmenter(48));
		}

		ScoreDoc[] scoreDocs = topDocs.scoreDocs;	
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		JSONArray finalArray = new JSONArray();
		if (scoreDocs.length == 0) {
			result.put("hitSize", scoreDocs.length);
			return result;
		}
		if (scoreDocs.length < pageSize) {
			result.put("hitSize", scoreDocs.length);
			pageSize = ((int) (long) scoreDocs.length);
		}
		Document doc = null;
		Set<String> fields = Set.of(PublicVersionIndexWriterThread.METADATAFIELDS);
		for (int i = 0; i < pageSize; i++) {
			try {
				doc = searcher.doc(scoreDocs[i].doc,fields);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject obj = new JSONObject();
			String type = doc.get(MetaDataImplementation.ENTITYTYPE);
			PublicReferenceImplementation reference = session.get(PublicReferenceImplementation.class,
					Integer.parseInt((doc.get(PublicVersionIndexWriterThread.PUBLICID))));
			if(reference.getAcceptedDate() == null) {
				//if in Testmode, use creation year
				obj.put("year", doc.get(MetaDataImplementation.STARTDATE));
			}else {
				obj.put("year", reference.getAcceptedDate().get(Calendar.YEAR));
			}

			if (type.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
				try {
					obj.put("doi", reference.getAssignedID());
				} catch (PublicReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				obj.put("title", reference.getVersion().getMetaData().toString());
				obj.put("fileName", "");
				obj.put("ext", "");
				obj.put("type", "record");
			} else {
				try {
					obj.put("doi", reference.getAssignedID());
				} catch (PublicReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String ext = doc.get(MetaDataImplementation.FILETYPE);
				String link = doc.get(PublicVersionIndexWriterThread.INTERNALID) + "/"
						+ doc.get(MetaDataImplementation.PRIMARYENTITYID) + "/"
						+ doc.get(PublicVersionIndexWriterThread.REVISION);
				PrimaryDataFileImplementation file = session.get(PrimaryDataFileImplementation.class,
						doc.get(MetaDataImplementation.PRIMARYENTITYID));
				obj.put("link", link);
				obj.put("fileName", file.toString());
				obj.put("docId",doc.get(PublicVersionIndexWriterThread.DOCID));
				obj.put("size", doc.get(MetaDataImplementation.SIZE));
				obj.put("title", reference.getVersion().getMetaData().toString());
				
				if(highlighter != null && analyzer != null) {
					try {
						String highlight = highlighter.getBestFragment(analyzer, PublicVersionIndexWriterThread.CONTENT,
								doc.get(PublicVersionIndexWriterThread.CONTENT));
						if(highlight != null) {
							obj.put("highlight", highlight);
						}else {
							obj.put("highlight", doc.get(PublicVersionIndexWriterThread.CONTENT).substring(0, 100));
						}
					} catch (IOException | InvalidTokenOffsetsException e) {
					}
				}
				
				if (type.equals(PublicVersionIndexWriterThread.FILE)) {
					obj.put("type", "File");
					obj.put("ext", ext);
				} else if (type.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
					obj.put("type", "Directory");
					obj.put("ext", "");
				}
			}
			finalArray.add(obj);
			//DataManager.getImplProv().getLogger().info("Loaded doc Nr."+i+" title: "+doc.get(MetaDataImplementation.TITLE));
		}
		result.put("results", finalArray);
		// bottomResult.docids needs to be stored, to support paginated Searching
		
		//Pagination
		int additionalPages;
		JSONArray pageArray = new JSONArray();
		JSONObject page = new JSONObject();
		Set<String> set = Set.of(new String[]{PublicVersionIndexWriterThread.DOCID});
		if (pageArraySize == 0) {
			page.put("bottomResult", null);
			page.put("bottomResultScore", 0);
			page.put("page", 1);
			page.put("index", 0);
			pageArray.add(page);
			// if pageSize equals the result size, only one page should be stored
			if (pageSize != scoreDocs.length) {
				additionalPages = 3;
				for (int i = 1; i < additionalPages; i++) {
					page = new JSONObject();
					int index = i * pageSize - 1;
					if (index < scoreDocs.length-1) {
						try {
							page.put("bottomResult",
									searcher.doc(scoreDocs[index].doc, set).get(PublicVersionIndexWriterThread.DOCID));
							page.put("bottomResultScore", scoreDocs[index].score);
							page.put("page", i + 1);
							page.put("index", i);
							pageArray.add(page);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						break;
					}
					//DataManager.getImplProv().getLogger().info("Loaded doc Nr."+index+" title: "+doc.get(MetaDataImplementation.TITLE));
				}
			}
		} else {
			// check if there needs to be loaded more additional Sites or only 1 (the
			// current selected Site)
			if (currentPageNumber + 2 > pageArraySize && scoreDocs.length > pageSize) {
				int offset = pageArraySize - currentPageNumber + 1;
				additionalPages = 3 - offset;
				for (int i = 0; i < additionalPages; i++) {
					page = new JSONObject();
					int index = (i + offset) * pageSize - 1;
					if (index < scoreDocs.length) {
						try {
							page.put("bottomResult",
									searcher.doc(scoreDocs[index].doc, set).get(PublicVersionIndexWriterThread.DOCID));
							page.put("bottomResultScore", scoreDocs[index].score);
							page.put("page", i + 1 + pageArraySize);
							page.put("index", i + pageArraySize);
							pageArray.add(page);
							//DataManager.getImplProv().getLogger().info("Loaded doc Nr."+index+" title: "+doc.get(MetaDataImplementation.TITLE));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
			}
		}
		result.put("pageArray", pageArray);
		try {
			result.put("bottomResult",
					searcher.doc(scoreDocs[pageSize - 1].doc).get(PublicVersionIndexWriterThread.DOCID));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(searcher != null) {
					searchManager.release(searcher);
					DataManager.getImplProv().getLogger().debug("Released searcher");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result.put("bottomResultScore", scoreDocs[pageSize - 1].score);
		try {
			internalCountDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		result.put("facets", innerThread.getFacets());
		return result;
	}

	public static Query parseToLuceneQuery(JSONObject jsonArray) {
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
		QueryParser pars = new QueryParser(MetaDataImplementation.ALL, analyzer);
		pars.setDefaultOperator(Operator.AND);
		String type = ((String) jsonArray.get("type"));
		String keyword = (String) jsonArray.get("searchterm");
		QueryParser queryParser = new QueryParser(type, analyzer);
		queryParser.setDefaultOperator(Operator.AND);
		try {
			if (type.equals("humanQuery")) {
				if (keyword.charAt(0) != '+' && keyword.charAt(0) != '-') {
					keyword = '+' + keyword;
				}
				return pars.parse(QueryParser.escape((String) jsonArray.get("searchterm")));
			}
			if ((boolean) jsonArray.get("fuzzy")) {
				keyword = keyword + '~';
			}
			keyword = Occur.valueOf((String) jsonArray.get("occur")) + keyword;
			return queryParser.parse(keyword);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONArray builQueryAndDrillDown(JSONObject json) throws ParseException, IOException {
		QueryParser pars = new QueryParser(MetaDataImplementation.ALL, ((FileSystemImplementationProvider)DataManager.getImplProv()).getWriter().getAnalyzer());
		pars.setDefaultOperator(Operator.AND);
		StringJoiner queryJoiner = new StringJoiner(" ");
		JSONArray filters = (JSONArray) json.get("filters");
		Query luceneQuery = null;
		for (Object obj : filters) {
			JSONObject queryData = (JSONObject) obj;
			String type = ((String) queryData.get("type"));
			String keyword = (String) queryData.get("searchterm");
			if (type.equals(MetaDataImplementation.STARTDATE) || type.equals(MetaDataImplementation.ENDDATE)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
				String lower = DateTools.timeToString(
						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				String upper = DateTools.timeToString(
						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				luceneQuery = TermRangeQuery.newStringRange(MetaDataImplementation.STARTDATE, lower, upper, false, false);
			} else if (type.equals(MetaDataImplementation.SIZE)) {
				luceneQuery = TermRangeQuery.newStringRange(MetaDataImplementation.SIZE,
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						false, false);
			} else if (type.equals(MetaDataImplementation.FILETYPE)) {
				keyword.replace("\\", "");
				String fileTypeQuery = MetaDataImplementation.FILETYPE+":"+keyword;
				try {
					luceneQuery = pars.parse(QueryParser.escape(fileTypeQuery));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(luceneQuery != null) {
				DataManager.getImplProv().getLogger().debug(luceneQuery.toString());
				queryJoiner.add(Occur.MUST.toString() + luceneQuery.toString());
			}
		}
		String hitType = (String) json.get("hitType");
		if (hitType.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
			luceneQuery = new TermQuery(
					new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.PUBLICREFERENCE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.INDIVIDUALFILE)) {
			luceneQuery = new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.FILE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
			luceneQuery = new TermQuery(
					new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.DIRECTORY));
		} else {
			return null;
		}
		queryJoiner.add(luceneQuery.toString());
		return drillDown(queryJoiner.toString());
	}
	
	public static JSONArray drillDown(String query) throws ParseException, IOException {
		QueryParser queryParser = new QueryParser(MetaDataImplementation.ALL, 
				((FileSystemImplementationProvider)DataManager.getImplProv()).getWriter().getAnalyzer());
		queryParser.setDefaultOperator(Operator.AND);
		IndexSearcher searcher = searchManager.acquire();
		try {
			if(taxoReader != null) {
				TaxonomyReader newReader = TaxonomyReader.openIfChanged(taxoReader);
				if(newReader != null) {
					taxoReader = newReader;
				}
			}else {
				taxoReader = new DirectoryTaxonomyReader(((FileSystemImplementationProvider)DataManager.getImplProv()).getFacetDirectory());
			}
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("Error when creating DirectoryTaxonomyReader.. "+e.getMessage());
			searchManager.release(searcher);
			if(taxoReader != null) {
				taxoReader.close();	
			}
			return new JSONArray();
		}
		
		DrillDownQuery drillQuery = new DrillDownQuery(config, queryParser.parse(query));
	    FacetsCollector fc = new FacetsCollector();
	    FacetsCollector.search(searcher,drillQuery, 50000, fc);

	    List<FacetResult> results = new ArrayList<>();

	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
	    getSearchManager().release(searcher);
	    try {
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.CREATORNAME));
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.CONTRIBUTORNAME));
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.SUBJECT));
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.TITLE));
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.DESCRIPTION));
		    results.add(facets.getTopChildren(5000, MetaDataImplementation.FILETYPE));
	    }catch(IOException e) {
	    	return null;
	    }
		JSONArray result = new JSONArray();
	    for(FacetResult facet : results) {
	    	if(facet == null)
	    		continue;
	    	JSONObject jsonFacet = new JSONObject();
	    	if(facet.dim.equals(MetaDataImplementation.CREATORNAME))
	    		jsonFacet.put("category", MetaDataImplementation.PERSON);
	    	else if(facet.dim.equals(MetaDataImplementation.CONTRIBUTORNAME))
	    		jsonFacet.put("category", MetaDataImplementation.CONTRIBUTOR);
	    	else
	    		jsonFacet.put("category",facet.dim);
	    	jsonFacet.put("sortedByHits",facet.labelValues);
	    	result.add(jsonFacet);
	    }
	    return result;
	}

	public static Query buildQueryFromJSON(JSONObject jsonArray, JSONObject result) {
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		String whereToSearch = (String) jsonArray.get("whereToSearch");
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
		QueryParser pars = whereToSearch.equals(PublicVersionIndexWriterThread.CONTENT) ? 
				new QueryParser(PublicVersionIndexWriterThread.CONTENT, analyzer) : new QueryParser(MetaDataImplementation.ALL, analyzer);
		pars.setDefaultOperator(Operator.AND);
		String existing = (String) jsonArray.get("existingQuery");
		StringJoiner queryJoiner = new StringJoiner(" ");
		if (!existing.equals(""))
			try {
				Query parsedQuery = pars.parse(existing);
				finalQuery.add(parsedQuery, Occur.MUST);
				String queryString = parsedQuery.toString();
				if (queryString.charAt(0) != '+' && queryString.charAt(0) != '-') {
					queryString = '+' + queryString;
				}
				result.put("parsedQuery", queryString.toString());
				queryJoiner.add(queryString);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		JSONArray queries = (JSONArray) jsonArray.get("queries");
		if (queries != null) {
			for (Object query : queries) {
				if (query instanceof String)
					queryJoiner.add((String) query);
			}
		}
		JSONArray filters = (JSONArray) jsonArray.get("filters");
		Query query = null;
		for (Object obj : filters) {
			JSONObject queryData = (JSONObject) obj;
			String type = ((String) queryData.get("type"));
			String keyword = (String) queryData.get("searchterm");
			if (type.equals(MetaDataImplementation.STARTDATE) || type.equals(MetaDataImplementation.ENDDATE)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
				String lower = DateTools.timeToString(
						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				String upper = DateTools.timeToString(
						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				query = TermRangeQuery.newStringRange(MetaDataImplementation.STARTDATE, lower, upper, false, false);
			} else if (type.equals(MetaDataImplementation.SIZE)) {
				query = TermRangeQuery.newStringRange(MetaDataImplementation.SIZE,
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						false, false);
			} else if (type.equals(MetaDataImplementation.FILETYPE)) {
				QueryParser queryParser = new QueryParser(type, analyzer);
				queryParser.setDefaultOperator(Operator.AND);
				keyword.replace("\\", "");
				try {
					query = queryParser.parse(QueryParser.escape(keyword));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			DataManager.getImplProv().getLogger().debug(query.toString());
			queryJoiner.add(Occur.MUST.toString() + query.toString());
			finalQuery.add(query, Occur.MUST);
		}

		String hitType = (String) jsonArray.get("hitType");
		if (hitType.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
			query = new TermQuery(
					new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.PUBLICREFERENCE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.INDIVIDUALFILE)) {
			query = new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.FILE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
			query = new TermQuery(
					new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.DIRECTORY));
		} else {
			return null;
		}
		finalQuery.add(query, Occur.MUST);
		queryJoiner.add(query.toString());
		DataManager.getImplProv().getLogger().debug("Builded queryjoiner_ " + queryJoiner.toString());
		BooleanQuery.setMaxClauseCount(10000);
		try {
			return pars.parse(queryJoiner.toString());
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Parsing Error: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Method to get the top 10 highlighted passages for a Document 
	 * @param doc The Document to be highlighted
	 * @param q The Query that should contain a keyword for highlighting
	 * @return The Highlighted passages wrapped in a JSONObject
	 * @throws IOException
	 * @throws ParseException
	 */
	public static JSONObject getHighlightedSections(String doc, String q) throws IOException, ParseException {
		IndexSearcher searcher = DataManager.getSearchManager().acquire();
		Query query = new TermQuery(new Term(PublicVersionIndexWriterThread.DOCID, doc));
		ScoreDoc[] hits = searcher.search(query, 1).scoreDocs;
		JSONObject result = new JSONObject();
		if(hits.length > 0) {
			Analyzer analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter()
					.getAnalyzer();
			QueryParser parser = new QueryParser(PublicVersionIndexWriterThread.CONTENT, analyzer);
			Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<span style='color:#0275d8; font-weight:bold;'>", "</span>"), new QueryScorer(parser.parse(q)));
			highlighter.setTextFragmenter(new SimpleFragmenter(300));//48
			Document document = searcher.doc(hits[0].doc);
			TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), hits[0].doc, PublicVersionIndexWriterThread.CONTENT,
			analyzer);
			TextFragment[] fragments;
			try {
				fragments = highlighter.getBestTextFragments(tokenStream, document.get(PublicVersionIndexWriterThread.CONTENT), false, 10);
				List<String> snipets = new ArrayList<>();
				for(TextFragment fragment : fragments) {
					if(fragment.getScore() > 0.0) {
						snipets.add(fragment.toString());
					}
				}
				result.put(PublicVersionIndexWriterThread.CONTENT, snipets);
			} catch (IOException | InvalidTokenOffsetsException e) {
				result.put("msg", "There was an Error, Document not found.");
			}

		}else {
			result.put("msg", "There was an Error, Document not found.");
		}
		return result;
	}

}
