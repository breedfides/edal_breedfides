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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DateType;

/**
 * Different dates relevant to the work. YYYY or YYYY-MM-DD or any other format
 * described in W3CDTF (http://www.w3.org/TR/NOTE-datetime)
 * 
 * @author arendd
 */
@XmlType(name = "date")
public class XmlDate implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field dateType.
	 */
	private DateType dateType;

	/**
	 * Field value.
	 */
	private String value;

	/**
	 * Default constructor
	 */
	public XmlDate() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlDate} with the specified date.
	 * 
	 * @param value
	 *            the date to set.
	 */
	public XmlDate(String value) {
		this();
		setValue(value);

	}

	/**
	 * Constructor to create a {@link XmlDate} with the specified date and
	 * dateType.
	 * 
	 * @param value
	 *            the date to set.
	 * @param dateType
	 *            the dateType to set.
	 */
	public XmlDate(String value, DateType dateType) {
		this(value);
		setDateType(dateType);
	}

	/**
	 * Returns the value of field 'dateType'.
	 * 
	 * @return the value of field 'dateType'.
	 */
	@XmlAttribute
	public DateType getDateType() {
		return this.dateType;
	}

	/**
	 * Sets the value of field 'dateType'.
	 * 
	 * @param dateType
	 *            the value of field 'dateType'.
	 */
	public void setDateType(final DateType dateType) {
		this.dateType = dateType;
	}

	/**
	 * Sets the value of field 'value'.
	 * 
	 * @param value
	 *            the value of field 'value'.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the value of field 'value'.
	 * 
	 * @return the value of field 'value'.
	 */
	@XmlValue
	public String getValue() {
		return value;
	}
}