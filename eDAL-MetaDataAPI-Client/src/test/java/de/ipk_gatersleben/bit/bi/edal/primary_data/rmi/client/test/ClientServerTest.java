/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Before;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Super Class for JUnit tests with RMI to start and close the RMI-Server.
 * 
 * @author arendd
 */
public class ClientServerTest {

	public static final String HOST = "localhost";

	public static int REGISTRY_PORT = EdalServer.DEFAULT_REGISTRY_PORT;

	protected static int DATA_PORT = EdalServer.DEFAULT_DATA_PORT;

	public static int HTTP_PORT = 8080;

	public static int HTTPS_PORT = 8443;

	protected static final String ZIPFILE = "bmc_article.zip";

	protected final File OUTPUT_FILE = new File(System.getProperty("user.home") + File.separatorChar + "TEST_READ");

	public Path mountPath = null;

	private static final int MIN_PORT_NUMBER = 49152;
	private static final int MAX_PORT_NUMBER = 65535;

	/**
	 * Start a new eDAL-Server.
	 * 
	 * @throws Exception
	 */
	@Before
	public void startServer() throws Exception {

		int port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		while (!available(port)) {
			port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}
		ClientServerTest.HTTP_PORT = port;
		
		int portRMI = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		while (!available(portRMI)) {
			portRMI = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}
		ClientServerTest.REGISTRY_PORT = portRMI;
		
		int portDATA = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		while (!available(portDATA)) {
			portDATA = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}
		ClientServerTest.DATA_PORT = portDATA;

		final EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
				new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("user@nodomain.com.invalid"),
				new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));

		configuration.setHttpPort(ClientServerTest.HTTP_PORT);
		configuration.setHttpsPort(ClientServerTest.HTTPS_PORT);

		mountPath = Paths.get(System.getProperty("user.home"), "edaltest", UUID.randomUUID().toString());
		Files.createDirectories(mountPath);

		configuration.setMountPath(mountPath);
		configuration.setDataPath(mountPath);

		EdalServer.startServer(configuration, ClientServerTest.REGISTRY_PORT, ClientServerTest.DATA_PORT, true, false);

	}

	/**
	 * Stop the eDAL-Server after the unit test finished.
	 * 
	 * @throws Exception
	 */
	@After
	public void stopServer() throws Exception {
		EdalServer.stopServer(ClientServerTest.HOST, ClientServerTest.REGISTRY_PORT);
		EdalHelpers.cleanMountPath(mountPath);
	}

	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean available(int port) {
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

}