/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.server.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

/**
 * eDAL implementation for the {@link SslRMIServerSocketFactory} using a
 * constant port.
 * 
 * @author arendd
 */
public class EdalSslRmiServerSocketFactory extends SslRMIServerSocketFactory {

	private static final int DEFAULT_RMI_SOCKET_PORT = 1098;
	private URL path2KeyStore;

	/** the fixed RMI data port to be used */
	private int rmiDataPort;
	private static final Logger log;

	static {
		log = LogManager.getLogger(EdalSslRmiServerSocketFactory.class
				.getSimpleName());
	}

	public EdalSslRmiServerSocketFactory(int port, URL path2KeyStore) {
		super();
		this.rmiDataPort = port;
		this.path2KeyStore = path2KeyStore;
	}

	public EdalSslRmiServerSocketFactory(URL path2KeyStore) {
		this(DEFAULT_RMI_SOCKET_PORT, path2KeyStore);
	}

	public EdalSslRmiServerSocketFactory(URL path2KeyStore,
			String[] enabledCipherSuites, String[] enabledProtocols,
			boolean needClientAuth) {
		super(enabledCipherSuites, enabledProtocols, needClientAuth);

		this.rmiDataPort = DEFAULT_RMI_SOCKET_PORT;
		this.path2KeyStore = path2KeyStore;
	}

	/** {@inheritDoc} */
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		log.debug("Creating SSL server socket");
		if (port == 0) {
			return getServerSocket(this.rmiDataPort);
		} else {
			return getServerSocket(port);
		}
	}

	private SSLServerSocket getServerSocket(int port) throws IOException {

		SSLServerSocketFactory sslServerSocketFactory = null;
		SSLServerSocket sslServerSocket = null;

		KeyStore keyStore = null;
		String keyStoreType = KeyStore.getDefaultType();
		char[] keyStorePassword = EdalConfiguration.KEYSTORE_PASSWORD
				.toCharArray();
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
		} catch (KeyStoreException e) {
			throw new IOException(e);
		}
		boolean isLoadingFromResourceFile = false;
		try {
			InputStream keyStoreStream = null;
			if (isLoadingFromResourceFile) {
				keyStoreStream = path2KeyStore.openStream();
			} else {
				keyStoreStream = path2KeyStore.openStream();
			}

			keyStore.load(keyStoreStream, keyStorePassword);
		} catch (IOException | NoSuchAlgorithmException | CertificateException e) {
			throw new IOException(e);
		}

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}

		try {
			kmf.init(keyStore, keyStorePassword);
		} catch (NoSuchAlgorithmException | UnrecoverableKeyException
				| KeyStoreException e) {
			throw new IOException(e);
		}

		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(keyStore);
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			throw new IOException(e);
		}

		try {
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			sslServerSocketFactory = ctx.getServerSocketFactory();
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory
					.createServerSocket(port);
		} catch (KeyManagementException e) {
			throw new IOException(e);
		}

		return sslServerSocket;
	}
}
