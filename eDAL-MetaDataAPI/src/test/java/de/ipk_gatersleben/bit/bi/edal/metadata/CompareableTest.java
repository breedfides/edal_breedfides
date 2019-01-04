/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.metadata;

import java.util.GregorianCalendar;
import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormatException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * JUnit test to check if the implementation of the compareTo() function of the
 * eDAL data types works.
 * 
 * @author arendd
 */
public class CompareableTest {

	private static String FAIL = "compareTo() failed";

	@Test
	public void testUntypedDataCompareTo() {

		UntypedData myData = new UntypedData("myData");

		UntypedData yourData = new UntypedData("yourData");

		Assert.assertTrue(FAIL, myData.compareTo(yourData) < 0);

		Assert.assertTrue(FAIL, yourData.compareTo(myData) > 0);

		Assert.assertTrue(FAIL, myData.compareTo(myData) == 0);

	}

	@Test
	public void testPersonCompareTo() {

		NaturalPerson myNPerson = new NaturalPerson("max", "muster", "berlin",
				"01234", "germany");

		NaturalPerson yourNPerson = new NaturalPerson("john", "doe", "boston",
				"43210", "usa");

		Assert.assertTrue(FAIL, myNPerson.compareTo(yourNPerson) > 0);

		Assert.assertTrue(FAIL, yourNPerson.compareTo(myNPerson) < 0);

		Assert.assertTrue(FAIL, myNPerson.compareTo(myNPerson) == 0);

		LegalPerson myLPerson = new LegalPerson("IPK", "gatersleben", "06466",
				"germany");

		LegalPerson yourLPerson = new LegalPerson("MIT", "boston", "43210",
				"germany");

		Assert.assertTrue(FAIL, myLPerson.compareTo(yourLPerson) < 0);

		Assert.assertTrue(FAIL, yourLPerson.compareTo(myLPerson) > 0);

		Assert.assertTrue(FAIL, myLPerson.compareTo(myLPerson) == 0);
	}

	@Test
	public void testCheckSumCompareTo() {

		CheckSumType myCheckSumType = new CheckSumType("MD5", "12345");

		CheckSumType yourCheckSumType = new CheckSumType("SHA", "54321");

		Assert.assertTrue(FAIL, myCheckSumType.compareTo(yourCheckSumType) < 0);

		Assert.assertTrue(FAIL, yourCheckSumType.compareTo(myCheckSumType) > 0);

		Assert.assertTrue(FAIL, myCheckSumType.compareTo(myCheckSumType) == 0);

		CheckSum myCheckSum = new CheckSum();

		myCheckSum.add(myCheckSumType);
		myCheckSum.add(yourCheckSumType);

		CheckSum yourCheckSum = new CheckSum();

		yourCheckSum.add(yourCheckSumType);
		yourCheckSum.add(myCheckSumType);

		Assert.assertTrue(FAIL, myCheckSum.compareTo(yourCheckSum) == 0);

		yourCheckSum.remove(yourCheckSumType);

		Assert.assertTrue(FAIL, myCheckSum.compareTo(yourCheckSum) > 0);

		Assert.assertTrue(FAIL, yourCheckSum.compareTo(myCheckSum) < 0);

	}

	@Test
	public void testDataTypeCompareTo() {

		DataType myDataType = new DataType(EnumDCMIDataType.IMAGE);

		DataType yourDataType = new DataType(EnumDCMIDataType.SOFTWARE);

		Assert.assertTrue(FAIL, myDataType.compareTo(yourDataType) < 0);

		Assert.assertTrue(FAIL, yourDataType.compareTo(myDataType) > 0);

		Assert.assertTrue(FAIL, myDataType.compareTo(myDataType) == 0);

	}

	@Test
	public void testDataFormatCompareTo() {
		try {
			DataFormat myDataType = new DataFormat("application/pdf");

			DataFormat yourDataFormat = new DataFormat("image/gif");

			Assert.assertTrue(FAIL, myDataType.compareTo(yourDataFormat) < 0);

			Assert.assertTrue(FAIL, yourDataFormat.compareTo(myDataType) > 0);

			Assert.assertTrue(FAIL, myDataType.compareTo(myDataType) == 0);

		} catch (DataFormatException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testDataSizeCompareTo() {

		DataSize myDataSize = new DataSize(Long.valueOf(100));

		DataSize yourDataSize = new DataSize(Long.valueOf(1000));

		Assert.assertTrue(FAIL, myDataSize.compareTo(yourDataSize) < 0);

		Assert.assertTrue(FAIL, yourDataSize.compareTo(myDataSize) > 0);

		Assert.assertTrue(FAIL, myDataSize.compareTo(myDataSize) == 0);

	}

	@Test
	public void testDataEventsCompareTo() {

		EdalDate myEdalDate = new EdalDate(new GregorianCalendar(2012, 12, 12),
				EdalDatePrecision.HOUR, "event");

		EdalDate yourEdalDate = new EdalDate(
				new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR,
				"event");

		Assert.assertTrue(FAIL, myEdalDate.compareTo(yourEdalDate) > 0);

		Assert.assertTrue(FAIL, yourEdalDate.compareTo(myEdalDate) < 0);

		Assert.assertTrue(FAIL, myEdalDate.compareTo(myEdalDate) == 0);

		EdalDateRange myEdalDateRange = new EdalDateRange(
				new GregorianCalendar(2010, 10, 10), EdalDatePrecision.HOUR,
				new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR,
				"event");

		EdalDateRange yourEdalDateRange = new EdalDateRange(
				new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR,
				new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR,
				"event");

		Assert.assertTrue(FAIL,
				myEdalDateRange.compareTo(yourEdalDateRange) < 0);

		Assert.assertTrue(FAIL,
				yourEdalDateRange.compareTo(myEdalDateRange) > 0);

		Assert.assertTrue(FAIL, myEdalDateRange.compareTo(myEdalDateRange) == 0);

		DateEvents myDateEvents = new DateEvents("myEvents");

		myDateEvents.add(myEdalDate);
		myDateEvents.add(myEdalDateRange);

		DateEvents yourDateEvents = new DateEvents("yourEvents");

		yourDateEvents.add(yourEdalDate);
		yourDateEvents.add(yourEdalDateRange);

		Assert.assertTrue(FAIL, myDateEvents.compareTo(yourDateEvents) != 0);

		Assert.assertTrue(FAIL, yourDateEvents.compareTo(myDateEvents) != 0);

		Assert.assertTrue(FAIL, myDateEvents.compareTo(myDateEvents) == 0);

	}

	@Test
	public void testIdentifierRelationCompareTo() {

		IdentifierRelation myRelation = new IdentifierRelation();
		myRelation.add(new Identifier("ID_123"));
		myRelation.add(new Identifier("ID_123"));

		Assert.assertEquals(1, myRelation.size());

		myRelation.add(new Identifier("ID_12345"));

		Assert.assertEquals(2, myRelation.size());

		IdentifierRelation yourRelation = new IdentifierRelation();
		yourRelation.add(new Identifier("ID_321"));

		Assert.assertTrue(FAIL, myRelation.compareTo(yourRelation) != 0);

		Assert.assertTrue(FAIL, yourRelation.compareTo(myRelation) != 0);

		Assert.assertTrue(FAIL, myRelation.compareTo(myRelation) == 0);

		Identifier myIdentifier = new Identifier("myID");
		Identifier yourIdentifier = new Identifier("yourID");

		Assert.assertTrue(FAIL, myIdentifier.compareTo(yourIdentifier) < 0);

		Assert.assertTrue(FAIL, yourIdentifier.compareTo(myIdentifier) > 0);

		Assert.assertTrue(FAIL, myIdentifier.compareTo(myIdentifier) == 0);
	}

}
