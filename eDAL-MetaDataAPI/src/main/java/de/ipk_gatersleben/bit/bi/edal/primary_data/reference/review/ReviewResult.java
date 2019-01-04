/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review;

import java.util.ArrayList;
import java.util.List;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;

/**
 * Represent the result of the {@link ReviewProcess#review(List)} function.
 * 
 * @author arendd
 * 
 */
public final class ReviewResult {

    private ReviewStatusType _reviewResult;
    private List<ReviewStatus> reviewerStatusList;

    /**
     * Default constructor.
     */
    ReviewResult() {
	this.reviewerStatusList = new ArrayList<>();
    }

    /**
     * Constructor with defined {@link ReviewStatusType} and a {@link List} of
     * {@link ReviewStatus}
     * 
     * @param _reviewResult
     *            the {@link ReviewStatusType}.
     * @param reviewerStatusList
     *            a {@link List} of {@link ReviewStatus}.
     */
    ReviewResult(final ReviewStatusType reviewStatusType,
	    final List<ReviewStatus> reviewerStatusList) {

	this._reviewResult = reviewStatusType;
	this.reviewerStatusList = reviewerStatusList;
    }

    /**
     * @return the reviewerStatusList
     */
    public List<ReviewStatus> getReviewerStatusList() {
	return this.reviewerStatusList;
    }

    /**
     * @return the _reviewResult
     */
    public ReviewStatusType getReviewResult() {
	return this._reviewResult;
    }

    /**
     * @param reviewerStatusList
     *            the reviewerList to set
     */
    void setReviewerStatusList(final List<ReviewStatus> reviewerStatusList) {
	this.reviewerStatusList = reviewerStatusList;
    }

    /**
     * @param _reviewResult
     *            the _reviewResult to set
     */
    void setReviewResult(final ReviewStatusType reviewStatusType) {
	this._reviewResult = reviewStatusType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ReviewResult [_reviewResult=" + this._reviewResult
		+ ", reviewerStatusList=" + this.reviewerStatusList + "]";
    }

}
