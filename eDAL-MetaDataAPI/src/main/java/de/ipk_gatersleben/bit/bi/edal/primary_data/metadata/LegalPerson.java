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
/**
 * 
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * @author lange
 */
public class LegalPerson extends Person {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((legalName == null) ? 0 : legalName.hashCode());
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
		LegalPerson other = (LegalPerson) obj;
		if (legalName == null) {
			if (other.legalName != null)
				return false;
		} else if (!legalName.equals(other.legalName))
			return false;
		return true;
	}

	private static final long serialVersionUID = 226574498264820331L;
	private String legalName;

	/**
	 * Constructor for a natural person with specified values.
	 * 
	 * @param legalName
	 *            legal name of the {@link Person}.
	 * @param addressLine
	 *            address of the {@link Person}.
	 * @param zip
	 *            zip of the {@link Person}.
	 * @param country
	 *            country of the {@link Person}.
	 */
	public LegalPerson(final String legalName, final String addressLine,
			final String zip, final String country) {
		super(addressLine, zip, country);
		this.legalName = legalName;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof LegalPerson) {

			LegalPerson legalPerson = (LegalPerson) datatype;

			if (legalName.compareTo(legalPerson.legalName) == 0) {
				return super.compareTo(legalPerson);
			} else {
				return legalName.compareTo(legalPerson.legalName);
			}
		} else {
			return -1;
		}
	}

	/**
	 * Getter for the field <code>legalName</code>.
	 * 
	 * @return the legalName
	 */
	public String getLegalName() {
		return this.legalName;
	}

	/**
	 * Setter for the field <code>legalName</code>.
	 * 
	 * @param legalName
	 *            the legalName to set
	 */
	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.legalName.length() + this.getAddressLine().length()
				+ this.getZip().length() + this.getCountry().length() > 0) {
			return this.formatEmptyString(this.legalName) + ", "
					+ this.formatEmptyString(this.getAddressLine()) + ", "
					+ this.formatEmptyString(this.getZip()) + ", "
					+ this.formatEmptyString(this.getCountry());
		} else {
			return Person.UNKNOWN;
		}
	}
}