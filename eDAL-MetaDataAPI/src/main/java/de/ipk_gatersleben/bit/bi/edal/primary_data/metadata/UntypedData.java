/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
