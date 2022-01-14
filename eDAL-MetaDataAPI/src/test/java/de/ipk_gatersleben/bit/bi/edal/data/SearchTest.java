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


import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
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
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.sample.Search;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class SearchTest extends EdalTestCaseWithoutShutdown{
	
	private static Path PATH = Paths.get(System.getProperty("user.home"), "Search_Test");
	private static JSONObject json = new JSONObject();
	private static HashMap<String,Object> map = new HashMap<String,Object>();
	private static final int THREAD_AMOUNT = 10;

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

		
		JSONObject result = Search.advancedSearch(json);
		System.out.println(result.toJSONString());
		//find 3 datasets
		Assertions.assertEquals(3, result.get("hitSize"));
		
		json.put("hitType", PublicVersionIndexWriterThread.INDIVIDUALFILE);
		result = Search.advancedSearch(json);
		//find 9 files
		Assertions.assertEquals(9, result.get("hitSize"));
		
		json.put("whereToSearch", PublicVersionIndexWriterThread.CONTENT);
		json.put("existingQuery", new TermQuery(new Term(PublicVersionIndexWriterThread.CONTENT,"test")).toString());
		result = Search.advancedSearch(json);
		//search for content
		Assertions.assertEquals(9, result.get("hitSize"));
		JSONArray hitArray = (JSONArray) result.get("results");
		for(Object hit : hitArray) {
			String highlight = (String) ((JSONObject)hit).get("highlight");
			Assertions.assertTrue(highlight.contains("<B>test</B>"));
		}
		
		class RunnableSearch implements Runnable{
			JSONObject json;
			int expected;
			CountDownLatch latch;
			public RunnableSearch(JSONObject json, CountDownLatch latch){
				this.json = json;
				this.expected = expected;
				this.latch = latch;
			}
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				for(int i = 0; i < 3; i++) {
					json.put("hitType", PublicVersionIndexWriterThread.PUBLICREFERENCE);
					json.put("whereToSearch", "Metadata");
					json.put("existingQuery", "Title:test1");
					JSONObject result = Search.advancedSearch(json);
					Assertions.assertEquals(1, result.get("hitSize"));
					
					json.put("existingQuery", "Creator:Smith");
					result = Search.advancedSearch(json);
					Assertions.assertEquals(1, result.get("hitSize"));
					
					json.put("hitType", PublicVersionIndexWriterThread.INDIVIDUALFILE);
					result = Search.advancedSearch(json);
					Assertions.assertEquals(3, result.get("hitSize"));
					
					json.put("existingQuery", "Creator:Peter");
					result = Search.advancedSearch(json);
					Assertions.assertEquals(9, result.get("hitSize"));
					
					json.put("existingQuery", "Content:test");
					json.put("whereToSearch", PublicVersionIndexWriterThread.CONTENT);
					result = Search.advancedSearch(json);
					Assertions.assertEquals(9, result.get("hitSize"));
				}
				latch.countDown();
			}
		}
		CountDownLatch latch = new CountDownLatch(THREAD_AMOUNT);
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
		for(int i = 0; i < THREAD_AMOUNT; i++) {
			executor.execute(new RunnableSearch(new JSONObject(map),latch));
		}
		executor.shutdown();
		latch.await();
		DataManager.shutdown();
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
			int size = ((10*1024*1024)/s.getBytes().length);
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


class EdalDirectoryVisitorWithMetaData implements FileVisitor<Path> {

	private SortedSet<String> pathSet = new TreeSet<>();
	private PrimaryDataDirectory currentDirectory = null;
	private int numberOfRootElements = 0;
	private boolean store = false;
	private MetaData metaData = null;

	public EdalDirectoryVisitorWithMetaData(
			PrimaryDataDirectory currentDirectory, Path path,
			MetaData metaData, boolean store) {
		this.currentDirectory = currentDirectory;
		this.numberOfRootElements = path.getNameCount() - 1;
		this.store = store;
		this.metaData = metaData;

		if (this.metaData != null) {
			setMetaData(this.currentDirectory);
		}
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {

		try {
			PrimaryDataDirectory newCurrentDirectory = currentDirectory
					.createPrimaryDataDirectory(dir.getFileName().toString());

			this.currentDirectory = newCurrentDirectory;
		} catch (PrimaryDataDirectoryException e) {
			throw new IOException(e);
		}
		/* else rootDirectory */
		if (numberOfRootElements < dir.getNameCount()) {

			Path tmpPath = dir
					.subpath(numberOfRootElements, dir.getNameCount());

			StringBuffer tmpBuffer = new StringBuffer("//");

			for (int i = 0; i < tmpPath.getNameCount(); i++) {
				tmpBuffer.append(tmpPath.getName(i) + "/");
			}
			/* cut last "/"-symbol */
			pathSet.add(tmpBuffer.toString().substring(0,
					tmpBuffer.toString().length() - 1));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {

		try {
			PrimaryDataFile pdf = currentDirectory.createPrimaryDataFile(file
					.getFileName().toString());

			if (store) {
				pdf.store(new FileInputStream(file.toFile()));
			}

		} catch (PrimaryDataDirectoryException | PrimaryDataFileException
				| PrimaryDataEntityVersionException e) {
			throw new IOException(e);
		}
		Path tmpPath = file.subpath(numberOfRootElements, file.getNameCount());

		StringBuffer tmpBuffer = new StringBuffer("//");

		for (int i = 0; i < tmpPath.getNameCount(); i++) {
			tmpBuffer.append(tmpPath.getName(i) + "/");
		}

		/* cut last "/"-symbol */
		pathSet.add(tmpBuffer.toString().substring(0,
				tmpBuffer.toString().length() - 1));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		try {
			currentDirectory = currentDirectory.getParentDirectory();
		} catch (PrimaryDataDirectoryException e) {
			throw new IOException(e);
		}

		return FileVisitResult.CONTINUE;
	}

	public SortedSet<String> getPathSet() {
		return pathSet;
	}

	private void setMetaData(PrimaryDataEntity entity) {

		try {
			MetaData m = entity.getMetaData().clone();

			m.setElementValue(EnumDublinCoreElements.CREATOR, this.metaData
					.getElementValue(EnumDublinCoreElements.CREATOR));
			m.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, this.metaData
					.getElementValue(EnumDublinCoreElements.CONTRIBUTOR));
			m.setElementValue(EnumDublinCoreElements.SUBJECT, this.metaData
					.getElementValue(EnumDublinCoreElements.SUBJECT));
			m.setElementValue(EnumDublinCoreElements.LANGUAGE, this.metaData
					.getElementValue(EnumDublinCoreElements.LANGUAGE));
			m.setElementValue(EnumDublinCoreElements.DESCRIPTION, this.metaData
					.getElementValue(EnumDublinCoreElements.DESCRIPTION));
			m.setElementValue(EnumDublinCoreElements.PUBLISHER, this.metaData
					.getElementValue(EnumDublinCoreElements.PUBLISHER));
			m.setElementValue(EnumDublinCoreElements.RIGHTS, this.metaData
					.getElementValue(EnumDublinCoreElements.RIGHTS));
			m.setElementValue(EnumDublinCoreElements.SOURCE, this.metaData
					.getElementValue(EnumDublinCoreElements.SOURCE));

			entity.setMetaData(m);

		} catch (CloneNotSupportedException | MetaDataException
				| PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		}
	}

}
