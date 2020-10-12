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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

class H2ChunkCorruptionTest extends EdalTestCaseWithoutShutdown {
	
//	private static int HTTPS_PORT = 8443;
//	private static int HTTP_PORT = 8080;
//	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
//	private static final String EMAIL = "user@nodomain.com.invalid";
//	private static final String DATACITE_PREFIX = "10.5072";
//	private static final String DATACITE_PASSWORD = "";
//	private static final String DATACITE_USERNAME = "";
//	private static final int MIN_PORT_NUMBER = 49152;
//	private static final int MAX_PORT_NUMBER = 65535;
//	public EdalConfiguration configuration = null;
//	public Path mountPath = null;
	private PrimaryDataDirectory rootDirectory;

	@Test
	void generateDataTest() throws Exception {
		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());
    	createAndInsert(100000);
    	DataManager.shutdown();
	}
	
	
	private void createAndInsert(int size) throws Exception {
		ArrayList<String> names = getList("src/test/resources/names.txt");
		ArrayList<String> words = getList("src/test/resources/words.txt");
		Path pathToRessource = Paths.get("src/test/resources/_TEST.zip");
		FileInputStream fin = new FileInputStream(pathToRessource.toFile());
		Locale[] locals = Locale.getAvailableLocales();
		int length = locals.length-1;
		int countNames =names.size();
		int countWords =words.size();
		Random random = new Random();
		Identifier referenceIdentifier = new Identifier(words.get(random.nextInt(countWords)));
		ArrayList<PrimaryDataFile> files = new ArrayList<>();
		PrimaryDataDirectory currentDirectory = null;
		String archiveName = "";
		for(int i = 0; i < size; i++) {
			if(i%1000 == 0) {
				archiveName = "LibraryV7."+(i/1000);
				currentDirectory = rootDirectory.createPrimaryDataDirectory(archiveName);
			}
			PrimaryDataFile entity = currentDirectory.createPrimaryDataFile("Entityp.."+i);
			MetaData metadata = entity.getMetaData();
			Persons persons = new Persons();
			NaturalPerson np = new NaturalPerson(names.get(random.nextInt(countNames)),names.get(random.nextInt(countNames)),words.get(random.nextInt(countWords)),words.get(random.nextInt(countWords)),words.get(random.nextInt(countWords)));
			persons.add(np);
			metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
//			metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,new LegalPerson(
//					names.get(random.nextInt(countNames)),words.get(random.nextInt(countWords)),
//					words.get(random.nextInt(countWords)),words.get(random.nextInt(countWords))));
			
			//Subjects subjects = new Subjects();
			//subjects.add(new UntypedData("Subject"+words.get(random.nextInt(countWords))));
			//EdalLanguage lang = new EdalLanguage(locals[random.nextInt(length)]);
			//metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
			//metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(words.get(random.nextInt(countWords))));
			//metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(words.get(random.nextInt(countWords))));
			//metadata.setElementValue(EnumDublinCoreElements.IDENTIFIER, referenceIdentifier);
			//entity.setMetaData(metadata);
			entity.store(fin);
			log(archiveName+"_ "+i+"/"+size+" Saved");
		}
		fin.close();
	}
	
//	@BeforeEach
//	public void setUp() throws Exception {
//
//		int port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
//
//		while (!available(port)) {
//			port = ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
//		}
//
//		H2ChunkCorruptionTest.HTTP_PORT = port;
//
//		try {
//			this.configuration = new EdalConfiguration(H2ChunkCorruptionTest.DATACITE_USERNAME,
//					H2ChunkCorruptionTest.DATACITE_PASSWORD, H2ChunkCorruptionTest.DATACITE_PREFIX,
//					new InternetAddress(H2ChunkCorruptionTest.EMAIL),
//					new InternetAddress(H2ChunkCorruptionTest.EMAIL),
//					new InternetAddress(H2ChunkCorruptionTest.EMAIL),
//					new InternetAddress(H2ChunkCorruptionTest.ROOT_USER)
//					,"imap.ipk-gatersleben.de","","");
//			this.configuration.setHttpPort(H2ChunkCorruptionTest.HTTP_PORT);
//			this.configuration.setHttpsPort(H2ChunkCorruptionTest.HTTPS_PORT);
//
//			mountPath = Paths.get(System.getProperty("user.home"), "edaltest", "SEARCHTESTS");
//			Files.createDirectories(mountPath);
//
//			this.configuration.setMountPath(mountPath);
//			this.configuration.setDataPath(mountPath);
//
//		} catch (EdalConfigurationException | AddressException e) {
//			throw new EdalException(e);
//		}
//	}
//	
//	public static boolean available(int port) {
//		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
//			throw new IllegalArgumentException("Invalid start port: " + port);
//		}
//
//		ServerSocket ss = null;
//		DatagramSocket ds = null;
//		try {
//			ss = new ServerSocket(port);
//			ss.setReuseAddress(true);
//			ds = new DatagramSocket(port);
//			ds.setReuseAddress(true);
//			return true;
//		} catch (IOException e) {
//		} finally {
//			if (ds != null) {
//				ds.close();
//			}
//
//			if (ss != null) {
//				try {
//					ss.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return false;
//	}
	
	public ArrayList<String> getList(String pathName) throws FileNotFoundException{
	File file = new File(pathName);
    ArrayList<String> strings = new ArrayList<String>();
    Scanner in = new Scanner(file);
    while (in.hasNextLine()){
        strings.add(in.nextLine());
    }
    return strings;
	}
	
    private static void log(String msg) {
    	DataManager.getImplProv().getLogger().info(msg);
    }

}
