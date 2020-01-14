/**
org.junit.jupiter.api.Test * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.data_manager;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * Test the functions to check the supported principals when the user start the
 * eDAL system.
 * 
 * @author arendd
 */
public class SupportedPrincipalsTest extends EdalDefaultTestCase {

	/**
	 * Test what happens when the user try to connect subject, that has a
	 * {@link Principal}, which is not in the supported principals -> should get
	 * a {@link PrimaryDataDirectoryException}.
	 */
	@Test
	public final void testUnsupportedPrincipals() throws Exception {

		List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>(
				Arrays.asList(SamplePrincipal.class));

		EdalConfiguration configuration1 = this.configuration;

		configuration1.setSupportedPrincipals(principals);

		try {
			DataManager.getRootDirectory(
					EdalHelpers.getFileSystemImplementationProvider(false,
							configuration1), EdalHelpers
							.authenticateWinOrUnixOrMacUser());

			Assertions.fail("You should not be able to connect because of missing principal");

		} catch (PrimaryDataDirectoryException e) {
			Assertions.assertTrue(true,e.getMessage());
		}

//		DataManager.shutdown();
	}

	/**
	 * Test what happens when the user try to connect with a sub list of the
	 * supported principals -> no problem.
	 */
	@Test
	public final void testSupportedPrincipals() throws Exception {

		EdalConfiguration configuration1 = this.configuration;

		configuration1
				.setSupportedPrincipals(EdalConfiguration.DEFAULT_SUPPORTED_PRINCIPALS);

		DataManager.getRootDirectory(EdalHelpers
				.getFileSystemImplementationProvider(true, configuration1),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		DataManager.shutdown();

		try {

			List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>(
					Arrays.asList(SamplePrincipal.class));

			configuration1.setSupportedPrincipals(principals);

			DataManager.getRootDirectory(
					EdalHelpers.getFileSystemImplementationProvider(false,
							configuration1), EdalHelpers
							.authenticateSampleUser());

		} catch (PrimaryDataDirectoryException e) {
			Assertions.fail("Can not connect to eDAL :" + e.getMessage());
		}
//		DataManager.shutdown();
	}

	/**
	 * Test what happens when the user try to connect with a empty list of
	 * supported principals -> should get a
	 * {@link PrimaryDataDirectoryException}.
	 */
	@Test
	public final void testEmptySupportedPrincipals() throws Exception {

		List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>();

		EdalConfiguration configuration1 = this.configuration;
		configuration1.setSupportedPrincipals(principals);

		try {
			DataManager.getRootDirectory(EdalHelpers
					.getFileSystemImplementationProvider(true, configuration1),
					EdalHelpers.authenticateWinOrUnixOrMacUser());

			Assertions.fail("You should not be able to connect with an empty list");
		} catch (PrimaryDataDirectoryException e) {
			Assertions.assertTrue(true, e.getMessage());
		}
		/* no shutdown necessary */
		// DataManager.shutdown();
	}

	/**
	 * Test what happens when the user try to connect with a list of supported
	 * principals that is not stored in the database -> should get a
	 * {@link PrimaryDataDirectoryException}.
	 */
	@Test
	public final void testExistingSupportedPrincipals() throws Exception {

		List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>();

		principals.add(SamplePrincipal.class);

		EdalConfiguration configuration1 = this.configuration;

		configuration1.setSupportedPrincipals(principals);

		try {
			DataManager.getRootDirectory(EdalHelpers
					.getFileSystemImplementationProvider(true, configuration1),
					EdalHelpers.authenticateSampleUser());

		} catch (PrimaryDataDirectoryException e) {
			Assertions.fail(e.getMessage());
		}

		DataManager.shutdown();

		EdalConfiguration configuration2 = this.configuration;

		configuration2
				.setSupportedPrincipals(EdalConfiguration.DEFAULT_SUPPORTED_PRINCIPALS);

		try {
			DataManager.getRootDirectory(
					EdalHelpers.getFileSystemImplementationProvider(false,
							configuration2), EdalHelpers
							.authenticateSampleUser());
			Assertions.fail("You should not be able to connect because of missing principals in the new list");

		} catch (PrimaryDataDirectoryException e) {
			Assertions.assertTrue(true,e.getMessage());
		}

//		DataManager.shutdown();
	}

}
