/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

/**
 * Class for generating GPS locations from IP by using Geo2IP API
 * 
 * @author arendd
 *
 */
public class GenerateLocations {

	private static DatabaseReader reader = null;

	private static JSONArray allIPsList = new JSONArray();

	public static JSONArray getAllIPsList() {
		return allIPsList;
	}

	static {

		try {
			reader = new DatabaseReader.Builder(GenerateLocations.class.getResourceAsStream("GeoLite2-City.mmdb"))
					.withCache(new CHMCache(1024 * 16)).build();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String generateGpsLocations(HashSet<String> ipList) {

		StringBuffer locationXML = new StringBuffer();

		locationXML.append("<markers>");

		for (String ip : ipList) {

			try {
				InetAddress ipAddress = InetAddress.getByName(ip);
				CityResponse response = reader.city(ipAddress);
				Location location = response.getLocation();

				if (location != null) {
					String str = "";

					String cityName = response.getCity().getName();

					if (cityName != null) {

						if (cityName.contains("'")) {
							cityName = cityName.replace("'", " ");
						}
						str = "<marker lat='" + location.getLatitude() + "' lng='" + location.getLongitude()
								+ "' title='" + cityName + "' percent='" + "'></marker>";
					} else {
						str = "<marker lat='" + location.getLatitude() + "' lng='" + location.getLongitude()
								+ "' title='' percent='" + "'></marker>";
					}

					locationXML.append(str);
				}
			} catch (Exception e) {
			}

		}
		locationXML.append("</markers>");
		return locationXML.toString();
	}

	@SuppressWarnings("unchecked")
	public static JSONArray generateGpsLocationsToJson(HashSet<String> ipList) {

		JSONArray list = new JSONArray();

		for (String ip : ipList) {

			try {
				InetAddress ipAddress = InetAddress.getByName(ip);
				CityResponse response = reader.city(ipAddress);
				Location location = response.getLocation();

				if (location != null) {
					JSONObject str = new JSONObject();

					String cityName = response.getCity().getName();

					if (cityName != null) {

						if (cityName.contains("'")) {
							cityName = cityName.replace("'", " ");
						}
						str.put("lat", location.getLatitude());
						str.put("long", location.getLongitude());
						str.put("city", cityName);

					} else {
						str.put("lat", location.getLatitude());
						str.put("long", location.getLongitude());
						str.put("city", null);
					}

					if (!list.contains(str)) {
						list.add(str);
					}
					if (!allIPsList.contains(str)) {
						allIPsList.add(str);
					}
				}
			} catch (IOException | GeoIp2Exception  e) {
//				e.printStackTrace();
			}

		}
		return list;
	}

}