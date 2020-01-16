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

import javax.mail.internet.InternetAddress;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class to persist root user with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@Table(name = "ROOT")
public class RootImplementation {

	private int id;
	private String name;
	private String type;
	private String address;
	private String uuid;
	private boolean validated;

	/**
	 * Default constructor for {@link RootImplementation} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected RootImplementation() {
	}

	/**
	 * Constructor for {@link RootImplementation}.
	 * 
	 * @param name
	 *            principal name.
	 * @param type
	 *            principal type.
	 */
	protected RootImplementation(String name, String type) {
		this.name = name;
		this.type = type;
		this.validated = false;
	}

	/**
	 * Constructor for {@link RootImplementation}.
	 * 
	 * @param name
	 *            principal name.
	 * @param type
	 *            principal type.
	 * @param address
	 *            the email address of the root user.
	 */
	protected RootImplementation(String name, String type, InternetAddress address) {
		this(name, type);
		this.address = address.getAddress();
	}

	protected RootImplementation(String name, String type, InternetAddress address, String uuid) {
		this(name, type, address);
		this.uuid = uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RootImplementation [id=" + id + ", name=" + name + ", type=" + type + ", address=" + address + ", validated=" + validated + "]";
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Getter for the field <code>id</code>.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	protected int getId() {
		return id;
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
	 * Getter for the field <code>type</code>.
	 * 
	 * @return the type
	 */
	protected String getType() {
		return type;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * Setter for the field <code>name</code>.
	 * 
	 * @param name
	 *            the name to set
	 */
	protected void setName(String name) {
		this.name = name;
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
	 * @return the validated
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * @param validated
	 *            the validated to set
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}