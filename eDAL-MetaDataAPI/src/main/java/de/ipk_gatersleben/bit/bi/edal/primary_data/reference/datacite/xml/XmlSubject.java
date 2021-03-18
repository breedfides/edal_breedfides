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