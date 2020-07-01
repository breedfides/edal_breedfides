/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.review;


import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatusEvaluation;

/**
 * @author lange
 * 
 */
public class TestReviewerTruthTableEvaluation {

    /**
     * @param group_eval
     * @throws IOException
     */
    protected void checkEvaluationForTruthTable(
	    final ReviewStatusEvaluation eval, final String truth_table)
	    throws IOException {
	final Collection<Triple<ReviewStatusType, ReviewStatusType, ReviewStatusType>> test_truth_table = ReviewStatusEvaluation
		.loadTruthTable(truth_table);
	Pair<ReviewStatusType, ReviewStatusType> validate;
	for (final Triple<ReviewStatusType, ReviewStatusType, ReviewStatusType> triple : test_truth_table) {
	    validate = new ImmutablePair<ReviewStatusType, ReviewStatusType>(
		    triple.getLeft(), triple.getMiddle());
	    System.out
		    .println(validate.toString() + ": " + eval.eval(validate));
	    Assertions.assertEquals(eval.eval(validate), triple.getRight());
	}
    }

    /**
     * Test method for
     * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatusEvaluation#createReviewStatusEvaluationForGroups()}
     * .
     */
    @Test
    public void testCreateReviewStatusEvaluationForGroups() {
	try {

	    this.checkEvaluationForTruthTable(ReviewStatusEvaluation
		    .createReviewStatusEvaluationForGroups(),
		    ReviewStatusEvaluation.PROPERTY_FILE_GROUP_TRUTH_TABLE);
	    this.checkEvaluationForTruthTable(
		    ReviewStatusEvaluation
			    .createReviewStatusEvaluationForDatamanager(),
		    ReviewStatusEvaluation.PROPERTY_FILE_DATAMANAGER_TRUTH_TABLE);

	} catch (final IOException e) {
	    Assertions.fail(e.toString() + " :" + e.getMessage());
	}
    }

}
