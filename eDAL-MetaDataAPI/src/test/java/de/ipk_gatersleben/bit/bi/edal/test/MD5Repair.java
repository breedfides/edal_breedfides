/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.internet.InternetAddress;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class MD5Repair {

	static List<Integer> CHECKSUM_IDs = new ArrayList<>();
	static HashMap<Integer, String> ID_TO_CHECKSUM_MAP = new HashMap<>();
	static HashMap<String, Integer> PRIMARYIDATAID_TO_CHECKSUM_ID = new HashMap<>();
	static HashMap<Integer, String> newMap = new HashMap<>();

	static void writeCheckSumIds() throws Exception {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		NativeQuery getAllCheckSumIDs = session.createSQLQuery(
				"SELECT ID FROM UNTYPEDDATA WHERE ALGORITHM is not null and CHECKSUM is not 'd41d8cd98f0b24e980998ecf8427e'");

//		NativeQuery getAllCheckSumIDs = session.createSQLQuery(
//				"SELECT ID FROM UNTYPEDDATA WHERE ALGORITHM is 'MD5'");

		
		CHECKSUM_IDs = getAllCheckSumIDs.getResultList();

		session.close();

		FileWriter writer = new FileWriter(Paths.get(System.getProperty("user.home"), "list1.txt").toFile());
		for (int i = 0; i < CHECKSUM_IDs.size(); i++) {
			String str = CHECKSUM_IDs.get(i).toString();
			writer.write(str);
			if (i < CHECKSUM_IDs.size() - 1) {
				writer.write("\n");
			}
		}
		writer.close();

	}

	static void readCheckSumIds() throws Exception {

		CHECKSUM_IDs = new ArrayList<>();

		File list_file = Paths.get(System.getProperty("user.home"), "list1.txt").toFile();

		try (BufferedReader br = new BufferedReader(new FileReader(list_file))) {
			for (String line; (line = br.readLine()) != null;) {
				CHECKSUM_IDs.add(Integer.valueOf(line));
			}
		}

	}

	static void writeIDsToCheckSumMap() throws Exception {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		for (Integer i : CHECKSUM_IDs) {
			NativeQuery getCheckSums = session
					.createSQLQuery("SELECT CHECKSUM FROM UNTYPEDDATA WHERE ALGORITHM is not null and ID = ?");

			getCheckSums.setInteger(1, i);

			ID_TO_CHECKSUM_MAP.put(i, (String) getCheckSums.uniqueResult());
		}

		session.close();

		Properties properties = new Properties();

		for (Entry<Integer, String> entry : ID_TO_CHECKSUM_MAP.entrySet()) {

			if (entry.getValue() != null) {
				properties.put(String.valueOf(entry.getKey()), entry.getValue());
			} else {
				System.out.println(entry.getKey());
			}
		}

		properties.store(new FileOutputStream(Paths.get(System.getProperty("user.home"), "map1.txt").toFile()), null);

	}

	static void readIDsToCheckSumMap() throws FileNotFoundException, IOException {

		File map = Paths.get(System.getProperty("user.home"), "map1.txt").toFile();

		Properties properties = new Properties();

		properties.load(new FileInputStream(map));

		for (String key : properties.stringPropertyNames()) {
			ID_TO_CHECKSUM_MAP.put(Integer.valueOf(key), properties.get(key).toString());
		}

	}

	static void writePrimaryDataIdToCheckSumId() throws Exception {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		for (Integer i : CHECKSUM_IDs) {
			NativeQuery getPrimaryDataIDs = session.createSQLQuery(
					"SELECT PRIMARYENTITYID FROM ENTITY_VERSIONS WHERE METADATA_ID in(SELECT METADATA_ID FROM METADATA_MAP WHERE MYMAP_ID in(SELECT UNTYPEDDATA_ID FROM UNTYPEDDATA_CHECKSUM WHERE DATASET_ID =?))");

			getPrimaryDataIDs.setInteger(1, i);

			if (getPrimaryDataIDs.uniqueResult() != null) {
				PRIMARYIDATAID_TO_CHECKSUM_ID.put((String) getPrimaryDataIDs.uniqueResult(), i);
			} else {
				System.out.println(getPrimaryDataIDs.list());
			}

		}

		session.close();

		Properties properties = new Properties();

		for (Entry<String, Integer> entry : PRIMARYIDATAID_TO_CHECKSUM_ID.entrySet()) {

			if (entry.getValue() == null || entry.getKey() == null) {
				System.out.println("NULL\t" + entry.getKey() + "\t" + entry.getValue());
			}

			if (entry.getKey() != null && entry.getValue() != null) {
				properties.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}

		properties.store(new FileOutputStream(Paths.get(System.getProperty("user.home"), "map2.txt").toFile()), null);

	}

	static void readPrimaryDataIdToCheckSumId() throws FileNotFoundException, IOException {

		File map = Paths.get(System.getProperty("user.home"), "map2.txt").toFile();

		Properties properties = new Properties();

		properties.load(new FileInputStream(map));

		for (String key : properties.stringPropertyNames()) {
			PRIMARYIDATAID_TO_CHECKSUM_ID.put(key, Integer.valueOf((String) properties.get(key)));
		}

	}

	private static void writeNewMap() throws Exception {

		File sha_map = Paths.get(System.getProperty("user.home"), "sha.txt").toFile();

		try (BufferedReader br = new BufferedReader(new FileReader(sha_map))) {

			for (String line; (line = br.readLine()) != null;) {
				String[] s = line.split("  ");

				String primaryid = s[1].substring(s[1].lastIndexOf("/") + 1, s[1].lastIndexOf("-"));

				String new_checksum = s[0];

				if (!new_checksum.equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")) {

					Integer checksumId = PRIMARYIDATAID_TO_CHECKSUM_ID.get(primaryid);

					if (checksumId != null) {
						newMap.put(checksumId, new_checksum);
					}
				}

			}
		}

		Properties properties = new Properties();

		for (Entry<Integer, String> entry : newMap.entrySet()) {

			if (entry.getValue() != null) {
				properties.put(String.valueOf(entry.getKey()), entry.getValue());
			} else {
				System.out.println(entry.getKey());
			}
		}

		properties.store(new FileOutputStream(Paths.get(System.getProperty("user.home"), "newmap.txt").toFile()), null);
	}

	private static void readNewMap() throws Exception {
		File map = Paths.get(System.getProperty("user.home"), "newmap.txt").toFile();

		Properties properties = new Properties();

		properties.load(new FileInputStream(map));

		for (String key : properties.stringPropertyNames()) {
			newMap.put(Integer.valueOf(key), (String) properties.get(key));
		}
	}

	static void updateChecksum() {

		try {

			Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			int i = 0;

			for (Entry<Integer, String> entry : newMap.entrySet()) {
				System.out.println(i);
				System.out.println("Execute Update");
				i++;
				Transaction trans = session.beginTransaction();

				NativeQuery sqlQuery = session
						.createSQLQuery("UPDATE UNTYPEDDATA SET CHECKSUM=?, ALGORITHM='SHA-256' WHERE ID=?");
				sqlQuery.setInteger(2, entry.getKey());
				sqlQuery.setString(1, entry.getValue());
				sqlQuery.executeUpdate();
				trans.commit();

			}
			session.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void updateChecksumForEmptyFiles() {

		try {

			Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			Transaction trans = session.beginTransaction();

			NativeQuery sqlQuery = session.createSQLQuery(
					"UPDATE UNTYPEDDATA SET CHECKSUM='e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',ALGORITHM='SHA-256' WHERE CHECKSUM='d41d8cd98f0b24e980998ecf8427e' AND ALGORITHM='MD5'");

			sqlQuery.executeUpdate();
			trans.commit();

			session.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));

		PrimaryDataDirectory root = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

//		Thread.sleep(Integer.MAX_VALUE);
		
		writeCheckSumIds();

//		readCheckSumIds();

		writeIDsToCheckSumMap();

//		readIDsToCheckSumMap();

		writePrimaryDataIdToCheckSumId();

//		readPrimaryDataIdToCheckSumId();

		writeNewMap();
//		readNewMap();

		updateChecksum();

//		updateChecksumForEmptyFiles();
		
		DataManager.shutdown();

	}

}
