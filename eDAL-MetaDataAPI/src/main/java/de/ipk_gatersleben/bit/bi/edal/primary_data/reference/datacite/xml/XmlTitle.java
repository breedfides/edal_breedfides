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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.TitleType;

/**
 * A name or title by which a resource is known.
 * 
 * @author arendd
 */
@XmlType(name = "title")
public class XmlTitle implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * internal content storage
	 */
	private String value = "";

	/**
	 * Field titleType.
	 */
	private TitleType titleType;

	/**
	 * Default constructor
	 */
	public XmlTitle() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlTitle} with the specified title.
	 * 
	 * @param value
	 *            the title to set.
	 */
	public XmlTitle(String value) {
		this();
		setValue(value);
	}

	/**
	 * Constructor to create a {@link XmlTitle} with the specified title and
	 * titleType.
	 * 
	 * @param value
	 *            the title to set.
	 * @param titleType
	 *            the titleType to set.
	 */
	public XmlTitle(String value, TitleType titleType) {
		this(value);
		setTitleType(titleType);
	}

	/**
	 * Copy-Constructor to create a {@link XmlTitle} object from a
	 * {@link UntypedData} object.
	 * 
	 * @param data
	 *            to convert into a {@link XmlTitle}.
	 */
	public XmlTitle(UntypedData data) {
		this(data.getString());
	}

	/**
	 * Returns the value of field 'value'. The field 'value' has the following
	 * description: internal content storage
	 * 
	 * @return the value of field 'value'.
	 */
	@XmlValue
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the value of field 'titleType'.
	 * 
	 * @return the value of field 'titleType'.
	 */

	@XmlAttribute
	public TitleType getTitleType() {
		return this.titleType;
	}

	/**
	 * Sets the value of field 'value'. The field 'value' has the following
	 * description: internal content storage
	 * 
	 * @param value
	 *            the value of field 'value'.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets the value of field 'titleType'.
	 * 
	 * @param titleType
	 *            the value of field 'titleType'.
	 */
	public void setTitleType(final TitleType titleType) {
		this.titleType = titleType;
	}
}