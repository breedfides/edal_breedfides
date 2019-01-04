/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.sample.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class SampleClient {

	public static void main(final String[] args)
			throws PrimaryDataDirectoryException,
			PrimaryDataEntityVersionException, PrimaryDataFileException,
			EdalAuthenticateException, NotBoundException, IOException,
			EdalException {

		String serveraddress = null;
		int registryPort = 0;
		String localdirectory = null;
		String username = null;
		String password = null;

		if (args.length != 5) {
			System.out
					.println("Usage:    de.ipk_gatersleben.bit.bi.edal.rmi.client.sample.SampleClient option(s)");
			System.out
					.println("Options:    the first parameter is servername, the second parametr is registryport, the third parameter is local directory that you want to upload, the fourth parameter is your username, the fifth parameter is your password ");
			System.exit(-1);
		} else {
			serveraddress = args[0].trim();
			String sport = args[1].trim();
			try {
				registryPort = Integer.parseInt(sport);
			} catch (Exception e) {
				System.out
						.println("Usage:    de.ipk_gatersleben.bit.bi.edal.rmi.client.sample.SampleClient option(s)");
				System.out
						.println("Options:    the first parameter is servername, the second parametr is serverport, the third parameter is local directory that you want to upload");
				System.exit(-1);
			}
			localdirectory = args[2].trim();
			username = args[3].trim();
			password = args[4].trim();
		}

		Authentication loginobj = new Authentication(
				EdalHelpers.authenticateUser(username, password));

		/* connect with eDAL-Server */
		ClientDataManager client = new ClientDataManager(serveraddress,
				registryPort, loginobj);

		ClientPrimaryDataDirectory rootDirectory = client.getRootDirectory();

		uploaddir(rootDirectory, localdirectory);
		/*
		 * ClientPrimaryDataDirectory dir = rootDirectory
		 * .createPrimaryDataDirectory("testDir");
		 * 
		 * ClientPrimaryDataFile file = rootDirectory
		 * .createPrimaryDataFile("testFile");
		 */

		/*
		 * save File over RMI with rmiio remote inputstream
		 * 
		 * File inputFile = new File(System.getProperty("user.home") +
		 * File.separatorChar + "test.txt"); InputStream in = new
		 * FileInputStream(inputFile); file.store(in);
		 */

		/*
		 * read File over RMI with rmiio remote outputstream File outputFile =
		 * new File(System.getProperty("user.home") + File.separatorChar +
		 * "test2.txt"); final OutputStream out = new
		 * FileOutputStream(outputFile); file.read(out);
		 */
	}

	private static void uploaddir(ClientPrimaryDataDirectory remotedir,
			String parentpath) throws PrimaryDataDirectoryException,
			PrimaryDataFileException, PrimaryDataEntityVersionException,
			IOException {
		File curpath = new File(parentpath);
		File[] files = curpath.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				ClientPrimaryDataDirectory dir = remotedir
						.createPrimaryDataDirectory(file.getName());
				uploaddir(dir, file.getAbsolutePath());
			} else {
				ClientPrimaryDataFile remotefile = remotedir
						.createPrimaryDataFile(file.getName());
				InputStream in = new FileInputStream(file);
				remotefile.store(in);
				in.close();
			}
		}
	}
}
