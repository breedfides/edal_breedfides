/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite;

import javax.ws.rs.core.MediaType;

import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * {@link DataCiteMDSConnector} provides the interface between the DataCite
 * Rest-API and eDAL.
 * 
 * @author arendd
 */
public class DataCiteMDSConnector {

	private static final String DOI_URL = "https://mds.datacite.org/doi";
	private static final String METADATA_URL = "https://mds.datacite.org/metadata";
	private static final String DOI_TEST_URL = "https://mds.test.datacite.org/doi";
	private static final String METADATA_TEST_URL = "https://mds.test.datacite.org/metadata";

	private static final int GOOD_RESPONSE = 201;
	private static String dataCiteUser = "";
	private static String dataCitePassword = "";
	private static String dataCiteTestUser = "TIB";
	private static String dataCiteTestPassword = "edal0815";
	private String prefix = "";

	private static boolean testModus = false;

	private WebResource webResource = null;
	private final Client dataCiteClient;

	public DataCiteMDSConnector(EdalConfiguration configuration) throws EdalException {

		if (configuration.isUseSystemProxies()) {

			try {
				System.setProperty("http.proxyHost", configuration.getHttpProxyHost());
				System.setProperty("http.proxyPort", String.valueOf(configuration.getHttpProxyPort()));

				System.setProperty("https.proxyHost", configuration.getHttpsProxyHost());
				System.setProperty("https.proxyPort", String.valueOf(configuration.getHttpsProxyPort()));

				System.setProperty("java.net.useSystemProxies", "true");
			} catch (final EdalConfigurationException e) {
				throw new EdalException(e);
			}
		}

		if (configuration.isInTestMode()) {
			testModus = true;
			this.dataCiteClient = Client.create();
			this.dataCiteClient.addFilter(new HTTPBasicAuthFilter(dataCiteTestUser, dataCiteTestPassword));
		}

		else {
			try {
				DataCiteMDSConnector.dataCiteUser = configuration.getDataCiteUser();
				DataCiteMDSConnector.dataCitePassword = configuration.getDataCitePassword();
			} catch (final EdalConfigurationException e) {
				throw new EdalException("unable to init Configuration : " + e.getMessage(), e);
			}

			this.dataCiteClient = Client.create();
			this.dataCiteClient.addFilter(
					new HTTPBasicAuthFilter(DataCiteMDSConnector.dataCiteUser, DataCiteMDSConnector.dataCitePassword));

		}

		try {
			this.prefix = configuration.getDataCitePrefix();
		} catch (EdalConfigurationException e) {
			throw new EdalException("unable to load DataCite Prefix", e);
		}
	}

	/**
	 * Request the URL for the given DOI.
	 * 
	 * @param doi
	 *            the requested DOI
	 * @return a {@link ClientResponse} object.
	 */
	public ClientResponse getDOI(final String doi) {

		// set the URL to request the URL for a DOI
		if (testModus) {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_URL);
		}

		// append the path with the DOI and do the GET request
		final ClientResponse response = this.webResource.path("/" + doi).accept(MediaType.TEXT_HTML)
				.get(ClientResponse.class);

		return response;
	}

	/**
	 * Request the meta data for the given DOI.
	 * 
	 * @param doi
	 *            the requested DOI
	 * @return a {@link ClientResponse} object.
	 */
	public ClientResponse getMetadata(final String doi) {

		// set the URL to request meta data for a DOI
		if (testModus) {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.METADATA_TEST_URL);
		} else {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.METADATA_URL);
		}
		// append the path with the DOI and do the GET request
		final ClientResponse response = this.webResource.path("/" + doi).accept(MediaType.APPLICATION_XML_TYPE)
				.get(ClientResponse.class);

		return response;
	}

	/**
	 * Post a new DOI to DataCite.
	 * 
	 * @param doi
	 *            the new DOI.
	 * @param url
	 *            the corresponding URL for the new DOi
	 * @return a {@link ClientResponse} object.
	 * @throws DataCiteException
	 *             if {@link ClientResponse} is not a good response.
	 */

	public ClientResponse postDOI(final String doi, final String url) throws DataCiteException {

		// set the URL to post the URL for a DOI
		if (testModus) {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_URL);
		}
		final String requestBody = "doi=" + doi + "\r\n" + "url=" + url;

		String newRequestbody = requestBody;

		if (requestBody.contains("bit-252")) {
			newRequestbody = requestBody.replace("bit-252", "doi");
		}
		// set the content-type and request body, and post the request
		final ClientResponse response = this.webResource.type(MediaType.TEXT_PLAIN_TYPE + ";charset=utf-8")
				.entity(newRequestbody).post(ClientResponse.class);

		if (response.getStatus() != DataCiteMDSConnector.GOOD_RESPONSE) {
			throw new DataCiteException(response.getStatusInfo().getReasonPhrase());
		}

		return response;
	}

	/**
	 * Post a new meta data set for a DOI.
	 * 
	 * @param xml
	 *            a XML-{@link Document} object.
	 * @return a {@link ClientResponse} object.
	 * @throws DataCiteException
	 *             if {@link ClientResponse} is not a good response.
	 */
	public ClientResponse postMetadata(final Document xml) throws DataCiteException {

		// set the URL to post the meta data for a DOI
		if (testModus) {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.METADATA_TEST_URL);
		} else {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.METADATA_URL);
		}

		final String requestBody = XmlFunctions.toString(xml);

		// set the content-type and request body, and post the request
		final ClientResponse response = this.webResource.type(MediaType.APPLICATION_XML_TYPE + ";charset=utf-8")
				.entity(requestBody).post(ClientResponse.class);

		if (response.getStatus() != DataCiteMDSConnector.GOOD_RESPONSE) {
			throw new DataCiteException(response.getStatusInfo().getReasonPhrase());
		}

		return response;
	}

	public int getNextFreeDOI(int year, int startDoi, String datacentre) {
		// set the URL to request the URL for a DOI
		if (testModus) {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webResource = this.dataCiteClient.resource(DataCiteMDSConnector.DOI_URL);
		}

		boolean isDoiFree = false;

		while (!isDoiFree) {
			// append the path with the DOI and do the GET request
			final ClientResponse response = this.webResource
					.path("/" + this.prefix + "/" + datacentre + "/" + year + "/" + String.valueOf(startDoi))
					.accept(MediaType.TEXT_HTML).get(ClientResponse.class);

			if (response.getStatus() == 200) {
				startDoi++;
			} else {
				break;
			}
		}
		return startDoi;
	}
}