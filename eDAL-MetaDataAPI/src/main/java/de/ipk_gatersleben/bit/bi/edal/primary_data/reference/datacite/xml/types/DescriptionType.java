/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * The type of the description.
 * 
 * @author arendd
 */

public enum DescriptionType {

	/**
	 * Constant ABSTRACT
	 */
	Abstract("Abstract"),
	/**
	 * Constant SERIESINFORMATION
	 */
	SeriesInformation("SeriesInformation"),
	/**
	 * Constant TABLEOFCONTENTS
	 */
	TableOfContents("TableOfContents"),
	/**
	 * Constant OTHER
	 */
	Other("Other"),
	/**
	 * Constant METHODS
	 */
	Methods("Methods");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, DescriptionType> ENUM_CONSTANTS = new HashMap<String, DescriptionType>();

	static {
		for (DescriptionType c : DescriptionType.values()) {
			DescriptionType.ENUM_CONSTANTS.put(c.value, c);
		}

	};

	private DescriptionType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the description to set
	 * @return the constant for this value
	 */
	public static DescriptionType fromValue(final String value) {
		DescriptionType c = DescriptionType.ENUM_CONSTANTS.get(value);
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
