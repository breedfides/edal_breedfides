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
package de.ipk_gatersleben.bit.bi.edal.rmi.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.security.auth.Subject;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ALLPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.DataManagerRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.ssl.EdalSslRmiClientSocketFactory;

/**
 * Class that provides the connection to an eDAL RMI server.
 * 
 * @author arendd
 */
public class ClientDataManager {

	/** seconds to wait for a server connection */
	private static final int TIMEOUT_FOR_SERVER_CONNECTION = 10;

	public static Logger logger = null;

	static {

		ClientDataManager.logger = LogManager.getLogger("eDAL-Client");
		ClientDataManager.subject = null;
		ClientDataManager.dataManager = null;
	}

	private static Subject subject;

	private static DataManagerRmiInterface dataManager;

	private static Map<Principal, List<Methods>> userPermissions;

	/**
	 * Constant map with all initial default permissions.
	 * <p>
	 * {@link ALLPrincipal}:
	 * <p>
	 * Methods.listPrimaryDataEntities
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
	public static Map<Principal, List<Methods>> DEFAULT_PERMISSIONS = new HashMap<Principal, List<Methods>>();

	/**
	 * Do nothing. Just to force the Java Class loader to load the class and run
	 * the static block to initialize the logging system.
	 */
	public static void init() {

	}

	private final String serverAddress;
	private final int registryPort;

	private final Authentication auth;

	/**
	 * Constructor for {@link ClientDataManager}.
	 * 
	 * @param authentication
	 *            the {@link Authentication} object.
	 * @param registryPort
	 *            the port to the eDAL server.
	 * @param serverAddress
	 *            the path of the eDAL server.
	 * @throws EdalAuthenticateException
	 *             if the {@link Subject} is null.
	 */
	public ClientDataManager(final String serverAddress, final int registryPort, final Authentication authentication)
			throws EdalAuthenticateException {

		this.serverAddress = serverAddress;
		this.registryPort = registryPort;
		this.auth = authentication;

	}

	/**
	 * Create a new {@link MetaData} instance with default values.
	 * 
	 * @return a new {@link MetaData} object.
	 */
	public MetaData createMetadataInstance() {
		MetaData metadata = null;
		try {
			metadata = ClientDataManager.dataManager.createMetaDataInstance();
		} catch (final RemoteException e) {
			ClientDataManager.logger.error(e.getMessage());
		}
		return metadata;
	}

	public Authentication getAuthentication() {
		return this.auth;

	}

	/**
	 * Getter for the available space in the mount path of eDAL.
	 * 
	 * @return available space
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if no mount path is set.
	 */
	public Long getAvailableStorageSpace() throws RemoteException, EdalException {
		return ClientDataManager.dataManager.getAvailableStorageSpace();
	}

	/**
	 * Get the local stored default permissions for the current client user.
	 * 
	 * @return the {@link Map} with the default permissions.
	 */
	public Map<Principal, List<Methods>> getDefaultPermissions() {
		return ClientDataManager.userPermissions;
	}

	/**
	 * @return the registryPort
	 */
	public int getRegistryPort() {
		return this.registryPort;
	}

	/**
	 * Central entry point. Connect the client to the eDAL system and provide
	 * the root {@link ClientPrimaryDataDirectory}.
	 * 
	 * @return the root {@link ClientPrimaryDataDirectory} object.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws NotBoundException
	 *             if no {@link DataManagerRmiInterface} is bound.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load the rootDirectory.
	 * @throws EdalException
	 *             if failed
	 * @throws EdalAuthenticateException
	 *             if authentication failed
	 */
	public ClientPrimaryDataDirectory getRootDirectory() throws RemoteException, NotBoundException,
			PrimaryDataDirectoryException, EdalException, EdalAuthenticateException {

		/**
		 * it is important to reload the log4j configuration again, because it
		 * will be overwritten by the EdalServer, when calling lookup.
		 */
//		PropertyConfigurator.configure(ClientDataManager.class.getResource("log4j.properties"));
		ClientDataManager.logger = LogManager.getLogger("eDAL-Client");

		Registry registry = null;
		try {
			ClientDataManager.logger.info("Trying unsecure connection to '" + this.serverAddress + "'...");

			ExecutorService executor = Executors.newSingleThreadExecutor();

			RmiRegistryCaller caller = new RmiRegistryCaller(registry, serverAddress, registryPort, false);

			Future<Object> connectionThread = executor.submit(caller);

			connectionThread.get(TIMEOUT_FOR_SERVER_CONNECTION, TimeUnit.SECONDS);

			/**
			 * it is important to reload the log4j configuration again, because
			 * it will be overwritten by the EdalServer, when calling lookup.
			 */
//			PropertyConfigurator.configure(ClientDataManager.class.getResource("log4j.properties"));
			ClientDataManager.logger = LogManager.getLogger("eDAL-Client");
			ClientDataManager.logger.info("Unsecure connection successful");

		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			ClientDataManager.logger.info("Unsecure Connection failed");
			ClientDataManager.logger.info("Trying SSL Connection to '" + this.serverAddress + "'...");

			ExecutorService executor = Executors.newSingleThreadExecutor();

			RmiRegistryCaller caller = new RmiRegistryCaller(registry, serverAddress, registryPort, true);

			Future<Object> connectionThread = executor.submit(caller);

			try {
				connectionThread.get(TIMEOUT_FOR_SERVER_CONNECTION, TimeUnit.SECONDS);
			} catch (InterruptedException | TimeoutException | ExecutionException ex) {
				ClientDataManager.logger.info("SSL Connection failed");

				if (ex.getClass().equals(TimeoutException.class)) {
					throw new ConnectException("Timeout was exceeded");
				} else {
					throw new ConnectException("Connection refused");
				}

			}

			/**
			 * it is important to reload the log4j configuration again, because
			 * it will be overwritten by the EdalServer, when calling lookup.
			 */
//			PropertyConfigurator.configure(ClientDataManager.class.getResource("log4j.properties"));
			ClientDataManager.logger = LogManager.getLogger("eDAL-Client");

			ClientDataManager.logger.info("Secure connection successful");

		}

		ClientDataManager.subject = ClientDataManager.dataManager.authenticate(this.auth);

		final PrimaryDataDirectoryRmiInterface rootDirectory = ClientDataManager.dataManager
				.getRootDirectory(ClientDataManager.subject);

		ClientDataManager.userPermissions = ClientDataManager.dataManager.getDefaultPermissions();
		ClientDataManager.DEFAULT_PERMISSIONS = ClientDataManager.dataManager.getDefaultPermissions();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String testString = "E!DAL";
		try {
			this.testReadingFromServer(testString, byteArrayOutputStream);
		} catch (IOException e) {
			ClientDataManager.logger.info("Test Reading from server failed");
			throw new ConnectException("Test Reading from server failed");
		}

		if (byteArrayOutputStream.toString().equals(testString)) {
			ClientDataManager.logger.info("Test Reading from server successful");
		} else {
			ClientDataManager.logger.info("Test Reading form server failed");
			throw new ConnectException("Test Reading from server failed");
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testString.getBytes());

		try {
			this.testStoringOnServer("test.txt", byteArrayInputStream);
		} catch (IOException e) {
			ClientDataManager.logger.info("Test Storing to server failed");
			throw new ConnectException("Test Storing to server failed");

		}

		if (byteArrayInputStream.read() == -1) {
			ClientDataManager.logger.info("Test Storing to server successful");
		} else {
			ClientDataManager.logger.info("Test Storing to server failed");
			throw new ConnectException("Test Storing to server failed");
		}

		return new ClientPrimaryDataDirectory(rootDirectory, this);

	}

	private class RmiRegistryCaller implements Callable<Object> {

		private String serverAddress;
		private int registryPort;
		private Registry registry;
		private boolean useSSL = false;

		public RmiRegistryCaller(Registry registry, String serverAddress, int registryPort, boolean useSSL) {
			this.serverAddress = serverAddress;
			this.registryPort = registryPort;
			this.registry = registry;
			this.useSSL = useSSL;
		}

		@Override
		public Object call() throws Exception {
			if (this.useSSL) {
				this.registry = LocateRegistry.getRegistry(this.serverAddress, this.registryPort,
						new EdalSslRmiClientSocketFactory(EdalConfiguration.KEY_STORE_PATH));
				ClientDataManager.dataManager = (DataManagerRmiInterface) this.registry
						.lookup(EdalServer.DATA_MANAGER_NAME);
			} else {
				this.registry = LocateRegistry.getRegistry(this.serverAddress, this.registryPort);
				ClientDataManager.dataManager = (DataManagerRmiInterface) this.registry
						.lookup(EdalServer.DATA_MANAGER_NAME);
			}
			return null;
		}

	}

	/**
	 * @return the serverAddress
	 */
	public String getServerAddress() {
		return this.serverAddress;
	}

	/**
	 * Getter for the current {@link Subject}.
	 * 
	 * @return the subject
	 */
	protected Subject getSubject() {

		Subject ret;

		if ((ret = ClientDataManager.subject) == null) {
			ClientDataManager.logger.error("current subject is null");
		}
		return ret;
	}

	/**
	 * Getter all supported {@link Principal}s of the current eDAL system.
	 * 
	 * @return the list of supported {@link Principal}s
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if unable to load {@link Principal}s.
	 */
	public List<Class<? extends Principal>> getSupportedPrincipals() throws RemoteException, EdalException {
		return ClientDataManager.dataManager.getSupportedPrincipals();
	}

	/**
	 * Getter for the used space in the mount path of eDAL.
	 * 
	 * @return used space
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if no mount path is set.
	 */
	public Long getUsedStorageSpace() throws RemoteException, EdalException {
		return ClientDataManager.dataManager.getUsedStorageSpace();
	}

	/**
	 * Reset the initial default permissions.
	 */
	public void resetDefaultPermissions() {
		ClientDataManager.userPermissions = ClientDataManager.DEFAULT_PERMISSIONS;
	}

	/**
	 * Overrides the current default permissions of the current user with the
	 * new permissions.
	 * 
	 * @param permissions
	 *            the permissions to store.
	 */
	public void setDefaultPermissions(final Map<Principal, List<Methods>> permissions) {
		ClientDataManager.userPermissions = permissions;
	}

	public void testReadingFromServer(String fileName, OutputStream outputStream) throws IOException {

		InputStream inStream = RemoteInputStreamClient.wrap(ClientDataManager.dataManager.sendFileToClient(fileName));

		IOUtils.copy(inStream, outputStream);

		inStream.close();
		outputStream.close();

	}

	public void testStoringOnServer(String fileName, InputStream inputStream) throws IOException {

		OutputStream outStream = RemoteOutputStreamClient
				.wrap(ClientDataManager.dataManager.sendOutputStreamToFillFromClient(fileName));

		IOUtils.copy(inputStream, outStream);

		inputStream.close();
		outStream.close();

	}

	public void sendEmail(String emailMessage, String emailSubject) throws RemoteException {

		if (ClientDataManager.dataManager == null) {
			throw new RemoteException(
					"Please execute getRootDirectory first to connect to the server, before sending an eMail!");
		}
		ClientDataManager.dataManager.sendEmail(this.auth, emailMessage, emailSubject);

	}

	public void sendEmail(String emailMessage, String emailSubject, URL attachment) throws RemoteException {

		if (ClientDataManager.dataManager == null) {
			throw new RemoteException(
					"Please execute getRootDirectory first to connect to the server, before sending an eMail!");
		}
		ClientDataManager.dataManager.sendEmail(this.auth, emailMessage, emailSubject, attachment);

	}

}