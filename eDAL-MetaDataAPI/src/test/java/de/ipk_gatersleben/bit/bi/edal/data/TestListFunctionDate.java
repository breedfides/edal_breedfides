/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
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