/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.helper;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;

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

			FullTextSession fullTextSession = Search
					.getFullTextSession(session);

			Transaction transaction = fullTextSession.beginTransaction();

			this.setMassIndexer(fullTextSession
					.createIndexer(MyUntypedData.class)
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

				transaction.commit();
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