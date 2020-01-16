/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DescriptionType;

/**
 * Class Description.
 * 
 * @author arendd
 */
@XmlType(name = "description")
public class XmlDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field descriptionType.
	 */
	private DescriptionType descriptionType;

	/**
	 * internal content storage
	 */
	private String content = "";

	public XmlDescription() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlDescription} with the specified
	 * description.
	 * 
	 * @param content
	 *            the description to set.
	 */
	public XmlDescription(String content) {
		this();
		setContent(content);
	}

	/**
	 * Constructor to create a {@link XmlDescription} with the specified
	 * description and descriptionType.
	 * 
	 * @param content
	 *            the description to set.
	 * @param descriptionType
	 *            the descriptionType to set.
	 */
	public XmlDescription(String content, DescriptionType descriptionType) {
		this(content);
		setDescriptionType(descriptionType);
	}

	/**
	 * Returns the value of field 'content'. The field 'content' has the
	 * following description: internal content storage
	 * 
	 * @return the value of field 'Content'.
	 */
	@XmlValue
	public String getContent() {
		return this.content;
	}

	/**
	 * Returns the value of field 'descriptionType'.
	 * 
	 * @return the value of field 'DescriptionType'.
	 */
	@XmlAttribute
	public DescriptionType getDescriptionType() {
		return this.descriptionType;
	}

	/**
	 * Sets the value of field 'content'. The field 'content' has the following
	 * description: internal content storage
	 * 
	 * @param content
	 *            the value of field 'content'.
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * Sets the value of field 'descriptionType'.
	 * 
	 * @param descriptionType
	 *            the value of field 'descriptionType'.
	 */
	public void setDescriptionType(final DescriptionType descriptionType) {
		this.descriptionType = descriptionType;
	}

}
