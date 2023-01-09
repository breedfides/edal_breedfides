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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.List;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

public class EdalHttpServer {

	public static final String EDAL_PATH_SEPARATOR = "/";

	private static URL url = null;
	private static URL httpDownloadUrl = null;

	private boolean useSSL = false;

	private Server eDALServer = null;

	private String domainNameToUse = null;

	private EdalConfiguration configuration = null;

	private HandlerCollection handlerCollection = new HandlerCollection(true);

	protected EdalHttpServer(final EdalConfiguration configuration) {

		this.configuration = configuration;

		if (configuration.getStaticServerAddress() == null) {
			try {
				if (configuration.isReadOnly()) {
					domainNameToUse = InetAddress.getLocalHost().getCanonicalHostName();
				} else {
					if (configuration.isInTestMode()) {
						domainNameToUse = InetAddress.getLocalHost().getCanonicalHostName();
					} else {
						checkIfDomainIsRegisteredByDataCite(InetAddress.getLocalHost(),
								configuration.getAliasDomainNames());
					}
				}
			} catch (UnknownHostException e) {
				DataManager.getImplProv().getLogger().error("Unable to validate registered domain names");
				System.exit(0);
			}
		} else {

			if (configuration.isReadOnly()) {
				domainNameToUse = configuration.getStaticServerAddress();
			} else {
				if (configuration.isInTestMode()) {
					domainNameToUse = configuration.getStaticServerAddress();
				} else {
					checkIfStaticServerIsRegisteredByDataCite(configuration.getStaticServerAddress(),
							configuration.getAliasDomainNames());
				}
			}

		}

		this.useSSL = configuration.isUseSSLForHttpListener();

		this.eDALServer = new Server();

		if (!Files.exists(
				Paths.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "jetty_log"))) {
			try {
				Files.createDirectories(
						Paths.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "jetty_log"));
			} catch (IOException e) {
				DataManager.getImplProv().getLogger().error("Unable to create jetty log directory " + e.getMessage());
			}
		}

		EdalRequestLog requestLog = new EdalRequestLog(
				Paths.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "jetty_log",
						"jetty-yyyy_mm_dd.request.log").toString());
		requestLog.setRetainDays(Integer.MAX_VALUE);
		requestLog.setAppend(true);
		requestLog.setExtended(true);
		requestLog.setLogServer(true);
		requestLog.setPreferProxiedForAddress(true);
		requestLog.setLogTimeZone("GMT+1");
		requestLog.setIgnorePaths(
				new String[] { "/" + EdalHttpFunctions.CSS.name() + "/*", "/" + EdalHttpFunctions.LOGO.name() + "/*",
						"/" + EdalHttpFunctions.JS.name() + "/*", "/" + EdalHttpFunctions.ACCEPT.name() + "/*",
						"/" + EdalHttpFunctions.REJECT.name() + "/*", "/" + EdalHttpFunctions.USER_ACCEPT.name() + "/*",
						"/" + EdalHttpFunctions.USER_REJECT.name() + "/*", "/" + EdalHttpFunctions.LOGIN.name() + "/*",
						"/favicon.ico", "/index.htm", "/robots.txt", "/Report/*", "/REPORT/*", "/report/*" });
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);

		ContextHandler edalContextHandler = new ContextHandler("/");
		edalContextHandler.setHandler(new EdalHttpHandler());

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setResourceBase(".");


		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		restHandler.setContextPath("/rest");
		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		
		jerseyServlet.setInitOrder(2);
		
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames","de.ipk_gatersleben.bit.bi.edal.primary_data.EdalSearchRestEndpoints,org.glassfish.jersey.media.multipart.MultiPartFeature,de.ipk_gatersleben.bit.bi.edal.primary_data.CORSFilter");

//		ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
//		contextHandlerCollection.addHandler(edalContextHandler);
//		contextHandlerCollection.addHandler(restHandler);
//		contextHandlerCollection.addHandler(resourceHandler);
//		contextHandlerCollection.addHandler(requestLogHandler);
		

		handlerCollection.addHandler(restHandler);
		handlerCollection.addHandler(edalContextHandler);
		handlerCollection.addHandler(resourceHandler);
		handlerCollection.addHandler(requestLogHandler);
		
		ServletContextHandler submissionRestHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		submissionRestHandler.setContextPath("/restfull");
		ServletHolder submissionJerseyServlet = submissionRestHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		submissionJerseyServlet.setInitOrder(0);
		submissionJerseyServlet.setInitParameter("jersey.config.server.provider.packages",
				"de.ipk_gatersleben.bit.bi.edal.rest.server");
		//submissionJerseyServlet.setInitParameter("javax.ws.rs.Application", "de.ipk_gatersleben.bit.bi.edal.primary_data.CustomApplication");
		submissionJerseyServlet.setInitParameter("jersey.config.server.provider.classnames","org.glassfish.jersey.media.multipart.MultiPartFeature");
		handlerCollection.prependHandler(submissionRestHandler);
//		collection.addHandler(restHandler);
//		handlerCollection.addHandler(resourceHandler);
//
//		handlerCollection.addHandler(edalContextHandler);
//
//		handlerCollection.addHandler(contextHandlerCollection);
//
//		handlerCollection.addHandler(requestLogHandler);
		/*
		 * SSLServerSocketFactory ssf = (SSLServerSocketFactory)
		 * SSLServerSocketFactory.getDefault();
		 * 
		 * String[] defaultCiphers = ssf.getDefaultCipherSuites(); String[]
		 * availableCiphers = ssf.getSupportedCipherSuites();
		 * 
		 * TreeMap ciphers = new TreeMap();
		 * 
		 * for (int i = 0; i < availableCiphers.length; ++i)
		 * ciphers.put(availableCiphers[i], Boolean.FALSE);
		 * 
		 * for (int i = 0; i < defaultCiphers.length; ++i)
		 * ciphers.put(defaultCiphers[i], Boolean.TRUE);
		 * 
		 * System.out.println("Default\tCipher"); for (Iterator i =
		 * ciphers.entrySet().iterator(); i.hasNext();) { Map.Entry cipher = (Map.Entry)
		 * i.next();
		 * 
		 * if (Boolean.TRUE.equals(cipher.getValue())) System.out.print('*'); else
		 * System.out.print(' ');
		 * 
		 * System.out.print('\t'); System.out.println(cipher.getKey()); }
		 */

		if (this.useSSL) {

			try {

				SslContextFactory sslContextFactory = new SslContextFactory.Server();

				KeyStore keystore = null;
				try {
					keystore = KeyStore.getInstance("JKS");
					keystore.load(new FileInputStream(configuration.getCertificatePathForHttpListener().getPath()),
							configuration.getKeystorePasswordForHttpListener().toCharArray());
				} catch (GeneralSecurityException | IOException e) {
					DataManager.getImplProv().getLogger().error("Unable to load/open keystore : " + e.getMessage());
				}

				sslContextFactory.setKeyStore(keystore);
				sslContextFactory.setKeyStorePassword(configuration.getKeystorePasswordForHttpListener());
				sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);

				// HTTP Configuration
				HttpConfiguration httpConfig = new HttpConfiguration();
				httpConfig.setSecureScheme("https");
				httpConfig.setSecurePort(configuration.getHttpsPort());
				httpConfig.setSendXPoweredBy(true);
				httpConfig.setSendServerVersion(true);

				// HTTP Connector
				ServerConnector httpConnector = new ServerConnector(this.eDALServer,
						new HttpConnectionFactory(httpConfig), new HTTP2CServerConnectionFactory(httpConfig));
				httpConnector.setPort(configuration.getHttpPort());
				this.eDALServer.addConnector(httpConnector);

				// HTTPS Configuration
				HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
				httpsConfig.addCustomizer(new SecureRequestCustomizer());

				// HTTP2 Connection Factory
				HTTP2ServerConnectionFactory http2ConnectionFactory = new HTTP2ServerConnectionFactory(httpsConfig);

				ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
				alpn.setDefaultProtocol(httpConnector.getDefaultProtocol());

				// SSL Connection Factory
				SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory,
						alpn.getProtocol());

				// HTTP2 Connector
				ServerConnector http2Connector = new ServerConnector(this.eDALServer, sslConnectionFactory, alpn,
						http2ConnectionFactory, new HttpConnectionFactory(httpsConfig));
				http2Connector.setPort(configuration.getHttpsPort());
				this.eDALServer.addConnector(http2Connector);

				this.eDALServer.setHandler(handlerCollection);

				if (configuration.getStaticServerAddress() != null) {

					URI domainNameTuUseUri = new URI("https://" + domainNameToUse);

					this.setURL(new URL("https://" + domainNameTuUseUri.getHost() + ":"
							+ configuration.getStaticServerPort() + domainNameTuUseUri.getPath()));
					this.setHttpDownloadURL(new URL("https://" + domainNameTuUseUri.getHost() + ":"
							+ configuration.getStaticServerPort() + domainNameTuUseUri.getPath()));

				} else {
					this.setURL(new URL("https://" + domainNameToUse + ":" + configuration.getHttpsPort()));
					this.setHttpDownloadURL(new URL("https://" + domainNameToUse + ":" + configuration.getHttpsPort()));
				}

			} catch (MalformedURLException | EdalConfigurationException | URISyntaxException e) {
				DataManager.getImplProv().getLogger().error("Unable to initialize HTTPS-server : " + e.getMessage());
			}

		} else {

			ServerConnector connector = new ServerConnector(this.eDALServer);
			connector.setIdleTimeout(600000);

			try {
				connector.setPort(configuration.getHttpPort());
				this.eDALServer.setConnectors(new Connector[] { connector });
				this.eDALServer.setHandler(handlerCollection);

				if (configuration.getStaticServerAddress() != null) {

					URI domainNameTuUseUri = new URI("http://" + domainNameToUse);

					this.setURL(new URL("http://" + domainNameTuUseUri.getHost() + ":"
							+ configuration.getStaticServerPort() + domainNameTuUseUri.getPath()));
					this.setHttpDownloadURL(new URL("http://" + domainNameTuUseUri.getHost() + ":"
							+ configuration.getStaticServerPort() + domainNameTuUseUri.getPath()));

				} else {
					this.setURL(new URL("http://" + domainNameToUse + ":" + configuration.getHttpPort()));
					this.setHttpDownloadURL(new URL("http://" + domainNameToUse + ":" + configuration.getHttpPort()));
				}

			} catch (MalformedURLException | EdalConfigurationException | URISyntaxException e) {
				DataManager.getImplProv().getLogger().error("Unable to initialize HTTP-server : " + e.getMessage());
			}
		}
	}

	private boolean checkIfDomainIsRegisteredByDataCite(InetAddress localhost, List<String> domainNames) {

		try {
			for (String domain : domainNames) {
				if (checkIfLocalhostIsInDomain(localhost, domain) || checkIfLocalhostHasSubDomain(localhost, domain)) {
					domainNameToUse = localhost.getCanonicalHostName();
					return true;
				} else if (checkIfDomainIsAliasForLocalhost(localhost, domain)) {
					domainNameToUse = domain;
					return true;
				}
			}
			DataManager.getImplProv().getLogger().error("e!DAL is running on '" + localhost.getCanonicalHostName()
					+ "' and not one of your registrated DataCite Domain(s) '" + domainNames + "'");
			System.exit(0);
		}

		catch (UnknownHostException e) {
			DataManager.getImplProv().getLogger().error("Unable to validate registered domain names");
			System.exit(0);
		}
		System.exit(0);
		return false;

	}

	private boolean checkIfStaticServerIsRegisteredByDataCite(String host, List<String> domainNames) {

		try {
			for (String domain : domainNames) {
				if (checkIfLocalhostIsInDomain(host, domain) || checkIfLocalhostHasSubDomain(host, domain)) {
					domainNameToUse = host;
					return true;
				} else if (checkIfDomainIsAliasForLocalhost(host, domain)) {
					domainNameToUse = domain;
					return true;
				}
			}
			DataManager.getImplProv().getLogger().error("e!DAL is running on '" + host
					+ "' and not one of your registrated DataCite Domain(s) '" + domainNames + "'");
			System.exit(0);
		}

		catch (UnknownHostException e) {
			DataManager.getImplProv().getLogger().error("Unable to validate registered domain names");
			System.exit(0);
		}
		System.exit(0);
		return false;

	}

	public static boolean checkIfLocalhostIsInDomain(InetAddress localhost, String domain) {
		if (localhost.getCanonicalHostName().endsWith(domain) || domain.equals("*")) {
			return true;
		}
		return false;
	}

	public static boolean checkIfDomainIsAliasForLocalhost(InetAddress localhost, String domain)
			throws UnknownHostException {

		if (localhost.getCanonicalHostName().endsWith(InetAddress.getByName(domain).getCanonicalHostName())) {
			return true;
		}
		return false;
	}

	public static boolean checkIfLocalhostHasSubDomain(InetAddress localhost, String domain)
			throws UnknownHostException {

		if (localhost.getCanonicalHostName().endsWith(domain)) {
			return true;
		}
		return false;
	}

	public static boolean checkIfLocalhostIsInDomain(String localhost, String domain) {
		if (localhost.endsWith(domain) || domain.equals("*")) {
			return true;
		}
		return false;
	}

	public static boolean checkIfDomainIsAliasForLocalhost(String localhost, String domain)
			throws UnknownHostException {

		if (localhost.endsWith(InetAddress.getByName(domain).getCanonicalHostName())) {
			return true;
		}
		return false;
	}

	public static boolean checkIfLocalhostHasSubDomain(String localhost, String domain) throws UnknownHostException {

		if (localhost.endsWith(domain)) {
			return true;
		}
		return false;
	}

	/**
	 * Start the eDAL {@link org.eclipse.jetty.server.Server}.
	 */
	protected void start() {
		try {
			this.eDALServer.start();
			if (this.useSSL) {
				DataManager.getImplProv().getLogger()
						.info("HTTPS-Server is listening on : " + EdalHttpServer.getServerURL());
			} else {
				DataManager.getImplProv().getLogger()
						.info("HTTP-Server is listening on : " + EdalHttpServer.getServerURL());
			}
		} catch (Exception e) {
			if (this.useSSL) {
				DataManager.getImplProv().getLogger().error("Unable to start HTTPS Server : " + e.getMessage());
				if (e.getClass().equals(SocketException.class) && e.getMessage().equals("Permission denied")) {
					try {
						DataManager.getImplProv().getLogger().warn("Unable to bind HTTPS Server to port "
								+ this.configuration.getHttpPort() + ". Try to run again with root permission.");
					} catch (EdalConfigurationException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				DataManager.getImplProv().getLogger().error("Unable to start HTTP Server : " + e.getMessage());
				if (e.getClass().equals(SocketException.class) && e.getMessage().equals("Permission denied")) {
					try {
						DataManager.getImplProv().getLogger().warn("Unable to bind HTTPS Server to port "
								+ this.configuration.getHttpPort() + ". Try to run again with root permission.");
					} catch (EdalConfigurationException e1) {
						e1.printStackTrace();
					}
				}
			}
			System.exit(0);
		}
	}

	/**
	 * Stop the eDAL {@link org.eclipse.jetty.server.Server}.
	 */
	protected void stop() {

		try {
			this.eDALServer.stop();
		} catch (Exception e) {
			if (this.useSSL) {
				DataManager.getImplProv().getLogger().error("Unable to stop HTTPS-Server : " + e.getMessage());
			} else {
				DataManager.getImplProv().getLogger().error("Unable to stop HTTP-Server : " + e.getMessage());
			}
		}
	}

	/**
	 * internal setter for the {@link URL} of the HTTP server.
	 * 
	 * @param url the {@link URL} to set.
	 */
	private void setURL(URL url) {
		EdalHttpServer.url = url;
	}

	/**
	 * internal setter for the {@link URL} of the HTTP downloads.
	 * 
	 * @param url the {@link URL} to set.
	 */
	private void setHttpDownloadURL(URL url) {
		EdalHttpServer.httpDownloadUrl = url;
	}

	/**
	 * Get the {@link URL} of the HTTP server.
	 * 
	 * @return the {@link URL} of the HTTP server.
	 * @throws EdalException if no HTTP server was started.
	 */
	public static URL getServerURL() throws EdalException {
		if (EdalHttpServer.url != null) {
			return EdalHttpServer.url;
		} else {
			throw new EdalException("no eDAL HTTP server started");
		}
	}

	/**
	 * Get the {@link URL} for the HTTP downloads.
	 * 
	 * @return the {@link URL} of the HTTP server.
	 * @throws EdalException if no HTTP server was started.
	 */
	public static URL getHttpDownloadURL() throws EdalException {
		if (EdalHttpServer.httpDownloadUrl != null) {
			return EdalHttpServer.httpDownloadUrl;
		} else {
			throw new EdalException("no eDAL HTTP server started");
		}
	}

	/**
	 * Generate an {@link URL} to a ticket for the given method.
	 * 
	 * @param ticket       the ticket to the method.
	 * @param reviewerCode the code to identify the reviewer.
	 * @param method       the method for this {@link URL}.
	 * @return the URL to accept the
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 *         .
	 * @throws EdalException if unable to generate an URL.
	 */
	public static URL generateMethodURL(String ticket, int reviewerCode, EdalHttpFunctions method)
			throws EdalException {

		try {
			URI uri = new URI(getServerURL().toString());

			URL methodURL = new URL(uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + uri.getPath()
					+ EdalHttpServer.EDAL_PATH_SEPARATOR + method + EdalHttpServer.EDAL_PATH_SEPARATOR + ticket
					+ EdalHttpServer.EDAL_PATH_SEPARATOR + reviewerCode);

			return methodURL;
		} catch (URISyntaxException | MalformedURLException e) {
			throw new EdalException("unable to generate URL for " + method, e);
		}
	}

	/**
	 * Generate an {@link URL} to access a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity} as
	 * reviewer over a temporal landing page.
	 * 
	 * @param entityURL     the normal {@link URL} to this
	 *                      {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *                      .
	 * @param reviewersCode the code to identify the reviewer
	 * @return the {@link URL} to access the landing page for the reviewer.
	 * @throws EdalException if unable to generate reviewer {@link URL}
	 */
	public static URL generateReviewerURL(URL entityURL, int reviewersCode) throws EdalException {

		try {
			URL reviewerUrl = new URL(entityURL.toString() + EdalHttpServer.EDAL_PATH_SEPARATOR + reviewersCode);
			return reviewerUrl;
		} catch (MalformedURLException e) {
			throw new EdalException("unable to generate ReviewerURL", e);
		}
	}

	public HandlerCollection getHandlers() {
		return this.handlerCollection;
	}
}
