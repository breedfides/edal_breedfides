/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.metadata;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DirectoryMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

public class MetaDataTest extends EdalDefaultTestCase {

	@Test
	public void testRenameObjects() throws PrimaryDataDirectoryException, EdalException, EdalAuthenticateException,
			CloneNotSupportedException, MetaDataException, PrimaryDataEntityVersionException {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("file");

		Assertions.assertEquals(file.getVersions().size(), 1);

		file.rename("newFile");

		Assertions.assertEquals(file.getVersions().size(), 2);

		final PrimaryDataDirectory dir = rootDirectory.createPrimaryDataDirectory("dir");

		Assertions.assertEquals(dir.getVersions().size(), 1);

		dir.rename("newDir");

		Assertions.assertEquals(dir.getVersions().size(), 2);

	}

	/**
	 * Test different cases when trying to set or change the {@link DateEvents} of
	 * an {@link PrimaryDataEntity}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetDateEvents() throws Exception {

		/**
		 * test what happens if trying to set a date event with no CREATED and MODIFIED
		 * date
		 */
		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("file");

		final MetaData metaData = file.getMetaData();

		final DateEvents dateEvents = new DateEvents("eventdate");

		dateEvents.add(new EdalDate(new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "Controlled"));

		metaData.setElementValue(EnumDublinCoreElements.DATE, dateEvents);

		try {
			file.setMetaData(metaData);
			Assertions.fail("it should be not allowed to set a date event with no CREATED AND UPDATED date");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, "not allowed to set a date event with no CREATED AND UPDATED date: " + e.getMessage());
		}

		Assertions.assertEquals(
				((DateEvents) file.getMetaData().getElementValue(EnumDublinCoreElements.DATE)).getSet().size(), 2);

		/**
		 * test that it is allowed to set another date, when CREATED and UPDATED is
		 * already set
		 */
		final MetaData metaData2 = file.getMetaData();

		final DateEvents dateEvents2 = metaData2.getElementValue(EnumDublinCoreElements.DATE);

		dateEvents2.add(new EdalDate(new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "Controlled"));

		metaData2.setElementValue(EnumDublinCoreElements.DATE, dateEvents2);

		try {
			file.setMetaData(metaData2);
			Assertions.assertTrue(true, "it is allowed to set an other date ");
		} catch (final MetaDataException e) {
			Assertions.fail("it should be  allowed to set an other date :" + e.getMessage());
		}

		Assertions.assertEquals(
				((DateEvents) file.getMetaData().getElementValue(EnumDublinCoreElements.DATE)).getSet().size(), 3);

		/** test what happen, when trying to set /change CREATED date */
		final MetaData metaData3 = file.getMetaData();

		final DateEvents dateEvents3 = metaData3.getElementValue(EnumDublinCoreElements.DATE);

		dateEvents3.add(new EdalDate(new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "CREATED"));

		metaData3.setElementValue(EnumDublinCoreElements.DATE, dateEvents);

		try {
			file.setMetaData(metaData3);
			Assertions.fail("it should be not allowed to set the CREATED date");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, "it is not allowed to set the CREATED date");
		}

		Assertions.assertEquals(
				((DateEvents) file.getMetaData().getElementValue(EnumDublinCoreElements.DATE)).getSet().size(), 3);

		/** test what happen, when trying to set /change UPDATED date */
		final MetaData metaData4 = file.getMetaData();

		final DateEvents dateEvents4 = metaData3.getElementValue(EnumDublinCoreElements.DATE);

		dateEvents4.add(new EdalDate(new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "UPDATED"));

		metaData4.setElementValue(EnumDublinCoreElements.DATE, dateEvents);

		try {
			file.setMetaData(metaData4);
			Assertions.fail("it should be not allowed to set the UPDATED date");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, "it is not allowed to set the UPDATED date");
		}

		Assertions.assertEquals(
				((DateEvents) file.getMetaData().getElementValue(EnumDublinCoreElements.DATE)).getSet().size(), 3);

	}

	/**
	 * Test that it is not allowed to call setMetaData() and overwrite the
	 * {@link DataSize} and the {@link CheckSum} of a {@link PrimaryDataFile}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetDatSizeOrCheckSumForFile() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		/** test for data size */
		final PrimaryDataFile file1 = rootDirectory.createPrimaryDataFile("file");

		Assertions.assertEquals(file1.getVersions().size(), 1);

		final MetaData metaData1 = file1.getMetaData();

		final DataSize dataSizeBefore = metaData1.getElementValue(EnumDublinCoreElements.SIZE);

		metaData1.setElementValue(EnumDublinCoreElements.SIZE, new DataSize(Long.valueOf(100)));

		try {
			file1.setMetaData(metaData1);
			Assertions.fail("not allowed to set DataSize for PrimaryDataFile");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}

		Assertions.assertEquals(file1.getVersions().size(), 1);

		/** the type must be the same as before **/
		Assertions.assertEquals(file1.getMetaData().getElementValue(EnumDublinCoreElements.SIZE), dataSizeBefore);

		/** test for checksum */
		final PrimaryDataFile file2 = rootDirectory.createPrimaryDataFile("file2");

		Assertions.assertEquals(file2.getVersions().size(), 1);

		final MetaData metaData2 = file2.getMetaData();

		final CheckSum checkSumBefore = metaData2.getElementValue(EnumDublinCoreElements.CHECKSUM);

		final CheckSum checkSum = new CheckSum();

		checkSum.add(new CheckSumType("md5", "checkSum"));

		metaData2.setElementValue(EnumDublinCoreElements.CHECKSUM, checkSum);

		try {
			file2.setMetaData(metaData2);
			Assertions.fail("not allowed to set checksum for PrimaryDataFile");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}
		Assertions.assertEquals(file2.getVersions().size(), 1);

		Assertions.assertEquals(file2.getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM), checkSumBefore);

	}

	/**
	 * Test what happen if trying to change the
	 * {@link EnumDublinCoreElements#FORMAT} of a {@link PrimaryDataDirectory}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFormatForDirectory() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataDirectory directory = rootDirectory.createPrimaryDataDirectory("directory");

		final MetaData metaData = directory.getMetaData();

		/** not allowed : set other Format for directory */
		metaData.setElementValue(EnumDublinCoreElements.FORMAT, new DataFormat("image/jpeg"));

		try {
			directory.setMetaData(metaData);
			Assertions.fail("not allowed to set other Format for a PrimaryDataDirectory");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}
		/** the type must be the same as before **/
		Assertions.assertEquals(directory.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT).toString(),
				new EmptyMetaData().toString());

	}

	/**
	 * Test what happen if trying to set the {@link EnumDublinCoreElements#FORMAT}
	 * of a {@link PrimaryDataFile}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetFormatForFile() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("file");

		final MetaData metaData = file.getMetaData();

		/** not allowed : set Format for file as DirectoryMetaData */
		metaData.setElementValue(EnumDublinCoreElements.FORMAT, new DirectoryMetaData());

		try {
			file.setMetaData(metaData);
			Assertions.fail("not allowed to set DirectoryMetaData for the FORMAT for a PrimaryDataFile");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}

		/** the type must be the same as before **/
		Assertions.assertEquals(file.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT).toString(),
				new DataFormat("application/octet-stream").toString());

		final MetaData metaData2 = file.getMetaData();

		/** not allowed : set Format for file as EmptyMetaData */
		metaData2.setElementValue(EnumDublinCoreElements.FORMAT, new EmptyMetaData());

		try {
			file.setMetaData(metaData2);
			Assertions.fail("not allowed to set EmptyMetaData for the FORMAT of a PrimaryDataFile");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}

		/** the type must be the same as before **/
		Assertions.assertEquals(file.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT).toString(),
				new DataFormat("application/octet-stream").toString());

	}

	/**
	 * Test that it is allowed to call setMetaData() and overwrite all other
	 * elements of a {@link PrimaryDataFile}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetMetaDataForFile() throws PrimaryDataDirectoryException, EdalException, EdalAuthenticateException,
			CloneNotSupportedException, MetaDataException, PrimaryDataEntityVersionException {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("file");

		Assertions.assertEquals(file.getVersions().size(), 1);

		final MetaData metaData = file.getMetaData();

		metaData.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("description"));

		try {
			file.setMetaData(metaData);
			Assertions.assertTrue(true);
		} catch (final MetaDataException e) {
			Assertions.fail("not allowed to set description for PrimaryDataFile");

		}

		Assertions.assertEquals(file.getVersions().size(), 2);

	}

	/**
	 * Test what happen if trying to change the {@link EnumDublinCoreElements#TYPE}
	 * of a {@link PrimaryDataDirectory}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetTypeForDirectory() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataDirectory directory = rootDirectory.createPrimaryDataDirectory("directory");

		final MetaData metaData = directory.getMetaData();

		/** not allowed : set other DataType for directory */
		metaData.setElementValue(EnumDublinCoreElements.TYPE, new DataType(EnumDCMIDataType.DATASET));

		try {
			directory.setMetaData(metaData);
			Assertions.fail("not allowed to set other DataType for a PrimaryDataDirectory");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true, e.getMessage());
		}
		/** the type must be the same as before **/
		Assertions.assertEquals(directory.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).toString(),
				new DirectoryMetaData().toString());

	}

	/**
	 * Test what happen if trying to set the {@link EnumDublinCoreElements#TYPE} of
	 * a {@link PrimaryDataFile} as {@link DirectoryMetaData}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetTypeForFile() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("file");

		final MetaData metaData = file.getMetaData();

		/** not allowed : set DirectoryMetaData for file */
		metaData.setElementValue(EnumDublinCoreElements.TYPE, new DirectoryMetaData());

		try {
			file.setMetaData(metaData);
			Assertions.fail("not allowed to set DirectoryMetaData for a PrimaryDataFile");
		} catch (final MetaDataException e) {
			Assertions.assertTrue(true,e.getMessage());
		}

		/** the type must be the same as before **/
		Assertions.assertEquals(file.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).toString(),
				new DataType(EnumDCMIDataType.TEXT).toString());

	}

	/**
	 * Test the generation of how to cite string of a {@link PrimaryDataEntity}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testToString() throws Exception {

		final PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, this.configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		final PrimaryDataFile file = rootDirectory.createPrimaryDataFile("My Test Data");

		final MetaData meta = file.getMetaData();

		final Persons creators = new Persons();

		meta.setElementValue(EnumDublinCoreElements.CREATOR, creators);
		meta.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("IPK", "Gatersleben", "01234", "germany"));
		meta.setElementValue(EnumDublinCoreElements.RIGHTS, new UntypedData("opensource"));

		final DateEvents event = meta.getElementValue(EnumDublinCoreElements.DATE);

		final EdalDateRange range = new EdalDateRange(Calendar.getInstance(), EdalDatePrecision.SECOND,
				Calendar.getInstance(), EdalDatePrecision.SECOND, "event");

		event.add(range);

		meta.setElementValue(EnumDublinCoreElements.DATE, event);

		file.setMetaData(meta);
		// check if citable metadata is longer than minimum expected citation
		// string
		Assertions.assertTrue(meta.toString().length() > 31);

	}
}