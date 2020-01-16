/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "rights")
public class XmlRights implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value = "";
	
	private String rightsURI;

	/**
	 * Constructor to create a {@link XmlRights} with the specified right.
	 * 
	 * @param value
	 *            the right to set.
	 */
	public XmlRights(String value) {
		this();
		setValue(value);

	}

	public XmlRights() {
	}

	/**
	 * @return the rightsURI
	 */
	@XmlAttribute
	public String getRightsURI() {
		return rightsURI;
	}

	/**
	 * @param rightsURI
	 *            the rightsURI to set
	 */
	public void setRightsURI(String rightsURI) {
		this.rightsURI = rightsURI;
	}

	/**
	 * @return the value
	 */
	@XmlValue
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
