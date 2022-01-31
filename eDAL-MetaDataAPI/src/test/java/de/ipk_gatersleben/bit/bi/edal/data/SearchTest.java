/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.internet.AddressException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.helper.EdalDirectoryVisitorWithMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.EnumIndexField;
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
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

public class SearchTest extends EdalDefaultTestCase{
	
	private static Path PATH = Paths.get(System.getProperty("user.home"), "Search_Test");
	private static JSONObject json = new JSONObject();
	private static HashMap<String,Object> map = new HashMap<String,Object>();
	private static final int THREAD_AMOUNT = 20;

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
		map.put("existingQuery", "Subject:wheat");
		map.put("hitType", PublicVersionIndexWriterThread.PUBLICREFERENCE);
		map.put("pageIndex", 0l);
		map.put("pageArraySize", 0l);
		map.put("pageSize", 10l);
		map.put("filters", new JSONArray());
		map.put("queries", new JSONArray());
		map.put("bottomResultId", null);
		map.put("displayedPage", 1l);
		map.put("whereToSearch", "Metadata");		
		json = new JSONObject(map);
		SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();

		
		JSONObject result = searchProvider.advancedSearch(json);
		//find 3 datasets
		Assertions.assertEquals(3, result.get("hitSize"));
		
		json.put("hitType", PublicVersionIndexWriterThread.FILE);
		result = searchProvider.advancedSearch(json);
		//find 9 files
		Assertions.assertEquals(9, result.get("hitSize"));
		
		json.put("whereToSearch", EnumIndexField.CONTENT.value());
		json.put("existingQuery", new TermQuery(new Term(EnumIndexField.CONTENT.value(),"test")).toString());
		result = searchProvider.advancedSearch(json);
		//search for content
		Assertions.assertEquals(9, result.get("hitSize"));
		JSONArray hitArray = (JSONArray) result.get("results");
		for(Object hit : hitArray) {
			String highlight = (String) ((JSONObject)hit).get("highlight");
			Assertions.assertTrue(highlight.contains("<B>test</B>"));
		}
		
		class RunnableSearch implements Runnable{
			JSONObject json;
			CountDownLatch latch;
			public RunnableSearch(JSONObject json, CountDownLatch latch){
				this.json = json;
				this.latch = latch;
			}
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					SearchProvider threadSearchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
					for(int i = 0; i < 5; i++) {
						json.put("hitType", PublicVersionIndexWriterThread.PUBLICREFERENCE);
						json.put("whereToSearch", "Metadata");
						json.put("existingQuery", "Title:test1");
						JSONObject result = threadSearchProvider.advancedSearch(json);
						Assertions.assertEquals(1, result.get("hitSize"));
						
						json.put("existingQuery", "Creator:Smith");
						result = threadSearchProvider.advancedSearch(json);
						Assertions.assertEquals(1, result.get("hitSize"));
						
						json.put("hitType", PublicVersionIndexWriterThread.FILE);
						result = threadSearchProvider.advancedSearch(json);
						Assertions.assertEquals(3, result.get("hitSize"));
						
						json.put("existingQuery", "Creator:Peter");
						result = threadSearchProvider.advancedSearch(json);
						Assertions.assertEquals(9, result.get("hitSize"));
						
						json.put("existingQuery", "Content:test");
						json.put("whereToSearch", EnumIndexField.CONTENT.value());
						result = threadSearchProvider.advancedSearch(json);
						Assertions.assertEquals(9, result.get("hitSize"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				latch.countDown();
			}
		}
		CountDownLatch latch = new CountDownLatch(THREAD_AMOUNT);
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
		for(int i = 0; i < THREAD_AMOUNT; i++) {
			executor.execute(new RunnableSearch(new JSONObject(map),latch));
		}
		latch.await();
		executor.shutdownNow();
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