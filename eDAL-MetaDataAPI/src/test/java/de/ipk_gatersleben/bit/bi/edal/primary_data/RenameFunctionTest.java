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

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * JUnit-Test to test the {@link PrimaryDataEntity#rename(String)} function.
 * 
 * @author arendd
 */
public class RenameFunctionTest extends EdalDefaultTestCase {

	@Test
	public void testRenameObjectWithExistingName() throws Exception {

		/* create clean database */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateSampleUser());
		/* create file and directory */
		PrimaryDataFile pdf = rootDirectory.createPrimaryDataFile("file");
		PrimaryDataDirectory pdd = rootDirectory.createPrimaryDataDirectory("directory");

		try {
			pdf.rename("directory");
			Assert.fail("able to rename file with existing name");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		try {
			pdd.rename("file");
			Assert.fail("able to rename directory with existing name");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testRenameFileWithOldName() throws Exception {

		/* create clean database */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateSampleUser());
		/* create and rename file */
		PrimaryDataFile pdf = rootDirectory.createPrimaryDataFile("file");
		pdf.rename("file2");

		try {
			pdf.rename("file");
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail("unable to rename file with an old name again");
		}
	}

	@Test
	public void testCreateNewFileWithOldFileName() throws Exception {

		/* create clean database */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateSampleUser());
		/* create and rename file */
		PrimaryDataFile pdf = rootDirectory.createPrimaryDataFile("file");
		pdf.rename("file2");
		pdf.rename("file3");

		try {
			rootDirectory.createPrimaryDataFile("file");
			rootDirectory.createPrimaryDataFile("file2");
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail("unable to create file with an old name of another file");
		}
	}
}
