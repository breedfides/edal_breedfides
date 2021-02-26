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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;

/**
 * An identifier other than the primary identifier applied to the resource being
 * registered. This may be any alphanumeric string which is unique within its
 * domain of issue. The format is open.
 * 
 * @author arendd
 */
@XmlType(name = "alternativeIdentifier")
public class XmlAlternateIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field identifier.
	 */
	private String identifier = "";

	/**
	 * Field alternateIdentifierType.
	 */
	private String alternateIdentifierType;

	/**
	 * Default constructor
	 */
	public XmlAlternateIdentifier() {
		super();
		setIdentifier("");
	}

	/**
	 * Constructor to create a {@link XmlAlternateIdentifier} with the specified
	 * identifier.
	 * 
	 * @param identifier
	 *            the identifier to set.
	 */
	public XmlAlternateIdentifier(String identifier) {
		this();
		setIdentifier(identifier);
	}

	/**
	 * Constructor to create a {@link XmlAlternateIdentifier} with the specified
	 * identifier and identifierType.
	 * 
	 * @param identifier
	 *            the identifier to set.
	 * @param alternateIdentifierType
	 *            the identifierType to set.
	 */
	public XmlAlternateIdentifier(String identifier,
			String alternateIdentifierType) {
		this(identifier);
		setAlternateIdentifierType(alternateIdentifierType);
	}

	/**
	 * Copy-Constructor to create a {@link XmlAlternateIdentifier} from a
	 * {@link Identifier} object.
	 * 
	 * @param identifier
	 *            to convert into a {@link XmlAlternateIdentifier}.
	 */
	public XmlAlternateIdentifier(Identifier identifier) {
		this(identifier.getIdentifier());
		setAlternateIdentifierType("test");
	}

	/**
	 * Returns the value of field 'alternateIdentifierType'.
	 * 
	 * @return the value of field 'alternateIdentifierType'.
	 */
	@XmlAttribute
	public String getAlternateIdentifierType() {
		return this.alternateIdentifierType;
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
	 * Sets the value of field 'alternateIdentifierType'.
	 * 
	 * @param alternateIdentifierType
	 *            the value of field 'alternateIdentifierType'.
	 */
	public void setAlternateIdentifierType(final String alternateIdentifierType) {
		this.alternateIdentifierType = alternateIdentifierType;
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

}
