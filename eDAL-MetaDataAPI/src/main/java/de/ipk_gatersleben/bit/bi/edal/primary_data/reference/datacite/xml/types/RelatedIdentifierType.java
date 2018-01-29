/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types;

import java.util.HashMap;
import java.util.Map;

/**
 * The type of the RelatedIdentifier.
 * 
 * @author arendd
 */
public enum RelatedIdentifierType {

	/**
	 * Constant ARK
	 */
	ARK("ARK"),
	/**
	 * Constant DOI
	 */
	DOI("DOI"),
	/**
	 * Constant EAN13
	 */
	EAN13("EAN13"),
	/**
	 * Constant EISSN
	 */
	EISSN("EISSN"),
	/**
	 * Constant HANDLE
	 */
	HANDLE("Handle"),
	/**
	 * Constant ISBN
	 */
	ISBN("ISBN"),
	/**
	 * Constant ISSN
	 */
	ISSN("ISSN"),
	/**
	 * Constant ISTC
	 */
	ISTC("ISTC"),
	/**
	 * Constant LISSN
	 */
	LISSN("LISSN"),
	/**
	 * Constant LSID
	 */
	LSID("LSID"),
	/**
	 * Constant PMID
	 */
	PMID("PMID"),
	/**
	 * Constant PURL
	 */
	PURL("PURL"),
	/**
	 * Constant UPC
	 */
	UPC("UPC"),
	/**
	 * Constant URL
	 */
	URL("URL"),
	/**
	 * Constant URN
	 */
	URN("URN");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, RelatedIdentifierType> ENUM_CONSTANTS = new HashMap<String, RelatedIdentifierType>();

	static {
		for (RelatedIdentifierType c : RelatedIdentifierType.values()) {
			RelatedIdentifierType.ENUM_CONSTANTS.put(c.value, c);
		}

	};

	private RelatedIdentifierType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the related identifier
	 * @return the constant for this value
	 */
	public static RelatedIdentifierType fromValue(final String value) {
		RelatedIdentifierType c = RelatedIdentifierType.ENUM_CONSTANTS.get(value);
		if (c != null) {
			return c;
		}
		throw new IllegalArgumentException(value);
	}

	/**
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
	}

	/**
	 * Method toString.
	 * 
	 * @return the value of this constant
	 */
	public String toString() {
		return this.value;
	}

	/**
	 * Method value.
	 * 
	 * @return the value of this constant
	 */
	public String value() {
		return this.value;
	}

}
