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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.SmtpAppender;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.PlatformUtil.Platform;
import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.UnixPrincipal;

import de.ipk_gatersleben.bit.bi.edal.breedfides.rest.InfoEndpoint;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.ElixirPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.GooglePrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.JWTPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteMDSConnector;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteRestConnector;

/**
 * class to collect all parameters to start the eDAL system.
 * 
 * @author arendd
 */
public final class EdalConfiguration {

	private static final String NOREPLY_EMAIL_DEFAULT = "noreply@ipk-gatersleben.de";

	private static final String MSG_UNABLE_TO_SET_PROXY = "unable to set proxy: ";

	private static Logger logger = null;

	static {
		EdalConfiguration.logger = LogManager.getLogger("eDAL-API");
		InfoEndpoint.setLogger(LogManager.getLogger("BreedFides"));
	}

	public static final String DATACITE_SEARCH_URL = "http://search.datacite.org/api/";

	private static final String DATACITE_MDS_URL = "https://mds.datacite.org/doi";

	public static final String DATACITE_TESTPREFIX = "10.5072";

	/**
	 * Connection timeout for HTTP connection to DataCite in milliseconds.
	 */
	public static final int DATACITE_CONNECTION_TIMEOUT = 10000;
	/**
	 * Connection read timeout for HTTP connection to DataCite in milliseconds.
	 */
	public static final int DATACITE_CONNECTION_READ_TIMEOUT = 10000;
	/**
	 * Connection timeout for SMTP connection.
	 */
	public static final int SMTP_CONNECTION_TIMEOUT = 10000;

	public static final int STREAM_BUFFER_SIZE = 1024 * 1024 * 10;

	/**
	 * Default names to check the eMail server.
	 */
	private static final String[] MAIL_SERVER_NAMES = { "imap", "pop", "mail", "smtp", "exchange" };

	public static final Path DEFAULT_PATH = Paths.get(System.getProperty("user.home"), "edal");

	public static final URL KEY_STORE_PATH = EdalConfiguration.class.getClassLoader()
			.getResource("de/ipk_gatersleben/bit/bi/edal/primary_data/keystore.jks");

	public static final String KEYSTORE_PASSWORD = "eDALkey";

	/**
	 * The default supported principals by eDAL.
	 */
	public static final List<Class<? extends Principal>> DEFAULT_SUPPORTED_PRINCIPALS = new ArrayList<Class<? extends Principal>>(
			Arrays.asList(SamplePrincipal.class, NTUserPrincipal.class, UnixPrincipal.class, KerberosPrincipal.class,
					GooglePrincipal.class, ElixirPrincipal.class, JWTPrincipal.class));

	/**
	 * The default database user name
	 */
	public static final String DEFAULT_DATABASE_USERNAME = "sa";

	/**
	 * The default database password
	 */
	public static final String DEFAULT_DATABASE_PASSWORD = "";

	/**
	 * The default port for the HTTP listener
	 */
	public static final int DEFAULT_HTTP_PORT = 80;

	public static final int DEFAULT_HTTPS_PORT = 443;

	public static InetSocketAddress guessProxySettings() {

		final List<Strategy> strategies = new ArrayList<Strategy>();

		if (PlatformUtil.getCurrentPlattform() == Platform.WIN) {
			strategies.add(Strategy.ENV_VAR);
			strategies.add(Strategy.OS_DEFAULT);
			strategies.add(Strategy.WIN);
			strategies.add(Strategy.JAVA);
			strategies.add(Strategy.BROWSER);
			strategies.add(Strategy.IE);
			strategies.add(Strategy.FIREFOX);
		} else if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
			strategies.add(Strategy.ENV_VAR);
			strategies.add(Strategy.OS_DEFAULT);
			strategies.add(Strategy.JAVA);
			strategies.add(Strategy.BROWSER);
			strategies.add(Strategy.KDE);
			strategies.add(Strategy.GNOME);
			strategies.add(Strategy.FIREFOX);
		} else if (PlatformUtil.getCurrentPlattform() == Platform.MAC_OS) {
			strategies.add(Strategy.ENV_VAR);
			strategies.add(Strategy.OS_DEFAULT);
			strategies.add(Strategy.JAVA);
			strategies.add(Strategy.BROWSER);
			strategies.add(Strategy.FIREFOX);
		} else {
			strategies.add(Strategy.OS_DEFAULT);
		}

		/**
		 * Note: do not override the default ProxSelector
		 * ProxySelector.setDefault(proxySearch.getProxySelector());
		 * 
		 * If the ProxySelector return null, all SearchStrategies failed
		 */

		ProxySearch proxySearch = null;

		boolean findProxySettings = false;

		InetSocketAddress address = null;

		for (final Strategy strategy : strategies) {

			proxySearch = new ProxySearch();
			proxySearch.addStrategy(strategy);

			try {
				final List<Proxy> proxyList = proxySearch.getProxySelector()
						.select(new URI(EdalConfiguration.DATACITE_MDS_URL));

				if (proxyList != null) {
					for (final Proxy proxy2 : proxyList) {
						final Proxy proxy = proxy2;
						if (proxy.type().equals(Type.HTTP)) {
							address = (InetSocketAddress) proxy.address();

							if (address != null) {

								EdalConfiguration.logger
										.info("Found HTTP Proxy : " + address.getHostName() + ":" + address.getPort());
								/**
								 * set the found proxy settings to the configuration for later usage
								 */

								findProxySettings = true;
							}
						}
					}
				}

			} catch (URISyntaxException | NullPointerException e) {
				EdalConfiguration.logger.debug("No Proxy Settings found for Strategy " + strategy);
			}
			if (findProxySettings) {
				EdalConfiguration.logger.info("Proxy Settings determined automatically for Strategy : " + strategy);
				return address;
			}
		}
		if (!findProxySettings) {
			EdalConfiguration.logger.info("No automatic Proxy Settings found");
		}

		return null;
	}

	public static String guessSmtpSettings(final String smtpLogin, final String smtpPassword)
			throws EdalConfigurationException {

		for (String mailServerNames : EdalConfiguration.MAIL_SERVER_NAMES) {

			// if (System.getenv("userdnsdomain") != null &&
			// !System.getenv("userdnsdomain").isEmpty()) {
			// mailServerNames = mailServerNames + "." +
			// System.getenv("userdnsdomain").toLowerCase(Locale.ENGLISH);
			// }
			//

			try {
				if (!System.getenv("userdnsdomain").isEmpty()) {
					mailServerNames = mailServerNames + "."
							+ System.getenv("userdnsdomain").toLowerCase(Locale.ENGLISH);

				}
			} catch (final NullPointerException e) {
				try {
					if (InetAddress.getLocalHost().getCanonicalHostName().contains(".")) {

						String domain = InetAddress.getLocalHost().getCanonicalHostName().substring(
								InetAddress.getLocalHost().getCanonicalHostName().indexOf(".") + 1,
								InetAddress.getLocalHost().getCanonicalHostName().length());

						mailServerNames = mailServerNames + "." + domain.toLowerCase(Locale.ENGLISH);

					}
				} catch (UnknownHostException e1) {
				}
			}

			final Properties properties = new Properties();
			properties.put("mail.smtp.host", mailServerNames);
			properties.put("mail.smtp.connectiontimeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
			properties.put("mail.smtp.timeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
			// properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", mailServerNames);

			final Session session = Session.getDefaultInstance(properties);

			try {
				if (!smtpLogin.isEmpty() && !smtpPassword.isEmpty()) {
					final Transport transport = session.getTransport("smtp");

					transport.connect(mailServerNames, smtpLogin, smtpPassword);
					transport.close();

				} else {
					final Transport transport = session.getTransport("smtp");
					transport.connect(mailServerNames, null, null);
					transport.close();

				}

				EdalConfiguration.logger.info("SMTP connection test: " + mailServerNames + " : successful");
				return mailServerNames;
			} catch (final MessagingException e) {
				e.printStackTrace();
				EdalConfiguration.logger
						.warn("SMTP connection test: " + mailServerNames + " -> failed : " + e.getMessage());
			}
		}
		throw new EdalConfigurationException("unable to connect to eMail Server, check SMTP settings");
	}

	private boolean inTestMode = false;
	private boolean inReadOnlyMode = false;

	/**
	 * The eMail address to send messages of edal.
	 */
	private String edalEmailAddress = "noreply";

	private String dataCitePassword;

	private String dataCitePrefix;

	private String dataCiteUser;

	private String doiInfix;
	private String staticServerAddress;
	private int staticServerPort;
	/**
	 * The mount {@link Path} of the eDAL system.
	 */
	private Path mountPath = EdalConfiguration.DEFAULT_PATH;

	/**
	 * The data {@link Path} of the eDAL system.
	 */
	private Path dataPath = EdalConfiguration.DEFAULT_PATH;
	private boolean useSSL = true;
	private InternetAddress errorEmailAddress;
	private Logger errorLogger;
	/**
	 * The password for the database mount user
	 */
	private String databasePassword;
	/**
	 * The name of database mount user
	 */
	private String databaseUsername;
	/**
	 * Port for the HTTP server/listener
	 */
	private int httpPort;
	/**
	 * Port for the HTTP server/listener
	 */
	private int httpsPort;

	/**
	 * Optional proxy parameter to connect with the DataCite connector
	 */
	private String httpProxyHost;

	private int httpProxyPort;

	private String httpsProxyHost;

	private int httpsProxyPort;

	/**
	 * SMTP configuration to send approval eMails
	 */
	private String mailSmtpHost;

	private String mailSmtpLogin;

	private String mailSmtpPassword;

	private InternetAddress reviewerManaging;
	private InternetAddress reviewerScientific;
	private InternetAddress reviewerSubstitute;
	private InternetAddress rootUser;

	/**
	 * {@link List} of supported {@link Principal}s for the security system
	 */
	private List<Class<? extends Principal>> supportedPrincipals;

	private boolean useSystemProxies = false;

	private boolean useSSLForHttpListener = false;

	private URL certificatePathForHttpListener = null;

	private String keystorePasswordForHttpListener = "";

	private boolean cleanBrokenEntities = true;

	private boolean indexingStrategy = false;

	public static int HIBERNATE_SEARCH_INDEXING = 0;

	public static int NATIVE_LUCENE_INDEXING = 1;

	/**
	 * Alias domain name for HTTP server
	 */
	private List<String> aliasDomainNames = null;

	private String instanceNameLong = "e!DAL - Plant Genomics & Phenomics Research Data Repository";
	private String instanceNameShort = "e!DAL - PGP Repository";
	private String publisherString = "e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP), IPK Gatersleben";
	private String publisherURL = "https://www.ipk-gatersleben.de";

	/**
	 * default constructor set default values for parameter that has not specified
	 * explicitly by user <br>
	 * 
	 * @throws EdalConfigurationException if unable to load reviewer rule files.
	 */
	private EdalConfiguration() throws EdalConfigurationException {
		this.setDatabaseUsername(EdalConfiguration.DEFAULT_DATABASE_USERNAME);
		this.setDatabasePassword(EdalConfiguration.DEFAULT_DATABASE_PASSWORD);
		this.setSupportedPrincipals(EdalConfiguration.DEFAULT_SUPPORTED_PRINCIPALS);
		this.setHttpPort(EdalConfiguration.DEFAULT_HTTP_PORT);
		this.setHttpsPort(EdalConfiguration.DEFAULT_HTTPS_PORT);
		this.setMailSmtpHost("");
		this.setMailSmtpLogin("");
		this.setMailSmtpPassword("");
	}

	public EdalConfiguration(final String dataCiteUser, final String dataCitePassword, final String dataCitePrefix,
			final InternetAddress scientificReviewer, final InternetAddress substituteReviewer,
			final InternetAddress managingReviewer, final InternetAddress rootUser) throws EdalConfigurationException {

		this();
		this.setDataCiteUser(dataCiteUser);
		this.setDataCitePassword(dataCitePassword);
		this.setDataCitePrefix(dataCitePrefix);
		this.setReviewerScientific(scientificReviewer);
		this.setReviewerSubstitute(substituteReviewer);
		this.setReviewerManaging(managingReviewer);
		this.setRootUser(rootUser);
		this.setErrorEmailAddress(rootUser);
		this.validate();

	}

	public EdalConfiguration(final String dataCiteUser, final String dataCitePassword, final String dataCitePrefix,
			final InternetAddress scientificReviewer, final InternetAddress substituteReviewer,
			final InternetAddress managingReviewer, final InternetAddress rootUser, final String httpProxyHost,
			final int httpProxyPort, final String httpsProxyHost, final int httpsProxyPort, final String smtpHost,
			final String smtpLogin, final String smtpPassword) throws EdalConfigurationException {

		this();
		this.setUseSystemProxies(true);
		this.setHttpProxyHost(httpProxyHost);
		this.setHttpProxyPort(httpProxyPort);
		this.setHttpsProxyHost(httpsProxyHost);
		this.setHttpsProxyPort(httpsProxyPort);
		this.setDataCiteUser(dataCiteUser);
		this.setDataCitePassword(dataCitePassword);
		this.setDataCitePrefix(dataCitePrefix);
		this.setReviewerScientific(scientificReviewer);
		this.setReviewerSubstitute(substituteReviewer);
		this.setReviewerManaging(managingReviewer);
		this.setRootUser(rootUser);
		this.setErrorEmailAddress(rootUser);
		this.setMailSmtpHost(smtpHost);
		this.setMailSmtpLogin(smtpLogin);
		this.setMailSmtpPassword(smtpPassword);
		this.validate();

	}

	public EdalConfiguration(final String dataCiteUser, final String dataCitePassword, final String dataCitePrefix,
			final InternetAddress scientificReviewer, final InternetAddress substituteReviewer,
			final InternetAddress managingReviewer, final InternetAddress rootUser, final String smtpHost,
			final String smtpLogin, final String smtpPassword) throws EdalConfigurationException {

		this();
		this.setDataCiteUser(dataCiteUser);
		this.setDataCitePassword(dataCitePassword);
		this.setDataCitePrefix(dataCitePrefix);
		this.setReviewerScientific(scientificReviewer);
		this.setReviewerSubstitute(substituteReviewer);
		this.setReviewerManaging(managingReviewer);
		this.setRootUser(rootUser);
		this.setErrorEmailAddress(rootUser);
		this.setMailSmtpHost(smtpHost);
		this.setMailSmtpLogin(smtpLogin);
		this.setMailSmtpPassword(smtpPassword);
		this.validate();
	}

	public void setHibernateIndexing(int configValue) {
		if (configValue == EdalConfiguration.HIBERNATE_SEARCH_INDEXING)
			this.indexingStrategy = true;
		else if (configValue == EdalConfiguration.NATIVE_LUCENE_INDEXING)
			this.indexingStrategy = false;
	}

	public boolean isHibernateSearchIndexingEnabled() {
		return this.indexingStrategy;
	}

	/**
	 * Add a supported Principal to the list of principals.
	 * 
	 * @param principal the principal to add.
	 */
	public void addSupportedPrincipal(final Class<? extends Principal> principal) {
		this.supportedPrincipals.add(principal);
	}

	public List<String> getAliasDomainNames() {
		return this.aliasDomainNames;
	}

	/**
	 * @return the certificatePathForHttpListener
	 */
	protected URL getCertificatePathForHttpListener() {
		return this.certificatePathForHttpListener;
	}

	/**
	 * Getter for the database password.
	 * 
	 * @return the database password
	 * @throws EdalConfigurationException if no database password is defined
	 */
	public String getDatabasePassword() throws EdalConfigurationException {
		if (this.databasePassword == null) {
			throw new EdalConfigurationException("no database password set!");
		}
		return this.databasePassword;
	}

	/**
	 * Getter for the database user name.
	 * 
	 * @return the database user name.
	 * @throws EdalConfigurationException if no database user name is defined
	 */
	public String getDatabaseUsername() throws EdalConfigurationException {

		if (this.databaseUsername == null || this.databaseUsername.isEmpty()) {
			throw new EdalConfigurationException("no database user name set!");
		}
		return this.databaseUsername;
	}

	/**
	 * Getter for the DataCite password.
	 * 
	 * @return the DataCite password.
	 * @throws EdalConfigurationException if no DataCite password is defined
	 */
	public String getDataCitePassword() throws EdalConfigurationException {
		if (this.dataCitePassword == null || this.dataCitePassword.isEmpty()) {
			throw new EdalConfigurationException("no DataCite password set!");
		}
		return this.dataCitePassword;
	}

	/**
	 * Getter for the DataCite prefix
	 * 
	 * @return the DataCite prefix
	 * @throws EdalConfigurationException if no prefix is defined
	 */
	public String getDataCitePrefix() throws EdalConfigurationException {
		if (this.dataCitePrefix == null || this.dataCitePrefix.isEmpty()) {
			throw new EdalConfigurationException("no DataCite prefix set!");
		}
		return this.dataCitePrefix;
	}

	/**
	 * Getter for the DataCite user name.
	 * 
	 * @return the DataCite user name.
	 * @throws EdalConfigurationException if no DataCite user name is defined
	 */
	public String getDataCiteUser() throws EdalConfigurationException {
		if (this.dataCiteUser == null || this.dataCiteUser.isEmpty()) {
			throw new EdalConfigurationException("no DataCite user name set!");
		}
		return this.dataCiteUser;
	}

	/**
	 * @return the data path.
	 */
	public Path getDataPath() {
		return this.dataPath;
	}

	public String getDoiInfix() {
		return this.doiInfix;
	}

	/**
	 * Getter for the edal email address.
	 * 
	 * @return the email address for edal messages.
	 */
	public String getEdalEmailAddress() {
		return this.edalEmailAddress;
	}

	/**
	 * Setter for the edal email address.
	 * 
	 */
	private void setEdalEmailAddress(String emailaddress) {
		this.edalEmailAddress = emailaddress;
	}

	/**
	 * Getter for the eMail address to send error messages.
	 * 
	 * @return the errorEmailAddress
	 * @throws EdalConfigurationException if no email address is set
	 */
	protected InternetAddress getErrorEmailAddress() throws EdalConfigurationException {
		if (this.errorEmailAddress == null) {
			throw new EdalConfigurationException("no error Email address set!");
		}
		return this.errorEmailAddress;
	}

	/**
	 * @return the logger
	 */
	public Logger getErrorLogger() {
		return this.errorLogger;
	}

	/**
	 * Getter for the port of the HTTP server/listener.
	 * 
	 * @return the HTTP port.
	 * @throws EdalConfigurationException if no HTTP port is set.
	 */
	public int getHttpPort() throws EdalConfigurationException {
		if (this.httpPort == 0) {
			throw new EdalConfigurationException("no http port set! ");
		}
		return this.httpPort;
	}

	/**
	 * Getter for the HTTP proxy host.
	 * 
	 * @return the HTTP proxy host.
	 * @throws EdalConfigurationException if no HTTP proxy host is defined
	 */
	public String getHttpProxyHost() throws EdalConfigurationException {
		if (this.httpProxyHost == null || this.httpProxyHost.isEmpty()) {
			throw new EdalConfigurationException("no HTTP proxy host set!");
		}
		return this.httpProxyHost;
	}

	/**
	 * Getter for the HTTP proxy port.
	 * 
	 * @return the HTTP proxy port.
	 * @throws EdalConfigurationException if no HTTP proxy port is defined
	 */
	public int getHttpProxyPort() throws EdalConfigurationException {
		if (this.httpProxyPort == 0) {
			throw new EdalConfigurationException("no HTTP proxy port set!");
		}
		return this.httpProxyPort;
	}

	/**
	 * Getter for the port of the HTTPS server/listener.
	 * 
	 * @return the HTTPS port.
	 * @throws EdalConfigurationException if no HTTPS port is set.
	 */
	public int getHttpsPort() throws EdalConfigurationException {
		if (this.httpsPort == 0) {
			throw new EdalConfigurationException("no https port set! ");
		}
		return this.httpsPort;
	}

	/**
	 * Getter for the HTTPS proxy host.
	 * 
	 * @return the HTTPS proxy host.
	 * @throws EdalConfigurationException if no HTTPS proxy host is defined
	 */
	public String getHttpsProxyHost() throws EdalConfigurationException {
		if (this.httpsProxyHost == null || this.httpsProxyHost.isEmpty()) {
			throw new EdalConfigurationException("no HTTPS proxy host set!");
		}
		return this.httpsProxyHost;
	}

	/**
	 * Getter for the HTTPS proxy port.
	 * 
	 * @return the HTTPS proxy port.
	 * @throws EdalConfigurationException if no HTTPS proxy port is defined
	 */
	public int getHttpsProxyPort() throws EdalConfigurationException {
		if (this.httpsProxyPort == 0) {
			throw new EdalConfigurationException("no HTTPS port host set!");
		}
		return this.httpsProxyPort;
	}

	/**
	 * @return the keystorePasswordForHttpListener
	 */
	protected String getKeystorePasswordForHttpListener() {
		return this.keystorePasswordForHttpListener;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return EdalConfiguration.logger;
	}

	/**
	 * Getter for the mail SMTP host.
	 * 
	 * @return the mail SMTP host.
	 */
	public String getMailSmtpHost() {
		return this.mailSmtpHost;
	}

	/**
	 * Getter for the mail SMTP host.
	 * 
	 * @return the mail SMTP host.
	 */
	public String getMailSmtpLogin() {
		return this.mailSmtpLogin;
	}

	/**
	 * Getter for the mail SMTP password.
	 * 
	 * @return the mail SMTP password.
	 */
	public String getMailSmtpPassword() {
		return this.mailSmtpPassword;
	}

	/**
	 * Getter for the mount path of the eDAL system.
	 * 
	 * @return the mountPath
	 */
	public Path getMountPath() {
		return this.mountPath;
	}

	/**
	 * Getter for the eMail address of the managing reviewer.
	 * 
	 * @return the REVIEWER_MANAGING
	 * @throws EdalConfigurationException if no emailAddress is defined or if it is
	 *                                    invalid.
	 */
	public InternetAddress getReviewerManaging() throws EdalConfigurationException {
		if (this.reviewerManaging == null) {
			throw new EdalConfigurationException("no email address for the managing reviewer set!");
		}
		try {
			this.reviewerManaging.validate();
		} catch (final AddressException e) {
			throw new EdalConfigurationException("invalid email address for managing reviewer: " + e.getMessage());
		}
		return this.reviewerManaging;
	}

	/**
	 * Getter for the eMail address of the scientific reviewer.
	 * 
	 * @return the reviewerScientific
	 * @throws EdalConfigurationException if no emailAddress is defined or if it is
	 *                                    invalid.
	 */
	public InternetAddress getReviewerScientific() throws EdalConfigurationException {
		if (this.reviewerScientific == null) {
			throw new EdalConfigurationException("no email address for the scientific reviewer set!");
		}
		try {
			this.reviewerScientific.validate();
		} catch (final AddressException e) {
			throw new EdalConfigurationException("invalid email address for scientific reviewer: " + e.getMessage());
		}
		return this.reviewerScientific;
	}

	/**
	 * Getter for the eMail address of the substitute reviewer.
	 * 
	 * @return the reviewerSubstitute
	 * @throws EdalConfigurationException if no emailAddress is defined or if it is
	 *                                    invalid.
	 */
	public InternetAddress getReviewerSubstitute() throws EdalConfigurationException {
		if (this.reviewerSubstitute == null) {
			throw new EdalConfigurationException("no email address for the substitute reviewer set!");
		}
		try {
			this.reviewerSubstitute.validate();
		} catch (final AddressException e) {
			throw new EdalConfigurationException("invalid email address for substitute reviewer: " + e.getMessage());
		}
		return this.reviewerSubstitute;
	}

	/**
	 * Getter for the eMail address for the root user.
	 * 
	 * @return the rootUser
	 * @throws EdalConfigurationException if no emailAddress is defined or if it is
	 *                                    invalid.
	 */
	public InternetAddress getRootUser() throws EdalConfigurationException {

		if (this.rootUser == null) {
			throw new EdalConfigurationException("no email address for the root user set!");
		}
		try {
			this.rootUser.validate();
		} catch (final AddressException e) {
			throw new EdalConfigurationException("invalid email address for root user: " + e.getMessage());
		}
		return this.rootUser;
	}

	public String getStaticServerAddress() {
		return this.staticServerAddress;
	}

	public int getStaticServerPort() {
		return this.staticServerPort;
	}

	/**
	 * Getter for the List of supported {@link Principal}s.
	 * 
	 * @return the List of supported {@link Principal}s
	 * @throws EdalConfigurationException if no supported principals are defined !
	 */
	public List<Class<? extends Principal>> getSupportedPrincipals() throws EdalConfigurationException {
		if (this.supportedPrincipals.isEmpty()) {
			throw new EdalConfigurationException("no supported principals defined!");
		}
		return this.supportedPrincipals;
	}

	/**
	 * @return the inTestModeE
	 */
	public boolean isInTestMode() {
		return this.inTestMode;
	}

	public boolean isReadOnly() {
		return this.inReadOnlyMode;
	}

	/**
	 * @return the useSSL
	 */
	public boolean isUseSSL() {
		return this.useSSL;
	}

	/**
	 * @return the useSSLForHttpListener
	 */
	public boolean isUseSSLForHttpListener() {
		return this.useSSLForHttpListener;
	}

	/**
	 * Check if proxies should be used.
	 * 
	 * @return true or false
	 */
	public boolean isUseSystemProxies() {
		return this.useSystemProxies;
	}

	private List<String> requestRegisteredDataCiteDomainName() throws EdalConfigurationException {

		JerseyClient client = JerseyClientBuilder.createClient();

		// full request
		// WebTarget webResource = client.target("https://api.datacite.org/prefixes/" +
		// this.dataCitePrefix+"?include=clients,providers,client-prefixes,provider-prefixes");

		WebTarget webResource = client
				.target("https://api.datacite.org/prefixes/" + this.dataCitePrefix + "?include=clients");

		final Response response = webResource.request(MediaType.APPLICATION_JSON).get();

		JSONObject json = null;
		try {
			json = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		} catch (ParseException e) {
			client.close();
			e.printStackTrace();
		}
		JSONArray included = (JSONArray) json.get("included");
		JSONObject eins = (JSONObject) included.get(0);
		JSONObject attributes = (JSONObject) eins.get("attributes");
		String domains = (String) attributes.get("domains");

		if (domains.contains(",")) {
			final String[] registeredDomains = domains.split(",");

			return Arrays.asList(registeredDomains);
		} else {
			return new ArrayList<String>(Arrays.asList(domains));
		}
	}

	private void setAliasDomainNames(final List<String> aliasDomainNames) {
		this.aliasDomainNames = aliasDomainNames;
	}

	/**
	 * @param certificatePathForHttpListener the certificatePathForHttpListener to
	 *                                       set
	 */
	protected void setCertificatePathForHttpListener(final URL certificatePathForHttpListener) {
		this.certificatePathForHttpListener = certificatePathForHttpListener;
	}

	/**
	 * Setter for the database password.
	 * 
	 * @param databasePassword the database password to set
	 */
	public void setDatabasePassword(final String databasePassword) {
		this.databasePassword = databasePassword;
	}

	/**
	 * Setter for the database user name.
	 * 
	 * @param databaseUsername the database user name to set
	 */
	public void setDatabaseUsername(final String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	/**
	 * Setter for the DataCite password.
	 * 
	 * @param dataCitePassword
	 */
	private void setDataCitePassword(final String dataCitePassword) {
		this.dataCitePassword = dataCitePassword;
	}

	/**
	 * Setter for the DataCite test prefix
	 * 
	 * @param dataCitePrefix
	 */
	private void setDataCitePrefix(final String dataCitePrefix) {
		this.dataCitePrefix = dataCitePrefix;
	}

	/**
	 * Setter for the DataCite user name.
	 * 
	 * @param dataCiteUser
	 */
	private void setDataCiteUser(final String dataCiteUser) {
		this.dataCiteUser = dataCiteUser;
	}

	/**
	 * Setter for the data path.
	 * 
	 * @param dataPath the dataPath to set
	 */
	public void setDataPath(final Path dataPath) {
		this.dataPath = dataPath;
	}

	public void setDoiInfix(final String doiInfix) throws EdalConfigurationException {
		if (doiInfix == null || doiInfix.isEmpty()) {
			throw new EdalConfigurationException("It is not allow to set an empty infix for DOI generation");
		}
		this.doiInfix = doiInfix;
	}

	/**
	 * @param errorEmailAddress the errorEmail to set
	 */
	protected void setErrorEmailAddress(final InternetAddress errorEmailAddress) {
		this.errorEmailAddress = errorEmailAddress;
	}

	/**
	 * Setter for the error email logger.
	 * 
	 * @param errorLogger the logger to set
	 */
	private void setErrorLogger(final Logger errorLogger) {
		this.errorLogger = errorLogger;
	}

	/**
	 * Setter for the port of the HTTP server/listener.
	 * 
	 * @param httpPort the HTTP port to set.
	 */
	public void setHttpPort(final int httpPort) {
		this.httpPort = httpPort;
	}

	/**
	 * Setter for the HTTP proxy host.
	 * 
	 * @param httpProxyHost the HTTP proxy host.
	 */
	public void setHttpProxyHost(final String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	/**
	 * Setter for the HTTP proxy port.
	 * 
	 * @param httpProxyPort the HTTP proxy port.
	 */
	public void setHttpProxyPort(final int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	/**
	 * Setter for the port of the HTTPS server/listener.
	 * 
	 * @param httpsPort the HTTPS port to set.
	 */
	public void setHttpsPort(final int httpsPort) {
		this.httpsPort = httpsPort;
	}

	/**
	 * Setter for the HTTPS proxy host.
	 * 
	 * @param httpsProxyHost the HTTP proxy host to set.
	 */
	public void setHttpsProxyHost(final String httpsProxyHost) {
		this.httpsProxyHost = httpsProxyHost;
	}

	/**
	 * Setter for the HTTPS proxy port.
	 * 
	 * @param httpsProxyPort the HTTP proxy port to set.
	 */
	public void setHttpsProxyPort(final int httpsProxyPort) {
		this.httpsProxyPort = httpsProxyPort;
	}

	/**
	 * @param inTestMode the iN_TEST_MODE to set
	 */
	private void setInTestMode(final boolean inTestMode) {
		this.inTestMode = inTestMode;
	}

	/**
	 * @param keystorePasswordForHttpListener the keystorePasswordForHttpListener to
	 *                                        set
	 */
	protected void setKeystorePasswordForHttpListener(final String keystorePasswordForHttpListener) {
		this.keystorePasswordForHttpListener = keystorePasswordForHttpListener;
	}

	/**
	 * Setter for the mail SMTP host.
	 * 
	 * @param mailSmtpHost the mail SMTP host to set.
	 */
	public void setMailSmtpHost(final String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	/**
	 * Setter for the mail SMTP login.
	 * 
	 * @param mailSmtpLogin the mail SMTP login to set.
	 */
	public void setMailSmtpLogin(final String mailSmtpLogin) {
		this.mailSmtpLogin = mailSmtpLogin;
	}

	/**
	 * Setter for the SMTP password.
	 * 
	 * @param mailSmtpPassword the password for the SMTP user
	 */
	public void setMailSmtpPassword(final String mailSmtpPassword) {
		this.mailSmtpPassword = mailSmtpPassword;
	}

	/**
	 * Setter for the mount path.
	 * 
	 * @param mountPath the mount path to set
	 */
	public void setMountPath(final Path mountPath) {
		this.mountPath = mountPath;
	}

	private void setReadOnly(final boolean inReadOnlyMode) {
		this.inReadOnlyMode = inReadOnlyMode;
	}

	/**
	 * Setter for the eMail address of the managing reviewer.
	 * 
	 * @param reviewerManaging the eMail address of the managing reviewer to set
	 */
	private void setReviewerManaging(final InternetAddress reviewerManaging) {
		this.reviewerManaging = reviewerManaging;
	}

	/**
	 * Setter for the eMail address of the scientific reviewer.
	 * 
	 * @param reviewerScientific the eMail address of the scientific reviewer to set
	 */
	private void setReviewerScientific(final InternetAddress reviewerScientific) {
		this.reviewerScientific = reviewerScientific;
	}

	/**
	 * Setter for the eMail address of the substitute reviewer.
	 * 
	 * @param reviewerSubstitute the eMail address of the substitute reviewer to set
	 */
	private void setReviewerSubstitute(final InternetAddress reviewerSubstitute) {
		this.reviewerSubstitute = reviewerSubstitute;
	}

	/**
	 * Setter for the eMail address of the root user.
	 * 
	 * @param rootUser the rootUser to set
	 */
	private void setRootUser(final InternetAddress rootUser) {
		this.rootUser = rootUser;
	}

	public void setStaticServerAddress(final String staticServerAddress) {
		this.staticServerAddress = staticServerAddress;
	}

	public void setStaticServerPort(final int staticServerPort) {
		this.staticServerPort = staticServerPort;
	}

	/**
	 * Setter for the supported {@link Principal}s.
	 * 
	 * @param supportedPrincipals the supported {@link Principal}s to set.
	 */
	public void setSupportedPrincipals(final List<Class<? extends Principal>> supportedPrincipals) {
		this.supportedPrincipals = supportedPrincipals;
	}

	/**
	 * @param useSSL the useSSL to set
	 */
	public void setUseSSL(final boolean useSSL) {
		this.useSSL = useSSL;
	}

	/**
	 * @param useSSLForHttpListener the useSSLForHttpListener to set
	 * @param pathToKeyStore        the {@link Path} to the keystore file
	 * @param keystorePassword      the password for the keystore
	 */
	public void setUseSSLForHttpListener(final boolean useSSLForHttpListener, final URL pathToKeyStore,
			final String keystorePassword) {
		this.useSSLForHttpListener = useSSLForHttpListener;

		this.setCertificatePathForHttpListener(pathToKeyStore);

		this.setKeystorePasswordForHttpListener(keystorePassword);
	}

	/**
	 * Setter to activate the usage of proxies.
	 * 
	 * @param useSystemProxies true if proxy should be used
	 */
	public void setUseSystemProxies(final boolean useSystemProxies) {
		this.useSystemProxies = useSystemProxies;
	}

	/**
	 * Validate the {@link EdalConfiguration} object.
	 * 
	 * @return true if validation was successful.
	 * @throws EdalConfigurationException if validation failed.
	 */
	private boolean validate() throws EdalConfigurationException {

		this.getMountPath();
		this.getDatabaseUsername();
		this.getDatabasePassword();
		this.getHttpPort();
		this.getHttpsPort();
		this.getSupportedPrincipals();

		this.getReviewerScientific();
		this.getReviewerSubstitute();
		this.getReviewerManaging();
		this.getRootUser();

		this.validateProxies();
		this.validateSmtpSettings();

		try {
			this.validateDataCiteConnection();
			this.validateDataCiteAuthentication();
			this.validateDateCiteRestAPI();
		} catch (final EdalConfigurationException e) {
			EdalConfiguration.logger
					.warn("DataCite validation failed, System is running in read-only Mode: " + e.getMessage());
			this.errorLogger
					.fatal("DataCite validation failed, System is running in read-only Mode: " + e.getMessage());
			this.setReadOnly(true);

		}
		return true;
	}

	/**
	 * Validate the given DataCite user name and password.
	 * 
	 * @return true if the validation was successful, otherwise false.
	 * @throws EdalConfigurationException if unable to validate the parameter.
	 */
	private boolean validateDataCiteAuthentication() throws EdalConfigurationException {

		if (!this.getDataCitePrefix().equals(EdalConfiguration.DATACITE_TESTPREFIX)) {

			this.getDataCiteUser();
			this.getDataCitePassword();
			this.getDataCitePrefix();

			String dataCitePassword = this.getDataCitePassword();

			if (dataCitePassword.startsWith("#")) {
				try {

					CodeSource codeSource = EdalConfiguration.class.getProtectionDomain().getCodeSource();
					File currentFile = new File(codeSource.getLocation().toURI().getPath());
					String currentPath = currentFile.getParentFile().getPath();

					Properties prop = new Properties();
					InputStream input = new FileInputStream(Paths.get(currentPath, "key.property").toFile());
					prop.load(input);
					input.close();

					dataCitePassword = dataCitePassword.substring(1, dataCitePassword.length());

					byte[] key = (prop.getProperty("key")).getBytes("UTF-8");
					MessageDigest sha = MessageDigest.getInstance("SHA-256");
					key = sha.digest(key);
					key = Arrays.copyOf(key, 16);
					SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

					byte[] crypted = Base64.getDecoder().decode(dataCitePassword);
					Cipher cipher = Cipher.getInstance("AES");
					cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
					this.setDataCitePassword(new String(cipher.doFinal(crypted)));

				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
						| IllegalBlockSizeException | BadPaddingException | URISyntaxException | IOException e) {

					EdalConfiguration.logger.warn("DataCite password decryption failed : " + e.getMessage());
				}
			}

			DataCiteMDSConnector connector;
			try {
				connector = new DataCiteMDSConnector(this);
			} catch (final EdalException e) {
				throw new EdalConfigurationException(
						"DataCite Authentification Test failed : unable to create DataCiteMDSConnector");
			}

			final Response response = connector.getDOI(this.getDataCitePrefix() + "/" + UUID.randomUUID().toString());

			if (response.getStatus() == 404 || response.getStatus() == 200) {
				EdalConfiguration.logger.info("DataCite Authentification Test: successful");

				try {
					DataCiteRestConnector restConnector = new DataCiteRestConnector(this);

					if (restConnector.checkIfPrefixIsRegisteredForDataCenterId()) {
						EdalConfiguration.logger.info("DataCite Prefix Test: successful");
					} else {
						throw new EdalConfigurationException(
								"DataCite Prefix Test failed : this given prefix is not registered for the given DataCite account");
					}

				} catch (DataCiteException e) {
					throw new EdalConfigurationException("DataCite Prefix Test failed : unable to check prefix");
				}

			}

			else if (response.getStatus() == 401) {
				throw new EdalConfigurationException(
						"DataCite Authentification failed: please check username and password");
			}

			else if (response.getStatus() == 500) {
				throw new EdalConfigurationException("DataCite Authentification failed: please check prefix");
			} else {
				throw new EdalConfigurationException("DataCite Authentification Test failed: " + response.getStatus()
						+ " : " + response.getStatusInfo().getReasonPhrase());
			}

			final List<String> landingPageDomainNames = this.requestRegisteredDataCiteDomainName();

			if (landingPageDomainNames != null) {

				EdalConfiguration.logger.info("DataCite Domain Check: " + landingPageDomainNames.toString());

				this.setAliasDomainNames(landingPageDomainNames);
			} else {
				throw new EdalConfigurationException("DataCite Domain Check failed: no registrated domain found");
			}
			return true;
		} else {

			this.setInTestMode(true);

			EdalConfiguration.logger
					.warn("DataCite Authentication : skiped (Publication-Module is running in test mode)");
			return true;
		}
	}

	/**
	 * Validate if the system if able to connect to DataCite with the given
	 * connection and proxy settings.
	 * 
	 * @return true if the validation was successful, otherwise false.
	 * 
	 * @throws EdalConfigurationException if unable to validate the parameter.
	 */
	private boolean validateDataCiteConnection() throws EdalConfigurationException {

		try {
			EdalConfiguration.logger.debug("connecting to DataCite...");

			Authenticator.setDefault(null);

			final URL url = new URL(EdalConfiguration.DATACITE_MDS_URL);

			// final URL url = new URL(null);

			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(EdalConfiguration.DATACITE_CONNECTION_TIMEOUT);
			connection.setReadTimeout(EdalConfiguration.DATACITE_CONNECTION_READ_TIMEOUT);

			connection.setRequestProperty("Authorization", null);

			connection.getResponseCode();

			EdalConfiguration.logger.info("DataCite Connection Test : successful");
			return true;
		} catch (final IOException e) {
			throw new EdalConfigurationException(
					"unable to access DataCite : " + e.getMessage() + ", please check your proxy settings", e);
		}
	}

	/**
	 * Validate if the REST DataCite server is available.
	 * 
	 * @throws EdalConfigurationException if unable to connect to REST server.
	 */
	private boolean validateDateCiteRestAPI() throws EdalConfigurationException {

		JerseyClient client = JerseyClientBuilder.createClient();

		WebTarget webResource = null;

		if (this.isInTestMode()) {
			webResource = client.target("https://api.datacite.org/prefixes/");
		} else {
			webResource = client.target("https://api.datacite.org/prefixes/" + this.dataCitePrefix);
		}

		final Response response = webResource.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == 200) {
			client.close();
			EdalConfiguration.logger.info("DataCite RestAPI Test : successful");
			return true;
		} else {
			client.close();
			EdalConfiguration.logger.error("DataCite RestAPI Test failed");
			throw new EdalConfigurationException(
					"unable to connect to DataCite RestAPI: " + response.readEntity(String.class));
		}

	}

	/**
	 * Validate the given proxy settings. If the user do not define any settings,
	 * the function try to read out the settings from the system.
	 * 
	 * 
	 * @return true if the validation was successful, otherwise false.
	 * @throws EdalConfigurationException if unable to validate the parameter.
	 */
	private boolean validateProxies() throws EdalConfigurationException {

		if ((this.httpProxyHost == null || this.httpProxyHost.isEmpty())
				&& (this.httpsProxyHost == null || this.httpsProxyHost.isEmpty())
				&& this.httpProxyPort == 0 & this.httpsProxyPort == 0) {

			EdalConfiguration.logger.info("No Proxy Settings configured");

			this.setUseSystemProxies(false);
		}

		if (this.isUseSystemProxies()) {

			System.setProperty("java.net.useSystemProxies", "true");

			try {
				this.getHttpProxyHost();
				System.setProperty("http.proxyHost", this.getHttpProxyHost());

			} catch (final EdalConfigurationException e) {
				EdalConfiguration.logger.error(EdalConfiguration.MSG_UNABLE_TO_SET_PROXY + e.getMessage());
			}

			try {
				this.getHttpProxyPort();
				System.setProperty("http.proxyPort", String.valueOf(this.getHttpProxyPort()));
			} catch (final EdalConfigurationException e) {
				EdalConfiguration.logger.error("unabale to set Proxy : " + e.getMessage());
			}

			try {
				this.getHttpsProxyHost();
				System.setProperty("https.proxyHost", this.getHttpsProxyHost());
			} catch (final EdalConfigurationException e) {
				EdalConfiguration.logger.error(EdalConfiguration.MSG_UNABLE_TO_SET_PROXY + e.getMessage());
			}
			try {
				this.getHttpsProxyPort();

				System.setProperty("https.proxyPort", String.valueOf(this.getHttpsProxyPort()));

			} catch (final EdalConfigurationException e) {
				EdalConfiguration.logger.error(EdalConfiguration.MSG_UNABLE_TO_SET_PROXY + e.getMessage());
			}
			EdalConfiguration.logger.info("apply manual proxy settings");
		}

		else {

			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");

			final List<Strategy> strategies = new ArrayList<Strategy>();

			if (PlatformUtil.getCurrentPlattform() == Platform.WIN) {
				strategies.add(Strategy.OS_DEFAULT);
				strategies.add(Strategy.WIN);
				strategies.add(Strategy.ENV_VAR);
				strategies.add(Strategy.JAVA);
				strategies.add(Strategy.BROWSER);
				strategies.add(Strategy.IE);
				strategies.add(Strategy.FIREFOX);
			} else if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
				strategies.add(Strategy.OS_DEFAULT);
				strategies.add(Strategy.ENV_VAR);
				strategies.add(Strategy.JAVA);
				strategies.add(Strategy.BROWSER);
				strategies.add(Strategy.KDE);
				strategies.add(Strategy.GNOME);
				strategies.add(Strategy.FIREFOX);
			} else if (PlatformUtil.getCurrentPlattform() == Platform.MAC_OS) {
				strategies.add(Strategy.OS_DEFAULT);
				strategies.add(Strategy.ENV_VAR);
				strategies.add(Strategy.JAVA);
				strategies.add(Strategy.BROWSER);
				strategies.add(Strategy.FIREFOX);
			} else {
				strategies.add(Strategy.OS_DEFAULT);
			}

			/**
			 * Note: do not override the default ProxSelector
			 * ProxySelector.setDefault(proxySearch.getProxySelector());
			 * 
			 * If the ProxySelector return null, all SearchStrategies failed
			 */

			ProxySearch proxySearch = null;

			boolean findProxySettings = false;

			for (final Strategy strategy : strategies) {

				proxySearch = new ProxySearch();
				proxySearch.addStrategy(strategy);

				try {
					final List<Proxy> proxyList = proxySearch.getProxySelector()
							.select(new URI(EdalConfiguration.DATACITE_MDS_URL));

					if (proxyList != null) {
						for (final Proxy proxy2 : proxyList) {
							final Proxy proxy = proxy2;
							if (proxy.type().equals(Type.HTTP)) {
								final InetSocketAddress address = (InetSocketAddress) proxy.address();

								if (address != null) {

									System.setProperty("http.proxyHost", address.getHostName());
									System.setProperty("https.proxyHost", address.getHostName());
									System.setProperty("http.proxyPort", Integer.toString(address.getPort()));
									System.setProperty("https.proxyPort", Integer.toString(address.getPort()));
									EdalConfiguration.logger.info(
											"Found HTTP Proxy : " + address.getHostName() + ":" + address.getPort());
									/**
									 * set the found proxy settings to the configuration for later usage
									 */
									this.setHttpProxyHost(address.getHostName());
									this.setHttpProxyPort(address.getPort());
									this.setHttpsProxyHost(address.getHostName());
									this.setHttpsProxyPort(address.getPort());
									findProxySettings = true;
								}
							}
						}
					}

				} catch (URISyntaxException | NullPointerException e) {
					EdalConfiguration.logger.debug("No Proxy Settings found for Strategy " + strategy);
				}
				if (findProxySettings) {
					EdalConfiguration.logger.info("Proxy Settings determined automatically for Strategy : " + strategy);
					break;
				}
			}
			if (!findProxySettings) {
				EdalConfiguration.logger.info("No automatic Proxy Settings found");
			}
		}
		return true;
	}

	/**
	 * Validate the given SMTP settings. If the user do not define any parameter,
	 * the function try to find out the parameter from the system.
	 * 
	 * @return true if the validation was successful, otherwise false.
	 * @throws EdalConfigurationException if unable to validate the parameter.
	 */
	private boolean validateSmtpSettings() throws EdalConfigurationException {

		if (!this.getMailSmtpHost().isEmpty()) {

			final Properties properties = new Properties();
			properties.put("mail.smtp.host", this.getMailSmtpHost());
			properties.put("mail.smtp.connectiontimeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
			properties.put("mail.smtp.timeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
			// properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.trust", this.getMailSmtpHost());

			final Session session = Session.getDefaultInstance(properties);

			if (this.getMailSmtpLogin() == null || this.getMailSmtpLogin().isEmpty()) {

				try {
					final Transport transport = session.getTransport("smtp");

					transport.connect(this.getMailSmtpHost(), null, null);

					transport.close();

					EdalConfiguration.logger.info("SMTP Connection Test -> successful");

					this.setEdalEmailAddress(NOREPLY_EMAIL_DEFAULT);

				} catch (final MessagingException e) {
					EdalConfiguration.logger.warn("SMTP Connection Test -> failed : " + e.getMessage());
					throw new EdalConfigurationException(
							"unable to connect to eMail Server, check SMTP settings : " + e.getMessage());
				}
				return true;
			} else {
				try {
					final Transport transport = session.getTransport("smtp");

					transport.connect(this.getMailSmtpHost(), this.getMailSmtpLogin(), this.getMailSmtpPassword());

					transport.close();

					EdalConfiguration.logger.info("SMTP Connection Test -> successful");

					this.setEdalEmailAddress(NOREPLY_EMAIL_DEFAULT);

				} catch (final MessagingException e) {
					EdalConfiguration.logger.warn("SMTP Connection Test -> failed : " + e.getMessage());
					throw new EdalConfigurationException(
							"unable to connect to eMail Server, check SMTP settings : " + e.getMessage());
				}
				return true;
			}
		} else {

			for (String mailServerNames : EdalConfiguration.MAIL_SERVER_NAMES) {

				try {
					if (!System.getenv("userdnsdomain").isEmpty()) {
						mailServerNames = mailServerNames + "."
								+ System.getenv("userdnsdomain").toLowerCase(Locale.ENGLISH);

						/**
						 * generate dynamic email reply address using the mail server domain
						 */
						this.setEdalEmailAddress(this.getEdalEmailAddress() + "@"
								+ System.getenv("userdnsdomain").toLowerCase(Locale.ENGLISH));

					}
				} catch (final NullPointerException e) {
					try {
						if (InetAddress.getLocalHost().getCanonicalHostName().contains(".")) {

							String domain = InetAddress.getLocalHost().getCanonicalHostName().substring(
									InetAddress.getLocalHost().getCanonicalHostName().indexOf(".") + 1,
									InetAddress.getLocalHost().getCanonicalHostName().length());

							mailServerNames = mailServerNames + "." + domain.toLowerCase(Locale.ENGLISH);

							/**
							 * generate dynamic email reply address using the mail server domain
							 */
							this.setEdalEmailAddress(
									this.getEdalEmailAddress() + "@" + domain.toLowerCase(Locale.ENGLISH));
						} else {
							/**
							 * set default reply email when not automatic detection failed
							 */
							this.setEdalEmailAddress(NOREPLY_EMAIL_DEFAULT);
						}
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					}
				}

				final Properties properties = new Properties();
				properties.put("mail.smtp.host", mailServerNames);
				properties.put("mail.smtp.connectiontimeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
				properties.put("mail.smtp.timeout", EdalConfiguration.SMTP_CONNECTION_TIMEOUT);
				// properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.smtp.ssl.trust", mailServerNames);

				final Session session = Session.getDefaultInstance(properties);

				try {
					if (!this.getMailSmtpLogin().isEmpty() && !this.getMailSmtpPassword().isEmpty()) {
						final Transport transport = session.getTransport("smtp");

						transport.connect(this.getMailSmtpLogin(), this.getMailSmtpPassword());

						transport.close();

					} else {
						final Transport transport = session.getTransport("smtp");

						transport.connect();

						transport.close();
					}
					final Logger edalLogger = LogManager.getLogger("EDAL_SMTP_ERROR_APPENDER");

					final SmtpAppender smtp = SmtpAppender.createAppender(null, "SMTP-APP",
							this.getErrorEmailAddress().getAddress(), null, null, this.getEdalEmailAddress(), null,
							"[eDAL ERROR]", null, mailServerNames, "0", this.getMailSmtpLogin(),
							this.getMailSmtpPassword(), "SMTP_DEBUG", "512", null, null, null);

					((org.apache.logging.log4j.core.Logger) edalLogger).addAppender(smtp);

					this.setErrorLogger(edalLogger);

					this.setMailSmtpHost(mailServerNames);

					EdalConfiguration.logger.info("SMTP Connection Test: " + mailServerNames + " : successful");
					EdalConfiguration.logger.info("e!DAL-eMail-Address: " + this.getEdalEmailAddress());

					return true;
				} catch (final MessagingException e) {
					EdalConfiguration.logger
							.warn("SMTP Connection Test: " + mailServerNames + " -> failed : " + e.getMessage());
				}
			}
			throw new EdalConfigurationException("unable to connect to eMail Server, check SMTP settings");
		}
	}

	public boolean isCleanBrokenEntities() {
		return cleanBrokenEntities;
	}

	public void setCleanBrokenEntities(boolean cleanBrokenEntities) {
		this.cleanBrokenEntities = cleanBrokenEntities;
	}

	public String getInstanceNameLong() {
		return instanceNameLong;
	}

	public void setInstanceNameLong(String instanceNameLong) {
		this.instanceNameLong = instanceNameLong;
	}

	public String getInstanceNameShort() {
		return instanceNameShort;
	}

	public void setInstanceNameShort(String instanceNameShort) {
		this.instanceNameShort = instanceNameShort;
	}

	public String getPublisherString() {
		return this.publisherString;
	}

	public void setPublisherString(String publisherString) {
		this.publisherString = publisherString;
	}

	public String getPublisherURL() {
		return this.publisherURL;
	}

	public void setPublisherURL(String publisherURL) {
		this.publisherURL = publisherURL;
	}

	public void setCustomReplyEmail(String customReplyEmail) {
		this.setEdalEmailAddress(customReplyEmail);
	}

}