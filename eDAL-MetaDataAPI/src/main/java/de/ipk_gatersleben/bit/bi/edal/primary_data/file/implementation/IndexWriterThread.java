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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.hibernate.search.mapper.orm.work.SearchWorkspace;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;

abstract class IndexWriterThread extends EdalThread {
	protected static final int SLEEP_RUNTIME_FACTOR = 2;
	protected static final long MIN_THREAD_SLEEP_MILLISECONDS = 500;
	protected static final long MAX_THREAD_SLEEP_MILLISECONDS = 2000;

	protected SessionFactory sessionFactory;
	
	protected Logger indexLogger = null;

	protected int lastIndexedID = 0;
	protected Path indexDirectory;
	protected Logger indexWriterThreadLogger = null;
	protected Logger implementationProviderLogger = null;
	SearchIndexingPlan indexingPlan = null;
	SearchWorkspace workspace = null;
	protected boolean requestForReset = false;

	/** create Lock with fairness parameter true */
	protected final ReentrantLock lock = new ReentrantLock(true);

	/** create {@link CountDownLatch} to wait for finishing index */
	protected CountDownLatch latch = new CountDownLatch(1);
	
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

		final Session session = this.sessionFactory.openSession();
		try {			
			File folder = new File(this.indexDirectory.toString());
			File[] listOfFiles = folder.listFiles();
			Directory directory = null;
			IndexReader reader = null;
			int numberDocs = 0;
			for (File file : listOfFiles) {
				  if (file.isDirectory()) {
						try {					
							directory = FSDirectory.open(Paths.get(this.indexDirectory.toString(),file.getName()));
							reader = DirectoryReader.open( directory );
							numberDocs += reader.numDocs();
						} catch (IOException e) {
							e.printStackTrace();
						}
				  }
			}
			this.implementationProviderLogger.info("Number of docs after index rebuild: " + numberDocs);

			

		} finally {
			//readerProvider.close(reader);
			session.close();
		}
	}
	
	protected abstract void executeIndexing();
	
	protected abstract void indexRestObjects();
	
	protected abstract void resetIndexThread();

	
	/** {@inheritDoc} */
	@Override
	public void run() {

		while (!this.sessionFactory.isClosed()) {
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

	}
	
	/**
	 * @return the lock
	 */
	private ReentrantLock getLock() {
		return lock;
	}

	/**
	 * wait until the indexing method is finished
	 */
	void waitForFinish() {

		final long time = System.currentTimeMillis();
		this.indexWriterThreadLogger.debug("Wait for finish current indexing...");
		this.lock.lock();
		this.indexWriterThreadLogger.debug("Got lock for last indexing...");
		this.indexWriterThreadLogger.info("FINALZE indexing...");
		this.executeIndexing();

		/** close SessionFactory so no indexing again */
		/** executeIndexing() runs only with open SessionFactory */

		this.sessionFactory.close();
		this.lock.unlock();
		this.indexWriterThreadLogger
				.debug("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexLogger.info("Index is finished after waiting : " + (System.currentTimeMillis() - time + " ms"));
		this.indexWriterThreadLogger.debug("unlock Lock");
	}

	//final IndexAccessor readerProvider = searchFactory

	//final IndexReader reader = readerProvider.open(MyUntypedData.class);

}
