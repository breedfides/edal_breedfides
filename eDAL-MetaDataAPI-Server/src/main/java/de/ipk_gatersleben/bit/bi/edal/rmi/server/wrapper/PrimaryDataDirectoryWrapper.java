/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.server.wrapper;

import java.rmi.RemoteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
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
 * Wrapper class to wrap {@link PrimaryDataDirectory} functions on server side.
 * 
 * @author arendd
 */
public class PrimaryDataDirectoryWrapper extends PrimaryDataEntityWrapper implements PrimaryDataDirectoryRmiInterface {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for {@link PrimaryDataDirectoryWrapper}.
	 * 
	 * @param dataPort
	 *            the data port for the remote objects.
	 * @param directory
	 *            a {@link PrimaryDataDirectory} object.
	 * @throws RemoteException
	 *             is unable to call remote function.
	 */
	public PrimaryDataDirectoryWrapper(int dataPort, PrimaryDataDirectory directory) throws RemoteException {
		super(dataPort, directory);
	}

	/** {@inheritDoc} */
	public PrimaryDataDirectoryRmiInterface createPrimaryDataDirectory(String path, Subject subject,
			Map<Principal, List<Methods>> defaultPermissions) throws PrimaryDataDirectoryException, RemoteException {

		DataManager.setSubject(subject);
		DataManager.setDefaultPermissions(defaultPermissions);

		return (PrimaryDataDirectoryRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
				((PrimaryDataDirectory) this.myEntity).createPrimaryDataDirectory(path));

	}

	/** {@inheritDoc} */
	public PrimaryDataFileRmiInterface createPrimaryDataFile(String name, Subject subject,
			Map<Principal, List<Methods>> defaultPermissions) throws PrimaryDataDirectoryException, RemoteException {

		DataManager.setSubject(subject);
		DataManager.setDefaultPermissions(defaultPermissions);

		return (PrimaryDataFileRmiInterface) new PrimaryDataFileWrapper(myDataPort,

				((PrimaryDataDirectory) this.myEntity).createPrimaryDataFile(name));

	}

	/** {@inheritDoc} */
	public boolean exist(String path, Subject subject) throws RemoteException, PrimaryDataDirectoryException {

		DataManager.setSubject(subject);

		return ((PrimaryDataDirectory) myEntity).exist(path);
	}

	/** {@inheritDoc} */
	public List<PrimaryDataEntityRmiInterface> listPrimaryDataEntities(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException {

		DataManager.setSubject(subject);

		List<PrimaryDataEntity> myList = ((PrimaryDataDirectory) myEntity).listPrimaryDataEntities();

		List<PrimaryDataEntityRmiInterface> rmiList = new ArrayList<PrimaryDataEntityRmiInterface>(myList.size());

		for (PrimaryDataEntity primaryDataEntity : myList) {
			if (primaryDataEntity.isDirectory()) {
				rmiList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				rmiList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return rmiList;
	}

	/** {@inheritDoc} */
	public void setMetaData(MetaData metadata, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {

		DataManager.setSubject(subject);
		((PrimaryDataDirectory) myEntity).setMetaData(metadata);
	}

	/** {@inheritDoc} */
	public List<PrimaryDataEntityRmiInterface> searchByDublinCoreElement(EnumDublinCoreElements element,
			UntypedData data, boolean fuzzy, boolean recursiveIntoSubdirectories, Subject subject)
					throws RemoteException, PrimaryDataDirectoryException {

		DataManager.setSubject(subject);

		List<PrimaryDataEntity> internList = ((PrimaryDataDirectory) myEntity).searchByDublinCoreElement(element, data,
				fuzzy, recursiveIntoSubdirectories);

		List<PrimaryDataEntityRmiInterface> externList = new ArrayList<PrimaryDataEntityRmiInterface>(
				internList.size());

		for (PrimaryDataEntity primaryDataEntity : internList) {
			if (primaryDataEntity.isDirectory()) {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return externList;
	}

	/** {@inheritDoc} */
	public List<PrimaryDataEntityRmiInterface> searchByMetaData(MetaData query, boolean fuzzy,
			boolean recursiveIntoSubdirectories, Subject subject)
					throws RemoteException, PrimaryDataDirectoryException, MetaDataException {

		DataManager.setSubject(subject);

		List<PrimaryDataEntity> internList = ((PrimaryDataDirectory) myEntity).searchByMetaData(query, fuzzy,
				recursiveIntoSubdirectories);

		List<PrimaryDataEntityRmiInterface> externList = new ArrayList<PrimaryDataEntityRmiInterface>(
				internList.size());

		for (PrimaryDataEntity primaryDataEntity : internList) {
			if (primaryDataEntity.isDirectory()) {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return externList;
	}

	/** {@inheritDoc} */
	public PrimaryDataEntityRmiInterface getPrimaryDataEntity(String name, Subject subject)
			throws PrimaryDataDirectoryException, RemoteException {
		DataManager.setSubject(subject);
		PrimaryDataEntity entity = ((PrimaryDataDirectory) myEntity).getPrimaryDataEntity(name);
		if (entity.isDirectory()) {
			return (PrimaryDataDirectoryRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
					(PrimaryDataDirectory) entity);
		} else {
			return (PrimaryDataFileRmiInterface) new PrimaryDataFileWrapper(myDataPort, (PrimaryDataFile) entity);
		}
	}

	/** {@inheritDoc} */
	public List<PublicReference> getPublicReferences(Subject subject) throws RemoteException {
		DataManager.setSubject(subject);

		return ((PrimaryDataDirectory) myEntity).getPublicReferences();
	}

	/** {@inheritDoc} */
	@Override
	public List<PrimaryDataEntityRmiInterface> searchByPublicationStatus(PublicationStatus publicationStatus,
			Subject subject) throws PrimaryDataDirectoryException, RemoteException {

		DataManager.setSubject(subject);

		List<PrimaryDataEntity> internList = ((PrimaryDataDirectory) myEntity)
				.searchByPublicationStatus(publicationStatus);

		List<PrimaryDataEntityRmiInterface> externList = new ArrayList<PrimaryDataEntityRmiInterface>(
				internList.size());

		for (PrimaryDataEntity primaryDataEntity : internList) {
			if (primaryDataEntity.isDirectory()) {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return externList;
	}

	/** {@inheritDoc} */
	@Override
	public List<PrimaryDataEntityRmiInterface> getAllPublishedEntities(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException {
		DataManager.setSubject(subject);

		List<PrimaryDataEntity> internList = ((PrimaryDataDirectory) myEntity).getAllPublishedEntities();

		List<PrimaryDataEntityRmiInterface> externList = new ArrayList<PrimaryDataEntityRmiInterface>(
				internList.size());

		for (PrimaryDataEntity primaryDataEntity : internList) {
			if (primaryDataEntity.isDirectory()) {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return externList;
	}

	/** {@inheritDoc} */
	@Override
	public List<PrimaryDataEntityRmiInterface> searchByKeyword(String keyword, boolean fuzzy,
			boolean recursiveIntoSubdirectories, Subject subject)
					throws PrimaryDataDirectoryException, RemoteException {
		DataManager.setSubject(subject);

		List<PrimaryDataEntity> internList = ((PrimaryDataDirectory) myEntity).searchByKeyword(keyword, fuzzy,
				recursiveIntoSubdirectories);

		List<PrimaryDataEntityRmiInterface> externList = new ArrayList<PrimaryDataEntityRmiInterface>(
				internList.size());

		for (PrimaryDataEntity primaryDataEntity : internList) {
			if (primaryDataEntity.isDirectory()) {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
						(PrimaryDataDirectory) primaryDataEntity));
			} else {
				externList.add((PrimaryDataEntityRmiInterface) new PrimaryDataFileWrapper(myDataPort,
						(PrimaryDataFile) primaryDataEntity));
			}
		}
		return externList;
	}
}