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
package de.ipk_gatersleben.bit.bi.edal.primary_data;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.search.Query;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ManagedAsync;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

@Path("extendedSearch")
public class EdalSearchRestEndpoints {
	
	private static final String DOI = "doi";
	private static final String YEAR = "year";
	private static final String QUERY = "query";
	private static final String DOC = "doc";
	private final String LOCATIONS = "locations";
	private final String ACCESSES = "accesses";
	private final String DOWNLOADS = "downloads";
	private final String TITLE = "title";

	/**
	 * Rest function to find indexed Files/datasets
	 * @param jsonWithQueryInformation A container that shoul have specific Information
	 * @return The found files/version information wrapped in a JSONArray
	 */
	@Path("/search")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject extendedSearch(String jsonWithQueryInformation) {
		JSONParser parser = new JSONParser();
		try {
			SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
			return searchProvider.advancedSearch((JSONObject) parser.parse(jsonWithQueryInformation));
		} catch (Exception e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return new JSONObject();
		}
	}
	
	/**
	 * REST function to parse a String to a Lucene query
	 * @param jsonWrappedQuery The String to parse
	 * @return The parsed Lucene query
	 */
	@Path("/parsequery")
	@POST
	@ManagedAsync
	@Produces(MediaType.TEXT_PLAIN)
	public String parseQuery(String jsonWrappedQuery) {
		JSONParser parser = new JSONParser();
		try {
			SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
			Query query = searchProvider.parseToLuceneQuery((JSONObject) parser.parse(jsonWrappedQuery));
			if(query == null) {
				return "";
			}
			return query.toString();
		} catch (Exception e) {
			DataManager.getImplProv().getLogger().debug("Error occured in REST Endpoint parseQuery(): "+e.getMessage());
			return new String();
		}
	}
	
	/**
	 * Drills down with a given query to return Lists of objects with [term/nubmer of hits] (Facets)
	 * @param jsonWithQueryInformation The given query information
	 * @return The facets
	 */
	@Path("/drillDown")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray drillDown(String jsonWithQueryInformation) {
		JSONParser parser = new JSONParser();
		try {
			SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
			return searchProvider.drillDown(searchProvider.buildQueryFromJSON((JSONObject)parser.parse(jsonWithQueryInformation)));
		} catch (Exception e) {
			DataManager.getImplProv().getLogger().debug("Error at REST drilldown: "+e.getMessage());
			return new JSONArray();
		}
	}
	
	/**
	 * Finds and highlights sections that contain the given query term(s)
	 * @param jsonWithQuery Query information
	 * @return The most relevant highlighted text sections
	 */
	@Path("/getHighlightedSections")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getHighlightedSections(String jsonWithQuery) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject requestObj = (JSONObject) parser.parse(jsonWithQuery);
			SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
			return searchProvider.getHighlightedSections((String)requestObj.get(DOC),(String)requestObj.get(QUERY));
		} catch (Exception e) {
			DataManager.getImplProv().getLogger().debug("Error at REST getHighlightedSections: "+e.getMessage());
			return new JSONObject();
		}
	}
	
	
	
	/**
	 * Search for indexed publicreferences
	 * @param keyword The given search keyword
	 * @return The hits wrapped as JSONObject in a JSONArray
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{keyword}")
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray keywordSearch(@PathParam("keyword") String keyword) {
		
		HashSet<Integer> ids = new HashSet<Integer>();
		try {
			SearchProvider searchProvider = DataManager.getImplProv().getSearchProvider().getDeclaredConstructor().newInstance();
			ids = searchProvider.searchByKeyword(keyword, false, PublicVersionIndexWriterThread.PUBLICREFERENCE);
		} catch (Exception e) {
			DataManager.getImplProv().getLogger().debug("Error at REST getHighlightedSections: "+e.getMessage());
			return new JSONArray();
		} 
		JSONArray results = new JSONArray();
		Session session = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSessionFactory().openSession();
		for(Integer id : ids) {
			PublicReferenceImplementation reference = session.get(PublicReferenceImplementation.class, id);
			JSONObject obj = new JSONObject();
			if(reference.getPublicationStatus().equals(PublicationStatus.ACCEPTED)) {
				obj.put(YEAR, reference.getAcceptedDate().get(Calendar.YEAR));
				try {
					obj.put(DOI, reference.getAssignedID());
				} catch (PublicReferenceException e) {
					DataManager.getImplProv().getLogger().debug("No PublicReference ID set: "+e.getMessage());
				}
			}else {
				continue;
			}
			obj.put(TITLE, reference.getVersion().getMetaData().toString());
			String internalID = reference.getInternalID();
			obj.put(DOWNLOADS, String.valueOf(VeloCityHtmlGenerator.downloadedVolume.get(internalID)));
			obj.put(ACCESSES, String.valueOf(VeloCityHtmlGenerator.uniqueAccessNumbers.get(internalID)));	
			if(VeloCityHtmlGenerator.ipMap.get(internalID) != null) {
				obj.put(LOCATIONS,
						GenerateLocations.generateGpsLocationsToJson(VeloCityHtmlGenerator.ipMap.get(internalID)));
			}
			results.add(obj);
		}
		return results;
	}
	
	

}