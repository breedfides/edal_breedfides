/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.review;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewResult;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

/**
 * @author lange
 * 
 */
public class TestReviewProcess extends EdalDefaultTestCase {

    /**
     * Test method for
     * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess#review(java.util.List)}
     * .
     * 
     * @return
     * 
     * @throws EdalException
     * @throws EdalAuthenticateException
     * @throws PrimaryDataDirectoryException
     * @throws EdalConfigurationException
     */
    @Test
    public void testReview() throws EdalException,
	    PrimaryDataDirectoryException, EdalAuthenticateException,
	    EdalConfigurationException {

	DataManager.getRootDirectory(EdalHelpers
		.getFileSystemImplementationProvider(true, this.configuration),
		EdalHelpers.authenticateWinOrUnixOrMacUser());

	ReviewResult res = ReviewProcess.review(new LinkedList<ReviewStatus>());
	res = ReviewProcess.review(res.getReviewerStatusList());
	System.out.println(res);

	final ReviewStatus scientificReviewStatus = new ReviewStatus();
	scientificReviewStatus.setEmailAddress(DataManager.getConfiguration()
		.getReviewerScientific());
	scientificReviewStatus.setStatusType(ReviewStatusType.ACCEPTED);
	scientificReviewStatus.setRequestedDate(Calendar.getInstance());
	scientificReviewStatus.setReviewerType(ReviewerType.SCIENTIFIC);

	// create the substitute reviewer
	final ReviewStatus substituteReviewStatus = new ReviewStatus();
	substituteReviewStatus.setEmailAddress(DataManager.getConfiguration()
		.getReviewerSubstitute());
	substituteReviewStatus.setStatusType(ReviewStatusType.UNDECIDED);
	substituteReviewStatus.setRequestedDate(Calendar.getInstance());
	substituteReviewStatus.setReviewerType(ReviewerType.SUBSTITUTE);

	// create the managing reviewer
	final ReviewStatus managingReviewStatus = new ReviewStatus();
	managingReviewStatus.setEmailAddress(DataManager.getConfiguration()
		.getReviewerManaging());
	managingReviewStatus.setStatusType(ReviewStatusType.ACCEPTED);
	managingReviewStatus.setRequestedDate(Calendar.getInstance());
	managingReviewStatus.setReviewerType(ReviewerType.MANAGING);

	res = ReviewProcess.review(Arrays.asList(scientificReviewStatus,
		substituteReviewStatus, managingReviewStatus));

	// System.out.println(res);
//	DataManager.shutdown();
	// TODO test with truth tables
	Assert.assertEquals(res.getReviewResult(), ReviewStatusType.ACCEPTED);
    }

}
