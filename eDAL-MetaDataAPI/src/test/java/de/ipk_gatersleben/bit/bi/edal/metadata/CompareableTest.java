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

import java.util.GregorianCalendar;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

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
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;

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

		Assertions.assertTrue(myData.compareTo(yourData) < 0, FAIL);

		Assertions.assertTrue(yourData.compareTo(myData) > 0, FAIL);

		Assertions.assertTrue(myData.compareTo(myData) == 0, FAIL);

	}

	@Test
	public void testPersonCompareTo() {

		NaturalPerson myNPerson = new NaturalPerson("max", "muster", "berlin", "01234", "germany");

		NaturalPerson yourNPerson = new NaturalPerson("john", "doe", "boston", "43210", "usa");

		Assertions.assertTrue(myNPerson.compareTo(yourNPerson) > 0, FAIL);

		Assertions.assertTrue(yourNPerson.compareTo(myNPerson) < 0, FAIL);

		Assertions.assertTrue(myNPerson.compareTo(myNPerson) == 0, FAIL);

		LegalPerson myLPerson = new LegalPerson("IPK", "gatersleben", "06466", "germany");

		LegalPerson yourLPerson = new LegalPerson("MIT", "boston", "43210", "germany");

		Assertions.assertTrue(myLPerson.compareTo(yourLPerson) < 0, FAIL);

		Assertions.assertTrue(yourLPerson.compareTo(myLPerson) > 0, FAIL);

		Assertions.assertTrue(myLPerson.compareTo(myLPerson) == 0, FAIL);
	}

	@Test
	public void testCheckSumCompareTo() {

		CheckSumType myCheckSumType = new CheckSumType("MD5", "12345");

		CheckSumType yourCheckSumType = new CheckSumType("SHA", "54321");

		Assertions.assertTrue(myCheckSumType.compareTo(yourCheckSumType) < 0, FAIL);

		Assertions.assertTrue(yourCheckSumType.compareTo(myCheckSumType) > 0, FAIL);

		Assertions.assertTrue(myCheckSumType.compareTo(myCheckSumType) == 0, FAIL);

		CheckSum myCheckSum = new CheckSum();

		myCheckSum.add(myCheckSumType);
		myCheckSum.add(yourCheckSumType);

		CheckSum yourCheckSum = new CheckSum();

		yourCheckSum.add(yourCheckSumType);
		yourCheckSum.add(myCheckSumType);

		Assertions.assertTrue(myCheckSum.compareTo(yourCheckSum) == 0, FAIL);

		yourCheckSum.remove(yourCheckSumType);

		Assertions.assertTrue(myCheckSum.compareTo(yourCheckSum) > 0, FAIL);

		Assertions.assertTrue(yourCheckSum.compareTo(myCheckSum) < 0, FAIL);

	}

	@Test
	public void testDataTypeCompareTo() {

		DataType myDataType = new DataType(EnumDCMIDataType.IMAGE);

		DataType yourDataType = new DataType(EnumDCMIDataType.SOFTWARE);

		Assertions.assertTrue(myDataType.compareTo(yourDataType) < 0, FAIL);

		Assertions.assertTrue(yourDataType.compareTo(myDataType) > 0, FAIL);

		Assertions.assertTrue(myDataType.compareTo(myDataType) == 0, FAIL);

	}

	@Test
	public void testDataFormatCompareTo() {
		try {
			DataFormat myDataType = new DataFormat("application/pdf");

			DataFormat yourDataFormat = new DataFormat("image/gif");

			Assertions.assertTrue(myDataType.compareTo(yourDataFormat) < 0, FAIL);

			Assertions.assertTrue(yourDataFormat.compareTo(myDataType) > 0, FAIL);

			Assertions.assertTrue(myDataType.compareTo(myDataType) == 0, FAIL);

		} catch (DataFormatException e) {
			Assertions.fail(e.getMessage());
		}

	}

	@Test
	public void testDataSizeCompareTo() {

		DataSize myDataSize = new DataSize(Long.valueOf(100));

		DataSize yourDataSize = new DataSize(Long.valueOf(1000));

		Assertions.assertTrue(myDataSize.compareTo(yourDataSize) < 0, FAIL);

		Assertions.assertTrue(yourDataSize.compareTo(myDataSize) > 0, FAIL);

		Assertions.assertTrue(myDataSize.compareTo(myDataSize) == 0, FAIL);

	}

	@Test
	public void testDataEventsCompareTo() {

		EdalDate myEdalDate = new EdalDate(new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "event");

		EdalDate yourEdalDate = new EdalDate(new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR, "event");

		Assertions.assertTrue(myEdalDate.compareTo(yourEdalDate) > 0, FAIL);

		Assertions.assertTrue(yourEdalDate.compareTo(myEdalDate) < 0, FAIL);

		Assertions.assertTrue(myEdalDate.compareTo(myEdalDate) == 0, FAIL);

		EdalDateRange myEdalDateRange = new EdalDateRange(new GregorianCalendar(2010, 10, 10), EdalDatePrecision.HOUR,
				new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR, "event");

		EdalDateRange yourEdalDateRange = new EdalDateRange(new GregorianCalendar(2011, 11, 11), EdalDatePrecision.HOUR,
				new GregorianCalendar(2012, 12, 12), EdalDatePrecision.HOUR, "event");

		Assertions.assertTrue(myEdalDateRange.compareTo(yourEdalDateRange) < 0, FAIL);

		Assertions.assertTrue(yourEdalDateRange.compareTo(myEdalDateRange) > 0, FAIL);

		Assertions.assertTrue(myEdalDateRange.compareTo(myEdalDateRange) == 0, FAIL);

		DateEvents myDateEvents = new DateEvents("myEvents");

		myDateEvents.add(myEdalDate);
		myDateEvents.add(myEdalDateRange);

		DateEvents yourDateEvents = new DateEvents("yourEvents");

		yourDateEvents.add(yourEdalDate);
		yourDateEvents.add(yourEdalDateRange);

		Assertions.assertTrue(myDateEvents.compareTo(yourDateEvents) != 0, FAIL);

		Assertions.assertTrue(yourDateEvents.compareTo(myDateEvents) != 0, FAIL);

		Assertions.assertTrue(myDateEvents.compareTo(myDateEvents) == 0, FAIL);

	}

	@Test
	public void testIdentifierRelationCompareTo() {

		IdentifierRelation myRelation = new IdentifierRelation();
		myRelation.add(new Identifier("ID_123", RelatedIdentifierType.DOI, RelationType.IsPartOf));
		myRelation.add(new Identifier("ID_123", RelatedIdentifierType.DOI, RelationType.IsPartOf));

		Assertions.assertEquals(1, myRelation.size());

		myRelation.add(new Identifier("ID_12345", RelatedIdentifierType.DOI, RelationType.IsPartOf));

		Assertions.assertEquals(2, myRelation.size());

		IdentifierRelation yourRelation = new IdentifierRelation();
		myRelation.add(new Identifier("ID_321", RelatedIdentifierType.DOI, RelationType.IsPartOf));

		Assertions.assertTrue(myRelation.compareTo(yourRelation) != 0, FAIL);

		Assertions.assertTrue(yourRelation.compareTo(myRelation) != 0, FAIL);

		Assertions.assertTrue(myRelation.compareTo(myRelation) == 0, FAIL);

		Identifier myIdentifier = new Identifier("my_ID", RelatedIdentifierType.DOI, RelationType.IsPartOf);
		Identifier yourIdentifier = new Identifier("your_ID", RelatedIdentifierType.DOI, RelationType.IsPartOf);

		Assertions.assertTrue(myIdentifier.compareTo(yourIdentifier) < 0, FAIL);

		Assertions.assertTrue(yourIdentifier.compareTo(myIdentifier) > 0, FAIL);

		Assertions.assertTrue(myIdentifier.compareTo(myIdentifier) == 0, FAIL);
	}

}
