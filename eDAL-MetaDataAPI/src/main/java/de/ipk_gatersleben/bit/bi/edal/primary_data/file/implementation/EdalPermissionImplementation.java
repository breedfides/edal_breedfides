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

import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.EdalClasses;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;

/**
 * Implementation of {@link EdalPermission} for persist with <em>HIBERNATE</em>
 * 
 * @author arendd
 */

@Entity
@Table(name = "EDALPERMISSIONS", indexes = { @Index(name = "index_internId", columnList = "internId") })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "EdalPermission")
public class EdalPermissionImplementation extends EdalPermission {

	private static final long serialVersionUID = -6598537001401120856L;

	private int id;
	private String internId;
	private Long internVersion;
	private EdalClasses internClass;
	private Methods internMethod;
	private PrincipalImplementation principal;

	/**
	 * Default constructor for {@link EdalPermissionImplementation} is necessary
	 * for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected EdalPermissionImplementation() {
	}

	/**
	 * Constructor for EdalPermissionImplementation.
	 * 
	 * @param principal
	 *            a {@link PrincipalImplementation} object.
	 * @param primaryDataEntityID
	 *            a {@link String} object.
	 * @param version
	 *            a {@link Long} object.
	 * @param actionClass
	 *            a {@link EdalClasses} object.
	 * @param actionMethod
	 *            a {@link Methods} object.
	 */
	protected EdalPermissionImplementation(PrincipalImplementation principal,
			String primaryDataEntityID, Long version, EdalClasses actionClass,
			Methods actionMethod) {

		this.setInternId(primaryDataEntityID);
		this.setInternVersion(version);
		this.setInternClass(actionClass);
		this.setInternMethod(actionMethod);
		this.setPrincipal(principal);
	}

	/**
	 * Convert the private {@link EdalPermissionImplementation} into an public
	 * {@link EdalPermission} object.
	 * 
	 * @return EDALPermission
	 */
	@Transient
	protected EdalPermission toEdalPermission() {

		Class<? extends PrimaryDataEntity> clazz = null;
		if (this.getInternClass().equals(EdalClasses.PrimaryDataEntity)) {
			clazz = PrimaryDataEntity.class;
		} else if (this.getInternClass().equals(
				EdalClasses.PrimaryDataDirectory)) {
			clazz = PrimaryDataDirectory.class;
		} else if (this.getInternClass().equals(EdalClasses.PrimaryDataFile)) {
			clazz = PrimaryDataFile.class;
		}

		Method method = null;
		try {
			method = this.getInternMethod().getImplClass(clazz);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		EdalPermission edalPermission = new EdalPermission(this.getInternId(),
				this.getInternVersion(), clazz, method);

		return edalPermission;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof EdalPermissionImplementation)) {
			return false;
		}
		EdalPermissionImplementation other = (EdalPermissionImplementation) obj;
		if (internClass != other.internClass) {
			return false;
		}
		if (internId == null) {
			if (other.internId != null) {
				return false;
			}
		} else if (!internId.equals(other.internId)) {
			return false;
		}
		if (internMethod != other.internMethod) {
			return false;
		}
		if (internVersion == null) {
			if (other.internVersion != null) {
				return false;
			}
		} else if (!internVersion.equals(other.internVersion)) {
			return false;
		}
		if (principal == null) {
			if (other.principal != null) {
				return false;
			}
		} else if (!principal.equals(other.principal)) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for the field <code>id</code>.
	 * <p>
	 * Value for id is generated by <em>HIBERNATE</em>
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	protected int getId() {
		return id;
	}

	/**
	 * Getter for the field <code>internClass</code>.
	 * 
	 * @return the internClass
	 */
	@Enumerated(EnumType.ORDINAL)
	private EdalClasses getInternClass() {
		return internClass;
	}

	/**
	 * Getter for the field <code>internId<code/>.
	 * <p>
	 * <em> HIBERNATE : constant length cause it is an {@link UUID}</em>
	 * 
	 * @return the internId
	 */
	@Column(columnDefinition = "char(40)")
	private String getInternId() {
		return internId;
	}

	/**
	 * Setter for the field <code>internMethod</code>.
	 * 
	 * @return the internMethod
	 */
	@Enumerated(EnumType.ORDINAL)
	private Methods getInternMethod() {
		return internMethod;
	}

	/**
	 * Getter for the field <code>internVersion</code>.
	 * 
	 * @return the internVersion
	 */
	private Long getInternVersion() {
		return internVersion;
	}

	/**
	 * Getter for the field <code>principal</code>.
	 * 
	 * @return the principal
	 */
	@ManyToOne
	protected PrincipalImplementation getPrincipal() {
		return principal;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((internClass == null) ? 0 : internClass.hashCode());
		result = prime * result
				+ ((internId == null) ? 0 : internId.hashCode());
		result = prime * result
				+ ((internMethod == null) ? 0 : internMethod.hashCode());
		result = prime * result
				+ ((internVersion == null) ? 0 : internVersion.hashCode());
		result = prime * result
				+ ((principal == null) ? 0 : principal.hashCode());
		return result;
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
	 * Setter for the field <code>internClass</code>.
	 * 
	 * @param internClass
	 *            the internClass to set
	 */
	private void setInternClass(EdalClasses internClass) {
		this.internClass = internClass;
	}

	/**
	 * Setter for the field <code>internId</code>.
	 * 
	 * @param internId
	 *            the internId to set
	 */
	private void setInternId(String internId) {
		this.internId = internId;
	}

	/**
	 * Setter for the field <code>internMethod</code>.
	 * 
	 * @param internMethod
	 *            the internMethod to set
	 */
	private void setInternMethod(Methods internMethod) {
		this.internMethod = internMethod;
	}

	/**
	 * Setter for the field <code>internVersion</code>.
	 * 
	 * @param internVersion
	 *            the internVersion to set
	 */
	private void setInternVersion(Long internVersion) {
		this.internVersion = internVersion;
	}

	/**
	 * Setter for the field <code>principal</code>.
	 * 
	 * @param principal
	 *            the principal to set
	 */
	private void setPrincipal(PrincipalImplementation principal) {
		this.principal = principal;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "EDALPermissionImplementation [internId=" + this.getInternId()
				+ ", internVersion=" + this.getInternVersion()
				+ ", internClass=" + this.getInternClass() + ", internMethod="
				+ this.getInternMethod() + ", principal=" + this.getPrincipal()
				+ "]";
	}
}