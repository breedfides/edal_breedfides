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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review;

import java.io.Serializable;
import java.util.Calendar;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;

/**
 * Represent the status of a {@link ApprovalServiceProvider} process.
 * 
 * @author arendd
 */
public class ReviewStatus implements Serializable {

    public enum ReviewerType {
	SCIENTIFIC {

	},

	SUBSTITUTE {

	},

	MANAGING {

	};
    }

    /**
     * {@link Enum} to describe the review status.
     */
    public enum ReviewStatusType {

	/**
	 * The
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * was accepted.
	 */
	ACCEPTED,
	/**
	 * The
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * was rejected.
	 */
	REJECTED,
	/**
	 * The status of
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * was not yet decided and the decision process timed out.
	 */
	TIMEOUT,
	/**
	 * The status of
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * was not yet decided.
	 */
	UNDECIDED;
    }

    private static final long serialVersionUID = 1L;

    private InternetAddress emailAddress;

    private ReviewStatusType statusType;

    private Calendar requestedDate;

    private ReviewerType reviewerType;

    /**
     * @return the emailAddress
     */
    public InternetAddress getEmailAddress() {
	return this.emailAddress;
    }

    /**
     * @return the requestedDate
     */
    public Calendar getRequestedDate() {
	return this.requestedDate;
    }

    /**
     * @return the reviewerType
     */
    public ReviewerType getReviewerType() {
	return this.reviewerType;
    }

    /**
     * @return the statusType
     */
    public ReviewStatusType getStatusType() {
	return this.statusType;
    }

    /**
     * @param emailAddress
     *            the emailAddress to set
     */
    public void setEmailAddress(final InternetAddress emailAddress) {
	this.emailAddress = emailAddress;
    }

    /**
     * @param requestedDate
     *            the requestedDate to set
     */
    public void setRequestedDate(final Calendar requestedDate) {
	this.requestedDate = requestedDate;
    }

    /**
     * @param reviewerType
     *            the reviewerType to set
     */
    public void setReviewerType(final ReviewerType reviewerType) {
	this.reviewerType = reviewerType;
    }

    /**
     * @param statusType
     *            the statusType to set
     */
    public void setStatusType(final ReviewStatusType statusType) {
	this.statusType = statusType;
    }

    @Override
    public String toString() {
	return "ReviewStatus [emailAddress=" + this.emailAddress
		+ ", statusType=" + this.statusType + ", requestedDate="
		+ this.requestedDate.getTime() + "]";
    }

}
