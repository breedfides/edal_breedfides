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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface SearchProvider {
	
	/**
	 * Searches the index with the given query information
	 * @param requestObject Wrapped query information: "existingQuery","hitType",
	 *  	  "pageIndex", "pageArraySize", "filters", "queries", "bottomResultId", "displayedPage","whereToSearch"
	 * @return The results and the associated facets wrapped in JSONObjects/Arrays
	 */
	JSONObject advancedSearch(JSONObject parse);

	/**
	 * Parses the given wrapped lucene field and the keyword to a valid lucene query
	 * @param requestObject The query parts wrapped in a JSONObject
	 * @return The parsed Lucene Query
	 */
	Query parseToLuceneQuery(JSONObject parse);

	/**
	 * Drills down on a Lucene query to obtain the related facets
	 * @param query The query String
	 * @return The facets wrapped as JSONObjects in a JSONArray
	 */
	JSONArray drillDown(String buildQueryFromJSON);
	
	/**
	 * Getter for the MIN/MAX values of the creation dates and the max file size that are stored in the index
	 * @return The MIN/MAX values wrapped in a <String, String>HashMap
	 */
	HashMap<String, String> getInitialFilterOptions();
	
	/**
	 * Builds the valid Query string from a JSONObject that contains the following inforamtion: 
	 * "filters", "type", "searchterm", "hitType"
	 * @param json The RequestObject that should contain query information and optional filters
	 * @return A Lucene query string
	 */
	String buildQueryFromJSON(JSONObject parse);

	/**
	 * Method to get the top 10 highlighted passages for a Document
	 * 
	 * @param doc The Document to be highlighted
	 * @param q   The Query that should contain a keyword for highlighting
	 * @return The Highlighted passages wrapped in a JSONObject
	 * @throws IOException 
	 * @throws ParseException 
	 */
	JSONObject getHighlightedSections(String string, String string2) throws IOException, ParseException;

	/**
	 * Searches on all Metadata Lucene fields with the given keyword/query string
	 * @param keyword The given keyword
	 * @param fuzzy if true it will run a fuzzy search
	 * @param entityType The desired Entity types (dataset/file/directory)
	 * @return The found version IDs wrapped in a HashSet
	 */
	HashSet<Integer> searchByKeyword(String keyword, boolean b, String publicreference);


}
