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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

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
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.SearchProviderBreedFidesImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.SearchProviderImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.sample.*;

@SuppressWarnings("unused")
public class TestClass {

	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "arendd@ipk-gatersleben.de";

	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";

	private static void listDir(final PrimaryDataDirectory currentDirectory) throws PrimaryDataDirectoryException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();
		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				System.out.println(primaryDataEntity.getPath());
				if (primaryDataEntity.isDirectory()) {
					listDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
		}
	}

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration(DATACITE_USERNAME, DATACITE_PASSWORD, DATACITE_PREFIX,
				new InternetAddress(EMAIL), new InternetAddress(EMAIL), new InternetAddress(EMAIL),
				new InternetAddress(ROOT_USER), "imap.ipk-gatersleben.de", "", "");
		configuration.setUseSSL(true);

		FileSystemImplementationProvider filesystemImplementationProvider= (FileSystemImplementationProvider) EdalHelpers.getFileSystemImplementationProvider(false, configuration);
		
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				filesystemImplementationProvider,
				EdalHelpers.authenticateUserWithJWT("1677161018301"));
		
		

//		PrimaryDataDirectory dir =  (PrimaryDataDirectory) rootDirectory.getPrimaryDataEntity("1677161018301");

		
//		listDir(rootDirectory);
		
//		try {
//			SearchProviderBreedFidesImplementation si = (SearchProviderBreedFidesImplementation) filesystemImplementationProvider
//					.getSearchProviderBreedFides().getDeclaredConstructor().newInstance();
//
//			String set1 = si.breedFidesKeywordSearch(rootDirectory,"Arend", true, false,1,25);
//			System.out.println(set1);
//			
////			String set2 = si.breedFidesSearch(dir,"MIAPPE", true, true);
////			System.out.println(set2);
//			
//
//
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
//				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
//
//			e.printStackTrace();
//		}
//		
		
		
		
		
//
//
//		MetaData metadata = filesystemImplementationProvider.createMetaDataInstance();
//
//		Subjects subjectsMetadata = new Subjects();
//
//		subjectsMetadata.add(new UntypedData("vulgare"));
//
//		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjectsMetadata);
//
//		List<PrimaryDataEntity> list = dir.searchByMetaData(metadata, false, false);
//
//		System.out.println(list);
//		

		
//		System.out.println(dir.getID());
//		
//		List<PrimaryDataEntity> list2 = dir.searchByKeyword("MIAPPE", false, false);
//		List<PrimaryDataEntity> list3 = dir.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, new UntypedData("MIAPPE"), false, false);
//		
//		System.out.println(list2);
//		System.out.println(list3);

		
		DataManager.shutdown();
		

	}

}