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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.helper.EdalDirectoryVisitorWithMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * Test implementation of {@link PrimaryDataFile#store(InputStream)} and
 * {@link PrimaryDataFile#read(OutputStream)} function.
 * <p>
 * Save all objects of a folder into the eDAL system and reload all objects.
 * 
 * @author arendd
 */
public class StoreReadFunctionTest extends EdalDefaultTestCase {

	private int indent = 0;
	private StringBuffer outputString = new StringBuffer("");
	private SortedSet<String> outputSet = new TreeSet<>();
	private OutputStream outputStream;

	private Path INPUT_PATH = Paths.get(System.getProperty("java.home"), "bin");

	private Path OUTPUT_PATH = Paths.get(System.getProperty("user.home"), "TEST_READ");

	// private Path INPUT_PATH = Paths.get("D:\\VMShared\\Set_10000_0_5MB");
	//
	// private Path OUTPUT_PATH = Paths.get("D:\\output");

	/**
	 * List recursively all {@link PrimaryDataEntity} object in the current
	 * {@link PrimaryDataDirectory} an save names in the {@link StringBuffer} 
	 * 'outputSet'. Save complete structure of the {@link PrimaryDataDirectory}
	 * into the FileSystem.
	 * 
	 * @param currentDirectory
	 *            the {@link PrimaryDataDirectory} to list and read all
	 *            containing {@link PrimaryDataEntity}.
	 * @throws PrimaryDataDirectoryException
	 * @throws PrimaryDataFileException
	 * @throws FileNotFoundException
	 */
	private void listDir(final PrimaryDataDirectory currentDirectory) throws PrimaryDataDirectoryException, PrimaryDataFileException, FileNotFoundException {

		indent += 1;
		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				outputSet.add(primaryDataEntity.getPath());

				for (int j = 1; j < indent; j++) {
					outputString.append("  ");
				}
				outputString.append(primaryDataEntity + "\n");
				if (!primaryDataEntity.isDirectory()) {
					PrimaryDataFile file = (PrimaryDataFile) primaryDataEntity;

					File tmp = Paths.get(OUTPUT_PATH.toString(), primaryDataEntity.getName()).toFile();

					if (!tmp.exists()) {
						try {
							tmp.getParentFile().mkdirs();
							tmp.createNewFile();
						} catch (IOException e) {
							System.out.println("couldn't create File");
						}
					}

					outputStream = new FileOutputStream(tmp);
					file.read(outputStream);

				}
				if (primaryDataEntity.isDirectory()) {
					OUTPUT_PATH = Paths.get(OUTPUT_PATH.toString(), primaryDataEntity.getName());
					listDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
			indent -= 1;
			OUTPUT_PATH = OUTPUT_PATH.getParent();
		}
	}

	@SuppressWarnings("unused")
	private void createZipFileOfPrimaryDataDirectory(ZipOutputStream zout, PrimaryDataDirectory entity) throws IOException, PrimaryDataFileException, PrimaryDataDirectoryException {

		List<PrimaryDataEntity> files = entity.listPrimaryDataEntities();

		System.out.println("Adding directory " + entity.getPath());

		for (PrimaryDataEntity primaryDataEntity : files) {

			// if the file is directory, call the function recursively
			if (primaryDataEntity.isDirectory()) {
				createZipFileOfPrimaryDataDirectory(zout, (PrimaryDataDirectory) primaryDataEntity);
				continue;
			}

			System.out.println("Adding file " + primaryDataEntity.getPath());

			zout.putNextEntry(new ZipEntry(primaryDataEntity.getPath().substring(2)));

			((PrimaryDataFile) primaryDataEntity).read(zout);

			zout.closeEntry();
		}
	}

	@Test
	public void testStoreRead() throws Exception {

		System.out.println(INPUT_PATH);

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		if (Files.notExists(INPUT_PATH, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("Given path do not exist");
			System.exit(0);
		}
		EdalDirectoryVisitorWithMetaData edalVisitor = new EdalDirectoryVisitorWithMetaData(rootDirectory, INPUT_PATH, null, true);

		final long zeitVorher1 = System.currentTimeMillis();
		Files.walkFileTree(INPUT_PATH, edalVisitor);
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

		System.out.println("\nZeit (Speichern): " + (zeitNachher1 - zeitVorher1) / 1000 + " sec");
		System.out.println("Zeit (Auflisten): " + (zeitNachher2 - zeitVorher2) / 1000 + " sec");

		System.out.println(inputSet.equals(outputSet));

		Assert.assertEquals(inputSet, outputSet);

		EdalHelpers.getStatistic(((FileSystemImplementationProvider) DataManager.getImplProv()).getStatistics());

		// FileOutputStream zipOutput = new FileOutputStream(Paths.get(
		// System.getProperty("user.home"), "rootdirectory.zip").toFile());
		//
		// // create object of ZipOutputStream from FileOutputStream
		// ZipOutputStream zout = new ZipOutputStream(zipOutput);
		//
		// createZipFileOfPrimaryDataDirectory(zout, rootDirectory);
		//
		// // close the ZipOutputStream
		// zout.close();
		//
		// System.out.println("Zip file has been created!");
	}
}