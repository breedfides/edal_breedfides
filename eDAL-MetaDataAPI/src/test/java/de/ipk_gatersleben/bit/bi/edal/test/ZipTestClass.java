/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.internet.InternetAddress;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.sample.*;

@SuppressWarnings("unused")
public class ZipTestClass {

	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "user@nodomain.com.invalid";

	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";

	private static final String FILE_NAME = "eDAL-Project-2.1-webstart.jar";

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration(
				DATACITE_USERNAME, DATACITE_PASSWORD, DATACITE_PREFIX,
				new InternetAddress(EMAIL), new InternetAddress(EMAIL),
				new InternetAddress(EMAIL), new InternetAddress(ROOT_USER));

		configuration.setUseSSL(true);

		PrimaryDataDirectory rootDirectory = DataManager
				.getRootDirectory(EdalHelpers
						.getFileSystemImplementationProvider(true,
								configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());

		Path path = Paths.get(System.getProperty("user.home"), FILE_NAME);

		ZipInputStream stream = new ZipInputStream(new FileInputStream(
				path.toFile()));

		try {

			ZipEntry entry;

			while ((entry = stream.getNextEntry()) != null) {

				if (entry.getName().startsWith("SampleData")) {

					String[] array = entry.getName().split("/");

					if (entry.isDirectory()) {

						PrimaryDataDirectory currentDirectory = rootDirectory;

						for (String string : array) {
							try {
								currentDirectory = (PrimaryDataDirectory) currentDirectory
										.getPrimaryDataEntity(string);
							} catch (PrimaryDataDirectoryException e) {
								currentDirectory = currentDirectory
										.createPrimaryDataDirectory(string);
							}
						}
					} else {
						String directories[] = Arrays.copyOfRange(array, 0,
								array.length - 1);

						String files[] = Arrays.copyOfRange(array,
								array.length - 1, array.length);

						PrimaryDataDirectory currentDirectory = rootDirectory;

						for (String string : directories) {
							try {
								currentDirectory = (PrimaryDataDirectory) currentDirectory
										.getPrimaryDataEntity(string);
							} catch (PrimaryDataDirectoryException e) {
								currentDirectory = currentDirectory
										.createPrimaryDataDirectory(string);
							}
						}

						PrimaryDataFile file = currentDirectory
								.createPrimaryDataFile(files[0]);

						file.store(stream);

					}
				}
				stream.closeEntry();
			}
		}

		catch (PrimaryDataFileException | PrimaryDataDirectoryException
				| IOException | PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		} finally {
			stream.close();
		}

		DataManager.shutdown();
	}
}