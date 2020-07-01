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
package de.ipk_gatersleben.bit.bi.edal.primary_data.security;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;

/**
 * Interface that provide all necessary functions for the security system of the
 * eDAL API.
 * 
 * @author lange
 * @author arendd
 */
public interface PermissionProvider {

	/**
	 * Getter all supported {@link Principal}s of the current eDAL system.
	 * 
	 * @return the list of supported {@link Principal}s
	 * @throws EdalException
	 *             if unable to load {@link Principal}s.
	 */
	List<Class<? extends Principal>> getSupportedPrincipals() throws EdalException;

	/**
	 * Check if the root user is already validated
	 * 
	 * @param address
	 *            the email address of the root user
	 * @return yes if the root user is already validated; no if not
	 */
	boolean isRootValidated(InternetAddress address);

	/**
	 * Load the email address of the existing root user.
	 * 
	 * @return the email address.
	 * @throws EdalException
	 *             If unable to load existing root user.
	 */
	InternetAddress getCurrentRootUser() throws EdalException;

	/**
	 * Validate the root user with the given email address and {@link UUID}
	 * 
	 * @param address
	 *            the email address of the root user
	 * @param uuid
	 *            the {@link UUID} of the root user
	 * @return true if the user is valid, otherwise false
	 */
	boolean validateRootUser(InternetAddress address, UUID uuid);

	/**
	 * Store a new root user to the back-end
	 * 
	 * @param subject
	 *            the {@link Subject} of the root user
	 * @param address
	 *            the email address of the root user
	 * @param uuid
	 *            the {@link UUID} of the root user
	 * @throws EdalException
	 *             if unable to store
	 */
	void storeRootUser(Subject subject, InternetAddress address, UUID uuid) throws EdalException;

	/**
	 * Provides all granted {@link EdalPermission} object for a {@link Set} of
	 * {@link Principal} objects.
	 * 
	 * A {@link Set} of {@link Principal} represent a
	 * {@link javax.security.auth.Subject}.
	 * 
	 * @param principalList
	 *            a {@link Set} of {@link Principal} objects.
	 * @return a {@link List} of all granted {@link EdalPermission} objects.
	 */
	List<EdalPermission> findPermissions(final Set<Principal> principalList);

	/**
	 * Grant one {@link EdalPermission} for one {@link Principal}.
	 * <p>
	 * <em>grantPermission(Principal,Method)</em>
	 * 
	 * @param principalType
	 *            the type of the {@link Principal} object.
	 * @param principalName
	 *            the name of the {@link Principal} object.
	 * @param edalPermission
	 *            the {@link EdalPermission} to grant.
	 * @throws PrimaryDataEntityException
	 *             if unable to grant {@link EdalPermission}.
	 */
	void grantPermission(final String principalType, final String principalName, final EdalPermission edalPermission)
			throws PrimaryDataEntityException;

	/**
	 * Grant all {@link EdalPermission}s for one {@link Principal}.
	 * <p>
	 * <em>grantPermission(Principal,ALL)</em>
	 * 
	 * @param principalType
	 *            the type of the {@link Principal} object.
	 * @param principalName
	 *            the name of the {@link Principal} object.
	 * @param entity
	 *            the {@link PrimaryDataEntity} to grant.
	 * @throws PrimaryDataEntityException
	 *             if unable to grant {@link EdalPermission}.
	 */
	void grantPermission(final String principalType, final String principalName, final PrimaryDataEntity entity)
			throws PrimaryDataEntityException;

	/**
	 * Check if the {@link Principal} is a root user.
	 * 
	 * @param principal
	 *            the {@link Principal} to check.
	 * @return <code>true</code> when the {@link Principal} is a root user;
	 *         <code>false</code> otherwise.
	 */
	boolean isRoot(final Principal principal);

	/**
	 * Revoke one {@link EdalPermission} for one {@link Principal}.
	 * 
	 * @param principalType
	 *            the type of the {@link Principal} object.
	 * @param principalName
	 *            the name of the {@link Principal} object.
	 * @param edalPermission
	 *            the {@link EdalPermission} to revoke.
	 * @throws PrimaryDataEntityException
	 *             if unable to revoke {@link EdalPermission}.
	 */
	void revokePermission(final String principalType, final String principalName, final EdalPermission edalPermission)
			throws PrimaryDataEntityException;

	/**
	 * Revoke all {@link EdalPermission}s for one {@link Principal}.
	 * <p>
	 * <em>revokePermission(Principal,ALL)</em>
	 * 
	 * @param principalType
	 *            the type of the {@link Principal} object.
	 * @param principalName
	 *            the name of the {@link Principal} object.
	 * @param entity
	 *            the {@link PrimaryDataEntity} to revoke.
	 * @throws PrimaryDataEntityException
	 *             if unable to revoke {@link EdalPermission}.
	 */
	void revokePermission(final String principalType, final String principalName, final PrimaryDataEntity entity)
			throws PrimaryDataEntityException;

	/**
	 * Set the id of the current {@link PrimaryDataEntity} object.
	 * 
	 * @param id
	 *            the id to set.
	 */
	void setPermissionObjectID(String id);

}
