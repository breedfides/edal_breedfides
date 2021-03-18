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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.security.Principal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Class to persist all supported user principals with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
public class SupportedPrincipals {

	private int id;
	private String name;

	/**
	 * Default constructor for {@link SupportedPrincipals} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected SupportedPrincipals() {

	}

	/**
	 * Constructor for SupportedPrincipals.
	 * 
	 * @param name
	 *            a {@link String} object.
	 */
	protected SupportedPrincipals(String name) {
		this.name = name;
	}

	/**
	 * Constructor for SupportedPrincipals.
	 * 
	 * @param principal
	 *            the {@link Principal} to support
	 */
	protected SupportedPrincipals(Class<? extends Principal> principal) {

		this.name = principal.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SupportedPrincipals)) {
			return false;
		}
		SupportedPrincipals other = (SupportedPrincipals) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SupportedPrincipals [name=" + name + "]";
	}

	/**
	 * Getter for the field <code>id</code>.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	protected int getId() {
		return this.id;
	}

	/**
	 * Setter for the field <code>id</code>.
	 * 
	 * @param id
	 *            the id to set
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter for the field <code>name</code>.
	 * 
	 * @return the name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * Setter for the field <code>name</code>.
	 * 
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

}
