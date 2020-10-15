package ralfs.de.ipk_gatersleben.bit.bi.edal.examples;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.logging.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class FullExample {

	public static void main(String[] args) throws Exception {
//	PrimaryDataDirectory rootDirectory = getRoot();
//	Inserter inserter = new Inserter(rootDirectory);
//	inserter.process(rootDirectory, 2);
//	DataManager.shutdown();
		testSearchByDublin();
    }
	
	private static void testKeyword() throws Exception {
		PrimaryDataDirectory rootDirectory = getRoot();
    	try {
        	List<PrimaryDataEntity> results =  rootDirectory.searchByKeyword("text", false, true);
        	log("\n#### Result Size: "+results.size()+" ####\n");
        	for(PrimaryDataEntity entity : results) {
    			log("\n\n#### Entity: "+entity.toString()+" ####");
        		for(EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
        			log(element.toString()+": "+entity.getMetaData().getElementValue(element));
        		}
        	}
    	}catch(Exception e) {
    		log(e.getMessage());;
    	}
    	DataManager.shutdown();
	}
	
	public static PrimaryDataDirectory getRoot() throws Exception{
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("scientific_reviewer@mail.com"),
				new InternetAddress("substitute_reviewer@mail.com"), new InternetAddress("managing_reviewer@mail.com"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),"imap.ipk-gatersleben.de","","");
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		return rootDirectory;
	}
	
	public static ArrayList<String> getList(String pathName) throws FileNotFoundException{
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
    
    private static void testSearchByDublin() throws Exception {
		PrimaryDataDirectory rootDirectory = getRoot();
		ArrayList<PrimaryDataFile> entities = StoreDataScript.process(rootDirectory,2);
		MetaData storedMetaData = entities.get(0).getMetaData();
		//Test Search by Title
		String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\nTITLE\nINSERTED -> Title: "+expected);
		Thread.sleep(4000);
		List<PrimaryDataEntity> results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE,
				new UntypedData(expected), false, true);
		String actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\n___FOUND -> Title: "+actual);
		
		//Test Search by Description
		expected = storedMetaData.getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		log("\nDESCRIPTION\nINSERTED -> Description: "+expected);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
				new UntypedData(expected), false, true);
		actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		log("\n___FOUND ->  Description: "+actual);
		//Test Search by Creator
		NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
		Persons persons = new Persons();
		persons.add(expPerson);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
				expPerson, false, true);
		NaturalPerson actPerson = (NaturalPerson) ((Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		log("\nCreator\nFOUND -> Creator: "+actPerson.toString());
		//Test for Publisher
		LegalPerson expLp = storedMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nINSERTED: ->"+expLp.toString());
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.PUBLISHER, expLp, true, true);
		LegalPerson actLp = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nFOUND: ->"+expLp.toString());
		log("\n___FOUND ->  Publisher: "+actLp.toString());
			//Test for Identifier
			Identifier expIdent = storedMetaData.getElementValue(EnumDublinCoreElements.IDENTIFIER);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.IDENTIFIER,expIdent, false, true);
			Identifier actIdent = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.IDENTIFIER);
			
			CheckSumType expChecksum = ((CheckSum)storedMetaData.getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next();
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CHECKSUM, expChecksum, false, true);
			
			EdalLanguage expLang = (EdalLanguage)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.LANGUAGE, expLang, false, true);
			EdalLanguage actLang = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE);
			
			//Datasize
			DataSize expSize = (DataSize)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SIZE, expSize, false, true);
			DataSize actSize = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
			
//			Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
//			UntypedData expSubj = subjects.iterator().next();
//			log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
//			UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
//			log("\nFOUND -> Subject: "+actSubj.toString());
//			Assertions.assertEquals(expSubj, actSubj);
			try {
							
			//RELATION
				
				IdentifierRelation relation = (IdentifierRelation)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.RELATION);
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.RELATION, relation, false, true);
				String searchedRelation = relation.getRelations().iterator().next().getID();
				String foundRelation =((IdentifierRelation)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.RELATION)).getRelations().iterator().next().getID();
				log("\n STORED: "+searchedRelation+"\n FOUND: "+foundRelation);
				
//				Calendar lastEvents = (Calendar)date.getStartDate().clone();
//				lastEvents.set(Calendar.HOUR, 9);
//				EdalDateRange range = new EdalDateRange(lastEvents, EdalDatePrecision.SECOND, date.getStartDate(), EdalDatePrecision.SECOND, actual);
//				log("\nManipluiertes Date: "+range.toString());
//				expdate = new DateEvents("dates");
//				expdate.add(range);
//				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
//				actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
//				for(EdalDate e : actDates)
//					log("\nDATE: "+e.toString());
				//DATE
				
//				DateEvents expdate = new DateEvents("dates");
//				expdate = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE));
//				EdalDateRange date = null;
//				while(expdate.iterator().hasNext()) {
//					EdalDate temp = expdate.iterator().next();
//					if(temp instanceof EdalDateRange) {
//						date = (EdalDateRange) temp;
//					}
//				}
				EdalDate date = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE)).iterator().next();
				DateEvents expdate = new DateEvents("dates");
				expdate.add(date);
				for(EdalDate e : expdate)
					log("\nSearched DATE ->"+e.toString());
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
				DateEvents actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
				for(EdalDate e : actDates)
					log("\nFOUND DATE ->"+e.toString());
				log("result: "+actDates.compareTo(expdate));
//				expdate = storedMetaData.getElementValue(EnumDublinCoreElements.DATE);
				
				//results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE);
				//Test for Identifier
				Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
				UntypedData expSubj = subjects.iterator().next();
				log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, false, true);
				UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
				log("\nFOUND -> Subject: "+actSubj.toString());

			}catch(Exception e) {
				e.printStackTrace();
				log("\n####FAIL###### ----------->"+e.getMessage());
			}
			DataManager.shutdown();
    }
}