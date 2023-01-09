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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;

/**
 * Provide the implementation for the ApprovalService to get a persistent
 * identifier.
 * 
 * @author arendd
 */
public interface ApprovalServiceProvider {

	/**
	 * Accept a {@link PublicReference} and publish to public.
	 * 
	 * @param ticket
	 *            the ticket number to identify the approved
	 *            {@link PublicReference}.
	 * @param reviewerId
	 *            the ID to identify the reviewer.
	 * @throws EdalApprovalException
	 *             if unable to accept the ticket.
	 */
	void accept(String ticket, int reviewerId) throws EdalApprovalException;

	/**
	 * Initialize a request to approve a {@link PublicReference}.
	 * 
	 * @param reference
	 *            the {@link PublicReference} to approve.
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user
	 * @throws EdalApprovalException
	 *             if unable to initialize the approval request.
	 */
	void approve(PublicReference reference, InternetAddress emailNotificationAddress) throws EdalApprovalException;

	/**
	 * Reject a {@link PublicReference}.
	 * 
	 * @param ticket
	 *            the ticket number to identify the rejected
	 *            {@link PublicReference}.
	 * @param reviewerId
	 *            the ID to identify the reviewer.
	 * @throws EdalApprovalException
	 *             if unable to reject the ticket.
	 */
	void reject(String ticket, int reviewerId) throws EdalApprovalException;

	/**
	 * Load all open {@link PublicReference}s that are not yet accepted or
	 * rejected.
	 * 
	 * @return a {@link Map} with all open {@link PublicReference}s and the
	 *         corresponding {@link List} with the {@link ReviewStatus}.
	 */
	Map<PublicReference, List<ReviewStatus>> getAllOpenReviews();

	/**
	 * Check all {@link ReviewStatus} of the given {@link PublicReference} by
	 * calling {@link ReviewProcess#review(List)}.
	 * 
	 * @param results
	 *            a {@link Map} with all open {@link PublicReference}s and their
	 *            {@link ReviewStatus}.
	 * @throws EdalApprovalException
	 *             if unable to check open review processes.
	 */
	void checkOpenReviews(Map<PublicReference, List<ReviewStatus>> results) throws EdalApprovalException;

	/**
	 * Create an new {@link URL} for a {@link PublicReference} for
	 * {@link URLReference}
	 * 
	 * @param reference
	 *            the {@link PublicReference} to get a {@link URL}
	 * @return a new URL as {@link String}
	 * @throws EdalApprovalException
	 *             if unable to get a {@link URL}
	 */
	String getNewURL(PublicReference reference) throws EdalApprovalException;

	/**
	 * Store the generated {@link DataCiteReference} in to the eDAL system.
	 * 
	 * @param reference
	 *            the corresponding {@link PublicReference}
	 * @param id
	 *            the ID as {@link String}
	 * @param year
	 *            the year of the ID
	 * @return the final ID as {@link String}
	 * @throws EdalApprovalException
	 *             if unable to store the ID
	 */
	String storePersistentID(PublicReference reference, String id, int year) throws EdalApprovalException;

	/**
	 * Get a {@link PublicReference} object by the internal {@link UUID} for
	 * generating HTML template
	 * 
	 * @param internalId
	 *            as {@link String}
	 * @return the corresponding {@link PublicReference}
	 * @throws EdalException
	 *             if unable get {@link PublicReference}
	 */
	PublicReference getPublicReferenceByInternalId(String internalId) throws EdalException;

	/**
	 * Accept an open ticket by the user who requested the data publication to
	 * get a final persistent ID
	 * 
	 * @param ticket
	 *            the ticket of the open request
	 * @param reviewerId
	 *            the id of the reviewer
	 * @throws EdalApprovalException
	 *             if unable to accept the request
	 */
	void acceptTicketByUser(String ticket, int reviewerId) throws EdalApprovalException;

	/**
	 * Reject an open ticket by the user who requested to discard the process
	 * and not getting a persistent ID
	 * 
	 * @param ticket
	 *            the ticket of the open request
	 * @param reviewerId
	 *            the id of the reviewer
	 * @throws EdalApprovalException
	 *             if unable to accept the request
	 */
	void rejectTicketByUser(String ticket, int reviewerId) throws EdalApprovalException;

}