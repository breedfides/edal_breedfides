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
package de.ipk_gatersleben.bit.bi.edal.rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * RMI Interface for
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
 * objects.
 * 
 * @author arendd
 */
public interface PrimaryDataEntityVersionRmiInterface extends Remote {

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getCreationDate()}
	 * 
	 * @return the creation date of this version.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	Calendar getCreationDate() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getEntity()}
	 * 
	 * @return the corresponding
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 *         to this version.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	PrimaryDataEntityRmiInterface getEntity() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getMetaData()}
	 * 
	 * @return the corresponding {@link MetaData} to this version.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	MetaData getMetaData() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getPublicReferences()}
	 * 
	 * @return read only {@link List} of {@link PublicReference}.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	List<PublicReference> getPublicReferences() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getPublicReference(PersistentIdentifier)}
	 * 
	 * @param identifierType
	 *            of the {@link PublicReference}
	 * 
	 * @return the found {@link PublicReference}
	 * 
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PublicReference} with this
	 *             {@link PersistentIdentifier} defined.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	PublicReference getPublicReference(PersistentIdentifier identifierType)
			throws PrimaryDataEntityVersionException, RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getRevision()}
	 * .
	 * 
	 * @return the revision number of this version.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	Long getRevision() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getRevisionDate()}
	 * 
	 * @return the revision date of this version.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	Calendar getRevisionDate() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#isDeleted()}
	 * 
	 * @return if the version is marked as deleted.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	boolean isDeleted() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#setAllReferencesPublic(InternetAddress)}
	 * 
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user.
	 * @param releaseDate
	 *            the {@link Date} when the reference should be released
	 * @throws PublicReferenceException
	 *             if unable to request the {@link PublicReference} to set
	 *             public.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	void setAllReferencesPublic(InternetAddress emailNotificationAddress,
			Calendar releaseDate) throws PublicReferenceException,
			RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion#getOwner()}
	 * 
	 * @return the owner of this version
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	Principal getOwner() throws RemoteException;
}