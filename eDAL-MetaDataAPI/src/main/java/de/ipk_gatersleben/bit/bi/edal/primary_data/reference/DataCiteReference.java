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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteMDSConnector;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteRestConnector;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.DataCiteXmlMapper;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.XmlFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.XmlResource;

/**
 * DataCite implementation of the {@link Referenceable} interface, to connect
 * the system with the DataCite interface.
 * 
 * @author arendd
 */
@SuppressWarnings("unused")
public class DataCiteReference implements Referenceable {

	private static final String TEST_URL = "http://doi.ipk-gatersleben.de/testdata/demo_doi_landingpage/";
	private static final String TEST_DOI = EdalConfiguration.DATACITE_TESTPREFIX + "/EDALTEST/";

	/** {@inheritDoc} */
	@Override
	public String acceptApprovalRequest(PublicReference publicReference) throws ReferenceableException {

		synchronized (DataCiteReference.class) {

			if (!DataManager.getConfiguration().isInTestMode()) {

				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);

					String doi = "";
					try {
						doi = new DataCiteRestConnector(DataManager.getConfiguration()).generateNewDOI(year);
					} catch (DataCiteException e) {
						throw new ReferenceableException("unable to generate new DOI", e);
					}

					DataManager.getImplProv().getLogger().info("Next Free DOI from DataCite: " + doi);

					StringBuffer dataCiteXml = generateDataCiteXML(publicReference, doi);

					DataManager.getImplProv().getLogger().info("Generated DataCite XML : \n" + dataCiteXml);

					String internalURL = createLandingPageURL(publicReference).toString();

					DataManager.getImplProv().getLogger().info("Validating : " + doi + "...");

					this.validateMetaData(publicReference.getVersion());

					DataManager.getImplProv().getLogger().info("Validation successful !");

					DataCiteMDSConnector connector = new DataCiteMDSConnector(DataManager.getConfiguration());

					try {
						DataManager.getImplProv().getLogger().info("Posting MetaData for : " + doi + "...");
						connector.postMetadata(XmlFunctions.parse(dataCiteXml.toString()));
						DataManager.getImplProv().getLogger().info("Posting URL : " + internalURL + "...");
						connector.postDOI(doi, internalURL);
						DataManager.getImplProv().getLogger().info("Post was successful for : " + doi);
					} catch (DataCiteException e) {
						throw new ReferenceableException("unable to post DOI to DataCite : ", e);
					}

					try {
						DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
								.storePersistentID(publicReference, doi, year);
					} catch (ReflectiveOperationException e) {
						throw new EdalApprovalException("unable to store a new DOI for the PublicReference");
					}

					return doi;

				} catch (EdalException | EdalPublicationMetaDataException e) {
					throw new ReferenceableException("unable to accept approvalRequest", e);
				}
			} else {
				try {

					int year = Calendar.getInstance().get(Calendar.YEAR);

					String testDoi = TEST_DOI + year + "/" + UUID.randomUUID();

					StringBuffer dataCiteXml = generateDataCiteXML(publicReference, testDoi);

					// countFileTypes(publicReference.getVersion().getEntity());

					this.validateMetaData(publicReference.getVersion());

					System.out.println(dataCiteXml);

					try {
						DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
								.storePersistentID(publicReference, testDoi, year);
					} catch (ReflectiveOperationException e) {
						throw new ReferenceableException("unable to store a new DOI for the PublicReference");
					}

					DataManager.getImplProv().getLogger().warn(
							"Your PublicReference was not posted to DataCite, because you are running in Test-Mode");

					return testDoi;

				} catch (EdalException | EdalPublicationMetaDataException e) {
					throw new ReferenceableException("unable to accept approvalRequest:" + e.getMessage(),
							e.getCause());
				}
			}
		}
	}

	private void countFileTypes(PrimaryDataEntity entity) {

		HashMap<String, Integer> myMap = new HashMap<String, Integer>();

		if (entity instanceof PrimaryDataDirectory) {

			PrimaryDataDirectory directory = (PrimaryDataDirectory) entity;

			try {
				listDir(directory, myMap);
			} catch (PrimaryDataDirectoryException e) {
				e.printStackTrace();
			}

		}

		System.out.println(myMap);

	}

	private void listDir(final PrimaryDataDirectory currentDirectory, HashMap<String, Integer> map)
			throws PrimaryDataDirectoryException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();
		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				if (primaryDataEntity.isDirectory()) {
					listDir((PrimaryDataDirectory) primaryDataEntity, map);
				} else {
					PrimaryDataFile file = (PrimaryDataFile) primaryDataEntity;
					DataFormat format = null;
					try {
						format = file.getMetaData().getElementValue(EnumDublinCoreElements.FORMAT);
					} catch (MetaDataException e) {
						e.printStackTrace();
					}

					String type = format.getMimeType().substring(0, format.getMimeType().indexOf("/"));

					if (map.containsKey(type)) {
						map.put(type, ((Integer) map.get(type)) + 1);
					} else {
						map.put(type, 1);
					}
				}
			}
		}
	}

	/**
	 * Generate a DataCiteX XML document as {@link String} from a
	 * {@link PublicReference} object.
	 * 
	 * @param publicReference
	 *            the reference to generate a XML document.
	 * @param doi
	 *            the new DOI for this {@link PublicReference}.
	 * @return the XML document as {@link String}.
	 * @throws EdalPublicationMetaDataException
	 *             if unable to marshal the meta data to XML.
	 */
	private StringBuffer generateDataCiteXML(PublicReference publicReference, String doi)
			throws EdalPublicationMetaDataException {

		DataCiteXmlMapper xmlMapper = new DataCiteXmlMapper(publicReference.getVersion());

		XmlResource xmlResource = xmlMapper.createXmlResource();

		xmlResource.setIdentifier(new XmlIdentifier(doi));

		StringWriter strw = new StringWriter();

		try {
			xmlMapper.createXmlMarshaller().marshal(xmlResource, strw);
		} catch (JAXBException e) {
			throw new EdalPublicationMetaDataException("Unable to marshall meta data from PublicReference", e);
		}
		return strw.getBuffer();
	}

	/**
	 * Create the landing page string without the server part (host and port) for a
	 * given PublicReference.
	 * 
	 * @param reference
	 *            the {@link PublicReference} corresponding to this landing page.
	 * @return the landing page
	 */
	private String createLandingPageString(PublicReference reference) {

		String landingpage = EdalHttpServer.EDAL_PATH_SEPARATOR + reference.getIdentifierType().toString()
				+ EdalHttpServer.EDAL_PATH_SEPARATOR + reference.getInternalID() + EdalHttpServer.EDAL_PATH_SEPARATOR
				+ reference.getVersion().getEntity().getID() + EdalHttpServer.EDAL_PATH_SEPARATOR
				+ reference.getVersion().getRevision();

		return landingpage;
	}

	/**
	 * Create the complete landing page {@link URL} including server part to send it
	 * in an email to the requested author.
	 * 
	 * @param reference
	 *            the {@link PublicReference} corresponding to this {@link URL}.
	 * @return the complete URL
	 * @throws EdalApprovalException
	 *             if unable to create the {@link URL}.
	 */
	private URL createLandingPageURL(PublicReference reference) throws EdalApprovalException {
		URL url = null;
		try {
			url = EdalHttpServer.getServerURL();
			return new URL(url, createLandingPageString(reference));

		} catch (EdalException | MalformedURLException e) {
			throw new EdalApprovalException("unable to create URL for the landing page : " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * no implementation for DataCite necessary: impossible to reserve IDs
	 */
	@Override
	public void rejectApprovalRequest(PublicReference publicReference) throws ReferenceableException {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Check the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} schema
	 * against the {@link DataCiteXmlMapper} schema.
	 * 
	 * @throws EdalPublicationMetaDataException
	 *             if validation failed.
	 * 
	 */
	@Override
	public void validateMetaData(PrimaryDataEntityVersion entityVersion) throws EdalPublicationMetaDataException {

		DataCiteXmlMapper mapper = new DataCiteXmlMapper(entityVersion);
		XmlResource resource = mapper.createXmlResource();
		mapper.validateSchema(resource);

	}

	@Override
	public StringBuffer negotiateContent(PublicReference publicReference, ContentNegotiationType type) {

		try {
			if (publicReference.getAssignedID().startsWith(EdalConfiguration.DATACITE_TESTPREFIX)) {
				return null;
			} else {
				JerseyClient client = JerseyClientBuilder.createClient();

				WebTarget resource = null;
				try {
					resource = client.target("https://data.datacite.org/"
							+ type.getType().substring(0, type.getType().indexOf("/")) + "/"
							+ type.getType().substring(type.getType().indexOf("/"), type.getType().length()) + "/"
							+ publicReference.getAssignedID());
				} catch (PublicReferenceException e) {
					e.printStackTrace();
				}

				final Response response = resource.request(type.getType() + ";charset=UTF-8").get();

				if (response.getStatus() == (HttpStatus.Code.OK.getCode())) {

					StringBuffer buf = new StringBuffer(response.readEntity(String.class));

					client.close();
					return buf;
				} else {
					client.close();
					return null;
				}
			}
		} catch (PublicReferenceException e) {
			e.printStackTrace();
			return null;
		}

	}

}