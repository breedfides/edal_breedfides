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

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;

@DisallowConcurrentExecution
public class CalculateDirectorySizeJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		ServiceProvider service = null;

		try {
			service = DataManager.getImplProv().getServiceProvider().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			DataManager.getImplProv().getLogger().error(e);
			throw new JobExecutionException("Unable to load ApprovalServiceProvider: " + e.getMessage(), e.getCause());
		}
		
		service.calculateDirectorySizes();

	}
}