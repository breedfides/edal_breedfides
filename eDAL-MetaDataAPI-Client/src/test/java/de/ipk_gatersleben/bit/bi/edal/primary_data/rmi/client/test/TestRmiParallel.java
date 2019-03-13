/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
/**
 * uncommented due to some problems with junit 5 and jdk 11 
 */
//package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.config.Configurator;
//import org.apache.logging.log4j.core.util.Loader;
//import org.junit.Test;
//
//import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
//import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
//import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
//import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
//import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
//import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
//import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
//import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
//
//public class TestRmiParallel extends ClientServerTest {
//
//	private static ClientPrimaryDataDirectory rootDirectory;
//
//	private static class PseudoReader {
//
//		private Path PATH = null;
//
//		private ClientPrimaryDataDirectory root = null;
//
//		public PseudoReader(ClientPrimaryDataDirectory root) {
//			try {
//				PATH = Paths.get(Loader.getResource("bmc_article.zip", null).toURI());
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//			this.root = root;
//		}
//
//		public void createRandomDirectoryStructure(int numberOfFiles) {
//			try {
//				byte[] encoded = Files.readAllBytes(PATH);
//				ClientPrimaryDataDirectory directory = this.root
//						.createPrimaryDataDirectory(UUID.randomUUID().toString());
//
//				for (int i = 0; i < numberOfFiles; i++) {
//
//					ClientPrimaryDataFile file = directory.createPrimaryDataFile("File_" + i);
//					InputStream inputStream = new ByteArrayInputStream(encoded);
//					file.store(inputStream);
//					inputStream.close();
//				}
//			} catch (PrimaryDataDirectoryException | PrimaryDataFileException | PrimaryDataEntityVersionException
//					| IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private static class CreateDirectoryRandomStructureThread extends Thread {
//
//		long runingTime = 0;
//		int numberOfFiles = 0;
//
//		public long getRuningTime() {
//			return runingTime;
//		}
//
//		public void setRuningTime(long runingTime) {
//			this.runingTime = runingTime;
//		}
//
//		CreateDirectoryRandomStructureThread(int numberOfFiles) {
//			this.numberOfFiles = numberOfFiles;
//		}
//
//		@Override
//		public void run() {
//
//			final long startTime = System.currentTimeMillis();
//			PseudoReader reader = new PseudoReader(rootDirectory);
//			reader.createRandomDirectoryStructure(numberOfFiles);
//			setRuningTime(System.currentTimeMillis() - startTime);
//			System.out.println("\nZeit (Speichern): " + (getRuningTime()) / 1000 + " sec");
//		}
//
//	}
//
//	@Test
//	public void TestStoreRead() throws Exception {
//
//		Authentication auth = new Authentication(EdalHelpers.authenticateUser("SampleUser", "password"));
//
//		final ClientDataManager dataManagerClient = new ClientDataManager(ClientServerTest.HOST,
//				ClientServerTest.REGISTRY_PORT, auth);
//
//		rootDirectory = dataManagerClient.getRootDirectory();
//
//		int numberOfThreads = 5;
//		int numberOfFiles = 50;
//
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1, TimeUnit.MINUTES,
//				new ArrayBlockingQueue<Runnable>(numberOfThreads));
//
//		Configurator.setLevel("eDAL-API", Level.OFF);
//
//		for (int i = 0; i < numberOfThreads; i++) {
//			CreateDirectoryRandomStructureThread thread = new CreateDirectoryRandomStructureThread(numberOfFiles);
//			System.out.println("Starting Thread " + i);
//			executor.execute(thread);
//			Thread.sleep(500);
//		}
//		executor.shutdown();
//
//		while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
//			System.out.println("Awaiting completion of threads.");
//		}
//
//		Configurator.setLevel("eDAL-API", Level.ALL);
////		Thread.sleep(Long.MAX_VALUE);
//	}
//}
