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
import java.nio.file.Files;
import java.nio.file.Path;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BreedFidesClientForTest {

	private static final String HTTP_LOCALHOST = "http://localhost/";
//	private static final String HTTP_LOCALHOST = "http://94.156.201.214:81/";
	private static String myJWT = "";

	private static void register() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("countryName", "DE");
		rootNode.put("stateName", "Saxony-Anhalt");
		rootNode.put("cityName", "Stadt Seeland, OT Gatersleben");
		rootNode.put("organizationName", "IPK Gatersleben");
		rootNode.put("organizationUnitName", "BIT");
		rootNode.put("commonName", "Daniel Arend");
		rootNode.put("commonName", "arendd@ipk-gatersleben.de");

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
		System.out.println(jsonString);

		WebTarget streamRequest = client.target(HTTP_LOCALHOST).path("breedfides/aai/register");
		Response streamResponse = streamRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM)
				.post(Entity.json(jsonString));

		File file = streamResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file), Paths.get(System.getProperty("user.home"), "breedFides","compressed.zip"));

		client.close();
	}

	private static void login() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget loginRequest = client.target(HTTP_LOCALHOST).path("breedfides/aai/login");

		File file1 = Paths.get(System.getProperty("user.home"), "breedFides","certificate.cer").toFile();

		final FileDataBodyPart filePart = new FileDataBodyPart("file", file1);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar")
				.bodyPart(filePart);

		final Response loginResponse = loginRequest.request().post(Entity.entity(multipart, multipart.getMediaType()));

		// Use response object to verify upload success
		System.out.println("Respose (" + loginResponse.getStatus() + " - " + loginResponse.getStatusInfo() + ")");
		System.out.println("Respose (" + loginResponse.readEntity(String.class) + ")");
		System.out.println(loginResponse.getCookies());
		formDataMultiPart.close();
		multipart.close();

		client.close();

	}

	private static void upload() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget uploadRequest = client.target(HTTP_LOCALHOST).path("breedfides/upload/datasets");

		File file = Paths.get(System.getProperty("user.home"),"breedFides", "certificate.cer").toFile();

		final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("title", "Italian Accessions of the Bridge Dataset");
		rootNode.put("description", "MIAPPE ISA Tab formatted dataset of all italian accessions of the BRIDGE Dataset");
		rootNode.put("language", "english");
		rootNode.put("license", "CC 1.0 Universal");

		ArrayNode authorsArrayNode = rootNode.arrayNode();
		ObjectNode creatorNode = mapper.createObjectNode();

		creatorNode.put("firstName", "Daniel");
		creatorNode.put("lastName", "Arend");
		creatorNode.put("country", "Deutschland");
		creatorNode.put("zip", "123");
		creatorNode.put("address", "Corrensstraße 3 Seeland");
		creatorNode.put("role", "Creator");
		
		
		ObjectNode contributorNode = mapper.createObjectNode();

		contributorNode.put("legalName", "IPK Gatersleben");
		contributorNode.put("country", "Deutschland");
		contributorNode.put("zip", "06466");
		contributorNode.put("address", "Corrensstraße 3 Seeland");
		contributorNode.put("role", "Contributor");


		authorsArrayNode.add(creatorNode);
		authorsArrayNode.add(contributorNode);
		
		rootNode.set("authors", authorsArrayNode);

		ArrayNode subjectsArrayNode = rootNode.arrayNode();
		subjectsArrayNode.add("MIAPPE");
		subjectsArrayNode.add("Triticum");
		subjectsArrayNode.add("Ro-Crate");
		rootNode.set("subjects", subjectsArrayNode);

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("path", "directory2")
				.field("metaData", jsonString).bodyPart(filePart);

		final Response uploadResponse = uploadRequest.request().header("Authorization", "Bearer " + myJWT)
				.post(Entity.entity(multipart, multipart.getMediaType()));

		// Use response object to verify upload success
		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");
		System.out.println("Respose (" + uploadResponse.readEntity(String.class) + ")");
		System.out.println(uploadResponse.getCookies());
		formDataMultiPart.close();
		multipart.close();

	}

	private static void access() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget accessRequest = client.target(HTTP_LOCALHOST).path("breedfides/access/datasets");

		Response registerResponse = accessRequest.queryParam("pageSize", 25).queryParam("page", 1)
				.queryParam("keywords", "amanda").request().header("Authorization", "Bearer " + myJWT)
				.accept(MediaType.APPLICATION_JSON).get();

		System.out.println(
				"Respose (" + registerResponse.getStatus() + ") value: " + registerResponse.readEntity(String.class));
	}

	private static void download() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget downloadRequest = client.target(HTTP_LOCALHOST).path("breedfides/download/dataset");

		Response downloadResponse = downloadRequest.queryParam("datasetOwner", "1682596147141")
				.queryParam("datasetRoot", "amanda").request().header("Authorization", "Bearer " + myJWT)
				.accept(MediaType.APPLICATION_OCTET_STREAM).get();

		File file = downloadResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file), Paths.get(System.getProperty("user.home"), "download.zip"));

		client.close();

	}

	private static void uploadDirectory() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget uploadRequest = client.target(HTTP_LOCALHOST).path("breedfides/upload/datasets");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("title", "Amanda");
		rootNode.put("description", "Crazy Chromosome Videos");
		rootNode.put("language", "english");
		rootNode.put("license", "CC 1.0 Universal");

		ArrayNode authorsArrayNode = rootNode.arrayNode();
		ObjectNode creatorNode = mapper.createObjectNode();

		creatorNode.put("firstName", "Amanda");
		creatorNode.put("lastName", "Camara");
		creatorNode.put("country", "Deutschland");
		creatorNode.put("zip", "06466");
		creatorNode.put("address", "Corrensstraße 3 Seeland");
		creatorNode.put("role", "Creator");
		
		authorsArrayNode.add(creatorNode);

		rootNode.set("authors", authorsArrayNode);

		ArrayNode subjectsArrayNode = rootNode.arrayNode();
		subjectsArrayNode.add("barley");
		subjectsArrayNode.add("chromosome");
		subjectsArrayNode.add("mpeg");
		rootNode.set("subjects", subjectsArrayNode);

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

		Path uplodPath = Paths.get(System.getProperty("user.home"), "Downloads","amanda");

		FileUploadVisitor uploadVisitor = new FileUploadVisitor(uplodPath, jsonString, uploadRequest, myJWT);

		Files.walkFileTree(uplodPath, uploadVisitor);

//		File file = Paths.get(System.getProperty("user.home"), "certificate.cer").toFile();
//
//		final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
//		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//		
//		
//		
//
//		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("path", "directory2")
//				.field("metaData", jsonString).bodyPart(filePart);
//
//		final Response uploadResponse = uploadRequest.request().header("Authorization", "Bearer " + myJWT)
//				.post(Entity.entity(multipart, multipart.getMediaType()));
//
//		// Use response object to verify upload success
//		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");
//		System.out.println("Respose (" + uploadResponse.readEntity(String.class) + ")");
//		System.out.println(uploadResponse.getCookies());
//		formDataMultiPart.close();
//		multipart.close();

	}

	public static void main(String[] args) throws Exception {

//			register();

//			login();

		myJWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzZXJpYWxOdW1iZXIiOiIxNjgzMjAxNTYyNjA5Iiwicm9sZSI6ImJyZWVkZXIiLCJpc3MiOiJCcmVlZEZpZGVzIiwiaWF0IjoxNjgzMjAxNTc3LCJqdGkiOiJhMGI0ZjgwYi0xY2EzLTRkZmUtYTQzZC0xMDI4ZjdiMGNjY2QifQ.X_dt9vKNjr7tLCV5_K5F7hdWODk9cMlQd1-6dvHDEBoiAYyi4ZGvJ97ftJQMM-4HJFf3Xu40MFx7Wx4Xk-Msy3BhhyS4k7Xb3T6XPIjvjWTiO4o93_d7Z5BbIAdZa2ZwHuo7NOpQmZMv-SP7jpzuEVfMAHGfy3SI8XNanwNj5Apvqxz7adf0ZvERGoScRLZDbbe7CYh6C7kxwTpYnrWR__873GuKiGpl4E0arlAQ2uX8g8M3MlK-meZ9rPr8Lk8BQvpMeOoWP_gdmz2EABDnEgYSq_IxS0ex_UgL0LOpcz-e4ZmOt1vTFoYKhBn2R09Fg2FeXqPm9u06fu6Y7iuLWQ";


//			myJWT =  "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzZXJpYWxOdW1iZXIiOiIxNjgyNTk2MTQ3MTQxIiwicm9sZSI6ImJyZWVkZXIiLCJpc3MiOiJCcmVlZEZpZGVzIiwiaWF0IjoxNjgyNTk2MTYwLCJqdGkiOiI5NDY3ODdmYi0zYjZlLTQ5MGMtOGRiYi1jODU0YzljOTI2NjMifQ.SWKYsSSO0z0gtLqtadE6YtQjeRcaE_61SjX-ewMGVIOilokgbwrAQvKALVEaZUav7I9FlR-6f98r44PXzBO5sIsw8Kj4ZxiOtPr_I5dj9zV8y349Xf2CTkyvx3lbmPF8iTYPiUEv62971HPFQhHRc43NxlyHXxvNxh3wd44Vzwe48QHqAL-9xidCbipBMx4-CpHeqsOfIv_6wPdk1dY1xnev1e7S5P38RhaFRPpzqDjY_HQySDs94m0fZPDHEzEqT_lGTcqDbQV47yA3uvLD1Az-qGx3IzZ9nIMwDeS92OIajXDNe2uBy_Sr1Zsqh8v1ZPVN8xok_3IyQngFX_BgFA";


			
			upload();

//			access();

//		download();

//		uploadDirectory();

	}

}
