/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
			app = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
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