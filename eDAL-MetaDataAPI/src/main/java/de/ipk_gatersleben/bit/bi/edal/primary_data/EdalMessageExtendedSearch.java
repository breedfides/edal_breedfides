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
package de.ipk_gatersleben.bit.bi.edal.primary_data;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.search.Query;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ManagedAsync;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.sample.Search;

@Path("extendedSearch")
public class EdalMessageExtendedSearch {

	/**
	 * Rest function to find indexed Files/datasets
	 * @param json A container that shoul have specific Information
	 * @return The found files/version information wrapped in a JSONArray
	 */
	@Path("/search")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject extendedSearch(String json) {
		JSONParser parser = new JSONParser();
		try {
			return Search.advancedSearch((JSONObject) parser.parse(json));
		} catch (Exception e) {
			e.printStackTrace();
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return new JSONObject();
		}
	}
	
	/**
	 * REST function to parse a String to a Lucene query
	 * @param json The String to parse
	 * @return The parsed Lucene query
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Path("/parsequery")
	@POST
	@ManagedAsync
	@Produces(MediaType.TEXT_PLAIN)
	public String parseQuery(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		try {
			Query query = Search.parseToLuceneQuery((JSONObject) parser.parse(json));
			if(query == null) {
				return "";
			}
			return query.toString();
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return "";
		}
	}
	
	/**
	 * Drills down with a given query to return Lists of objects with [term/nubmer of hits] (Facets)
	 * @param json The given query information
	 * @return The facets
	 */
	@Path("/drillDown")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray drillDown(String json) {
		JSONParser parser = new JSONParser();
		try {
			return Search.builQueryAndDrillDown((JSONObject)parser.parse(json));
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}
	
	/**
	 * Finds and highlights sections that contain the given query term(s)
	 * @param json Query information
	 * @return The most relevant highlighted text sections
	 */
	@Path("/getHighlightedSections")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getHighlightedSections(String json) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(json);
			return Search.getHighlightedSections((String)requestObj.get("doc"),(String)requestObj.get("query"));
		} catch (Exception e) {
			return null;
		}
	}
	
	@GET
    @Path("/pdf")
    public Response downloadPdfFile()
    {
        StreamingOutput fileStream =  new StreamingOutput() 
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
            {
                try
                {
                    java.nio.file.Path path = Paths.get("C:\\Program Files (x86)\\Google\\evx.zip");
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } 
                catch (Exception e) 
                {
                    throw new WebApplicationException("File Not Found !!");
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = evx.zip")
                .build();
    }
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String uploadedFileLocation = "d://uploaded/" + fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	

}