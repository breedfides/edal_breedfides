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

import java.security.Principal;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

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
			Assertions.assertEquals(directory.getCurrentVersion().getOwner()
					.getName(), p.getName());
			Assertions.assertEquals(((PrincipalImplementation) directory
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
			Assertions.assertEquals(directory.getCurrentVersion().getOwner()
					.getName(), p.getName());
			Assertions.assertEquals(((PrincipalImplementation) directory
					.getCurrentVersion().getOwner()).getType(), p.getClass()
					.getSimpleName());

			break;
		}

		DataManager.shutdown();
	}
}