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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;

/**
 * The main researchers involved working on the data, or the authors of the
 * publication in priority order. May be a corporate/institutional or personal
 * name. Format: Family, Given.
 * 
 * @author arendd
 */
@XmlType(name = "creator", propOrder = { "creatorName", "givenName", "familyName", "nameIdentifier", "affiliation" })
public class XmlCreator implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field creatorName.
	 */
	private String creatorName;

	/** new since kernel-4 */

	private String givenName;
	private String familyName;
	private String affiliation;

	/**
	 * Field nameIdentifier.
	 */
	private XmlNameIdentifier nameIdentifier;

	/**
	 * Default constructor
	 */
	public XmlCreator() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlCreator} with the specified creatorName.
	 * 
	 * @param creatorName
	 *            the creatorName to set.
	 */
	public XmlCreator(String creatorName) {
		this();
		setCreatorName(creatorName);
	}

	/**
	 * Constructor to create a {@link XmlCreator} with the specified creatorName and
	 * nameIdentifier.
	 * 
	 * @param creatorName
	 *            the creatorName to set.
	 * @param nameIdentifier
	 *            the nameIdentifier to set.
	 */
	public XmlCreator(String creatorName, XmlNameIdentifier nameIdentifier) {
		this(creatorName);
		setNameIdentifier(nameIdentifier);
	}

	/**
	 * Copy-Constructor to create a {@link XmlCreator} from a {@link Person} object.
	 * 
	 * @param person
	 *            to convert into a {@link XmlCreator}.
	 */
	public XmlCreator(Person person) {
		this();

		if (person instanceof NaturalPerson) {

			NaturalPerson np = (NaturalPerson) person;

			setCreatorName(np.getSureName() + ", " + np.getGivenName());
			setGivenName(np.getGivenName());
			setFamilyName(np.getSureName());
			setAffiliation(np.getAddressLine() + ", " + np.getZip() + ", " + np.getCountry());

			if (np.getOrcid() != null) {
				setNameIdentifier(new XmlNameIdentifier(np.getOrcid().getOrcid(), "ORCID", "http://orcid.org/"));
			}

		} else if (person instanceof LegalPerson) {

			LegalPerson lp = (LegalPerson) person;

			setCreatorName(lp.getLegalName());
			setAffiliation(lp.getAddressLine() + ", " + lp.getZip() + ", " + lp.getCountry());
		}
	}

	/**
	 * Returns the value of field 'creatorName'.
	 * 
	 * @return the value of field 'creatorName'.
	 */
	@XmlElement(required = true)
	public String getCreatorName() {
		return this.creatorName;
	}

	/**
	 * Returns the value of field 'nameIdentifier'.
	 * 
	 * @return the value of field 'nameIdentifier'.
	 */
	@XmlElement
	public XmlNameIdentifier getNameIdentifier() {
		return this.nameIdentifier;
	}

	/**
	 * Sets the value of field 'creatorName'.
	 * 
	 * @param creatorName
	 *            the value of field 'creatorName'.
	 */
	public void setCreatorName(final String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * Sets the value of field 'nameIdentifier'.
	 * 
	 * @param nameIdentifier
	 *            the value of field 'nameIdentifier'.
	 */
	public void setNameIdentifier(final XmlNameIdentifier nameIdentifier) {
		this.nameIdentifier = nameIdentifier;
	}

	@XmlElement
	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	@XmlElement
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@XmlElement
	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

}