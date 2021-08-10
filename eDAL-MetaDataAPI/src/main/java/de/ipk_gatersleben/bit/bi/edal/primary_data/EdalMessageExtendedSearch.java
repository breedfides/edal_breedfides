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

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.NativeLuceneIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.TextDataBase;

import javax.ws.rs.Produces;

@Path("extendedSearch")
public class EdalMessageExtendedSearch {

	@Path("/search")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject extendedSearch(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		try {
			return DataManager.advancedSearch((JSONObject) parser.parse(json));
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
			Query query = DataManager.parseToLuceneQuery((JSONObject) parser.parse(json));
			if(query == null) {
				return "";
			}
			return query.toString();
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return "";
		}
	}
	
	@Path("/parsequery2")
	@POST
	@ManagedAsync
	@Produces(MediaType.TEXT_PLAIN)
	public String parseQuery2(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		try {
			Query query = DataManager.parseToLuceneQuery2((JSONObject) parser.parse(json));
			if(query == null) {
				return "";
			}
			return query.toString();
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Error occured when parsing String parameter to JSONArray");
			return "";
		}
	}
	
	@Path("/countHits")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject countHits(String json) throws JsonParseException, JsonMappingException, IOException {
		JSONParser parser = new JSONParser();
		try {
			return DataManager.countHits((JSONObject)parser.parse(json));
		} catch (ParseException e) {
			return new JSONObject();
		}
	}
	@Path("/getTermLists")
	@POST
	@ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getTermLists() throws JsonParseException, JsonMappingException, IOException {
		JSONObject result = new JSONObject();
		result.put(MetaDataImplementation.PERSON, NativeLuceneIndexWriterThread.getCreators());
		result.put(MetaDataImplementation.CONTRIBUTOR, NativeLuceneIndexWriterThread.getContributors());
		result.put(MetaDataImplementation.SUBJECT, NativeLuceneIndexWriterThread.getSubjects());
		result.put(MetaDataImplementation.TITLE, NativeLuceneIndexWriterThread.getTitles());
		result.put(MetaDataImplementation.DESCRIPTION, NativeLuceneIndexWriterThread.getDescriptions());		
		return result;
	}

}