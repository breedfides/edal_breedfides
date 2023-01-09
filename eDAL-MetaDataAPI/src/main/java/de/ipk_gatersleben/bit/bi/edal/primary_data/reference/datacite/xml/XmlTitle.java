/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.TitleType;

/**
 * A name or title by which a resource is known.
 * 
 * @author arendd
 */
@XmlType(name = "title")
public class XmlTitle implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * internal content storage
	 */
	private String value = "";

	/**
	 * Field titleType.
	 */
	private TitleType titleType;

	/**
	 * Default constructor
	 */
	public XmlTitle() {
		super();
	}

	/**
	 * Constructor to create a {@link XmlTitle} with the specified title.
	 * 
	 * @param value
	 *            the title to set.
	 */
	public XmlTitle(String value) {
		this();
		setValue(value);
	}

	/**
	 * Constructor to create a {@link XmlTitle} with the specified title and
	 * titleType.
	 * 
	 * @param value
	 *            the title to set.
	 * @param titleType
	 *            the titleType to set.
	 */
	public XmlTitle(String value, TitleType titleType) {
		this(value);
		setTitleType(titleType);
	}

	/**
	 * Copy-Constructor to create a {@link XmlTitle} object from a
	 * {@link UntypedData} object.
	 * 
	 * @param data
	 *            to convert into a {@link XmlTitle}.
	 */
	public XmlTitle(UntypedData data) {
		this(data.getString());
	}

	/**
	 * Returns the value of field 'value'. The field 'value' has the following
	 * description: internal content storage
	 * 
	 * @return the value of field 'value'.
	 */
	@XmlValue
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the value of field 'titleType'.
	 * 
	 * @return the value of field 'titleType'.
	 */

	@XmlAttribute
	public TitleType getTitleType() {
		return this.titleType;
	}

	/**
	 * Sets the value of field 'value'. The field 'value' has the following
	 * description: internal content storage
	 * 
	 * @param value
	 *            the value of field 'value'.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets the value of field 'titleType'.
	 * 
	 * @param titleType
	 *            the value of field 'titleType'.
	 */
	public void setTitleType(final TitleType titleType) {
		this.titleType = titleType;
	}
}