/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.Serializable;
import java.security.Principal;

/**
 * This class implements the <code>Principal</code> interface to represents a
 * Sample Principal.
 * 
 * @author arendd
 */
public class SamplePrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	/**
	 * Create a SamplePrincipal with a name.
	 * 
	 * @param name
	 *            the name for this principal.
	 * 
	 * @exception NullPointerException
	 *                if the name is <code>null</code>.
	 */
	public SamplePrincipal(String name) {
		if (name == null) {
			throw new NullPointerException("no null name allowed");
		}
		this.name = name;
	}

	/**
	 * Return the name for this {@link SamplePrincipal}.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return a string representation of this {@link SamplePrincipal}.
	 * 
	 * @return a string representation.
	 */
	public String toString() {
		return ("SamplePrincipal: " + name);
	}

	/**
	 * Compares the specified Object with this {@link SamplePrincipal} for
	 * equality.
	 * 
	 * @param object
	 *            Object to be compared.
	 * @return true if the specified Object is equal equal to this
	 *         {@link SamplePrincipal}.
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (this == object) {
			return true;
		}
		if (!(object instanceof SamplePrincipal)) {
			return false;
		}
		SamplePrincipal that = (SamplePrincipal) object;

		if (this.getName().equals(that.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * Return a hash code for this {@link SamplePrincipal}.
	 * 
	 * @return a hash code.
	 */
	public int hashCode() {
		return name.hashCode();
	}
}
