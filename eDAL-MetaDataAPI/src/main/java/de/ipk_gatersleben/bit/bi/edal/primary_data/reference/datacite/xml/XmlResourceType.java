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

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.ResourceTypeGeneral;

/**
 * The type of a resource. You may enter an additional free text description.
 * 
 * @author arendd
 */
@XmlType(name = "resourceType")
public class XmlResourceType implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field resourceTypeGeneral.
	 */
	private ResourceTypeGeneral resourceTypeGeneral;

	/**
	 * internal content storage
	 */
	private String content = "";

	/**
	 * Default constructor
	 */
	public XmlResourceType() {
		super();
		setContent("");
	}

	/**
	 * Constructor to create a {@link XmlResourceType} with the specified
	 * resource.
	 * 
	 * @param content
	 *            the resource to set.
	 */
	public XmlResourceType(String content) {
		this();
		setContent(content);
	}

	/**
	 * Constructor to create a {@link XmlCreator} with the specified resource
	 * and resourceType.
	 * 
	 * @param content
	 *            the resource to set.
	 * @param resourceTypeGeneral
	 *            the resourceType to set.
	 */
	public XmlResourceType(String content, ResourceTypeGeneral resourceTypeGeneral) {
		this(content);
		setResourceTypeGeneral(resourceTypeGeneral);
	}

	/**
	 * Returns the value of field 'content'. The field 'content' has the
	 * following description: internal content storage
	 * 
	 * @return the value of field 'content'.
	 */
	@XmlValue
	public String getContent() {
		return this.content;
	}

	/**
	 * Returns the value of field 'resourceTypeGeneral'.
	 * 
	 * @return the value of field 'resourceTypeGeneral'.
	 */
	@XmlAttribute
	public ResourceTypeGeneral getResourceTypeGeneral() {
		return this.resourceTypeGeneral;
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
	 * Sets the value of field 'resourceTypeGeneral'.
	 * 
	 * @param resourceTypeGeneral
	 *            the value of field 'resourceTypeGeneral'.
	 */
	public void setResourceTypeGeneral(final ResourceTypeGeneral resourceTypeGeneral) {
		this.resourceTypeGeneral = resourceTypeGeneral;
	}

}
