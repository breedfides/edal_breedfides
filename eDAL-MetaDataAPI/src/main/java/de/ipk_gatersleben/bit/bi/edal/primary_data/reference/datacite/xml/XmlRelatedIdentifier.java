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

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;

/**
 * Identifiers of related resources. Use this property to indicate subsets of
 * properties, as appropriate.
 * 
 * @author arendd
 */
@XmlType
public class XmlRelatedIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * internal content storage
	 */
	private String content = "";

	/**
	 * Field relatedIdentifierType.
	 */
	private RelatedIdentifierType relatedIdentifierType;

	/**
	 * Field relationType.
	 */
	private RelationType relationType;

	/**
	 * Field relatedMetadataScheme
	 */
	private String relatedMetadataScheme;

	/**
	 * Field schemeURI
	 */
	private String schemeURI;

	/**
	 * Field schemeType
	 */
	private String schemeType;

	public XmlRelatedIdentifier() {
		super();
		setContent("");
	}

	public XmlRelatedIdentifier(final String defaultValue) {
		setContent(defaultValue);
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
	 * Returns the value of field 'relatedIdentifierType'.
	 * 
	 * @return the value of field 'RelatedIdentifierType'.
	 */
	@XmlAttribute(required = true)
	public RelatedIdentifierType getRelatedIdentifierType() {
		return this.relatedIdentifierType;
	}

	/**
	 * Returns the value of field 'relationType'.
	 * 
	 * @return the value of field 'RelationType'.
	 */
	@XmlAttribute(required = true)
	public RelationType getRelationType() {
		return this.relationType;
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
	 * Sets the value of field 'relatedIdentifierType'.
	 * 
	 * @param relatedIdentifierType
	 *            the value of field 'relatedIdentifierType'.
	 */
	public void setRelatedIdentifierType(
			final RelatedIdentifierType relatedIdentifierType) {
		this.relatedIdentifierType = relatedIdentifierType;
	}

	/**
	 * Sets the value of field 'relationType'.
	 * 
	 * @param relationType
	 *            the value of field 'relationType'.
	 */
	public void setRelationType(final RelationType relationType) {
		this.relationType = relationType;
	}

	/**
	 * @return the relatedMetadataScheme
	 */
	@XmlAttribute
	public String getRelatedMetadataScheme() {
		return relatedMetadataScheme;
	}

	/**
	 * @param relatedMetadataScheme
	 *            the relatedMetadataScheme to set
	 */
	public void setRelatedMetadataScheme(String relatedMetadataScheme) {
		this.relatedMetadataScheme = relatedMetadataScheme;
	}

	/**
	 * @return the schemeURI
	 */
	@XmlAttribute
	public String getSchemeURI() {
		return schemeURI;
	}

	/**
	 * @param schemeURI
	 *            the schemeURI to set
	 */
	public void setSchemeURI(String schemeURI) {
		this.schemeURI = schemeURI;
	}

	/**
	 * @return the schemeType
	 */
	@XmlAttribute
	public String getSchemeType() {
		return schemeType;
	}

	/**
	 * @param schemeType
	 *            the schemeType to set
	 */
	public void setSchemeType(String schemeType) {
		this.schemeType = schemeType;
	}
}