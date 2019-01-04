/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class to create a {@link ThreadPoolExecutor} that will be automatically
 * closed, when calling {@link DataManager#shutdown()}
 * 
 * @author arendd
 */
public class EdalThreadPoolExcecutor extends ThreadPoolExecutor {

	/**
	 * Constructor uses the super constructor and start a MonitorThread.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 *            @param name of the threadpool
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} is null
	 */
	public EdalThreadPoolExcecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String name) {

		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
		this.setThreadFactory(new EdalThreadFactory(name));
		
		new MonitorThread(this).start();
	}

	/**
	 * Internal monitoring {@link Thread} to check if the
	 * {@link DataManager#shutdown()} method was called.
	 * 
	 * @author arendd
	 */
	class MonitorThread extends EdalThread {

		private ThreadPoolExecutor executor;

		public MonitorThread(ThreadPoolExecutor executor) {
			super();
			this.executor = executor;
		}

		@Override
		public void run() {
			DataManager.waitForShutDown();
			executor.shutdown();
		}
	}

}
