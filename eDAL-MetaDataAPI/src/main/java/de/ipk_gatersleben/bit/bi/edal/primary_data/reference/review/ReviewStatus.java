/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
