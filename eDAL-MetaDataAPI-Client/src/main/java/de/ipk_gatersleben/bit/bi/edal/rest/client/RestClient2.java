/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class RestClient2 {

	public static void main(String[] args) throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);

		WebTarget request = client.target("http://localhost/").path("rest/entity");

		Response response = request.request().accept(MediaType.APPLICATION_XML).get();
		System.out.println("Respose (" + response.getStatus() + ")");
		System.out.println("Respose (" + response.getStatusInfo() + ")");
		System.out.println("Respose (" + response.readEntity(String.class) + ")");
		
		Response response2 = request.request().accept(MediaType.APPLICATION_JSON).get();
		System.out.println("Respose (" + response2.getStatus() + ")");
		System.out.println("Respose (" + response2.getStatusInfo() + ")");
		System.out.println("Respose (" + response2.readEntity(String.class) + ")");
		
		Response response3 = request.request().accept(MediaType.TEXT_XML).get();
		System.out.println("Respose (" + response3.getStatus() + ")");
		System.out.println("Respose (" + response3.getStatusInfo() + ")");
		System.out.println("Respose (" + response3.readEntity(String.class) + ")");
		
		request = client.target("http://localhost/").path("rest/api/getMetaData/7dd964b8-fcba-418f-af3d-f737f1d8bfd8/2");

			
		Response response4 = request.request().accept(MediaType.TEXT_PLAIN).get();
		System.out.println("Respose (" + response4.getStatus() + ")");
		System.out.println("Respose (" + response4.getStatusInfo() + ")");
		System.out.println("Respose (" + response4.readEntity(String.class) + ")");
		
		
}

}
