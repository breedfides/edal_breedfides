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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;

/**
 * Class to define the {@link Job} to remove all broken
 * {@link PrimaryDataEntity} which where created during a failed upload.
 * 
 * @author arendd
 */
@DisallowConcurrentExecution
public class CleanBrokenEntitiesJob implements Job {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Call the {@link ServiceProvider} of the current
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider}
	 * and remove all broken uploads.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		SchedulerContext schedulerContext=null;
		try {
			schedulerContext = context.getScheduler().getContext();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}

		PrimaryDataDirectory root = (PrimaryDataDirectory) schedulerContext.get("root");

		ServiceProvider service = null;

		try {
			service = DataManager.getImplProv().getServiceProvider().getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			DataManager.getImplProv().getLogger().error(e);
			throw new JobExecutionException("Unable to load ApprovalServiceProvider: " + e.getMessage(), e.getCause());
		}

		service.cleanUpForBrokenEntities(root);
	}
}