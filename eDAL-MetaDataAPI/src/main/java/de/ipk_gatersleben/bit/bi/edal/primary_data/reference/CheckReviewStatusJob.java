/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;

/**
 * Class to define the {@link Job} to check the review status of all existing
 * requests for a {@link PublicReference}.
 * 
 * @author arendd
 */
@DisallowConcurrentExecution
public class CheckReviewStatusJob implements Job {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Call the {@link ApprovalServiceProvider} of the current
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider}
	 * and load all open requests for a {@link PublicReference} and check the
	 * current {@link ReviewStatus}.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		ApprovalServiceProvider app = null;

		try {
			app = DataManager.getImplProv().getApprovalServiceProvider().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			DataManager.getImplProv().getLogger().error(e);
			throw new JobExecutionException("Unable to load ApprovalServiceProvider: " + e.getMessage(), e.getCause());
		}
		Map<PublicReference, List<ReviewStatus>> map = app.getAllOpenReviews();

		if (map.isEmpty()) {
			DataManager.getImplProv().getLogger().debug("Running CheckReviewStatusJob : found no open requests !");
		} else {
			DataManager.getImplProv().getLogger().debug("Running CheckReviewStatusJob : found " + map.size() + " open requests !");
		}
		try {
			app.checkOpenReviews(map);
		} catch (EdalApprovalException e) {
			DataManager.getImplProv().getLogger().error(e);
			throw new JobExecutionException("unable to check open review processes: " + e.getMessage(), e.getCause());
		}
	}
}