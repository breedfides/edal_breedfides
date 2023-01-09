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
