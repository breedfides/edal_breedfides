/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class TestClient {

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072", new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));

		configuration.setUseSSL(false);

		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, true, false);

		Authentication testAuthentication = new Authentication(EdalHelpers.authenticateWinOrUnixOrMacUser());

		ClientDataManager clientDataManager = new ClientDataManager("localhost", EdalServer.DEFAULT_REGISTRY_PORT, testAuthentication);

		ClientPrimaryDataDirectory rootDirectory = clientDataManager.getRootDirectory();

		ClientPrimaryDataFile file = rootDirectory.createPrimaryDataFile("File.txt");

		System.out.println(file.getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM));

	
		// MetaData metadata = file.getMetaData().clone();
		//
		// metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new
		// UntypedData("Description"));
		//
		// file.setMetaData(metadata);
		//
		// System.out.println(file.getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM));

	}
}
