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
package de.ipk_gatersleben.bit.bi.edal.ralfs;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalApprovalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewResult;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

class ExistingSearchTest {
	
	private static int HTTPS_PORT = 8443;
	private static int HTTP_PORT = 8080;
	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "ralfs@ipk-gatersleben.de";
	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";
	private static final int MIN_PORT_NUMBER = 49152;
	private static final int MAX_PORT_NUMBER = 65535;

	public EdalConfiguration configuration = null;
	public Path mountPath = null;
	
	
	//@Test
    void searchByDublinCoreElementTest() throws Exception {
    	PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
//		Inserterr insert = new Inserterr(rootDirectory);
//		insert.process(1);
//		MetaData storedMetaData = insert.getSearchableMetaData();
		this.testKeywordSearch(rootDirectory);
		//Test Search by Title
		//this.searchbyDublin(storedMetaData, rootDirectory);
		DataManager.shutdown();
    }
	
	void searchbyDublin(MetaData storedMetaData, PrimaryDataDirectory rootDirectory) {
		try {
			String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
			log("\nTITLE\nINSERTED -> Title: "+expected);
			Thread.sleep(2000);
			List<PrimaryDataEntity> results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE,
					new UntypedData(expected), false, true);
			String actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.TITLE).getString();
			log("\n___FOUND -> Title: "+actual);
			Assertions.assertEquals(expected, actual);
			this.logEntities(results1);
			//Test Search by Description
			expected = storedMetaData.getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
			log("\nDESCRIPTION\nINSERTED -> Description: "+expected);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
					new UntypedData(expected), false, true);
			actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
			Assertions.assertEquals(expected, actual);
			log("\n___FOUND ->  Description: "+actual);
			this.logEntities(results1);
//			
//			
//			//Test Search by Creator
			NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
			log("\nLISTE VON PERSONEN ANZAHL: "+((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().size());
			log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
			Persons persons = new Persons();
			persons.add(expPerson);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
					expPerson, false, true);
			Persons foundPersons = (Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR);
			NaturalPerson actPerson = (NaturalPerson) foundPersons.getPersons().iterator().next();
			for(Person p : foundPersons) {
				log("\n Found Person: "+p.toString());
			}
			Assertions.assertEquals(expPerson, actPerson);
			this.logEntities(results1);
//			//Test for Publisher
			LegalPerson expLp = storedMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER);
			log("\nPublisher\nINSERTED: ->"+expLp.toString());
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.PUBLISHER, expLp, false, true);
			LegalPerson actLp = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
			log("\nPublisher\nFOUND: ->"+expLp.toString());
			Assertions.assertEquals(expLp, actLp);
			log("\n___FOUND ->  Publisher: "+actLp.toString());
			this.logEntities(results1);
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
			DateEvents expdate = new DateEvents("dates");
			DateEvents savedDates = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE));
			expdate.add(savedDates.iterator().next());
			for(EdalDate e : expdate)
				log("\nSearched DATE ->"+e.toString());
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
			for(int i = 0; i < results1.size(); i++) {
				DateEvents actDates = results1.get(i).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
				for(EdalDate e : actDates)
					log("\nFOUND DATE ->"+e.toString());
				log("result: "+actDates.compareTo(expdate));
			}
			//Assertions.assertTrue(actDates.compareTo(expdate) == 1);
			
			//RELATION
			IdentifierRelation relation = storedMetaData.getElementValue(EnumDublinCoreElements.RELATION);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.RELATION, relation, false, true);
			String searchedRelation = relation.getRelations().iterator().next().getIdentifier();
			String foundRelation =((IdentifierRelation)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.RELATION)).getRelations().iterator().next().getIdentifier();
			Assertions.assertEquals(searchedRelation, foundRelation);
			
			//CHecksum
			CheckSumType expChecksum = ((CheckSum)storedMetaData.getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next();
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CHECKSUM, expChecksum, false, true);
			Assertions.assertEquals(expChecksum, ((CheckSum)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next());
			
			//Subject
			Subjects expSubjects = storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
			UntypedData expSubj = expSubjects.iterator().next();
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
			UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).iterator().next();
			Assertions.assertEquals(actSubj, expSubj);
			
			String fuzzy = expSubj.getString();
			fuzzy = fuzzy.substring(0, fuzzy.length()-2);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, new UntypedData(fuzzy), true, true);
			Assertions.assertTrue(results1.size() > 0);
			//Lnaguage
			EdalLanguage expLang = (EdalLanguage)storedMetaData.getElementValue(EnumDublinCoreElements.LANGUAGE);
			log("\nLanguage\nFormat\nCreated ->: "+expLang.toString());
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.LANGUAGE, expLang, false, true);
			EdalLanguage actLang = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE);
			Assertions.assertEquals(expLang, actLang);
			log("\nFOUND -->"+actLang.toString());
			
			//Datasize
			DataSize expSize = storedMetaData.getElementValue(EnumDublinCoreElements.SIZE);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SIZE, expSize, false, true);
			DataSize actSize = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
			Assertions.assertEquals(expSize, actSize);
				
//			String subfile = StoreDataScript.getSubfileTitle(rootDirectory);
//			log("\nSEARCHED SUBDIRECTORY FILE "+subfile);
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData(subfile), false, true);
//			log("\nFOUND SUBDIRECTORY FILE "+results1.get(0).toString());
			//results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE);
//			
			
		}catch(Exception e) {
			e.printStackTrace();
			log("\n####FAIL###### ----------->"+e.getMessage());
		}
	}
    
    
    //@Test 
    void searchByDublinCorePerformance() throws Exception{
    	PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
    	long start = System.currentTimeMillis();
    	List<PrimaryDataEntity> results =  rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.PUBLISHER, new LegalPerson("Lukey","Klimaschrankes","Roggenernte","wegdruecktest"), false, true); 
    	long finish = System.currentTimeMillis();
    	log("Size: "+results.size()+"\nTime: "+(start-finish));
    	DataManager.shutdown();
    }
    
    void testKeywordSearch(PrimaryDataDirectory rootDirectory) throws Exception {
    	long start = System.currentTimeMillis();
    	List<PrimaryDataEntity> results =  rootDirectory.searchByKeyword("testing_directory", false, true);
    	long finish = System.currentTimeMillis();
    	try {
    		for(PrimaryDataEntity p : results) {
        		SortedSet<PrimaryDataEntityVersion> versions = p.getVersions();
    			log("######## Entity Versions: ########\n");
    			log("dirName: "+p.getName());
        		for(PrimaryDataEntityVersion version : versions) {
        			List<PublicReference> refs = version.getPublicReferences();
	            	for(PublicReference ref : refs) {
	            		log(" Reference: "+ref.toString());
	            		log(ref.getPublicationStatus().toString());
	            	}
	    			MetaData temp = version.getMetaData();
	    			log("######## Entity: ########\n");
		    		for(EnumDublinCoreElements element : EnumDublinCoreElements.values()){
		    			//if(temp.getElementValue(element) != null && element.equals(EnumDublinCoreElements.CREATOR))
		    				log(element.toString()+": "+temp.getElementValue(element).toString());
		    		}
        		}
        		p.addPublicReference(PersistentIdentifier.DOI);
        		// apply to publish all references
        		p.getCurrentVersion().setAllReferencesPublic(new InternetAddress("applicant@mail.de"));
        		Thread.sleep(10000);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	log("Size: "+results.size()+"\nTime: "+(finish-start));
    }
     
    //@Test
    void testMetaDataSearch() throws Exception {
    	PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		Inserterr insert = new Inserterr(rootDirectory);
		insert.process(1);
		MetaData metaData = insert.getSearchableMetaData();
		for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
			log("Searched MEtadata key: "+element.toString()+" value: "+metaData.getElementValue(element));
		}
    	List<PrimaryDataEntity> results =  rootDirectory.searchByMetaData(metaData, false, true);
		log("Result size after search: "+results.size());
    	for(PrimaryDataEntity primEntity : results) {
    		MetaData curMetaData = primEntity.getMetaData();
	    	if(!results.isEmpty()) {
				for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
					log("Found MEtadata key: "+element.toString()+" value: "+curMetaData.getElementValue(element));
				}
	    	}else {
	    		log("No identical Metadata found!");
	    	}
    	}
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
			this.configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);

			mountPath = Paths.get(System.getProperty("user.home"), "edaltest", "mapping4luc");
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
	
	private void logEntities(List<PrimaryDataEntity> list) {
		if(!list.isEmpty()) {
			log("############ Printing List of found Entities below: ############");
			for(PrimaryDataEntity entity : list) {
				MetaData metaData = entity.getMetaData();
				for(EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
					try {
						log(element.toString()+": "+metaData.getElementValue(element));
					} catch (MetaDataException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    
    private static void log(String msg) {
    	DataManager.getImplProv().getLogger().info(msg);
    }


}

