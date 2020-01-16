/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.test;

import java.net.InetSocketAddress;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

public class TestDataCiteRestAPI {

	public static void main(String[] args) throws ParseException {

		InetSocketAddress proxy = EdalConfiguration.guessProxySettings();
		System.setProperty("http.proxyHost", proxy.getHostName());
		System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
		System.setProperty("https.proxyHost", proxy.getHostName());
		System.setProperty("https.proxyPort", String.valueOf(proxy.getPort()));
		System.setProperty("java.net.useSystemProxies", "true");

		// get all by year and datacenter id

		WebTarget resource = JerseyClientBuilder.createClient()
				.target("https://api.datacite.org/works?data-center-id=tib.ipk&year=2017&page[size]=999");

		final Response request = resource.request(MediaType.APPLICATION_JSON).get();

		if (request.getStatus() == HttpStatus.OK_200) {

			String result = request.readEntity(String.class);

			// System.out.println(resultForAuthentication);

			JSONObject json = ((JSONObject) new JSONParser().parse(result));

			JSONArray array = (JSONArray) json.get("data");

			System.out.println(array.size());

		} else {
			System.out.println(request.readEntity(String.class));
		}

		////////////////////////////////////////

		WebTarget resource2 = JerseyClientBuilder.createClient()
				.target("https://api.datacite.org/works?query=10.5073&page[size]=1");

		final Response request2 = resource2.request(MediaType.APPLICATION_JSON).get();

		if (request2.getStatus() == HttpStatus.OK_200) {

			String result = request2.readEntity(String.class);

			// System.out.println(resultForAuthentication);

			JSONObject json = ((JSONObject) new JSONParser().parse(result));

			JSONArray array = (JSONArray) json.get("data");

			JSONObject object = (JSONObject) array.get(0);

			System.out.println(object);

			JSONObject object2 = (JSONObject) object.get("attributes");

			System.out.println(object2);

			String object3 = (String) object2.get("data-center-id");

			System.out.println(object3);

		} else {
			System.out.println(request2.readEntity(String.class));
		}
	}

}
