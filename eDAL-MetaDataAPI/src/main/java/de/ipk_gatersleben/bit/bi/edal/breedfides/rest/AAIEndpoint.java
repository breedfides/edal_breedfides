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
package de.ipk_gatersleben.bit.bi.edal.breedfides.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.JwtGenerator;
import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.KeyGenerator;
import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.SelfSignedCertGenerator;
import de.ipk_gatersleben.bit.bi.edal.breedfides.persistence.Certificate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * REST endpoints for all AAI relevant function of the BreedFides portal
 * 
 * @author arendd
 *
 */
@Path("aai")
public class AAIEndpoint {

	private final static String COMMON_NAME = "commonName";
	private final static String COUNTRY_NAME = "countryName";
	private final static String CITY_NAME = "cityName";
	private final static String ORGANISATION_NAME = "organizationName";
	private final static String ORGANISATION_UNIT_NAME = "organizationUnitName";
	private final static String STATE_NAME = "stateName";

	private static final String HASH_ALGORITHM = "SHA256withRSA";
	private static final String CERTIFICATE_FILE_NAME = "certificate.cer";
	private static final String ZIP_FILE_NAME = "certificate.zip";

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {
		return "BreedFides AAI endpoint";
	}

	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response register(JSONObject inputJson) {

		InfoEndpoint.getLogger().info("Call 'register' endpoint");

		StreamingOutput fileStream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) {

				try (ZipOutputStream zipStream = new ZipOutputStream(output)) {

					X509Certificate certificate = SelfSignedCertGenerator.generate(KeyGenerator.loadKeyPair(),
							HASH_ALGORITHM, (String) inputJson.get(COMMON_NAME),
							(String) inputJson.get(ORGANISATION_NAME), (String) inputJson.get(ORGANISATION_UNIT_NAME),
							(String) inputJson.get(COUNTRY_NAME), (String) inputJson.get(STATE_NAME),
							(String) inputJson.get(CITY_NAME), 14);

					ZipEntry entry = new ZipEntry(CERTIFICATE_FILE_NAME);
					zipStream.putNextEntry(entry);
					zipStream.write(certificate.getEncoded());
					zipStream.closeEntry();
					zipStream.finish();
					zipStream.close();

					Certificate.storeCertificate(certificate);

				} catch (Exception e) {
					DataManager.getImplProv().getLogger().error(e.getMessage());
				}

			}
		};

		return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename=" + ZIP_FILE_NAME).build();

	}

	/**
	 * Endpoint to login with certificate.
	 * 
	 * ToDo:Add Check if certificate is valid in database
	 * 
	 * @param fileInputStream the login certificate as input stream
	 * @return
	 * @throws Exception
	 */
	@Path("login")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormDataParam("file") InputStream fileInputStream) throws Exception {

		InfoEndpoint.getLogger().info("Call 'login' endpoint");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();

		CertificateFactory factory = CertificateFactory.getInstance("X509");

		X509Certificate certificate = (X509Certificate) factory.generateCertificate(fileInputStream);

		if (Certificate.checkSerialNumber(certificate)) {

			Principal subject = certificate.getSubjectX500Principal();
			String subjectArray[] = subject.toString().split(",");

			for (String subjects : subjectArray) {

				String[] strings = subjects.trim().split("=");

				String key = strings[0];
				if (key.equals("CN")) {
					rootNode.put("message", "Signed in");
					rootNode.put("displayName", strings[1]);
				}
			}

			String responseJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

			HashMap<String, String> payload = new HashMap<>();
			payload.put("role", "breeder");

			String jwt = JwtGenerator.generateJwt(payload);

			InfoEndpoint.getLogger().info("Call 'login' endpoint -> generate JWT '" + jwt.substring(0, 10) + ".."
					+ jwt.substring(jwt.length() - 11, jwt.length() - 1) + "'");

			return Response.ok(responseJson, MediaType.APPLICATION_JSON).cookie(new NewCookie("token", jwt)).build();
		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "No valid certificate, please register before")
					.build();
		}

	}

	@POST
	@Path("logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() throws Exception {

		InfoEndpoint.getLogger().info("Call 'logout' endpoint");

		JsonBuilder builder = new JsonBuilder();
		builder.put("message", "Logged out");

		String emptyJwt = JwtGenerator.generateLogoutJwt();

		return Response.ok(builder.getJsonString(), MediaType.APPLICATION_JSON).cookie(new NewCookie("token", emptyJwt))
				.build();
	}

}
