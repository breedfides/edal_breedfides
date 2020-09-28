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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.logging.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class FullExample {

	public static void main(String[] args) throws Exception {
		
		
		ArrayList<String> names = getList("src/test/resources/names.txt");
		ArrayList<String> words = getList("src/test/resources/words.txt");
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("scientific_reviewer@mail.com"),
				new InternetAddress("substitute_reviewer@mail.com"), new InternetAddress("managing_reviewer@mail.com"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),"imap.ipk-gatersleben.de","","");
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		ArrayList<PrimaryDataFile> entities = StoreDataScript.process(rootDirectory,1);
		MetaData storedMetaData = entities.get(0).getMetaData();
		//Test Search by Title
		String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\nTITLE\nINSERTED -> Title: "+expected);
		Thread.sleep(1000);
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
//		NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
//		log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
//		Persons persons = new Persons();
//		persons.add(expPerson);
//		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
//				expPerson, false, true);
//		NaturalPerson actPerson = (NaturalPerson) ((Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
//		log("\nCreator\nFOUND -> Creator: "+actPerson.toString());
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
			
			
//			Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
//			UntypedData expSubj = subjects.iterator().next();
//			log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
//			UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
//			log("\nFOUND -> Subject: "+actSubj.toString());
//			Assertions.assertEquals(expSubj, actSubj);

			//DATE
			//DATE
			EdalDate date = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE)).iterator().next();
			DateEvents expdate = new DateEvents("dates");
			expdate.add(date);
			log("\nSearched DATE ->"+date.toString());
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
			DateEvents actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
			for(EdalDate e : actDates)
				log("\nFOUND DATE ->"+e.toString());
			try {
							
			//RELATION
				IdentifierRelation relation = (IdentifierRelation)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.RELATION);
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.RELATION, relation, false, true);
				log("\n STORED: "+entities.get(1).getMetaData().getElementValue(EnumDublinCoreElements.TITLE).toString()+"\n FOUND: "+results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.TITLE));
				
				Subjects expSubjects = entities.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT);
				UntypedData expSubj = expSubjects.iterator().next();
				String fuzzy = expSubj.getString();
				fuzzy = fuzzy.substring(1, fuzzy.length());
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, new UntypedData(fuzzy), true, true);
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
				
				//results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE);
				//Test for Identifier
//				Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
//				UntypedData expSubj = subjects.iterator().next();
//				log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
//				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
//				UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
//				log("\nFOUND -> Subject: "+actSubj.toString());
//				Assertions.assertEquals(expSubj, actSubj);

			}catch(Exception e) {
				e.printStackTrace();
				log("\n####FAIL###### ----------->"+e.getMessage());
			}
			DataManager.shutdown();
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
}