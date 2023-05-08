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
package de.ipk_gatersleben.bit.bi.edal.breedfides.rest;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;


/** Important function to get use e!DAL without getting AccessControlException due to JAAS/AspectJ
 * 
 * @author arendd
 *
 */
public class EdalFunctions {

	public static PrimaryDataDirectory getRootDirectory() {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
				.createQuery(PrimaryDataDirectoryImplementation.class);

		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
				.from(PrimaryDataDirectoryImplementation.class);

		directoryCriteria
				.where(builder.isNull(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY)));

		final PrimaryDataDirectoryImplementation primaryDataDirectory = session.createQuery(directoryCriteria)
				.uniqueResult();

		session.close();

		return primaryDataDirectory;

	}

	public static List<PrimaryDataDirectory> getUserDirectories(PrimaryDataDirectory rootDirectory) {

		try {
			List<PrimaryDataEntity> fullList = rootDirectory.listPrimaryDataEntities();
			List<PrimaryDataDirectory> directoryOnlyList = new ArrayList<>();

			for (PrimaryDataEntity primaryDataEntity : fullList) {
				if (primaryDataEntity.isDirectory()) {
					directoryOnlyList.add((PrimaryDataDirectory) primaryDataEntity);
				}
			}
			return directoryOnlyList;
		} catch (PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static PrimaryDataDirectory getUserDirectory(PrimaryDataDirectory rootDirectory) {
		return null;
	}

	public static PrimaryDataDirectory getParentDirectory(PrimaryDataEntity entity) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
				.createQuery(PrimaryDataDirectoryImplementation.class);

		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
				.from(PrimaryDataDirectoryImplementation.class);

		directoryCriteria
				.where(builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID), entity.getID()))
				.select(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY));

		PrimaryDataDirectoryImplementation primaryDataDirectory = session.createQuery(directoryCriteria).uniqueResult();

		session.close();

		return primaryDataDirectory;

	}
}
