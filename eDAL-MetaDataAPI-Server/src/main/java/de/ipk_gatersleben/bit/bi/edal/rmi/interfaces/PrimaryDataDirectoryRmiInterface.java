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
package de.ipk_gatersleben.bit.bi.edal.rmi.interfaces;

import java.rmi.RemoteException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * RMI Interface for
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
 * objects.
 * 
 * @author arendd
 */
public interface PrimaryDataDirectoryRmiInterface extends
		PrimaryDataEntityRmiInterface {

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#createPrimaryDataDirectory(String)}
	 * 
	 * @param path
	 *            the name of the new
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            .
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @param defaultPermissions
	 *            current default permissions.
	 * @return the new
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *         object.<br>
	 *         <code>null</code> if failed
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if the current
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *             of this
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             is marked as deleted or if there is already a
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             with the same name.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#createPrimaryDataDirectory(String)
	 */
	PrimaryDataDirectoryRmiInterface createPrimaryDataDirectory(
			final String path, Subject subject,
			Map<Principal, List<Methods>> defaultPermissions)
			throws RemoteException, PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#createPrimaryDataFile(String)}
	 * 
	 * @param name
	 *            of the new
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile}
	 *            object.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @param defaultPermissions
	 *            current default permissions
	 * @return the new
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile}
	 *         .<br>
	 *         <code>null</code> if failed
	 * @throws PrimaryDataDirectoryException
	 *             if the current
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
	 *             of this
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             is marked as deleted or if there is already a
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             with the same name.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#createPrimaryDataFile(String)
	 */
	PrimaryDataFileRmiInterface createPrimaryDataFile(String name,
			Subject subject, Map<Principal, List<Methods>> defaultPermissions)
			throws PrimaryDataDirectoryException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#exist(String)}
	 * 
	 * @param path
	 *            the name of the
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *            to check.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return <code>true</code> if there is already a PrimartyDataEntity with
	 *         the same path;<br>
	 *         <code>false</code> otherwise
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             objects in this
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             to check if the name already exists.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#exist(String)
	 */
	boolean exist(String path, Subject subject) throws RemoteException,
			PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#getAllPublishedEntities()}
	 * 
	 * @return a {@link List} of all
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         with a
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 *         .
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load objcts.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	List<PrimaryDataEntityRmiInterface> getAllPublishedEntities(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#getPrimaryDataEntity(String)}
	 * 
	 * @param name
	 *            name of the
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *            in this
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            .
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return the found
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         object.
	 * @throws PrimaryDataDirectoryException
	 *             if no such
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             exists.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#getPrimaryDataEntity(String)
	 */
	PrimaryDataEntityRmiInterface getPrimaryDataEntity(final String name,
			Subject subject) throws PrimaryDataDirectoryException,
			RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#listPrimaryDataEntities()}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return an unmodifiable {@link List} containing all
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             objects in this
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *             .
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#listPrimaryDataEntities()
	 */
	List<PrimaryDataEntityRmiInterface> listPrimaryDataEntities(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByDublinCoreElement(EnumDublinCoreElements, UntypedData, boolean, boolean)}
	 * 
	 * @param element
	 *            the {@link EnumDublinCoreElements} for query.
	 * @param data
	 *            {@link UntypedData} parameter for search.
	 * @param fuzzy
	 *            <code>true</code>: exact search;<br>
	 *            <code>false</code>: fuzzy search.
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub directories
	 *            recursively;<br>
	 *            <code>false</code>: search only in the current
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            object.
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return an unmodifiable {@link List} of
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         that match the parameter.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             object or if there are too much results.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByDublinCoreElement(EnumDublinCoreElements,
	 *      UntypedData, boolean, boolean)
	 */
	List<PrimaryDataEntityRmiInterface> searchByDublinCoreElement(
			final EnumDublinCoreElements element, final UntypedData data,
			final boolean fuzzy, final boolean recursiveIntoSubdirectories,
			Subject subject) throws RemoteException,
			PrimaryDataDirectoryException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByMetaData(MetaData, boolean, boolean)}
	 * 
	 * @param query
	 *            a {@link MetaData} object for query.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search;<br>
	 *            <code>false</code>: exact search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            recursively;<br>
	 *            <code>false</code> search only in the current
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *            object
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @return a unmodifiable {@link List} of
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         that match the {@link MetaData} parameter
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *             object or if there are too much results.
	 * @throws MetaDataException
	 *             if there are non valid values for some {@link MetaData}
	 *             elements.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByMetaData(MetaData,
	 *      boolean, boolean)
	 */
	List<PrimaryDataEntityRmiInterface> searchByMetaData(final MetaData query,
			final boolean fuzzy, final boolean recursiveIntoSubdirectories,
			Subject subject) throws RemoteException,
			PrimaryDataDirectoryException, MetaDataException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @param publicationStatus
	 *            the {@link PublicationStatus} of the searched
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *            .
	 * @return a {@link List} of all
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         with a
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 *         .
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for {@link PublicationStatus}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)
	 */
	List<PrimaryDataEntityRmiInterface> searchByPublicationStatus(
			PublicationStatus publicationStatus, Subject subject)
			throws PrimaryDataDirectoryException, RemoteException;

	/**
	 * {@link PrimaryDataDirectory#searchByKeyword(String, boolean, boolean)}
	 * 
	 * @param subject
	 *            the authenticated subject for check permission.
	 * @param keyword
	 *            the term to search over all elements.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search; <code>false</code>: exact
	 *            search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively; <code>false</code>
	 *            search only in the current {@link PrimaryDataDirectory} object
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the keyword.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for the keyword.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory#searchByKeyword(String,
	 *      boolean, boolean)
	 */
	public List<PrimaryDataEntityRmiInterface> searchByKeyword(String keyword,
			boolean fuzzy, boolean recursiveIntoSubdirectories, Subject subject)
			throws PrimaryDataDirectoryException, RemoteException;

	/** {@inheritDoc} */
	void setMetaData(MetaData metadata, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException,
			MetaDataException;

}
