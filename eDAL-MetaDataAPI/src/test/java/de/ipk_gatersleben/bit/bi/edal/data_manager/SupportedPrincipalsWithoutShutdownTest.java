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
import de.ipk_gatersleben.bit.bi.edal.test.EdalTestCaseWithoutShutdown;

/**
 * Test the functions to check the supported principals when the user start the
 * eDAL system.
 * 
 * @author arendd
 */
public class SupportedPrincipalsWithoutShutdownTest extends EdalTestCaseWithoutShutdown {

	/**
	 * Test what happens when the user try to connect subject, that has a
	 * {@link Principal}, which is not in the supported principals -> should get a
	 * {@link PrimaryDataDirectoryException}.
	 */
	@Test
	public final void testUnsupportedPrincipals() throws Exception {

		List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>(
				Arrays.asList(SamplePrincipal.class));

		EdalConfiguration configuration1 = this.configuration;

		configuration1.setSupportedPrincipals(principals);

		try {
			DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(false, configuration1),
					EdalHelpers.authenticateWinOrUnixOrMacUser());

			Assertions.fail("You should not be able to connect because of missing principal");

		} catch (PrimaryDataDirectoryException e) {
			Assertions.assertTrue(true, e.getMessage());
		}

	}

	/**
	 * Test what happens when the user try to connect with a empty list of supported
	 * principals -> should get a {@link PrimaryDataDirectoryException}.
	 */
	@Test
	public final void testEmptySupportedPrincipals() throws Exception {

		List<Class<? extends Principal>> principals = new ArrayList<Class<? extends Principal>>();

		EdalConfiguration configuration1 = this.configuration;
		configuration1.setSupportedPrincipals(principals);

		try {
			DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, configuration1),
					EdalHelpers.authenticateWinOrUnixOrMacUser());

			Assertions.fail("You should not be able to connect with an empty list");
		} catch (PrimaryDataDirectoryException e) {
			Assertions.assertTrue(true, e.getMessage());
		}

	}
}