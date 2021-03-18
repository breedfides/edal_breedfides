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
