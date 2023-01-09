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
/**
 * 
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;

/**
 * abstract class to evaluate review statuses and compute a review result it use
 * a truth table that is specific for use case and must be parameterized in
 * constructor
 * 
 * @author lange
 * 
 */
public class ReviewStatusEvaluation {

    private static final String TRUTH_TABLE_FILE_VALUE_DELIMITER = ",";
    public static final String PROPERTY_FILE_GROUP_TRUTH_TABLE = "group_truth_table.prop";
    public static final String PROPERTY_FILE_DATAMANAGER_TRUTH_TABLE = "datamanager_truth_table.prop";

    /**
     * create truth table for final data manager decision
     * 
     * @return {@link java.util.Collection} of
     *         {@link org.apache.commons.lang3.tuple.Triple}: comprise a three
     *         columns formated truth table (research group decision,group
     *         leader, group leader assistant)
     * @throws IOException if failed
     */
    public static ReviewStatusEvaluation createReviewStatusEvaluationForDatamanager()
	    throws IOException {

	return new ReviewStatusEvaluation(
		ReviewStatusEvaluation
			.loadTruthTable(ReviewStatusEvaluation.PROPERTY_FILE_DATAMANAGER_TRUTH_TABLE));
    }

    /**
     * create truth table for group decision
     * 
     * @return {@link java.util.Collection} of
     *         {@link org.apache.commons.lang3.tuple.Triple}: comprise a three
     *         columns formated truth table (research group decision,group
     *         leader, group leader assistant)
     * @throws IOException if failed
     */
    public static ReviewStatusEvaluation createReviewStatusEvaluationForGroups()
	    throws IOException {

	return new ReviewStatusEvaluation(
		ReviewStatusEvaluation
			.loadTruthTable(ReviewStatusEvaluation.PROPERTY_FILE_GROUP_TRUTH_TABLE));
    }

    /**
     * load truth table from {@link java.util.Properties} file
     * 
     * 
     * @param truth_file the table to load
     * @return {@link java.util.Collection} of
     *         {@link org.apache.commons.lang3.tuple.Triple}: comprise a three
     *         columns formated truth table (research group decision,group
     *         leader, group leader assistant)
     * @throws IOException if failed
     */
    public static Collection<Triple<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>> loadTruthTable(
	    final String truth_file) throws IOException {
	final Properties properties_group_truth_table = new Properties();

	final InputStream in = ReviewStatusEvaluation.class
		.getResourceAsStream(truth_file);
	if (in == null) {
	    throw new IOException("truth table file not found: " + truth_file);
	}

	final Collection<Triple<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>> group_truth_table = new ArrayList<Triple<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>>(
		ReviewStatusType.values().length
			* ReviewStatusType.values().length);

	properties_group_truth_table.load(in);
	String R;
	String[] review_statuses;
	String L;
	String M;
	Triple<ReviewStatusType, ReviewStatusType, ReviewStatusType> row;
	final Set<Entry<Object, Object>> truth_rows = properties_group_truth_table
		.entrySet();
	for (final Entry<Object, Object> entry : truth_rows) {
	    R = entry.getValue().toString().toUpperCase();

	    review_statuses = entry
		    .getKey()
		    .toString()
		    .split(ReviewStatusEvaluation.TRUTH_TABLE_FILE_VALUE_DELIMITER);
	    L = review_statuses[0].trim().toUpperCase();
	    M = review_statuses[1].trim().toUpperCase();
	    row = new ImmutableTriple<ReviewStatusType, ReviewStatusType, ReviewStatusType>(
		    ReviewStatusType.valueOf(L), ReviewStatusType.valueOf(M),
		    ReviewStatusType.valueOf(R));
	    // System.out.println(row);
	    if (truth_file == ReviewStatusEvaluation.PROPERTY_FILE_DATAMANAGER_TRUTH_TABLE
		    && ReviewStatusType.valueOf(R) == ReviewStatusType.TIMEOUT) {
		throw new IllegalArgumentException("illegal evaluation result "
			+ R + " in truth table " + truth_file + " line "
			+ row.toString());
	    }

	    group_truth_table.add(row);
	}
	// System.out.println(group_truth_table);
	in.close();
	return group_truth_table;
    }

    protected Map<Pair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>, ReviewStatus.ReviewStatusType> truthTable;

    protected ReviewStatusEvaluation() {
	this.truthTable = new TreeMap<Pair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>, ReviewStatus.ReviewStatusType>();
    }

    protected ReviewStatusEvaluation(
	    final Collection<Triple<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>> truthTable) {
	this();
	for (final Triple<ReviewStatusType, ReviewStatusType, ReviewStatusType> triple : truthTable) {
	    this.truthTable
		    .put(new ImmutablePair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>(
			    triple.getLeft(), triple.getMiddle()), triple
			    .getRight());
	}
    }

    public ReviewStatus.ReviewStatusType eval(
	    final Pair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType> configuration) {
	// System.out.println("Eval: " + configuration + " = "
	// + this.truthTable.get(configuration));
	return this.truthTable.get(configuration);
    }

}
