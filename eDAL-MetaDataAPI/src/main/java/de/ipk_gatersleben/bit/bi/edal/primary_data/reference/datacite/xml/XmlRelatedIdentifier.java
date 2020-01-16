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
	 * Field relatedMetaDataSchema
	 */
	private String relatedMetaDataSchema;

	/**
	 * Field schemeURI
	 */
	private String schemeURI;

	/**
	 * Field schemaType
	 */
	private String schemaType;

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
	 * @return the relatedMetaDataSchema
	 */
	public String getRelatedMetaDataSchema() {
		return relatedMetaDataSchema;
	}

	/**
	 * @param relatedMetaDataSchema
	 *            the relatedMetaDataSchema to set
	 */
	public void setRelatedMetaDataSchema(String relatedMetaDataSchema) {
		this.relatedMetaDataSchema = relatedMetaDataSchema;
	}

	/**
	 * @return the schemeURI
	 */
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
	 * @return the schemaType
	 */
	public String getSchemaType() {
		return schemaType;
	}

	/**
	 * @param schemaType
	 *            the schemaType to set
	 */
	public void setSchemaType(String schemaType) {
		this.schemaType = schemaType;
	}
}