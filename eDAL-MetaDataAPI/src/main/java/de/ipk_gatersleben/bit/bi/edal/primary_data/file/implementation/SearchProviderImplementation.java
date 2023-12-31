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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager.SearcherAndTaxonomy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;

/**
 * Implementaion of the {@link SearchProvider} interface
 * 
 * @author Ralfs
 *
 */
public class SearchProviderImplementation implements SearchProvider {
	
	public static final String TILDE = "~";

	/** The last result of a resultlist, needed for pagination  **/
	private static final String BOTTOM_RESULT_SCORE = "bottomResultScore";

	private static final String BOTTOM_RESULT_ID = "bottomResultId";

	private static final String PAGE_SIZE = "pageSize";

	private static final String PAGE_INDEX = "pageIndex";

	private static final String PAGE_ARRAY_SIZE = "pageArraySize";

	private static final String DISPLAYED_PAGE = "displayedPage";

	public final String MIN_YEAR = "minYear";
	
	public final String MAX_YEAR = "maxYear";
	
	public final String MAX_FILE_SIZE = "maxFileSize";

	public final String[] METADATA_FIELDS = { EnumIndexField.TITLE.value(), EnumIndexField.SIZE.value(),
			EnumIndexField.VERSIONID.value(), EnumIndexField.ENTITYID.value(), EnumIndexField.PRIMARYENTITYID.value(),
			EnumIndexField.ENTITYTYPE.value(), EnumIndexField.DOCID.value(),EnumIndexField.PUBLICID.value(),
			EnumIndexField.REVISION.value(), EnumIndexField.INTERNALID.value(),EnumIndexField.CONTENT.value(),
			EnumIndexField.FILETYPE.value() };


	@SuppressWarnings("unchecked")
	public JSONObject advancedSearch(JSONObject requestObject) {
		JSONObject result = new JSONObject();
		Query buildedQuery = buildQueryFromJSON(requestObject, result);
		CountDownLatch internalCountDownLatch = new CountDownLatch(1);		
		class DrillDownThread extends Thread {
			public JSONArray facets = new JSONArray();
			public void run() {
				facets = drillDown(buildedQuery.toString());
				internalCountDownLatch.countDown();
			}
		}
		
		DrillDownThread innerThread = new DrillDownThread();
		innerThread.start();
		SearcherAndTaxonomy manager = null;
		try {
			manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("Lucene Reference Manager is already closed "+e.getMessage());
			return result;
		}
		DataManager.getImplProv().getLogger().debug(buildedQuery.toString());
		TopDocs topDocs = null;
		int currentPageNumber = ((int) (long) requestObject.get(DISPLAYED_PAGE));
		int pageArraySize = ((int) (long) requestObject.get(PAGE_ARRAY_SIZE));
		int pageIndex = ((int) (long) requestObject.get(PAGE_INDEX));
		int pageSize = ((int) (long) requestObject.get(PAGE_SIZE));
		try {
			if (pageArraySize == 0 || currentPageNumber == 1) {
				DataManager.getImplProv().getLogger()
						.debug("Builded QUery advanced search: " + buildedQuery.toString());
				topDocs = manager.searcher.search(buildedQuery, pageSize * 3);
			} else {
				ScoreDoc bottomScoreDoc = null;
				bottomScoreDoc = manager.searcher.search(new TermQuery(
						new Term(EnumIndexField.DOCID.value(), (String) requestObject.get(BOTTOM_RESULT_ID))),
						5).scoreDocs[0];
				bottomScoreDoc.score = ((float) (double) requestObject.get(BOTTOM_RESULT_SCORE));
				topDocs = manager.searcher.searchAfter(bottomScoreDoc, buildedQuery, pageSize * 3);
			}
			if (requestObject.get("hitSize") == null) {
				TotalHitCountCollector collector = new TotalHitCountCollector();
				manager.searcher.search(buildedQuery, collector);
				result.put("hitSize", collector.getTotalHits());
			} else {
				result.put("hitSize", requestObject.get("hitSize"));
			}
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("IOexception on searching: "+e.getMessage());
		}
		result.put("hitSizeDescription", "");
		result.put(DISPLAYED_PAGE, (long) requestObject.get(DISPLAYED_PAGE));
		result.put(PAGE_INDEX, pageIndex);
		// DataManager.getImplProv().getLogger().info("Finished Query with:
		// "+topDocs.totalHits.value);
		String whereToSearch = (String) requestObject.get("whereToSearch");
		Highlighter highlighter = null;
		Analyzer analyzer = null;
		if (whereToSearch != null && whereToSearch.equals(EnumIndexField.CONTENT.value())
				&& buildedQuery.toString().contains(EnumIndexField.CONTENT.value())) {
			// prepare highlighting
			analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer();
			Query contentQuery = extractContentQuery((String) requestObject.get("existingQuery"), (JSONArray) requestObject.get("queries"));
			if(contentQuery != null) {
				highlighter = new Highlighter(new QueryScorer(contentQuery));
				highlighter.setTextFragmenter(new SimpleFragmenter(48));
			}
		}

		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		JSONArray finalArray = new JSONArray();
		if (scoreDocs.length == 0) {
			result.put("hitSize", scoreDocs.length);
			return result;
		}
		if (scoreDocs.length < pageSize) {
			result.put("hitSize", scoreDocs.length);
			pageSize = ((int) (long) scoreDocs.length);
		}
		Document doc = null;
		Set<String> fields = Set.of(METADATA_FIELDS);
		for (int i = 0; i < pageSize; i++) {
			try {
				doc = manager.searcher.doc(scoreDocs[i].doc, fields);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger().debug("IOexception while searching a specific version: "+e.getMessage());
			}
			JSONObject obj = new JSONObject();
			String type = doc.get(EnumIndexField.ENTITYTYPE.value());
			PublicReferenceImplementation reference = session.get(PublicReferenceImplementation.class,
					Integer.parseInt((doc.get(EnumIndexField.PUBLICID.value()))));
			if (reference.getAcceptedDate() == null) {
				// if in Testmode, use creation year
				obj.put("year", doc.get(EnumIndexField.STARTDATE.value()));
			} else {
				obj.put("year", reference.getAcceptedDate().get(Calendar.YEAR));
			}

			if (type.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
				try {
					obj.put("doi", reference.getAssignedID());
				} catch (PublicReferenceException e) {
					DataManager.getImplProv().getLogger().debug("no Publicreference ID set: "+e.getMessage());
				}
				obj.put("title", reference.getVersion().getMetaData().toString());
				obj.put("fileName", "");
				obj.put("ext", "");
				obj.put("type", "record");
			} else {
				try {
					obj.put("doi", reference.getAssignedID());
				} catch (PublicReferenceException e) {
					DataManager.getImplProv().getLogger().debug("no Publicreference ID set: "+e.getMessage());
				}
				String ext = doc.get(EnumIndexField.FILETYPE.value());
				String link = doc.get(EnumIndexField.INTERNALID.value()) + "/"
						+ doc.get(EnumIndexField.PRIMARYENTITYID.value()) + "/"
						+ doc.get(EnumIndexField.REVISION.value());
				PrimaryDataFileImplementation file = session.get(PrimaryDataFileImplementation.class,
						doc.get(EnumIndexField.PRIMARYENTITYID.value()));
				obj.put("link", link);
				obj.put("fileName", file.toString());
				obj.put("docId", doc.get(EnumIndexField.DOCID.value()));
				obj.put("size", doc.get(EnumIndexField.SIZE.value()));
				obj.put("title", reference.getVersion().getMetaData().toString());

				if (highlighter != null) {
					try {
						String highlight = highlighter.getBestFragment(analyzer, EnumIndexField.CONTENT.value(),
								doc.get(EnumIndexField.CONTENT.value()));
						if (highlight != null) {
							obj.put("highlight", highlight);
						} else {
							obj.put("highlight", doc.get(EnumIndexField.CONTENT.value()).substring(0, 100));
						}
					} catch (IOException | InvalidTokenOffsetsException e) {
						obj.put("highlight", doc.get(EnumIndexField.CONTENT.value()).substring(0, 100));
					}
				}
				if (type.equals(PublicVersionIndexWriterThread.FILE)) {
					obj.put("type", "File");
					obj.put("ext", ext);
				} else if (type.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
					obj.put("type", "Directory");
					obj.put("ext", "");
				}
			}
			finalArray.add(obj);
			// DataManager.getImplProv().getLogger().info("Loaded doc Nr."+i+" title:
			// "+doc.get(IndexSearchConstants.TITLE));
		}
		result.put("results", finalArray);
		// bottomResult.docids needs to be stored, to support paginated Searching

		// Pagination
		int additionalPages;
		JSONArray pageArray = new JSONArray();
		JSONObject page = new JSONObject();
		Set<String> set = Set.of(new String[] { EnumIndexField.DOCID.value() });
		if (pageArraySize == 0) {
			page.put("bottomResult", null);
			page.put(BOTTOM_RESULT_SCORE, 0);
			page.put("page", 1);
			page.put("index", 0);
			pageArray.add(page);
			// if pageSize equals the result size, only one page should be stored
			if (pageSize != scoreDocs.length) {
				additionalPages = 3;
				for (int i = 1; i < additionalPages; i++) {
					page = new JSONObject();
					int index = i * pageSize - 1;
					if (index < scoreDocs.length - 1) {
						try {
							page.put("bottomResult", manager.searcher.doc(scoreDocs[index].doc, set)
									.get(EnumIndexField.DOCID.value()));
							page.put(BOTTOM_RESULT_SCORE, scoreDocs[index].score);
							page.put("page", i + 1);
							page.put("index", i);
							pageArray.add(page);
						} catch (IOException e) {
							DataManager.getImplProv().getLogger().debug("IOException when searching a specific version: "+e.getMessage());
						}
					} else {
						break;
					}
					// DataManager.getImplProv().getLogger().info("Loaded doc Nr."+index+" title:
					// "+doc.get(IndexSearchConstants.TITLE));
				}
			}
		} else {
			// check if there needs to be loaded more additional Sites or only 1 (the
			// current selected Site)
			if (currentPageNumber + 2 > pageArraySize && scoreDocs.length > pageSize) {
				int offset = pageArraySize - currentPageNumber + 1;
				additionalPages = 3 - offset;
				for (int i = 0; i < additionalPages; i++) {
					page = new JSONObject();
					int index = (i + offset) * pageSize - 1;
					if (index < scoreDocs.length) {
						try {
							page.put("bottomResult", manager.searcher.doc(scoreDocs[index].doc, set)
									.get(EnumIndexField.DOCID.value()));
							page.put(BOTTOM_RESULT_SCORE, scoreDocs[index].score);
							page.put("page", i + 1 + pageArraySize);
							page.put("index", i + pageArraySize);
							pageArray.add(page);
							// DataManager.getImplProv().getLogger().info("Loaded doc Nr."+index+" title:
							// "+doc.get(IndexSearchConstants.TITLE));
						} catch (IOException e) {
							DataManager.getImplProv().getLogger().debug("IOException when searching a specific version: "+e.getMessage());
						}
					} else {
						break;
					}
				}
			}
		}
		result.put("pageArray", pageArray);
		try {
			result.put("bottomResult",
					manager.searcher.doc(scoreDocs[pageSize - 1].doc).get(EnumIndexField.DOCID.value()));
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("IOException when searching a specific version: "+e.getMessage());
		} finally {
			try {
				if (manager != null) {
					((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().release(manager);
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger().debug("Couldnt close SearcherManager: "+e.getMessage());
			}
		}
		result.put(BOTTOM_RESULT_SCORE, scoreDocs[pageSize - 1].score);
		if(Thread.interrupted()) {
			result.put("facets", new JSONArray());
		}else {
			try {
				internalCountDownLatch.await(60, TimeUnit.SECONDS);
				result.put("facets", innerThread.facets);
			} catch (InterruptedException e) {
				result.put("facets", new JSONArray());
			}
		}
		return result;
	}
	
	/**
	 * Builds a valid Lucene Query from the given wrapped information
	 * @param jsonArray The wrapped query information
	 * @param result The JSONObject to which the parsedQuery will get attached
	 * @return The parsed query
	 */
	private Query buildQueryFromJSON(JSONObject jsonArray, JSONObject result) {
		BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
		String whereToSearch = (String) jsonArray.get("whereToSearch");
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
		QueryParser pars = whereToSearch.equals(EnumIndexField.CONTENT.value())
				? new QueryParser(EnumIndexField.CONTENT.value(), analyzer)
				: new QueryParser(EnumIndexField.ALL.value(), analyzer);
		pars.setDefaultOperator(Operator.AND);
		String existing = (String) jsonArray.get("existingQuery");
		StringJoiner queryJoiner = new StringJoiner(" ");
		if (!existing.equals(""))
			try {
				Query parsedQuery = pars.parse(existing);
				finalQuery.add(parsedQuery, Occur.MUST);
				String queryString = parsedQuery.toString();
				if (queryString.charAt(0) != '+' && queryString.charAt(0) != '-') {
					queryString = '+' + queryString;
				}
				result.put("parsedQuery", queryString.toString());
				queryJoiner.add(queryString);
			} catch (ParseException e) {
				DataManager.getImplProv().getLogger().debug("Parsing error occured: "+e.getMessage());
			}
		JSONArray queries = (JSONArray) jsonArray.get("queries");
		if (queries != null) {
			for (Object query : queries) {
				if (query instanceof String)
					queryJoiner.add((String) query);
			}
		}
		JSONArray filters = (JSONArray) jsonArray.get("filters");
		Query query = null;
		for (Object obj : filters) {
			JSONObject queryData = (JSONObject) obj;
			String type = ((String) queryData.get("type"));
			String keyword = (String) queryData.get("searchterm");
			if (type.equals(EnumIndexField.STARTDATE.value()) || type.equals(EnumIndexField.ENDDATE.value())) {
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
//				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
//				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
//				String lower = DateTools.timeToString(
//						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
//				String upper = DateTools.timeToString(
//						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				query = TermRangeQuery.newStringRange(EnumIndexField.STARTDATE.value(), (String)queryData.get("lower"), (String)queryData.get("upper"), true,
						true);
			} else if (type.equals(EnumIndexField.SIZE.value())) {
				query = TermRangeQuery.newStringRange(EnumIndexField.SIZE.value(),
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						true, true);
			} else if (type.equals(EnumIndexField.FILETYPE.value())) {
				QueryParser queryParser = new QueryParser(type, analyzer);
				queryParser.setDefaultOperator(Operator.AND);
				keyword.replace("\\", "");
				try {
					query = queryParser.parse(QueryParser.escape(keyword));
				} catch (ParseException e) {
					DataManager.getImplProv().getLogger().debug("Parsing error: "+e.getMessage());
				}
			}
			if(query != null) {
				DataManager.getImplProv().getLogger().debug(query.toString());
			}
			queryJoiner.add(Occur.MUST.toString() + query.toString());
			finalQuery.add(query, Occur.MUST);
		}

		String hitType = (String) jsonArray.get("hitType");
		if (hitType.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
			query = new TermQuery(
					new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.PUBLICREFERENCE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.FILE)) {
			query = new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.FILE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
			query = new TermQuery(
					new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.DIRECTORY));
		} else {
			return null;
		}
		finalQuery.add(query, Occur.MUST);
		queryJoiner.add(query.toString());
		DataManager.getImplProv().getLogger().debug("Builded queryjoiner_ " + queryJoiner.toString());
		BooleanQuery.setMaxClauseCount(10000);
		try {
			return pars.parse(queryJoiner.toString());
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Parsing Error: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Producses a query if the parameters contain the keyword "Content"
	 * @param existingQuery User entered Query
	 * @param jsonArray List of additional user entered queries
	 * @return A Query that only contains useful terms for highlighting
	 */
	private Query extractContentQuery(String existingQuery, JSONArray jsonArray) {
		QueryParser parser = new QueryParser(EnumIndexField.CONTENT.value(), ((FileSystemImplementationProvider)DataManager.getImplProv()).getWriter().getAnalyzer());
		parser.setDefaultOperator(Operator.AND);
		StringJoiner contentQuery = new StringJoiner(" ");
		String parsedQuery = "";
		if(existingQuery.length() > 0) {
			try {
				parsedQuery = parser.parse(existingQuery).toString();
			} catch (ParseException e) {
				try {
					parsedQuery = parser.parse(QueryParser.escape(existingQuery)).toString();
				} catch (ParseException e1) {
					DataManager.getImplProv().getLogger().debug(e.getMessage());
				}
			}
			if(parsedQuery.contains(EnumIndexField.CONTENT.value())) {
				contentQuery.add(parsedQuery);
			}	
		}
		for(Object query : jsonArray) {
			if(query instanceof String) {
				if(((String)query).contains(EnumIndexField.CONTENT.value())) {
					contentQuery.add((String)query);
				}
			}
		}
		String toParse = contentQuery.toString();
		if(toParse.length() > 0) {
			try {
				return parser.parse(contentQuery.toString());
			} catch (ParseException e) {
				try {
					return parser.parse(QueryParser.escape(contentQuery.toString()));
				} catch (ParseException e1) {
					return null;
				}
			}
		}else {
			return null;
		}
	}

	public Query parseToLuceneQuery(JSONObject requestObject) {
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
		QueryParser pars = new QueryParser(EnumIndexField.ALL.value(), analyzer);
		pars.setDefaultOperator(Operator.AND);
		String type = ((String) requestObject.get("type"));
		String keyword = (String) requestObject.get("searchterm");
		QueryParser queryParser = new QueryParser(type, analyzer);
		queryParser.setDefaultOperator(Operator.AND);
		try {
			if (type.equals("humanQuery")) {
				if (keyword.charAt(0) != '+' && keyword.charAt(0) != '-') {
					keyword = '+' + keyword;
				}
				return pars.parse(QueryParser.escape((String) requestObject.get("searchterm")));
			}
			if ((boolean) requestObject.get("fuzzy")) {
				keyword = keyword + '~';
			}
			keyword = Occur.valueOf((String) requestObject.get("occur")) + keyword;
			return queryParser.parse(keyword);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			DataManager.getImplProv().getLogger().debug("Parsing error occured: "+e.getMessage());
		}
		return null;
	}


	public String buildQueryFromJSON(JSONObject json) {
		QueryParser pars = new QueryParser(EnumIndexField.ALL.value(),
				((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer());
		pars.setDefaultOperator(Operator.AND);
		StringJoiner queryJoiner = new StringJoiner(" ");
		JSONArray filters = (JSONArray) json.get("filters");
		Query luceneQuery = null;
		for (Object obj : filters) {
			JSONObject queryData = (JSONObject) obj;
			String type = ((String) queryData.get("type"));
			String keyword = (String) queryData.get("searchterm");
			if (type.equals(EnumIndexField.STARTDATE.value()) || type.equals(EnumIndexField.ENDDATE.value())) {
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);
//				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
//				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
//				String lower = DateTools.timeToString(
//						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
//				String upper = DateTools.timeToString(
//						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				luceneQuery = TermRangeQuery.newStringRange(EnumIndexField.STARTDATE.value(), (String)queryData.get("lower"), (String)queryData.get("upper"), true,
						true);
			} else if (type.equals(EnumIndexField.SIZE.value())) {
				luceneQuery = TermRangeQuery.newStringRange(EnumIndexField.SIZE.value(),
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						true, true);
			} else if (type.equals(EnumIndexField.FILETYPE.value())) {
				keyword.replace("\\", "");
				String fileTypeQuery = EnumIndexField.FILETYPE + ":" + keyword;
				try {
					luceneQuery = pars.parse(QueryParser.escape(fileTypeQuery));
				} catch (ParseException e) {
					DataManager.getImplProv().getLogger().debug("Parsing error occured: "+e.getMessage());
				}
			}
			if (luceneQuery != null) {
				DataManager.getImplProv().getLogger().debug(luceneQuery.toString());
				queryJoiner.add(Occur.MUST.toString() + luceneQuery.toString());
			}
		}
		String hitType = (String) json.get("hitType");
		if (hitType.equals(PublicVersionIndexWriterThread.PUBLICREFERENCE)) {
			luceneQuery = new TermQuery(
					new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.PUBLICREFERENCE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.FILE)) {
			luceneQuery = new TermQuery(
					new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.FILE));
		} else if (hitType.equals(PublicVersionIndexWriterThread.DIRECTORY)) {
			luceneQuery = new TermQuery(
					new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.DIRECTORY));
		} else {
			return null;
		}
		queryJoiner.add(luceneQuery.toString());
		return queryJoiner.toString();
	}


	public JSONArray drillDown(String query) {
		try {
			QueryParser queryParser = new QueryParser(EnumIndexField.ALL.value(),
					((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer());
			queryParser.setDefaultOperator(Operator.AND);
			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();

			FacetsConfig config = ((FileSystemImplementationProvider)DataManager.getImplProv()).getFacetsConfig();
			DrillDownQuery drillQuery = new DrillDownQuery(config, queryParser.parse(query));
			FacetsCollector fc = new FacetsCollector();
			FacetsCollector.search(manager.searcher, drillQuery, 50000, fc);
			List<FacetResult> results = new ArrayList<>();

			try {				
				Facets facets = new FastTaxonomyFacetCounts(manager.taxonomyReader, config, fc);
				results.add(facets.getTopChildren(5000, EnumIndexField.CREATORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.CONTRIBUTORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.SUBJECT.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.TITLE.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.DESCRIPTION.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.FILETYPE.value()));
			} catch (Exception e) {
				((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().release(manager);
				return new JSONArray();
			}
			((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().release(manager);
			JSONArray result = new JSONArray();
			for (FacetResult facet : results) {
				if (facet == null)
					continue;
				JSONObject jsonFacet = new JSONObject();
				if (facet.dim.equals(EnumIndexField.CREATORNAME.value()))
					jsonFacet.put("category", EnumIndexField.CREATOR.value());
				else if (facet.dim.equals(EnumIndexField.CONTRIBUTORNAME.value()))
					jsonFacet.put("category", EnumIndexField.CONTRIBUTOR.value());
				else
					jsonFacet.put("category", facet.dim);
				jsonFacet.put("sortedByHits", facet.labelValues);
				result.add(jsonFacet);
			}
			return result;
		} catch (IOException ioError) {
			DataManager.getImplProv().getLogger().debug("Low level index error when retrieving Facets: "+ioError.getMessage());
		} catch (ParseException parserError) {
			DataManager.getImplProv().getLogger().debug("Parsing error occured: "+parserError.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	

	public HashMap<String, String> getInitialFilterOptions() {
		try {
			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();
			FacetsConfig config = ((FileSystemImplementationProvider)DataManager.getImplProv()).getFacetsConfig();
			FacetsCollector fc = new FacetsCollector();
			BooleanQuery booleanQuery = new BooleanQuery.Builder()
				    .add(new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(),PublicVersionIndexWriterThread.FILE)), BooleanClause.Occur.SHOULD)
				    .add(new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(),PublicVersionIndexWriterThread.PUBLICREFERENCE)), BooleanClause.Occur.SHOULD)
				    .build();
			FacetsCollector.search(manager.searcher, new DrillDownQuery(config,booleanQuery ), 50000, fc);
			List<FacetResult> results = new ArrayList<>();
			try {				
				Facets facets = new FastTaxonomyFacetCounts(manager.taxonomyReader, config, fc);
				results.add(facets.getTopChildren(Integer.MAX_VALUE, EnumIndexField.SIZE.value()));
				results.add(facets.getTopChildren(Integer.MAX_VALUE, EnumIndexField.STARTDATE.value()));
			} catch (Exception e) {
				((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().release(manager);
				return new HashMap<String, String>();
			}finally {
				((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().release(manager);	
			}
			HashMap<String, String> result = new HashMap<String, String>();
			for (FacetResult facet : results) {
				if (facet == null)
					continue;
				if(facet.dim.equals(EnumIndexField.SIZE.value())) {
					long maxFileSize = 0;
					for(LabelAndValue lav : facet.labelValues) {
						long size = Long.valueOf(lav.label.replaceFirst("^0+(?!$)", ""));
						if(size > maxFileSize) {
							maxFileSize = size;
						}
					}
					result.put(MAX_FILE_SIZE, Long.toString(maxFileSize));
				}else if(facet.dim.equals(EnumIndexField.STARTDATE.value())) {
					int minYear = Integer.MAX_VALUE;
					int maxYear = 0;
					for(LabelAndValue lav : facet.labelValues) {
						int year = Integer.valueOf(lav.label.substring(0,4));
						if(year <= minYear) {
							minYear = year;
						}
						if(year > maxYear) {
							maxYear = year;
						}
					}
					result.put(MIN_YEAR, Integer.toString(minYear));
					result.put(MAX_YEAR, Integer.toString(maxYear));
				}
			}
			return result;
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("Low level index error when generating the initial filter values: "+e.getMessage());
		}
		return new HashMap<String, String>();
	}

	


	public JSONObject getHighlightedSections(String docId, String queryString) {
		try {
			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();
			Query query = new TermQuery(new Term(EnumIndexField.DOCID.value(), docId));
			ScoreDoc[] hits = manager.searcher.search(query, 1).scoreDocs;
			JSONObject result = new JSONObject();
			if (hits.length > 0) {
				Analyzer analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter()
						.getAnalyzer();
				QueryParser parser = new QueryParser(EnumIndexField.CONTENT.value(), analyzer);
				Highlighter highlighter = new Highlighter(
						new SimpleHTMLFormatter("<span style='color:#0275d8; font-weight:bold;'>", "</span>"),
						new QueryScorer(parser.parse(queryString)));
				highlighter.setTextFragmenter(new SimpleFragmenter(300));// 48
				Document document = manager.searcher.doc(hits[0].doc);
				TokenStream tokenStream = TokenSources.getAnyTokenStream(manager.searcher.getIndexReader(), hits[0].doc,
						EnumIndexField.CONTENT.value(), analyzer);
				TextFragment[] fragments;
				try {
					fragments = highlighter.getBestTextFragments(tokenStream,
							document.get(EnumIndexField.CONTENT.value()), false, 10);
					List<String> snipets = new ArrayList<>();
					for (TextFragment fragment : fragments) {
						if (fragment.getScore() > 0.0) {
							snipets.add(fragment.toString());
						}
					}
					result.put(EnumIndexField.CONTENT.value(), snipets);
				} catch (IOException | InvalidTokenOffsetsException e) {
					result.put("msg", "There was an Error, Document not found.");
				}

			} else {
				result.put("msg", "There was an Error, Document not found.");
			}
			return result;
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("IOError occured when working with Lucene index: "+e.getMessage());
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().debug("Parsing error occured: "+e.getMessage());
		}
		return new JSONObject();
	}

	public HashSet<Integer> searchByKeyword(String keyword, boolean fuzzy, String entityType) {
		BooleanQuery.setMaxClauseCount(10000);
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		Analyzer standardAnalyzer = new StandardAnalyzer(stopSet);
		String[] fields = { EnumIndexField.TITLE.value(), EnumIndexField.DESCRIPTION.value(),
				EnumIndexField.COVERAGE.value(), EnumIndexField.IDENTIFIER.value(), EnumIndexField.SIZE.value(),
				EnumIndexField.TYPE.value(), EnumIndexField.LANGUAGE.value(), EnumIndexField.CREATOR.value(),
				EnumIndexField.LEGALPERSON.value(), EnumIndexField.ALGORITHM.value(), EnumIndexField.CHECKSUM.value(),
				EnumIndexField.SUBJECT.value(), EnumIndexField.RELATION.value(), EnumIndexField.MIMETYPE.value(),
				EnumIndexField.STARTDATE.value(), EnumIndexField.ENDDATE.value(), EnumIndexField.RELATIONTYPE.value(),
				EnumIndexField.RELATEDIDENTIFIERTYPE.value() };
		org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,
				standardAnalyzer);
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		org.apache.lucene.search.Query luceneQuery = null;
		if (fuzzy) {
			keyword += TILDE;
		}
		try {
			luceneQuery = parser.parse(keyword);
		} catch (ParseException e2) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.debug("Was not able to Parse: \n" + keyword);
			return new HashSet<>();
		}
		if (luceneQuery == null) {
			return new HashSet<>();
		}
	
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser queryType = new QueryParser(EnumIndexField.CHECKSUM.value(), standardAnalyzer);
		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
		booleanQuery.add(new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(), entityType)), Occur.FILTER);
		try {
			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();
			ScoreDoc[] hits2 = manager.searcher.search(booleanQuery.build(), 50000).scoreDocs;
			HashSet<Integer> ids = new HashSet<>();
			for (int i = 0; i < hits2.length; i++) {
				Document doc = null;
				doc = manager.searcher.doc(hits2[i].doc);
				ids.add(Integer.parseInt((doc.get(EnumIndexField.PUBLICID.value()))));
			}
			return ids;
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("IOException when searching: "+e.getMessage());
		}
		return new HashSet<Integer>();
	}

	@Override
	public List<Taxon> taxonSearch(String internalId) {
		SearcherAndTaxonomy manager = null;
		try {
			manager = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSearcherTaxonomyManagerForPublicReferences().acquire();
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().debug("Lucene Reference Manager is already closed "+e.getMessage());
			return new ArrayList<Taxon>();
		}
		List<Taxon> list = new ArrayList<Taxon>();
		
		List<Taxon> taxons = Stream.of(EnumTaxon.values())
			    .map(EnumTaxon::value)
			    .collect(Collectors.toList());
		
		try {
			for(Taxon taxon : taxons) {
				
				BooleanQuery.Builder idQuery = new BooleanQuery.Builder()
				.add(new TermQuery(new Term(EnumIndexField.INTERNALID.value(), internalId)), Occur.MUST)
				.add(new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(), PublicVersionIndexWriterThread.PUBLICREFERENCE)), Occur.MUST);
				
				BooleanQuery.Builder idTaxonQuery = new BooleanQuery.Builder()
				.add(idQuery.build(), Occur.MUST);
				
				BooleanQuery.Builder taxonQuery = new BooleanQuery.Builder();
				
				for(String keyword : taxon.getSynonyms()) {
					taxonQuery.add(new TermQuery(new Term(EnumIndexField.TITLE.value(), keyword)), Occur.SHOULD)
							.add(new TermQuery(new Term(EnumIndexField.DESCRIPTION.value(), keyword)), Occur.SHOULD)
							.add(new TermQuery(new Term(EnumIndexField.SUBJECT.value(), keyword)), Occur.SHOULD);
				}	
				idTaxonQuery.add(taxonQuery.build(), Occur.MUST);
				Query q = idTaxonQuery.build();
				if(manager.searcher.search(q, 1).totalHits.value > 0) {
					list.add(taxon);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return list;

	}

}
