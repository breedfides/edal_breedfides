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
package de.ipk_gatersleben.bit.bi.edal.rmi.client;

import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityVersionRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataFileRmiInterface;

/**
 * SampleClient version of {@link PrimaryDataEntityVersion} to use eDAL system
 * like the stand-alone implementation.
 * 
 * @author arendd
 */
public class ClientPrimaryDataEntityVersion extends ClientContext
		implements Comparable<ClientPrimaryDataEntityVersion> {

	protected PrimaryDataEntityVersionRmiInterface myEntityVersionRMI;

	protected ClientPrimaryDataEntityVersion(PrimaryDataEntityVersionRmiInterface version, ClientDataManager client) {
		super(client);
		myEntityVersionRMI = version;
	}

	@Override
	public int compareTo(ClientPrimaryDataEntityVersion o) {
		try {
			if (o.getRevision().equals(this.getRevision())) {
				return 0;
			}
			return this.getRevision() < o.getRevision() ? -1 : 1;

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * {@link PrimaryDataEntityVersion#getCreationDate()}
	 * 
	 * @return creationDate
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public Calendar getCreationDate() throws RemoteException {
		return this.myEntityVersionRMI.getCreationDate();

	}

	/**
	 * {@link PrimaryDataEntityVersion#getEntity()}
	 * 
	 * @return the corresponding {@link ClientPrimaryDataEntity}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */

	public ClientPrimaryDataEntity getEntity() throws RemoteException {

		if (myEntityVersionRMI.getEntity().isDirectory(clientDataManager.getSubject())) {

			return new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) myEntityVersionRMI.getEntity(),
					clientDataManager);
		} else {
			return new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) myEntityVersionRMI.getEntity(),
					clientDataManager);

		}
	}

	/**
	 * {@link PrimaryDataEntityVersion#getMetaData()}
	 * 
	 * @return the corresponding meta data
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public MetaData getMetaData() throws RemoteException {
		return myEntityVersionRMI.getMetaData();
	}

	/**
	 * {@link PrimaryDataEntityVersion#getOwner()}
	 * 
	 * @return the owner.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public Principal getOwner() throws RemoteException {
		return myEntityVersionRMI.getOwner();
	}

	/**
	 * {@link PrimaryDataEntityVersion#getPublicReference(PersistentIdentifier)}
	 * 
	 * @param identifierType
	 *            of the {@link PublicReference}
	 * @return the found {@link PublicReference}
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PublicReference} with this
	 *             {@link PersistentIdentifier} defined.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public PublicReference getPublicReference(PersistentIdentifier identifierType)
			throws RemoteException, PrimaryDataEntityVersionException {
		return this.myEntityVersionRMI.getPublicReference(identifierType);
	}

	/**
	 * {@link PrimaryDataEntityVersion#getPublicReferences()}
	 * 
	 * @return read only {@link List} of {@link PublicReference}.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public List<PublicReference> getPublicReferences() throws RemoteException {
		return this.myEntityVersionRMI.getPublicReferences();
	}

	/**
	 * {@link PrimaryDataEntityVersion#getRevision()}
	 * 
	 * @return the revision number
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public Long getRevision() throws RemoteException {
		return myEntityVersionRMI.getRevision();
	}

	/**
	 * {@link PrimaryDataEntityVersion#getRevisionDate()}
	 * 
	 * @return revisionDate
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public Calendar getRevisionDate() throws RemoteException {
		return this.myEntityVersionRMI.getRevisionDate();
	}

	/**
	 * {@link PrimaryDataEntityVersion#isDeleted()}
	 * 
	 * @return <code>true</code> if the {@link ClientPrimaryDataEntityVersion}
	 *         is marked as deleted;<code>false</code> otherwise.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public boolean isDeleted() throws RemoteException {
		return this.myEntityVersionRMI.isDeleted();
	}

	/**
	 * {@link PrimaryDataEntityVersion#setAllReferencesPublic(InternetAddress)}
	 * 
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user.
	 * @param releaseDate
	 *            the release data
	 * @throws PublicReferenceException
	 *             if unable to request the {@link PublicReference} to set
	 *             public.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public void setAllReferencesPublic(InternetAddress emailNotificationAddress, Calendar releaseDate)
			throws PublicReferenceException, RemoteException {
		this.myEntityVersionRMI.setAllReferencesPublic(emailNotificationAddress, releaseDate);
	}
}