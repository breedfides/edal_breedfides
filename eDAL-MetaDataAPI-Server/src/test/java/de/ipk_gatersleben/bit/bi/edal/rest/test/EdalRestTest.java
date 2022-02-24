package de.ipk_gatersleben.bit.bi.edal.rest.test;
/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rest.server.EdalRestServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalRestTest{

	private static int HTTPS_PORT = 8443;
	private static int HTTP_PORT = 8080;
	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "user@nodomain.com.invalid";
	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";
	private static final int MIN_PORT_NUMBER = 49152;
	private static final int MAX_PORT_NUMBER = 65535;

	public EdalConfiguration configuration = null;
	public Path mountPath = null;

	@BeforeEach
	public void setUp() throws Exception {

		int port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);

		while (!available(port)) {
			port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}

		EdalRestTest.HTTP_PORT = port;

		try {
			this.configuration = new EdalConfiguration(EdalRestTest.DATACITE_USERNAME,
					EdalRestTest.DATACITE_PASSWORD, EdalRestTest.DATACITE_PREFIX,
					new InternetAddress(EdalRestTest.EMAIL),
					new InternetAddress(EdalRestTest.EMAIL),
					new InternetAddress(EdalRestTest.EMAIL),
					new InternetAddress(EdalRestTest.ROOT_USER),
					"imap.ipk-gatersleben.de","","");
			this.configuration.setHttpPort(EdalRestTest.HTTP_PORT);
			this.configuration.setHttpsPort(EdalRestTest.HTTPS_PORT);

			mountPath = Paths.get(System.getProperty("user.home"), "edaltest", UUID.randomUUID().toString());
			Files.createDirectories(mountPath);

			this.configuration.setMountPath(mountPath);
			this.configuration.setDataPath(mountPath);

		} catch (EdalConfigurationException | AddressException e) {
			throw new EdalException(e);
		}
	}
	
	@Test
	public void testServer() throws AddressException, EdalConfigurationException, PrimaryDataDirectoryException, EdalAuthenticateException, MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException, CloneNotSupportedException, PrimaryDataEntityException, PublicReferenceException, IOException {
		PrimaryDataDirectory root = EdalRestServer.startRestServer(configuration);
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:6789/restfull").path("api/test");
		String result = webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
		System.out.println("response: "+result);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataManager.shutdown();
	}

	@AfterEach
	public void tearDown() throws Exception {
		DataManager.shutdown();
//		EdalHelpers.cleanMountPath(mountPath);
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