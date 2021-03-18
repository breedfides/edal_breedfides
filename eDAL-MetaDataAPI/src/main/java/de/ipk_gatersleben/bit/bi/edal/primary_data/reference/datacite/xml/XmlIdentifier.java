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