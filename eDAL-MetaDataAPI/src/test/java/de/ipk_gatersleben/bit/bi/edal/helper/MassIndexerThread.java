/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.helper;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;

public class MassIndexerThread extends Thread {

	private SessionFactory sessionFactory;

	private MassIndexer massIndexer;

	public MassIndexerThread(SessionFactory sessionFactory) {

		// setDaemon(true);

		setPriority(MAX_PRIORITY);

		this.setSessionFactory(sessionFactory);

	}

	@Override
	public void run() {
		while (true) {

			long indexingTime = 0;

			if (this.getSessionFactory().isClosed()) {
				break;
			}

			Session session = this.getSessionFactory().openSession();

			SearchSession fullTextSession = Search
					.session(session);

			//Transaction transaction = fullTextSession.beginTransaction();

			this.setMassIndexer(fullTextSession
					.massIndexer(MyUntypedData.class)
					.batchSizeToLoadObjects(30).cacheMode(CacheMode.NORMAL)
					.threadsToLoadObjects(4));

			try {
				System.out.println("Start INDEXING...");
				final long start = System.currentTimeMillis();

				this.getMassIndexer().startAndWait();

				final long end = System.currentTimeMillis();

				System.out.println("Zeit (INDEXING): " + (end - start)
						+ " millisec");
				indexingTime = end - start;

				//transaction.commit();
				session.close();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep((indexingTime * 5));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * @return the sessionFactory
	 */
	private SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	private void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return the massIndexer
	 */
	private MassIndexer getMassIndexer() {
		return massIndexer;
	}

	/**
	 * @param massIndexer
	 *            the massIndexer to set
	 */
	private void setMassIndexer(MassIndexer massIndexer) {
		this.massIndexer = massIndexer;
	}
}