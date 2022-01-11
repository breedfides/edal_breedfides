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

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalApprovalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;

/**
 * Class to handle interaction between eDAL and the ReviewProcess.
 * 
 * @author arendd
 * 
 */
public class ReviewProcess {

	/**
	 * the default timeout for the review process half years in days
	 */
	public static final int DEFAULT_TIMEOUT = 182;

	protected static ReviewStatusEvaluation evaluationForGroups;
	protected static ReviewStatusEvaluation evaluationForDataManager;

	static {
		try {
			ReviewProcess.evaluationForGroups = ReviewStatusEvaluation.createReviewStatusEvaluationForGroups();
			ReviewProcess.evaluationForDataManager = ReviewStatusEvaluation
					.createReviewStatusEvaluationForDatamanager();
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a new {@link ReviewResult}
	 * 
	 * @return with default initialized {@link ReviewResult}
	 * @throws EdalApprovalException
	 *             if creation failed
	 */
	protected static List<ReviewStatus> createNewReviewResult() throws EdalApprovalException {
		try {
			// create the scientific reviewer
			final ReviewStatus scientificReviewStatus = new ReviewStatus();
			scientificReviewStatus.setEmailAddress(DataManager.getConfiguration().getReviewerScientific());
			scientificReviewStatus.setStatusType(ReviewStatusType.UNDECIDED);
			scientificReviewStatus.setRequestedDate(Calendar.getInstance());
			scientificReviewStatus.setReviewerType(ReviewerType.SCIENTIFIC);

			// create the substitute reviewer
			final ReviewStatus substituteReviewStatus = new ReviewStatus();
			substituteReviewStatus.setEmailAddress(DataManager.getConfiguration().getReviewerSubstitute());
			substituteReviewStatus.setStatusType(ReviewStatusType.UNDECIDED);
			substituteReviewStatus.setRequestedDate(Calendar.getInstance());
			substituteReviewStatus.setReviewerType(ReviewerType.SUBSTITUTE);

			// create the managing reviewer
			final ReviewStatus managingReviewStatus = new ReviewStatus();
			managingReviewStatus.setEmailAddress(DataManager.getConfiguration().getReviewerManaging());
			managingReviewStatus.setStatusType(ReviewStatusType.UNDECIDED);
			managingReviewStatus.setRequestedDate(Calendar.getInstance());
			managingReviewStatus.setReviewerType(ReviewerType.MANAGING);

			// list with all reviewers there get an email notification
			return Arrays.asList(scientificReviewStatus, substituteReviewStatus, managingReviewStatus);

		} catch (final EdalConfigurationException e) {
			throw new EdalApprovalException(e);
		}
	}

	/**
	 * Checks if the timeout of the review process is exceeded with a specific
	 * requested date.
	 * 
	 * @param requestedDate
	 *            the date when the Reviewer was notified with an email
	 * @return <code>true</code> if the timeout is exceeded <code>false</code>
	 *         otherwise
	 */
	private static boolean isTimeoutExceeded(final Calendar requestedDate) {
		final Calendar now = Calendar.getInstance();
		final long difference = (now.getTimeInMillis() - requestedDate.getTimeInMillis()) / (1000 * 60 * 60 * 24);
		return difference > ReviewProcess.DEFAULT_TIMEOUT ? true : false;
	}

	/**
	 * Checks with specific rules the review status of a publication.
	 * 
	 * @param reviewStatusList
	 *            list with all reviewer they have responded
	 * @return {@link ReviewResult} as container with all review results
	 * @throws EdalApprovalException
	 *             if wrong email address
	 */
	public static ReviewResult review(List<ReviewStatus> reviewStatusList) throws EdalApprovalException {

		// get the 3 reviewStatus
		ReviewStatus scientificReviewStatus = null;
		ReviewStatus substituteReviewStatus = null;
		ReviewStatus managingReviewStatus = null;

		// Result of the review process
		final ReviewResult reviewResult = new ReviewResult();

		// initial step, no reviewers was notified
		if (reviewStatusList.size() == 0) {

			reviewStatusList = ReviewProcess.createNewReviewResult();
		}
		// check if the parameter size is correct
		if (reviewStatusList.size() != 3) {
			throw new EdalApprovalException("Unexpected reviewStatusList size. Expected was 3 but list size is: "
					+ reviewStatusList.size() + ".");
		}

		for (final ReviewStatus reviewStatus : reviewStatusList) {
			if (reviewStatus.getStatusType() == ReviewStatus.ReviewStatusType.UNDECIDED
					&& ReviewProcess.isTimeoutExceeded(reviewStatus.getRequestedDate())) {
				reviewStatus.setStatusType(ReviewStatus.ReviewStatusType.TIMEOUT);
			}
			switch (reviewStatus.getReviewerType()) {
			case SCIENTIFIC:
				scientificReviewStatus = reviewStatus;
				break;

			case SUBSTITUTE:
				substituteReviewStatus = reviewStatus;
				break;

			case MANAGING:
				managingReviewStatus = reviewStatus;
				break;
			}
		}

		reviewResult.setReviewResult(ReviewProcess.evaluationForDataManager
				.eval(new ImmutablePair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>(
						ReviewProcess.evaluationForGroups
								.eval(new ImmutablePair<ReviewStatus.ReviewStatusType, ReviewStatus.ReviewStatusType>(
										scientificReviewStatus.getStatusType(),
										substituteReviewStatus.getStatusType())),
						managingReviewStatus.getStatusType())));
		reviewResult.setReviewerStatusList(reviewStatusList);
		return reviewResult;

	}
}
