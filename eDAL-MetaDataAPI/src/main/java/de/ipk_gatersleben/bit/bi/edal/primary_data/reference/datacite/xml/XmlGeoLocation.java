/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
