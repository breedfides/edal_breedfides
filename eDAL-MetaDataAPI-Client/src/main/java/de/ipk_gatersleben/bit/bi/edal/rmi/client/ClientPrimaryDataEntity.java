/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client;

import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityVersionRmiInterface;

/**
 * SampleClient version of {@link PrimaryDataEntity} to use eDAL system like the
 * stand-alone implementation.
 * 
 * @author arendd
 */
public abstract class ClientPrimaryDataEntity extends ClientContext implements Comparable<ClientPrimaryDataEntity> {

	protected PrimaryDataEntityRmiInterface myEntityRMI;

	protected ClientPrimaryDataEntityVersion currentVersion = null;

	/**
	 * Constructor for ClientPrimaryDataEntity.
	 * 
	 * @param entity
	 *            a {@link PrimaryDataEntityRmiInterface} object.
	 * @param client
	 *            the {@link ClientDataManager}
	 */
	public ClientPrimaryDataEntity(PrimaryDataEntityRmiInterface entity, ClientDataManager client) {
		super(client);
		this.myEntityRMI = entity;
	}

	public void addPublicReference(PersistentIdentifier identifierType)
			throws RemoteException, PrimaryDataEntityException {

		myEntityRMI.addPublicReference(identifierType, clientDataManager.getSubject());

		this.currentVersion = this.getVersions().last();

	}

	/**
	 * {@link PrimaryDataEntity#delete()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#delete(javax.security.auth.Subject)
	 * @throws PrimaryDataEntityVersionException
	 *             if trying to delete the root {@link PrimaryDataDirectory}
	 * @throws PrimaryDataDirectoryException
	 *             if trying to delete a non-current
	 *             {@link PrimaryDataEntityVersion}.
	 */
	public void delete() throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException {
		myEntityRMI.delete(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getCurrentVersion()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getCurrentVersion(javax.security.auth.Subject)
	 * @return the currently set {@link PrimaryDataEntityVersion}
	 */
	public ClientPrimaryDataEntityVersion getCurrentVersion() throws RemoteException {

		if (this.currentVersion == null) {
			return new ClientPrimaryDataEntityVersion(myEntityRMI.getCurrentVersion(clientDataManager.getSubject()),
					clientDataManager);
		} else {
			return this.currentVersion;
		}
	}

	/**
	 * {@link PrimaryDataEntity#getID()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getID(javax.security.auth.Subject)
	 * @return the ID of this {@link PrimaryDataEntity}.
	 */
	public String getID() throws RemoteException {
		return myEntityRMI.getID(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getMetaData()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getMetaData(javax.security.auth.Subject)
	 * @return {@link MetaData} object of the current
	 *         {@link PrimaryDataEntityVersion} of this
	 *         {@link PrimaryDataEntity}.
	 */
	public MetaData getMetaData() throws RemoteException {
		return myEntityRMI.getMetaData(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getName()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getName(javax.security.auth.Subject)
	 * @return the name of this {@link PrimaryDataEntity}.
	 */
	public String getName() throws RemoteException {
		return myEntityRMI.getName(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getParentDirectory()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if trying to access the parent {@link PrimaryDataDirectory}
	 *             of the root {@link PrimaryDataDirectory}.
	 * @see PrimaryDataEntityRmiInterface#getParentDirectory(javax.security.auth.Subject)
	 * @return the parent {@link PrimaryDataDirectory} of this
	 *         {@link PrimaryDataEntity}.
	 */
	public ClientPrimaryDataDirectory getParentDirectory() throws PrimaryDataDirectoryException, RemoteException {
		return new ClientPrimaryDataDirectory(myEntityRMI.getParentDirectory(clientDataManager.getSubject()),
				clientDataManager);
	}

	/**
	 * {@link PrimaryDataEntity#getPath()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getPath(javax.security.auth.Subject)
	 * @return the complete path as {@link String} to the
	 *         {@link PrimaryDataEntity} object
	 */
	public String getPath() throws RemoteException {
		return myEntityRMI.getPath(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getPermissions()}
	 * 
	 * @see PrimaryDataEntityRmiInterface#getPermissions(Subject)
	 * @return the map with all permissions
	 * @throws RemoteException
	 *             if unable to call remote function
	 * @throws PrimaryDataEntityException if unable to load permissions
	 */
	public Map<Principal, List<Methods>> getPermissions() throws RemoteException, PrimaryDataEntityException {
		return myEntityRMI.getPermissions(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getPublicReferences()}
	 * 
	 * @see PrimaryDataEntityRmiInterface#getPublicReferences(Subject)
	 * @return a {@link List} with all {@link PublicReference}s.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public List<PublicReference> getPublicReferences() throws RemoteException {
		return myEntityRMI.getPublicReferences(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#getVersions()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#getVersions(javax.security.auth.Subject)
	 * @return an unmodifiable {@link SortedSet} containing all
	 *         {@link PrimaryDataEntity}
	 */
	public SortedSet<ClientPrimaryDataEntityVersion> getVersions() throws RemoteException {

		SortedSet<ClientPrimaryDataEntityVersion> externalSet = new TreeSet<ClientPrimaryDataEntityVersion>();

		for (PrimaryDataEntityVersionRmiInterface primaryDataEntityVersionRMI : myEntityRMI
				.getVersions(clientDataManager.getSubject())) {
			externalSet.add(new ClientPrimaryDataEntityVersion(primaryDataEntityVersionRMI, clientDataManager));
		}

		return externalSet;
	}

	/**
	 * {@link PrimaryDataEntity#getVersionByDate(Calendar)}
	 * 
	 * @param date
	 *            the date of the {@link PrimaryDataEntityVersion}
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if there are only older {@link PrimaryDataEntityVersion}
	 *             stored for this {@link PrimaryDataEntity}
	 * @see PrimaryDataEntity#getVersionByDate(Calendar)
	 */
	public ClientPrimaryDataEntityVersion getVersionByDate(Calendar date)
			throws RemoteException, PrimaryDataEntityVersionException {

		return new ClientPrimaryDataEntityVersion(myEntityRMI.getVersionByDate(date, clientDataManager.getSubject()),
				clientDataManager);
	}

	/**
	 * {@link PrimaryDataEntity#getVersionByRevisionNumber(long)}
	 * 
	 * @param revisionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PrimaryDataEntityVersion} with this
	 *             version stored.
	 * @see PrimaryDataEntity#getVersionByRevisionNumber(long)
	 */
	public ClientPrimaryDataEntityVersion getVersionByRevisionNumber(long revisionNumber)
			throws RemoteException, PrimaryDataEntityVersionException {

		return new ClientPrimaryDataEntityVersion(
				myEntityRMI.getVersionByRevisionNumber(revisionNumber, clientDataManager.getSubject()),
				clientDataManager);
	}

	/**
	 * PrimaryDataEntity#grantPermission(Principal, Methods)}
	 * 
	 * @param principal
	 *            to grant this method
	 * @param method
	 *            {@link Enum} for a the method
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if can not found method to grant.
	 */
	public void grantPermission(Principal principal, Methods method)
			throws RemoteException, PrimaryDataEntityException {
		myEntityRMI.grantPermission(principal, method, clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#isDirectory()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataEntityRmiInterface#isDirectory(javax.security.auth.Subject)
	 * @return <code>true</code> when it is a {@link PrimaryDataDirectory};<br>
	 *         <code>false</code> otherwise.
	 */
	public boolean isDirectory() throws RemoteException {
		return myEntityRMI.isDirectory(clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#move(PrimaryDataDirectory)}
	 * 
	 * @param destinationDirectory
	 *            the new parent {@link PrimaryDataDirectory} for this
	 *            {@link PrimaryDataEntity}.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if trying to move the root {@link PrimaryDataDirectory}.
	 * @see PrimaryDataEntityRmiInterface#move(PrimaryDataDirectoryRmiInterface,
	 *      javax.security.auth.Subject)
	 */
	public void move(ClientPrimaryDataDirectory destinationDirectory)
			throws RemoteException, PrimaryDataDirectoryException {
		myEntityRMI.move((PrimaryDataDirectoryRmiInterface) destinationDirectory.myEntityRMI,
				clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#rename(String)}
	 * 
	 * @param name
	 *            the new name for this {@link PrimaryDataEntity}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to set the new {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException
	 *             if an {@link PrimaryDataEntity} with this name already
	 *             exists.
	 * @see PrimaryDataEntityRmiInterface#rename(java.lang.String,
	 *      javax.security.auth.Subject)
	 */
	public void rename(String name)
			throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException {
		myEntityRMI.rename(name, clientDataManager.getSubject());
		this.currentVersion = this.getVersions().last();

	}

	/**
	 * PrimaryDataEntity#revokePermission(Principal, Enum)
	 * 
	 * @param principal
	 *            to revoke this method.
	 * @param method
	 *            {@link Enum} for a the method.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityException
	 *             if trying to revoke the {@link EdalPermission} for
	 *             PrimaryDataEntity#grantPermission(Principal, Enum) of your
	 *             own {@link PrimaryDataEntity}.
	 */
	public void revokePermission(Principal principal, Methods method)
			throws RemoteException, PrimaryDataEntityException {
		myEntityRMI.revokePermission(principal, method, clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#setMetaData(MetaData)}
	 * 
	 * @param new_metadata
	 *            the new {@link MetaData} object to set.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store {@link PrimaryDataEntityVersion}.
	 * @throws MetaDataException
	 *             if there are non valid value for element in the
	 *             {@link MetaData} object.
	 * @throws AccessControlException
	 *             if the user with the current {@link Subject} is not allowed
	 *             to execute this method. The AccessControlException is thrown
	 *             by AspectJ.
	 * @see PrimaryDataEntityRmiInterface#setMetaData(MetaData, Subject)
	 */
	public void setMetaData(MetaData new_metadata)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {
		myEntityRMI.setMetaData(new_metadata, clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataEntity#switchCurrentVersion(PrimaryDataEntityVersion)}
	 * 
	 * @param version
	 *            the new {@link PrimaryDataEntityVersion} to set as current
	 *            {@link PrimaryDataEntityVersion}.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataEntityVersionException
	 *             if requested {@link PrimaryDataEntityVersion} is not
	 *             available or marked as deleted.
	 */
	public void switchCurrentVersion(ClientPrimaryDataEntityVersion version)
			throws RemoteException, PrimaryDataEntityVersionException {

		if (!this.getVersions().contains(version)) {
			throw new PrimaryDataEntityVersionException("Requested version not available !");
		}
		if (!this.getVersions().last().equals(version) && this.getVersions().last().isDeleted()) {
			throw new PrimaryDataEntityVersionException("Requested version is marked as deleted !");
		}

		this.currentVersion = version;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		try {
			result = prime * result + (this.getID() == null ? 0 : this.getID().hashCode());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		try {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ClientPrimaryDataEntity)) {
				return false;
			}
			final ClientPrimaryDataEntity other = (ClientPrimaryDataEntity) obj;
			if (this.getID() == null) {
				if (other.getID() != null) {
					return false;
				}
			} else if (!this.getID().equals(other.getID())) {
				return false;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public int compareTo(ClientPrimaryDataEntity other) {
		try {
			return this.getName().compareToIgnoreCase(other.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;

	}

}
