/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.Serializable;
import java.util.Calendar;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;

/**
 * Class to persist the review status with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@Table(name = "REVIEWSTATUS")
public class ReviewStatusImplementation implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String emailAddress;
	private ReviewStatusType statusType;
	private Calendar requestedDate;
	private ReviewerType reviewerType;
	private PublicReferenceImplementation publicReference;

	/**
	 * Default constructor for {@link ReviewStatusImplementation} is necessary
	 * for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public ReviewStatusImplementation() {

	}

	/**
	 * @return the emailAddress of the reviewer
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress of the reviewer to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the statusType of this review
	 */
	@Enumerated(EnumType.STRING)
	public ReviewStatusType getStatusType() {
		return this.statusType;
	}

	/**
	 * @param statusType
	 *            the reviewStatusType to set
	 */
	public void setStatusType(ReviewStatusType statusType) {
		this.statusType = statusType;
	}

	/**
	 * @return the requestedDate
	 */
	public Calendar getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate
	 *            the requestedDate to set
	 */
	public void setRequestedDate(Calendar requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the publicReference
	 */
	@ManyToOne
	public PublicReferenceImplementation getPublicReference() {
		return publicReference;
	}

	/**
	 * @param publicReference
	 *            the publicReference to set
	 */
	public void setPublicReference(PublicReferenceImplementation publicReference) {
		this.publicReference = publicReference;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public ReviewStatus toReviewStatus() {

		ReviewStatus publicReviewStatus = new ReviewStatus();

		try {
			publicReviewStatus.setEmailAddress(new InternetAddress(this
					.getEmailAddress()));
		} catch (AddressException e) {
			e.printStackTrace();
		}

		publicReviewStatus.setRequestedDate(this.getRequestedDate());
		publicReviewStatus.setStatusType(this.getStatusType());
		publicReviewStatus.setReviewerType(this.getReviewerType());

		return publicReviewStatus;

	}

	/**
	 * @return the reviewerType
	 */
	@Enumerated(EnumType.STRING)
	public ReviewerType getReviewerType() {
		return reviewerType;
	}

	/**
	 * @param reviewerType
	 *            the reviewerType to set
	 */
	public void setReviewerType(ReviewerType reviewerType) {
		this.reviewerType = reviewerType;
	}

}
