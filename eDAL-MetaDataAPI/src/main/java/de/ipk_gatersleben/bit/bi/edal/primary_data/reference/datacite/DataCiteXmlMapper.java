/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DirectoryMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalPublicationMetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlAlternateIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlAlternateIdentifiers;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlContributor;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlContributors;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlCreator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlCreators;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDates;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDescription;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDescriptions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlFormats;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlRelatedIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlRelatedIdentifiers;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlResource;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlResourceType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlRightsList;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSizes;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSubject;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlTitle;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlTitles;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DescriptionType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.ResourceTypeGeneral;

/**
 * Class to map an eDAL {@link MetaData} object to a valid DataCite XML
 * document.
 * 
 * @author arendd
 */
public class DataCiteXmlMapper {

	private static final String SCHEMA_LOCATION = "http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd";

	private PrimaryDataEntityVersion version;

	/**
	 * Constructor to set the {@link MetaData} for this {@link DataCiteXmlMapper}.
	 * 
	 * @param version the {@link PrimaryDataEntityVersion} to create DataCite xml.
	 */
	public DataCiteXmlMapper(final PrimaryDataEntityVersion version) {
		this.setVersion(version);
	}

	/**
	 * Generate a JAXB-{@link Marshaller} for a {@link XmlResource} object.
	 * 
	 * @return the generated JAXB-{@link Marshaller}.
	 * @throws EdalPublicationMetaDataException if unable to initialize
	 *                                          {@link Marshaller}.
	 */
	public Marshaller createXmlMarshaller() throws EdalPublicationMetaDataException {

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(XmlResource.class);

			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, DataCiteXmlMapper.SCHEMA_LOCATION);

			return jaxbMarshaller;

		} catch (final JAXBException e) {
			throw new EdalPublicationMetaDataException("Unable to init JAXB-Marshaller", e);
		}
	}

	/**
	 * Create a {@link XmlResource} from the given {@link MetaData} and validate the
	 * Document against the DataCite Schema before return.
	 * 
	 * @return the valid {@link XmlResource}.
	 * @throws EdalPublicationMetaDataException if unable to create
	 *                                          {@link XmlResource}.
	 */
	public XmlResource createXmlResource() throws EdalPublicationMetaDataException {

		final XmlResource resource = new XmlResource();

		try {
			this.setAlternativeIdentifiers(resource);
			this.setCreators(resource);
			this.setContributors(resource);
			this.setDates(resource);
			this.setDescriptions(resource);
			this.setFormats(resource);
			this.setLanguage(resource);
			this.setIdentifier(resource);
			this.setPublicationYear(resource);
			this.setPublisher(resource);
			this.setTitles(resource);
			this.setSizes(resource);
			this.setSubjects(resource);
			this.setResourceType(resource);
			this.setRights(resource);
			this.setRelatedIdentifier(resource);
//			this.setVersion(resource);
		} catch (final DataCiteMappingException e) {
			throw new EdalPublicationMetaDataException("unable to map metadata to DataCite XML", e);
		}

		return resource;
	}

	private void setRelatedIdentifier(XmlResource resource) {

		XmlRelatedIdentifiers xmlRelatedIdentifiers = new XmlRelatedIdentifiers();

		try {

			IdentifierRelation ir = (IdentifierRelation)this.getMetaData().getElementValue(EnumDublinCoreElements.RELATION);

			if (!ir.getRelations().isEmpty()) {

				for (Identifier id : ir.getRelations()) {

					XmlRelatedIdentifier xmlRelatedIdentifer = new XmlRelatedIdentifier(id.getIdentifier());

					xmlRelatedIdentifer.setRelatedIdentifierType(
							RelatedIdentifierType.fromValue(id.getRelatedIdentifierType().toString()));
					xmlRelatedIdentifer.setRelationType(RelationType.fromValue(id.getRelationType().toString()));
//					xmlRelatedIdentifer.setRelatedMetadataScheme("metadataschema");
//					xmlRelatedIdentifer.setSchemeType("schemaType");
//					xmlRelatedIdentifer.setSchemeURI("schemURI");

					xmlRelatedIdentifiers.addRelatedIdentifier(xmlRelatedIdentifer);

				}
				resource.setRelatedIdentifiers(xmlRelatedIdentifiers);
			}
		} catch (MetaDataException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Getter for the {@link MetaData} of this {@link DataCiteXmlMapper}.
	 * 
	 * @return the set {@link MetaData}.
	 */
	protected MetaData getMetaData() {
		return this.getVersion().getMetaData();
	}

	/**
	 * Getter for the {@link PrimaryDataEntityVersion} of this
	 * {@link DataCiteXmlMapper}.
	 * 
	 * @return the {@link PrimaryDataEntityVersion} to this
	 *         {@link DataCiteXmlMapper}.
	 */
	protected PrimaryDataEntityVersion getVersion() {
		return this.version;
	}

	/**
	 * Internal setter for the {@link XmlAlternateIdentifiers} of the given
	 * {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlAlternateIdentifiers}.
	 * @throws DataCiteMappingException if unable to set AlternativeIdentifiers.
	 */
	private void setAlternativeIdentifiers(final XmlResource resource) throws DataCiteMappingException {

		final XmlAlternateIdentifiers alternateIdentifiers = new XmlAlternateIdentifiers();

		try {

			Identifier id = this.getMetaData().getElementValue(EnumDublinCoreElements.IDENTIFIER);

			if (!id.getIdentifier().equals(Identifier.UNKNOWN_ID.toString())) {
				alternateIdentifiers.addAlternateIdentifier(new XmlAlternateIdentifier(
						(Identifier) this.getMetaData().getElementValue(EnumDublinCoreElements.IDENTIFIER)));
				resource.setAlternateIdentifiers(alternateIdentifiers);
			}

		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add identifiers", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the {@link XmlContributors} of the given
	 * {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlContributors}.
	 * @throws DataCiteMappingException if unable to set Contributors.
	 */
	private void setContributors(final XmlResource resource) throws DataCiteMappingException {

		final XmlContributors contributors = new XmlContributors();

		try {
			Persons persons = this.getMetaData().getElementValue(EnumDublinCoreElements.CONTRIBUTOR);

			for (Person person : persons.getPersons()) {
				XmlContributor contributor = new XmlContributor(person);

				contributors.addContributor(contributor);
			}

		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add contributor", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		resource.setContributors(contributors);
	}

	/**
	 * Internal setter for the {@link XmlCreators} of the given {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlCreators}.
	 * @throws DataCiteMappingException if unable to set Creators.
	 */
	private void setCreators(final XmlResource resource) throws DataCiteMappingException {

		final XmlCreators creators = new XmlCreators();

		try {

			Persons persons = this.getMetaData().getElementValue(EnumDublinCoreElements.CREATOR);

			for (Person person : persons.getPersons()) {
				creators.addCreator(new XmlCreator(person));
			}
		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add creator", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		resource.setCreators(creators);
	}

	/**
	 * Internal setter for the {@link XmlDates} of the given {@link XmlResource} .
	 * 
	 * @param resource to set the {@link XmlDates}.
	 * @throws DataCiteMappingException if unable to set Dates.
	 */
	private void setDates(final XmlResource resource) throws DataCiteMappingException {

		try {
			final XmlDates dates = new XmlDates(
					(DateEvents) this.getMetaData().getElementValue(EnumDublinCoreElements.DATE));
			if (dates.getDate().length != 0) {
				resource.setDates(dates);
			}
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the {@link XmlDescriptions} of the given
	 * {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlDescriptions}.
	 * @throws DataCiteMappingException if unable to set Descriptions.
	 */
	private void setDescriptions(final XmlResource resource) throws DataCiteMappingException {

		final XmlDescriptions descriptions = new XmlDescriptions();

		try {

			final XmlDescription description = new XmlDescription(
					this.getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).getString());
			description.setDescriptionType(DescriptionType.Abstract);
			descriptions.addDescription(description);
		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add descriptions", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		resource.setDescriptions(descriptions);

	}

	/**
	 * Internal setter for the {@link XmlFormats} of the given {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlFormats}.
	 * @throws DataCiteMappingException if unable to set Formats.
	 */
	private void setFormats(final XmlResource resource) throws DataCiteMappingException {

		final XmlFormats formats = new XmlFormats();
		try {

			if (this.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT).getClass().equals(DataFormat.class)) {

				formats.addFormat(
						((DataFormat) this.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType());
				resource.setFormats(formats);
			} else if (this.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT).getClass()
					.equals(EmptyMetaData.class)) {

				// do nothing for directories
				// formats.addFormat("empty");
				// resource.setFormats(formats);
			}
		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add formats", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the {@link XmlIdentifier} of the given
	 * {@link XmlIdentifier}.
	 * 
	 * @param resource to set the {@link XmlFormats}.
	 */
	private void setIdentifier(final XmlResource resource) {
		final XmlIdentifier identifier = new XmlIdentifier("10.1000/100");
		resource.setIdentifier(identifier);
	}

	/**
	 * Internal setter for the language of the given {@link XmlResource}.
	 * 
	 * @param resource to set the language.
	 * @throws DataCiteMappingException if unable to set Language.
	 */
	private void setLanguage(final XmlResource resource) throws DataCiteMappingException {

		try {
			final String language = ((EdalLanguage) this.getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE))
					.getLanguage().getLanguage();
			if (!language.isEmpty()) {
				resource.setLanguage(language);
			}
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}
	}

	/**
	 * Internal setter for the publicationYear of the given {@link XmlResource}.
	 * 
	 * @param resource to set the publicationYear.
	 */
	private void setPublicationYear(final XmlResource resource) {
		resource.setPublicationYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
	}

	/**
	 * Internal setter for the publisher of the given {@link XmlResource}.
	 * 
	 * @param resource to set the publisher.
	 * @throws DataCiteMappingException if unable to set Publisher.
	 */
	private void setPublisher(final XmlResource resource) throws DataCiteMappingException {

		try {
			final UntypedData data = this.getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);

			if (data instanceof NaturalPerson) {

				final NaturalPerson person = (NaturalPerson) data;

				final StringBuffer buffer = new StringBuffer();

				if (!person.getSureName().isEmpty()) {
					buffer.append(person.getSureName() + " ");
				}
				if (!person.getGivenName().isEmpty()) {
					buffer.append(person.getGivenName() + " ");
				}
				if (!person.getZip().isEmpty()) {
					buffer.append(person.getZip() + " ");
				}
				if (!person.getAddressLine().isEmpty()) {
					buffer.append(person.getAddressLine() + " ");
				}
				if (!person.getCountry().isEmpty()) {
					buffer.append(person.getCountry() + " ");
				}

				if (!buffer.toString().isEmpty()) {
					resource.setPublisher(buffer.toString());
				}

			} else if (data instanceof LegalPerson) {

				final LegalPerson person = (LegalPerson) data;
				final StringBuffer buffer = new StringBuffer();

				if (!person.getLegalName().isEmpty()) {
					buffer.append(person.getLegalName() + ", ");
				}

				if (!person.getAddressLine().isEmpty()) {
					buffer.append(person.getAddressLine() + ", ");
				}

				if (!person.getZip().isEmpty()) {
					buffer.append(person.getZip() + ", ");
				}

				if (!person.getCountry().isEmpty()) {
					buffer.append(person.getCountry());
				}

				if (!buffer.toString().isEmpty()) {
					resource.setPublisher(buffer.toString());
				}
			}

			else if (!data.getString().isEmpty()) {
				resource.setPublisher(data.getString());
			}
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the {@link XmlResourceType} of the given
	 * {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlResourceType}.
	 * @throws DataCiteMappingException if unable to set ResourceType.
	 */
	private void setResourceType(final XmlResource resource) throws DataCiteMappingException {

		try {

			if (this.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).getClass().equals(DataType.class)) {

				final XmlResourceType resourceType = new XmlResourceType(
						((DataType) this.getMetaData().getElementValue(EnumDublinCoreElements.TYPE)).getString());
				resourceType.setResourceTypeGeneral(ResourceTypeGeneral.Dataset);
				resource.setResourceType(resourceType);

			}

			else if (this.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).getClass()
					.equals(DirectoryMetaData.class)) {

				final XmlResourceType resourceType = new XmlResourceType("directory");

				resourceType.setResourceTypeGeneral(ResourceTypeGeneral.Collection);
				resource.setResourceType(resourceType);
			}

		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the rights of the given {@link XmlResource}.
	 * 
	 * @param resource to set the rights.
	 * @throws DataCiteMappingException if unable to set Rights.
	 */
	private void setRights(final XmlResource resource) throws DataCiteMappingException {

		try {

			if (this.getMetaData().getElementValue(EnumDublinCoreElements.RIGHTS) != null && !this.getMetaData()
					.getElementValue(EnumDublinCoreElements.RIGHTS).getString().equals(UntypedData.EMPTY)) {
				resource.setRightsList(
						new XmlRightsList(this.getMetaData().getElementValue(EnumDublinCoreElements.RIGHTS)));
			}

		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

	}

	/**
	 * Internal setter for the {@link XmlSizes} of the given {@link XmlResource} .
	 * 
	 * @param resource to set the {@link XmlSizes}.
	 * @throws DataCiteMappingException if unable to set Sizes.
	 */
	private void setSizes(final XmlResource resource) throws DataCiteMappingException {

		final XmlSizes sizes = new XmlSizes();

		try {
			sizes.addSize(((DataSize) this.getMetaData().getElementValue(EnumDublinCoreElements.SIZE)).getFileSize());
		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add sizes", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		resource.setSizes(sizes);
	}

	/**
	 * Internal setter for the {@link XmlSubjects} of the given {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlSubjects}.
	 * @throws DataCiteMappingException if unable to set Subjects.
	 */
	private void setSubjects(final XmlResource resource) throws DataCiteMappingException {

		final XmlSubjects xmlSubjects = new XmlSubjects();

		Subjects subjects = null;

		try {
			subjects = this.getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		if (!subjects.isEmpty()) {
			for (UntypedData subject : subjects) {
				try {
					xmlSubjects.addSubject(new XmlSubject(subject.getString()));
				} catch (final IndexOutOfBoundsException e) {
					throw new DataCiteMappingException("unable to add subject", e);
				}
			}
			resource.setSubjects(xmlSubjects);
		}
	}

	/**
	 * Internal setter for the {@link XmlTitles} of the given {@link XmlResource}.
	 * 
	 * @param resource to set the {@link XmlTitles}.
	 * @throws DataCiteMappingException if unable to set Titles.
	 */
	private void setTitles(final XmlResource resource) throws DataCiteMappingException {

		final XmlTitles titles = new XmlTitles();

		try {
			titles.addTitle(new XmlTitle(this.getMetaData().getElementValue(EnumDublinCoreElements.TITLE)));
		} catch (final IndexOutOfBoundsException e) {
			throw new DataCiteMappingException("unable to add title", e);
		} catch (final MetaDataException e) {
			throw new DataCiteMappingException("unable to load metadata", e);
		}

		resource.setTitles(titles);
	}

	/**
	 * Internal setter for the {@link PrimaryDataEntityVersion} of the given
	 * {@link XmlResource}.
	 * 
	 * @param version the {@link PrimaryDataEntityVersion} to set.
	 */
	private void setVersion(final PrimaryDataEntityVersion version) {
		this.version = version;
	}

	/**
	 * Internal setter for the version of the given {@link XmlResource}.
	 * 
	 * @param resource to set the version.
	 */
	private void setVersion(final XmlResource resource) {
		resource.setVersion(this.version.getRevisionDate().getTime().toString());
	}

	/**
	 * Validate the {@link MetaData} schema of the generated {@link XmlResource} .
	 * 
	 * @param resource the {@link XmlResource} to check.
	 * @throws EdalPublicationMetaDataException if validation failed.
	 */
	public void validateSchema(final XmlResource resource) throws EdalPublicationMetaDataException {

		final StringWriter stringWriter = new StringWriter();

		final URL dataCiteMetadataSchema = DataCiteXmlMapper.class.getResource("schema-4.3/metadata.xsd");

		try {
			this.createXmlMarshaller().marshal(resource, stringWriter);
			stringWriter.close();
		} catch (final JAXBException e) {
			throw new EdalPublicationMetaDataException("unable to marshall resource", e);
		} catch (final IOException e) {
			throw new EdalPublicationMetaDataException("unable to write to StringWriter", e);
		} catch (final EdalPublicationMetaDataException e) {
			throw new EdalPublicationMetaDataException("unable to create XML-Marshaller", e);
		}

		final Document doc = XmlFunctions.parse(stringWriter.toString());

		System.out.println(stringWriter.toString());
		try {
			XmlFunctions.validate(dataCiteMetadataSchema, doc);
		} catch (final SAXException e) {
			throw new EdalPublicationMetaDataException("resource object is invalid: " + e.getMessage(), e);
		} catch (final IOException e) {
			throw new EdalPublicationMetaDataException("unable to load meta data schema", e);
		}

	}

}
