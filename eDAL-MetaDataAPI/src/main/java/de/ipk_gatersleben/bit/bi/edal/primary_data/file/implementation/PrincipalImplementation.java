/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Class to persist user principals with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@Table(name = "PRINCIPALS")
public class PrincipalImplementation implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String type;

	private Set<EdalPermissionImplementation> permissions = new HashSet<EdalPermissionImplementation>();

	/**
	 * Default constructor for {@link PrincipalImplementation} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected PrincipalImplementation() {

	}

	/**
	 * Constructor for PrincipalImplementation.
	 * 
	 * @param name
	 *            a {@link String} object.
	 * @param type
	 *            a {@link String} object.
	 */
	protected PrincipalImplementation(String name, String type) {
		this.name = name;
		this.type = type;
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
	public String getName() {
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

	/**
	 * Getter for the field <code>type</code>.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter for the field <code>type</code>.
	 * 
	 * @param type
	 *            the type to set
	 */
	protected void setType(String type) {
		this.type = type;
	}

	/**
	 * Setter for the field <code>permissions</code>.
	 * 
	 * @param permissions
	 *            the permissions to set
	 */
	protected void setPermissions(Set<EdalPermissionImplementation> permissions) {
		this.permissions = permissions;
	}

	/**
	 * Getter for the field <code>permissions</code>.
	 * 
	 * @return the permissions
	 */
	@OneToMany(targetEntity = EdalPermissionImplementation.class, mappedBy = "principal")
	protected Set<EdalPermissionImplementation> getPermissions() {
		return permissions;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getName() == null) ? 0 : this.getName().hashCode());
		result = prime * result
				+ ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
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
		if (!(obj instanceof PrincipalImplementation)) {
			return false;
		}
		PrincipalImplementation other = (PrincipalImplementation) obj;
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		if (this.getType() == null) {
			if (other.getType() != null) {
				return false;
			}
		} else if (!this.getType().equals(other.getType())) {
			return false;
		}
		return true;
	}

	@Transient
	public Principal toPrincipal() throws EdalException {

		List<Class<? extends Principal>> list = DataManager
				.getSupportedPrincipals();
		for (Class<? extends Principal> clazz : list) {
			if (this.type.equals(clazz.getSimpleName())) {
				Constructor<? extends Principal> constructor;
				Principal principal = null;
				try {
					constructor = clazz.getConstructor(String.class);
					principal = constructor.newInstance(this.name);
				} catch (Exception e) {
					throw new EdalException(
							"Can not to convert to public principal type "
									+ this.type + " : " + e.getMessage());
				}
				return principal;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "PrincipalImplementation [name=" + this.getName() + ", type="
				+ this.getType() + "]";
	}
}