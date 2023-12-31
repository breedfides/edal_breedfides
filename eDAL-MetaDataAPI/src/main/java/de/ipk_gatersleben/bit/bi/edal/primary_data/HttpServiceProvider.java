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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.util.UUID;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * Interface to provide function for the {@link EdalHttpHandler}
 * 
 * @author arendd
 *
 */
public interface HttpServiceProvider {

	/**
	 * Getter to load a {@link PrimaryDataEntity} object specified by
	 * {@link UUID} and number of the {@link PrimaryDataEntityVersion}.
	 * 
	 * @param uuid
	 *            the {@link UUID} of the {@link PrimaryDataEntity}.
	 * @param versionNumber
	 *            the version number of the {@link PrimaryDataEntityVersion}.
	 * @throws EdalException
	 *             if there is no {@link PrimaryDataEntity} with the specified
	 *             values.
	 * @return the specified {@link PrimaryDataEntity}
	 */
	PrimaryDataEntity getPrimaryDataEntityByID(String uuid, long versionNumber) throws EdalException;

	/**
	 * Get a {@link PrimaryDataEntity} for a given {@link PublicReference}
	 * 
	 * @param uuid
	 *            the id of the {@link PrimaryDataEntity}
	 * @param versionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @param persistentIdentifier
	 *            the type of the identifier
	 * @return the {@link PrimaryDataEntity}
	 * @throws EdalException
	 *             if unable to find a {@link PrimaryDataEntity}
	 */
	PrimaryDataEntity getPrimaryDataEntityForPersistentIdentifier(String uuid, long versionNumber,
			PersistentIdentifier persistentIdentifier) throws EdalException;

	/**
	 * Getter for a {@link PrimaryDataEntity} for a reviewer to create a
	 * temporary landing page.
	 * 
	 * @param uuid
	 *            the id of the {@link PrimaryDataEntity}
	 * @param versionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @param internalId
	 *            the internal ID of the corresponding {@link PublicReference}
	 * @param reviewerCode
	 *            the id to identify a reviewer
	 * @return the searched {@link PrimaryDataEntity}
	 * @throws EdalException
	 *             if unable to load the {@link PrimaryDataEntity}
	 */
	PrimaryDataEntity getPrimaryDataEntityForReviewer(String uuid, long versionNumber, String internalId,
			int reviewerCode) throws EdalException;

	/**
	 * Getter for the parent {@link PrimaryDataEntity} with a
	 * {@link PersistentIdentifier} of a given {@link PrimaryDataEntity}
	 * 
	 * @param entity
	 *            to search for the parent recursive
	 * @param versionNumber
	 *            number of the {@link PrimaryDataEntityVersion}
	 * @param persistentIdentifier
	 *            the ID type
	 * @return the {@link PrimaryDataEntity} with the
	 *         {@link PersistentIdentifier}
	 * @throws EdalException
	 *             if no {@link PersistentIdentifier} set
	 */
	PrimaryDataEntity getPrimaryDataEntityRekursiveForPersistenIdentifier(PrimaryDataEntity entity, long versionNumber,
			PersistentIdentifier persistentIdentifier) throws EdalException;

}
