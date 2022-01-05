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
package de.ipk_gatersleben.bit.bi.edal.data;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.ralfs.EdalDirectoryVisitorWithMetaData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

public class SearchTest extends EdalDefaultTestCase{
	
	private static Path PATH = Paths.get(System.getProperty("user.home"), "Search_Test");
	private static JSONObject json = new JSONObject();

	@SuppressWarnings("unchecked")
	@Test
	public void testSearch() throws Exception{
		createDataset();
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());
		JSONParser parser = new JSONParser();
		uploadZip(rootDirectory, "test1", "Schmid");
		uploadZip(rootDirectory, "test2", "Smith");
		uploadZip(rootDirectory, "test3", "Brown");
		DataManager.shutdown();
		
		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());
		
		json = (JSONObject) parser.parse("{\"existingQuery\":\"Subject:wheat\",\"hitType\":\"dataset\","
				+ "\"pagination\":[],\"pageIndex\":0,\"pageArraySize\":0,\"pageSize\":10,\"filters\":[],"
				+ "\"bottomResultId\":null,\"displayedPage\":1,\"queries\":[],\"whereToSearch\":\"Metadata\"}");
		JSONObject result = DataManager.advancedSearch(json);
		System.out.println(result.toJSONString());
		//find 3 datasets
		Assertions.assertEquals(3, result.get("hitSize"));
		
		json.put("hitType", PublicVersionIndexWriterThread.INDIVIDUALFILE);
		result = DataManager.advancedSearch(json);
		//find 9 files
		Assertions.assertEquals(9, result.get("hitSize"));
		
		json.put("whereToSearch", PublicVersionIndexWriterThread.CONTENT);
		json.put("existingQuery", new TermQuery(new Term(PublicVersionIndexWriterThread.CONTENT,"test")).toString());
		result = DataManager.advancedSearch(json);
		//search for content
		Assertions.assertEquals(9, result.get("hitSize"));
		JSONArray hitArray = (JSONArray) result.get("results");
		for(Object hit : hitArray) {
			String highlight = (String) ((JSONObject)hit).get("highlight");
			System.out.println(highlight);
			Assertions.assertTrue(highlight.contains("<B>test</B>"));
		}
		System.out.println(result.toJSONString());
	}
	@AfterEach
	public void clearTestFiles() throws IOException {
		FileUtils.deleteDirectory(new File(PATH.toString()));
	}
	static void createDataset() throws IOException {
		System.out.println("PREPARING");
		File dir = PATH.toFile();
		if(!dir.exists()) {
			System.out.println("creating "+PATH.toAbsolutePath().toString());
			dir.mkdir();
		}		
		for(int j = 0; j < 3; j++) {
			FileWriter myWriter = new FileWriter(Paths.get(PATH.toString(), "content_"+Math.pow(10, j)+".txt").toString());
			BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
			String s = "This is a test for indexing the content of text files.";
			int size = (int) ((Math.pow(10, j)*1024*1024)/s.getBytes().length);
			for(int i = 0; i < size; i++) {
				bufferedWriter.write(s);
			}
			myWriter.close();
		}
	}
	
	public static void uploadZip(PrimaryDataDirectory currentDirectory, String title, String author)
			throws MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			PrimaryDataDirectoryException, CloneNotSupportedException, PrimaryDataEntityException, AddressException,
			PublicReferenceException, IOException {
		System.out.println("UPLOADING A DATASET");
		PrimaryDataDirectory entity = currentDirectory.createPrimaryDataDirectory("100mb dateien 2012");
		MetaData metadata = entity.getMetaData().clone();
		Persons persons = new Persons();
		NaturalPerson np = new NaturalPerson("Peter", author,
				"2Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstraße 3",
				"06466", "Germany");
		persons.add(np);
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
						"IPK Gatersleben, Seeland OT Gatersleben, Corrensstraße 3", "06466", "Germany"));

		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("wheat"));
		subjects.add(new UntypedData("transcriptional network"));
		subjects.add(new UntypedData("genie3"));
		subjects.add(new UntypedData("transcriptomics"));
		EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
		metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
		metadata.setElementValue(EnumDublinCoreElements.TITLE,
				new UntypedData(title));
		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
				"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
		entity.setMetaData(metadata);
		EdalDirectoryVisitorWithMetaData edalVisitor = new EdalDirectoryVisitorWithMetaData(entity, PATH,
				metadata, true);
		Files.walkFileTree(PATH, edalVisitor);
		entity.addPublicReference(PersistentIdentifier.DOI);
		//entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress("ralfs@ipk-gatersleben.de"));

	}
}
