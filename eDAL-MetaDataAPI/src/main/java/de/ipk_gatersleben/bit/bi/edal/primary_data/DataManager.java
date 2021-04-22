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

import java.io.File;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.hibernate.Session;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ALLPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CalculateDirectorySizeThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CleanBrokenEntitiesThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ListThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.NativeLuceneIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
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
			return DataManager.getImplProv().getHttpServiceProvider().getDeclaredConstructor().newInstance().getPrimaryDataEntityByID(uuid,
					versionNumber);
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
			return DataManager.getImplProv().getPermissionProvider().getDeclaredConstructor().newInstance().getSupportedPrincipals();
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
			return DataManager.getImplProv().getServiceProvider().getDeclaredConstructor().newInstance().getUsedStorageSpace();
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
				permissionProvider = DataManager.getImplProv().getPermissionProvider().getDeclaredConstructor().newInstance();
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
				edalPolicy = new EdalPolicy(implementationProvider.getPermissionProvider().getDeclaredConstructor().newInstance());
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
		implementationprovider.getLogger().info("\nTime: "+(finish-start));
    	start = System.currentTimeMillis();
		if (!DataManager.calculateThread.getState().equals(Thread.State.TERMINATED)) {
			implementationprovider.getLogger().info("Trying to shutdown calculateThread");
			DataManager.calculateThread.done();
		}
    	finish = System.currentTimeMillis();
		implementationprovider.getLogger().info("\nTime: "+(finish-start));
		implementationprovider.getLogger().info("Trying to shutdown ImplProvider");
    	start = System.currentTimeMillis();
		DataManager.getImplProv().shutdown();
    	finish = System.currentTimeMillis();
		implementationprovider.getLogger().info("\nTime: "+(finish-start));
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
		//checkLastIds();
		//checkDb();
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
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider)getImplProv());
		try {
			while(!((FileSystemImplementationProvider)getImplProv()).getConnection().isClosed()) {	
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
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider)getImplProv());
		while(((FileSystemImplementationProvider)getImplProv()).getSessionFactory().isOpen()) {
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
		FileSystemImplementationProvider implProvi = ((FileSystemImplementationProvider)getImplProv());
		Path path = implProvi.getConfiguration().getMountPath();
		try {
			if(Files.exists(Paths.get(path.toString(),"edaldb.mv.db")))
				Files.delete(Paths.get(path.toString(),"edaldb.mv.db"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(Files.exists(Paths.get(path.toString(),"edaldb.trace.db")))
				Files.delete(Paths.get(path.toString(),"edaldb.trace.db"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	static public ArrayList<Integer> searchByKeyword(String keyword, boolean fuzzy, String entityType){
		final long startTime = System.currentTimeMillis();
    	IndexReader reader = null;
		try {
	    	Directory indexDirectory = FSDirectory.open(Paths.get(((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexDirectory().toString(),"Master_Index"));
	    	reader = DirectoryReader.open(indexDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BooleanQuery.setMaxClauseCount(10000);
		Analyzer standardAnalyzer = new StandardAnalyzer();
    	String[] fields = {MetaDataImplementation.TITLE,MetaDataImplementation.DESCRIPTION,MetaDataImplementation.COVERAGE,MetaDataImplementation.IDENTIFIER,
    			MetaDataImplementation.SIZE,MetaDataImplementation.TYPE,MetaDataImplementation.LANGUAGE,MetaDataImplementation.PERSON,MetaDataImplementation.LEGALPERSON,MetaDataImplementation.ALGORITHM,MetaDataImplementation.CHECKSUM,MetaDataImplementation.SUBJECT,
    			MetaDataImplementation.RELATION,MetaDataImplementation.MIMETYPE,MetaDataImplementation.STARTDATE,MetaDataImplementation.ENDDATE,
    			MetaDataImplementation.RELATIONTYPE, MetaDataImplementation.RELATEDIDENTIFIERTYPE};
		org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser =
			    new MultiFieldQueryParser(fields, standardAnalyzer);
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
        org.apache.lucene.search.Query luceneQuery = null;
        if(fuzzy) {
        	keyword += "~";
        }
		try {
			luceneQuery = parser.parse(QueryParser.escape(keyword));
		} catch (ParseException e2) {
			((FileSystemImplementationProvider)getImplProv()).getLogger().info("Not able to Parse: \n"+keyword);
			return new ArrayList<Integer>();
		}
		if(luceneQuery == null) {
			return new ArrayList<Integer>();
		}
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser queryType = new QueryParser(MetaDataImplementation.CHECKSUM,standardAnalyzer);
		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
		try {
			booleanQuery.add(queryType.parse(new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, entityType)).toString()), Occur.FILTER);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	IndexSearcher searcher = new IndexSearcher(reader);
        ScoreDoc[] hits2 = null;
        //BooleanQuery booleanQuery2 = booleanQuery.build();
		try {
			hits2 = searcher.search(booleanQuery.build(), 50000).scoreDocs;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		HashSet<PrimaryDataEntity> entities = new HashSet<>();
		
		final CriteriaBuilder builder = session.getCriteriaBuilder();
		ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < hits2.length; i++) {
        	Document doc = null;
			try {
				doc = searcher.doc(hits2[i].doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	ids.add(Integer.parseInt((doc.get(PublicVersionIndexWriterThread.PUBLICID))));
//			try {
//				doc = searcher.doc(hits2[i].doc);
//				CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
//						.createQuery(PrimaryDataFileImplementation.class);
//
//				Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);
//
//				fileCriteria.where(builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
//						builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID), doc.get(MetaDataImplementation.PRIMARYENTITYID))));
//				final PrimaryDataFileImplementation primaryDataFile = session.createQuery(fileCriteria)
//						.setCacheable(false)
//						.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();
//				if(primaryDataFile != null) {
//					entities.add(primaryDataFile);
//				}else {
//					CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
//							.createQuery(PrimaryDataDirectoryImplementation.class);
//
//					Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
//							.from(PrimaryDataDirectoryImplementation.class);
//					directoryCriteria.where(builder.and(builder.equal(fileRoot.type(), PrimaryDataDirectoryImplementation.class),
//							builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID), doc.get(MetaDataImplementation.PRIMARYENTITYID))));
//
//
//					final PrimaryDataDirectoryImplementation primaryDataDirectory = session.createQuery(directoryCriteria)
//							.setCacheable(false)
//							.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();
//					if(primaryDataDirectory != null) {
//						entities.add(primaryDataDirectory);
//					}
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
//		final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(entities);
//		return Collections.unmodifiableList(results);
        return ids;
	}

}
