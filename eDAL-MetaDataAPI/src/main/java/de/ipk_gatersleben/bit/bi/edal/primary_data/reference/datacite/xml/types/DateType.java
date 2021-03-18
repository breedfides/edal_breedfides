/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * The type of date. To indicate a date period, provide two dates, specifying
 * the StartDate and the EndDate. To indicate the end of an embargo period, use
 * Available. To indicate the start of an embargo period, use Submitted or
 * Accepted, as appropriate.
 * 
 * @author arendd
 */
public enum DateType {

	/**
	 * Constant ACCEPTED
	 */
	Accepted("Accepted"),
	/**
	 * Constant AVAILABLE
	 */
	Available("Available"),
	/**
	 * Constant COPYRIGHTED
	 */
	Copyrighted("Copyrighted"),
	/**
	 * Constant CREATED
	 */
	Created("Created"),
	/**
	 * Constant COLLECTED
	 */
	Collected("Collected"),
	/**
	 * Constant ISSUED
	 */
	Issued("Issued"),
	/**
	 * Constant SUBMITTED
	 */
	Submitted("Submitted"),
	/**
	 * Constant UPDATED
	 */
	Updated("Updated"),
	/**
	 * Constant VALID
	 */
	Valid("Valid");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, DateType> ENUM_CONSTANTS = new HashMap<String, DateType>();

	static {
		for (DateType c : DateType.values()) {
			DateType.ENUM_CONSTANTS.put(c.value, c);
		}

	};

	private DateType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the date to set
	 * @return the constant for this value
	 */
	public static DateType fromValue(final String value) {
		DateType c = DateType.ENUM_CONSTANTS.get(value);
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
