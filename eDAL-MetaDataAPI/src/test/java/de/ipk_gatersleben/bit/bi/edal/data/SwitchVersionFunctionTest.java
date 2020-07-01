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

import java.util.SortedSet;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * Test implementation of
 * {@link PrimaryDataEntity#switchCurrentVersion(PrimaryDataEntityVersion)}.
 * 
 * @author arendd
 */
public class SwitchVersionFunctionTest extends EdalDefaultTestCase {

	/**
	 * Create a {@link PrimaryDataDirectory} with different
	 * {@link PrimaryDataEntityVersion}s. Switch between the
	 * {@link PrimaryDataEntityVersion}s an compare them.
	 */
	@Test
	public void testSwitchVersion() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager
				.getRootDirectory(EdalHelpers
						.getFileSystemImplementationProvider(true,
								this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		PrimaryDataDirectory subDir = rootDirectory
				.createPrimaryDataDirectory("subDir");

		// System.out.println("\ncurrent version: " + subDir.getCurrentVersion()
		// + "\n");

		MetaData m1 = subDir.getMetaData().clone();
		m1.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(
				"subDir_new_1"));
		subDir.setMetaData(m1);

		MetaData m2 = subDir.getMetaData().clone();
		m2.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(
				"subDir_new_2"));
		subDir.setMetaData(m2);

		SortedSet<PrimaryDataEntityVersion> versionList = subDir.getVersions();

		// for (PrimaryDataEntityVersion primaryDataEntityVersion : versionList)
		// {
		// System.out.println("Version: " + primaryDataEntityVersion);
		// }

		// System.out.println("\ncurrent version: " +
		// subDir.getCurrentVersion());

		/* compare current version revision with last version Revision */

		Assertions.assertEquals(subDir.getCurrentVersion(), versionList.last());

		// System.out.println("\nswitch current version");

		for (PrimaryDataEntityVersion primaryDataEntityVersion : versionList) {
			subDir.switchCurrentVersion(primaryDataEntityVersion);
			break;
		}

		// System.out.println("\ncurrent version: " +
		// subDir.getCurrentVersion());

		/* compare current version revision with first version revision */

		Assertions.assertEquals(subDir.getCurrentVersion(), versionList.first());

		// System.out.println("\nswitch version back");
		subDir.switchCurrentVersion(subDir.getVersions().last());

		// System.out.println("\ncurrent version: " + subDir.getCurrentVersion()
		// + "\n");

		/* compare currentVersion revision with last version Revision */

		Assertions.assertEquals(subDir.getCurrentVersion(), versionList.last());

	}
}