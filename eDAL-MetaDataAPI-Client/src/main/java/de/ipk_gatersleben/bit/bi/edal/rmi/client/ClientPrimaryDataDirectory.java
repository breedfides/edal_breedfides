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
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataFileRmiInterface;

/**
 * SampleClient version of {@link PrimaryDataDirectory} to use eDAL system like
 * the stand-alone implementation.
 * 
 * @author arendd
 */
public class ClientPrimaryDataDirectory extends ClientPrimaryDataEntity {

	/**
	 * Constructor for ClientPrimaryDataDirectory.
	 * 
	 * @param directory
	 *            a {@link PrimaryDataDirectoryRmiInterface} object.
	 * @param client
	 *            the {@link ClientDataManager}
	 */
	public ClientPrimaryDataDirectory(PrimaryDataDirectoryRmiInterface directory, ClientDataManager client) {
		super(directory, client);
	}

	/**
	 * {@link PrimaryDataDirectory#createPrimaryDataDirectory(String)}
	 * 
	 * @param path
	 *            the name of the new {@link PrimaryDataDirectory}.
	 * @return the new {@link PrimaryDataDirectory} object.<br>
	 *         <code>null</code> if failed
	 * @throws PrimaryDataDirectoryException
	 *             if the current {@link PrimaryDataEntityVersion} of this
	 *             {@link PrimaryDataDirectory} is marked as deleted or if there
	 *             is already a {@link PrimaryDataEntity} with the same name.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataDirectoryRmiInterface#createPrimaryDataDirectory(String,
	 *      Subject, Map)
	 */
	public ClientPrimaryDataDirectory createPrimaryDataDirectory(String path)
			throws RemoteException, PrimaryDataDirectoryException {
		return new ClientPrimaryDataDirectory(
				((PrimaryDataDirectoryRmiInterface) myEntityRMI).createPrimaryDataDirectory(path,
						clientDataManager.getSubject(), clientDataManager.getDefaultPermissions()),
				clientDataManager);
	}

	/**
	 * {@link PrimaryDataDirectory#createPrimaryDataFile(String)}
	 * 
	 * @param name
	 *            of the new {@link PrimaryDataFile} object.
	 * @return the new {@link PrimaryDataFile}.<br>
	 *         <code>null</code> if failed
	 * @throws PrimaryDataDirectoryException
	 *             if the current {@link PrimaryDataEntityVersion} of this
	 *             {@link PrimaryDataDirectory} is marked as deleted or if there
	 *             is already a {@link PrimaryDataEntity} with the same name.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataDirectoryRmiInterface#createPrimaryDataFile(String,
	 *      Subject, Map)
	 */
	public ClientPrimaryDataFile createPrimaryDataFile(String name)
			throws PrimaryDataDirectoryException, RemoteException {
		return new ClientPrimaryDataFile(((PrimaryDataDirectoryRmiInterface) myEntityRMI).createPrimaryDataFile(name,
				clientDataManager.getSubject(), clientDataManager.getDefaultPermissions()), clientDataManager);
	}

	/**
	 * {@link PrimaryDataDirectory#exist(String)}
	 * 
	 * @param path
	 *            the name of the {@link PrimaryDataEntity} to check.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all {@link PrimaryDataEntity} objects in
	 *             this {@link PrimaryDataDirectory} to check if the name
	 *             already exists.
	 * @see PrimaryDataDirectoryRmiInterface#exist(String, Subject)
	 * @return <code>true</code> if there is already a PrimartyDataEntity with
	 *         the same path;<br>
	 *         <code>false</code> otherwise
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public boolean exist(String path) throws RemoteException, PrimaryDataDirectoryException {
		return ((PrimaryDataDirectoryRmiInterface) myEntityRMI).exist(path, clientDataManager.getSubject());
	}

	/**
	 * {@link PrimaryDataDirectory#getPrimaryDataEntity(String)}
	 * 
	 * @param name
	 *            name of the {@link PrimaryDataEntity} in this
	 *            {@link PrimaryDataDirectory}.
	 * @return the found {@link PrimaryDataEntity} object.
	 * @throws PrimaryDataDirectoryException
	 *             if no such {@link PrimaryDataEntity} exists.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataDirectoryRmiInterface#getPrimaryDataEntity(String,
	 *      Subject)
	 */
	public ClientPrimaryDataEntity getPrimaryDataEntity(String name)
			throws PrimaryDataDirectoryException, RemoteException {

		PrimaryDataEntityRmiInterface remote = (((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.getPrimaryDataEntity(name, clientDataManager.getSubject()));

		if (remote.isDirectory(clientDataManager.getSubject())) {

			return new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) remote, clientDataManager);
		} else {
			return new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) remote, clientDataManager);

		}
	}

	/**
	 * {@link PrimaryDataDirectory#listPrimaryDataEntities()}
	 * 
	 * @return an unmodifiable {@link List} containing all
	 *         {@link PrimaryDataEntity}
	 * @throws PrimaryDataDirectoryException
	 *             this {@link PrimaryDataDirectory}.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataDirectoryRmiInterface#listPrimaryDataEntities(Subject)
	 */
	public List<ClientPrimaryDataEntity> listPrimaryDataEntities()
			throws PrimaryDataDirectoryException, RemoteException {

		List<PrimaryDataEntityRmiInterface> rmiList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.listPrimaryDataEntities(clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> myList = new ArrayList<ClientPrimaryDataEntity>();

		for (PrimaryDataEntityRmiInterface primaryDataEntityRMI : rmiList) {
			if (primaryDataEntityRMI.isDirectory(clientDataManager.getSubject())) {
				myList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntityRMI,
						clientDataManager));
			} else {
				myList.add(new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntityRMI,
						clientDataManager));
			}
		}
		return myList;
	}

	/**
	 * {@link PrimaryDataDirectory#getAllPublishedEntities()}
	 * 
	 * @return a {@link List} of all {@link PrimaryDataEntity} with a
	 *         {@link PublicReference}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load objcts.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public List<ClientPrimaryDataEntity> getAllPublishedEntities()
			throws PrimaryDataDirectoryException, RemoteException {
		List<PrimaryDataEntityRmiInterface> internList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.getAllPublishedEntities(clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> externList = new ArrayList<ClientPrimaryDataEntity>(internList.size());

		for (PrimaryDataEntityRmiInterface primaryDataEntity : internList) {

			if (primaryDataEntity.isDirectory(clientDataManager.getSubject())) {

				externList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntity,
						clientDataManager));
			} else {
				externList.add(
						new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntity, clientDataManager));

			}
		}
		return externList;
	}

	/**
	 * {@link PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)}
	 * 
	 * @param publicationStatus
	 *            the {@link PublicationStatus} of the searched
	 *            {@link PrimaryDataEntity}.
	 * @return a {@link List} of all {@link PrimaryDataEntity} with a
	 *         {@link PublicReference}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for {@link PublicationStatus}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)
	 */
	public List<ClientPrimaryDataEntity> searchByPublicationStatus(PublicationStatus publicationStatus)
			throws PrimaryDataDirectoryException, RemoteException {

		List<PrimaryDataEntityRmiInterface> internList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.searchByPublicationStatus(publicationStatus, clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> externList = new ArrayList<ClientPrimaryDataEntity>(internList.size());

		for (PrimaryDataEntityRmiInterface primaryDataEntity : internList) {

			if (primaryDataEntity.isDirectory(clientDataManager.getSubject())) {

				externList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntity,
						clientDataManager));
			} else {
				externList.add(
						new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntity, clientDataManager));

			}
		}
		return externList;
	}

	/**
	 * {@link PrimaryDataDirectory#searchByDublinCoreElement(EnumDublinCoreElements, UntypedData, boolean, boolean)}
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
	 *            {@link PrimaryDataDirectory} object.
	 * @return an unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the parameter.
	 * @see PrimaryDataDirectoryRmiInterface#searchByDublinCoreElement(EnumDublinCoreElements,
	 *      UntypedData, boolean, boolean, Subject)
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 */
	public List<ClientPrimaryDataEntity> searchByDublinCoreElement(EnumDublinCoreElements element, UntypedData data,
			boolean fuzzy, boolean recursiveIntoSubdirectories) throws RemoteException, PrimaryDataDirectoryException {

		List<PrimaryDataEntityRmiInterface> internList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.searchByDublinCoreElement(element, data, fuzzy, recursiveIntoSubdirectories,
						clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> externList = new ArrayList<ClientPrimaryDataEntity>(internList.size());

		for (PrimaryDataEntityRmiInterface primaryDataEntity : internList) {

			if (primaryDataEntity.isDirectory(clientDataManager.getSubject())) {

				externList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntity,
						clientDataManager));
			} else {
				externList.add(
						new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntity, clientDataManager));

			}
		}
		return externList;
	}

	/**
	 * {@link PrimaryDataDirectory#searchByMetaData(MetaData, boolean, boolean)}
	 * 
	 * @param query
	 *            a {@link MetaData} object for query.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search;<br>
	 *            <code>false</code>: exact search.
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively;<br>
	 *            <code>false</code> search only in the current
	 *            {@link PrimaryDataDirectory} object.
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the {@link MetaData} parameter.
	 * @throws MetaDataException
	 *             if there are non valid values for some {@link MetaData}
	 *             elements.
	 * @see PrimaryDataDirectoryRmiInterface#searchByMetaData(MetaData, boolean,
	 *      boolean, Subject)
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 */
	public List<ClientPrimaryDataEntity> searchByMetaData(MetaData query, boolean fuzzy,
			boolean recursiveIntoSubdirectories)
					throws RemoteException, PrimaryDataDirectoryException, MetaDataException {

		List<PrimaryDataEntityRmiInterface> internList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.searchByMetaData(query, fuzzy, recursiveIntoSubdirectories, clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> externList = new ArrayList<ClientPrimaryDataEntity>(internList.size());

		for (PrimaryDataEntityRmiInterface primaryDataEntity : internList) {

			if (primaryDataEntity.isDirectory(clientDataManager.getSubject())) {

				externList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntity,
						clientDataManager));
			} else {
				externList.add(
						new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntity, clientDataManager));

			}

		}
		return externList;
	}

	/**
	 * {@link PrimaryDataDirectory#searchByKeyword(String, boolean, boolean)}
	 * 
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
	 */
	public List<ClientPrimaryDataEntity> searchByKeyword(String keyword, boolean fuzzy,
			boolean recursiveIntoSubdirectories) throws RemoteException, PrimaryDataDirectoryException {

		List<PrimaryDataEntityRmiInterface> internList = ((PrimaryDataDirectoryRmiInterface) myEntityRMI)
				.searchByKeyword(keyword, fuzzy, recursiveIntoSubdirectories, clientDataManager.getSubject());

		List<ClientPrimaryDataEntity> externList = new ArrayList<ClientPrimaryDataEntity>(internList.size());

		for (PrimaryDataEntityRmiInterface primaryDataEntity : internList) {

			if (primaryDataEntity.isDirectory(clientDataManager.getSubject())) {

				externList.add(new ClientPrimaryDataDirectory((PrimaryDataDirectoryRmiInterface) primaryDataEntity,
						clientDataManager));
			} else {
				externList.add(
						new ClientPrimaryDataFile((PrimaryDataFileRmiInterface) primaryDataEntity, clientDataManager));

			}
		}
		return externList;
	}

	/** {@inheritDoc} */
	public void setMetaData(MetaData new_metadata)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {
		((PrimaryDataDirectoryRmiInterface) myEntityRMI).setMetaData(new_metadata, clientDataManager.getSubject());
		this.currentVersion = this.getVersions().last();
	}
}
