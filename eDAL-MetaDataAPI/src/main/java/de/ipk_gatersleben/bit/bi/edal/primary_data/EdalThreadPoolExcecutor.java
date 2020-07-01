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
