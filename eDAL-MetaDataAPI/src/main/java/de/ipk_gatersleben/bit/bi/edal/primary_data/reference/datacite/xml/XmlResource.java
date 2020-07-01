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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Root element of a single record. This wrapper element is for XML
 * implementation only and is not defined in the DataCite DOI standard. Note:
 * This is the case for any wrapper element within this schema! No content in
 * this wrapper element.
 * 
 * 
 * @author arendd
 */
@XmlRootElement(name = "resource")
@XmlType(propOrder = { "identifier", "creators", "titles", "publisher",
		"publicationYear", "subjects", "contributors", "dates", "language",
		"resourceType", "alternateIdentifiers", "sizes", "formats", "version",
		"rightsList", "descriptions", "relatedIdentifiers", "geoLocations" })
public class XmlResource implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * keeps track of state for field: metadataVersionNumber
	 */
	private boolean hasMetadataVersionNumber;

	/**
	 * A persistent identifier that identifies a resource.
	 */
	private XmlIdentifier identifier;

	/**
	 * Field creators.
	 */
	private XmlCreators creators;

	/**
	 * Field titles.
	 */
	private XmlTitles titles;

	/**
	 * Field geoLocations.
	 */
	private XmlGeoLocations geoLocations;

	/**
	 * A holder of the data (including archives as appropriate) or institution
	 * which submitted the work. Any others may be listed as contributors. This
	 * property will be used to formulate the citation, so consider the
	 * prominence of the role.
	 * 
	 */
	private String publisher;

	/**
	 * Year when the data is made publicly available. If an embargo period has
	 * been in effect, use the date when the embargo period ends.
	 */
	private String publicationYear;

	/**
	 * Field subjects.
	 */
	private XmlSubjects subjects;

	/**
	 * Field contributors.
	 */
	private XmlContributors contributors;

	/**
	 * Field dates.
	 */
	private XmlDates dates;

	/**
	 * Primary language of the resource. Allowed values from: ISO 639-2/B, ISO
	 * 639-3
	 */
	private String language;

	/**
	 * The type of a resource. You may enter an additional free text
	 * description.
	 */
	private XmlResourceType resourceType;

	/**
	 * Field alternateIdentifiers.
	 */
	private XmlAlternateIdentifiers alternateIdentifiers;

	/**
	 * Field relatedIdentifiers.
	 */
	private XmlRelatedIdentifiers relatedIdentifiers;

	/**
	 * Field sizes.
	 */
	private XmlSizes sizes;

	/**
	 * Field formats.
	 */
	private XmlFormats formats;

	/**
	 * Version number of the resource. If the primary resource has changed the
	 * version number increases.
	 * 
	 */
	private String version;

	/**
	 * Any rights information for this resource. Provide a rights management
	 * statement for the resource or reference a service providing such
	 * information. Include embargo information if applicable.
	 * 
	 */
	private XmlRightsList rightsList;

	/**
	 * Field descriptions.
	 */
	private XmlDescriptions descriptions;

	public XmlResource() {
		super();
	}

	/**
     */
	public void deleteMetadataVersionNumber() {
		this.hasMetadataVersionNumber = false;
	}

	/**
	 * Returns the value of field 'alternateIdentifiers'.
	 * 
	 * @return the value of field 'AlternateIdentifiers'.
	 */
	public XmlAlternateIdentifiers getAlternateIdentifiers() {
		return this.alternateIdentifiers;
	}

	/**
	 * Returns the value of field 'contributors'.
	 * 
	 * @return the value of field 'Contributors'.
	 */
	public XmlContributors getContributors() {
		return this.contributors;
	}

	/**
	 * Returns the value of field 'creators'.
	 * 
	 * @return the value of field 'Creators'.
	 */
	public XmlCreators getCreators() {
		return this.creators;
	}

	/**
	 * Returns the value of field 'dates'.
	 * 
	 * @return the value of field 'Dates'.
	 */
	public XmlDates getDates() {
		return this.dates;
	}

	/**
	 * Returns the value of field 'descriptions'.
	 * 
	 * @return the value of field 'Descriptions'.
	 */
	public XmlDescriptions getDescriptions() {
		return this.descriptions;
	}

	/**
	 * Returns the value of field 'formats'.
	 * 
	 * @return the value of field 'Formats'.
	 */
	public XmlFormats getFormats() {
		return this.formats;
	}

	/**
	 * Returns the value of field 'identifier'. The field 'identifier' has the
	 * following description: A persistent identifier that identifies a
	 * resource.
	 * 
	 * @return the value of field 'Identifier'.
	 */
	public XmlIdentifier getIdentifier() {
		return this.identifier;
	}

	/**
	 * Returns the value of field 'language'. The field 'language' has the
	 * following description: Primary language of the resource. Allowed values
	 * from: ISO 639-2/B, ISO 639-3
	 * 
	 * @return the value of field 'Language'.
	 */
	@XmlElement(required = true)
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the value of field 'publicationYear'. The field 'publicationYear'
	 * has the following description: Year when the data is made publicly
	 * available. If an embargo period has been in effect, use the date when the
	 * embargo period ends.
	 * 
	 * @return the value of field 'PublicationYear'.
	 */
	public String getPublicationYear() {
		return this.publicationYear;
	}

	/**
	 * Returns the value of field 'publisher'. The field 'publisher' has the
	 * following description: A holder of the data (including archives as
	 * appropriate) or institution which submitted the work. Any others may be
	 * listed as contributors. This property will be used to formulate the
	 * citation, so consider the prominence of the role.
	 * 
	 * 
	 * @return the value of field 'Publisher'.
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * Returns the value of field 'relatedIdentifiers'.
	 * 
	 * @return the value of field 'RelatedIdentifiers'.
	 */
	public XmlRelatedIdentifiers getRelatedIdentifiers() {
		return this.relatedIdentifiers;
	}

	/**
	 * Returns the value of field 'resourceType'. The field 'resourceType' has
	 * the following description: The type of a resource. You may enter an
	 * additional free text description.
	 * 
	 * @return the value of field 'ResourceType'.
	 */
	public XmlResourceType getResourceType() {
		return this.resourceType;
	}

	/**
	 * Returns the value of field 'rights'. The field 'rights' has the following
	 * description: Any rights information for this resource. Provide a rights
	 * management statement for the resource or reference a service providing
	 * such information. Include embargo information if applicable.
	 * 
	 * 
	 * @return the value of field 'Rights'.
	 */
	public XmlRightsList getRightsList() {
		return this.rightsList;
	}

	/**
	 * Returns the value of field 'sizes'.
	 * 
	 * @return the value of field 'Sizes'.
	 */
	public XmlSizes getSizes() {
		return this.sizes;
	}

	/**
	 * Returns the value of field 'subjects'.
	 * 
	 * @return the value of field 'Subjects'.
	 */
	public XmlSubjects getSubjects() {
		return this.subjects;
	}

	/**
	 * Returns the value of field 'titles'.
	 * 
	 * @return the value of field 'Titles'.
	 */
	public XmlTitles getTitles() {
		return this.titles;
	}

	/**
	 * Returns the value of field 'version'. The field 'version' has the
	 * following description: Version number of the resource. If the primary
	 * resource has changed the version number increases.
	 * 
	 * 
	 * @return the value of field 'Version'.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Method hasMetadataVersionNumber.
	 * 
	 * @return true if at least one MetadataVersionNumber has been added
	 */
	public boolean hasMetadataVersionNumber() {
		return this.hasMetadataVersionNumber;
	}

	/**
	 * Sets the value of field 'alternateIdentifiers'.
	 * 
	 * @param alternateIdentifiers
	 *            the value of field 'alternateIdentifiers'.
	 */
	public void setAlternateIdentifiers(
			final XmlAlternateIdentifiers alternateIdentifiers) {
		this.alternateIdentifiers = alternateIdentifiers;
	}

	/**
	 * Sets the value of field 'contributors'.
	 * 
	 * @param contributors
	 *            the value of field 'contributors'.
	 */
	public void setContributors(final XmlContributors contributors) {
		this.contributors = contributors;
	}

	/**
	 * Sets the value of field 'creators'.
	 * 
	 * @param creators
	 *            the value of field 'creators'.
	 */
	public void setCreators(final XmlCreators creators) {
		this.creators = creators;
	}

	/**
	 * Sets the value of field 'dates'.
	 * 
	 * @param dates
	 *            the value of field 'dates'.
	 */
	public void setDates(final XmlDates dates) {
		this.dates = dates;
	}

	/**
	 * Sets the value of field 'descriptions'.
	 * 
	 * @param descriptions
	 *            the value of field 'descriptions'.
	 */
	public void setDescriptions(final XmlDescriptions descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * Sets the value of field 'formats'.
	 * 
	 * @param formats
	 *            the value of field 'formats'.
	 */
	public void setFormats(final XmlFormats formats) {
		this.formats = formats;
	}

	/**
	 * Sets the value of field 'identifier'. The field 'identifier' has the
	 * following description: A persistent identifier that identifies a
	 * resource.
	 * 
	 * @param identifier
	 *            the value of field 'identifier'.
	 */
	public void setIdentifier(final XmlIdentifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the value of field 'language'. The field 'language' has the
	 * following description: Primary language of the resource. Allowed values
	 * from: ISO 639-2/B, ISO 639-3
	 * 
	 * @param language
	 *            the value of field 'language'.
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}

	/**
	 * Sets the value of field 'publicationYear'. The field 'publicationYear'
	 * has the following description: Year when the data is made publicly
	 * available. If an embargo period has been in effect, use the date when the
	 * embargo period ends.
	 * 
	 * @param publicationYear
	 *            the value of field 'publicationYear'.
	 */
	public void setPublicationYear(final String publicationYear) {
		this.publicationYear = publicationYear;
	}

	/**
	 * Sets the value of field 'publisher'. The field 'publisher' has the
	 * following description: A holder of the data (including archives as
	 * appropriate) or institution which submitted the work. Any others may be
	 * listed as contributors. This property will be used to formulate the
	 * citation, so consider the prominence of the role.
	 * 
	 * 
	 * @param publisher
	 *            the value of field 'publisher'.
	 */
	public void setPublisher(final String publisher) {
		this.publisher = publisher;
	}

	/**
	 * Sets the value of field 'relatedIdentifiers'.
	 * 
	 * @param relatedIdentifiers
	 *            the value of field 'relatedIdentifiers'.
	 */
	public void setRelatedIdentifiers(
			final XmlRelatedIdentifiers relatedIdentifiers) {
		this.relatedIdentifiers = relatedIdentifiers;
	}

	/**
	 * Sets the value of field 'resourceType'. The field 'resourceType' has the
	 * following description: The type of a resource. You may enter an
	 * additional free text description.
	 * 
	 * @param resourceType
	 *            the value of field 'resourceType'.
	 */
	public void setResourceType(final XmlResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * Sets the value of field 'rights'. The field 'rights' has the following
	 * description: Any rights information for this resource. Provide a rights
	 * management statement for the resource or reference a service providing
	 * such information. Include embargo information if applicable.
	 * 
	 * 
	 * @param rightsList
	 *            the value of field 'rightsList'.
	 */
	public void setRightsList(final XmlRightsList rightsList) {
		this.rightsList = rightsList;
	}

	/**
	 * Sets the value of field 'sizes'.
	 * 
	 * @param sizes
	 *            the value of field 'sizes'.
	 */
	public void setSizes(final XmlSizes sizes) {
		this.sizes = sizes;
	}

	/**
	 * Sets the value of field 'subjects'.
	 * 
	 * @param subjects
	 *            the value of field 'subjects'.
	 */
	public void setSubjects(final XmlSubjects subjects) {
		this.subjects = subjects;
	}

	/**
	 * Sets the value of field 'titles'.
	 * 
	 * @param titles
	 *            the value of field 'titles'.
	 */
	public void setTitles(final XmlTitles titles) {
		this.titles = titles;
	}

	/**
	 * Sets the value of field 'version'. The field 'version' has the following
	 * description: Version number of the resource. If the primary resource has
	 * changed the version number increases.
	 * 
	 * 
	 * @param version
	 *            the value of field 'version'.
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * @return the geoLocations
	 */
	public XmlGeoLocations getGeoLocations() {
		return geoLocations;
	}

	/**
	 * @param geoLocations
	 *            the geoLocations to set
	 */
	public void setGeoLocations(XmlGeoLocations geoLocations) {
		this.geoLocations = geoLocations;
	}
}
