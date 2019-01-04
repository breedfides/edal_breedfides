/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
