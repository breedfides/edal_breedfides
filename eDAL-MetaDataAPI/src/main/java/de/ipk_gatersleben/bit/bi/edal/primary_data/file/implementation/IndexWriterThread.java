/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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


import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.hibernate.search.mapper.orm.work.SearchWorkspace;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;

/**
 * @author ralfs
 *
 */
abstract class IndexWriterThread extends EdalThread {
	protected static final int SLEEP_RUNTIME_FACTOR = 2;
	protected static final long MIN_THREAD_SLEEP_MILLISECONDS = 500;
	protected static final long MAX_THREAD_SLEEP_MILLISECONDS = 2000;
	protected SessionFactory sessionFactory;
	
	protected Logger indexLogger = null;

	protected Path indexDirectory;
	protected Logger indexWriterThreadLogger = null;
	protected Logger implementationProviderLogger = null;
	SearchIndexingPlan indexingPlan = null;
	SearchWorkspace workspace = null;
	protected boolean requestForReset = false;
	private boolean finishIndexing = false;

	/** create Lock with fairness parameter true */
	protected final ReentrantLock lock = new ReentrantLock(true);

	/** create {@link CountDownLatch} to wait for finishing index */
	protected CountDownLatch latch = null;
	
	protected CountDownLatch countDownLatch = null;
	
	/**
	 * Constructor for IndexWriterThread
	 * 
	 * @param sessionFactory
	 *            the current {@link SessionFactory} object.
	 * @param indexDirectory
	 *            the mount path of the running instance
	 * @param implementationProviderLogger
	 *            the logger of the used {@link ImplementationProvider}
	 */
	protected IndexWriterThread(final SessionFactory sessionFactory, final Path indexDirectory,
			final Logger implementationProviderLogger) {
		super();
		this.indexLogger = LogManager.getLogger("index-thread");
		this.indexWriterThreadLogger = LogManager.getLogger("IndexWriterThread");
		this.implementationProviderLogger = implementationProviderLogger;

		this.indexDirectory = indexDirectory;

		this.sessionFactory = sessionFactory;
	}
	
	
	protected abstract void executeIndexing();
	
	protected abstract void resetIndexThread();

	
	/** {@inheritDoc} */
	@Override
	public void run() {
		while (!this.finishIndexing && !this.sessionFactory.isClosed()) {
			this.indexWriterThreadLogger.debug("Wait for Reseting the index structure: " + this.requestForReset);

			if (!this.requestForReset) {
				this.indexWriterThreadLogger.debug("try lock run method");
				this.getLock().lock();
				latch = new CountDownLatch(1);
				this.indexWriterThreadLogger.debug("locked run method");
				this.executeIndexing();
				this.indexWriterThreadLogger.debug("unlock run method");
				this.getLock().unlock();
				latch.countDown();
			}
		}		
		//Latch has to be countdown in Implementations (PublicVersionIndexWriterThread/NativeLuceneIndexWriterThread)
	}

	public boolean isFinishIndexing() {
		return finishIndexing;
	}

	public void setFinishIndexing(boolean finishIndexing) {
		this.finishIndexing = finishIndexing;
	}

	/**
	 * @return the lock
	 */
	private ReentrantLock getLock() {
		return lock;
	}

	/**
	 * wait until the indexing method is finished
	 * 
	 */
	public void waitForFinish() {
		final long time = System.currentTimeMillis();
		this.lock.lock();
		this.indexWriterThreadLogger.debug("Got lock for last indexing...");
		this.indexWriterThreadLogger.info("FINALZE indexing...");
		this.executeIndexing();
		this.setFinishIndexing(true);
		this.lock.unlock();
		this.indexWriterThreadLogger.debug("Indexing thread worked: "
				+ (System.currentTimeMillis() - time + " ms"));
		this.indexLogger.info("Indexing thread worked: "
				+ (System.currentTimeMillis() - time + " ms"));
	}

	public void setCountDownLatch(
			CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}


}

