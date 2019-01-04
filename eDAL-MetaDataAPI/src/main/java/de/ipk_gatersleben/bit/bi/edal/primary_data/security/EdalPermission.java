/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.security;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Permission;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;

/**
 * Definition of an {@link EdalPermission}.
 * 
 * Consists of PrimaryDataEntity ID, PrimaryDataEntitiy Version, ActionClass,
 * ActionMethod (defined for a Principal (principalType/principalName pair).
 * 
 * @author arendd
 */
public class EdalPermission extends Permission implements Serializable {

	private static final long serialVersionUID = -8475468902103759973L;
	/**
	 * The {@link java.util.UUID} of the {@link PrimaryDataEntity}.
	 */
	private String primaryDataEntityID;
	/**
	 * The {@link Method} of the action; See
	 * {@link de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods}.
	 */
	private Method actionMethod;
	/**
	 * The {@link Class} of the action method.
	 */
	private Class<? extends PrimaryDataEntity> actionClass;
	/**
	 * The revision number of the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 * of the {@link PrimaryDataEntity}.
	 */
	private Long version;

	/**
	 * Constructor for {@link EdalPermission} with specified parameter.
	 * 
	 * @param primaryDataEntityID
	 *            a {@link String} object.
	 * @param version
	 *            a {@link Long} object.
	 * @param actionClass
	 *            a {@link Class} object.
	 * @param actionMethod
	 *            a {@link Method} object.
	 */
	public EdalPermission(final String primaryDataEntityID, final Long version,
			final Class<? extends PrimaryDataEntity> actionClass,
			final Method actionMethod) {

		/**
		 * super.name will be ignored and no longer checked by
		 * implies()/equals()
		 */
		super("EDALPermission");
		this.primaryDataEntityID = primaryDataEntityID;
		/**
		 * if (PrimaryDataEntityID == null) { throw new
		 * NullPointerException("permission name may not be null."); }
		 */
		this.actionMethod = actionMethod;
		this.version = version;
		this.actionClass = actionClass;

	}

	/**
	 * Constructor for {@link EdalPermission}.
	 */
	public EdalPermission() {
		super("My EDALPermission");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Checks if two EDALPermissions are equal.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj.getClass() != EdalPermission.class) {
			return false;
		}

		final EdalPermission other = (EdalPermission) obj;

		return this.getActionMethod().equals(other.getActionMethod())
				&& this.getActionClass().equals(other.getActionClass())
				&& this.getVersion().equals(other.getVersion())
				&& this.getPrimaryDataEntityID().equals(
						other.getPrimaryDataEntityID());
	}

	/**
	 * Getter for the field <code>primaryDataEntityID</code>.
	 * 
	 * @return the primaryDataEntityID.
	 */
	public String getPrimaryDataEntityID() {
		return this.primaryDataEntityID;
	}

	/**
	 * Getter for the field <code>actionClass</code>.
	 * 
	 * @return the actionClass.
	 */
	public Class<? extends PrimaryDataEntity> getActionClass() {
		return this.actionClass;
	}

	/**
	 * Getter for the field <code>version</code>.
	 * 
	 * @return the version.
	 */
	public Long getVersion() {
		return this.version;
	}

	/**
	 * Getter for the field <code>actionMethod</code>.
	 * 
	 * @return the actionMethod
	 */
	public Method getActionMethod() {
		return this.actionMethod;
	}

	/** {@inheritDoc} */
	@Override
	public String getActions() {
		return this.actionMethod.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Returns the hash code for this {@link EdalPermission}.
	 * 
	 * @see java.security.Permission#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getClass().getName().hashCode() + this.getName().hashCode()
				+ this.getActionMethod().hashCode()
				+ this.getActionClass().hashCode()
				+ this.getVersion().hashCode();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Check if the permission is implied by this object.
	 */
	@Override
	public boolean implies(final Permission permission) {

		if (this.equals(permission)) {
			return true;
		}

		if (!this.getClass().equals(permission.getClass())) {
			return false;
		}

		final EdalPermission other = (EdalPermission) permission;

		if (!this.getPrimaryDataEntityID().equals(
				other.getPrimaryDataEntityID())) {
			return false;
		}
		if (!this.getActionMethod().equals(other.getActionMethod())) {
			return false;
		}
		if (!this.getActionClass().equals(other.getActionClass())) {
			return false;
		}
		if (!this.getVersion().equals(other.getVersion())) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Returns a {@link String} Representation of an {@link EdalPermission}.
	 * 
	 * @see java.security.Permission#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.getPrimaryDataEntityID() + " " + this.getVersion()
				+ " " + this.getActionClass().getSimpleName() + " "
				+ this.getActionMethod().getName() + ")";
	}

}
