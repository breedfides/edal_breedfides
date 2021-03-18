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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DescriptionType;

/**
 * Class Description.
 * 
 * @author arendd
 */
@XmlType(name = "description")
public class XmlDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field descriptionType.
	 */
	private DescriptionType descriptionType;

	/**
	 * internal content storage
	 */
	private String content = "";

	public XmlDescription() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlDescription} with the specified
	 * description.
	 * 
	 * @param content
	 *            the description to set.
	 */
	public XmlDescription(String content) {
		this();
		setContent(content);
	}

	/**
	 * Constructor to create a {@link XmlDescription} with the specified
	 * description and descriptionType.
	 * 
	 * @param content
	 *            the description to set.
	 * @param descriptionType
	 *            the descriptionType to set.
	 */
	public XmlDescription(String content, DescriptionType descriptionType) {
		this(content);
		setDescriptionType(descriptionType);
	}

	/**
	 * Returns the value of field 'content'. The field 'content' has the
	 * following description: internal content storage
	 * 
	 * @return the value of field 'Content'.
	 */
	@XmlValue
	public String getContent() {
		return this.content;
	}

	/**
	 * Returns the value of field 'descriptionType'.
	 * 
	 * @return the value of field 'DescriptionType'.
	 */
	@XmlAttribute
	public DescriptionType getDescriptionType() {
		return this.descriptionType;
	}

	/**
	 * Sets the value of field 'content'. The field 'content' has the following
	 * description: internal content storage
	 * 
	 * @param content
	 *            the value of field 'content'.
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * Sets the value of field 'descriptionType'.
	 * 
	 * @param descriptionType
	 *            the value of field 'descriptionType'.
	 */
	public void setDescriptionType(final DescriptionType descriptionType) {
		this.descriptionType = descriptionType;
	}

}
