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

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import org.apache.logging.log4j.Logger;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ContentNegotiationType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.Referenceable;

public class ContentNegotiator {

	public static StringBuffer generateContentNogitiation(final PrimaryDataEntity entity, final long versionNumber,
			final String internalId, final PersistentIdentifier identifierType, final ContentNegotiationType type)
			throws EdalException {

		Calendar date = null;

		try {
			date = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance()
					.getPublicReferenceByInternalId(internalId).getCreationDate();
		} catch (EdalException | RuntimeException | ReflectiveOperationException e) {
			throw new EdalException("unable to initialize ApprovalServiceProvider: " + e.getMessage(), e);
		}

		/**
		 * try if the given PublicReference matches to the given version number,
		 * otherwise send error message
		 */
		try {
			if (!entity.getVersionByDate(date).equals(entity.getVersionByRevisionNumber(versionNumber))) {
				throw new EdalException("PublicReference and version number are not compatible");
			}
		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException("unable to load versions of " + entity + " :" + e.getMessage(), e);
		}

		final Logger log = DataManager.getImplProv().getLogger();

		PrimaryDataEntity currentEntity = entity;

		PrimaryDataEntityVersion primaryDataEntityVersion = null;

		try {
			primaryDataEntityVersion = currentEntity.getVersionByRevisionNumber(versionNumber);

			boolean foundPublicReference = false;
			try {
				if (primaryDataEntityVersion.getPublicReference(identifierType).getPublicationStatus()
						.equals(PublicationStatus.ACCEPTED)) {

					currentEntity.switchCurrentVersion(primaryDataEntityVersion);

				}

			} catch (final PrimaryDataEntityVersionException e) {
				log.debug(currentEntity + " has no " + identifierType);
				while (!foundPublicReference) {
					try {
						log.debug("try ParentDirectory '" + currentEntity.getParentDirectory() + "'");

						if (currentEntity.getParentDirectory() == null) {
							throw new EdalException("No Public Reference for this version set!");
						}

						if (currentEntity.getParentDirectory().getVersionByDate(date).getPublicReference(identifierType)
								.getPublicationStatus().equals(PublicationStatus.ACCEPTED)) {

							if (primaryDataEntityVersion.getRevisionDate().before(date)) {

								log.debug(currentEntity.getParentDirectory() + " has " + identifierType);

								foundPublicReference = true;
								currentEntity = currentEntity.getParentDirectory();

							} else {
								throw new EdalException("No Public Reference for this version set!");
							}
						}

					} catch (PrimaryDataEntityVersionException | PrimaryDataDirectoryException e1) {

						log.debug("ParentDirectory has no " + identifierType);

						foundPublicReference = false;

						try {
							currentEntity = currentEntity.getParentDirectory();
						} catch (final PrimaryDataDirectoryException e2) {
							throw new EdalException("unable to get parent directory: " + e.getMessage(), e);
						}
					}
				}
			}

		} catch (final PrimaryDataEntityVersionException e) {
			throw new EdalException("unable to get version by version number: " + e.getMessage(), e);
		}

		try {
			Referenceable referencable = null;
			try {
				referencable = identifierType.getImplClass().getDeclaredConstructor().newInstance();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuffer buffer = referencable
					.negotiateContent(currentEntity.getVersionByDate(date).getPublicReference(identifierType), type);

			return buffer;
		} catch (InstantiationException | IllegalAccessException | PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
