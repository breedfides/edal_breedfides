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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.jersey.client.ClientResponse;
import org.xml.sax.XMLReader;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteMDSConnector;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteXmlMapper;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.XmlFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlContributor;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlContributors;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlCreator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlCreators;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDates;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDescription;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlDescriptions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlFormats;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlNameIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlResource;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlResourceType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlRights;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlRightsList;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSizes;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSubject;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlSubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlTitle;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlTitles;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.ContributorType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DateType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DescriptionType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.ResourceTypeGeneral;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.TitleType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

@SuppressWarnings("unused")
public class DataCiteTest {

	public static void main(String[] args) throws Exception {

		// JAXBContext jaxbContext = JAXBContext.newInstance(XmlResource.class);
		// Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		//
		// jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
		// "http://datacite.org/schema/kernel-2.2
		// http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
		//
		// XmlResource resource = new XmlResource();
		//
		// XmlContributors contributors = new XmlContributors();
		// XmlContributor contributor = new XmlContributor("Lange, Matthias",
		// ContributorType.Supervisor);
		//
		// contributors.addContributor(contributor);
		//
		// XmlCreators creators = new XmlCreators();
		// XmlCreator creator = new XmlCreator("Arend, Daniel");
		// XmlNameIdentifier nameid = new XmlNameIdentifier("testid");
		// nameid.setNameIdentifierScheme("testSchema");
		// creator.setNameIdentifier(nameid);
		// creators.addCreator(creator);
		//
		// XmlDates dates = new XmlDates();
		// XmlDate date = new XmlDate(new java.util.Date().toString(),
		// DateType.Created);
		// dates.addDate(date);
		//
		// XmlSubjects subjects = new XmlSubjects();
		// XmlSubject subject = new XmlSubject("bioinformatics", null);
		// subjects.addSubject(subject);
		//
		// XmlTitles titles = new XmlTitles();
		// XmlTitle title = new XmlTitle("eDAL-API", null);
		// XmlTitle subtitle = new XmlTitle("Storage backend", TitleType.Subtitle);
		// titles.addTitle(title);
		// titles.addTitle(subtitle);
		//
		// XmlIdentifier identifier = new XmlIdentifier("10.5072/EDALTEST/123456");
		//
		// XmlSizes sizes = new XmlSizes();
		// sizes.addSize("1.0 MB");
		//
		// XmlResourceType type = new XmlResourceType("testDOI", ResourceType.Software);
		//
		// XmlFormats formats = new XmlFormats();
		// formats.addFormat("application/zip");
		//
		// XmlDescriptions descriptions = new XmlDescriptions();
		// XmlDescription description = new XmlDescription();
		// description.setDescriptionType(DescriptionType.Abstract);
		// description.setContent("datacite test");
		// descriptions.addDescription(description);
		//
		// XmlRightsList rightsList = new XmlRightsList();
		//
		// rightsList.addRights(new XmlRights("open source"));
		//
		// resource.setDescriptions(descriptions);
		// resource.setVersion("1.0");
		// resource.setRightsList(rightsList);
		// resource.setPublicationYear("2012");
		// resource.setFormats(formats);
		// resource.setResourceType(type);
		// resource.setIdentifier(identifier);
		// resource.setTitles(titles);
		// resource.setSubjects(subjects);
		// // resource.setDates(dates);
		// resource.setContributors(contributors);
		// resource.setCreators(creators);
		// resource.setPublicationYear("2012");
		// resource.setPublisher("Leibniz Institute of Plant Genetics and Crop Plant
		// Research");
		// resource.setLanguage("eng");
		// resource.setSizes(sizes);
		// jaxbMarshaller.marshal(resource, System.out);
		//
		// StringWriter strw = new StringWriter();
		//
		// jaxbMarshaller.marshal(resource, strw);

		EdalConfiguration configuration = null;

		try {
			configuration = new EdalConfiguration("TIB", "", "10.5072", new InternetAddress(""),
					new InternetAddress(""), new InternetAddress(""), new InternetAddress(""));
		} catch (AddressException | EdalConfigurationException e1) {
			e1.printStackTrace();
		}

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		PrimaryDataFile file = rootDirectory.createPrimaryDataFile("TestFile.txt");

		MetaData metadata = file.getMetaData().clone();

		Persons p = new Persons();
		p.add(new NaturalPerson("Daniel", "Arend", "address", "zip", "country"));
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, p);
		metadata.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, p);

		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("IPK", "Gatersleben", "zip", "country"));

		Subjects s = new Subjects();
		s.add(new UntypedData("TestDOI"));

		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, s);

		file.setMetaData(metadata);

		DataCiteXmlMapper mapper = new DataCiteXmlMapper(file.getCurrentVersion());

		XmlResource resource = mapper.createXmlResource();

		resource.setIdentifier(new XmlIdentifier("10.5072/EDALTEST/123456"));

		StringWriter strw = new StringWriter();
		mapper.createXmlMarshaller().marshal(resource, strw);
		System.out.println(strw.getBuffer());

		mapper.validateSchema(resource);

		DataCiteMDSConnector myClient = new DataCiteMDSConnector(configuration);

		Response cr = null;
		try {
			cr = myClient.postMetadata(XmlFunctions.parse(strw.toString()));
		} catch (DataCiteException e) {
			e.printStackTrace();
		}

		System.out.println(cr.getStatus());

		DataManager.shutdown();

	}
}
