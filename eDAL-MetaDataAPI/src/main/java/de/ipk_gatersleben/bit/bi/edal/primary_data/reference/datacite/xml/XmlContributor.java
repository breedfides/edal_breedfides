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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.ContributorType;

/**
 * The institution or person responsible for collecting, creating, or otherwise
 * contributing to the development of the dataset. The personal name format
 * should be: Family, Given.
 * 
 * @author arendd
 */
@XmlType(name = "contributor", propOrder = { "contributorName", "givenName", "familyName", "nameIdentifier",
		"affiliation" })
public class XmlContributor implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field contributorType.
	 */
	private ContributorType contributorType;

	/**
	 * Field contributorName.
	 */
	private String contributorName;

	/**
	 * Field nameIdentifier.
	 */
	private XmlNameIdentifier nameIdentifier;

	/** new since kernel-4 */

	private String givenName;
	private String familyName;
	private String affiliation;

	/**
	 * Default constructor
	 */
	public XmlContributor() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlContributor} with the specified
	 * contributorName.
	 * 
	 * @param contributorName
	 *            the contributorName to set.
	 */
	public XmlContributor(String contributorName) {
		this();
		setContributorName(contributorName);
	}

	/**
	 * Constructor to create a {@link XmlContributor} with the specified
	 * contributorName and contributorType.
	 * 
	 * @param contributorName
	 *            the contributorName to set.
	 * @param contributorType
	 *            the contributorType to set.
	 */
	public XmlContributor(String contributorName, ContributorType contributorType) {
		this(contributorName);
		setContributorType(contributorType);
	}

	/**
	 * Copy-Constructor to create a {@link XmlContributor} from a {@link Person}
	 * object.
	 * 
	 * @param person
	 *            to convert into a {@link XmlContributor}.
	 */

	public XmlContributor(Person person) {

		this();
		if (person instanceof NaturalPerson) {

			NaturalPerson np = (NaturalPerson) person;

			setContributorName(np.getSureName() + ", " + np.getGivenName());
			setContributorType(ContributorType.Researcher);
			setGivenName(np.getGivenName());
			setFamilyName(np.getSureName());
			setAffiliation(np.getAddressLine() + ", " + np.getZip() + ", " + np.getCountry());
			
			if (np.getOrcid() != null) {
				setNameIdentifier(new XmlNameIdentifier(np.getOrcid().getOrcid(), "ORCID", "http://orcid.org/"));
			}

		} else if (person instanceof LegalPerson) {

			LegalPerson lp = (LegalPerson) person;

			setContributorName(lp.getLegalName());

			setContributorType(ContributorType.ResearchGroup);
			setAffiliation(lp.getAddressLine() + ", " + lp.getZip() + ", " + lp.getCountry());

		}
	}

	/**
	 * Returns the value of field 'contributorName'.
	 * 
	 * @return the value of field 'contributorName'.
	 */
	@XmlElement(required = true)
	public String getContributorName() {
		return this.contributorName;
	}

	/**
	 * Returns the value of field 'contributorType'.
	 * 
	 * @return the value of field 'contributorType'.
	 */
	@XmlAttribute
	public ContributorType getContributorType() {
		return this.contributorType;
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
	 * Sets the value of field 'contributorName'.
	 * 
	 * @param contributorName
	 *            the value of field 'contributorName'.
	 */
	public void setContributorName(final String contributorName) {
		this.contributorName = contributorName;
	}

	/**
	 * Sets the value of field 'contributorType'.
	 * 
	 * @param contributorType
	 *            the value of field 'contributorType'.
	 */
	public void setContributorType(final ContributorType contributorType) {
		this.contributorType = contributorType;
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