/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.CheckReviewStatusJob;

/**
 * Thread to clean broken {@link PrimaryDataEntity} which where created during
 * upload process.
 * 
 * @author arendd
 */
public class CleanBrokenEntitiesThread extends EdalThread {

	/**
	 * Interval to re-run the {@link CleanBrokenEntitiesJob} in seconds
	 */
	private static final int INTERVAL_TO_REPEAT_JOB = 24;

	/**
	 * Quartz {@link Scheduler} to run the {@link CleanBrokenEntitiesJob}.
	 */
	private static Scheduler scheduler;
	private final PrimaryDataEntity root;

	/**
	 * Default constructor.
	 * 
	 * @param root the directory to start clean process
	 */
	public CleanBrokenEntitiesThread(PrimaryDataEntity root) {
		super();
		this.root = root;
	}

	/**
	 * Finish the {@link CleanBrokenEntitiesThread} an shutdown the
	 * {@link Scheduler}.
	 */
	public void done() {
		try {
			this.getScheduler().shutdown(true);
		} catch (SchedulerException e) {
			DataManager.getImplProv().getLogger().info("unable to stop CheckReviewScheduler", e);
		}
	}

	/**
	 * Getter for the {@link Scheduler}
	 * 
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Start the {@link CleanBrokenEntitiesThread} by starting the {@link Scheduler}
	 * and run the {@link CheckReviewStatusJob}.
	 */
	public void run() {

		DataManager.getImplProv().getLogger().info("Starting CleanBrokenEntitiesThread");

		Scheduler defaultQuartzScheduler = SchedulerRepository.getInstance().lookup("DefaultQuartzScheduler");

		if (defaultQuartzScheduler == null) {

			try {
				Scheduler std = new StdSchedulerFactory().getScheduler();
				std.getContext().put("root", this.root);
				CleanBrokenEntitiesThread.setScheduler(std);
			} catch (SchedulerException e) {
				DataManager.getImplProv().getLogger().warn("unable to create new CleanBrokenEntitiesThread", e);
			}
		}

		else {

			try {
				defaultQuartzScheduler.getContext().put("root", this.root);
			} catch (SchedulerException e) {
				DataManager.getImplProv().getLogger().warn("unable to create new CleanBrokenEntitiesThread", e);
			}
			CleanBrokenEntitiesThread.setScheduler(defaultQuartzScheduler);
		}
		try {
			this.getScheduler().start();

			JobDetail job = JobBuilder.newJob(CleanBrokenEntitiesJob.class).build();

			Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(INTERVAL_TO_REPEAT_JOB).repeatForever())
					.build();

			this.getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			DataManager.getImplProv().getLogger().warn("unable to start CleanBrokenEntitiesThread", e);

		}

	}

	/**
	 * Setter for the {@link Scheduler}
	 * 
	 * @param scheduler the scheduler to set
	 */
	private static void setScheduler(Scheduler scheduler) {
		CleanBrokenEntitiesThread.scheduler = scheduler;
	}
}