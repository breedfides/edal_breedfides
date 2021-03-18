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
package de.ipk_gatersleben.bit.bi.edal.helper;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.*;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalExample {

	/* set the files to save into the eDAL system */
	private static File INPUTFILES = new File(System.getProperty("java.home")
			+ File.separatorChar + "bin");

	/* set the directory to save the objects from the eDAL system */
	private static File OUTPUTFILES = new File(System.getProperty("user.home")
			+ File.separatorChar + "output");

	public static void main(final String[] args)
			throws PrimaryDataDirectoryException,
			PrimaryDataEntityVersionException, CloneNotSupportedException,
			MetaDataException, EdalAuthenticateException,
			FileNotFoundException, PrimaryDataFileException,
			EdalConfigurationException, AddressException, EdalException {

		/* identify the user to get a authenticated Subject */
		Subject mysubject = EdalHelpers.authenticateSampleUser();

		/* save some files */
		/* start eDAL and delete database before and get the root directory */
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						new EdalConfiguration("", "", "10.5072",
								new InternetAddress("test@online.de"),
								new InternetAddress("test@online.de"),
								new InternetAddress("test@online.de"),
								new InternetAddress(
										"eDAL0815@ipk-gatersleben.de"))),
				mysubject);

		/* same object in INPUTFILES */
		saveDir(INPUTFILES, rootDirectory);

		/* shutdown eDAL */
		DataManager.shutdown();

		/* search some files and reload a directory */
		/* start eDAL and get the root directory */
		rootDirectory = DataManager.getRootDirectory(EdalHelpers
				.getFileSystemImplementationProvider(false,
						new EdalConfiguration("", "", "10.5072",
								new InternetAddress("test@online.de"),
								new InternetAddress("test@online.de"),
								new InternetAddress("test@online.de"),
								new InternetAddress(
										"eDAL0815@ipk-gatersleben.de"))),
				mysubject);

		searchElement(rootDirectory, EnumDublinCoreElements.TYPE,
				new UntypedData("programm"));

		/* load objects of the root directory */
		loadDir(rootDirectory);

		/* shutdown eDAL */
		DataManager.shutdown();

	}

	/**
	 * Save recursively all {@link PrimaryDataEntity} objects.
	 * 
	 * @param currentFile
	 *            the current {@link File} object to save.
	 * @param currentDir
	 *            the current {@link PrimaryDataDirectory} object to save.
	 */
	private static void saveDir(final File currentFile,
			final PrimaryDataDirectory currentDir)
			throws PrimaryDataDirectoryException,
			PrimaryDataEntityVersionException, CloneNotSupportedException,
			MetaDataException, FileNotFoundException, PrimaryDataFileException {

		File[] files = currentFile.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {

					PrimaryDataDirectory subDir = currentDir
							.createPrimaryDataDirectory(file.getName());
					saveDir(file, subDir);

				} else {

					PrimaryDataFile subFile = currentDir
							.createPrimaryDataFile(file.getName());

					subFile.store(new FileInputStream(file));

					MetaData fileMetaData = subFile.getMetaData().clone();

					fileMetaData.setElementValue(
							EnumDublinCoreElements.CREATOR, new NaturalPerson(
									"CreaterName", "Given_Name", "Gatersleben",
									"01234", "Germany"));
					fileMetaData.setElementValue(
							EnumDublinCoreElements.LANGUAGE, new EdalLanguage(
									Locale.GERMANY));
					fileMetaData.setElementValue(
							EnumDublinCoreElements.IDENTIFIER, new Identifier(
									UUID.randomUUID().toString()
											.substring(0, 8)));
					fileMetaData.setElementValue(EnumDublinCoreElements.TYPE,
							new DataType(EnumDCMIDataType.SOFTWARE));
					fileMetaData.setElementValue(
							EnumDublinCoreElements.DESCRIPTION,
							new UntypedData(
									"this is the description of the file"));

					subFile.setMetaData(fileMetaData);

				}
			}
		}
	}

	/**
	 * Load recursively all {@link PrimaryDataEntity} object in the current
	 * {@link PrimaryDataDirectory} an save complete structure to 'outputFile'.
	 * 
	 * @param currentDirectory
	 *            the current {@link PrimaryDataDirectory}.
	 * 
	 */
	private static void loadDir(final PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException, PrimaryDataFileException,
			FileNotFoundException {

		final List<PrimaryDataEntity> list = currentDirectory
				.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {

				if (!primaryDataEntity.isDirectory()) {
					PrimaryDataFile file = (PrimaryDataFile) primaryDataEntity;

					File tmp = new File(OUTPUTFILES.getAbsolutePath()
							+ File.separatorChar + primaryDataEntity);

					if (!tmp.exists()) {
						try {
							tmp.getParentFile().mkdirs();
							tmp.createNewFile();
						} catch (IOException e) {
							System.out.println("couldn't create File");
						}
					}

					FileOutputStream outputStream = new FileOutputStream(tmp);
					file.read(outputStream);

				}
				if (primaryDataEntity.isDirectory()) {
					OUTPUTFILES = new File(OUTPUTFILES.getAbsolutePath()
							+ File.separatorChar + primaryDataEntity);
					loadDir((PrimaryDataDirectory) primaryDataEntity);
				}
			}
			OUTPUTFILES = new File(OUTPUTFILES.getParentFile()
					.getAbsolutePath());
		}
	}

	/**
	 * Search fuzzy and recursively in the {@link PrimaryDataDirectory}.
	 * 
	 * @param dir
	 *            {@link PrimaryDataDirectory}
	 * @param element
	 *            {@link EnumDublinCoreElements}
	 * @param data
	 *            {@link UntypedData}
	 * @throws PrimaryDataDirectoryException
	 */
	private static void searchElement(PrimaryDataDirectory dir,
			EnumDublinCoreElements element, UntypedData data)
			throws PrimaryDataDirectoryException {

		List<PrimaryDataEntity> results = dir.searchByDublinCoreElement(
				element, data, true, true);

		for (PrimaryDataEntity primaryDataEntity : results) {
			System.out.println(primaryDataEntity.getMetaData());
		}

	}

}
