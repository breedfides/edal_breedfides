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
import java.rmi.server.UnicastRemoteObject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityVersionRmiInterface;

/**
 * Wrapper class to wrap {@link PrimaryDataEntity} functions on server side.
 * 
 * @author arendd
 */
public abstract class PrimaryDataEntityWrapper extends UnicastRemoteObject implements PrimaryDataEntityRmiInterface {

	private static final long serialVersionUID = 1L;

	public PrimaryDataEntity myEntity;

	public int myDataPort;

	/**
	 * Constructor for {@link PrimaryDataEntityWrapper}.
	 * 
	 * @param dataPort
	 *            the data port for the remote objects.
	 * @param entity
	 *            a {@link PrimaryDataEntity} object.
	 * @throws RemoteException
	 *             is unable to call remote function.
	 */
	public PrimaryDataEntityWrapper(int dataPort, PrimaryDataEntity entity) throws RemoteException {
		super(dataPort);
		myEntity = entity;
		myDataPort = dataPort;
	}

	/** {@inheritDoc} */
	@Override
	public void delete(Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException {

		DataManager.setSubject(subject);

		myEntity.delete();
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataEntityVersionRmiInterface getCurrentVersion(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return (PrimaryDataEntityVersionRmiInterface) new PrimaryDataEntityVersionWrapper(myDataPort,
				myEntity.getCurrentVersion());

	}

	private PrimaryDataDirectory getDirectoryByID(String uuid) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		Transaction transaction = session.beginTransaction();

		PrimaryDataDirectoryImplementation directory = (PrimaryDataDirectoryImplementation) session
				.get(PrimaryDataDirectoryImplementation.class, uuid);

		transaction.commit();
		session.close();
		return directory;

	}

	/** {@inheritDoc} */
	@Override
	public String getID(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return myEntity.getID();
	}

	/** {@inheritDoc} */
	@Override
	public MetaData getMetaData(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return myEntity.getMetaData();
	}

	/** {@inheritDoc} */
	@Override
	public String getName(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return myEntity.getName();
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataDirectoryRmiInterface getParentDirectory(Subject subject)
			throws PrimaryDataDirectoryException, RemoteException {

		DataManager.setSubject(subject);

		return (PrimaryDataDirectoryRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
				myEntity.getParentDirectory());
	}

	/** {@inheritDoc} */
	@Override
	public Map<Principal, List<Methods>> getPermissions(Subject subject)
			throws PrimaryDataEntityException, RemoteException {

		DataManager.setSubject(subject);

		Map<Principal, List<EdalPermission>> privateMap = myEntity.getPermissions();

		Map<Principal, List<Methods>> publicMap = new HashMap<Principal, List<Methods>>(privateMap.size());

		for (Map.Entry<Principal, List<EdalPermission>> entry : privateMap.entrySet()) {

			List<Methods> tmpList = new ArrayList<Methods>();

			for (EdalPermission perm : entry.getValue()) {
				tmpList.add(Methods.valueOf(perm.getActionMethod().getName()));
			}

			publicMap.put(entry.getKey(), tmpList);
		}

		return publicMap;

	}

	/** {@inheritDoc} */
	@Override
	public String getPath(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return myEntity.getPath();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<PrimaryDataEntityVersionRmiInterface> getVersions(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		Collection<PrimaryDataEntityVersionRmiInterface> externalSet = new ArrayList<>();

		for (PrimaryDataEntityVersion primaryDataEntityVersion : myEntity.getVersions()) {

			externalSet.add((PrimaryDataEntityVersionRmiInterface) new PrimaryDataEntityVersionWrapper(myDataPort,
					primaryDataEntityVersion));
		}
		return externalSet;

	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataEntityVersionRmiInterface getVersionByDate(Calendar date, Subject subject)
			throws PrimaryDataEntityVersionException, RemoteException {

		DataManager.setSubject(subject);

		return (PrimaryDataEntityVersionRmiInterface) new PrimaryDataEntityVersionWrapper(myDataPort,
				myEntity.getVersionByDate(date));
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataEntityVersionRmiInterface getVersionByRevisionNumber(long revisionNumber, Subject subject)
			throws PrimaryDataEntityVersionException, RemoteException {

		DataManager.setSubject(subject);

		return (PrimaryDataEntityVersionRmiInterface) new PrimaryDataEntityVersionWrapper(myDataPort,
				myEntity.getVersionByRevisionNumber(revisionNumber));

	}

	/** {@inheritDoc} */
	@Override
	public void grantPermission(Principal principal, Methods method, Subject subject)
			throws RemoteException, PrimaryDataEntityException {

		DataManager.setSubject(subject);

		myEntity.grantPermission(principal, method);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDirectory(Subject subject) throws RemoteException {

		DataManager.setSubject(subject);

		return myEntity.isDirectory();
	}

	/** {@inheritDoc} */
	@Override
	public void move(PrimaryDataDirectoryRmiInterface destinationDirectory, Subject subject)
			throws RemoteException, PrimaryDataDirectoryException {

		DataManager.setSubject(subject);

		myEntity.move(getDirectoryByID(destinationDirectory.getID(subject)));
	}

	/** {@inheritDoc} */
	public void rename(String name, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException {

		DataManager.setSubject(subject);

		myEntity.rename(name);
	}

	/** {@inheritDoc} */
	public void revokePermission(Principal principal, Methods method, Subject subject)
			throws RemoteException, PrimaryDataEntityException {

		DataManager.setSubject(subject);

		myEntity.revokePermission(principal, method);
	}

	/** {@inheritDoc} */
	public void setMetaData(MetaData metadata, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {

		DataManager.setSubject(subject);

		myEntity.setMetaData(metadata);
	}

	/** {@inheritDoc} */
	public void addPublicReference(PersistentIdentifier identifierType, Subject subject)
			throws RemoteException, PrimaryDataEntityException {
		DataManager.setSubject(subject);

		myEntity.addPublicReference(identifierType);

	}
}