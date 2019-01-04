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

/**
 * A persistent identifier that identifies a resource. Currently, only DOI is
 * allowed.
 * 
 * @author arendd
 */

@XmlType
public class XmlIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field identifier.
	 */
	private String identifier = "";

	/**
	 * Field identifierType.
	 */
	private String identifierType = "DOI";

	public XmlIdentifier() {
		super();
		setIdentifierType("DOI");
	}

	public XmlIdentifier(final String identifier) {
		setIdentifier(identifier);
	}

	/**
	 * Returns the value of field 'identifier'.
	 * 
	 * @return the value of field 'identifier'.
	 */
	@XmlValue
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Returns the value of field 'identifierType'.
	 * 
	 * @return the value of field 'IdentifierType'.
	 */
	@XmlAttribute
	public String getIdentifierType() {
		return this.identifierType;
	}

	/**
	 * Sets the value of field 'identifier'.
	 * 
	 * @param identifier
	 *            the value of field 'identifier'.
	 */
	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the value of field 'identifierType'.
	 * 
	 * @param identifierType
	 *            the value of field 'identifierType'.
	 */
	public void setIdentifierType(final String identifierType) {
		this.identifierType = identifierType;
	}
}