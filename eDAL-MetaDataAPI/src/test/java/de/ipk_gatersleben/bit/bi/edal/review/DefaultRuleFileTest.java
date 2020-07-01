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
package de.ipk_gatersleben.bit.bi.edal.review;

import java.util.Arrays;
import java.util.Calendar;

import javax.mail.internet.InternetAddress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalApprovalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewResult;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * Test the {@link ReviewProcess#review(List, Workgroup)} function with
 * {@link Workgroup} == <code>null</code>.
 * 
 * @author Denny Hecht
 */
public class DefaultRuleFileTest extends EdalDefaultTestCase {

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#ACCEPTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccepted() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus1.setRequestedDate(Calendar.getInstance());
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus2.setRequestedDate(Calendar.getInstance());
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus3.setRequestedDate(Calendar.getInstance());
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.ACCEPTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#ACCEPTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAcceptedScientificWithTimeout() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -(ReviewProcess.DEFAULT_TIMEOUT + 2));

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus1.setRequestedDate(c);
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus2.setRequestedDate(c);
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(c);
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.ACCEPTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#ACCEPTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAcceptedSubstituteWithTimeout() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		// six days before today
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -(ReviewProcess.DEFAULT_TIMEOUT + 2));

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus1.setRequestedDate(c);
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus2.setRequestedDate(c);
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(c);
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.ACCEPTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#REJECTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllUndecidedWithTimeout() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		// six days before today
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -(ReviewProcess.DEFAULT_TIMEOUT + 2));

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus1.setRequestedDate(c);
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus2.setRequestedDate(c);
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(c);
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.REJECTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#REJECTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRejected() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus1.setRequestedDate(Calendar.getInstance());
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus2.setRequestedDate(Calendar.getInstance());
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus3.setRequestedDate(Calendar.getInstance());
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.REJECTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#REJECTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRejectedScientificWithTimeout() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		// six days before today
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -(ReviewProcess.DEFAULT_TIMEOUT + 1));

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus1.setRequestedDate(c);
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus2.setRequestedDate(c);
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(c);
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.REJECTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#REJECTED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRejectedSubstituteWithTimeout() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		// six days before today
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -(ReviewProcess.DEFAULT_TIMEOUT + 2));

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus1.setRequestedDate(c);
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus2.setRequestedDate(c);
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(c);
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.REJECTED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}

	/**
	 * Test if {@link ReviewProcess#review(List, Workgroup)} return the
	 * {@link ReviewStatusType#UNDECIDED}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUndecided() throws Exception {

		DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

		final ReviewStatus reviewStatus1 = new ReviewStatus();
		reviewStatus1.setEmailAddress(new InternetAddress("user1@nodomain.de"));
		reviewStatus1.setStatusType(ReviewStatusType.ACCEPTED);
		reviewStatus1.setRequestedDate(Calendar.getInstance());
		reviewStatus1.setReviewerType(ReviewerType.SCIENTIFIC);

		final ReviewStatus reviewStatus2 = new ReviewStatus();
		reviewStatus2.setEmailAddress(new InternetAddress("user2@nodomain.de"));
		reviewStatus2.setStatusType(ReviewStatusType.REJECTED);
		reviewStatus2.setRequestedDate(Calendar.getInstance());
		reviewStatus2.setReviewerType(ReviewerType.SUBSTITUTE);

		final ReviewStatus reviewStatus3 = new ReviewStatus();
		reviewStatus3.setEmailAddress(new InternetAddress("user3@nodomain.de"));
		reviewStatus3.setStatusType(ReviewStatusType.UNDECIDED);
		reviewStatus3.setRequestedDate(Calendar.getInstance());
		reviewStatus3.setReviewerType(ReviewerType.MANAGING);

		try {
			final ReviewResult result = ReviewProcess.review(Arrays.asList(reviewStatus1, reviewStatus2, reviewStatus3));

			Assertions.assertEquals(ReviewStatusType.UNDECIDED, result.getReviewResult());
		} catch (final EdalApprovalException e) {
			Assertions.fail(e.getMessage());
		}
	}
}
