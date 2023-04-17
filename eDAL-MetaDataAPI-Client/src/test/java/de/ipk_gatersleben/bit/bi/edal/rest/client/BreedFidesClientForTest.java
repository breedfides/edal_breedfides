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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BreedFidesClientForTest {
	
	
	private static String myJWT = "";

	private static void register() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		///// register to get certificate stored in a zip

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

		WebTarget streamRequest = client.target("http://localhost/").path("breedfides/aai/register");
		Response streamResponse = streamRequest.request().accept(MediaType.APPLICATION_OCTET_STREAM)
				.post(Entity.json(jsonString));

		File file = streamResponse.readEntity(File.class);
		Files.copy(new FileInputStream(file), Paths.get(System.getProperty("user.home"), "compressed.zip"));

		client.close();
	}

	private static void login() throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget loginRequest = client.target("http://localhost/").path("breedfides/aai/login");

		File file1 = Paths.get(System.getProperty("user.home"), "certificate.cer").toFile();

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
		
		WebTarget uploadRequest = client.target("http://localhost/").path("breedfides/upload/datasets");

		File file = Paths.get(System.getProperty("user.home"), "certificate.cer").toFile();

		final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		rootNode.put("title", "Italian Accessions of the Bridge Dataset");
		rootNode.put("description", "MIAPPE ISA Tab formatted dataset of all italian accessions of the BRIDGE Dataset");
		rootNode.put("language", "english");
		rootNode.put("license", "CC 1.0 Universal");

		ArrayNode authorsArrayNode = rootNode.arrayNode();
		ObjectNode authorNode = mapper.createObjectNode();

		authorNode.put("firstName", "Manuel");
		authorNode.put("lastName", "Feser");
		authorNode.put("country", "Deutschland");
		authorNode.put("zip", "06466");
		authorNode.put("address", "Corrensstraße 3 Seeland");
		authorNode.put("role", "Creator");

		authorsArrayNode.add(authorNode);

		rootNode.set("authors", authorsArrayNode);

		ArrayNode subjectsArrayNode = rootNode.arrayNode();
		subjectsArrayNode.add("MIAPPE");
		subjectsArrayNode.add("Triticum");
		subjectsArrayNode.add("Ro-Crate");
		rootNode.set("subjects", subjectsArrayNode);

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

		final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("path", "directory2")
				.field("metaData", jsonString).bodyPart(filePart);

		final Response uploadResponse = uploadRequest.request().header("Authorization",
				"Bearer "+ myJWT)
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
		
		WebTarget accessRequest = client.target("http://localhost/").path("breedfides/access/datasets");
		
				Response registerResponse = accessRequest.queryParam("pageSize", 25).queryParam("page", 1).queryParam("keywords", "triticum").request()
						.header("Authorization", "Bearer "+ myJWT)
		.accept(MediaType.APPLICATION_JSON).get();
		
				System.out.println(
						"Respose (" + registerResponse.getStatus() + ") value: " + registerResponse.readEntity(String.class));
	}
	
	

	public static void main(String[] args) throws Exception {

//			register();
			
//			login();
			
			
				myJWT="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzZXJpYWxOdW1iZXIiOiIxNjgxMjkzMDU0MDE5Iiwicm9sZSI6ImJyZWVkZXIiLCJpc3MiOiJCcmVlZEZpZGVzIiwiaWF0IjoxNjgxMjkzMDkzLCJqdGkiOiI4MTJlNjk3ZC1mNmFiLTQzZGQtYjUwMi1hZjE2MTVhZWFlNGYifQ.aH_MvGHI2cUp0qjXHnM2ZxZQ20Xbrjymj_f5NQul5G0hM37FDNjjVXJpPmS1smFq8eMuIQ1OAuUcliTQrWXlVjN9YwO4NgwWJwbD9oj351quiue-r02wqNTcyVbOiJnO06aumdCmJYgS0qxwf2t2_NpWinND2IMWscxbdPaXb-AH7GXeEC397_OOR5D13nCML3RSC0UgT-jpgtO2N1cmhL2CAfdbuD8U4lhgjVpbpDW9qBIyOwESzxX6GAAErJzNq64IGAr3nfTSfUYr0ooXYxy0PAfh4ecnjl9DjUVVFToJWk47qlcLRdMVuQEGJEaiMUxf0XKnpu37QUroRYuEzw";

		
//			upload();
		
			access();

	}

}
