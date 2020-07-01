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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.security.auth.Subject;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;

/**
 * RMI Interface for
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile}
 * objects.
 * 
 * @author arendd
 */
public interface PrimaryDataFileRmiInterface extends PrimaryDataEntityRmiInterface {

//	/**
//	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile#read(OutputStream)}
//	 * 
//	 * @param dataOutputStream
//	 *            the loaded data.
//	 * @param subject
//	 *            the authenticated subject for check permission.
//	 * @throws RemoteException
//	 *             if unable to call remote function.
//	 * @throws PrimaryDataFileException
//	 *             if no data is stored.
//	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile#read(OutputStream)
//	 */
//	void read(RemoteOutputStream dataOutputStream, long versionNumber, Subject subject) throws RemoteException, PrimaryDataFileException;

	/** {@inheritDoc} */
	void setMetaData(MetaData metadata, Subject subject) throws RemoteException, PrimaryDataEntityVersionException, MetaDataException;

//	/**
//	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile#store(InputStream)}
//	 * 
//	 * @param dataInputStream
//	 *            the date to store in this
//	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion}
//	 *            .
//	 * @throws PrimaryDataFileException
//	 *             if storing of data fails.
//	 * @throws PrimaryDataEntityVersionException
//	 *             if provided version conflicts with existing versions.
//	 * @throws RemoteException
//	 *             if unable to call remote function.
//	 */
//	void store(RemoteInputStream dataInputStream, Subject subject) throws RemoteException, PrimaryDataFileException, PrimaryDataEntityVersionException;

	RemoteInputStream sendFileToClient(long versionNumber, Subject subject) throws RemoteException, FileNotFoundException, IOException;

	RemoteOutputStream sendOutputStreamToFillFromClient(Subject subject) throws RemoteException, PrimaryDataFileException, PrimaryDataEntityVersionException;
	
}