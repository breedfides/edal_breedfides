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
package de.ipk_gatersleben.bit.bi.edal.rest.server;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.LocaleUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.ElixirPrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SimpleCallbackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumCCLicense;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DateType;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import javafx.application.Platform;


@Path("api")
public class RestEntryPoint {


	
	/**
	 * REST path For testing if there already is a PrimaryDataEntity in the root Directory with the given name associated with this email
	 * @param email The email base64 encoded for identification
	 * @param name The name that will be used to look for an already existing entity
	 * @return true if an entity with this name already exists, else false
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkIfExists")
	@POST
	public Boolean checkIfExists(@FormDataParam("email") String email,@FormDataParam("name") String name) {
		return new DataSubmissionService().checkIfExists(email, name);			
	}
	
	/**
	 * For dataset creation with metadata. 
	 * @param metadataString A JSON formatted String with metadata attributes
	 * @param email A base64 encoded email
	 * @param entityName The esired PrimaryDataDirectory name
	 * @return Status code
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("uploadEntityAndMetadata")
	@POST
	public Response uploadEntityAndMetadata(@FormDataParam("metadata") String metadataString, @FormDataParam("email") String email,@FormDataParam("name") String entityName) {
		return new DataSubmissionService().uploadEntityAndMetadata(metadataString, email, entityName);			
	}
	
	/**
	 * REST path to Search for an ORCID
	 * @param firstName The given firstName
	 * @param lastName The given lastName
	 * @return A JSONArray with found ids, empty if no ids were found
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchORCID/{firstName}/{lastName}")
	@POST
	public JSONArray searchORCID(@PathParam("firstName") String firstName, @PathParam("lastName") String lastName) {
		return new DataSubmissionService().searchORCID(firstName, lastName);
				
	}
	
	/**
	 * REST path to check the progress of a file upload.
	 * @param key The key (constructed on client side constructed with the base64 encoded email + file path )
	 * @return If there is a progress with the given name the return value is an int between 0 and 100, else -1
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("getProgress")
	@POST
	public int getProgress(@FormDataParam("key") String key) {
		return new DataSubmissionService().getProgress(key);		
	}
	
	
	
	/**
	 * 
	 * @param uploadedInputStream The optional InputStream of a file
	 * @param path The full path of an file/directory
	 * @param email The base64 encoded email of a user
	 * @param type The type of the desired entity, File or Directory
	 * @param datasetRoot The root title of the parent dataset
	 * @param size Size of the optional file
	 * @return The Status code
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("uploadEntity")
	@POST
	public Response uploadEntity(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("path") String path, @FormDataParam("email") String email, @FormDataParam("type") String type,
			@FormDataParam("datasetRoot") String datasetRoot, @FormDataParam("size") String size)  {
		return new DataSubmissionService().uploadEntity(uploadedInputStream, path, email, type, datasetRoot, size);	
	}

	
	/**
	 * REST path to set a given dataset public.
	 * @param emailEncoded The base64 encoded email of a user
	 * @param name The name of the dataset
	 * @param embargo An optional embargo date, to delay the publication of the associated DOIs
	 * @return The Status code
	 * @throws PrimaryDataDirectoryException if unable to create the root Directory
	 * @throws AddressException If there was a problem creating the InternetAddress objects
	 * @throws PublicReferenceException if unable to request the PublicReference to setpublic.
	 * @throws PrimaryDataEntityException if unable to add the PublicReference to this PrimaryDataEntity
	 * @throws ParseException if there was a problem parsing the embargo date
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@Path("publishDataset")
	@POST
	public Response publishDataset(@FormDataParam("email") String emailEncoded,@FormDataParam("name") String name, @FormDataParam("embargo") String embargo) throws PrimaryDataDirectoryException, AddressException,
			PublicReferenceException, PrimaryDataEntityException, ParseException {
		return new DataSubmissionService().publishDataset(emailEncoded, name, embargo);
	}

}

@XmlRootElement
class ServerInfo {
	public String server;
}


