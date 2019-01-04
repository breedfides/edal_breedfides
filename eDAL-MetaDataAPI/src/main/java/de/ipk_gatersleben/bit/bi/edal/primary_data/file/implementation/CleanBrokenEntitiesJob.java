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