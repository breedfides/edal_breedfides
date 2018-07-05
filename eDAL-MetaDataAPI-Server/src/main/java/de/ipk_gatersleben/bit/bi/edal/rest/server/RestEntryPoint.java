package de.ipk_gatersleben.bit.bi.edal.rest.server;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.jersey.media.multipart.FormDataParam;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;

@Path("api")
public class RestEntryPoint {

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_XML)
	public ServerInfo serverinfo() {
		ServerInfo info = new ServerInfo();
		info.server = System.getProperty("os.name") + " " + System.getProperty("os.version");
		return info;
	}

	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Path("sendSubject")
	@POST
	public String sendSubject(byte[] data) throws Exception {

		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		Subject subject = (Subject) is.readObject();

		String principal = null;

		for (Principal p : subject.getPrincipals()) {
			principal = p.toString();
			break;
		}

		return principal;
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

	@GET
	@Path("getMetaData/{uuid}/{version}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getMetaData(@PathParam("uuid") String uuid, @PathParam("version") String version) {

		return EdalRestServer.getEntityMetadata(uuid, Long.valueOf(version));
	}

}

@XmlRootElement
class ServerInfo {
	public String server;
}
