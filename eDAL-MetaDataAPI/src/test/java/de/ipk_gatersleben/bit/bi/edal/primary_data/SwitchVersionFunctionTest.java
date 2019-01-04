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

import java.util.SortedSet;

import junit.framework.Assert;

import org.junit.Test;

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

		Assert.assertEquals(subDir.getCurrentVersion(), versionList.last());

		// System.out.println("\nswitch current version");

		for (PrimaryDataEntityVersion primaryDataEntityVersion : versionList) {
			subDir.switchCurrentVersion(primaryDataEntityVersion);
			break;
		}

		// System.out.println("\ncurrent version: " +
		// subDir.getCurrentVersion());

		/* compare current version revision with first version revision */

		Assert.assertEquals(subDir.getCurrentVersion(), versionList.first());

		// System.out.println("\nswitch version back");
		subDir.switchCurrentVersion(subDir.getVersions().last());

		// System.out.println("\ncurrent version: " + subDir.getCurrentVersion()
		// + "\n");

		/* compare currentVersion revision with last version Revision */

		Assert.assertEquals(subDir.getCurrentVersion(), versionList.last());

	}
}