/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataFileRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataEntityVersionRmiInterface;

/**
 * Wrapper class to wrap {@link PrimaryDataEntityVersion} functions on server
 * side.
 * 
 * @author arendd
 */
public class PrimaryDataEntityVersionWrapper extends UnicastRemoteObject
		implements PrimaryDataEntityVersionRmiInterface {

	private static final long serialVersionUID = 1L;

	public PrimaryDataEntityVersion myVersion;

	public int myDataPort;

	/**
	 * Constructor for {@link PrimaryDataEntityVersionWrapper}using no SSL.
	 * 
	 * @param dataPort
	 *            the data port for the remote objects.
	 * @param version
	 *            a {@link PrimaryDataEntityVersionWrapper} object.
	 * @throws RemoteException
	 *             is unable to call remote function.
	 */
	protected PrimaryDataEntityVersionWrapper(int dataPort, PrimaryDataEntityVersion version) throws RemoteException {
		super(dataPort);
		myVersion = version;
		myDataPort = dataPort;
	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataEntityRmiInterface getEntity() throws RemoteException {

		if (myVersion.getEntity().isDirectory()) {
			return (PrimaryDataDirectoryRmiInterface) new PrimaryDataDirectoryWrapper(myDataPort,
					((PrimaryDataDirectory) myVersion.getEntity()));
		} else {
			return (PrimaryDataFileRmiInterface) new PrimaryDataFileWrapper(myDataPort,
					((PrimaryDataFile) myVersion.getEntity()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public MetaData getMetaData() throws RemoteException {
		return this.myVersion.getMetaData();
	}

	/** {@inheritDoc} */
	@Override
	public Long getRevision() throws RemoteException {
		return this.myVersion.getRevision();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDeleted() throws RemoteException {
		return this.myVersion.isDeleted();
	}

	/** {@inheritDoc} */
	@Override
	public Calendar getRevisionDate() throws RemoteException {
		return this.myVersion.getRevisionDate();
	}

	/** {@inheritDoc} */
	@Override
	public Calendar getCreationDate() throws RemoteException {
		return this.myVersion.getCreationDate();

	}

	/** {@inheritDoc} */
	@Override
	public void setAllReferencesPublic(InternetAddress emailNotificationAddress, Calendar releaseDate)
			throws PublicReferenceException, RemoteException {
		this.myVersion.setAllReferencesPublic(emailNotificationAddress, releaseDate);
	}

	/** {@inheritDoc} */
	@Override
	public List<PublicReference> getPublicReferences() throws RemoteException {
		return this.myVersion.getPublicReferences();
	}

	/** {@inheritDoc} */
	@Override
	public PublicReference getPublicReference(PersistentIdentifier identifierType)
			throws RemoteException, PrimaryDataEntityVersionException {
		return this.myVersion.getPublicReference(identifierType);
	}

	@Override
	public Principal getOwner() throws RemoteException {
		return this.myVersion.getOwner();
	}
}
