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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CalculateDirectorySizeThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.NativeLuceneIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.ServiceProviderImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumCCLicense;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * VeloCity template generator to create HTML output for
 * {@link PrimaryDataDirectory}s and {@link PrimaryDataFile}s.
 * 
 * @author arendd
 */
class VeloCityHtmlGenerator {

	private static final String STRING_VERSION = "version";
	private static final String STRING_NO_PUBLIC_REFERENCE_FOR_THIS_VERSION_SET = "No Public Reference for this version set!";
	private static final String STRING_UNABLE_TO_LOAD_VERSIONS_OF = "unable to load versions of ";
	private static final String STRING_PUBLIC_REFERENCE_AND_VERSION_NUMBER_ARE_NOT_COMPATIBLE = "PublicReference and version number are not compatible";
	private static final String STRING_UNABLE_TO_INITIALIZE_APPROVAL_SERVICE_PROVIDER = "unable to initialize ApprovalServiceProvider: ";
	private static final String STRING_ALLOBJECTS = "allobjects";
	private static final String STRING_REVIEWER_CODE = "reviewerCode";
	private static final String STRING_INTERNAL_ID = "internalId";
	private static final String STRING_IDENTIFIER_TYPE = "identifierType";
	private static final String STRING_DATE = "date";
	private static final String STRING_ENTITY = "entity";
	private static final String STRING_ALL_ELEMENTS = "allElements";
	private static final String STRING_UNABLE_TO_WRITE_HTML_OUTPUT = "unable to write HTML output";
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	private static final String STRING_SERVER_URL = "serverURL";
	private static final String STRING_CITATION_ENTITY = "citation_entity";
	private static final String DOWNLOAD_SERVER_URL = "downloadURL";
	
	public static final Map<String, HashSet<String>> ipMap = new HashMap<String, HashSet<String>>();

	public static final Map<String, Long> downloadedVolume = new HashMap<String, Long>();

	public static final Map<String, Long> uniqueAccessNumbers = new HashMap<String, Long>();

	/**
	 * Default constructor to load all VeloCity properties.
	 */
	VeloCityHtmlGenerator() {

		Velocity.setProperty("resource.loader", "class");
		Velocity.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.setProperty("input.encoding", DEFAULT_CHARSET);
		Velocity.setProperty("output.encoding", DEFAULT_CHARSET);
		Velocity.init();
	}

	/**
	 * Generate the HTML output for an eMail that the root user was changed.
	 * 
	 * @param newAddress the address of the new root user.
	 * @param oldAddress the address of the old root user.
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateEmailForChangedRootUser(final InternetAddress newAddress,
			final InternetAddress oldAddress) throws EdalException {

		final VelocityContext context = new VelocityContext();

		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		/* set the address of the new root user */
		context.put("newRoot", newAddress);

		/* set the address of the old root user */
		context.put("oldRoot", oldAddress);

		/* set the server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL());

		final StringWriter output = new StringWriter();

		Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/ChangedRootUserEmailTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * Generate the HTML output for an eMail for the double opt-in of a root user.
	 * 
	 * @param address the address of the root user.
	 * @param the     {@link UUID} to identify the root user.
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateEmailForDoubleOptIn(final InternetAddress address, final UUID uuid)
			throws EdalException {

		final VelocityContext context = new VelocityContext();

		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		/** create the URL to confirm the eMail address */

		final String url = EdalHttpServer.getServerURL().toString() + EdalHttpServer.EDAL_PATH_SEPARATOR
				+ EdalHttpFunctions.LOGIN.toString() + EdalHttpServer.EDAL_PATH_SEPARATOR + uuid.toString()
				+ EdalHttpServer.EDAL_PATH_SEPARATOR + address.getAddress();

		/** set the URL to confirm the email address */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, url);
		context.put("server", EdalHttpServer.getServerURL());
		context.put("root", address);
		final StringWriter output = new StringWriter();

		Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/DoubleOptInEmailTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * Generate HTML output for an Error message of the HHTP handler.
	 * 
	 * @param responseCode    the error code of the response, e.g. 404.
	 * @param message         the error message.
	 * @param teeOutputStream
	 * @param latch
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected OutputStreamWriter generateEmbeddedHtmlForErrorMessage(final HttpStatus.Code responseCode,
			final String message, final TeeOutputStream teeOutputStream, final CountDownLatch latch)
			throws EdalException {

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set responseCode */
		context.put("responseCode", responseCode.getCode());
		/* set title */
		context.put("title", responseCode.getMessage());
		/* set message */
		context.put("message", message);
		/* set serverURL */
		context.put("serverURL", EdalHttpServer.getServerURL());

		final OutputStreamWriter output = new OutputStreamWriter(teeOutputStream);

		final MergingMessageOutputThread thread = new MergingMessageOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/HtmlMessageTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output, latch);

		DataManager.getVelocityExecutorService().execute(thread);

		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataDirectory} for the landing
	 * page of the HTTP handler.
	 *
	 * @param directory the {@link PrimaryDataDirectory} to present on the landing
	 *                  page.
	 *
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateHtmlForDirectory(final PrimaryDataDirectory directory) throws EdalException {

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set entity */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, directory);
		/* set version */
		context.put(VeloCityHtmlGenerator.STRING_VERSION, directory.getCurrentVersion());
		/* set meta data */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());
		/* set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set some enums **/
		context.put("description", EnumDublinCoreElements.DESCRIPTION);
		context.put("rights", EnumDublinCoreElements.RIGHTS);
		context.put("title", EnumDublinCoreElements.TITLE);
		context.put("creator", EnumDublinCoreElements.CREATOR);
		context.put("format", EnumDublinCoreElements.FORMAT);
		context.put("year", Calendar.YEAR);

		try {
			context.put(VeloCityHtmlGenerator.STRING_ALLOBJECTS, directory.listPrimaryDataEntities());
		} catch (final PrimaryDataDirectoryException e) {
			throw new EdalException("unable to load entity list of the directory", e);
		}

		final StringWriter output = new StringWriter();

		// Velocity.mergeTemplate(
		// "de/ipk_gatersleben/bit/bi/edal/primary_data/DirectoryTemplate.xml",
		// VeloCityHtmlGenerator.CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataDirectory} for the landing
	 * page of the HTTP handler. Special function for the landing page for the
	 * reviewer, who should approve this object.
	 * 
	 * @param directory      the {@link PrimaryDataDirectory} to present on the
	 *                       landing page.
	 * @param reviewerCode   the reviewerCode to identify a reviewer.
	 * @param internalId     the internal ID of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @param identifierType the {@link PersistentIdentifier} of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected OutputStreamWriter generateHtmlForDirectoryForReviewer(final PrimaryDataDirectory directory,
			final long versionNumber, final String internalId, final PersistentIdentifier identifierType,
			final int reviewerCode, final TeeOutputStream teeOutputStream, final CountDownLatch latch)
			throws EdalException {

		Calendar date = null;

		Long publicReferenceDirectorySize = Long.valueOf(0);

		String publicReferenceFileDirectoryNumber = new String();

		try {
			date = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
					.getPublicReferenceByInternalId(internalId).getCreationDate();
		} catch (ReflectiveOperationException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_INITIALIZE_APPROVAL_SERVICE_PROVIDER + e.getMessage(), e);
		} catch (final EdalException e) {
			throw e;
		}

		/**
		 * try if the given PublicReference matches to the given version number,
		 * otherwise send error message
		 */
		try {
			if (!directory.getVersionByDate(date).equals(directory.getVersionByRevisionNumber(versionNumber))) {
				return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
						VeloCityHtmlGenerator.STRING_PUBLIC_REFERENCE_AND_VERSION_NUMBER_ARE_NOT_COMPATIBLE,
						teeOutputStream, latch);
			}
		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_LOAD_VERSIONS_OF + directory + " :" + e.getMessage(), e);
		}

		if (CalculateDirectorySizeThread.directorySizes.containsKey(internalId + "/" + directory.getID())) {
			publicReferenceDirectorySize = CalculateDirectorySizeThread.directorySizes
					.get(internalId + "/" + directory.getID());
		}

		if (CalculateDirectorySizeThread.directoryFiles.containsKey(internalId + "/" + directory.getID())) {
			publicReferenceFileDirectoryNumber = CalculateDirectorySizeThread.directoryFiles
					.get(internalId + "/" + directory.getID());
		}

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		/** set entity for citation */
		context.put(STRING_CITATION_ENTITY, directory);

		/** set identifierType of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_IDENTIFIER_TYPE, identifierType.toString());

		/** set internalId of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_INTERNAL_ID, internalId);

		/** set date of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_DATE, date);

		/** set entity */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, directory);

		/** set meta data */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());

		/** set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set download URL */
		context.put(VeloCityHtmlGenerator.DOWNLOAD_SERVER_URL, EdalHttpServer.getHttpDownloadURL().toString());

		/** set reviewer code */
		context.put(VeloCityHtmlGenerator.STRING_REVIEWER_CODE, String.valueOf(reviewerCode));

		/** set description enum **/
		context.put("description", EnumDublinCoreElements.DESCRIPTION);

		/** set license enum **/
		context.put("rights", EnumDublinCoreElements.RIGHTS);

		/** set publicReferenceSize **/
		context.put("metadatasize",
				DataSize.StorageUnit.of(publicReferenceDirectorySize).format(publicReferenceDirectorySize));

		context.put("SizeList", CalculateDirectorySizeThread.directorySizes);

		context.put("DataSizeClass", DataSize.StorageUnit.class);

		context.put("EnumSize", de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements.SIZE);

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());
		
		List<PrimaryDataEntity> list = null;

		if (!publicReferenceFileDirectoryNumber.isEmpty()) {

			context.put("directorynumber", publicReferenceFileDirectoryNumber.split(",")[0]);

			context.put("filenumber", publicReferenceFileDirectoryNumber.split(",")[1]);
		}

		try {
			list = directory.listPrimaryDataEntities();

			context.put(VeloCityHtmlGenerator.STRING_ALLOBJECTS, list);
		} catch (final PrimaryDataDirectoryException e) {
			throw new EdalException("unable to load entity list of the directory", e);
		}

		addInstituteLogoPathToVelocityContext(context, getCurrentPath());
		
		final OutputStreamWriter output = new OutputStreamWriter(teeOutputStream);

		final MergingEntityOutputThread thread = new MergingEntityOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/DirectoryTemplateForReviewer.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output, latch, list);

		DataManager.getVelocityExecutorService().execute(thread);

		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataDirectory} for the landing
	 * page of the HTTP handler. Special function for the landing page for a
	 * screenshot of a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * 
	 * @param versionNumber  the number of the {@link PrimaryDataEntityVersion} of
	 *                       this {@link PrimaryDataDirectory}.
	 * @param directory      the {@link PrimaryDataDirectory} to present on the
	 *                       landing page.
	 * @param internalId     the internal ID of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @param identifierType the {@link PersistentIdentifier} of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create the HTML output.
	 */
	protected OutputStreamWriter generateHtmlForDirectoryOfSnapshot(final PrimaryDataDirectory directory,
			final long versionNumber, final String internalId, final PersistentIdentifier identifierType,
			final TeeOutputStream teeOutputStream, final CountDownLatch latch) throws EdalException {

		PrimaryDataEntity entityWithPersitentIdentifierForCitation = null;

		if (directory.getPublicReferences().size() == 0) {
			entityWithPersitentIdentifierForCitation = DataManager
					.getPrimaryDataEntityRekursiveForPersistenIdentifier(directory, versionNumber, identifierType);
		}

		Calendar date = null;

		try {
			date = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
					.getPublicReferenceByInternalId(internalId).getCreationDate();
		} catch (ReflectiveOperationException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_INITIALIZE_APPROVAL_SERVICE_PROVIDER + e.getMessage(), e);
		}

		/**
		 * try if the given PublicReference matches to the given version number,
		 * otherwise send error message
		 */
		try {
			if (!directory.getVersionByDate(date).equals(directory.getVersionByRevisionNumber(versionNumber))) {
				return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
						VeloCityHtmlGenerator.STRING_PUBLIC_REFERENCE_AND_VERSION_NUMBER_ARE_NOT_COMPATIBLE,
						teeOutputStream, latch);
			}
		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_LOAD_VERSIONS_OF + directory + " :" + e.getMessage(), e);
		}

		final Logger log = DataManager.getImplProv().getLogger();

		PrimaryDataDirectory currentDirectory = directory;

		PrimaryDataEntityVersion primaryDataEntityVersion = null;

		Long publicReferenceDirectorySize = Long.valueOf(0);

		String publicReferenceFileDirectoryNumber = new String();

		try {
			primaryDataEntityVersion = currentDirectory.getVersionByRevisionNumber(versionNumber);

			boolean foundPublicReference = false;
			try {
				if (primaryDataEntityVersion.getPublicReference(identifierType).getPublicationStatus()
						.equals(PublicationStatus.ACCEPTED)) {

					currentDirectory.switchCurrentVersion(primaryDataEntityVersion);
					/**
					 * set the directory size variable after calculated in separate Thread
					 */
					if (CalculateDirectorySizeThread.directorySizes
							.containsKey(internalId + "/" + currentDirectory.getID())) {
						publicReferenceDirectorySize = CalculateDirectorySizeThread.directorySizes
								.get(internalId + "/" + currentDirectory.getID());
					}
					if (CalculateDirectorySizeThread.directoryFiles
							.containsKey(internalId + "/" + currentDirectory.getID())) {
						publicReferenceFileDirectoryNumber = CalculateDirectorySizeThread.directoryFiles
								.get(internalId + "/" + currentDirectory.getID());
					}

				}

			} catch (final PrimaryDataEntityVersionException e) {
				log.debug(currentDirectory + " has no " + identifierType);
				while (!foundPublicReference) {
					try {
						log.debug("try ParentDirectory '" + currentDirectory.getParentDirectory() + "'");

						if (currentDirectory.getParentDirectory() == null) {
							return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
									VeloCityHtmlGenerator.STRING_NO_PUBLIC_REFERENCE_FOR_THIS_VERSION_SET,
									teeOutputStream, latch);
						}

						if (currentDirectory.getParentDirectory().getVersionByDate(date)
								.getPublicReference(identifierType).getPublicationStatus()
								.equals(PublicationStatus.ACCEPTED)) {

							if (primaryDataEntityVersion.getRevisionDate().before(date)) {

								log.debug(currentDirectory.getParentDirectory() + " has " + identifierType);

								foundPublicReference = true;

								currentDirectory = directory;
								/**
								 * set the directory size variable after calculated in separate Thread
								 */
								if (CalculateDirectorySizeThread.directorySizes
										.containsKey(internalId + "/" + currentDirectory.getID())) {
									publicReferenceDirectorySize = CalculateDirectorySizeThread.directorySizes
											.get(internalId + "/" + currentDirectory.getID());
								}
								if (CalculateDirectorySizeThread.directoryFiles
										.containsKey(internalId + "/" + currentDirectory.getID())) {
									publicReferenceFileDirectoryNumber = CalculateDirectorySizeThread.directoryFiles
											.get(internalId + "/" + currentDirectory.getID());
								}
							} else {
								return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
										VeloCityHtmlGenerator.STRING_NO_PUBLIC_REFERENCE_FOR_THIS_VERSION_SET,
										teeOutputStream, latch);
							}
						}

					} catch (PrimaryDataEntityVersionException | PrimaryDataDirectoryException e1) {

						log.debug("ParentDirectory has no " + identifierType);

						foundPublicReference = false;
						try {
							currentDirectory = currentDirectory.getParentDirectory();
						} catch (final PrimaryDataDirectoryException e2) {
							throw new EdalException("unable to get parent directory: " + e.getMessage(), e);
						}
					}
				}
			}

		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException("unable to get version by version number: " + e.getMessage(), e);
		}

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		/** set entity for citation */
		if (entityWithPersitentIdentifierForCitation != null) {
			context.put(STRING_CITATION_ENTITY, entityWithPersitentIdentifierForCitation);
			/** license URL **/
			try {
				for (Entry<EnumCCLicense, String> entry : EnumCCLicense.enummap.entrySet()) {
					if (entry.getKey().getDescription().equals(entityWithPersitentIdentifierForCitation.getMetaData()
							.getElementValue(EnumDublinCoreElements.RIGHTS).toString().trim())) {
						context.put("licenseURL", entry.getValue());
					}
				}

			} catch (MetaDataException e) {
				e.printStackTrace();
			}
		} else {
			context.put(STRING_CITATION_ENTITY, currentDirectory);
			/** license URL **/
			try {
				for (Entry<EnumCCLicense, String> entry : EnumCCLicense.enummap.entrySet()) {
					if (entry.getKey().getDescription().equals(currentDirectory.getMetaData()
							.getElementValue(EnumDublinCoreElements.RIGHTS).toString().trim())) {
						context.put("licenseURL", entry.getValue());
					}
				}
			} catch (MetaDataException e) {
				e.printStackTrace();
			}
		}

		/** set date of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_DATE, date);

		/** set identifier type of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_IDENTIFIER_TYPE, identifierType.toString());

		/** set internalId of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_INTERNAL_ID, internalId);

		/** set entity */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, currentDirectory);

		/** set metadata */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());

		/** set some enums **/
		context.put("publisher", EnumDublinCoreElements.PUBLISHER);
		context.put("subjects", EnumDublinCoreElements.SUBJECT);
		context.put("creator", EnumDublinCoreElements.CREATOR);
		context.put("contributor", EnumDublinCoreElements.CONTRIBUTOR);
		context.put("language", EnumDublinCoreElements.LANGUAGE);
		context.put("description", EnumDublinCoreElements.DESCRIPTION);
		context.put("rights", EnumDublinCoreElements.RIGHTS);
		context.put("type", EnumDublinCoreElements.TYPE);
		context.put("format", EnumDublinCoreElements.FORMAT);

		/** set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set the ID for schema.org metadata harvesting */
		context.put("SchemaOrgID", EdalHttpServer.getServerURL().toString() + "/" + identifierType.toString() + "/"
				+ internalId + "/" + currentDirectory.getID() + "/" + versionNumber);

		/** set download URL */
		context.put(VeloCityHtmlGenerator.DOWNLOAD_SERVER_URL, EdalHttpServer.getHttpDownloadURL().toString());

		/** set publicReferenceSize **/
		context.put("metadatasize",
				DataSize.StorageUnit.of(publicReferenceDirectorySize).format(publicReferenceDirectorySize));

		context.put("SizeList", CalculateDirectorySizeThread.directorySizes);

		context.put("directorynumber", publicReferenceFileDirectoryNumber.split(",")[0]);

		context.put("DataSizeClass", DataSize.StorageUnit.class);

		context.put("EnumSize", de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements.SIZE);

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());
		
		if (!publicReferenceFileDirectoryNumber.isEmpty()) {

			context.put("directorynumber", publicReferenceFileDirectoryNumber.split(",")[0]);

			context.put("filenumber", publicReferenceFileDirectoryNumber.split(",")[1]);
		}

		if (CalculateDirectorySizeThread.referenceContent.containsKey(internalId)) {

			context.put("referenceDirectoryContent",
					CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[0]);
			context.put("referenceFileContent",
					CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[1]);
			context.put("referenceDataVolume", DataSize.StorageUnit
					.of(Long.valueOf(CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[2]))
					.format(Long.valueOf(CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[2])));
		}

		List<PrimaryDataEntity> list = null;

		try {
			list = currentDirectory.listPrimaryDataEntities();
			context.put(VeloCityHtmlGenerator.STRING_ALLOBJECTS, list);
		} catch (final PrimaryDataDirectoryException e) {
			throw new EdalException("unable to load entity list of the directory", e);
		}

		String currentPath = getCurrentPath();

		addMatomoPathToVelocityContext(context, currentPath);

		addStatementPathToVelocityContext(context, currentPath);

		addInstituteLogoPathToVelocityContext(context, currentPath);
		
		final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(teeOutputStream);

		final MergingEntityOutputThread thread = new MergingEntityOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/DirectoryTemplateForSnapshot.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, outputStreamWriter, latch, list);

		DataManager.getVelocityExecutorService().execute(thread);

		return outputStreamWriter;

	}

	/**
	 * Generate HTML output for an Error message of the HHTP handler.
	 * 
	 * @param responseCode the error code of the response, e.g. 404.
	 * @param message      the error message.
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateHtmlErrorMessage(final HttpStatus.Code responseCode, final String message)
			throws EdalException {

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set responseCode */
		context.put("responseCode", responseCode.getCode());
		/* set title */
		context.put("title", responseCode.getMessage());
		/* set message */
		context.put("message", message);
		/* set serverURL */
		context.put("serverURL", EdalHttpServer.getServerURL());

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());

		addInstituteLogoPathToVelocityContext(context, getCurrentPath());
		
		final StringWriter output = new StringWriter();

		Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/HtmlMessageTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataFile} for the landing page of
	 * the HTTP handler.
	 *
	 * @param file the {@link PrimaryDataFile} to present on the landing page.
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateHtmlForFile(final PrimaryDataFile file) throws EdalException {

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set entity name */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, file);
		/* set version */
		context.put(VeloCityHtmlGenerator.STRING_VERSION, file.getCurrentVersion());
		/* set meta data */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());
		/* set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set some enums **/
		context.put("description", EnumDublinCoreElements.DESCRIPTION);
		context.put("rights", EnumDublinCoreElements.RIGHTS);
		context.put("title", EnumDublinCoreElements.TITLE);
		context.put("creator", EnumDublinCoreElements.CREATOR);
		context.put("format", EnumDublinCoreElements.FORMAT);
		context.put("year", Calendar.YEAR);
		context.put("publisher", EnumDublinCoreElements.PUBLISHER);
		context.put("subjects", EnumDublinCoreElements.SUBJECT);
		context.put("language", EnumDublinCoreElements.LANGUAGE);

		final StringWriter output = new StringWriter();

		Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/FileTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataFile} for the landing page of
	 * the HTTP handler. Special function for the landing page for the reviewer, who
	 * should approve this object.
	 * 
	 * @param file           the {@link PrimaryDataFile} to present on the landing
	 *                       page.
	 * @param reviewerCode   the reviewerCode to identify a reviewer.
	 * @param internalId     the internal ID of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @param identifierType the {@link PersistentIdentifier} of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected OutputStreamWriter generateHtmlForFileForReviewer(final PrimaryDataFile file, final long versionNumber,
			final String internalId, final PersistentIdentifier identifierType, final int reviewerCode,
			final TeeOutputStream teeOutputStream, final CountDownLatch latch) throws EdalException {

		Calendar date = null;

		try {
			date = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
					.getPublicReferenceByInternalId(internalId).getCreationDate();
		} catch (ReflectiveOperationException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_INITIALIZE_APPROVAL_SERVICE_PROVIDER + e.getMessage(), e);
		} catch (final EdalException e) {
			throw e;
		}

		/**
		 * try if the given PublicReference matches to the given version number,
		 * otherwise send error message
		 */
		try {
			if (!file.getVersionByDate(date).equals(file.getVersionByRevisionNumber(versionNumber))) {
				return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
						VeloCityHtmlGenerator.STRING_PUBLIC_REFERENCE_AND_VERSION_NUMBER_ARE_NOT_COMPATIBLE,
						teeOutputStream, latch);
			}
		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_LOAD_VERSIONS_OF + file + " :" + e.getMessage(), e);
		}

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		context.put(STRING_CITATION_ENTITY, file);

		/** set identifierType of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_IDENTIFIER_TYPE, identifierType.toString());

		/** set internalId of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_INTERNAL_ID, internalId);

		/** set date of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_DATE, date);

		/** set entity */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, file);

		/** set meta data */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());

		/** set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set download URL */
		context.put(VeloCityHtmlGenerator.DOWNLOAD_SERVER_URL, EdalHttpServer.getHttpDownloadURL().toString());

		/** set reviewer code */
		context.put(VeloCityHtmlGenerator.STRING_REVIEWER_CODE, String.valueOf(reviewerCode));

		/** set description enum **/
		context.put("description", EnumDublinCoreElements.DESCRIPTION);

		/** set rights enum **/
		context.put("rights", EnumDublinCoreElements.RIGHTS);

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());
		
		addInstituteLogoPathToVelocityContext(context, getCurrentPath());

		final OutputStreamWriter output = new OutputStreamWriter(teeOutputStream);

		final MergingEntityOutputThread thread = new MergingEntityOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/FileTemplateForReviewer.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output, latch, null);

		DataManager.getVelocityExecutorService().execute(thread);

		return output;
	}

	/**
	 * Generate the HTML output of a {@link PrimaryDataFile} for the landing page of
	 * the HTTP handler. Special function for the landing page for a screenshot of a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * 
	 * @param versionNumber  the number of the {@link PrimaryDataEntityVersion} of
	 *                       this {@link PrimaryDataFile}.
	 * @param file           the {@link PrimaryDataFile} to present on the landing
	 *                       page.
	 * @param internalId     the internal ID of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @param identifierType the {@link PersistentIdentifier} of the
	 *                       {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create the HTML output.
	 */
	protected OutputStreamWriter generateHtmlForFileOfSnapshot(final PrimaryDataFile file, final long versionNumber,
			final PersistentIdentifier identifierType, final String internalId, final TeeOutputStream teeOutputStream,
			final CountDownLatch latch) throws EdalException {

		PrimaryDataEntity entityWithPersitentIdentifierForCitation = null;

		if (file.getPublicReferences().size() == 0) {
			entityWithPersitentIdentifierForCitation = DataManager
					.getPrimaryDataEntityRekursiveForPersistenIdentifier(file, versionNumber, identifierType);
		}

		Calendar date = null;

		try {
			date = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
					.getPublicReferenceByInternalId(internalId).getCreationDate();
		} catch (ReflectiveOperationException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_INITIALIZE_APPROVAL_SERVICE_PROVIDER + e.getMessage(), e);
		}

		/**
		 * try if the given PublicReference matches to the given version number,
		 * otherwise send error message
		 */
		try {
			if (!file.getVersionByDate(date).equals(file.getVersionByRevisionNumber(versionNumber))) {

				return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
						VeloCityHtmlGenerator.STRING_PUBLIC_REFERENCE_AND_VERSION_NUMBER_ARE_NOT_COMPATIBLE,
						teeOutputStream, latch);

			}
		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException(
					VeloCityHtmlGenerator.STRING_UNABLE_TO_LOAD_VERSIONS_OF + file + " :" + e.getMessage(), e);
		}

		final Logger log = DataManager.getImplProv().getLogger();

		PrimaryDataFile currentFile = file;

		PrimaryDataEntityVersion primaryDataEntityVersion = null;

		try {
			primaryDataEntityVersion = currentFile.getVersionByRevisionNumber(versionNumber);

			boolean foundPublicReference = false;
			try {
				if (primaryDataEntityVersion.getPublicReference(identifierType).getPublicationStatus()
						.equals(PublicationStatus.ACCEPTED)) {

					currentFile.switchCurrentVersion(primaryDataEntityVersion);
				}

			} catch (final PrimaryDataEntityVersionException e) {
				log.debug(currentFile + " has no " + identifierType);

				PrimaryDataDirectory currentDirectory = null;
				try {
					currentDirectory = currentFile.getParentDirectory();
				} catch (final PrimaryDataDirectoryException e3) {

				}

				while (!foundPublicReference) {
					try {
						log.debug("try ParentDirectory '" + currentDirectory + "'");

						if (currentDirectory == null) {
							return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
									VeloCityHtmlGenerator.STRING_NO_PUBLIC_REFERENCE_FOR_THIS_VERSION_SET,
									teeOutputStream, latch);
						}

						if (currentDirectory.getVersionByDate(date).getPublicReference(identifierType)
								.getPublicationStatus().equals(PublicationStatus.ACCEPTED)) {

							if (primaryDataEntityVersion.getRevisionDate().before(date)) {
								log.debug(currentDirectory + " has " + identifierType);
								foundPublicReference = true;

								currentFile = file;
							} else {
								return this.generateEmbeddedHtmlForErrorMessage(HttpStatus.Code.NOT_FOUND,
										VeloCityHtmlGenerator.STRING_NO_PUBLIC_REFERENCE_FOR_THIS_VERSION_SET,
										teeOutputStream, latch);
							}
						}

					} catch (final PrimaryDataEntityVersionException e1) {

						log.debug("ParentDirectory has no " + identifierType);

						foundPublicReference = false;
						try {

							if (currentDirectory.getParentDirectory() == null) {
								throw new EdalException("root Directory arrived -> no reference found" + e.getMessage(),
										e);
							} else {
								currentDirectory = currentDirectory.getParentDirectory();
							}
						} catch (final PrimaryDataDirectoryException e2) {
							throw new EdalException("unable to get parent directory: " + e.getMessage(), e);
						}
					}
				}
			}

		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException("unable to get version by revision number: " + e.getMessage(), e);
		}

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());

		/** set entity for citation */
		if (entityWithPersitentIdentifierForCitation != null) {
			context.put(STRING_CITATION_ENTITY, entityWithPersitentIdentifierForCitation);
			/** license URL **/
			try {
				for (Entry<EnumCCLicense, String> entry : EnumCCLicense.enummap.entrySet()) {
					if (entry.getKey().getDescription().equals(entityWithPersitentIdentifierForCitation.getMetaData()
							.getElementValue(EnumDublinCoreElements.RIGHTS).toString().trim())) {
						context.put("licenseURL", entry.getValue());
					}
				}
			} catch (MetaDataException e) {
				e.printStackTrace();
			}
		} else {
			context.put(STRING_CITATION_ENTITY, file);
			/** license URL **/
			try {
				for (Entry<EnumCCLicense, String> entry : EnumCCLicense.enummap.entrySet()) {
					if (entry.getKey().getDescription().equals(
							file.getMetaData().getElementValue(EnumDublinCoreElements.RIGHTS).toString().trim())) {
						context.put("licenseURL", entry.getValue());
					}
				}
			} catch (MetaDataException e) {
				e.printStackTrace();
			}
		}
		/** set identifier type */
		context.put(VeloCityHtmlGenerator.STRING_IDENTIFIER_TYPE, identifierType.toString());

		/** set entity name */
		context.put(VeloCityHtmlGenerator.STRING_ENTITY, file);

		/** set date of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_DATE, date);

		/** set metadata */
		context.put(VeloCityHtmlGenerator.STRING_ALL_ELEMENTS, MetaData.ELEMENT_TYPE_MAP.keySet());

		/** set server URL */
		context.put(VeloCityHtmlGenerator.STRING_SERVER_URL, EdalHttpServer.getServerURL().toString());

		/** set the ID for schema.org metadata harvesting */
		context.put("SchemaOrgID", EdalHttpServer.getServerURL().toString() + "/" + identifierType.toString() + "/"
				+ internalId + "/" + file.getID() + "/" + versionNumber);

		/** set download URL */
		context.put(VeloCityHtmlGenerator.DOWNLOAD_SERVER_URL, EdalHttpServer.getHttpDownloadURL().toString());

		/** set internalId of the PublicReference */
		context.put(VeloCityHtmlGenerator.STRING_INTERNAL_ID, internalId);

		/** set some enums **/
		context.put("publisher", EnumDublinCoreElements.PUBLISHER);
		context.put("subjects", EnumDublinCoreElements.SUBJECT);
		context.put("creator", EnumDublinCoreElements.CREATOR);
		context.put("contributor", EnumDublinCoreElements.CONTRIBUTOR);
		context.put("language", EnumDublinCoreElements.LANGUAGE);
		context.put("description", EnumDublinCoreElements.DESCRIPTION);
		context.put("rights", EnumDublinCoreElements.RIGHTS);
		context.put("type", EnumDublinCoreElements.TYPE);
		context.put("format", EnumDublinCoreElements.FORMAT);

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());
		
		if (CalculateDirectorySizeThread.referenceContent.containsKey(internalId)) {

			context.put("referenceDirectoryContent",
					CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[0]);
			context.put("referenceFileContent",
					CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[1]);

			context.put("referenceDataVolume", DataSize.StorageUnit
					.of(Long.valueOf(CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[2]))
					.format(Long.valueOf(CalculateDirectorySizeThread.referenceContent.get(internalId).split(",")[2])));
		}

		String currentPath = getCurrentPath();

		addMatomoPathToVelocityContext(context, currentPath);

		addStatementPathToVelocityContext(context, currentPath);

		addInstituteLogoPathToVelocityContext(context, currentPath);
		
		final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(teeOutputStream);

		final MergingEntityOutputThread thread = new MergingEntityOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/FileTemplateForSnapshot.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, outputStreamWriter, latch, null);

		DataManager.getVelocityExecutorService().execute(thread);

		return outputStreamWriter;
	}

	/**
	 * @param context
	 * @param currentPath
	 */
	private void addStatementPathToVelocityContext(final VelocityContext context, String currentPath) {
		Path statementPath = Paths.get(currentPath, "StatementTemplate.txt");

		if (Files.exists(statementPath)) {
			try {
				context.put("StatementTemplate", new String(Files.readAllBytes(statementPath)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (Files.exists(FileUtils.getFile("StatementTemplate.txt").toPath())) {
			try {
				context.put("StatementTemplate",
						FileUtils.readFileToString(FileUtils.getFile("StatementTemplate.txt"), "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DataManager.getImplProv().getLogger().warn("Unable to find 'StatementTemplate.txt'");
		}
	}

	/**
	 * @param context
	 * @param currentPath
	 */
	private void addInstituteLogoPathToVelocityContext(final VelocityContext context, String currentPath) {
		Path institutePath = Paths.get(currentPath, "institute_logo.png");
		if (Files.exists(institutePath)) {
			context.put("InstituteLogo", institutePath);
		} else if (Files.exists(FileUtils.getFile("institute_logo.png").toPath())) {
			context.put("InstituteLogo", FileUtils.getFile("institute_logo.png").getPath());
		} else {
			DataManager.getImplProv().getLogger().debug("Unable to find 'institute_logo.png'");
		}
	}

	/**
	 * @param context
	 * @param currentPath
	 */
	private void addMatomoPathToVelocityContext(final VelocityContext context, String currentPath) {
		Path matomoPath = Paths.get(currentPath, "MatomoTemplate.xml");

		if (Files.exists(matomoPath)) {
			try {
				context.put("MatomoTemplate", new String(Files.readAllBytes(matomoPath)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (Files.exists(FileUtils.getFile("MatomoTemplate.xml").toPath())) {
			try {
				context.put("MatomoTemplate",
						FileUtils.readFileToString(FileUtils.getFile("MatomoTemplate.xml"), "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DataManager.getImplProv().getLogger().warn("Unable to find 'MatomoTemplate.xml'");
		}
	}

	@SuppressWarnings("unchecked")
	protected OutputStreamWriter generateHtmlForReport(final HttpStatus.Code responseCode,
			final OutputStream outputStream, final CountDownLatch latch) throws Exception {

		JSONArray finalArray = new JSONArray();

		final Map<String, HashSet<String>> accessMap = new HashMap<String, HashSet<String>>();

		final Map<String, String[]> accessStatistic = new TreeMap<String, String[]>();
		
		VeloCityHtmlGenerator.ipMap.clear();

		VeloCityHtmlGenerator.downloadedVolume.clear();;

		VeloCityHtmlGenerator.uniqueAccessNumbers.clear();;

		final Path pathToLogFiles = Paths.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(),
				"jetty_log");

		for (final File file : pathToLogFiles.toFile().listFiles()) {

			final FileInputStream is = new FileInputStream(file);
			final BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String strLine;

			while ((strLine = br.readLine()) != null) {

				final String[] split = strLine.split("\t");

				if (split[5].startsWith("GET /DOI/")) {

					final String publicReferenceId = split[5].split("/")[2];

					if (publicReferenceId.length() == 36) {

						if (split[5].endsWith("ZIP HTTP/1.1") && split[6].equals("200")) {

							final String directoryId = split[5].split("/")[3];

							if (CalculateDirectorySizeThread.directorySizes
									.containsKey(publicReferenceId + "/" + directoryId)) {

								if (downloadedVolume.containsKey(publicReferenceId)) {
									downloadedVolume.put(publicReferenceId,
											downloadedVolume.get(publicReferenceId)
													+ CalculateDirectorySizeThread.directorySizes
															.get(publicReferenceId + "/" + directoryId));
								} else {
									downloadedVolume.put(publicReferenceId, CalculateDirectorySizeThread.directorySizes
											.get(publicReferenceId + "/" + directoryId));
								}

							}
						} else {
							if (downloadedVolume.containsKey(publicReferenceId)) {
								downloadedVolume.put(publicReferenceId,
										downloadedVolume.get(publicReferenceId) + Long.parseLong(split[7]));
							} else {
								downloadedVolume.put(publicReferenceId, Long.parseLong(split[7]));
							}
						}

						final String ipAddress = split[1];

						if (accessMap.containsKey(ipAddress)) {
							accessMap.get(ipAddress).add(publicReferenceId);
						} else {
							accessMap.put(ipAddress, new HashSet<String>(Arrays.asList(publicReferenceId)));
						}

						if (ipMap.containsKey(publicReferenceId)) {
							ipMap.get(publicReferenceId).add(ipAddress);

						} else {
							ipMap.put(publicReferenceId, new HashSet<String>(Arrays.asList(ipAddress)));
						}

					}
				}

			}
			br.close();
			is.close();

		}

		for (final Entry<String, HashSet<String>> ipToPublicReferenceEntry : accessMap.entrySet()) {
			for (final String publicReferenceID : ipToPublicReferenceEntry.getValue()) {
				if (uniqueAccessNumbers.containsKey(publicReferenceID)) {
					uniqueAccessNumbers.put(publicReferenceID, uniqueAccessNumbers.get(publicReferenceID) + 1);
				} else {
					uniqueAccessNumbers.put(publicReferenceID, Long.valueOf(1));
				}
			}
		}

		for (final Entry<String, Long> entry : uniqueAccessNumbers.entrySet()) {

			PublicReference reference = null;

			try {
				final ApprovalServiceProvider appService = DataManager.getImplProv().getApprovalServiceProvider()
						.getDeclaredConstructor().newInstance();
				reference = appService.getPublicReferenceByInternalId(entry.getKey());

			} catch (ReflectiveOperationException | EdalException e) {
				// e.printStackTrace();
			}

			if (reference != null && reference.getAssignedID() != null) {

				accessStatistic.put(reference.getAssignedID(),
						new String[] { reference.getVersion().getMetaData().toString(),
								String.valueOf(entry.getValue()), String.valueOf(downloadedVolume.get(entry.getKey())),
								GenerateLocations.generateGpsLocations(ipMap.get(reference.getInternalID())) });

				JSONObject obj = new JSONObject();
				obj.put("year", reference.getAcceptedDate().get(Calendar.YEAR));
				obj.put("doi", reference.getAssignedID());
				obj.put("title", reference.getVersion().getMetaData().toString());
				obj.put("downloads", String.valueOf(downloadedVolume.get(entry.getKey())));
				obj.put("accesses", String.valueOf(entry.getValue()));
				obj.put("locations",
						GenerateLocations.generateGpsLocationsToJson(ipMap.get(reference.getInternalID())));

				finalArray.add(obj);

			}
		}

		final List<String> statisticList = new ArrayList<String>();

		long totalAccesses = 0;

		long totalDownloadVolume = 0;

		for (final Entry<String, String[]> entry : accessStatistic.entrySet()) {

			final String size = entry.getValue()[2] != null ? DataSize.StorageUnit.of(Long.valueOf(entry.getValue()[2]))
					.format(Long.valueOf(entry.getValue()[2])) : "0";

			statisticList.add(entry.getKey() + "\t" + entry.getValue()[0] + "\t" + entry.getValue()[1] + "\t" + size
					+ "\t" + entry.getValue()[3]);

			totalAccesses = totalAccesses + Long.valueOf(entry.getValue()[1]);

			totalDownloadVolume = totalDownloadVolume + Long.valueOf(entry.getValue()[2]);

		}

		final String totalDownloadVolumeString = DataSize.StorageUnit.of(Long.valueOf(totalDownloadVolume))
				.format(totalDownloadVolume);

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set responseCode */
		context.put("responseCode", responseCode.getCode());
		/* set title */
		context.put("title", "Request-Statistics");
		/* set accessStatistic */
		context.put("accessStatistic", statisticList);
		/* set accessStatistic */
		context.put("json", finalArray.toString().replace("\\/", "/"));
		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());
		/* set publisherURL */
		context.put("publisherUrl", DataManager.getConfiguration().getPublisherURL());
		/* set serverURL */
		context.put("serverURL", EdalHttpServer.getServerURL());
		/* set number of DOIs */
		context.put("dois", statisticList.size());

		/* set number of total accesses */
		context.put("totalAccesses", totalAccesses);
		/* set number of total downloaded volume */
		context.put("totalDownloadVolume", totalDownloadVolumeString);

		/* value for total data volume of all stored entities */
		final DataSize size = new DataSize(CalculateDirectorySizeThread.totalVolumeDataStock);

		context.put("datastock", size.toHTML());

		/* value for total file number of all stored entities */
		final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
		context.put("filenumber", numberFormat.format(ServiceProviderImplementation.totalNumberOfFiles));

		/* value for total number of users */
		context.put("users", DataManager.getImplProv().getServiceProvider().getDeclaredConstructor().newInstance()
				.getNumberOfUsers());

		/* all IP map */
		context.put("jsonall", GenerateLocations.getAllIPsList());

		String currentPath = getCurrentPath();

		addMatomoPathToVelocityContext(context, currentPath);

		addInstituteLogoPathToVelocityContext(context, currentPath);

		final OutputStreamWriter output = new OutputStreamWriter(outputStream);

		final MergingReportOutputThread thread = new MergingReportOutputThread(
				"de/ipk_gatersleben/bit/bi/edal/primary_data/ReportTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output, latch);

		DataManager.getVelocityExecutorService().execute(thread);

		// String s = finalArray.toString().replace("\\/", "/");
		//
		// System.out.println(s);

		return output;

	}
	
	/**
	 * Generate HTML output for an Error message of the HHTP handler.
	 * 
	 * @param responseCode the error code of the response, e.g. 404.
	 * @param responseCode2      the error message.
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException if unable to create output.
	 */
	protected StringWriter generateHtmlForSearch(final HttpStatus.Code responseCode, final Code responseCode2)
			throws EdalException {

		final VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		context.put("responseCode", responseCode.getCode());
		/* set serverURL */
		context.put("serverURL", EdalHttpServer.getServerURL());
		
		context.put("filetypes", NativeLuceneIndexWriterThread.getTerms());

		/* set instance name long */
		context.put("repositoryNameLong", DataManager.getConfiguration().getInstanceNameLong());
		/* set instance name short */
		context.put("repositoryNameShort", DataManager.getConfiguration().getInstanceNameShort());

		addInstituteLogoPathToVelocityContext(context, getCurrentPath());
		
		final StringWriter output = new StringWriter();

		Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/SearchTemplate.xml",
				VeloCityHtmlGenerator.DEFAULT_CHARSET.toString(), context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException(VeloCityHtmlGenerator.STRING_UNABLE_TO_WRITE_HTML_OUTPUT, e);
		}
		return output;
	}

	/**
	 * @return
	 */
	private String getCurrentPath() {
		String currentPath = VeloCityHtmlGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath();

		try {
			currentPath = URLDecoder.decode(currentPath, "UTF-8");
			currentPath = currentPath.substring(1, currentPath.lastIndexOf("/"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return currentPath;
	}

	public Object generatePublicReferenceLatestStatusResponse(Code responseCode) {
		try {
			final ServiceProvider serviceProvider = DataManager.getImplProv().getServiceProvider()
					.getDeclaredConstructor().newInstance();

			String output = serviceProvider.getLatestPersistentIdentifierStatus();

			return output;

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("doi", "");
		json.put("date", "");
		return json.toJSONString();
	}


}