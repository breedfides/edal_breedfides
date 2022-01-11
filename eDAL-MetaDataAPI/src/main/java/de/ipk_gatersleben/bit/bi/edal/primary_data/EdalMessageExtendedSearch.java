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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.text.DateFormatter;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ManagedAsync;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ipk_gatersleben.bit.bi.edal.helper.Search;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.NativeLuceneIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

@Path("extendedSearch")
public class EdalMessageExtendedSearch {

	@Path("/search")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject extendedSearch(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		try {
			return Search.advancedSearch((JSONObject) parser.parse(json));
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return new JSONObject();
		}
	}
	
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
	
	@Path("/drillDown")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray drillDown(String json) {
		JSONParser parser = new JSONParser();
		try {
			return Search.builQueryAndDrillDown((JSONObject)parser.parse(json));
		} catch (org.apache.lucene.queryparser.classic.ParseException | ParseException | IOException e) {
			return new JSONArray();
		}
	}
	
	@Path("/getTermLists")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTermLists() throws JsonParseException, JsonMappingException, IOException {
		JSONObject result = new JSONObject();
		result.put(MetaDataImplementation.PERSON, VeloCityHtmlGenerator.getCreators());
		result.put(MetaDataImplementation.CONTRIBUTOR, VeloCityHtmlGenerator.getContributors());
		result.put(MetaDataImplementation.SUBJECT, VeloCityHtmlGenerator.getSubjects());
		result.put(MetaDataImplementation.TITLE, VeloCityHtmlGenerator.getTitles());
		result.put(MetaDataImplementation.DESCRIPTION, VeloCityHtmlGenerator.getDescriptions());		
		return result;
	}
	
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