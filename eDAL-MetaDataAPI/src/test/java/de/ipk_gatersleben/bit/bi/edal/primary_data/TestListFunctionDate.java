/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

public class TestListFunctionDate extends EdalTestCaseWithoutShutdown {

	/**
	 * Test store and read function with a folder in the file system.
	 */
	@Test
	public void testListDate() throws Exception {

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		PrimaryDataDirectory directory = rootDirectory
				.createPrimaryDataDirectory("directory");

		directory.createPrimaryDataFile("File 1");
		directory.createPrimaryDataFile("File 2");
		directory.createPrimaryDataFile("File 3");

		directory.rename("directory_new");

		directory.createPrimaryDataFile("File 4");
		directory.createPrimaryDataFile("File 5");
		directory.createPrimaryDataFile("File 6");

		DataManager.shutdown();

		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		PrimaryDataDirectory loadedDirectory = (PrimaryDataDirectory) rootDirectory
				.getPrimaryDataEntity("directory_new");

		loadedDirectory.switchCurrentVersion(loadedDirectory
				.getVersionByRevisionNumber(0));

		System.out.println(loadedDirectory.listPrimaryDataEntities().size());

		loadedDirectory.switchCurrentVersion(loadedDirectory
				.getVersionByRevisionNumber(1));

		System.out.println(loadedDirectory.listPrimaryDataEntities().size());
		
		DataManager.shutdown();
	}
}