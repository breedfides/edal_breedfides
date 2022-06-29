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
	public String uploadEntityAndMetadata(String metadataString,String email, String entityName) {
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
				System.out.println("created root with name: "+metadataWrapper.getTitle().toString());
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
				return "200";
			}else {
				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}
		} catch (ParseException | IOException | ClassNotFoundException | PrimaryDataDirectoryException | ORCIDException | MetaDataException | CloneNotSupportedException | PrimaryDataEntityVersionException e) {
			e.printStackTrace();
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
	 * @param email The base64 encoded email of a user
	 * @param name The name (constructed on client side constructed with the base64 encoded email + file path )
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
	public String uploadEntity(@FormDataParam("file") InputStream uploadedInputStream,
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				return "500";
			}
			return "200";
		} catch (PrimaryDataDirectoryException | PrimaryDataEntityVersionException | IOException e) {
			e.printStackTrace();
			return "500";
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "500";
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
	public String publishDataset(@FormDataParam("email") String emailEncoded,@FormDataParam("name") String name, @FormDataParam("embargo") String embargo) throws PrimaryDataDirectoryException, AddressException,
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
					System.out.println(release.toString());
					entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress(email), release);
					return "success";
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress(email));
			return "success";
		}catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "500";
		}
	}
}

class MetaDataWrapper {
	
	
	private final String CREATORS = "creators";
	private final String CONTRIBUTORS = "contributors";
	private final String TITLE = "title";
	private final String DESCRIPTION = "description";
	private final String LICENSE = "license";
	private final String LANGUAGE = "language";
	private final String SUBJECTS = "subjects";
	
	
	private final String FIRSTNAME = "Firstname";
	private final String LASTNAME = "Lastname";
	private final String LEGALNAME = "Legalname";
	private final String ADDRESS = "Address";
	private final String ZIP = "Zip";
	private final String COUNTRY = "Country";
	private final String ORCID = "ORCID";
	
	private UntypedData title;
	private UntypedData description;
	private UntypedData license;
	private EdalLanguage language;
	private Subjects subjects;
	private Persons creators;
	private Persons contributors;
	private LegalPerson legalPerson;
	
	public MetaDataWrapper(JSONObject metadataObject) throws ORCIDException {
		this.title = new UntypedData((String) metadataObject.get(TITLE));
		this.description = new UntypedData((String) metadataObject.get(DESCRIPTION));
		this.license = new UntypedData(EnumCCLicense.valueOf((String) metadataObject.get(LICENSE)).name());
		this.language = new EdalLanguage(LocaleUtils.toLocale((String) metadataObject.get(LANGUAGE)));
		subjects = new Subjects();
		JSONArray subjectsArr = (JSONArray) metadataObject.get(SUBJECTS);
		for(Object subjectStr : subjectsArr) {
			subjects.add(new UntypedData((String) subjectStr));
		}
		this.creators = new Persons();
		this.contributors = new Persons();
		this.legalPerson = new LegalPerson("null","null","null","null");
		fillPersonCollections(creators, (JSONArray) metadataObject.get(CREATORS));
		fillPersonCollections(contributors, (JSONArray) metadataObject.get(CONTRIBUTORS));
	}
	
	private void fillPersonCollections(Persons persons, JSONArray personArray) throws ORCIDException {
		for(Object personObj : personArray) {
			JSONObject person = (JSONObject) personObj;
			if((String) person.get("Legalname") != null) {				
				this.legalPerson = new LegalPerson((String) person.get(LEGALNAME),  (String) person.get(ADDRESS), (String) person.get(ZIP), (String) person.get(COUNTRY));
				persons.add(new LegalPerson((String) person.get(LEGALNAME),  (String) person.get(ADDRESS), (String) person.get(ZIP), (String) person.get(COUNTRY)));
			}else {
				NaturalPerson parsedPerson = new NaturalPerson((String) person.get(FIRSTNAME), (String) person.get(LASTNAME), (String) person.get(ADDRESS),
							(String) person.get(ZIP), (String) person.get(COUNTRY));
				String orcid = (String)person.get(ORCID);
				if(orcid != null && !orcid.isEmpty()) {
					parsedPerson.setOrcid(new ORCID((String) person.get(ORCID)));
				}
				persons.add(parsedPerson);
			}
		}
	}

	public UntypedData getTitle() {
		return title;
	}

	public void setTitle(UntypedData title) {
		this.title = title;
	}

	public UntypedData getDescription() {
		return description;
	}

	public void setDescription(UntypedData description) {
		this.description = description;
	}

	public UntypedData getLicense() {
		return license;
	}

	public void setLicense(UntypedData license) {
		this.license = license;
	}

	public EdalLanguage getLanguage() {
		return language;
	}

	public void setLanguage(EdalLanguage language) {
		this.language = language;
	}

	public Subjects getSubjects() {
		return subjects;
	}

	public void setSubjects(Subjects subjects) {
		this.subjects = subjects;
	}

	public Persons getCreators() {
		return creators;
	}

	public void setCreators(Persons creators) {
		this.creators = creators;
	}

	public Persons getContributors() {
		return contributors;
	}

	public void setContributors(Persons contributors) {
		this.contributors = contributors;
	}

	public LegalPerson getLegalpersons() {
		return this.legalPerson;
	}

	public void setLegalpersons(LegalPerson legalperson) {
		this.legalPerson = legalperson;
	}
	
	
	
	
}
