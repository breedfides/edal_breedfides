/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.server.RMISocketFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @deprecated
 * @author colmsee
 */
public class EdalSslRmiSocketFactory extends RMISocketFactory {

	private static final int DEFAULT_RMI_SOCKET_PORT = 1098;
	private URL path2KeyStore;

	/** the fixed RMI data port to be used */
	private int rmiDataPort;
	private static final Logger log;

	static {
		PropertyConfigurator.configure(EdalServer.class.getResource("log4j.properties"));
		log = Logger.getLogger(EdalSslRmiSocketFactory.class.getSimpleName());
	}

	public EdalSslRmiSocketFactory(int port, URL path2KeyStore) {
		super();
		this.rmiDataPort = port;
		this.path2KeyStore = path2KeyStore;
	}

	public EdalSslRmiSocketFactory(URL path2KeyStore) {
		this(DEFAULT_RMI_SOCKET_PORT, path2KeyStore);
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {

		log.info("Creating SSL Server socket...");

		SSLServerSocket socket = null;

		if (port == 0) {
			socket = getServerSocket(rmiDataPort);
			log.info("SSL server socket created!");
			return socket;
		} else {
			socket = getServerSocket(port);
			log.info("SSL server socket created!");
			return socket;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {

		log.info("Creating SSL client socket...");

		SSLSocket socket = null;

		if (port == 0) {
			socket = getSocket(host, rmiDataPort);
			log.info("SSL client socket created!");
			return socket;
		} else {
			socket = getSocket(host, port);
			log.info("SSL client socket created!");
			return socket;

		}

	}

	private SSLServerSocket getServerSocket(int port) {
		SSLServerSocketFactory sslServerSocketFactory = null;
		SSLServerSocket sslServerSocket = null;

		KeyStore keyStore = null;
		String ksttyp = "jks";
		char[] kstpwd = "daniel".toCharArray();
		try {
			keyStore = KeyStore.getInstance(ksttyp);
		} catch (KeyStoreException e1) {
			log.error("keystore problem " + e1.getMessage(), e1);
		}
		boolean isLoadingFromResourceFile = false;
		try {
			InputStream keyStoreStream = null;
			if (isLoadingFromResourceFile) {
				keyStoreStream = path2KeyStore.openStream();
			} else {
				keyStoreStream = path2KeyStore.openStream();
			}

			keyStore.load(keyStoreStream, kstpwd);
		} catch (NoSuchAlgorithmException e2) {
			log.error("keystore load: no such algo " + e2.getMessage(), e2);
		} catch (CertificateException e3) {
			log.error("keystore load: certificate problem " + e3.getMessage(),
					e3);
		} catch (FileNotFoundException e4) {
			log.error("keystore load: file <" + path2KeyStore + "> not found",
					e4);
		} catch (IOException e5) {
			log.error("keystore load: io problem: " + e5.getMessage(), e5);
		}

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e6) {
			log.error("ctx: no such algo " + e6.getMessage(), e6);
		}
		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e7) {
			log.error("kmf: no such algo " + e7.getMessage(), e7);
		}

		try {
			kmf.init(keyStore, kstpwd);
		} catch (UnrecoverableKeyException e12) {
			log.error("kmf init: key problem " + e12.getMessage(), e12);
		} catch (KeyStoreException e13) {
			log.error("kmf init: keystore problem " + e13.getMessage(), e13);
		} catch (NoSuchAlgorithmException e14) {
			log.error("kmf init: no such algo " + e14.getMessage(), e14);
		}

		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException ex) {
			log.error("tmf: no such algo " + ex.getMessage(), ex);
		}
		try {
			tmf.init(keyStore);
		} catch (KeyStoreException ex) {
			log.error("tmf init: keystore problem " + ex.getMessage(), ex);
		}

		try {
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (KeyManagementException e15) {
			log.error("ctx init: key problem " + e15.getMessage(), e15);
		}
		sslServerSocketFactory = ctx.getServerSocketFactory();

		try {
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory
					.createServerSocket(port);
		} catch (IOException e16) {
			log.error("socket: io problem " + e16.getMessage(), e16);
		}
		return sslServerSocket;
	}

	private SSLSocket getSocket(String host, int port) {

		KeyStore keyStore = null;
		String ksttyp = "jks";
		char[] kstpwd = "daniel".toCharArray();
		try {
			keyStore = KeyStore.getInstance(ksttyp);
		} catch (KeyStoreException e) {
			log.error("Cant instanciate keystore " + e.getMessage(), e);
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

			keyStore.load(keyStoreStream, kstpwd);
		} catch (NoSuchAlgorithmException e1) {
			log.error("Algorithm not foud " + e1.getMessage(), e1);
		} catch (CertificateException e2) {
			log.error("certificate problem " + e2.getMessage(), e2);
		} catch (FileNotFoundException e3) {
			log.error("keystore file " + path2KeyStore + "not found", e3);
		} catch (IOException e4) {
			log.error("io problem " + e4.getMessage(), e4);
		}

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e5) {
			log.error("ctx Algorithm not found " + e5.getMessage(), e5);
		}
		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e6) {
			log.error("kmf Algorithm not found " + e6.getMessage(), e6);
		}

		try {
			kmf.init(keyStore, kstpwd);
		} catch (UnrecoverableKeyException e11) {
			log.error("init: key problem " + e11.getMessage(), e11);
		} catch (KeyStoreException e12) {
			log.error("init: keystore problem " + e12.getMessage(), e12);
		} catch (NoSuchAlgorithmException e13) {
			log.error("init: Algorithm not found " + e13.getMessage(), e13);
		}

		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException ex) {
			log.error("tmf: no such algo " + ex.getMessage(), ex);
		}
		try {
			tmf.init(keyStore);
		} catch (KeyStoreException ex) {
			log.error("tmf init: keystore problem " + ex.getMessage(), ex);
		}

		try {
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (KeyManagementException e14) {
			log.error("ctx init: key problem " + e14.getMessage(), e14);
		}
		final SocketFactory sslSocketFactory = ctx.getSocketFactory();

		try {
			final SSLSocket sslSocket = (SSLSocket) sslSocketFactory
					.createSocket(host, port);
			return sslSocket;
		} catch (IOException e15) {
			log.error("socket: io problem " + e15.getMessage(), e15);
		}
		return null;
	}
}
