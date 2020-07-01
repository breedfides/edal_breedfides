/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
