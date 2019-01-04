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

/**
 * Class to define the {@link Job} to rebuild the Lucene index after running a
 * clean up.
 * 
 * @author arendd
 */
@DisallowConcurrentExecution
public class RebuildIndexJob implements Job {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Call the {@link ServiceProvider} of the current
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider}
	 * and rebuild the index
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			ServiceProvider s = ((FileSystemImplementationProvider) DataManager.getImplProv()).getServiceProvider()
					.newInstance();

			if (s.isCleaned()) {

				((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexThread().resetIndexThread();

				s.setCleaned(false);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}