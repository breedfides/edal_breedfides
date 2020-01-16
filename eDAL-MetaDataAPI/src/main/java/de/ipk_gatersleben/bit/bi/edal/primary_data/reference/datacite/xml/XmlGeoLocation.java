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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "geoLocation")
public class XmlGeoLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	private double geoLocationPoint;
	private double geoLocationBox;
	private String geoLocationPlace;

	public XmlGeoLocation() {
		super();
	}

	/**
	 * @return the geoLocationPoint
	 */
	@XmlElement
	public double getGeoLocationPoint() {
		return geoLocationPoint;
	}

	/**
	 * @param geoLocationPoint
	 *            the geoLocationPoint to set
	 */
	public void setGeoLocationPoint(double geoLocationPoint) {
		this.geoLocationPoint = geoLocationPoint;
	}

	/**
	 * @return the geoLocationBox
	 */
	@XmlElement
	public double getGeoLocationBox() {
		return geoLocationBox;
	}

	/**
	 * @param geoLocationBox
	 *            the geoLocationBox to set
	 */
	public void setGeoLocationBox(double geoLocationBox) {
		this.geoLocationBox = geoLocationBox;
	}

	/**
	 * @return the geoLocationPlace
	 */
	@XmlElement
	public String getGeoLocationPlace() {
		return geoLocationPlace;
	}

	/**
	 * @param geoLocationPlace
	 *            the geoLocationPlace to set
	 */
	public void setGeoLocationPlace(String geoLocationPlace) {
		this.geoLocationPlace = geoLocationPlace;
	}
}
