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
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Base64;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import javafx.application.Platform;

@Path("api")
public class RestEntryPoint {

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test2";
	}

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_XML)
	public ServerInfo serverinfo() {
		ServerInfo info = new ServerInfo();
		info.server = System.getProperty("os.name") + " " + System.getProperty("os.version");
		return info;
	}

	@Produces(MediaType.TEXT_PLAIN)
	@Path("authenticate")
	@POST
	public String authenticate(@FormDataParam("email") String email) throws Exception {
		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		System.out.println(email);
		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;
		try {
			ctx = new LoginContext("Elixir", new SimpleCallbackHandler(email));
			ctx.login();
			Thread.currentThread().setContextClassLoader(currentClassLoader);
			Platform.setImplicitExit(true);
			Subject subject = ctx.getSubject();
			// return (String) jsonEmail.get("email");
//			StreamingOutput stream = new StreamingOutput() {
//				@Override
//				public void write(OutputStream os) throws IOException, WebApplicationException {
//					ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
//					objectOutputStream.writeObject(subject);
//					objectOutputStream.flush();
//				}
//			};			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = null;
			try {
			  out = new ObjectOutputStream(bos);   
			  out.writeObject(subject);
			  out.flush();
			  byte[] yourBytes = bos.toByteArray();			
				return Base64.getEncoder().encodeToString(yourBytes);
			} finally {
			  try {
			    bos.close();
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			}
			// PrimaryDataDirectory root =
			// DataManager.getRootDirectory(EdalRestServer.implProv, subject);
		} catch (final LoginException e) {

			int result = 0;
			if (e.getCause() == null) {
				result = (Integer) JOptionPane.showConfirmDialog(null,
						"Your login attempt was not successful !\nReason: " + "no null name allowed" + "\nTry again ?",
						"Login to Elixir", JOptionPane.YES_NO_OPTION);
			} else {
				result = (Integer) JOptionPane.showConfirmDialog(null,
						"Your login attempt was not successful !\nReason: " + e.getMessage() + "\nTry again ?",
						"Login to Elixir+", JOptionPane.YES_NO_OPTION);
			}

		}
		return "";
//		JSONObject obj = new JSONObject();
//		obj.put("email", jsonEmail.get("email"));
//		return obj;
	}

	@Produces("text/html; charset=UTF-8")
	@Path("authenticateEmail")
	@POST
	public String authenticateEmail(String email) throws Exception {
		Gson gson = new Gson();

		JSONObject jsonEmail = (JSONObject) new JSONParser().parse(email);
		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;
		try {
			ctx = new LoginContext("Elixir", new SimpleCallbackHandler((String) jsonEmail.get("email")));
			ctx.login();
			Thread.currentThread().setContextClassLoader(currentClassLoader);
			Platform.setImplicitExit(true);
			Subject subject = ctx.getSubject();
			return (String) jsonEmail.get("email");
//			StreamingOutput stream = new StreamingOutput() {
//			    @Override
//			    public void write(OutputStream os) throws IOException,
//			    WebApplicationException {
//		    	 ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
//		    	 objectOutputStream.writeObject(subject);
//		    	 objectOutputStream.flush();
//			    }
//			  };
//		    	 ByteArrayOutputStream output = new ByteArrayOutputStream();
//		    	 stream.write(output);
//		    	 System.out.println("1.");
//		    	 System.out.println(new String(output.toByteArray(), "UTF-8"));
//			  return Response.ok(stream).build();
//			PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
		} catch (final LoginException e) {

			int result = 0;
			if (e.getCause() == null) {
				result = (Integer) JOptionPane.showConfirmDialog(null,
						"Your login attempt was not successful !\nReason: " + "no null name allowed" + "\nTry again ?",
						"Login to Elixir", JOptionPane.YES_NO_OPTION);
			} else {
				result = (Integer) JOptionPane.showConfirmDialog(null,
						"Your login attempt was not successful !\nReason: " + e.getMessage() + "\nTry again ?",
						"Login to Elixir+", JOptionPane.YES_NO_OPTION);
			}

		}
		return null;
//		JSONObject obj = new JSONObject();
//		obj.put("email", jsonEmail.get("email"));
//		return obj;
	}

	@Produces("text/html; charset=UTF-8")
	@Path("reuseSubject")
	@POST
	public String reuseSubject(@FormDataParam("subject") String src) {
		
		try {
			InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(src));		
			ObjectInputStream oi = new ObjectInputStream(in);
			Subject subject = (Subject) oi.readObject();
			PrimaryDataDirectory dir = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			System.out.println(dir.getName());
			return subject.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		ObjectInputStream in;
//		try {
//			in = new ObjectInputStream(rawData);
//			// Method for deserialization of object
//			Subject subject = (Subject) in.readObject();
//
//			return subject.toString();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "failed";
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrimaryDataDirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src;
	}

	@Produces(MediaType.TEXT_PLAIN)
	@Path("sendSubject")
	@POST
	public String sendSubject(String subjectString) {
		try {
			Subject subject = new Subject();
			subject.getPrincipals().add(new ElixirPrincipal(subjectString));
			PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			PrimaryDataDirectory entity = (PrimaryDataDirectory) root.getPrimaryDataEntity("REST");
			System.out.println(entity.getMetaData());
//			MetaData metadata = entity.getMetaData().clone();
//			Persons persons = new Persons();
//			NaturalPerson np = new NaturalPerson("Peter", "Ralfs",
//					"2Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstraße 3",
//					"06466", "Germany");
//			persons.add(np);
//			metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
//			metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
//					new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
//							"IPK Gatersleben, Seeland OT Gatersleben, Corrensstraße 3", "06466", "Germany"));
//	
//			Subjects subjects = new Subjects();
//			subjects.add(new UntypedData("wheat"));
//			subjects.add(new UntypedData("transcriptional network"));
//			subjects.add(new UntypedData("genie3"));
//			subjects.add(new UntypedData("transcriptomics"));
//			EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
//			metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
//			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
//			metadata.setElementValue(EnumDublinCoreElements.TITLE,
//					new UntypedData("REST"));
//			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
//					"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
//			entity.setMetaData(metadata);
			System.out.println(root);
			System.out.println("json_: " + subject.toString());

			for (Principal p : subject.getPrincipals()) {
				return p.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "bummer";
	}

	@Produces(MediaType.TEXT_PLAIN)
	@Path("uploadEntity")
	@POST
	public String uploadEntity(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("name") String name, @FormDataParam("email") String email,
			@FormDataParam("type") String type, @FormDataParam("metadata") String metadatastring)
			throws InterruptedException {
		// JSONObject data = new JSONObject();
		try {
			// data = (JSONObject) new JSONParser().parse(request);
			String[] pathArray = name.split("\\\\");
			// String type = (String) data.get("type");

			Subject subject = new Subject();
			subject.getPrincipals().add(new ElixirPrincipal(email));
			PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
			System.out.println(name);
			if (pathArray.length > 1) {
				int j = 0;
			}
			if (pathArray.length > 0) {
				PrimaryDataDirectory parent = root;
				int lastIndex = pathArray.length - 1;
				for (int i = 0; i < lastIndex; i++) {
					parent = (PrimaryDataDirectory) parent.getPrimaryDataEntity(pathArray[i]);
				}
				if (type.equals("Directory")) {
					PrimaryDataDirectory newDataSet = parent.createPrimaryDataDirectory(pathArray[lastIndex]);
					JSONObject metadatajson = null;
					metadatajson = (JSONObject) new JSONParser().parse(metadatastring);
					String authorr = metadatajson == null ? "nometadata" : (String) metadatajson.get("author");
					if (pathArray.length == 1) {
						MetaData metadata = newDataSet.getMetaData().clone();
						Persons persons = new Persons();
						NaturalPerson np = new NaturalPerson("Peter", authorr,
								"2Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstraße 3",
								"06466", "Germany");
						persons.add(np);
						metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
						metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
								new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
										"IPK Gatersleben, Seeland OT Gatersleben, Corrensstraße 3", "06466",
										"Germany"));

						Subjects subjects = new Subjects();
						subjects.add(new UntypedData("wheat"));
						subjects.add(new UntypedData("transcriptional network"));
						subjects.add(new UntypedData("genie3"));
						subjects.add(new UntypedData("transcriptomics"));
						EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
						metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
						metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
						metadata.setElementValue(EnumDublinCoreElements.TITLE,
								new UntypedData(pathArray[pathArray.length - 1]));
						metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
								"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
						newDataSet.setMetaData(metadata);
					}
				} else {
					PrimaryDataFile file = parent.createPrimaryDataFile(pathArray[pathArray.length - 1]);
					try {
						file.store(uploadedInputStream);
					} catch (PrimaryDataFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				return "500";
			}
			return "200";
		} catch (ParseException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
		} catch (MetaDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrimaryDataEntityVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "500";
	}

	@Produces(MediaType.TEXT_PLAIN)
	@Path("publishDataset")
	@POST
	public String publishDataset(String request) throws PrimaryDataDirectoryException, AddressException,
			PublicReferenceException, PrimaryDataEntityException, ParseException {
		JSONObject data = (JSONObject) new JSONParser().parse(request);
		Subject subject = new Subject();
		subject.getPrincipals().add(new ElixirPrincipal((String) data.get("email")));
		PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);
		PrimaryDataDirectory entity = (PrimaryDataDirectory) root.getPrimaryDataEntity((String) data.get("name"));
		entity.addPublicReference(PersistentIdentifier.DOI);
		entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress((String) data.get("email")));
		return "success";
	}

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("sendSubjectAndObject")
	@POST
	public String rename(@FormDataParam("subject") byte[] subjectByteArray, @FormDataParam("name") String newName)
			throws Exception {

		ByteArrayInputStream in = new ByteArrayInputStream(subjectByteArray);
		ObjectInputStream is = new ObjectInputStream(in);
		Subject subject = (Subject) is.readObject();

		String principal = null;

		for (Principal p : subject.getPrincipals()) {
			principal = p.toString();
			break;
		}

		return principal + "\t" + "name: " + newName;
	}

	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Path("getRootDirectory")
	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getRootDirectory(byte[] data) throws Exception {

		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		Subject subject = (Subject) is.readObject();

		PrimaryDataDirectory root = DataManager.getRootDirectory(EdalRestServer.implProv, subject);

		System.out.println(root);

		return Response.ok(root, MediaType.APPLICATION_OCTET_STREAM).build();
	}

//	@GET
//	@Path("getMetaData/{uuid}/{version}")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String getMetaData(@PathParam("uuid") String uuid, @PathParam("version") String version) {
//
//		return EdalRestServer.getEntityMetadata(uuid, Long.valueOf(version));
//	}

	@GET
	@Path("/pdf")
	public Response downloadPdfFile() {
		StreamingOutput fileStream = new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
				try {
//                	File dir = Paths.get(System.getProperty("user.home"), "Search_Test").toFile();
//            		if(!dir.exists()) {
//            			dir.mkdir();
//            		}
//            		
					java.nio.file.Path path = Paths.get(System.getProperty("user.home"), "Search_Test", "111.txt");
//                	FileWriter myWriter = new FileWriter(path.toString());
//        			BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
//        			String s = "This is a test for indexing the content of text files.";
//        			int size = (int) ((Math.pow(10, 4)*1024*1024)/s.getBytes().length);
//        			for(int i = 0; i < size; i++) {
//        				bufferedWriter.write(s);
//        			}
//        			myWriter.close();
//                	byte[] data = FileUtils.readFileToByteArray(path.toFile());
//                    //byte[] data = Files.readAllBytes(path);
//                    output.write(data);
//                    output.flush();

					final int BUFFER_SIZE = 1024 * 1024; // this is actually bytes

					FileInputStream fis = new FileInputStream(path.toFile());
					byte[] buffer = new byte[BUFFER_SIZE];
					int read = 0;
					while ((read = fis.read(buffer)) > 0) {
						output.write(buffer);
						output.flush();
					}

					fis.close();

				} catch (Exception e) {
					throw new WebApplicationException("File Not Found !!");
				}
			}
		};
		return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = build.txt").build();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("name") String name) {

		String uploadedFileLocation = "d://uploaded/" + name;

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}

	@POST
	@Path("/uploadDirectory")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadDirectory(FormDataMultiPart multiPart) {

		for (BodyPart part : multiPart.getBodyParts()) {
			System.out.println(part.getContentDisposition().getFileName());
		}
//		String uploadedFileLocation = "d://uploaded/" + fileDetail.getFileName();
//		System.out.println(uploadedFileLocation);
//
//		// save it
//		//writeToFile(uploadedInputStream, uploadedFileLocation);
//
//		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity("test output").build();

	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("Error at REST writeToFile: " + e.getMessage());
		}

	}

}

@XmlRootElement
class ServerInfo {
	public String server;
}

class SimpleCallbackHandler implements CallbackHandler {

	final String username;

	SimpleCallbackHandler(String username) {
		this.username = username;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (final Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				final NameCallback nc = (NameCallback) callback;
				nc.setName(username);
			}
		}
	}

}
