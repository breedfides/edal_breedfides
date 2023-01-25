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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * REST endpoints for all metadata relevant function of the BreedFides portal
 * 
 * @author arendd
 *
 */
@javax.ws.rs.Path("metadata")
public class MetadataEndpoint {

	@GET
	@javax.ws.rs.Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public String info() {
		return "BreedFides Metadata endpoint";
	}

	
	@POST
	@javax.ws.rs.Path("/uploadmetadata")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadMetaData(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("path") String path, @FormDataParam("parent") String parent,
			@FormDataParam("metadata") JSONObject metadata) throws IOException {

		Path root = DataManager.getConfiguration().getDataPath();

		Path filePath = Paths.get(root.toString(), path);

		Files.copy(fileInputStream, filePath);

		InfoEndpoint.getLogger().info(metadata.toString());

		String output = "File successfully uploaded";

		return Response.status(200).entity(output).build();
	}

}
