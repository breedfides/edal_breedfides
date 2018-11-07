/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.util.Calendar;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;

public class ListThread extends EdalThread {

	private static final String STRING_PARENT_DIRECTORY = "parentDirectory";

	private AsynchronList<PrimaryDataEntity> asynList = null;
	private PrimaryDataDirectory parentDirectory = null;
	private Calendar currentVersionDate = null;
	private Calendar nextVersionDate = null;

	public ListThread(PrimaryDataDirectory parentDirectory, Calendar currentVersionDate, Calendar nextVersionDate) {
		super();
		this.asynList = new AsynchronList<PrimaryDataEntity>();
		this.parentDirectory = parentDirectory;
		this.currentVersionDate = currentVersionDate;
		this.nextVersionDate = nextVersionDate;

	}

	public AsynchronList<PrimaryDataEntity> getAsynchronList() {
		return this.asynList;
	}

	@Override
	public void run() {

		// System.out.println("START LIST THREAD");
		// Long start = System.currentTimeMillis();

		try {
			Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session.setDefaultReadOnly(true);

			int firstPage = 0;
			int pagesSteps = 1000;

			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder.createQuery(PrimaryDataDirectoryImplementation.class);
			Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria.from(PrimaryDataDirectoryImplementation.class);
			directoryCriteria.where(builder.and(
					builder.equal(directoryRoot.type(),PrimaryDataDirectoryImplementation.class)),
					builder.equal(directoryRoot.get(STRING_PARENT_DIRECTORY), this.parentDirectory));
			
			Query<PrimaryDataDirectoryImplementation> directoryQuery = session.createQuery(directoryCriteria).setFirstResult(firstPage).setMaxResults(pagesSteps);

			List<PrimaryDataDirectoryImplementation> directories = null;

			while ((directories = directoryQuery.list()).size() > 0) {

				session.clear();
				System.gc();

				// DataManager.getImplProv().getLogger().debug("Run loop for listing directories
				// : "
				// + directoryQuery.list().size());

				for (PrimaryDataDirectoryImplementation directory : directories) {

					if (nextVersionDate == null) {
						try {
							directory.switchCurrentVersion(
									directory.getVersionByRevisionNumber(directory.getVersions().size() - 1));

							asynList.add(directory);
							asynList.notifyNewDataAvailable();

						} catch (PrimaryDataEntityVersionException e) {
							throw new PrimaryDataDirectoryException(
									"Can not switch version of '" + this + "' :" + e.getMessage());
						}
					} else {

						for (PrimaryDataEntityVersion version : directory.getVersions()) {

							if (version.getRevisionDate().before(nextVersionDate)
									&& version.getRevisionDate().after(currentVersionDate)) {
								try {
									directory.switchCurrentVersion(version);

									asynList.add(directory);
									asynList.notifyNewDataAvailable();
									break;

								} catch (PrimaryDataEntityVersionException e) {
									throw new PrimaryDataDirectoryException(
											"Can not switch version of '" + this + "' :" + e.getMessage());
								}
							}
						}
					}

					// session.evict(directory);

				}
				if (asynList.isStopped()) {
					DataManager.getImplProv().getLogger().warn("Stopped ListThread !");
					break;

				}
				directoryQuery.setFirstResult(firstPage += pagesSteps);
			}

			firstPage = 0;

			CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder.createQuery(PrimaryDataFileImplementation.class);
			Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);
			fileCriteria.where(builder.and(
					builder.equal(fileRoot.type(),PrimaryDataFileImplementation.class)),
					builder.equal(fileRoot.get(STRING_PARENT_DIRECTORY), this.parentDirectory));
			
			Query<PrimaryDataFileImplementation> fileQuery = session.createQuery(fileCriteria).setFirstResult(firstPage).setMaxResults(pagesSteps);
						
			List<PrimaryDataFileImplementation> files = null;

			// Long startfiles = System.currentTimeMillis();
			// Long endfiles = null;

			while ((files = fileQuery.list()).size() > 0) {

				session.clear();
				System.gc();
				// DataManager.getImplProv().getLogger().debug("Run loop for listing files : "
				// + fileQuery.list().size());

				for (PrimaryDataFileImplementation file : files) {

					if (nextVersionDate == null) {

						try {
							file.switchCurrentVersion(file.getVersionByRevisionNumber(file.getVersions().size() - 1));

							asynList.add(file);
							asynList.notifyNewDataAvailable();

						} catch (PrimaryDataEntityVersionException e) {
							throw new PrimaryDataDirectoryException(
									"Can not switch version of '" + this + "' :" + e.getMessage());
						}
					} else {

						for (PrimaryDataEntityVersion version : file.getVersions()) {

							if (version.getRevisionDate().before(nextVersionDate)
									&& version.getRevisionDate().after(currentVersionDate)) {
								try {
									file.switchCurrentVersion(version);

									asynList.add(file);
									asynList.notifyNewDataAvailable();
									break;

								} catch (PrimaryDataEntityVersionException e) {
									throw new PrimaryDataDirectoryException(
											"Can not switch version of '" + this + "' :" + e.getMessage());
								}
							}
						}
					}
					// session.evict(file);
				}

				if (asynList.isStopped()) {
					DataManager.getImplProv().getLogger().warn("Stopped ListThread !");
					break;
				}

				fileQuery.setFirstResult(firstPage += pagesSteps);

				// endfiles = System.currentTimeMillis();
				// System.out.println("FINISHED FILE_LIST " + Thread.currentThread().getId() + "
				// in : " + (endfiles - startfiles) + " msec");
				// startfiles = System.currentTimeMillis();

			}

			/** sort list before return */
			/** java.util.Collections.sort(entities); */

			session.clear();
			session.close();
			System.gc();

			asynList.notifyNoMoreNewData();

			// Long end = System.currentTimeMillis();
			// System.out.println("FINISHED LIST_THREAD in : " + (end - start) / 1000 +
			// "sec");

		} catch (Exception e) {
			DataManager.getImplProv().getLogger().error(
					"Can not list all PrimaryDataEntities of '" + parentDirectory.getName() + "' :" + e.getMessage());
		}
	}
}
