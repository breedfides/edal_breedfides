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
