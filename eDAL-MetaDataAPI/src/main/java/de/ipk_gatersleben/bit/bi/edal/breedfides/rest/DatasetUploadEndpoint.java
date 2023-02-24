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
package de.ipk_gatersleben.bit.bi.edal.breedfides.rest;

import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.ipk_gatersleben.bit.bi.edal.breedfides.certificate.JwtValidator;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * REST endpoints for relevant function to upload data to the BreedFides portal
 * 
 * @author arendd
 *
 */
@javax.ws.rs.Path("upload")
public class DatasetUploadEndpoint {

	private static final String CREATOR = "Creator";
	private static final String CONTRIBUTOR = "Conributor";

	@GET
	@javax.ws.rs.Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {

		InfoEndpoint.getLogger().info("Call 'upload/info/' endpoint");

		return "Call 'upload/info/' endpoint";
	}

	@POST
	@javax.ws.rs.Path("/datasets")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadMetaData(@Context HttpServletRequest request,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("path") String path,
			@FormDataParam("metaData") String metaData) {

		String fileName = fileDetail.getFileName();

		String filePath = path;

		InfoEndpoint.getLogger().info("Call 'upload/datasets/' endpoint");

		InfoEndpoint.getLogger().info("FileName: " + fileName);

		InfoEndpoint.getLogger().info("FilePath: " + filePath);

		if (request.getHeader("Authorization") != null) {

			// cut of "Bearer"
			String jwt = request.getHeader("Authorization").substring(7);

			InfoEndpoint.getLogger().info(
					"Got JWT '" + jwt.substring(0, 3) + "..." + jwt.substring(jwt.length() - 3, jwt.length()) + "'");

			String serialNumber = null;

			try {
				JwtValidator.validate(jwt);
				serialNumber = JwtValidator.getSerialNumber(jwt);
				InfoEndpoint.getLogger().info("SerialNumber of JWT: " + serialNumber);

			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Given JWT is invalid: " + e.getMessage())
						.build();
			}

			InfoEndpoint.getLogger().info("Got Metadata : " + metaData);

//			try {
//				MetaData metadata = convertJsonMetadata(dataFile.getMetaData(), metaData);
//				// ToDo: set MetaData to
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			///////////////

			try {
				FileSystemImplementationProvider filesystemImplementationProvider = ((FileSystemImplementationProvider) DataManager
						.getImplProv());

				PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(filesystemImplementationProvider,
						EdalHelpers.authenticateUserWithJWT(serialNumber));

				InfoEndpoint.getLogger().info("Got root directory: " + rootDirectory);

				String[] parentDirectories = filePath.split("/");
				
				PrimaryDataDirectory currentRootDirectory = rootDirectory;
				PrimaryDataDirectory currentDirectory = null;

				// get or create user directory

				if (currentRootDirectory.exist(serialNumber)) {
					currentDirectory = (PrimaryDataDirectory) currentRootDirectory.getPrimaryDataEntity(serialNumber);

					currentRootDirectory = currentDirectory;
					InfoEndpoint.getLogger().info("Primary User Directory already exists : " + serialNumber);

				} else {
					currentDirectory = currentRootDirectory.createPrimaryDataDirectory(serialNumber);
					currentRootDirectory = currentDirectory;
					InfoEndpoint.getLogger().info("Primary User Directory : " + serialNumber);

				}

				// get or create actual file path

				for (String directory : parentDirectories) {

					if (currentRootDirectory.exist(directory)) {
						currentDirectory = (PrimaryDataDirectory) currentRootDirectory.getPrimaryDataEntity(directory);
						currentDirectory.setMetaData(convertJsonMetadata(currentDirectory.getMetaData(), metaData));
						currentRootDirectory = currentDirectory;
						InfoEndpoint.getLogger().info("ParentDirectory already exists : " + directory);
					} else {
						currentDirectory = currentRootDirectory.createPrimaryDataDirectory(directory);
						currentDirectory.setMetaData(convertJsonMetadata(currentDirectory.getMetaData(), metaData));
						currentRootDirectory = currentDirectory;
						InfoEndpoint.getLogger().info("ParentDirectory was created : " + directory);

					}
				}

				PrimaryDataFile dataFile = null;

				if (currentDirectory.exist(fileName)) {
					dataFile = (PrimaryDataFile) currentDirectory.getPrimaryDataEntity(fileName);
					dataFile.setMetaData(convertJsonMetadata(dataFile.getMetaData(), metaData));
					dataFile.store(fileInputStream);
					InfoEndpoint.getLogger().info("File already exists : " + fileName);
				} else {
					dataFile = currentDirectory.createPrimaryDataFile(fileName);
					dataFile.setMetaData(convertJsonMetadata(dataFile.getMetaData(), metaData));
					dataFile.store(fileInputStream);
					InfoEndpoint.getLogger().info("File was created : " + fileName);

				}

				return Response.status(200).build();
			} catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();

			}
		} else {
			return Response.status(Status.UNAUTHORIZED.getStatusCode(), "Please check your Authorizaton Header")
					.build();

		}
	}

	private MetaData convertJsonMetadata(MetaData newMetadata, String metadata) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		try {

			JsonNode rootNode = objectMapper.readTree(metadata);
			JsonNode descriptionNode = rootNode.path("description");
			JsonNode subjectsNode = rootNode.path("subjects");
			JsonNode authorsNode = rootNode.path("authors");
			JsonNode languageNode = rootNode.path("language");

			InfoEndpoint.getLogger().info("description = " + descriptionNode.asText());
			InfoEndpoint.getLogger().info("subjects = " + (ArrayNode) rootNode.path("subjects"));
			InfoEndpoint.getLogger().info("authors = " + (ArrayNode) rootNode.path("authors"));
			InfoEndpoint.getLogger().info("language = " + languageNode.asText());

			// description
			newMetadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(descriptionNode.asText()));
			//

			// subjects
			Iterator<JsonNode> subjects = subjectsNode.elements();
			Subjects metadataSubjects = new Subjects();

			while (subjects.hasNext()) {
				metadataSubjects.add(new UntypedData(subjects.next().asText()));
			}
			newMetadata.setElementValue(EnumDublinCoreElements.SUBJECT, metadataSubjects);
			//

			// authors
			Iterator<JsonNode> authors = authorsNode.elements();
			Persons creatorPersons = new Persons();
			Persons contributorPersons = new Persons();

			while (authors.hasNext()) {

				JsonNode authorNode = authors.next();

				JsonNode countryNode = authorNode.path("country");
				JsonNode zipNode = authorNode.path("zip");
				JsonNode addressNode = authorNode.path("address");

				Person person = null;

				if (authorNode.has("legalName")) {
					JsonNode legalNameNode = authorNode.path("legalName");

					person = new LegalPerson(legalNameNode.asText(), addressNode.asText(), zipNode.asText(),
							countryNode.asText());

					InfoEndpoint.getLogger().info("author = " + (LegalPerson) person);

				} else if (authorNode.has("firstName")) {

					JsonNode lastNameNode = authorNode.path("lastName");
					JsonNode firstNameNode = authorNode.path("firstName");

					if (authorNode.has("orcid")) {
						JsonNode orcidNode = authorNode.path("orcid");

						person = new NaturalPerson(firstNameNode.asText(), lastNameNode.asText(), addressNode.asText(),
								zipNode.asText(), countryNode.asText(), new ORCID(orcidNode.asText()));
					} else {

						person = new NaturalPerson(firstNameNode.asText(), lastNameNode.asText(), addressNode.asText(),
								zipNode.asText(), countryNode.asText());
					}
					InfoEndpoint.getLogger().info("author = " + (NaturalPerson) person);

				}

				JsonNode roleNode = authorNode.path("role");

				if (roleNode.asText().equals(CREATOR)) {
					creatorPersons.add(person);
				} else if (roleNode.asText().equals(CONTRIBUTOR)) {
					contributorPersons.add(person);
				}

			}

			newMetadata.setElementValue(EnumDublinCoreElements.CREATOR, creatorPersons);
			newMetadata.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, contributorPersons);
			//

			return newMetadata;

		} catch (JsonProcessingException e) {
			throw new Exception("Processing Metadata Json failed : " + e.getMessage());
		} catch (MetaDataException e) {
			throw new Exception("Creating eDAL metadata failed : " + e.getMessage());
		}

	}

}
