/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * Description of the relationship of the resource being registered (A) and the
 * related resource (B).
 * 
 * @author arendd
 */
public enum RelationType {

	/**
	 * Constant ISCITEDBY
	 */
	IsCitedBy("IsCitedBy"),
	/**
	 * Constant CITES
	 */
	Cites("Cites"),
	/**
	 * Constant ISSUPPLEMENTTO
	 */
	IsSupplementTo("IsSupplementTo"),
	/**
	 * Constant ISSUPPLEMENTEDBY
	 */
	IsSupplementedBy("IsSupplementedBy"),
	/**
	 * Constant ISCONTINUEDBY
	 */
	IsContinuedBy("IsContinuedBy"),
	/**
	 * Constant CONTINUES
	 */
	Continues("Continues"),
	/**
	 * Constant ISNEWVERSIONOF
	 */
	IsNewVersionOf("IsNewVersionOf"),
	/**
	 * Constant ISPREVIOUSVERSIONOF
	 */
	IsPreviousVersionOf("IsPreviousVersionOf"),
	/**
	 * Constant ISPARTOF
	 */
	IsPartOf("IsPartOf"),
	/**
	 * Constant HASPART
	 */
	HasPart("HasPart"),
	/**
	 * Constant ISREFERENCEDBY
	 */
	IsReferencedBy("IsReferencedBy"),
	/**
	 * Constant REFERENCES
	 */
	References("References"),
	/**
	 * Constant ISDOCUMENTEDBY
	 */
	IsDocumentedBy("IsDocumentedBy"),
	/**
	 * Constant DOCUMENTS
	 */
	Documents("Documents"),
	/**
	 * Constant ISCOMPILEDBY
	 */
	IsCompiledBy("IsCompiledBy"),
	/**
	 * Constant COMPILES
	 */
	Compiles("Compiles"),
	/**
	 * Constant ISVARIANTFORMOF
	 */
	IsVariantFormOf("IsVariantFormOf"),
	/**
	 * Constant ISORIGINALFORMOF
	 */
	IsOriginalFormOf("IsOriginalFormOf"),
	/**
	 * Constant ISIDENTICALTO
	 */
	IsIdenticalTo("IsIdenticalTo"),
	/**
	 * Constant HASMETADATA
	 */
	HasMetadata("HasMetadata"),
	/**
	 * Constant ISMETADATAFOR
	 */
	IsMetadataFor("IsMetadataFor");

	/**
	 * Field value.
	 */
	private final String value;

	/**
	 * Field enumConstants.
	 */
	private static final Map<String, RelationType> ENUM_CONSTANTS = new HashMap<String, RelationType>();

	static {
		for (RelationType c : RelationType.values()) {
			RelationType.ENUM_CONSTANTS.put(c.value, c);
		}

	};

	private RelationType(final String value) {
		this.value = value;
	}

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 *            the value for the resource type
	 * @return the constant for this value
	 */
	public static RelationType fromValue(final String value) {
		RelationType c = RelationType.ENUM_CONSTANTS.get(value);
		if (c != null) {
			return c;
		}
		throw new IllegalArgumentException(value);
	}

	/**
	 * 
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