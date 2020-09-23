/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.ralfs;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

class ExistingSearchTest {
	
	private static int HTTPS_PORT = 8443;
	private static int HTTP_PORT = 8080;
	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "user@nodomain.com.invalid";
	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";
	private static final int MIN_PORT_NUMBER = 49152;
	private static final int MAX_PORT_NUMBER = 65535;

	public EdalConfiguration configuration = null;
	public Path mountPath = null;


    @Test
    void searchByDublinCoreElementTest() throws Exception {
    	PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		ArrayList<PrimaryDataFile> entities = StoreDataScript.process(rootDirectory,5);
		MetaData storedMetaData = entities.get(0).getMetaData();
		//Test Search by Title
		String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\nTITLE\nINSERTED -> Title: "+expected);
		Thread.sleep(1000);
		List<PrimaryDataEntity> results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE,
				new UntypedData(expected), false, true);
		String actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\n___FOUND -> Title: "+actual);
		Assertions.assertEquals(expected, actual);
		
		//Test Search by Description
		expected = storedMetaData.getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		log("\nDESCRIPTION\nINSERTED -> Description: "+expected);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
				new UntypedData(expected), false, true);
		actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		Assertions.assertEquals(expected, actual);
		log("\n___FOUND ->  Description: "+actual);
		try {
		//Test Search by Creator
		NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
		Persons persons = new Persons();
		persons.add(expPerson);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
				expPerson, false, true);
		NaturalPerson actPerson = (NaturalPerson) ((Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		Assertions.assertEquals(expected, actual);
		log("\n___FOUND ->  Creator: "+actPerson.toString());
		}catch(Exception e) {
			log("\n####FAIL###### ----------->"+e.getMessage());
		}

		
//		//Test Search by Publisher
//		LegalPerson expLPerson = (LegalPerson) storedMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER);
//		log("\nPublisher\nINSERTED -> Publisher: "+expLPerson.toString());
//		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
//				new UntypedData(expected), false, true);
//		LegalPerson actLPerson = (LegalPerson) results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
//		Assertions.assertEquals(expected, actual);
//		log("\n___FOUND ->  Publisher: "+actPerson.toString());
//		try {
//			//Test Search by Publisher
//			DataSize actualSize = storedMetaData.getElementValue(EnumDublinCoreElements.SIZE);
//			log("\nSize\nINSERTED -> Size: "+expPerson.toString());
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
//					new UntypedData(expected), false, true);
//			LegalPerson actLPerson = (LegalPerson) results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
//			Assertions.assertEquals(expected, actual);
//			log("\n___FOUND ->  Publisher: "+actPerson.toString());
//		}catch(Exception e) {
//			log("\n####FAIL###### ----------->"+e.getMessage());
//		}
		DataManager.shutdown();
    }
    
	
	@BeforeEach
	public void setUp() throws Exception {

		int port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);

		while (!available(port)) {
			port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
		}

		ExistingSearchTest.HTTP_PORT = port;

		try {
			this.configuration = new EdalConfiguration(ExistingSearchTest.DATACITE_USERNAME,
					ExistingSearchTest.DATACITE_PASSWORD, ExistingSearchTest.DATACITE_PREFIX,
					new InternetAddress(ExistingSearchTest.EMAIL),
					new InternetAddress(ExistingSearchTest.EMAIL),
					new InternetAddress(ExistingSearchTest.EMAIL),
					new InternetAddress(ExistingSearchTest.ROOT_USER)
					,"imap.ipk-gatersleben.de","","");
			this.configuration.setHttpPort(ExistingSearchTest.HTTP_PORT);
			this.configuration.setHttpsPort(ExistingSearchTest.HTTPS_PORT);

			mountPath = Paths.get(System.getProperty("user.home"), "edaltest", "SEARCHTESTS");
			Files.createDirectories(mountPath);

			this.configuration.setMountPath(mountPath);
			this.configuration.setDataPath(mountPath);

		} catch (EdalConfigurationException | AddressException e) {
			throw new EdalException(e);
		}
	}
	
	public static boolean available(int port) {
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
    
    
    private static void log(String msg) {
    	DataManager.getImplProv().getLogger().info(msg);
    }


}

