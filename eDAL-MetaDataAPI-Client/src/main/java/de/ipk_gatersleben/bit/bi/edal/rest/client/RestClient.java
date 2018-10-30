/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rest.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.security.auth.Subject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class RestClient {

	public static void main(String[] args) throws Exception {
		
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);
		Subject s = EdalHelpers.authenticateWinOrUnixOrMacUser();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(buffer);
		oos.writeObject(s);
		oos.close();

		byte[] subjectByteArray = buffer.toByteArray();

		WebTarget sendSubjectRequest = client.target("http://localhost/").path("rest/api/sendSubject");

		Response response = sendSubjectRequest.request()
				.post(Entity.entity(subjectByteArray, MediaType.APPLICATION_OCTET_STREAM_TYPE));
		System.out.println("Respose (" + response.getStatus() + ") value: " + response.readEntity(String.class));

		@SuppressWarnings("resource")
		final FormDataMultiPart multiPart = new FormDataMultiPart().field("name", "newname", MediaType.TEXT_PLAIN_TYPE)
				.field("subject", subjectByteArray, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		WebTarget sendSubjectAndObjectRequest = client.target("http://localhost/").path("rest/api/sendSubjectAndObject");

		Response response2 = sendSubjectAndObjectRequest.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));
		System.out.println("Respose (" + response2.getStatus() + ") value: " + response2.readEntity(String.class));
		
		
		WebTarget getRoootDirectoryRequest = client.target("http://localhost/").path("rest/api/getRootDirectory");

		Response response3 = getRoootDirectoryRequest.request()
				.post(Entity.entity(subjectByteArray, MediaType.APPLICATION_OCTET_STREAM));
		System.out.println("Respose (" + response3.getStatus() + ")");
		System.out.println("Respose (" + response3.getStatusInfo() + ")");
		ByteArrayInputStream in = new ByteArrayInputStream((byte[]) response3.getEntity());
		ObjectInputStream is = new ObjectInputStream(in);
		PrimaryDataDirectory dr = (PrimaryDataDirectory) is.readObject();
		
	}

}