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
package de.ipk_gatersleben.bit.bi.edal.breedfides.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;

@Path("io")
public class IOEndPoint {

	/** simple endpoint to download a static file **/
	@GET
	@Path("/download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadStaticFile() throws URISyntaxException {

		File fileDownload = new File(EdalHttpServer.class.getResource("edal_logo.png").toURI());

		ResponseBuilder response = Response.ok((Object) fileDownload);

		response.header("Content-Disposition", "attachment;filename=" + fileDownload);

		return response.build();
	}

	/** simple endpoint to stream a static file **/
	@GET
	@Path("/stream")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response streamStaticFile() throws URISyntaxException {

		StreamingOutput fileStream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {

				try {
					Files.copy(Paths.get(EdalHttpServer.class.getResource("edal_logo.png").toURI()), output);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		};

		return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename=edal_logo.png").build();

	}

	/**
	 * simple endpoint to upload a file
	 * 
	 * @throws IOException
	 **/
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadStaticFile(@FormDataParam("file") InputStream fileInputStream) throws IOException {

		java.nio.file.Path target = Paths.get(System.getProperty("user.home"), "edal_upload.png");

		Files.copy(fileInputStream, target);

		String output = "File successfully uploaded to : " + target;
		
		return Response.status(200).entity(output).build();
	}
	
	/**
	 * simple endpoint to upload a file
	 * 
	 * @throws IOException
	 **/
	@POST
	@Path("/upload_detail")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadStaticFile(@FormDataParam("file") InputStream fileInputStream,  @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {

		
		System.out.println(fileDetail.getFileName());
		
		
		java.nio.file.Path target = Paths.get(System.getProperty("user.home"), "edal_upload.png");

		Files.copy(fileInputStream, target);

		String output = "File successfully uploaded to : " + target;
		
		return Response.status(200).entity(output).build();
	}
	

}
