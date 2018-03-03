/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

//** for later performance Analysis for thesis **/
public class PerformanceAnalysisMultiThreaded extends ClientServerTest {

	private static Path OUTPUT_PATH = Paths.get(System.getProperty("user.home"), "TEST_READ");
	private static ClientPrimaryDataDirectory rootDirectory;
	// private OutputStream outputStream;

	class PseudoReader {

		private final Path PATH = Paths.get(System.getProperty("user.home"), "desktop", "test_image.jpg");

		private ClientPrimaryDataDirectory root = null;

		int numberOfFiles = 0;
		int numberOfDirectories = 0;
		int numberOfThread = 0;

		public PseudoReader(ClientPrimaryDataDirectory root) {
			this.root = root;
		}

		public PseudoReader(int numberOfThread, int numberOfFiles, int numberOfDirectories) {
			this.numberOfThread = numberOfThread;
			this.numberOfDirectories = numberOfDirectories;
			this.numberOfFiles = numberOfFiles;
		}

		public void createRandomDirectoryStructure() {
			try {
				byte[] encoded = Files.readAllBytes(PATH);

				ClientPrimaryDataDirectory threadDir = PerformanceAnalysisMultiThreaded.rootDirectory
						.createPrimaryDataDirectory(String.valueOf(this.numberOfThread));

				for (int j = 0; j < numberOfDirectories; j++) {

					ClientPrimaryDataDirectory directory = threadDir.createPrimaryDataDirectory(String.valueOf(j));

					for (int i = 0; i < numberOfFiles; i++) {

						ClientPrimaryDataFile file = directory.createPrimaryDataFile("File_" + i);
						InputStream inputStream = new ByteArrayInputStream(encoded);
						file.store(inputStream);
						inputStream.close();
					}
				}

			} catch (PrimaryDataDirectoryException | PrimaryDataFileException | PrimaryDataEntityVersionException
					| IOException e) {
				e.printStackTrace();
			}
		}
	}

	class CreateDirectoryRandomStructureThread extends Thread {

		long runingTime = 0;
		int numberOfFiles = 0;
		int numberOfDirectories = 0;
		int numberOfThread = 0;

		public long getRuningTime() {
			return runingTime;
		}

		public void setRuningTime(long runingTime) {
			this.runingTime = runingTime;
		}

		CreateDirectoryRandomStructureThread(int numberOfFiles, int numberOfDirectories, int threadNumber) {
			this.numberOfFiles = numberOfFiles;
			this.numberOfDirectories = numberOfDirectories;
			this.numberOfThread = threadNumber;
		}

		@Override
		public void run() {

			final long startTime = System.currentTimeMillis();

			PseudoReader reader = new PseudoReader(this.numberOfThread, this.numberOfFiles, this.numberOfDirectories);

			reader.createRandomDirectoryStructure();

			setRuningTime(System.currentTimeMillis() - startTime);

			System.out.println("\nZeit (Speichern): " + (getRuningTime()) / 1000 + " sec");
		}

	}

	class ReadDirectoryRandomStructureThread extends Thread {

		long runingTime = 0;
		int numberOfThread = 0;

		public long getRuningTime() {
			return runingTime;
		}

		public void setRuningTime(long runingTime) {
			this.runingTime = runingTime;
		}

		ReadDirectoryRandomStructureThread(int threadNumber) {
			this.numberOfThread = threadNumber;
		}

		@Override
		public void run() {

			final long startTime = System.currentTimeMillis();

			try {
				ClientPrimaryDataDirectory threadDir = (ClientPrimaryDataDirectory) PerformanceAnalysisMultiThreaded.rootDirectory
						.getPrimaryDataEntity(String.valueOf(this.numberOfThread));

				Path outputPath = Paths.get(PerformanceAnalysisMultiThreaded.OUTPUT_PATH.toString(),
						String.valueOf(this.numberOfThread));

				// try {
				// Files.createDirectories(outputPath);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				listDir(threadDir, outputPath);
				setRuningTime(System.currentTimeMillis() - startTime);
				System.out.println("\nZeit (Lesen): " + (getRuningTime()) / 1000 + " sec");

			} catch (RemoteException | PrimaryDataDirectoryException | AccessControlException | FileNotFoundException
					| PrimaryDataFileException e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	public void TestStoreRead() throws Exception {

		Authentication auth = new Authentication(EdalHelpers.authenticateUser("SampleUser", "password"));

		final ClientDataManager dataManagerClient = new ClientDataManager(ClientServerTest.HOST,
				ClientServerTest.REGISTRY_PORT, auth);

		PerformanceAnalysisMultiThreaded.rootDirectory = dataManagerClient.getRootDirectory();

		int numberOfThreads = 3;
		int numberOfFilesPerDirectory = 100;
		int numberOfDirectory = 10;

		ThreadPoolExecutor executorWrite = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1, TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(numberOfThreads));

		Logger.getLogger("eDAL-API").setLevel(Level.OFF);
		for (int threadNumber = 0; threadNumber < numberOfThreads; threadNumber++) {
			CreateDirectoryRandomStructureThread thread = new CreateDirectoryRandomStructureThread(
					numberOfFilesPerDirectory, numberOfDirectory, threadNumber);
			System.out.println("Starting Write Thread " + threadNumber);
			executorWrite.execute(thread);
			Thread.sleep(500);
		}
		executorWrite.shutdown();

		while (!executorWrite.awaitTermination(10, TimeUnit.SECONDS)) {
			System.out.println("Awaiting completion of write threads.");
		}

		Logger.getLogger("eDAL-API").setLevel(Level.ALL);

		ThreadPoolExecutor executorRead = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1, TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(numberOfThreads));

		Logger.getLogger("eDAL-API").setLevel(Level.OFF);
		for (int threadNumber = 0; threadNumber < numberOfThreads; threadNumber++) {

			ReadDirectoryRandomStructureThread thread = new ReadDirectoryRandomStructureThread(threadNumber);

			System.out.println("Starting Read Thread " + threadNumber);

			executorRead.execute(thread);

			Thread.sleep(500);
		}
		executorRead.shutdown();

		while (!executorRead.awaitTermination(10, TimeUnit.SECONDS)) {
			System.out.println("Awaiting completion of read threads.");
		}

		Logger.getLogger("eDAL-API").setLevel(Level.ALL);

	}

	private void listDir(final ClientPrimaryDataDirectory currentDirectory, Path outputPath)
			throws PrimaryDataDirectoryException, PrimaryDataFileException, FileNotFoundException,
			AccessControlException, RemoteException {

		final List<ClientPrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final ClientPrimaryDataEntity primaryDataEntity : list) {

				if (!primaryDataEntity.isDirectory()) {
					final ClientPrimaryDataFile file = (ClientPrimaryDataFile) primaryDataEntity;

					final File tmp = Paths.get(outputPath.toString(), primaryDataEntity.getName()).toFile();

					// if (!tmp.exists()) {
					try {
						tmp.getParentFile().mkdirs();
						tmp.createNewFile();
					} catch (final IOException e) {
						System.out.println("couldn't create File");
					}
					// }

					OutputStream os = new FileOutputStream(tmp);
					file.read(os);

				} else {
					outputPath = Paths.get(outputPath.toString(), primaryDataEntity.getName());
					this.listDir((ClientPrimaryDataDirectory) primaryDataEntity, outputPath);
					outputPath = outputPath.getParent();
				}
			}
		}
	}

}
