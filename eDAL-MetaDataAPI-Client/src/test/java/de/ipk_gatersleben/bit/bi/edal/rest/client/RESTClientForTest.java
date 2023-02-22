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
		rootNode.put("commonName", "Max Mustermann");
		rootNode.put("commonName", "mustermann@ipk-gatersleben.de");

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

	@Test
	public void testCertificateDownload() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("countryName", "DE");
		rootNode.put("stateName", "Saxony-Anhalt");
		rootNode.put("cityName", "Stadt Seeland, OT Gatersleben");
		rootNode.put("organizationName", "IPK Gatersleben");
		rootNode.put("organizationUnitName", "BIT");
		rootNode.put("commonName", "Max Mustermann");

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
		System.out.println(jsonString);

		WebTarget streamRequest = client.target("http://localhost/").path("breedfides/aai/register");
		Response streamResponse = streamRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM)
				.post(Entity.json(jsonString));

		File file = streamResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file), Paths.get(System.getProperty("user.home"), "compressed.zip"));
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

//		Client client = ClientBuilder.newClient();
//		client.register(MultiPartFeature.class);
//
//		
//		ObjectMapper mapper = new ObjectMapper();
//		ObjectNode rootNode = mapper.createObjectNode();
//
//		rootNode.put("countryName", "DE");
//		rootNode.put("stateName", "Saxony-Anhalt");
//		rootNode.put("cityName", "Stadt Seeland, OT Gatersleben");
//		rootNode.put("organizationName", "IPK Gatersleben");
//		rootNode.put("organizationUnitName", "BIT");
//		rootNode.put("commonName", "Max Mustermann");
//		rootNode.put("commonName", "mustermann@ipk-gatersleben.de");
//
//		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
//		System.out.println(jsonString);
//		
//		
//
//		
//		
//		WebTarget streamRequest = client.target("http://localhost/").path("breedfides/aai/register");
//		Response streamResponse = streamRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM).post(Entity.json(jsonString));
//		
//		
//		File file = streamResponse.readEntity(File.class);
//		Files.copy(new FileInputStream(file), Paths.get(System.getProperty("user.home"), "compressed.zip"));

//		Client client = ClientBuilder.newClient();
//		client.register(MultiPartFeature.class);
//
//		WebTarget uploadRequest = client.target("http://localhost/").path("breedfides/aai/login");
//
//		File file3 = Paths.get(System.getProperty("user.home"), "certificate.cer").toFile();
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
//		System.out.println(uploadResponse.getCookies());
//		formDataMultiPart.close();
//		multipart.close();

//		Client client = ClientBuilder.newClient();
//		client.register(MultiPartFeature.class);
//
//		WebTarget uploadRequest = client.target("http://localhost/").path("breedfides/upload/datasets");
//
//		File file3 = Paths.get(System.getProperty("user.home"), "certificate.cer").toFile();
//
//		final FileDataBodyPart filePart = new FileDataBodyPart("file", file3);
//		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("datasetRoot", "subdirectory")
//				.field("metaData", "{\"title\":\"Italian Accessions of the Bridge Dataset\",\"description\":\"MIAPPE ISA Tab formatted dataset of all italian accessions of the BRIDGE Dataset.\",\"language\":\"english\",\"subjects\":[\"Hordeum vulgare\",\"ISA Tab\",\"Phenotyping\",\"MIAPPE\"],\"authors\":[{\"country\":\"Deutschland\",\"zip\":\"D-06466\",\"address\":\"CORRENSSTRASSE 3\",\"role\":\"Contributor\",\"orcid\":\"0000-0001-6546-1818\",\"legalName\":\"Manuel Feser\",\"lastName\":\"Feser\",\"firstName\":\"Manuel\",\"id\":0}],\"embargoDate\":\"02/17/2023\",\"license\":\"CC 1.0 Universal\"}").bodyPart(filePart);
//
//		
//		
//		final Response uploadResponse = uploadRequest.request()
//				.header("Authorization", "Bearer JWT_Token")
//				.post(Entity.entity(multipart, multipart.getMediaType()));
//
//		// Use response object to verify upload success
//		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");
//		System.out.println("Respose (" + uploadResponse.readEntity(String.class) + ")");
//		System.out.println(uploadResponse.getCookies());
//		formDataMultiPart.close();
//		multipart.close();

		
		
		
		
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget accessRequest = client.target("http://localhost/").path("breedfides/access/datasets");

		Response registerResponse = accessRequest.queryParam("subjects", "hordeum vulgare").request()
				.header("Authorization", "Bearer JWT_Token").accept(MediaType.APPLICATION_JSON).get();

		System.out.println(
				"Respose (" + registerResponse.getStatus() + ") value: " + registerResponse.readEntity(String.class));

	}

}
