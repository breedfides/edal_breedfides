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
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;

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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.StoreDataScript;

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
		ArrayList<PrimaryDataFile> entities = StoreDataScript.process(rootDirectory,2);
		MetaData storedMetaData = entities.get(0).getMetaData();
		//Test Search by Title
		String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\nTITLE\nINSERTED -> Title: "+expected);
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
		//Test Search by Creator
//		NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
//		log("\nLISTE VON PERSONEN ANZAHL: "+((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().size());
//		log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
//		Persons persons = new Persons();
//		persons.add(expPerson);
//		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
//				expPerson, true, true);
//		NaturalPerson actPerson = (NaturalPerson) ((Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
//		log("\nCreator\nFOUND -> Creator: "+actPerson.toString());
//		Assertions.assertEquals(expPerson, actPerson);
		//Test for Publisher
		LegalPerson expLp = storedMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nINSERTED: ->"+expLp.toString());
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.PUBLISHER, expLp, false, true);
		LegalPerson actLp = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nFOUND: ->"+expLp.toString());
		Assertions.assertEquals(expLp, actLp);
		log("\n___FOUND ->  Publisher: "+actLp.toString());
		//Identifier
		Identifier expIdent = storedMetaData.getElementValue(EnumDublinCoreElements.IDENTIFIER);
		log("\nIdentifier\nCreated Identifier ->: "+expIdent.toString());
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.IDENTIFIER,expIdent, false, true);
		Identifier actIdent = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.IDENTIFIER);
		log("\nFOUND -> "+actIdent.toString());
		Assertions.assertEquals(expIdent, actIdent);
		//Format
		DataFormat expdt = storedMetaData.getElementValue(EnumDublinCoreElements.FORMAT);
		log("\nDATAFORMAT\nFormat\nCreated ->: "+expdt.toString());
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.FORMAT, expdt, false, true);
		DataFormat actdt = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.FORMAT);
		Assertions.assertEquals(expdt, actdt);
		log("\nFOUND -->"+actdt.toString());
		//DATE
//		EdalDate date = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE)).iterator().next();
//		DateEvents expdate = new DateEvents("dates");
//		expdate.add(date);
//		log("\nSearched DATE ->"+date.toString());
//		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
//		DateEvents actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
//		for(EdalDate e : actDates)
//			log("\nFOUND DATE ->"+e.toString());
//		Assertions.assertEquals(storedMetaData.getElementValue(EnumDublinCoreElements.DATE), actDates);
		try {
		//RELATION
			IdentifierRelation relation = (IdentifierRelation)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.RELATION);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.RELATION, relation, false, true);
			Assertions.assertEquals(entities.get(entities.size()-1), results1.get(0));
			
			CheckSumType expChecksum = ((CheckSum)storedMetaData.getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next();
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CHECKSUM, expChecksum, false, true);
			Assertions.assertEquals(expChecksum, ((CheckSum)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next());
			
			Subjects expSubjects = entities.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT);
			UntypedData expSubj = expSubjects.iterator().next();
			String fuzzy = expSubj.getString();
			fuzzy = fuzzy.substring(1, fuzzy.length());
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, new UntypedData(fuzzy), true, true);
			Assertions.assertEquals(expSubj, ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).iterator().next());
//			Calendar lastEvents = (Calendar)date.getStartDate().clone();
//			lastEvents.set(Calendar.HOUR, 9);
//			EdalDateRange range = new EdalDateRange(lastEvents, EdalDatePrecision.SECOND, date.getStartDate(), EdalDatePrecision.SECOND, actual);
//			log("\nManipluiertes Date: "+range.toString());
//			expdate = new DateEvents("dates");
//			expdate.add(range);
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
//			actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
//			for(EdalDate e : actDates)
//				log("\nDATE: "+e.toString());
			
			//results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE);
			//Test for Identifier
//			Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
//			UntypedData expSubj = subjects.iterator().next();
//			log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
//			UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
//			log("\nFOUND -> Subject: "+actSubj.toString());
//			Assertions.assertEquals(expSubj, actSubj);

		}catch(Exception e) {
			e.printStackTrace();
			log("\n####FAIL###### ----------->"+e.getMessage());
		}
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

