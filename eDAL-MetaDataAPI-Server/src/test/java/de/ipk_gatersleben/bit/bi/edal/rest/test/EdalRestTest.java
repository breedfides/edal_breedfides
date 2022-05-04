package de.ipk_gatersleben.bit.bi.edal.rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
	private final String METADATA_JSON = "{\"title\":\"my dataset title\",\"description\":\"Some description to describe this dataset.\",\"language\":\"de\",\"license\":\"CC010\",\"persons\":[{\"Firstname\":\"Eric\",\"Lastname\":\"Ralfs\",\"ORCID\":\"\",\"Legalname\":\"\",\"Adress\":\"Kingstr 26\",\"Zip\":\"25708\",\"Country\":\"Marne\",\"Type\":\"Creator\"}],\"subjects\":[\"A subject\"]}";

	public EdalConfiguration configuration = null;
	public Path mountPath = null;

	private final String ENC_EMAIL = "rO0ABXNyABtqYXZheC5zZWN1cml0eS5hdXRoLlN1YmplY3SMsjKTADP6aAMAAloACHJlYWRPbmx5TAAKcHJpbmNpcGFsc3QAD0xqYXZhL3V0aWwvU2V0O3hwAHNyACVqYXZhLnV0aWwuQ29sbGVjdGlvbnMkU3luY2hyb25pemVkU2V0BsPCeQLu3zwCAAB4cgAsamF2YS51dGlsLkNvbGxlY3Rpb25zJFN5bmNocm9uaXplZENvbGxlY3Rpb24qYfhNCZyZtQMAAkwAAWN0ABZMamF2YS91dGlsL0NvbGxlY3Rpb247TAAFbXV0ZXh0ABJMamF2YS9sYW5nL09iamVjdDt4cHNyACVqYXZheC5zZWN1cml0eS5hdXRoLlN1YmplY3QkU2VjdXJlU2V0bcwygBdVficDAANJAAV3aGljaEwACGVsZW1lbnRzdAAWTGphdmEvdXRpbC9MaW5rZWRMaXN0O0wABnRoaXMkMHQAHUxqYXZheC9zZWN1cml0eS9hdXRoL1N1YmplY3Q7eHAAAAABc3IAFGphdmEudXRpbC5MaW5rZWRMaXN0DClTXUpgiCIDAAB4cHcEAAAAAXNyAEFkZS5pcGtfZ2F0ZXJzbGViZW4uYml0LmJpLmVkYWwucHJpbWFyeV9kYXRhLmxvZ2luLkVsaXhpclByaW5jaXBhbAAAAAAAAAABAgABTAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwdAAYcmFsZnNAaXBrLWdhdGVyc2xlYmVuLmRleHEAfgACeHEAfgAHeHg=";

	
	@Before
	public void setUp() throws Exception {

		int port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);

		while (!available(port)) {
			port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}

		EdalRestTest.HTTP_PORT = port;

		try {
			this.configuration = new EdalConfiguration(EdalRestTest.DATACITE_USERNAME, EdalRestTest.DATACITE_PASSWORD,
					EdalRestTest.DATACITE_PREFIX, new InternetAddress(EdalRestTest.EMAIL),
					new InternetAddress(EdalRestTest.EMAIL), new InternetAddress(EdalRestTest.EMAIL),
					new InternetAddress(EdalRestTest.ROOT_USER), "imap.ipk-gatersleben.de", "", "");
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

	//@Test
	public void testServer() throws AddressException, EdalConfigurationException, PrimaryDataDirectoryException,
			EdalAuthenticateException, MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			CloneNotSupportedException, PrimaryDataEntityException, PublicReferenceException, IOException {
		PrimaryDataDirectory root = EdalRestServer.startRestServer(configuration);

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:6789/restfull").path("api/test");
		String result = webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
		System.out.println(result);
		Assert.assertTrue("Test".equals(result));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataManager.shutdown();
	}

	@Test
	public void testUpload() throws AddressException, EdalConfigurationException, PrimaryDataDirectoryException,
			EdalAuthenticateException, MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			CloneNotSupportedException, PrimaryDataEntityException, PublicReferenceException, IOException, InterruptedException {
		PrimaryDataDirectory root = EdalRestServer.startRestServer(configuration);
		String dir = "uploadtest";
		File file1 = new File(dir + File.separator + "file1.txt");
		File file2 = new File(dir + File.separator + "file2.txt");
		file1.getParentFile().mkdirs();
		file1.createNewFile();
		file2.createNewFile();
		String str = "test";
//		BufferedWriter writer1 = new BufferedWriter(new FileWriter(file1.getPath(), true));
//		BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2.getPath(), true));
//		for (int i = 0; i < 90000000; i++) {
//			writer1.append(' ');
//			writer1.append(str);
//			writer2.append(' ');
//			writer2.append(str);
//		}
//		writer1.close();
//		writer2.close();

		FormDataMultiPart form = new FormDataMultiPart()
		        .field("subject", ENC_EMAIL)
		        .field("name", "test")
		        .field("metadata", METADATA_JSON);
		javax.ws.rs.core.Response response = postRequest("uploadEntityAndMetadata", form);
		System.out.println("response okay?: "+response);
		String actual = root.getPrimaryDataEntity("my dataset title").getName();
		Assert.assertEquals("my dataset title", actual);
		form = new FormDataMultiPart()
		        .field("name", "neu")
		        .field("email", ENC_EMAIL)
		        .field("datasetRoot", "my dataset title")
		        .field("type", "Directory");
		response = postRequest("uploadEntity", form);
		System.out.println("response okay?: "+response);
		actual = ((PrimaryDataDirectory)root.getPrimaryDataEntity("my dataset title")).getPrimaryDataEntity("neu").getName();
		Assert.assertEquals("neu", actual);
		File initialFile = new File("neu"+File.separator+"Das ist ein test.txt");
	    InputStream targetStream = new FileInputStream(initialFile);
		form = new FormDataMultiPart()
		        .field("name", "neu/Das ist ein test")
		        .field("file", targetStream, MediaType.MULTIPART_FORM_DATA_TYPE)
		        .field("email", ENC_EMAIL)
		        .field("datasetRoot", "my dataset title")
		        .field("type", "File");
		response = postRequest("uploadEntity", form);
		Thread.sleep(2000);
		targetStream.close();
		
	}
	
	private javax.ws.rs.core.Response postRequest(String endpoint, FormDataMultiPart form) {
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);
		WebTarget webTarget = client.target("http://localhost:6789/restfull/api").path(endpoint);
		Builder request = webTarget.request().accept(MediaType.TEXT_PLAIN);
		return request.post(Entity.entity(form, form.getMediaType()));
	}

	@After
	public void tearDown() throws Exception {
		DataManager.shutdown();
	}

	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port the port to check for availability
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