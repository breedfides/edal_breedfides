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
