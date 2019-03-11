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

import java.security.Principal;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrincipalImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

public class OwnerTest extends EdalTestCaseWithoutShutdown {

	@Test
	public void testOwner() throws Exception {

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		PrimaryDataDirectory directory = rootDirectory
				.createPrimaryDataDirectory("directory");

		directory.grantPermission(new SamplePrincipal("SampleUser"),
				Methods.rename);

		System.out.println(directory.getCurrentVersion().getOwner());

		for (Principal p : DataManager.getSubject().getPrincipals()) {
			Assert.assertEquals(directory.getCurrentVersion().getOwner()
					.getName(), p.getName());
			Assert.assertEquals(((PrincipalImplementation) directory
					.getCurrentVersion().getOwner()).getType(), p.getClass()
					.getSimpleName());

			break;
		}

		DataManager.shutdown();

		rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false,
						this.configuration), EdalHelpers
						.authenticateSampleUser());

		directory = (PrimaryDataDirectory) rootDirectory
				.getPrimaryDataEntity("directory");

		directory.rename("new_directory");

		System.out.println(directory.getCurrentVersion().getOwner());

		for (Principal p : DataManager.getSubject().getPrincipals()) {
			Assert.assertEquals(directory.getCurrentVersion().getOwner()
					.getName(), p.getName());
			Assert.assertEquals(((PrincipalImplementation) directory
					.getCurrentVersion().getOwner()).getType(), p.getClass()
					.getSimpleName());

			break;
		}

		DataManager.shutdown();
	}
}