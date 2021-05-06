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

import java.io.IOException;
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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.TextDataBase;

import javax.ws.rs.Produces;

@Path("extendedSearch")
public class EdalMessageExtendedSearch {

	@Path("/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray extendedSearch(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		JSONObject jsonArray = null;
		JSONArray finalArray = new JSONArray();
		try {
			jsonArray = (JSONObject) parser.parse(json);
			//DataManager.getImplProv().getLogger().info(jsonArray.toJSONString());
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return finalArray;
		}
		String type = (String) jsonArray.get("hitType");
		JSONArray groups = (JSONArray) jsonArray.get("groups");
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		for(Object group : groups) {
			JSONObject currentGroup = (JSONObject) group;
			JSONArray currentGroupArray = (JSONArray) currentGroup.get("goup");
			BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
			for(Object untypedQueryData : currentGroupArray) {
				Query query = null;
				
				JSONObject queryData = (JSONObject)untypedQueryData;
				if(((String)queryData.get("type")).equals(MetaDataImplementation.STARTDATE) || 
						((String)queryData.get("type")).equals(MetaDataImplementation.ENDDATE)) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy",Locale.ENGLISH);
					LocalDateTime lowerBound = LocalDate.parse((String)queryData.get("lower"), formatter).atStartOfDay();
					LocalDateTime upperBound = LocalDate.parse((String)queryData.get("upper"), formatter).atStartOfDay();	
					ZonedDateTime lowerZDT = ZonedDateTime.of(lowerBound, ZoneId.of("UTC"));
					ZonedDateTime upperZDT = ZonedDateTime.of(upperBound, ZoneId.of("UTC"));
					long lowerms = lowerZDT.toInstant().toEpochMilli();
					query = TermRangeQuery.newStringRange(MetaDataImplementation.STARTDATE+"String", DateTools.timeToString(lowerZDT.toInstant().toEpochMilli(),Resolution.DAY), DateTools.timeToString(upperZDT.toInstant().toEpochMilli(),Resolution.DAY), false, false);
					
				}else {
					QueryParser queryParser = new QueryParser((String) queryData.get("type"), analyzer);
					try {
						if((boolean) queryData.get("fuzzy")) {
							query = queryParser.parse((String) queryData.get("value")+'~');
							
						}else {
							query = queryParser.parse((String) queryData.get("value"));
						}
					} catch (org.apache.lucene.queryparser.classic.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				DataManager.getImplProv().getLogger().info("New Query added:");
				DataManager.getImplProv().getLogger().info(query.toString());
				subQuery.add(query, Occur.valueOf((String) queryData.get("Occur")));
			}
			finalQuery.add(subQuery.build(), Occur.valueOf((String) currentGroup.get("Occur")));
		}
		QueryParser queryType = new QueryParser(MetaDataImplementation.CHECKSUM,analyzer);
		try {
			finalQuery.add(queryType.parse(new TermQuery(new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.PUBLICREFERENCE)).toString()), Occur.FILTER);
		} catch (org.apache.lucene.queryparser.classic.ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		IndexReader reader = null;
		try {
	    	Directory indexDirectory = FSDirectory.open(Paths.get(((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexDirectory().toString(),"Master_Index"));
	    	reader = DirectoryReader.open(indexDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BooleanQuery.setMaxClauseCount(10000);
		IndexSearcher searcher = new IndexSearcher(reader);
        ScoreDoc[] hits2 = null;
        //BooleanQuery booleanQuery2 = booleanQuery.build();
		try {
			hits2 = searcher.search(finalQuery.build(), 50000).scoreDocs;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		
		HashSet<Integer> ids = new HashSet<>();
        for(int i = 0; i < hits2.length; i++) {
        	Document doc = null;
			try {
				doc = searcher.doc(hits2[i].doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	ids.add(Integer.parseInt((doc.get(PublicVersionIndexWriterThread.PUBLICID))));
        }
//		final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(entities);
//		return Collections.unmodifiableList(results);
        
        for(Integer id : ids) {
			PublicReferenceImplementation reference = session.get(PublicReferenceImplementation.class, id);
			JSONObject obj = new JSONObject();
			obj.put("year", reference.getAcceptedDate().get(Calendar.YEAR));
			try {
				obj.put("doi", reference.getAssignedID());
			} catch (PublicReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			obj.put("title", reference.getVersion().getMetaData().toString());
			String internalID = reference.getInternalID();
			obj.put("downloads", String.valueOf(VeloCityHtmlGenerator.downloadedVolume.get(internalID)));
			obj.put("accesses", String.valueOf(VeloCityHtmlGenerator.uniqueAccessNumbers.get(internalID)));	
			if(VeloCityHtmlGenerator.ipMap.get(internalID) != null) {
				obj.put("locations",
						GenerateLocations.generateGpsLocationsToJson(VeloCityHtmlGenerator.ipMap.get(internalID)));
			}
			finalArray.add(obj);
		}
		return finalArray;
		
//		JSONObject jsonobj = new JSONObject();
//		jsonobj.put("msg", "Hello World");
//		finalArray.add(jsonobj);
//		return finalArray;
	}

}