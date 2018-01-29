/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
		this(identifier.getID());
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
