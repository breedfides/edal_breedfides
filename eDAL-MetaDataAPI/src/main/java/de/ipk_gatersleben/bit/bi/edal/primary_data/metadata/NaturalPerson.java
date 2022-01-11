/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

/**
 * Represent a natural person.
 * 
 * @author lange
 * @author arendd
 */
public class NaturalPerson extends Person {

	private static final long serialVersionUID = 6858824330148276157L;
	private String givenName;
	private String sureName;
	private ORCID orcid;

	/**
	 * Constructor for a natural person with specified values.
	 * 
	 * @param givenName
	 *            given name of the {@link Person}.
	 * @param sureName
	 *            sure name of the {@link Person}.
	 * @param addressLine
	 *            address of the {@link Person}.
	 * @param zip
	 *            zip of the {@link Person}.
	 * @param country
	 *            country of the {@link Person}.
	 */
	public NaturalPerson(final String givenName, final String sureName, final String addressLine, final String zip,
			final String country) {
		super(addressLine, zip, country);
		this.givenName = givenName;
		this.sureName = sureName;
	}

	/**
	 * Constructor for a natural person with specified values.
	 * 
	 * @param givenName
	 *            given name of the {@link Person}.
	 * @param sureName
	 *            sure name of the {@link Person}.
	 * @param addressLine
	 *            address of the {@link Person}.
	 * @param zip
	 *            zip of the {@link Person}.
	 * @param country
	 *            country of the {@link Person}.
	 * @param orcid
	 *            the ORCID of the person.
	 */
	public NaturalPerson(final String givenName, final String sureName, final String addressLine, final String zip,
			final String country, final ORCID orcid) {
		super(addressLine, zip, country);
		this.givenName = givenName;
		this.sureName = sureName;
		this.orcid = orcid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result + ((sureName == null) ? 0 : sureName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NaturalPerson other = (NaturalPerson) obj;
		if (givenName == null) {
			if (other.givenName != null)
				return false;
		} else if (!givenName.equals(other.givenName))
			return false;
		if (sureName == null) {
			if (other.sureName != null)
				return false;
		} else if (!sureName.equals(other.sureName))
			return false;

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof NaturalPerson) {

			NaturalPerson naturalPerson = (NaturalPerson) datatype;

			if (givenName.compareTo(naturalPerson.givenName) == 0) {
				if (sureName.compareTo(naturalPerson.sureName) == 0) {
					return super.compareTo(naturalPerson);
				} else {
					return sureName.compareTo(naturalPerson.sureName);
				}
			} else {
				return givenName.compareTo(naturalPerson.givenName);
			}
		} else {
			return -1;
		}
	}

	/**
	 * Getter for the field <code>sure_name</code>.
	 * 
	 * @return the sure name
	 */
	@Column(columnDefinition = "varchar(4000)")	
	public String getSureName() {
		return this.sureName;
	}

	/**
	 * Getter for the field <code>given_name</code>.
	 * 
	 * @return the given name
	 */
	@Column(columnDefinition = "varchar(4000)")
	public String getGivenName() {
		return this.givenName;
	}

	/**
	 * @param sureName
	 *            the sureName to set
	 */
	public void setSureName(String sureName) {
		this.sureName = sureName;
	}

	/**
	 * @param givenName
	 *            the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.getGivenName().length() + this.getSureName().length() + this.getAddressLine().length()
				+ this.getZip().length() + this.getCountry().length() > 0) {

			if (this.getOrcid() != null) {
				return this.formatEmptyString(this.getGivenName()) + " " + this.formatEmptyString(this.getSureName())
						+ ", " + this.formatEmptyString(this.getAddressLine()) + ", "
						+ this.formatEmptyString(this.getZip()) + ", " + this.formatEmptyString(
								this.getCountry() + ", " + this.formatEmptyString(this.getOrcid().toString()));
			} else {
				return this.formatEmptyString(this.getGivenName()) + " " + this.formatEmptyString(this.getSureName())
						+ ", " + this.formatEmptyString(this.getAddressLine()) + ", "
						+ this.formatEmptyString(this.getZip()) + ", " + this.formatEmptyString(this.getCountry());
			}
		} else {
			return Person.UNKNOWN;
		}
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orcid")
	public ORCID getOrcid() {
		return orcid;
	}

	public void setOrcid(ORCID orcid) {
		this.orcid = orcid;
	}

}
