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
 * Subject, keywords, classification codes, or key phrases describing the
 * resource.
 * 
 * @author arendd
 */

@XmlType(name = "subject")
public class XmlSubject implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field value
	 */
	private String value = "";

	/**
	 * Field subjectScheme.
	 */
	private String subjectScheme;

	/**
	 * Default constructor
	 */
	public XmlSubject() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlSubject} with the specified subject.
	 * 
	 * @param value
	 *            the subject to set.
	 */
	public XmlSubject(String value) {
		this();
		setValue(value);
	}

	/**
	 * Constructor to create a {@link XmlSubject} with the specified subject and
	 * subjectSchema.
	 * 
	 * @param value
	 *            the subject to set.
	 * @param subjectScheme
	 *            the subjectSchema to set.
	 */
	public XmlSubject(String value, String subjectScheme) {
		this(value);
		setSubjectScheme(subjectScheme);
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
	 * Returns the value of field 'subjectScheme'.
	 * 
	 * @return the value of field 'subjectScheme'.
	 */
	@XmlAttribute
	public String getSubjectScheme() {
		return this.subjectScheme;
	}

	/**
	 * Sets the value of field 'value'. The field 'value' has the following
	 * description: internal content storage
	 * 
	 * @param value
	 *            the value of field 'content'.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets the value of field 'subjectScheme'.
	 * 
	 * @param subjectScheme
	 *            the value of field 'subjectScheme'.
	 */
	public void setSubjectScheme(final String subjectScheme) {
		this.subjectScheme = subjectScheme;
	}
}