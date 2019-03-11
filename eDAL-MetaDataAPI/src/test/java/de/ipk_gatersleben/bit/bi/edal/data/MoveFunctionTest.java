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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * Test implementation of {@link PrimaryDataEntity#move(PrimaryDataDirectory)}
 * function.
 * <p>
 * Move two object from one {@link PrimaryDataDirectory} to another
 * {@link PrimaryDataDirectory} and back an compare them by name.
 * 
 * @author arendd
 */
public class MoveFunctionTest extends EdalDefaultTestCase {

	public int indent = 0;
	private StringBuffer output = new StringBuffer("");

	/**
	 * List recursively all {@link PrimaryDataEntity} object in the current
	 * {@link PrimaryDataDirectory} an save names in {@link String} 'ausgabe'.
	 * 
	 * @param currentDirectory
	 *            the {@link PrimaryDataDirectory} to list all containing
	 *            {@link PrimaryDataEntity}.
	 * @throws PrimaryDataDirectoryException
	 */
	public void listDir(final PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException {

		indent += 1;
		final List<PrimaryDataEntity> list = currentDirectory
				.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {

				for (int j = 1; j < indent; j++) {
					output.append("  ");
				}
				output.append(primaryDataEntity + "\n");
				if (primaryDataEntity.isDirectory()) {
					listDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
			indent -= 1;
		}
	}

	/**
	 * The test create two objects and move them from one
	 * {@link PrimaryDataDirectory} to another {@link PrimaryDataDirectory} and
	 * back an compare them by name.
	 */
	@Test
	public void testMove() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager
				.getRootDirectory(EdalHelpers
						.getFileSystemImplementationProvider(true,
								this.configuration), EdalHelpers
						.authenticateSampleUser());

		/* create 2 directories */
		PrimaryDataDirectory directory1 = rootDirectory
				.createPrimaryDataDirectory("directory1");
		PrimaryDataDirectory directory2 = rootDirectory
				.createPrimaryDataDirectory("directory2");

		/* create 2 files in directory1 */
		PrimaryDataFile file = directory1.createPrimaryDataFile("file");
		PrimaryDataDirectory dir = directory1
				.createPrimaryDataDirectory("directory");

		/* list all files in directory1 */
		listDir(directory1);
		String listDirectory1 = output.toString();
		/* move all files in directory1 to directory2 */
		file.move(directory2);
		dir.move(directory2);

		/* list all files in directory2 */
		output = new StringBuffer("");
		listDir(directory2);
		String listDirectory2 = output.toString();

		/* compare both directories */
		Assert.assertEquals(listDirectory1, listDirectory2);

		/* move all files back to directory1 */
		file.move(directory1);
		dir.move(directory1);

		/* list all files in directory1 */
		output = new StringBuffer("");
		listDir(directory1);
		String newListDirectory1 = output.toString();

		/* compare both directories */
		Assert.assertEquals(listDirectory1, newListDirectory1);
	}
}
