/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.io.Serializable;

/**
 * Represent a person.
 * 
 * @author lange
 * @author arendd
 */
public abstract class Person extends UntypedData implements Serializable, Comparable<UntypedData> {

	private static final long serialVersionUID = 8327505790776624810L;

	protected static final String UNKNOWN = "unknown";

	private final String addressLine;
	private final String country;
	private final String zip;

	/**
	 * Constructor for Person with specified values.
	 * 
	 * @param addressLine
	 *            address of the {@link Person}.
	 * @param zip
	 *            zip of the {@link Person}.
	 * @param country
	 *            country of the {@link Person}.
	 */
	public Person(final String addressLine, final String zip, final String country) {
		super();
		this.addressLine = addressLine;
		this.zip = zip;
		this.country = country;
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
		result = prime * result + ((addressLine == null) ? 0 : addressLine.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((zip == null) ? 0 : zip.hashCode());
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
		Person other = (Person) obj;
		if (addressLine == null) {
			if (other.addressLine != null)
				return false;
		} else if (!addressLine.equals(other.addressLine))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Person) {

			Person person = (Person) datatype;

			if (addressLine.compareTo(person.addressLine) == 0) {
				if (zip.compareTo(person.zip) == 0) {
					if (country.compareTo(person.country) == 0) {
						return super.compareTo(person);
					} else {
						return country.compareTo(person.country);
					}
				} else {
					return zip.compareTo(person.zip);
				}
			} else {
				return addressLine.compareTo(person.addressLine);
			}
		} else {
			return super.compareTo(datatype);

		}

	}

	/**
	 * Format address strings in person object set empty strings to n/a
	 * 
	 * @param string
	 *            the to format
	 * @return formated address string
	 */
	protected String formatEmptyString(final String string) {

		return string.length() > 0 ? string : Person.UNKNOWN;
	}

	/**
	 * Getter for the field <code>adress_line</code>.
	 * 
	 * @return the address line
	 */
	public String getAddressLine() {
		return this.addressLine;
	}

	/**
	 * Getter for the field <code>country</code>.
	 * 
	 * @return the country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Getter for the field <code>zip</code>.
	 * 
	 * @return the zip
	 */
	public String getZip() {
		return this.zip;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.addressLine.length() + this.zip.length() + this.country.length() > 0) {
			return this.formatEmptyString(this.addressLine) + ", " + this.formatEmptyString(this.zip) + ", "
					+ this.formatEmptyString(this.country);
		} else {
			return Person.UNKNOWN;
		}
	}

}
