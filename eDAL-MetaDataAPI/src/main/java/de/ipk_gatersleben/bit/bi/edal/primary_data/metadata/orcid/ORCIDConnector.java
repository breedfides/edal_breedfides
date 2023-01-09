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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

/**
 * Class to connect with the ORCID RESTfull-API to send requests and get
 * information from ORCID registry
 * 
 * @author arendd
 *
 */
public class ORCIDConnector {

	static {
		InetSocketAddress proxy = EdalConfiguration.guessProxySettings();
		System.setProperty("http.proxyHost", proxy.getHostName());
		System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
		System.setProperty("https.proxyHost", proxy.getHostName());
		System.setProperty("https.proxyPort", String.valueOf(proxy.getPort()));
		System.setProperty("java.net.useSystemProxies", "true");
	}

	private static final String CLIENT_ID = "QVBQLU9ONEgwSUcwWjYyQUJRUUI=";
	private static final String CLIENT_SECRET = "MjU3MDhjNjItZGI1Ny00NTBlLThkMmYtYjk1ZmQ3OTYzMTli";

	private final static File PATH_TO_ORCID_TOKEN = Paths
			.get(System.getProperty("user.home"), ".eDAL", "orcid_token.txt").toFile();

	private String accessToken = null;
	private String orcid = null;
	private String givenName = null;
	private String familyName = null;
	private String affiliation = null;

	public static final List<String> getORCIDForName() {
		return null;
	}

	public ORCIDConnector(String orcid) throws Exception {

		if (loadToken() == null) {
			this.accessToken = requestNewToken();
		} else {
			this.accessToken = loadToken();
		}

		getNameByOrcid(orcid);
		getCurrentAffiliationByOrcid(orcid);
		this.orcid = orcid;

	}

	public ORCIDConnector(String givenName, String familyName) throws Exception {

		if (loadToken() == null) {
			this.accessToken = requestNewToken();
		} else {
			this.accessToken = loadToken();
		}

		searchForName(givenName, familyName);

	}

	public ORCIDConnector(String givenName, String familyName, String affiliation) throws Exception {

		if (loadToken() == null) {
			this.accessToken = requestNewToken();
		} else {
			this.accessToken = loadToken();
		}

		searchForAffiliation(givenName, familyName, affiliation);
	}

	public String toString() {
		return "GivenName: " + this.givenName + "\t" + "FamilyName: " + this.familyName + "\t" + "(" + this.orcid
				+ ") (" + this.affiliation + ")";

	}

	private String requestNewToken() throws Exception {
		
		JerseyClient client = JerseyClientBuilder.createClient();

		Form input = new Form();
		input.param("client_id", new String(Base64.getDecoder().decode(CLIENT_ID)));
		input.param("client_secret", new String(Base64.getDecoder().decode(CLIENT_SECRET)));
		input.param("scope", "/read-public");
		input.param("grant_type", "client_credentials");

		WebTarget resource = client.target("https://pub.orcid.org/oauth/token");

		final Response response = resource.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED));

		if (response.getStatus() == 200) {
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) new JSONParser().parse((String) response.readEntity(String.class));
			} catch (ParseException e) {
				throw new Exception("Parsing of user token failed: " + e);
			}

			if (jsonObject.containsKey("access_token")) {
				accessToken = (String) jsonObject.get("access_token");
				try {
					saveTokenToProperties(accessToken);
					return accessToken;
				} catch (IOException e) {
					throw new Exception("Saving authentication token failed");
				}
			}
		} else {
			client.close();
			throw new Exception("Request for authentication token failed");
		}
		return null;
	}

	private String loadToken() {

		Properties props = new Properties();

		try {
			InputStream inputStream = new FileInputStream(PATH_TO_ORCID_TOKEN);
			props.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			return null;
		}

		if (props.containsKey("token")) {
			return props.getProperty("token");
		} else {
			return null;
		}
	}

	private void saveTokenToProperties(String token) throws IOException {
		Properties props = new Properties();
		props.setProperty("token", token);
		OutputStream outputStream = new FileOutputStream(PATH_TO_ORCID_TOKEN);
		props.store(outputStream, "Token");
		outputStream.close();
	}

	private void getNameByOrcid(String orcid) throws Exception {

		JerseyClient client = JerseyClientBuilder.createClient();

		WebTarget resource = client.target("https://pub.orcid.org/v2.0/" + orcid + "/personal-details");

		final Response response = resource.request("application/orcid+xml")
				.header("Authorization", "Bearer " + this.accessToken).get();

		if (response.getStatus() == 200) {

			String result = response.readEntity(String.class);

			try {

				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(result)));

				Node foundedGivenName = document.getElementsByTagName("personal-details:given-names").item(0);
				Node foundedfamilyName = document.getElementsByTagName("personal-details:family-name").item(0);

				if (foundedGivenName != null && foundedfamilyName != null) {

					this.givenName = foundedGivenName.getTextContent();
					this.familyName = foundedfamilyName.getTextContent();
				} else {
					throw new Exception("no personal data founded");
				}
			} catch (Exception e) {
				throw new Exception("no orcid registered", e);
			}
		}
		else {
			client.close();
		}
	}

	private void getCurrentAffiliationByOrcid(String orcid) throws Exception {

		JerseyClient client = JerseyClientBuilder.createClient();

		WebTarget resource = client.target("https://pub.orcid.org/v2.0/" + orcid + "/employments");

		final Response response = resource.request("application/vnd.orcid+xml ")
				.header("Authorization", "Bearer " + this.accessToken).get();

		if (response.getStatus() == 200) {

			String result = response.readEntity(String.class);

			try {

				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(result)));
				NodeList list = document.getElementsByTagName("employment:employment-summary");

				if (list.getLength() == 1) {
					this.affiliation = document.getElementsByTagName("common:name").item(0).getTextContent();
				} else {
					for (int i = 0; i < list.getLength(); i++) {
						Element element = (Element) list.item(i);

						if (element.getElementsByTagName("common:end-date").getLength() == 1) {
							// previous affiliation
						} else {
							// curent affiliation
							this.affiliation = element.getElementsByTagName("common:name").item(0).getTextContent();
						}
					}
				}
			} catch (Exception e) {
				throw new Exception("no orcid registered", e);
			}
		}
		else {
			client.close();
		}
	}

	private void searchForName(String givenName, String familyName) throws Exception {

		JerseyClient client = JerseyClientBuilder.createClient();

		WebTarget resource = client.target(
				"https://pub.orcid.org/v2.0/search/?q=given-names:" + givenName + "+AND+family-name:" + familyName);

		final Response response = resource.request("application/orcid+xml")
				.header("Authorization", "Bearer " + this.accessToken).get();

		if (response.getStatus() == 200) {

			String result = response.readEntity(String.class);

			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(result)));
			NodeList list = document.getElementsByTagName("search:result");

			if (list.getLength() == 1) {
				this.givenName = givenName;
				this.familyName = familyName;
				this.orcid = document.getElementsByTagName("common:path").item(0).getTextContent();
				this.getCurrentAffiliationByOrcid(this.orcid);
			} else if (list.getLength() > 1) {
				throw new Exception("find more than one ORCID with the given name");
			} else {
				throw new Exception("no orcid registered");
			}
		}
		else {
			client.close();
		}
	}

	private void searchForAffiliation(String givenName, String familyName, String affiliation) throws Exception {

		JerseyClient client = JerseyClientBuilder.createClient();

		String query = "";

		if (givenName != null && givenName != "") {

			if (query.length() > 0) {
				query = query + "+AND+given-names:" + givenName;
			} else {
				query = query + "given-names:" + givenName;
			}

		}
		if (familyName != null && familyName != "") {
			if (query.length() > 0) {
				query = query + "+AND+family-name:" + familyName;
			} else {
				query = query + "family-name:" + familyName;

			}
		}
		if (affiliation != null && affiliation != "") {
			if (query.length() > 0) {
				query = query + "+AND+affiliation-org-name:" + affiliation;
			} else {
				query = query + "affiliation-org-name:" + affiliation;

			}
		}

		WebTarget resource = client.target("https://pub.orcid.org/search/orcid-bio/?q=" + query);

		final Response response = resource.request("application/orcid+xml")
				.header("Authorization", "Bearer " + this.accessToken).get();

		if (response.getStatus() == 200) {

			String result = response.readEntity(String.class);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(result)));
			NodeList list = document.getElementsByTagName("orcid-search-results");

			if (Integer.valueOf(list.item(0).getAttributes().getNamedItem("num-found").getNodeValue()) == 1) {
				this.givenName = document.getElementsByTagName("given-names").item(0).getTextContent();
				this.familyName = document.getElementsByTagName("family-name").item(0).getTextContent();
				this.orcid = document.getElementsByTagName("orcid-identifier").item(0).getChildNodes().item(3)
						.getTextContent();

			} else if (Integer.valueOf(list.item(0).getAttributes().getNamedItem("num-found").getNodeValue()) > 1) {

				throw new Exception("find more than one ORCID with the given name: "
						+ Integer.valueOf(list.item(0).getAttributes().getNamedItem("num-found").getNodeValue()));
			} else {
				throw new Exception("no orcid registered");
			}

		}
		else {
			client.close();
		}
	}

}