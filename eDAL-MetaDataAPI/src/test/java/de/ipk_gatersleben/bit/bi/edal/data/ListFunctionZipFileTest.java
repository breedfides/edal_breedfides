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

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Assertions;

import org.apache.logging.log4j.core.util.Loader;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.helper.EdalZipFileVisitor;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * New zip file test for store read function with Java 7.
 * 
 * @author arendd
 * 
 */
public class ListFunctionZipFileTest extends EdalDefaultTestCase {

	public ListFunctionZipFileTest() {
		super();
	}

	private SortedSet<String> outputSet = new TreeSet<>();

	public void listDir(final PrimaryDataDirectory currentDirectory) throws PrimaryDataDirectoryException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				outputSet.add(primaryDataEntity.getPath());

				if (primaryDataEntity.isDirectory()) {
					listDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
		}
	}

	/**
	 * Test the store and read function with a zip file.
	 */
	@Test
	public void testZipFile() throws Exception {

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final Path path = Paths.get(Loader.getResource("TEST.zip", null).toURI());

		if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("Given path do not exist");
			System.exit(0);
		}

		try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {

			EdalZipFileVisitor edalVisitor = new EdalZipFileVisitor(rootDirectory);
			final long zeitVorher1 = System.currentTimeMillis();
			Files.walkFileTree(fs.getPath("/"), edalVisitor);
			final long zeitNachher1 = System.currentTimeMillis();

			final long zeitVorher2 = System.currentTimeMillis();
			listDir(rootDirectory);
			final long zeitNachher2 = System.currentTimeMillis();

			SortedSet<String> inputSet = edalVisitor.getPathSet();

			/* print input String */
			// for (String string : inputSet) {
			// System.out.println(string);
			// }

			/* print output String */
			// for (String string : outputSet) {
			// System.out.println(string);
			// }

			System.out.println("\n" + "time (store): " + (zeitNachher1 - zeitVorher1) / 1000 + " sec");
			System.out.println("time (read): " + (zeitNachher2 - zeitVorher2) + " msec");

			System.out.println(inputSet.equals(outputSet));

			Assertions.assertEquals(inputSet, outputSet);
		}

		EdalHelpers.getStatistic(((FileSystemImplementationProvider) DataManager.getImplProv()).getStatistics());
	}
}