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
 * Enumeration TitleType.
 * 
 * @author arendd
 */
public enum TitleType {

	/**
	 * Constant ALTERNATIVETITLE
	 */
	AlternativeTitle("AlternativeTitle"),
	/**
	 * Constant SUBTITLE
	 */
	Subtitle("Subtitle"),
	/**
	 * Constant TRANSLATEDTITLE
	 */
	TranslatedTitle("TranslatedTitle");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, TitleType> ENUM_CONSTANTS = new HashMap<String, TitleType>();

	static {
		for (TitleType c : TitleType.values()) {
			TitleType.ENUM_CONSTANTS.put(c.value, c);
		}
	};

	private TitleType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the title type
	 * @return the constant for this value
	 */
	public static TitleType fromValue(final String value) {
		TitleType c = TitleType.ENUM_CONSTANTS.get(value);
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
