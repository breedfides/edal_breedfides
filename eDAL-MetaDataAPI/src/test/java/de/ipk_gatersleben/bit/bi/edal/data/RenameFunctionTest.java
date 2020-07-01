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
package de.ipk_gatersleben.bit.bi.edal.data;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

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
			Assertions.fail("able to rename file with existing name");
		} catch (Exception e) {
			Assertions.assertTrue(true);
		}
		try {
			pdd.rename("file");
			Assertions.fail("able to rename directory with existing name");
		} catch (Exception e) {
			Assertions.assertTrue(true);
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
			Assertions.assertTrue(true);
		} catch (Exception e) {
			Assertions.fail("unable to rename file with an old name again");
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
			Assertions.assertTrue(true);
		} catch (Exception e) {
			Assertions.fail("unable to create file with an old name of another file");
		}
	}
}
