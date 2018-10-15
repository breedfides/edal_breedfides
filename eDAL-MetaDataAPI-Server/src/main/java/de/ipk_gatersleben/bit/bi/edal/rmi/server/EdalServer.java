/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.ConnectIOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.RemoteServer;
import java.security.Principal;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.UnixPrincipal;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.DataManagerRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.nossl.EdalRmiSocketFactory;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.ssl.EdalSslRmiClientSocketFactory;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.ssl.EdalSslRmiServerSocketFactory;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.wrapper.DataManagerWrapper;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Implementation of a RMI server for the eDAL system.
 * 
 * @author arendd
 */
@SuppressWarnings("restriction")
public class EdalServer {

	public static final int DEFAULT_REGISTRY_PORT = 1099;
	public static final int DEFAULT_DATA_PORT = 1098;

	/** Clean database before server start */
	private static boolean cleanDatabase = false;

	/** The name of the eDAL RMI service */
	public static final String DATA_MANAGER_NAME = "DataManager";

	/** The port for the data */
	private static int dataPort = DEFAULT_DATA_PORT;

	/** The port for the {@link Registry} */
	private static int registryPort = DEFAULT_REGISTRY_PORT;

	/** The {@link Logger} for the eDAL-Server */
	private static Logger logger;

	/** The Logger for output to the console */
	private static Logger consoleLogger;

	/** Activate server log */
	private static boolean serverLog = false;

	/** Local Host Name **/
	private static String LOCAL_HOST_NAME = "localhost";

	/** rmi.server.hostname **/
	private static String RMI_SERVER_HOST_NAME = null;

	static {
		PropertyConfigurator.configure(EdalServer.class.getResource("log4j.properties"));

		EdalServer.logger = Logger.getLogger("eDAL-Server");
		EdalServer.consoleLogger = Logger.getLogger("eDAL-Server-Console");
		try {
			System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			EdalServer.logger.debug(e);
			EdalServer.logger.error("Unable to resolve localhost address: " + e.getMessage());
			EdalServer.logger.error("Please check your IPconfiguration !");
			System.exit(0);
		}
	}

	private static String dataCiteUser = "";
	private static String dataCitePassword = "";
	private static String dataCitePrefix = "";
	private static String httpProxyHost = "";
	private static int httpProxyPort = 0;
	private static String httpsProxyHost = "";
	private static int httpsProxyPort = 0;
	private static String smtpHost = "";
	private static String smtpLogin = "";
	private static String smtpPassword = "";

	private static InternetAddress scientificReviewer = null;
	private static InternetAddress substituteReviewer = null;
	private static InternetAddress managingReviewer = null;
	private static InternetAddress rootUser = null;

	private static boolean additionalParameter = false;

	/**
	 * Main function to start the RMI server
	 * {@link EdalServer#startServer(EdalConfiguration, int, int, boolean, boolean)}
	 * 
	 * @param args
	 *            an array of {@link String} objects.
	 * @throws EdalException
	 *             if failed
	 * @throws EdalConfigurationException
	 *             if configuration si invalid
	 */
	@SuppressWarnings("unchecked")
	public static void main(final String args[]) throws EdalException, EdalConfigurationException {

		/**
		 * -d dataPort<br>
		 * -h help<br>
		 * -p registryPort<br>
		 * -m mount path<br>
		 * -i data path<br>
		 * -u DatabaseUsername<br>
		 * -c DatabasePassword<br>
		 * -s server log<br>
		 * 
		 * 
		 * -l UnixPrincipal<br>
		 * -k KerberosPrincipal<br>
		 * -w NTUserPrincipal<br>
		 * -o own Principal<br>
		 * -dcu DataCite user<br>
		 * -dcp DataCite password<br>
		 * -dcr DataVite prefix<br>
		 * -r1 reviewer1<br>
		 * -r2 reviewer2<br>
		 * -r3 reviewer3<br>
		 * -sh SMTP host<br>
		 * -su SMTP user<br>
		 * -sp SMTP password<br>
		 * -hp Port for HTTP Listener<br>
		 * -hph HTTP proxy host<br>
		 * -hpp HTTP proxy port<br>
		 * -hsh HTTPS proxy host<br>
		 * -hsp HTTP proxy port<br>
		 */

		final Options options = new Options();

		EdalServer.consoleLogger.info("eDAL repository server" + "\nIPK-Gatersleben.  All rights reserved.\n");

		Option helpOption = new Option("h", "help", false, "print help");

		Option dataPortOption = new Option("d", "dataPort", true,
				"Port for transferring the RMI data streams (default=" + EdalServer.dataPort + ")");

		Option registryPortOption = new Option("p", "registryPort", true,
				"Port for RMI registry (default=" + EdalServer.registryPort + ")");

		Option serverLogOption = new Option("s", "serverLog", false, "Activate RMI server log");

		Option useNoSSLOption = new Option("n", "noSsl", false, "Deactivate SSL for RMI transfer");

		Option rmiServerHostNameOption = new Option("rmi", "rmiserver", true,
				"Set 'java.rmi.server.hostname'\nWARNING:\nIts strongly encouraged to use the dynamic identified server hostname");

		Option dataCitePasswordOption = new Option("dcp", "dataCitePassword", true,
				"Password for the used DataCite account");
		Option dataCitePrefixOption = new Option("dcr", "dataCitePrefix", true,
				"Prefix of the used DataCite account (use '10.5072' for test mode)");
		Option dataCiteUserOption = new Option("dcu", "dataCiteUser", true, "Username for the used DataCite account");

		Option databaseUserOption = new Option("du", "DBuser", true,
				"Username for the embedded database (default='" + EdalConfiguration.DEFAULT_DATABASE_USERNAME + "')");
		Option databasePasswordOption = new Option("dp", "DBpass", true,
				"Password for the embedded database (default='" + EdalConfiguration.DEFAULT_DATABASE_PASSWORD + "')");

		Option httpListenerPortOption = new Option("hp", "httpListenerPort", true,
				"Port for HTTP Listener (default=" + EdalConfiguration.DEFAULT_HTTP_PORT + ")");
		Option httpsListenerPortOption = new Option("hps", "httpsListenerPort", true,
				"Port for HTTPS Listener (default=" + EdalConfiguration.DEFAULT_HTTPS_PORT + ")");

		Option httpProxyHostOption = new Option("hph", "httpProxyHost", true, "Address for HTTP proxy");
		Option httpProxyPortOption = new Option("hpp", "httpProxyPort", true, "Port for HTTP proxy");
		Option httpsProxyHostOption = new Option("hsh", "httpsProxyHost", true, "Address for HTTPS proxy");
		Option httpsProxyPortOption = new Option("hsp", "httpsProxyPort", true, "Port for HTTPS proxy");

		Option mountPathOption = new Option("m", "mountPath", true,
				"Path for e!DAL database/index files (default=" + EdalConfiguration.DEFAULT_PATH.toString() + ")");

		Option dataPathOption = new Option("i", "dataPath", true,
				"Path for e!DAL data files (default=" + EdalConfiguration.DEFAULT_PATH.toString() + ")");

		Option useUnixPrincipalOption = new Option("l", "Use UnixPrincipal");
		Option useNTUserPrincipalOption = new Option("w", "Use NTUserPrincipal");
		Option useKerberosPrincipalOption = new Option("k", "Use KerberosPrincipal");
		Option useOwnPrincipalOption = new Option("o", true, "Use own Principal class by name");

		Option doiInfixOption = new Option("dif", "doiInfix", true,
				"Set specific infix for DOI construction schema: <doi-prefix>/<doi-infix>/<year>/<digit> WARNING:\nIts strongly encouraged to use automatic schema by embedded query the official registered data centre infix");

		Option scientificReviewerOption = new Option("r1", "scientificReviewer", true,
				"Email address for the scientific reviewer");
		Option substituteReviewerOption = new Option("r2", "substituteReviewer", true,
				"Email address for the substitute reviewer");
		Option managingReviewerOption = new Option("r3", "managingReviewer", true,
				"Email address for the managing reviewer");
		Option rootUserOption = new Option("ru", "rootUser", true, "Email address for the root user");

		Option smtpHostOption = new Option("sh", "smtpHost", true, "Address of SMTP host");
		Option smtpUserOption = new Option("su", "smtpLogin", true, "Username for SMTP host");
		Option smtpPasswordOption = new Option("sp", "smtpPassword", true, "Password for SMTP host");

		Option useHTTPSOption = new Option("https", "httpsListener", false, "Activate SSL for HTTP Listener");
		Option HTTPSKeyStorePathOption = new Option("kp", "keystorepath", true, "Path to SSL KeyStore");
		Option HTTPSKeyStorePasswordOption = new Option("kpass", "keystorepassword", true, "Password for SSL KeyStore");

		Option staticServerAdressOption = new Option("ss", "staticserver", true,
				"Static server address for HTTP Listener\nWARNING:\nIts strongly encouraged to use the dynamic identified server address");

		Option staticServerPortOption = new Option("ssp", "staticport", true,
				"Static server port for HTTP Listener\nWARNING:\nIts strongly encouraged to use the standard server port");

		Option deactivateCleanBrokenEntitiesOption = new Option("c", "stopClean", false,
				"Deactivate the default clean function for Entities, which were broken during upload process. Deactivate only if you use the system also as storgae backend");

		Option instanceNameLongOption = new Option("inl", "instanceNameLong", true,
				"Set the name of the runnign e!DAL instance (long version)");

		Option instanceNameShortOption = new Option("ins", "instanceNameShort", true,
				"Set the name of the runnign e!DAL instance (short version)");

		options.addOption(helpOption);
		options.addOption(registryPortOption);
		options.addOption(mountPathOption);
		options.addOption(dataPathOption);
		options.addOption(databaseUserOption);
		options.addOption(databasePasswordOption);
		options.addOption(serverLogOption);
		options.addOption(dataPortOption);
		options.addOption(useUnixPrincipalOption);
		options.addOption(useNTUserPrincipalOption);
		options.addOption(useKerberosPrincipalOption);
		options.addOption(useOwnPrincipalOption);

		options.addOption(dataCiteUserOption);
		options.addOption(dataCitePasswordOption);
		options.addOption(dataCitePrefixOption);
		options.addOption(doiInfixOption);

		options.addOption(scientificReviewerOption);
		options.addOption(substituteReviewerOption);
		options.addOption(managingReviewerOption);
		options.addOption(rootUserOption);

		options.addOption(smtpHostOption);
		options.addOption(smtpUserOption);
		options.addOption(smtpPasswordOption);

		options.addOption(httpProxyHostOption);
		options.addOption(httpProxyPortOption);
		options.addOption(httpsProxyHostOption);
		options.addOption(httpsProxyPortOption);
		options.addOption(httpListenerPortOption);
		options.addOption(httpsListenerPortOption);

		options.addOption(useNoSSLOption);
		options.addOption(useHTTPSOption);
		options.addOption(HTTPSKeyStorePathOption);
		options.addOption(HTTPSKeyStorePasswordOption);

		options.addOption(staticServerAdressOption);
		options.addOption(staticServerPortOption);
		options.addOption(rmiServerHostNameOption);

		options.addOption(deactivateCleanBrokenEntitiesOption);

		options.addOption(instanceNameLongOption);
		options.addOption(instanceNameShortOption);

		/**
		 * First check all mandatory parameter to create an EdalConfiguration object
		 */
		EdalConfiguration configuration = null;

		final CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			logger.warn("Unable to parse command line parameter: " + e.getMessage());
			System.exit(0);
		}
		if (cmd.hasOption(helpOption.getOpt())) {
			HelpFormatter form = new HelpFormatter();
			form.printHelp("-h", options);
			System.exit(0);
		}

		if (!cmd.hasOption(dataCitePrefixOption.getOpt())) {
			logger.warn("Please set DataCite prefix using '-" + dataCitePrefixOption.getOpt() + " <arg>'");
			System.exit(0);
		} else {

			String givenPrefix = cmd.getOptionValue(dataCitePrefixOption.getOpt());

			EdalServer.dataCitePrefix = cmd.getOptionValue(dataCitePrefixOption.getOpt());

			if (!givenPrefix.equals(EdalConfiguration.DATACITE_TESTPREFIX)) {
				if (!cmd.hasOption(dataCiteUserOption.getOpt())) {
					logger.warn("Please set DataCite user name using '-" + dataCiteUserOption.getOpt() + " <arg>'");
					System.exit(0);
				} else {
					EdalServer.dataCiteUser = cmd.getOptionValue(dataCiteUserOption.getOpt());
				}
				if (!cmd.hasOption(dataCitePasswordOption.getOpt())) {
					logger.warn("Please set DataCite password using '-" + dataCitePasswordOption.getOpt() + " <arg>'");
					System.exit(0);
				} else {
					EdalServer.dataCitePassword = cmd.getOptionValue(dataCitePasswordOption.getOpt());
				}

			}

		}

		if (!cmd.hasOption(rootUserOption.getOpt())) {
			logger.warn("Please set root user email address using '-" + rootUserOption.getOpt() + " <arg>'");
			System.exit(0);
		} else {
			try {
				EdalServer.rootUser = new InternetAddress(cmd.getOptionValue(rootUserOption.getOpt()));
			} catch (AddressException e) {
				logger.warn("unable to validate email address for root user: " + e.getMessage());
				System.exit(0);
			}
		}

		if (!cmd.hasOption(scientificReviewerOption.getOpt())) {
			logger.warn("Please set email address for the scientific reviewer using '-"
					+ scientificReviewerOption.getOpt() + " <arg>'");
			System.exit(0);
		} else {
			try {
				EdalServer.scientificReviewer = new InternetAddress(
						cmd.getOptionValue(scientificReviewerOption.getOpt()));
			} catch (AddressException e) {
				throw new EdalException("unable to set reviewer address", e);
			}
		}
		if (!cmd.hasOption(substituteReviewerOption.getOpt())) {
			logger.warn("Please set email address for the substitute reviewer using '-"
					+ substituteReviewerOption.getOpt() + " <arg>'");
			System.exit(0);
		} else {
			try {
				EdalServer.substituteReviewer = new InternetAddress(
						cmd.getOptionValue(substituteReviewerOption.getOpt()));
			} catch (AddressException e) {
				throw new EdalException("unable to set reviewer address", e);
			}
		}
		if (!cmd.hasOption(managingReviewerOption.getOpt())) {
			logger.warn("Please set email address for the managing reviewer using '-" + managingReviewerOption.getOpt()
					+ " <arg>'");
			System.exit(0);
		} else {
			try {
				EdalServer.managingReviewer = new InternetAddress(cmd.getOptionValue(managingReviewerOption.getOpt()));
			} catch (AddressException e) {
				throw new EdalException("unable to set reviewer address", e);
			}
		}

		/**
		 * now set all additional parameter
		 */

		if (cmd.hasOption(smtpHostOption.getOpt())) {
			smtpHost = cmd.getOptionValue(smtpHostOption.getOpt());
			additionalParameter = true;
		}
		if (cmd.hasOption(smtpUserOption.getOpt())) {
			smtpLogin = cmd.getOptionValue(smtpUserOption.getOpt());
			additionalParameter = true;
		}
		if (cmd.hasOption(smtpPasswordOption.getOpt())) {
			smtpPassword = cmd.getOptionValue(smtpPasswordOption.getOpt());
			additionalParameter = true;
		}

		if (cmd.hasOption(httpProxyHostOption.getOpt()) || cmd.hasOption(httpProxyPortOption.getOpt())
				|| cmd.hasOption(httpsProxyHostOption.getOpt()) || cmd.hasOption(httpsProxyPortOption.getOpt())) {

			if (cmd.hasOption(httpProxyHostOption.getOpt())) {
				httpProxyHost = cmd.getOptionValue(httpProxyHostOption.getOpt());
				additionalParameter = true;

			} else {
				logger.warn("If you set proxy settings, then set also HTTP Proxy Host using '-"
						+ httpProxyHostOption.getOpt() + " <arg>'");
				System.exit(0);
			}

			if (cmd.hasOption(httpProxyPortOption.getOpt())) {
				httpProxyPort = Integer.valueOf(cmd.getOptionValue(httpProxyPortOption.getOpt()));
				additionalParameter = true;
			} else {
				logger.warn("If you set proxy settings, then set also HTTP Proxy Port using '-"
						+ httpProxyPortOption.getOpt() + " <arg>'");
				System.exit(0);
			}

			if (cmd.hasOption(httpsProxyHostOption.getOpt())) {
				httpsProxyHost = cmd.getOptionValue(httpsProxyHostOption.getOpt());
				additionalParameter = true;

			} else {
				logger.warn("If you set proxy settings, then set also HTTPS Proxy Host using '-"
						+ httpsProxyHostOption.getOpt() + " <arg>'");
				System.exit(0);
			}

			if (cmd.hasOption(httpsProxyPortOption.getOpt())) {
				httpsProxyPort = Integer.valueOf(cmd.getOptionValue(httpsProxyPortOption.getOpt()));
				additionalParameter = true;
			} else {
				logger.warn("If you set proxy settings, then set also HTTPS Proxy Port using '-"
						+ httpsProxyPortOption.getOpt() + " <arg>'");
				System.exit(0);
			}
		}

		if (additionalParameter) {
			configuration = new EdalConfiguration(dataCiteUser, dataCitePassword, dataCitePrefix, scientificReviewer,
					substituteReviewer, managingReviewer, rootUser, httpProxyHost, httpProxyPort, httpsProxyHost,
					httpsProxyPort, smtpHost, smtpLogin, smtpPassword);
		} else {
			configuration = new EdalConfiguration(dataCiteUser, dataCitePassword, dataCitePrefix, scientificReviewer,
					substituteReviewer, managingReviewer, rootUser);
		}

		if (cmd.hasOption(useHTTPSOption.getOpt())) {
			if (cmd.hasOption(HTTPSKeyStorePathOption.getOpt())) {

				URL url = null;

				try {
					Path p = Paths.get(cmd.getOptionValue(HTTPSKeyStorePathOption.getOpt()));

					url = p.toFile().toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}

				if (cmd.hasOption(HTTPSKeyStorePasswordOption.getOpt())) {

					configuration.setUseSSLForHttpListener(true, url,
							cmd.getOptionValue(HTTPSKeyStorePasswordOption.getOpt()));

				} else {
					configuration.setUseSSLForHttpListener(true, url, "");
				}

			} else {
				logger.warn("If you want to use HTTPS, please set path to keyStore of type JKS using '-"
						+ HTTPSKeyStorePathOption.getOpt() + " <arg>'");
				System.exit(0);
			}
		}

		if (cmd.hasOption(useNoSSLOption.getOpt())) {
			configuration.setUseSSL(false);
		}

		if (cmd.hasOption(httpListenerPortOption.getOpt())) {
			configuration.setHttpPort(Integer.parseInt(cmd.getOptionValue(httpListenerPortOption.getOpt())));
		}
		if (cmd.hasOption(httpsListenerPortOption.getOpt())) {
			configuration.setHttpsPort(Integer.parseInt(cmd.getOptionValue(httpsListenerPortOption.getOpt())));
		}

		if (cmd.hasOption(databaseUserOption.getOpt())) {
			configuration.setDatabaseUsername(cmd.getOptionValue(databaseUserOption.getOpt()));
		}
		if (cmd.hasOption(databasePasswordOption.getOpt())) {
			configuration.setDatabasePassword(cmd.getOptionValue(databasePasswordOption.getOpt()));
		}
		if (cmd.hasOption(registryPortOption.getOpt())) {
			EdalServer.registryPort = Integer.parseInt(cmd.getOptionValue(registryPortOption.getOpt()));
		}
		if (cmd.hasOption("d")) {
			EdalServer.dataPort = Integer.parseInt(cmd.getOptionValue("d"));
		}
		if (cmd.hasOption(dataPathOption.getOpt())) {
			configuration.setDataPath(Paths.get(cmd.getOptionValue(dataPathOption.getOpt())));
			if (cmd.hasOption(mountPathOption.getOpt())) {
				configuration.setMountPath(Paths.get(cmd.getOptionValue(mountPathOption.getOpt())));
			}
		}
		if (cmd.hasOption(mountPathOption.getOpt())) {
			configuration.setMountPath(Paths.get(cmd.getOptionValue(mountPathOption.getOpt())));
			if (cmd.hasOption(dataPathOption.getOpt())) {
				configuration.setDataPath(Paths.get(cmd.getOptionValue(dataPathOption.getOpt())));
			} else {
				configuration.setDataPath(Paths.get(cmd.getOptionValue(mountPathOption.getOpt())));
			}

		}
		if (cmd.hasOption(serverLogOption.getOpt())) {
			EdalServer.serverLog = true;
		}
		if (cmd.hasOption(useUnixPrincipalOption.getOpt())) {
			configuration.addSupportedPrincipal(UnixPrincipal.class);
		}
		if (cmd.hasOption(useKerberosPrincipalOption.getOpt())) {
			configuration.addSupportedPrincipal(KerberosPrincipal.class);
		}
		if (cmd.hasOption(useNTUserPrincipalOption.getOpt())) {
			configuration.addSupportedPrincipal(NTUserPrincipal.class);
		}
		if (cmd.hasOption(useOwnPrincipalOption.getOpt())) {
			try {
				configuration.addSupportedPrincipal(
						(Class<? extends Principal>) Class.forName(cmd.getOptionValue(useOwnPrincipalOption.getOpt())));
			} catch (final ClassNotFoundException e) {
				logger.debug(e);
				throw new EdalException("Unable to load class for principal : " + cmd.getOptionValue("o"));

			}
		}

		if (cmd.hasOption(doiInfixOption.getOpt())) {
			logger.warn("None default DOI naming schema was configured. This could cause inhomogeneous DOIs");
			configuration.setDoiInfix(cmd.getOptionValue(doiInfixOption.getOpt()));
		}

		if (cmd.hasOption(staticServerAdressOption.getOpt())) {
			if (cmd.hasOption(staticServerPortOption.getOpt())) {
				logger.warn("None static server adress was configured. This could cause unresolveable DOIs");
				configuration.setStaticServerAdress(cmd.getOptionValue(staticServerAdressOption.getOpt()));
				configuration
						.setStaticServerPort(Integer.parseInt(cmd.getOptionValue(staticServerPortOption.getOpt())));
			} else {
				logger.warn("If you set a static server address, then please set also static server port using '-"
						+ staticServerPortOption.getOpt() + " <arg>'");
				System.exit(0);
			}
		}

		if (cmd.hasOption(staticServerPortOption.getOpt())) {
			if (cmd.hasOption(staticServerAdressOption.getOpt())) {
				logger.warn("None static server adress was configured. This could cause unresolveable DOIs");
				configuration.setStaticServerAdress(cmd.getOptionValue(staticServerAdressOption.getOpt()));
				configuration
						.setStaticServerPort(Integer.parseInt(cmd.getOptionValue(staticServerPortOption.getOpt())));
			} else {
				logger.warn("If you set a static server port, then please set also static server address using '-"
						+ staticServerAdressOption.getOpt() + " <arg>'");
				System.exit(0);
			}
		}

		if (cmd.hasOption(rmiServerHostNameOption.getOpt())) {
			RMI_SERVER_HOST_NAME = cmd.getOptionValue(rmiServerHostNameOption.getOpt());
		}

		if (cmd.hasOption(deactivateCleanBrokenEntitiesOption.getOpt())) {
			configuration.setCleanBrokenEntities(false);
		}

		if (cmd.hasOption(instanceNameLongOption.getOpt())) {
			configuration.setInstanceNameLong(cmd.getOptionValue(instanceNameLongOption.getOpt()));
		}
		if (cmd.hasOption(instanceNameShortOption.getOpt())) {
			configuration.setInstanceNameShort(cmd.getOptionValue(instanceNameShortOption.getOpt()));
		}

		EdalServer.startServer(configuration, EdalServer.registryPort, EdalServer.dataPort, EdalServer.cleanDatabase,
				EdalServer.serverLog);
	}

	/**
	 * Start the RMI server.
	 * 
	 * @param config
	 *            the configuration class to star eDal system
	 * @param registryPort
	 *            the port to start {@link Registry}
	 * @param dataPort
	 *            the data port for RMI
	 * @param cleanDB
	 *            <b><code>true</code></b>: <em>TAKE CARE!!</em> if the mount path
	 *            exist; the existing database and index files will be
	 *            dropped!!<b><br>
	 *            <code>false</code></b>: mount to existing mount path
	 * @param startLogging
	 *            <b><code>true</code></b>: print out server log to System.out. <br>
	 *            <b><code>false</code></b>: no output of server log.
	 */
	public static void startServer(final EdalConfiguration config, final int registryPort, final int dataPort,
			final boolean cleanDB, final boolean startLogging) {

		if (RMI_SERVER_HOST_NAME != null) {
			logger.warn("RMI server host name was configured to '" + RMI_SERVER_HOST_NAME
					+ "' . This could cause problems");
			System.setProperty("java.rmi.server.hostname", RMI_SERVER_HOST_NAME);
		}

		System.setProperty("com.healthmarketscience.rmiio.exporter.port", String.valueOf(dataPort));

		if (cleanDB) {
			try {
				cleanMountPath(config.getMountPath());
			} catch (EdalException e) {
				logger.error("Can not clean mount path : " + e.getMessage());
				System.exit(0);
			}
		}

		FileSystemImplementationProvider implementationProvider = null;

		implementationProvider = new FileSystemImplementationProvider(config);

		if (startLogging) {
			RemoteServer.setLog(System.out);
		} else {
			RemoteServer.setLog(null);
		}

		/* DataManager.initSecuritySystem(implementationProvider); */

		int httpListenPort = 0;
		int httpsListenPort = 0;

		try {
			try {
				httpListenPort = config.getHttpPort();
				httpsListenPort = config.getHttpsPort();
			} catch (EdalConfigurationException e) {
				logger.info("can not load port for HTTP/HTTPS Listener !", e);
				System.exit(0);
			}

			DataManager.getRootDirectory(implementationProvider, EdalHelpers.authenticateWinOrUnixOrMacUser());
			if (!config.isReadOnly()) {
				if (config.isUseSSL()) {

					/**
					 * It is not allowed to call createRegistry() twice within one JVM and there is
					 * no possibility to delete it. So the only way is to catch the exception and
					 * ignore it. Otherwise the Junit tests will not run
					 */
					try {
						Registry registry = LocateRegistry.createRegistry(registryPort,
								new EdalSslRmiClientSocketFactory(dataPort, EdalConfiguration.KEY_STORE_PATH),
								new EdalSslRmiServerSocketFactory(dataPort, EdalConfiguration.KEY_STORE_PATH));

						registry.rebind(EdalServer.DATA_MANAGER_NAME,
								new DataManagerWrapper(implementationProvider, dataPort));
					} catch (ExportException e) {
						logger.warn(e.getMessage());
						logger.info("RMI Registry already created !");
						Registry registry = LocateRegistry.getRegistry(LOCAL_HOST_NAME, registryPort,
								new EdalSslRmiClientSocketFactory(EdalConfiguration.KEY_STORE_PATH));
						registry.rebind(EdalServer.DATA_MANAGER_NAME,
								new DataManagerWrapper(implementationProvider, dataPort));
					}

				} else {

					if (!(RMISocketFactory.getSocketFactory() instanceof EdalRmiSocketFactory)) {
						RMISocketFactory.setSocketFactory(new EdalRmiSocketFactory(dataPort));

					}
					/**
					 * It is not allowed to call createRegistry() twice within one JVM and there is
					 * no possibility to delete it. So the only way is to catch the exception and
					 * ignore it. Otherwise the Junit tests will not run
					 */
					try {
						Registry registry = LocateRegistry.createRegistry(registryPort);

						registry.rebind(EdalServer.DATA_MANAGER_NAME,
								new DataManagerWrapper(implementationProvider, dataPort));
					} catch (ExportException e) {
						logger.warn(e.getMessage());
						logger.info("RMI Registry already created !");
						Registry registry = LocateRegistry.getRegistry(registryPort);
						registry.rebind(EdalServer.DATA_MANAGER_NAME,
								new DataManagerWrapper(implementationProvider, dataPort));
					}

				}

				if (config.isUseSSL()) {
					EdalServer.logger.info("RMI-Server is using secure SSL Connection");
				} else {
					EdalServer.logger.info("RMI-Server is using unsecure Connection");
				}

				EdalServer.logger.info("RMI-Server ready and listening at ports: " + registryPort + ", " + dataPort);

				if (config.isUseSSLForHttpListener()) {
					EdalServer.logger.info("HTTP-Listener ready and listening at port: " + httpListenPort);
					EdalServer.logger.info("HTTPS-Listener ready and listening at port: " + httpsListenPort);
				} else {
					EdalServer.logger.info("HTTP-Listener ready and listening at port: " + httpListenPort);
				}

				if (config.isUseSSL()) {
					EdalServer.consoleLogger.info("\nRMI-Server is using secure SSL Connection");
				} else {
					EdalServer.consoleLogger.info("\nRMI-Server is using unsecure Connection");
				}
				EdalServer.consoleLogger
						.info("RMI-Server ready and listening at ports: " + registryPort + ", " + dataPort);

				if (config.isUseSSLForHttpListener()) {
					EdalServer.consoleLogger.info("HTTP-Listener ready and listening at port: " + httpListenPort);
					EdalServer.consoleLogger.info("HTTPS-Listener ready and listening at port: " + httpsListenPort);
				} else {
					EdalServer.consoleLogger.info("HTTP-Listener ready and listening at port: " + httpListenPort);
				}
			}
		} catch (final PrimaryDataDirectoryException | EdalAuthenticateException | IOException e) {
			EdalServer.logger.error(e);
			System.exit(-1);
		}
	}

	/**
	 * Stop a running eDAL RMI server.
	 * 
	 * @param host
	 *            the host to stop the {@link Registry}.
	 * @param port
	 *            the port to stop the {@link Registry}.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public static void stopServer(final String host, final int port) throws RemoteException {

		PropertyConfigurator.configure(EdalServer.class.getResource("log4j.properties"));
		EdalServer.logger = Logger.getLogger("eDAL-Server");

		EdalServer.logger.info("Stopping RMI-Server at port: " + port);

		DataManagerRmiInterface dataManager;
		try {
			EdalServer.logger.info("trying unsecure Connection...");

			try {
				Registry registry = LocateRegistry.getRegistry(port);
				dataManager = (DataManagerRmiInterface) registry.lookup(EdalServer.DATA_MANAGER_NAME);
				dataManager.shutdown();
				registry.unbind(EdalServer.DATA_MANAGER_NAME);
				EdalServer.logger.info("unsecure Connection successful !");
			} catch (NotBoundException e) {
				EdalServer.logger.error("unable to unbind the DataManager: " + e.getMessage(), e);
			}

		} catch (final ConnectIOException e) {
			EdalServer.logger.info("unsecure Connection failed !");
			EdalServer.logger.info("trying SSL Connection...");
			try {
				Registry registry = LocateRegistry.getRegistry(LOCAL_HOST_NAME, port,
						new EdalSslRmiClientSocketFactory(EdalConfiguration.KEY_STORE_PATH));

				dataManager = (DataManagerRmiInterface) registry.lookup(EdalServer.DATA_MANAGER_NAME);
				dataManager.shutdown();
				registry.unbind(EdalServer.DATA_MANAGER_NAME);
				EdalServer.logger.info("secure Connection successful !");

			} catch (NotBoundException e1) {
				EdalServer.logger.error("unable to unbind the DataManager: " + e.getMessage(), e);
			}
		}

		EdalServer.logger.info("RMI-Server stopped !");

	}

	public static Logger getLogger() {
		return EdalServer.logger;
	}

	/**
	 * Clean all files in directory.
	 * 
	 * @param path
	 *            a {@link Path} object.
	 * @throws EdalException
	 *             if unable to clean mount path.
	 */
	private static void cleanMountPath(final Path path) throws EdalException {
		try {
			FileUtils.deleteDirectory(path.toFile());
		} catch (IOException e) {
			throw new EdalException("Can not clean mount path: " + e.getMessage());
		}
	}

}
