/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
