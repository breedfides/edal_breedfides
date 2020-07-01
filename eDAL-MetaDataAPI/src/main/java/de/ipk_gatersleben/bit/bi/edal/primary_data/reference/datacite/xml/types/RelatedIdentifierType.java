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
