/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
import org.junit.Assert;
import org.junit.Test;

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
	    Assert.assertEquals(eval.eval(validate), triple.getRight());
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
	    Assert.fail(e.toString() + " :" + e.getMessage());
	}
    }

}
