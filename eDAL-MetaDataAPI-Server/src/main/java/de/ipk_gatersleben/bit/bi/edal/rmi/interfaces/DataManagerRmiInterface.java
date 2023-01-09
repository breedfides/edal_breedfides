/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;

/**
 * RMI Interface for
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager} objects.
 * 
 * @author arendd
 */
public interface DataManagerRmiInterface extends Remote {

	/**
	 * authenticate a user and return on success a {@link Subject}
	 * 
	 * @param authentication
	 *            {@link de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication}
	 * @return the authenticated {@link Subject}
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalAuthenticateException
	 *             if unable to authenticate user
	 */
	Subject authenticate(Authentication authentication) throws RemoteException, EdalAuthenticateException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider#shutdown()}
	 * 
	 * @return a new {@link MetaData} object.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider#createMetaDataInstance()
	 */
	MetaData createMetaDataInstance() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getAvailableStorageSpace()}
	 * 
	 * @return available space
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if no mount path is set.
	 * 
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getAvailableStorageSpace()
	 */
	Long getAvailableStorageSpace() throws RemoteException, EdalException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getDefaultPermissions()}
	 * 
	 * @return map with all default permissions.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getDefaultPermissions()
	 */
	Map<Principal, List<Methods>> getDefaultPermissions() throws RemoteException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getRootDirectory(de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider, Subject)}
	 * 
	 * @param subject
	 *            the authenticated subject
	 * @return the root
	 *         {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory}
	 *         for the passed implementation
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws PrimaryDataDirectoryException
	 *             if any.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getRootDirectory(de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider,
	 *      Subject)
	 */
	PrimaryDataDirectoryRmiInterface getRootDirectory(final Subject subject)
			throws RemoteException, PrimaryDataDirectoryException;

	/**
	 * Get all supported {@link Principal}s of the current eDAL system.
	 * 
	 * @return the list of supported {@link Principal}s
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if unable to load {@link Principal}s.
	 */
	List<Class<? extends Principal>> getSupportedPrincipals() throws RemoteException, EdalException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getUsedStorageSpace()}
	 * 
	 * @return used space
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @throws EdalException
	 *             if no mount path is set.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager#getAvailableStorageSpace()
	 */
	Long getUsedStorageSpace() throws RemoteException, EdalException;

	void receiveTestData(RemoteOutputStream outputStream) throws RemoteException, IOException;

	/**
	 * Request the server to send a e-mail to an authenticated subject
	 * 
	 * @param authentication
	 *            the user authentication
	 * @param emailMessage
	 *            the content of the e-mail
	 * @param emailSubject
	 *            the subject of the e-mail
	 * 
	 * @throws RemoteException
	 *             if unable to send eMail
	 */
	void sendEmail(Authentication authentication, String emailMessage, String emailSubject) throws RemoteException;

	/**
	 * Request the server to send a e-mail with attachment to an authenticated
	 * subject
	 * 
	 * @param authentication
	 *            the user authentication
	 * @param emailMessage
	 *            the content of the e-mail
	 * @param emailSubject
	 *            the subject of the e-mail
	 * @param attachment
	 *            the attached {@link URL}
	 * @throws RemoteException
	 *             if unable to send eMail
	 */
	void sendEmail(Authentication authentication, String emailMessage, String emailSubject, URL attachment)
			throws RemoteException;

	RemoteInputStream sendFileToClient(String fileName) throws RemoteException, FileNotFoundException, IOException;

	RemoteOutputStream sendOutputStreamToFillFromClient(String fileName) throws RemoteException, FileNotFoundException;

	/**
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider#shutdown()}
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 * @see de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider#shutdown()
	 */
	void shutdown() throws RemoteException;

}