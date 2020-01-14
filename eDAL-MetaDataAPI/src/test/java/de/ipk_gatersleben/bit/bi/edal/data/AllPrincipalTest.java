/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.data;

import java.security.AccessControlException;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ALLPrincipal;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

/**
 * Test the {@link ALLPrincipal} class and test the
 * {@link PrimaryDataEntity#grantPermission(Principal, Enum)} and
 * {@link PrimaryDataEntity#revokePermission(Principal, Enum)} functions.
 * 
 * @author arendd
 */
public class AllPrincipalTest extends EdalTestCaseWithoutShutdown {

	@Test
	public void testGrantRevoke() throws Exception {

		/* Session 1 */
		/* - create clean database */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		DataManager.shutdown();

		/* Session 2 */
		/* - try to grant a method as another user -> fail */
		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateSampleUser());

		try {
			rootDirectory.grantPermission(new ALLPrincipal(), Methods.ALL);
			Assertions.fail();
		} catch (AccessControlException e) {
			Assertions.assertTrue(true);
		}

		DataManager.shutdown();

		/* Session 3 */
		/* - grant method for AllPrincipal */
		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		rootDirectory.grantPermission(new ALLPrincipal(), Methods.ALL);

		DataManager.shutdown();

		/* Session 4 */
		/* - try to create file as another user -> success */
		/* - try to revoke method as another user -> fail */
		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateSampleUser());

		try {
			rootDirectory.createPrimaryDataFile("file");
			Assertions.assertTrue(true);
		} catch (AccessControlException e) {
			Assertions.fail();
		}
		try {
			rootDirectory.revokePermission(new ALLPrincipal(), Methods.ALL);
			Assertions.fail();
		} catch (AccessControlException e) {
			Assertions.assertTrue(true);
		}

		DataManager.shutdown();
	}
}