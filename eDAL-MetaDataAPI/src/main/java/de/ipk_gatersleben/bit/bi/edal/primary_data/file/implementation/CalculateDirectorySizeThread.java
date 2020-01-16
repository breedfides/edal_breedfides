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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;

/**
 * Thread to calculate the size of directories within a {@link PublicReference}
 * 
 * @author arendd
 */
public class CalculateDirectorySizeThread extends EdalThread {

	/**
	 * Interval to re-run the {@link CalculateDirectorySizeThread} in hours
	 */
	private static final int INTERVAL_TO_REPEAT_JOB = 24;

	private static Scheduler scheduler;

	public static Map<String, Long> directorySizes = null;
	public static Map<String, String> directoryFiles = null;
	public static long totalVolumeDataStock = 0;
	public static Map<String, String> referenceContent = null;

	public CalculateDirectorySizeThread() {
		super();
	}

	/**
	 * Finish the {@link CalculateDirectorySizeThread} an shutdown the
	 * {@link Scheduler}.
	 */
	public void done() {
		try {
			if (this.getScheduler() != null) {
				this.getScheduler().shutdown(true);
			}
		} catch (SchedulerException e) {
			DataManager.getImplProv().getLogger().info("unable to stop CalculateDirectorySizeThreadScheduler", e);
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
	 * Start the {@link CalculateDirectorySizeThread} by starting the
	 * {@link Scheduler} and run the {@link CalculateDirectorySizeJob}.
	 */
	@SuppressWarnings("unchecked")
	public void run() {

		Path path = ServiceProviderImplementation.PATH_FOR_DIRECTORY_SIZE_MAP;

		if (Files.exists(path)) {
			directorySizes = new HashMap<String, Long>();

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
				Object readMap = ois.readObject();
				if (readMap != null && readMap instanceof HashMap) {
					directorySizes.putAll((Map<String, Long>) readMap);
				}
				ois.close();
			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
			}

		} else {
			directorySizes = new HashMap<String, Long>();
		}

		path = ServiceProviderImplementation.PATH_FOR_DIRECTORY_FILE_MAP;

		if (Files.exists(path)) {
			directoryFiles = new HashMap<String, String>();

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
				Object readMap = ois.readObject();
				if (readMap != null && readMap instanceof HashMap) {
					directoryFiles.putAll((Map<String, String>) readMap);
				}
				ois.close();
			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
			}

		} else {
			directoryFiles = new HashMap<String, String>();
		}

		path = ServiceProviderImplementation.PATH_FOR_REFERENCE_CONTENT;

		if (Files.exists(path)) {
			referenceContent = new HashMap<String, String>();

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
				Object readMap = ois.readObject();
				if (readMap != null && readMap instanceof HashMap) {
					referenceContent.putAll((Map<String, String>) readMap);
				}
				ois.close();
			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
			}

		} else {
			referenceContent = new HashMap<String, String>();
		}

		path = ServiceProviderImplementation.PATH_FOR_TOTAL_FILE_NUMBER;

		if (Files.exists(path)) {
			ServiceProviderImplementation.totalNumberOfFiles = Long.valueOf(0);

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
				Object readLong = ois.readObject();
				if (readLong != null && readLong instanceof Long) {
					ServiceProviderImplementation.totalNumberOfFiles = (Long) readLong;
				}
				ois.close();
			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
			}

		} else {
			ServiceProviderImplementation.totalNumberOfFiles = Long.valueOf(0);
		}

		path = ServiceProviderImplementation.PATH_FOR_TOTAL_VOLUME;

		if (Files.exists(path)) {
			CalculateDirectorySizeThread.totalVolumeDataStock = Long.valueOf(0);

			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
				Object readLong = ois.readObject();
				if (readLong != null && readLong instanceof Long) {
					CalculateDirectorySizeThread.totalVolumeDataStock = (Long) readLong;
				}
				ois.close();
			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
			}

		} else {
			CalculateDirectorySizeThread.totalVolumeDataStock = Long.valueOf(0);
		}

		DataManager.getImplProv().getLogger().info("Starting CalculateDirectorySizeThread");

		Scheduler defaultQuartzScheduler = SchedulerRepository.getInstance().lookup("DefaultQuartzScheduler");

		if (defaultQuartzScheduler == null) {

			try {
				CalculateDirectorySizeThread.setScheduler(new StdSchedulerFactory().getScheduler());
			} catch (SchedulerException e) {
				DataManager.getImplProv().getLogger().warn("unable to create new CalculateDirectorySizeThreadScheduler",
						e);
			}
		}

		else {
			CalculateDirectorySizeThread.setScheduler(defaultQuartzScheduler);
		}
		try {
			this.getScheduler().start();

			JobDetail job = JobBuilder.newJob(CalculateDirectorySizeJob.class).build();

			Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
					SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(INTERVAL_TO_REPEAT_JOB).repeatForever())
					.build();

			this.getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			DataManager.getImplProv().getLogger().warn("unable to start CalculateDirectorySizeThreadScheduler", e);

		}
	}

	/**
	 * Setter for the {@link Scheduler}
	 * 
	 * @param scheduler the scheduler to set
	 */
	private static void setScheduler(Scheduler scheduler) {
		CalculateDirectorySizeThread.scheduler = scheduler;
	}

}
