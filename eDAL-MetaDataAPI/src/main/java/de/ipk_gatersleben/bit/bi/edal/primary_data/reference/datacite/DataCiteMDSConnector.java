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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.w3c.dom.Document;

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

	private WebTarget webTarget = null;
	private final JerseyClient dataCiteClient;

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
			this.dataCiteClient = JerseyClientBuilder.createClient();

			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().nonPreemptive()
					.credentials(DataCiteMDSConnector.dataCiteTestUser, DataCiteMDSConnector.dataCiteTestPassword)
					.build();
			this.dataCiteClient.register(feature);
		}

		else {
			try {
				DataCiteMDSConnector.dataCiteUser = configuration.getDataCiteUser();
				DataCiteMDSConnector.dataCitePassword = configuration.getDataCitePassword();
			} catch (final EdalConfigurationException e) {
				throw new EdalException("unable to init Configuration : " + e.getMessage(), e);
			}

			this.dataCiteClient = JerseyClientBuilder.createClient();

			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().nonPreemptive()
					.credentials(DataCiteMDSConnector.dataCiteUser, DataCiteMDSConnector.dataCitePassword).build();
			this.dataCiteClient.register(feature);

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
	 * @return a {@link Response} object.
	 */
	public Response getDOI(final String doi) {

		// set the URL to request the URL for a DOI
		if (testModus) {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_URL);
		}

		// append the path with the DOI and do the GET request
		final Response response = this.webTarget.path("/" + doi).request(MediaType.TEXT_HTML).get();

		return response;
	}

	/**
	 * Request the meta data for the given DOI.
	 * 
	 * @param doi
	 *            the requested DOI
	 * @return a {@link Response} object.
	 */
	public Response getMetadata(final String doi) {

		// set the URL to request meta data for a DOI
		if (testModus) {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.METADATA_TEST_URL);
		} else {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.METADATA_URL);
		}
		// append the path with the DOI and do the GET request
		final Response response = this.webTarget.path("/" + doi).request(MediaType.APPLICATION_XML_TYPE).get();

		return response;
	}

	/**
	 * Post a new DOI to DataCite.
	 * 
	 * @param doi
	 *            the new DOI.
	 * @param url
	 *            the corresponding URL for the new DOi
	 * @return a {@link Response} object.
	 * @throws DataCiteException
	 *             if {@link Response} is not a good response.
	 */

	public Response postDOI(final String doi, final String url) throws DataCiteException {

		// set the URL to post the URL for a DOI
		if (testModus) {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_URL);
		}
		final String requestBody = "doi=" + doi + "\r\n" + "url=" + url;

		String newRequestbody = requestBody;

		if (requestBody.contains("bit-252")) {
			newRequestbody = requestBody.replace("bit-252", "doi");
		}
		// set the content-type and request body, and post the request
		final Response response = this.webTarget.request(MediaType.TEXT_PLAIN_TYPE + ";charset=utf-8")
				.post(Entity.entity(newRequestbody, MediaType.TEXT_PLAIN_TYPE + ";charset=utf-8"));

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
	 * @return a {@link Response} object.
	 * @throws DataCiteException
	 *             if {@link Response} is not a good response.
	 */
	public Response postMetadata(final Document xml) throws DataCiteException {

		// set the URL to post the meta data for a DOI
		if (testModus) {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.METADATA_TEST_URL);
		} else {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.METADATA_URL);
		}

		final String requestBody = XmlFunctions.toString(xml);

		// set the content-type and request body, and post the request
		final Response response = this.webTarget.request(MediaType.APPLICATION_XML_TYPE + ";charset=utf-8")
				.post(Entity.entity(requestBody, MediaType.APPLICATION_XML_TYPE + ";charset=utf-8"));

		if (response.getStatus() != DataCiteMDSConnector.GOOD_RESPONSE) {
			throw new DataCiteException(response.getStatusInfo().getReasonPhrase());
		}

		return response;
	}

	public int getNextFreeDOI(int year, int startDoi, String datacentre) {
		// set the URL to request the URL for a DOI
		if (testModus) {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_TEST_URL);
		} else {
			this.webTarget = this.dataCiteClient.target(DataCiteMDSConnector.DOI_URL);
		}

		boolean isDoiFree = false;

		while (!isDoiFree) {
			// append the path with the DOI and do the GET request
			final Response response = this.webTarget
					.path("/" + this.prefix + "/" + datacentre + "/" + year + "/" + String.valueOf(startDoi))
					.request(MediaType.TEXT_HTML).get();

			if (response.getStatus() == 200) {
				startDoi++;
			} else {
				break;
			}
		}
		return startDoi;
	}
}