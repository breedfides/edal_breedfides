/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.io.Serializable;
import java.util.Map.Entry;


/**
 * Basic type to store meta data values.
 * 
 * This object is super class of all values, to be stored in {@link MetaData}
 * and its sub classes.
 * <p>
 * The {@link UntypedData} support String as data value
 * 
 * @author lange
 * @author arendd
 */
public class UntypedData implements Serializable, Comparable<UntypedData> {

	private static final long serialVersionUID = -3498512898537693337L;

	public static final String EMPTY = "none";

	public static final String ALL_RIGHTS_RESERVED = "All rights reserved";

	protected String string;

	/**
	 * Constructor for UntypedData.
	 */
	public UntypedData() {
		this.string = EMPTY;
	}

	/**
	 * Constructor for UntypedData.
	 * 
	 * @param string
	 *            a {@link java.lang.String} object.
	 */
	public UntypedData(final String string) {
		this.string = string;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {
		return this.string.compareTo(datatype.string);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.string;
	}

	/**
	 * Return a String representation for the HTML pages of the HTTP-Listener
	 * 
	 * @return the HTML representation.
	 */
	public String toHTML() {

		for (Entry<EnumCCLicense, String> element : EnumCCLicense.enummap.entrySet()) {
			if (this.string.equals(element.getKey().getDescription())) {
				return "<a href=\"" + element.getValue() + "\">" + this.string + "</a>";
			}
		}

		return this.toString();
	}

	/**
	 * Getter for the field <code>_string</code>.
	 * 
	 * @return a {@link java.lang.String} object.
	 */
	public String getString() {
		return string;
	}

	/**
	 * Setter for the field <code>_string</code>.
	 * 
	 * @param string
	 *            a {@link java.lang.String} object.
	 */
	public void setString(String string) {
		this.string = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UntypedData other = (UntypedData) obj;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}

}
