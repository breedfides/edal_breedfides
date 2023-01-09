package de.ipk_gatersleben.bit.bi.edal.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RESTClientForTest {

	@AfterAll
	public void deleteFiles() throws Exception {

		Files.delete(Paths.get(System.getProperty("user.home"), "edal_download.png"));
		Files.delete(Paths.get(System.getProperty("user.home"), "edal_stream.png"));
		Files.delete(Paths.get(System.getProperty("user.home"), "edal_upload.png"));
	}

	@Test
	public void testInfo() throws Exception {
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		/**********************************************************************************************************/

		WebTarget infoRequest = client.target("http://localhost/").path("breedfides/aai/info");
		Response infoResponse = infoRequest.request().accept(MediaType.TEXT_PLAIN).get();
		System.out.println("Respose (" + infoResponse.getStatus() + " - " + infoResponse.getStatusInfo() + ")");
		System.out.println("Respose (" + infoResponse.readEntity(String.class) + ")");

		WebTarget registerRequest = client.target("http://localhost/").path("breedfides/aai/register");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("countryName", "DE");
		rootNode.put("stateName", "Saxony-Anhalt");
		rootNode.put("cityName", "Stadt Seeland, OT Gatersleben");
		rootNode.put("organizationName", "IPK Gatersleben");
		rootNode.put("organizationUnitName", "BIT");
		rootNode.put("commmonName", "Max Mustermann");
		rootNode.put("commmonName", "mustermann@ipk-gatersleben.de");

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
		System.out.println(jsonString);

		Response registerResponse = registerRequest.request().post(Entity.json(jsonString));
		System.out.println(
				"Respose (" + registerResponse.getStatus() + ") value: " + registerResponse.readEntity(String.class));
	}

	@Test
	public void testDownload() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget downloadRequest = client.target("http://localhost/").path("breedfides/io/download");
		Response downloadResponse = downloadRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM).get();

		File file1 = downloadResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file1), Paths.get(System.getProperty("user.home"), "edal_download.png"));

	}

	@Test
	public void testStream() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget streamRequest = client.target("http://localhost/").path("breedfides/io/stream");
		Response streamResponse = streamRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM).get();

		File file2 = streamResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file2), Paths.get(System.getProperty("user.home"), "edal_stream.png"));
	}

	@Test
	public void testUpload() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget uploadRequest = client.target("http://localhost/").path("breedfides/io/upload");

		File file3 = Paths.get(System.getProperty("user.home"), "edal_stream.png").toFile();

		final FileDataBodyPart filePart = new FileDataBodyPart("file", file3);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar")
				.bodyPart(filePart);

		final Response uploadResponse = uploadRequest.request()
				.post(Entity.entity(multipart, multipart.getMediaType()));

		// Use response object to verify upload success
		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");
		System.out.println("Respose (" + uploadResponse.readEntity(String.class) + ")");
		formDataMultiPart.close();
		multipart.close();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		/**********************************************************************************************************/

//		WebTarget uploadRequest = client.target("http://localhost/").path("breedfides/io/upload");
//
//		File file3 = Paths.get(System.getProperty("user.home"), "edal_stream.png").toFile();
//
//		final FileDataBodyPart filePart = new FileDataBodyPart("file", file3);
//		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar")
//				.bodyPart(filePart);
//
//		final Response uploadResponse = uploadRequest.request()
//				.post(Entity.entity(multipart, multipart.getMediaType()));
//
//		// Use response object to verify upload success
//		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");
//		System.out.println("Respose (" + uploadResponse.readEntity(String.class) + ")");
//		formDataMultiPart.close();
//		multipart.close();

		/**********************************************************************************************************/

	}

}
