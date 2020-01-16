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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JOptionPane;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.sample.*;

@SuppressWarnings("unused")
public class TestOrcid {

	public static void main(final String[] args) throws Exception {
//		EdalConfiguration configuration = null;
//
//		try {
//			configuration = new EdalConfiguration("", "", "10.5072", new InternetAddress("arendd@ipk-gatersleben.de"),
//					new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
//					new InternetAddress("eDAL0815@ipk-gatersleben.de"));
//		} catch (AddressException | EdalConfigurationException e1) {
//			e1.printStackTrace();
//		}
//		
//		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
//				EdalHelpers.getFileSystemImplementationProvider(true,
//						configuration), EdalHelpers
//						.authenticateWinOrUnixOrMacUser());
//	
//		PrimaryDataFile file = rootDirectory.createPrimaryDataFile("TestFile.txt");
//		
//		MetaData metadata = file.getMetaData().clone();
//		
//		Persons person = new Persons();
//		person.add(new NaturalPerson("Daniel", "Arend", "address", "zip", "country", new ORCID("0000-0002-2455-593X")));
//		metadata.setElementValue(EnumDublinCoreElements.CREATOR, person);
//		metadata.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, person);
//
//		
//
//		DataManager.shutdown();
//		new ORCID("0000-0003-1415-9269");
//		
//		new ORCID("0000-0002-2455-5938");
//		
//		new ORCID("0000-0002-4316-078X");
		
		
//		System.out.println(ORCID.getOrcidByID("0000-0002-2455-5938"));
		
//		System.out.println(ORCID.getOrcidsByName("Matthias", "Lange"));
//		
//		System.out.println(ORCID.getOrcidsByName("Daniel", "Arend"));
//		
//		System.out.println(ORCID.getOrcidsByName("Uwe", "Scholz"));
//		
		System.out.println(ORCID.getPersonByOrcid("0000-0002-2455-5938"));

		
	}

}