/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.server.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

/**
 * eDAL implementation for the {@link SslRMIClientSocketFactory} using a
 * constant port.
 * 
 * @author arendd
 */
public class EdalSslRmiClientSocketFactory extends SslRMIClientSocketFactory {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_RMI_SOCKET_PORT = 1098;
	private URL path2KeyStore;

	/** the fixed RMI data port to be used */
	private int rmiDataPort;
	private static final Logger log;

	static {
		log = LogManager.getLogger(EdalSslRmiClientSocketFactory.class.getSimpleName());
	}

	public EdalSslRmiClientSocketFactory(int port, URL path2KeyStore) {
		super();
		this.rmiDataPort = port;
		this.path2KeyStore = path2KeyStore;
	}

	public EdalSslRmiClientSocketFactory(URL path2KeyStore) {
		this(DEFAULT_RMI_SOCKET_PORT, path2KeyStore);
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {

		log.debug("Creating SSL client socket");

		if (port == 0) {
			return getSocket(host, rmiDataPort);
		} else {
			return getSocket(host, port);
		}
	}

	private SSLSocket getSocket(String host, int port) throws IOException {

		KeyStore keyStore = null;
		String keyStoreType = KeyStore.getDefaultType();
		char[] keyStorePassword = EdalConfiguration.KEYSTORE_PASSWORD.toCharArray();
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
		} catch (KeyStoreException e) {
			throw new IOException(e);
		}

		boolean isLoadingFromResourceFile = false;

		try {
			InputStream keyStoreStream = null;
			if (isLoadingFromResourceFile) {
				log.debug("loading keystore from resource");
				keyStoreStream = path2KeyStore.openStream();

			} else {
				log.debug("loading keystore from file: " + path2KeyStore);
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
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, keyStorePassword);
		} catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
			throw new IOException(e);
		}

		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			throw new IOException(e);
		}

		try {
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (KeyManagementException e) {
			throw new IOException(e);

		}
		final SocketFactory sslSocketFactory = ctx.getSocketFactory();

		final SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
		return sslSocket;
	}
}
