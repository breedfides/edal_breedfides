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

/**
 * Unique identifier, which is ensures to be global uniqueness defined at <a
 * href="http://www.iso.ch/cate/d2229.html" target="_blank">ISO/IEC
 * 11578:1996</a>.
 * 
 * @author lange
 * @author arendd
 */
public class Identifier extends UntypedData {

	public static final String UNKNOWN_ID = "Unknown_ID";
	
	/**
	 * the generated serial ID
	 */
	private static final long serialVersionUID = 3380495549321758121L;
	private final String id;

	/**
	 * Constructor for Identifier.
	 * 
	 * Set the id to "Unkown_ID".
	 */
	public Identifier() {
		this.id = UNKNOWN_ID;
	}

	/**
	 * Constructor for Identifier with specified id.
	 * 
	 * @param id
	 *            the id to set.
	 */
	public Identifier(String id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Identifier) {

			Identifier identifier = (Identifier) datatype;

			if (id.compareTo(identifier.id) == 0) {
				return super.compareTo(identifier);
			} else {
				return id.compareTo(identifier.id);
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * Getter for the id.
	 * 
	 * @return the id.
	 */
	public final String getID() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
