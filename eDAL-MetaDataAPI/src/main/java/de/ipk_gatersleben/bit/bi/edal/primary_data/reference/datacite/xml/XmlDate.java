/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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