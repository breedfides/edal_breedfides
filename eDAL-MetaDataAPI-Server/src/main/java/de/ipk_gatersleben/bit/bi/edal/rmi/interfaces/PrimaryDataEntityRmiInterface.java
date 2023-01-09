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
package de.ipk_gatersleben.bit.bi.edal.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * RMI Interface for
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
 * objects.
 * 
 * @author arendd
 */
public interface PrimaryDataEntityRmiInterface extends Remote {

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#addPublicReference(PersistentIdentifier)}
	 * 
	 * @param identifierType
	 *            the type of the new Identifier.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if unable to add the {@link PublicReference} to this
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             .
	 */
	void addPublicReference(PersistentIdentifier identifierType, Subject subject)
			throws RemoteException, PrimaryDataEntityException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#delete()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if trying to delete the root
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 * @throws PrimaryDataDirectoryException
	 *             if trying to delete a non-current
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *             .
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#delete()
	 */
	void delete(Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getCurrentVersion()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission..
	 * @return the currently set
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getCurrentVersion()
	 */
	PrimaryDataEntityVersionRmiInterface getCurrentVersion(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getID()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the ID of this
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         .
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getID()
	 */
	String getID(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getMetaData()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return {@link MetaData} object of the current
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *         of this
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getMetaData()
	 */
	MetaData getMetaData(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getName()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the file name.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getName()
	 */
	String getName(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getParentDirectory()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the parent
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *         of this
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         .
	 * @throws PrimaryDataDirectoryException
	 *             if trying to access the parent
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             of the root
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             .
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getParentDirectory()
	 */
	PrimaryDataDirectoryRmiInterface getParentDirectory(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getPath()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the complete path as {@link String} to the
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         object
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getPath()
	 */
	String getPath(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getPermissions()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return an unmodifiable {@link List} containing all
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission}
	 *         .
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if unable to load permissions
	 */
	Map<Principal, List<Methods>> getPermissions(Subject subject) throws PrimaryDataEntityException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getPublicReferences()}
	 * .
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return a {@link List} with all stored {@link PublicReference}s of the
	 *         current
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *         of this
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         .
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	List<PublicReference> getPublicReferences(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersions()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return an unmodifiable {@link java.util.SortedSet} containing all
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersions()
	 */
	Collection<PrimaryDataEntityVersionRmiInterface> getVersions(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersionByDate(Calendar)}
	 * 
	 * 
	 * @param date
	 *            the date of the {@link PrimaryDataEntityVersion}
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PrimaryDataEntityVersion} with this
	 *             version stored.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersionByDate(Calendar)
	 */
	PrimaryDataEntityVersionRmiInterface getVersionByDate(Calendar date, Subject subject)
			throws PrimaryDataEntityVersionException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersionByRevisionNumber(long)}
	 * 
	 * @param revisionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PrimaryDataEntityVersion} with this
	 *             version stored.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#getVersionByRevisionNumber(long)
	 */
	PrimaryDataEntityVersionRmiInterface getVersionByRevisionNumber(long revisionNumber, Subject subject)
			throws PrimaryDataEntityVersionException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#grantPermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
	 * 
	 * @param principal
	 *            to grant this method
	 * @param method
	 *            {@link de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods}
	 *            for a the method
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if can not found method to grant.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#grantPermission(Principal,
	 *      de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)
	 */
	void grantPermission(Principal principal, Methods method, Subject subject)
			throws RemoteException, PrimaryDataEntityException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#isDirectory()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return <code>true</code> when it is a
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *         ;<br>
	 *         <code>false</code> otherwise.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#isDirectory()
	 */
	boolean isDirectory(Subject subject) throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#move(de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory)}
	 * 
	 * @param destinationDirectory
	 *            the new parent
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            for this
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *            .
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if trying to move the root
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             .
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#move(de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory)
	 */
	void move(PrimaryDataDirectoryRmiInterface destinationDirectory, Subject subject)
			throws RemoteException, PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#rename(String)}
	 * 
	 * @param name
	 *            the new name for this
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *            .
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to set the new
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *             .
	 * @throws PrimaryDataDirectoryException
	 *             if an
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             with this name already exists.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#rename(String)
	 */
	void rename(String name, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#revokePermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
	 * 
	 * @param principal
	 *            to revoke this method.
	 * @param method
	 *            {@link de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods}
	 *            for a the method.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if trying to revoke the
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission}
	 *             for
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#grantPermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
	 *             of your own
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             .
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#revokePermission(Principal,
	 *      de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)
	 */
	void revokePermission(Principal principal, Methods method, Subject subject)
			throws RemoteException, PrimaryDataEntityException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#setMetaData(MetaData)}
	 * 
	 * @param metadata
	 *            the new {@link MetaData} object to set.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *             .
	 * @throws MetaDataException
	 *             if there are non valid value for element in the
	 *             {@link MetaData} object.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity#setMetaData(MetaData)
	 */
	void setMetaData(MetaData metadata, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException;
}