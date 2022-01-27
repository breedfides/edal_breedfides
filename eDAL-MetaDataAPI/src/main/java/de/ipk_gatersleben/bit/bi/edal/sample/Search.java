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
package de.ipk_gatersleben.bit.bi.edal.sample;


import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager.SearcherAndTaxonomy;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.EnumIndexField;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;

public class Search {
	
	public static final String MINYEAR = "minYear";
	public static final String MAXYEAR = "maxYear";
	public static final String MAXFILESIZE = "maxSize";
	private static final String[] METADATAFIELDS = { EnumIndexField.TITLE.value(), EnumIndexField.SIZE.value(),
			EnumIndexField.VERSIONID.value(), EnumIndexField.ENTITYID.value(), EnumIndexField.PRIMARYENTITYID.value(),
			EnumIndexField.ENTITYTYPE.value(), EnumIndexField.DOCID.value(),EnumIndexField.PUBLICID.value(),
			EnumIndexField.REVISION.value(), EnumIndexField.INTERNALID.value(),EnumIndexField.CONTENT.value(),
			EnumIndexField.FILETYPE.value() };

	@SuppressWarnings("unchecked")
	public static JSONObject advancedSearch(JSONObject requestObject) {
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
			manager = DataManager.getSearchManager().acquire();
		} catch (IOException e2) {
			e2.printStackTrace();
			return result;
		}
		DataManager.getImplProv().getLogger().debug(buildedQuery.toString());
		TopDocs topDocs = null;
		int currentPageNumber = ((int) (long) requestObject.get("displayedPage"));
		int pageArraySize = ((int) (long) requestObject.get("pageArraySize"));
		int pageIndex = ((int) (long) requestObject.get("pageIndex"));
		int pageSize = ((int) (long) requestObject.get("pageSize"));
		try {
			if (pageArraySize == 0 || currentPageNumber == 1) {
				DataManager.getImplProv().getLogger()
						.debug("Builded QUery advanced search: " + buildedQuery.toString());
				topDocs = manager.searcher.search(buildedQuery, pageSize * 3);
			} else {
				ScoreDoc bottomScoreDoc = null;
				bottomScoreDoc = manager.searcher.search(new TermQuery(
						new Term(EnumIndexField.DOCID.value(), (String) requestObject.get("bottomResultId"))),
						5).scoreDocs[0];
				bottomScoreDoc.score = ((float) (double) requestObject.get("bottomResultScore"));
				topDocs = manager.searcher.searchAfter(bottomScoreDoc, buildedQuery, pageSize * 3);
			}
			if (requestObject.get("hitSize") == null) {
				TotalHitCountCollector collector = new TotalHitCountCollector();
				manager.searcher.search(buildedQuery, collector);
				result.put("hitSize", collector.getTotalHits());
			} else {
				result.put("hitSize", requestObject.get("hitSize"));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put("hitSizeDescription", "");
		result.put("displayedPage", (long) requestObject.get("displayedPage"));
		result.put("pageIndex", pageIndex);
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
		Set<String> fields = Set.of(Search.METADATAFIELDS);
		for (int i = 0; i < pageSize; i++) {
			try {
				doc = manager.searcher.doc(scoreDocs[i].doc, fields);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				obj.put("title", reference.getVersion().getMetaData().toString());
				obj.put("fileName", "");
				obj.put("ext", "");
				obj.put("type", "record");
			} else {
				try {
					obj.put("doi", reference.getAssignedID());
				} catch (PublicReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			page.put("bottomResultScore", 0);
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
							page.put("bottomResultScore", scoreDocs[index].score);
							page.put("page", i + 1);
							page.put("index", i);
							pageArray.add(page);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
							page.put("bottomResultScore", scoreDocs[index].score);
							page.put("page", i + 1 + pageArraySize);
							page.put("index", i + pageArraySize);
							pageArray.add(page);
							// DataManager.getImplProv().getLogger().info("Loaded doc Nr."+index+" title:
							// "+doc.get(IndexSearchConstants.TITLE));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (manager != null) {
					DataManager.getSearchManager().release(manager);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result.put("bottomResultScore", scoreDocs[pageSize - 1].score);
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
	 * Producses a query if the parameters contain the keyword "Content"
	 * @param existingQuery User entered Query
	 * @param jsonArray List of additional user entered queries
	 * @return A Query that only contains useful terms for highlighting
	 */
	private static Query extractContentQuery(String existingQuery, JSONArray jsonArray) {
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

	public static Query parseToLuceneQuery(JSONObject jsonArray) {
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
		QueryParser pars = new QueryParser(EnumIndexField.ALL.value(), analyzer);
		pars.setDefaultOperator(Operator.AND);
		String type = ((String) jsonArray.get("type"));
		String keyword = (String) jsonArray.get("searchterm");
		QueryParser queryParser = new QueryParser(type, analyzer);
		queryParser.setDefaultOperator(Operator.AND);
		try {
			if (type.equals("humanQuery")) {
				if (keyword.charAt(0) != '+' && keyword.charAt(0) != '-') {
					keyword = '+' + keyword;
				}
				return pars.parse(QueryParser.escape((String) jsonArray.get("searchterm")));
			}
			if ((boolean) jsonArray.get("fuzzy")) {
				keyword = keyword + '~';
			}
			keyword = Occur.valueOf((String) jsonArray.get("occur")) + keyword;
			return queryParser.parse(keyword);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray builQueryAndDrillDown(JSONObject json) {
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
			if (type.equals(EnumIndexField.STARTDATE) || type.equals(EnumIndexField.ENDDATE)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
				String lower = DateTools.timeToString(
						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				String upper = DateTools.timeToString(
						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				luceneQuery = TermRangeQuery.newStringRange(EnumIndexField.STARTDATE.value(), lower, upper, false,
						false);
			} else if (type.equals(EnumIndexField.SIZE)) {
				luceneQuery = TermRangeQuery.newStringRange(EnumIndexField.SIZE.value(),
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						false, false);
			} else if (type.equals(EnumIndexField.FILETYPE)) {
				keyword.replace("\\", "");
				String fileTypeQuery = EnumIndexField.FILETYPE + ":" + keyword;
				try {
					luceneQuery = pars.parse(QueryParser.escape(fileTypeQuery));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		return drillDown(queryJoiner.toString());
	}

	public static JSONArray drillDown(String query) {
		try {
			QueryParser queryParser = new QueryParser(EnumIndexField.ALL.value(),
					((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer());
			queryParser.setDefaultOperator(Operator.AND);
			SearcherAndTaxonomy manager = DataManager.getSearchManager().acquire();

			FacetsConfig config = DataManager.getFacetsConfig();
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
				DataManager.getSearchManager().release(manager);
				return new JSONArray();
			}
			DataManager.getSearchManager().release(manager);
			JSONArray result = new JSONArray();
			for (FacetResult facet : results) {
				if (facet == null)
					continue;
				JSONObject jsonFacet = new JSONObject();
				if (facet.dim.equals(EnumIndexField.CREATORNAME.value()))
					jsonFacet.put("category", EnumIndexField.PERSON.value());
				else if (facet.dim.equals(EnumIndexField.CONTRIBUTORNAME.value()))
					jsonFacet.put("category", EnumIndexField.CONTRIBUTOR.value());
				else
					jsonFacet.put("category", facet.dim);
				jsonFacet.put("sortedByHits", facet.labelValues);
				result.add(jsonFacet);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	public static HashMap<String, String> getInitialFilterOptions() {
		try {
			SearcherAndTaxonomy manager = DataManager.getSearchManager().acquire();
			FacetsConfig config = DataManager.getFacetsConfig();
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
				e.printStackTrace();
				DataManager.getSearchManager().release(manager);
				return new HashMap<String, String>();
			}finally {
				DataManager.getSearchManager().release(manager);	
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
					result.put(Search.MAXFILESIZE, Long.toString(maxFileSize));
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
					result.put(Search.MINYEAR, Integer.toString(minYear));
					result.put(Search.MAXYEAR, Integer.toString(maxYear));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, String>();
	}

	public static Query buildQueryFromJSON(JSONObject jsonArray, JSONObject result) {
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
			} catch (ParseException e1) {
				e1.printStackTrace();
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
			if (type.equals(EnumIndexField.STARTDATE) || type.equals(EnumIndexField.ENDDATE)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
				LocalDateTime lowerDate = LocalDate.parse((String) queryData.get("lower"), formatter).atStartOfDay();
				LocalDateTime upperDate = LocalDate.parse((String) queryData.get("upper"), formatter).atStartOfDay();
				String lower = DateTools.timeToString(
						ZonedDateTime.of(lowerDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				String upper = DateTools.timeToString(
						ZonedDateTime.of(upperDate, ZoneId.of("UTC")).toInstant().toEpochMilli(), Resolution.YEAR);
				query = TermRangeQuery.newStringRange(EnumIndexField.STARTDATE.value(), lower, upper, false, false);
			} else if (type.equals(EnumIndexField.SIZE)) {
				query = TermRangeQuery.newStringRange(EnumIndexField.SIZE.value(),
						String.format("%014d", queryData.get("lower")), String.format("%014d", queryData.get("upper")),
						false, false);
			} else if (type.equals(EnumIndexField.FILETYPE)) {
				QueryParser queryParser = new QueryParser(type, analyzer);
				queryParser.setDefaultOperator(Operator.AND);
				keyword.replace("\\", "");
				try {
					query = queryParser.parse(QueryParser.escape(keyword));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			DataManager.getImplProv().getLogger().debug(query.toString());
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
	 * Method to get the top 10 highlighted passages for a Document
	 * 
	 * @param doc The Document to be highlighted
	 * @param q   The Query that should contain a keyword for highlighting
	 * @return The Highlighted passages wrapped in a JSONObject
	 * @throws IOException
	 * @throws ParseException
	 */
	public static JSONObject getHighlightedSections(String doc, String q) throws IOException, ParseException {
		SearcherAndTaxonomy manager = DataManager.getSearchManager().acquire();
		Query query = new TermQuery(new Term(EnumIndexField.DOCID.value(), doc));
		ScoreDoc[] hits = manager.searcher.search(query, 1).scoreDocs;
		JSONObject result = new JSONObject();
		if (hits.length > 0) {
			Analyzer analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter()
					.getAnalyzer();
			QueryParser parser = new QueryParser(EnumIndexField.CONTENT.value(), analyzer);
			Highlighter highlighter = new Highlighter(
					new SimpleHTMLFormatter("<span style='color:#0275d8; font-weight:bold;'>", "</span>"),
					new QueryScorer(parser.parse(q)));
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
	}

	public static HashSet<Integer> searchByKeyword(String keyword, boolean fuzzy, String entityType) {
		final long startTime = System.currentTimeMillis();
		IndexReader reader = null;
		try {
			Directory indexDirectory = FSDirectory.open(Paths.get(
					((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
					"Master_Index"));
			reader = DirectoryReader.open(indexDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BooleanQuery.setMaxClauseCount(10000);
		CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
		final CharArraySet stopSet = new CharArraySet(
				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
		stopSet.addAll(defaultStopWords);
		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
		Analyzer standardAnalyzer = new StandardAnalyzer(stopSet);
		String[] fields = { EnumIndexField.TITLE.value(), EnumIndexField.DESCRIPTION.value(),
				EnumIndexField.COVERAGE.value(), EnumIndexField.IDENTIFIER.value(), EnumIndexField.SIZE.value(),
				EnumIndexField.TYPE.value(), EnumIndexField.LANGUAGE.value(), EnumIndexField.PERSON.value(),
				EnumIndexField.LEGALPERSON.value(), EnumIndexField.ALGORITHM.value(), EnumIndexField.CHECKSUM.value(),
				EnumIndexField.SUBJECT.value(), EnumIndexField.RELATION.value(), EnumIndexField.MIMETYPE.value(),
				EnumIndexField.STARTDATE.value(), EnumIndexField.ENDDATE.value(), EnumIndexField.RELATIONTYPE.value(),
				EnumIndexField.RELATEDIDENTIFIERTYPE.value() };
		org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,
				standardAnalyzer);
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		org.apache.lucene.search.Query luceneQuery = null;
		if (fuzzy) {
			keyword += "~";
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

		/** A possible way to search for Files AND rootDirectories */
//		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
//		BooleanQuery.Builder subbooleanQuery = new BooleanQuery.Builder();
//		QueryParser queryType = new QueryParser(IndexSearchConstants.CHECKSUM,standardAnalyzer);
//		queryType.setDefaultOperator(QueryParser.OR_OPERATOR);
//		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
//		try {
//			subbooleanQuery.add(queryType.parse(new TermQuery(new Term(IndexSearchConstants.ENTITYTYPE, entityType)).toString()), Occur.SHOULD);
//			subbooleanQuery.add(queryType.parse(new TermQuery(new Term(IndexSearchConstants.ENTITYTYPE, PublicVersionIndexWriterThread.INDIVIDUALFILE)).toString()), Occur.SHOULD);
//			booleanQuery.add(queryType.parse(subbooleanQuery.build().toString()),Occur.MUST);
//		} catch (ParseException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}		
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		QueryParser queryType = new QueryParser(EnumIndexField.CHECKSUM.value(), standardAnalyzer);
		booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);
		booleanQuery.add(new TermQuery(new Term(EnumIndexField.ENTITYTYPE.value(), entityType)), Occur.FILTER);
		try {
			SearcherAndTaxonomy manager = DataManager.getSearchManager().acquire();
			ScoreDoc[] hits2 = manager.searcher.search(booleanQuery.build(), 50000).scoreDocs;
			HashSet<Integer> ids = new HashSet<>();
			for (int i = 0; i < hits2.length; i++) {
				Document doc = null;
				try {
					doc = manager.searcher.doc(hits2[i].doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ids.add(Integer.parseInt((doc.get(EnumIndexField.PUBLICID.value()))));
			}
			return ids;
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return new HashSet<Integer>();
	}

}
