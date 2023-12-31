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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * Connector to the REST-API to search for DOIs.
 * 
 * @author arendd
 */
public class DataCiteRestConnector {

	private String prefix = "";
	private JerseyClient restClient = null;
	private WebTarget webTarget = null;
	private EdalConfiguration configuration = null;

	/**
	 * Constructor to initialize a REST-Connection to DateCite.
	 * 
	 * @param configuration the {@link EdalConfiguration} to use
	 * @throws DataCiteException if unable to create DataCiteRestConnector.
	 */
	public DataCiteRestConnector(EdalConfiguration configuration) throws DataCiteException {

		this.configuration = configuration;

		if (configuration.isUseSystemProxies()) {

			try {
				System.setProperty("http.proxyHost", configuration.getHttpProxyHost());
				System.setProperty("http.proxyPort", String.valueOf(configuration.getHttpProxyPort()));
				System.setProperty("https.proxyHost", configuration.getHttpsProxyHost());
				System.setProperty("https.proxyPort", String.valueOf(configuration.getHttpsProxyPort()));
				System.setProperty("java.net.useSystemProxies", "true");
			} catch (final EdalConfigurationException e) {
				throw new DataCiteException(e);
			}
		}

		try {
			this.prefix = configuration.getDataCitePrefix();
		} catch (EdalConfigurationException e) {
			throw new DataCiteException("unable to load DataCite Prefix", e);
		}
	}

	/**
	 * Get the string for the data center of the given prefix
	 * 
	 * @param prefix the prefix for this data center
	 * @return the data center string
	 * @throws DataCiteException if unable to query the data center
	 */
	private String getDataCentreIdForPrefix(String prefix) throws DataCiteException {

		this.restClient = JerseyClientBuilder.createClient();

		this.webTarget = this.restClient.target("https://api.datacite.org/prefixes/" + prefix);

		final Response response = this.webTarget.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == 200) {

			try {
				JSONObject json = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
				JSONObject data = (JSONObject) json.get("data");
				JSONObject relationships = (JSONObject) data.get("relationships");
				JSONObject clients = (JSONObject) relationships.get("clients");
				JSONArray data2 = (JSONArray) clients.get("data");
				JSONObject id = (JSONObject) data2.get(0);
				String dataCentreString = (String) id.get("id");

				return dataCentreString;

			} catch (NullPointerException | ParseException e) {
				throw new DataCiteException("unable to query the data centre ID", e);
			}

		} else {
			this.restClient.close();
			throw new DataCiteException("unable to query the data centre ID");
		}

	}

	/**
	 * Generate a new DOI by counting current DOI
	 * 
	 * @param year the current year.
	 * @return the new DOI
	 * @throws DataCiteException if unable to generate new DOI.
	 */
	public String generateNewDOI(int year) throws DataCiteException {

		try {

			if (this.configuration.getDoiInfix() != null) {

				DataCiteMDSConnector connector = new DataCiteMDSConnector(this.configuration);

				int nextFreeDoiNumber = connector.getNextFreeDOI(year, 0, this.configuration.getDoiInfix());

				return this.prefix + "/" + this.configuration.getDoiInfix() + "/" + year + "/" + nextFreeDoiNumber;
			} else {

				DataCiteMDSConnector connector = new DataCiteMDSConnector(this.configuration);

				String dataCentreString = getDataCentreIdForPrefix(this.prefix);
				String dataCentreName = dataCentreString.substring(dataCentreString.indexOf(".") + 1,
						dataCentreString.length());

				int nextFreeDoiNumber = connector.getNextFreeDOI(year,
						getNumberOfResolvableDOIsByPrefix(year, this.prefix), dataCentreName);

				return this.prefix + "/" + dataCentreName + "/" + year + "/" + nextFreeDoiNumber;
			}

		} catch (EdalException e) {
			throw new DataCiteException("unable to get number of stored DOIs", e);
		}

	}

	/**
	 * Get the number of currently registered DOIs
	 * 
	 * @param year   the current year.
	 * @param prefix the prefix of the datacentre
	 * @return the number of DOIs
	 * @throws DataCiteException if unable to query DOIs
	 */
	private int getNumberOfResolvableDOIsByPrefix(int year, String prefix) throws DataCiteException {

		this.restClient = JerseyClientBuilder.createClient();

		this.webTarget = this.restClient.target(
				"https://api.datacite.org/works?query=prefix:" + prefix + "&registered=" + year + "&page[size]=999");

		final Response response = this.webTarget.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == 200) {

			try {
				JSONObject json = (JSONObject) new JSONParser().parse(response.readEntity(String.class));

				JSONArray data = (JSONArray) json.get("data");

				return data.size();

			} catch (NullPointerException | ParseException e) {
				throw new DataCiteException("unable to query the number of stored DOIs", e);
			}

		} else {
			this.restClient.close();
			throw new DataCiteException("unable to query the number of stored DOIs");
		}
	}

	public boolean checkIfPrefixIsRegisteredForDataCenterId() {

		this.restClient = JerseyClientBuilder.createClient();

		this.webTarget = this.restClient.target("https://api.datacite.org/prefixes/" + this.prefix);

		final Response response = this.webTarget.request(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() == 200) {

			try {
				JSONObject json = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
				JSONObject data = (JSONObject) json.get("data");
				JSONObject relationships = (JSONObject) data.get("relationships");
				JSONObject clients = (JSONObject) relationships.get("clients");
				JSONArray data2 = (JSONArray) clients.get("data");
				JSONObject id = (JSONObject) data2.get(0);

				String dataCentreString = (String) id.get("id");

				if (dataCentreString.toLowerCase().equals(this.configuration.getDataCiteUser().toLowerCase())) {
					return true;
				}

			} catch (NullPointerException | ParseException | EdalConfigurationException e) {
				return false;
			}
		} else {
			this.restClient.close();
			return false;
		}
		return false;

	}
}
