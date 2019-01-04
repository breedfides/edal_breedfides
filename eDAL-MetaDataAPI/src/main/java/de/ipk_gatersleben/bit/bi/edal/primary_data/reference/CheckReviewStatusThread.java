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

/**
 * Thread to check the review status of all existing
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}s.
 * Using the Quartz framework to start time controlled job to check all
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}s in
 * a defined interval.
 * 
 * @author arendd
 */
public class CheckReviewStatusThread extends Thread {

	/**
	 * Interval to rerun the {@link CheckReviewStatusJob} in seconds
	 */
	private static final int INTERVAL_TO_REPEAT_JOB = 10;

	/**
	 * Quartz {@link Scheduler} to run the {@link CheckReviewStatusJob}.
	 */
	private static Scheduler scheduler;

	/**
	 * Default constructor.
	 */
	public CheckReviewStatusThread() {

	}

	/**
	 * Finish the {@link CheckReviewStatusThread} an shutdown the {@link Scheduler}.
	 */
	public void done() {
		try {
			if (this.getScheduler() != null) {
				this.getScheduler().shutdown(true);
			}
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
	 * Start the {@link CheckReviewStatusThread} by starting the {@link Scheduler}
	 * and run the {@link CheckReviewStatusJob}.
	 */
	public void run() {

		DataManager.getImplProv().getLogger().info("Starting CheckReviewStatusThread");

		Scheduler defaultQuartzScheduler = SchedulerRepository.getInstance().lookup("DefaultQuartzScheduler");

		if (defaultQuartzScheduler == null) {

			try {
				CheckReviewStatusThread.setScheduler(new StdSchedulerFactory().getScheduler());
			} catch (SchedulerException e) {
				DataManager.getImplProv().getLogger().warn("unable to create new CheckReviewScheduler", e);
			}
		}

		else {
			CheckReviewStatusThread.setScheduler(defaultQuartzScheduler);
		}
		try {
			this.getScheduler().start();

			JobDetail job = JobBuilder.newJob(CheckReviewStatusJob.class).build();

			Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInSeconds(INTERVAL_TO_REPEAT_JOB).repeatForever()).build();

			this.getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			DataManager.getImplProv().getLogger().warn("unable to start CheckReviewScheduler", e);

		}
	}

	/**
	 * Setter for the {@link Scheduler}
	 * 
	 * @param scheduler
	 *            the scheduler to set
	 */
	private static void setScheduler(Scheduler scheduler) {
		CheckReviewStatusThread.scheduler = scheduler;
	}
}