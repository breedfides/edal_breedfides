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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
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
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.StoreDataScript;

class ExistingSearchTest extends EdalTestCaseWithoutShutdown{

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
    
    
    private static void log(String msg) {
    	DataManager.getImplProv().getLogger().info(msg);
    }


}

