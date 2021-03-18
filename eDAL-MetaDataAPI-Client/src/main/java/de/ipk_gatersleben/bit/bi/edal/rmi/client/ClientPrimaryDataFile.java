/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataFileRmiInterface;

/**
 * SampleClient version of {@link PrimaryDataFile} to use eDAL system like the
 * stand-alone implementation.
 * 
 * @author arendd
 */
public class ClientPrimaryDataFile extends ClientPrimaryDataEntity {

	/**
	 * Constructor for ClientPrimaryDataFile.
	 * 
	 * @param file
	 *            a {@link PrimaryDataFileRmiInterface} object.
	 * @param client
	 *            the {@link ClientDataManager}
	 */
	public ClientPrimaryDataFile(final PrimaryDataFileRmiInterface file, final ClientDataManager client) {
		super(file, client);
	}

	// /**
	// * {@link PrimaryDataFile#read(OutputStream)}
	// *
	// * @param dataOutputStream
	// * the loaded data.
	// * @throws PrimaryDataFileException
	// * if no data is stored.
	// * @see PrimaryDataFileRmiInterface#read(RemoteOutputStream, Subject)
	// * @throws RemoteException
	// * if unable to call remote function.
	// */
	// public void read(OutputStream dataOutputStream) throws RemoteException,
	// PrimaryDataFileException {
	//
	// /** Java 7 version */
	//
	// DefaultRemoteStreamExporter exporter = new
	// DefaultRemoteStreamExporter(clientDataManager.getDataPort());
	//
	// try (RemoteOutputStreamServer ostream = new
	// SimpleRemoteOutputStream(dataOutputStream)) {
	// ((PrimaryDataFileRmiInterface)
	// myEntityRMI).read(exporter.export(ostream),
	// this.getCurrentVersion().getRevision(), clientDataManager.getSubject());
	// }
	//
	// }

	public void read(final OutputStream outputStream) throws RemoteException, PrimaryDataFileException {

		try {
			final InputStream inputStream = RemoteInputStreamClient
					.wrap(((PrimaryDataFileRmiInterface) this.myEntityRMI).sendFileToClient(
							this.getCurrentVersion().getRevision(), this.clientDataManager.getSubject()));

			byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];

			for (int length; (length = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.close();

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	// /**
	// * {@link PrimaryDataFile#store(InputStream)}
	// *
	// * @param dataInputStream
	// * the date to store in this {@link PrimaryDataEntityVersion}.
	// * @throws PrimaryDataFileException
	// * if storing of data fails.
	// * @throws PrimaryDataEntityVersionException
	// * if provided version conflicts with existing versions.
	// * @see PrimaryDataFileRmiInterface#store(RemoteInputStream, Subject)
	// * @throws RemoteException
	// * if unable to call remote function.
	// */
	// public void store(InputStream dataInputStream) throws RemoteException,
	// PrimaryDataFileException, PrimaryDataEntityVersionException {
	//
	// if (dataInputStream == null) {
	// throw new
	// PrimaryDataFileException("can not store inputstream: stream is null ");
	// }
	//
	// /** Java 7 Version */
	//
	// DefaultRemoteStreamExporter exporter = new
	// DefaultRemoteStreamExporter(clientDataManager.getDataPort());
	//
	// try (RemoteInputStreamServer istream = new
	// SimpleRemoteInputStream(dataInputStream)) {
	//
	// ((PrimaryDataFileRmiInterface)
	// myEntityRMI).store(exporter.export(istream),
	// clientDataManager.getSubject());
	// }
	//
	// /** Java 6 Version */
	// // RemoteInputStreamServer istream = new SimpleRemoteInputStream(
	// // dataInputStream);
	// // ((PrimaryDataFileRmiInterface) myEntityRMI).store(istream.export(),
	// // clientDataManager.getSubject());
	// //
	// // istream.close();
	//
	// this.currentVersion = this.getVersions().last();
	//
	// }

	/** {@inheritDoc} */
	@Override
	public void setMetaData(final MetaData new_metadata)
			throws RemoteException, PrimaryDataEntityVersionException, MetaDataException {
		((PrimaryDataFileRmiInterface) this.myEntityRMI).setMetaData(new_metadata, this.clientDataManager.getSubject());
		this.currentVersion = this.getVersions().last();
	}

	public void store(final InputStream inputStream)
			throws RemoteException, PrimaryDataFileException, PrimaryDataEntityVersionException {
		final ClientPrimaryDataEntityVersion version = this.getCurrentVersion();
		try {
			final OutputStream outputStream = RemoteOutputStreamClient
					.wrap(((PrimaryDataFileRmiInterface) this.myEntityRMI)
							.sendOutputStreamToFillFromClient(this.clientDataManager.getSubject()));

			byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];

			for (int length; (length = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.close();

			while (version.getRevision() == this.getVersions().size() - 1) {

				// System.out.println("WAIT WAIT WAIT");
				try {
					Thread.sleep(0, 1);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (final IOException e) {
			throw new PrimaryDataFileException(e);
			// e.printStackTrace();
		}
	}

}