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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.util.List;

import javax.security.auth.Subject;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteOutputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataFileRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;

/**
 * Wrapper class to wrap {@link PrimaryDataFile} functions on server side.
 * 
 * @author arendd
 */
public class PrimaryDataFileWrapper extends PrimaryDataEntityWrapper implements PrimaryDataFileRmiInterface {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for {@link PrimaryDataFileWrapper}.
	 * 
	 * @param dataPort
	 *            the data port for the remote objects.
	 * @param file
	 *            a {@link PrimaryDataFile} object.
	 * @throws RemoteException
	 *             is unable to call remote function.
	 */
	public PrimaryDataFileWrapper(int dataPort, PrimaryDataFile file) throws RemoteException {
		super(dataPort, file);
	}

	/** {@inheritDoc} */
	public void setMetaData(MetaData metadata, Subject subject)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {

		DataManager.setSubject(subject);

		((PrimaryDataFile) myEntity).setMetaData(metadata);
	}

	/** {@inheritDoc} */
	public List<PublicReference> getPublicReferences(Subject subject) throws RemoteException {
		DataManager.setSubject(subject);

		return ((PrimaryDataFile) myEntity).getPublicReferences();
	}

	@SuppressWarnings("resource")
	@Override
	public RemoteInputStream sendFileToClient(long versionNumber, Subject subject)
			throws RemoteException, FileNotFoundException, IOException {

		DataManager.setSubject(subject);

		PrimaryDataFile file = ((PrimaryDataFile) myEntity);

		for (PrimaryDataEntityVersion version : file.getVersions()) {
			if (version.getRevision() == versionNumber) {
				try {
					file.switchCurrentVersion(version);
				} catch (PrimaryDataEntityVersionException e) {
					EdalServer.getLogger().error(e.getMessage());
				}
			}
		}

		PipedInputStream pin = new PipedInputStream();

		PipedOutputStream pout = new PipedOutputStream(pin);

		/** original version without 'try-with-resource' **/
		RemoteInputStreamServer istream = null;
		try {

			istream = new GZIPRemoteInputStream(new BufferedInputStream(pin, EdalConfiguration.STREAM_BUFFER_SIZE),SimpleRemoteInputStream.DUMMY_MONITOR,EdalConfiguration.STREAM_BUFFER_SIZE);
			
			StreamOutputToInputThread thread = new StreamOutputToInputThread(file, pout);

			thread.start();

			// export the final stream for returning to the client
			RemoteInputStream result = istream.export();
			istream = null;
			return result;
		} finally {
			// we will only close the stream here if the server fails before
			// returning an exported stream
			if (istream != null)
				istream.close();
		}

		/** new version with 'try-with-resource' construct **/
		/** not working right now **/
		// try (RemoteInputStreamServer istream = new
		// SimpleRemoteInputStream(new BufferedInputStream(pin));) {
		//
		// StreamOutputToInputThread thread = new
		// StreamOutputToInputThread(file, pout);
		//
		// thread.start();
		//
		// RemoteInputStream result = istream.export();
		//
		// return result;
		// }

	}

	@SuppressWarnings("resource")
	public RemoteOutputStream sendOutputStreamToFillFromClient(Subject subject)
			throws RemoteException, PrimaryDataFileException, PrimaryDataEntityVersionException {

		DataManager.setSubject(subject);

		PrimaryDataFile file = ((PrimaryDataFile) myEntity);

		PipedOutputStream pout = new PipedOutputStream();

		PipedInputStream pin = null;
		try {
			pin = new PipedInputStream(pout);
		} catch (IOException e) {
			e.printStackTrace();
		}

		StreamInputToOutputThread thread = new StreamInputToOutputThread(file, pin);

		thread.start();

		/** original version without 'try-with-resource' **/
		RemoteOutputStreamServer ostream = null;
		try {
			ostream = new SimpleRemoteOutputStream(new BufferedOutputStream(pout, EdalConfiguration.STREAM_BUFFER_SIZE));
			// export the final stream for returning to the client
			RemoteOutputStream result = ostream.export();
			ostream = null;
			return result;
		} finally {
			// we will only close the stream here if the server fails before
			// returning an exported stream
			if (ostream != null) {
				ostream.close();
			}

		}

		/** new version with 'try-with-resource' construct **/
		/** not working right now **/
		// try (RemoteOutputStreamServer ostream = new
		// SimpleRemoteOutputStream(new BufferedOutputStream(pout))) {
		//
		// RemoteOutputStream result = ostream.export();
		//
		// return result;
		// }
	}
}