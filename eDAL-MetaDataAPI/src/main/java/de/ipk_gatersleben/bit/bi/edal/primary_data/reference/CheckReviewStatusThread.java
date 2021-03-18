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