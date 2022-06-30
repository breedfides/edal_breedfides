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
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.LocaleUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumCCLicense;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * Implements the logic of all Functions that are used in the REST data submission endpoints
 * @author ralfs
 *
 */
public class DataSubmissionService {
	
	/**
	 * For testing if there already is a PrimaryDataEntity in the root Directory with the given name associated with this email
	 * @param email The email for identification
	 * @param name The name that will be used to look for an already existing entity
	 * @return true if an entity with this name already exists, else false
	 */
	public boolean checkIfExists(String email, String name) {
		try {
			//decode subject
			InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(email));		
			ObjectInputStream oi = new ObjectInputStream(in);
			Subject subject = (Subject) oi.readObject();
			PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			//try to obtain an entity with the given name
			root.getPrimaryDataEntity(name);
			return true;
		} catch (IOException | ClassNotFoundException | PrimaryDataDirectoryException e) {
			return false;
		}
	}
	
	/**
	 * For dataset creation with metadata. 
	 * @param metadataString A JSON formatted String with metadata attributes
	 * @param email A base64 encoded email
	 * @param entityName The esired PrimaryDataDirectory name
	 * @return Status code
	 */
	public Response uploadEntityAndMetadata(String metadataString,String email, String entityName) {
		try {
			//decode subject
			InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(email));		
			ObjectInputStream oi = new ObjectInputStream(in);
			Subject subject = (Subject) oi.readObject();	
			
			Object metadataObject = new JSONParser().parse(metadataString);
			if(metadataObject instanceof JSONObject) {
				MetaDataWrapper metadataWrapper = new MetaDataWrapper((JSONObject) metadataObject);
				PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
				PrimaryDataDirectory newDataSet = root.createPrimaryDataDirectory(metadataWrapper.getTitle().toString());
				MetaData metadata = newDataSet.getMetaData().clone();
				metadata.setElementValue(EnumDublinCoreElements.TITLE,metadataWrapper.getTitle());
				metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION,metadataWrapper.getDescription());
				metadata.setElementValue(EnumDublinCoreElements.SUBJECT, metadataWrapper.getSubjects());
				metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, metadataWrapper.getLanguage());
				metadata.setElementValue(EnumDublinCoreElements.CREATOR, metadataWrapper.getCreators());
				metadata.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, metadataWrapper.getContributors());
				metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, metadataWrapper.getLegalpersons());
				metadata.setElementValue(EnumDublinCoreElements.RIGHTS, metadataWrapper.getLicense());						
				newDataSet.setMetaData(metadata);
				return Response.status(Status.OK).build();
			}else {
				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}
		} catch (ParseException | IOException | ClassNotFoundException | PrimaryDataDirectoryException | ORCIDException | MetaDataException | CloneNotSupportedException | PrimaryDataEntityVersionException e) {
			DataManager.getImplProv().getLogger().debug("Error while uploading a entity with metadata via REST: "+e.getMessage());
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}

	/**
	 * REST path to Search for an ORCID
	 * @param firstName The given firstName
	 * @param lastName The given lastName
	 * @return A JSONArray with found ids, empty if no ids were found
	 */
	public JSONArray searchORCID(String firstName, String lastName) {
		JSONArray idArray = new JSONArray();
		try {
			List<ORCID> ids =  ORCID.getOrcidsByName(firstName, lastName);
			for(ORCID id : ids) {
				idArray.add(id.getOrcid());
			}
			return idArray;
		} catch (ORCIDException e) {
			return idArray;
		}
	}
	
	/**
	 * REST path to check the progress of a file upload.
	 * @param key The key (constructed on client side constructed with the base64 encoded email + file path )
	 * @return If there is a progress with the given name the return value is an int between 0 and 100, else -1
	 */	
	public int getProgress( String key) {
		if(ProgressionMap.getInstance().getMap().get(key) != null){
			int result = ProgressionMap.getInstance().getMap().get(key);	
			if(result >= 100) {
				ProgressionMap.getInstance().getMap().remove(key);
			}
			return result;	
		}else {
			return -1;
		}
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
	public Response uploadEntity(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("path") String path, @FormDataParam("email") String email, @FormDataParam("type") String type,
			@FormDataParam("datasetRoot") String datasetRoot, @FormDataParam("size") String size)  {
		String[] pathArray = path.split("/");
		try {
			InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(email));		
			ObjectInputStream oi = new ObjectInputStream(in);
			Subject subject = (Subject) oi.readObject();	
			
			// data = (JSONObject) new JSONParser().parse(request);
			// String type = (String) data.get("type");
			PrimaryDataDirectory managerRoot = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			PrimaryDataDirectory root = (PrimaryDataDirectory) managerRoot.getPrimaryDataEntity(datasetRoot);
			//if file child of directory -> walk Tree and find the parent of this child
			if (pathArray.length > 0) {
				PrimaryDataDirectory parent = root;
				int lastIndex = pathArray.length - 1;
				for (int i = 0; i < lastIndex; i++) {
					parent = (PrimaryDataDirectory) parent.getPrimaryDataEntity(pathArray[i]);
				}
				if (type.equals("Directory")) {
					System.out.println("Created:");
					System.out.println(Arrays.toString(pathArray));
					parent.createPrimaryDataDirectory(pathArray[lastIndex]);
				} else {
					PrimaryDataFile file = parent.createPrimaryDataFile(pathArray[pathArray.length - 1]);
					System.out.println("Starting store function __");
					try {				
						ProgressInputStream pis = new ProgressInputStream(ProgressionMap.getInstance().getMap(), uploadedInputStream, Long.parseLong(size), email+path);
						file.store(pis);
						System.out.println("finished store function __");

					} catch (PrimaryDataFileException e) {
						DataManager.getImplProv().getLogger().debug("Error while uploading a entity with a file via REST: "+e.getMessage());					}
				}
			} else {
				return Response.status(Status.BAD_REQUEST).build();
			}
			return Response.status(Status.OK).build();
		} catch (PrimaryDataDirectoryException | PrimaryDataEntityVersionException | IOException e) {
			DataManager.getImplProv().getLogger().debug("Error while uploading a entity via REST: "+e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (ClassNotFoundException e1) {
			DataManager.getImplProv().getLogger().debug("Error couldnt load a class while uploading a entity with metadata via REST: "+e1.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
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
	public Response publishDataset(@FormDataParam("email") String emailEncoded,@FormDataParam("name") String name, @FormDataParam("embargo") String embargo) throws PrimaryDataDirectoryException, AddressException,
			PublicReferenceException, PrimaryDataEntityException, ParseException {
		InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(emailEncoded));		
		ObjectInputStream oi;
		try {
			oi = new ObjectInputStream(in);
			Subject subject = (Subject) oi.readObject();	
			PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			PrimaryDataDirectory entity = (PrimaryDataDirectory) root.getPrimaryDataEntity(name);
			String email = "null";
			for(Principal p : subject.getPrincipals()) {
				email = p.getName();
				break;
			}
			entity.addPublicReference(PersistentIdentifier.DOI);
			if(embargo != null) {
				Calendar release = Calendar.getInstance();
				try {
					release.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(embargo));
					entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress(email), release);
					return Response.status(Status.OK).build();
				} catch (java.text.ParseException e) {
					DataManager.getImplProv().getLogger().debug("Error while parsing an embargo data: "+e.getMessage());
				}
			}
			entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress(email));
			return Response.status(Status.OK).build();
		}catch (IOException | ClassNotFoundException e) {
			DataManager.getImplProv().getLogger().debug("Error while publishing a entity via REST: "+e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}

