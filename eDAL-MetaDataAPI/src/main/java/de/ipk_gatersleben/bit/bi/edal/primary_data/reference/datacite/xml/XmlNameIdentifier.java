/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

/**
 * Class NameIdentifier.
 * 
 * @author arendd
 */
@XmlType
public class XmlNameIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * internal content storage
	 */
	private String value = "";

	/**
	 * Field nameIdentifierScheme.
	 */
	private String nameIdentifierScheme;

	/**
	 * Field nameIdentifierSchemeURI.
	 */
	private String schemeURI;

	public XmlNameIdentifier() {
		super();
		setValue("");
	}

	public XmlNameIdentifier(final String value) {
		setValue(value);
	}

	public XmlNameIdentifier(final String value, String nameIdentifierScheme, String schemeURI) {
		setValue(value);
		setNameIdentifierScheme(nameIdentifierScheme);
		setSchemeURI(schemeURI);
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
	 * Returns the value of field 'nameIdentifierScheme'.
	 * 
	 * @return the value of field 'nameIdentifierScheme'.
	 */
	@XmlAttribute(required = true)
	public String getNameIdentifierScheme() {
		return this.nameIdentifierScheme;
	}

	/**
	 * Sets the field 'value'. The field 'value' has the following description:
	 * internal content storage
	 * 
	 * @param value
	 *            the value of field 'value'.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets the value of field 'nameIdentifierScheme'.
	 * 
	 * @param nameIdentifierScheme
	 *            the value of field 'nameIdentifierScheme'.
	 */
	public void setNameIdentifierScheme(final String nameIdentifierScheme) {
		this.nameIdentifierScheme = nameIdentifierScheme;
	}

	/**
	 * Returns the value of field 'schemeURI'.
	 * 
	 * @return the value of field 'schemeURI'.
	 */
	@XmlAttribute(required = true)
	public String getSchemeURI() {
		return schemeURI;
	}

	/**
	 * Sets the value of field 'schemeURI'.
	 * 
	 * @param schemeURI
	 *            the value of field 'schemeURI'.
	 */
	public void setSchemeURI(String schemeURI) {
		this.schemeURI = schemeURI;
	}

}
