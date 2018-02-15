/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;

public class ORCID implements Serializable {

	private static final long serialVersionUID = -2975295104009712920L;
	private String orcid;

	/**
	 * Default constructor that check if the format and the checksum of the ORCID is
	 * valid
	 * 
	 * @param orcid
	 *            the ORCID String
	 * @throws ORCIDException
	 *             if the given ORCID is not valid
	 */
	public ORCID(String orcid) throws ORCIDException {

		if (orcid.matches("^\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9,X]")) {
			if (checkORCIDChecksum(orcid)) {
				this.orcid = orcid;
			} else {
				throw new ORCIDException("Illegal ORCID '" + orcid + "': ORCID checksum wrong");
			}
		} else {
			throw new ORCIDException("Illegal ORCID '" + orcid + "': Wrong format");
		}
	}

	/**
	 * Check ORCID checksum https://docs.google.com/spreadsheets/d/17KYZ-
	 * QMixxE55qxws2KovHy7LvC_ErfUKb2HzGiu3NQ/edit#gid=0
	 */
	private boolean checkORCIDChecksum(String orcid) {

		String baseDigits = orcid.replace("-", "");
		int[] sum = new int[baseDigits.length()];

		for (int i = 0; i < baseDigits.length() - 1; i++) {
			if (i == 0) {
				sum[i] = Character.getNumericValue(baseDigits.charAt(i)) * 2;
			} else {
				sum[i] = (Character.getNumericValue(baseDigits.charAt(i)) + sum[i - 1]) * 2;
			}
		}

		int remainder = sum[baseDigits.length() - 2] % 11;
		int checkDigit = (12 - remainder) % 11;

		if (checkDigit == Character.getNumericValue(baseDigits.charAt(baseDigits.length() - 1))) {
			return true;
		} else if (checkDigit == 10 && baseDigits.charAt(baseDigits.length() - 1) == 'X') {
			return true;
		}

		return false;

	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public String toString() {
		return orcid;

	}

	public static ORCID getOrcidByID(String orcidString) throws ORCIDException {

		ORCIDRestConnector connector = new ORCIDRestConnector();

		connector.searchForOrcid(orcidString);

		return new ORCID(orcidString);

	}

	public static List<ORCID> getOrcidsByName(String firstName, String LastName) throws ORCIDException {

		ORCIDRestConnector connector = new ORCIDRestConnector();

		return connector.searchForName(firstName, LastName);

	}

	public static NaturalPerson getPersonByOrcid(String orcidString) throws ORCIDException {
		ORCIDRestConnector connector = new ORCIDRestConnector();

		NaturalPerson person = connector.getPersonForOrcid(orcidString);

		person.setOrcid(new ORCID(orcidString));

		return person;
	}

	private static class ORCIDRestConnector {

		public class ConnectionFactoryWithProxy implements HttpURLConnectionFactory {

			Proxy.Type type;
			String proxyHost;
			int proxyPort;
			Proxy proxy;

			public ConnectionFactoryWithProxy(Proxy.Type type, String proxyHost, int proxyPort) {
				this.type = type;
				this.proxyHost = proxyHost;
				this.proxyPort = proxyPort;
			}

			private void initializeProxy() {
				proxy = new Proxy(this.type, new InetSocketAddress(this.proxyHost, this.proxyPort));
			}

			public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
				initializeProxy();
				return (HttpURLConnection) url.openConnection(proxy);
			}

		}

		private static final String CLIENT_ID = "QVBQLU9ONEgwSUcwWjYyQUJRUUI=";
		private static final String CLIENT_SECRET = "MjU3MDhjNjItZGI1Ny00NTBlLThkMmYtYjk1ZmQ3OTYzMTli";

		private final static File PATH_TO_ORCID_TOKEN = Paths
				.get(System.getProperty("user.home"), ".eDAL", "orcid_token.txt").toFile();

		private String accessToken = null;

		private static InetSocketAddress proxyAddress;
		private static boolean searchedForProxy = false;

		private URLConnectionClientHandler urlConnectionHandler = null;

		private ORCIDRestConnector() throws ORCIDException {

			if (!searchedForProxy) {
				proxyAddress = EdalConfiguration.guessProxySettings();
				searchedForProxy = true;
			}
			if (proxyAddress != null) {
				System.setProperty("http.proxyHost", proxyAddress.getHostName());
				System.setProperty("http.proxyPort", String.valueOf(proxyAddress.getPort()));
				System.setProperty("https.proxyHost", proxyAddress.getHostName());
				System.setProperty("https.proxyPort", String.valueOf(proxyAddress.getPort()));
				System.setProperty("java.net.useSystemProxies", "true");

				urlConnectionHandler = new URLConnectionClientHandler(new ConnectionFactoryWithProxy(Proxy.Type.HTTP,
						proxyAddress.getHostName(), proxyAddress.getPort()));

			}

			if (loadToken() == null) {
				this.accessToken = requestNewToken();
			} else {
				this.accessToken = loadToken();
			}
		}

		private String requestNewToken() throws ORCIDException {

			Client client = null;

			if (urlConnectionHandler == null) {
				client = Client.create();
			} else {
				client = new Client(urlConnectionHandler);
			}

			Form input = new Form();
			input.add("client_id", new String(Base64.getDecoder().decode(CLIENT_ID)));
			input.add("client_secret", new String(Base64.getDecoder().decode(CLIENT_SECRET)));
			input.add("scope", "/read-public");
			input.add("grant_type", "client_credentials");

			WebResource resource = client.resource("https://pub.orcid.org/oauth/token");

			final ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);

			if (response.getStatus() == 200) {
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) new JSONParser().parse((String) response.getEntity(String.class));
				} catch (ClientHandlerException | UniformInterfaceException | ParseException e) {
					throw new ORCIDException("Parsing of user token failed: " + e);
				}

				if (jsonObject.containsKey("access_token")) {
					accessToken = (String) jsonObject.get("access_token");
					try {
						saveTokenToProperties(accessToken);
						return accessToken;
					} catch (IOException e) {
						throw new ORCIDException("Saving authentication token failed");
					}
				}
			} else {
				throw new ORCIDException("Request for authentication token failed");
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

		private List<ORCID> searchForName(String givenName, String familyName) throws ORCIDException {

			givenName = givenName.replace(" ", "%20");
			familyName = familyName.replace(" ", "%20");

			Client client = null;

			if (urlConnectionHandler == null) {
				client = Client.create();
			} else {
				client = new Client(urlConnectionHandler);
			}

			WebResource resource = client.resource(
					"https://pub.orcid.org/v2.0/search/?q=given-names:" + givenName + "+AND+family-name:" + familyName);

			final ClientResponse response = resource.type("application/orcid+xml")
					.header("Authorization", "Bearer " + this.accessToken).get(ClientResponse.class);
			try {

				if (response.getStatus() == 200) {

					String result = response.getEntity(String.class);
					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse(new InputSource(new StringReader(result)));
					NodeList list = document.getElementsByTagName("search:result");

					if (list.getLength() == 1) {

						List<ORCID> orcids = new ArrayList<ORCID>(1);

						orcids.add(new ORCID(document.getElementsByTagName("common:path").item(0).getTextContent()));

						return orcids;

					} else if (list.getLength() > 1) {

						List<ORCID> orchids = new ArrayList<ORCID>(list.getLength() - 1);

						for (int i = 0; i < list.getLength(); i++) {
							Element e = (Element) list.item(i);
							orchids.add(new ORCID(e.getElementsByTagName("common:path").item(0).getTextContent()));
						}
						return orchids;

					} else {
						return new ArrayList<ORCID>(0);
					}
				}

			} catch (ParserConfigurationException | SAXException | IOException e) {
				throw new ORCIDException("Unable ro request ORCID API");
			}
			return null;
		}

		private void searchForOrcid(String orcid) throws ORCIDException {

			Client client = null;

			if (urlConnectionHandler == null) {
				client = Client.create();
			} else {
				client = new Client(urlConnectionHandler);
			}

			WebResource resource = client.resource("https://pub.orcid.org/search/orcid-bio/?q=orcid:" + orcid);

			final ClientResponse response = resource.type("application/orcid+xml")
					.header("Authorization", "Bearer " + this.accessToken).get(ClientResponse.class);

			if (response.getStatus() == 200) {

				String result = response.getEntity(String.class);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(result)));
					NodeList list = document.getElementsByTagName("orcid-search-results");
					if (list.item(0).getAttributes().getNamedItem("num-found").getNodeValue().equals("1")) {
						// ** okay**/
					} else {
						throw new ORCIDException("no orcid registered");
					}
				} catch (Exception e) {
					throw new ORCIDException("no orcid registered", e);
				}
			}
		}

		private NaturalPerson getPersonForOrcid(String orcid) throws ORCIDException {

			Client client = null;

			if (urlConnectionHandler == null) {
				client = Client.create();
			} else {
				client = new Client(urlConnectionHandler);
			}

			WebResource resource = client.resource("https://pub.orcid.org/v2.0/" + orcid + "/personal-details");

			final ClientResponse response = resource.type("application/orcid+xml")
					.header("Authorization", "Bearer " + this.accessToken).get(ClientResponse.class);

			if (response.getStatus() == 200) {

				String result = response.getEntity(String.class);
				try {

					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse(new InputSource(new StringReader(result)));

					Node foundedGivenName = document.getElementsByTagName("personal-details:given-names").item(0);
					Node foundedfamilyName = document.getElementsByTagName("personal-details:family-name").item(0);

					if (foundedGivenName != null && foundedfamilyName != null) {
						return new NaturalPerson(foundedGivenName.getTextContent(), foundedfamilyName.getTextContent(),
								"", "", "");

					} else {
						throw new ORCIDException("no orcid registered");
					}
				} catch (Exception e) {
					throw new ORCIDException("no orcid registered", e);
				}
			}
			return null;
		}
	}

}
