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
package de.ipk_gatersleben.bit.bi.edal.rmi.server.wrapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.GZIPRemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;
import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.DataManagerRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.interfaces.PrimaryDataDirectoryRmiInterface;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;

/**
 * Wrapper class to wrap {@link DataManager} functions on server side.
 * 
 * @author arendd
 */
public class DataManagerWrapper extends UnicastRemoteObject implements DataManagerRmiInterface {

	private static final long serialVersionUID = 1L;

	private ImplementationProvider implementationProvider;

	private int dataPort;

	/**
	 * @return the dataPort
	 */
	private int getDataPort() {
		return dataPort;
	}

	/**
	 * @param dataPort
	 *            the dataPort to set
	 */
	private void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}

	/**
	 * Constructor for {@link DataManagerWrapper}.
	 * 
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public DataManagerWrapper() throws RemoteException {
		super();
	}

	/**
	 * Constructor for DataManagerWrapper.
	 * 
	 * @param dataPort
	 *            the data port.
	 * 
	 * @param implementationProvider
	 *            a {@link ImplementationProvider} object.
	 * @throws RemoteException
	 *             if unable to call remote function.
	 */
	public DataManagerWrapper(final ImplementationProvider implementationProvider, int dataPort)
			throws RemoteException {
		super(dataPort);
		this.implementationProvider = implementationProvider;
		this.setDataPort(dataPort);

	}

	/** {@inheritDoc} */
	@Override
	public PrimaryDataDirectoryRmiInterface getRootDirectory(final Subject subject)
			throws RemoteException, PrimaryDataDirectoryException {

		return (PrimaryDataDirectoryRmiInterface) new PrimaryDataDirectoryWrapper(this.getDataPort(),
				DataManager.getRootDirectory(this.implementationProvider, subject));

	}

	/** {@inheritDoc} */
	@Override
	public MetaData createMetaDataInstance() throws RemoteException {
		return this.implementationProvider.createMetaDataInstance();
	}

	/** {@inheritDoc} */
	@Override
	public void shutdown() throws RemoteException {
		DataManager.shutdown();
	}

	/** {@inheritDoc} */
	@Override
	public Map<Principal, List<Methods>> getDefaultPermissions() throws RemoteException {
		return DataManager.getDefaultPermissions();
	}

	/** {@inheritDoc} */
	@Override
	public Long getAvailableStorageSpace() throws RemoteException, EdalException {
		return DataManager.getAvailableStorageSpace();
	}

	/** {@inheritDoc} */
	@Override
	public Long getUsedStorageSpace() throws RemoteException, EdalException {
		return DataManager.getUsedStorageSpace();
	}

	/** {@inheritDoc} */
	@Override
	public List<Class<? extends Principal>> getSupportedPrincipals() throws RemoteException, EdalException {
		return DataManager.getSupportedPrincipals();
	}

	/** {@inheritDoc} */
	@Override
	public Subject authenticate(Authentication authentication) throws RemoteException, EdalAuthenticateException {
		return authentication.getSubject();
	}

	/** {@inheritDoc} */
	@Override
	public void receiveTestData(RemoteOutputStream outputStream) throws RemoteException, IOException {
		DataManager.receiveTestData(RemoteOutputStreamClient.wrap(outputStream));
	}

	/** {@inheritDoc} */
	@Override
	public RemoteInputStream sendFileToClient(String fileName)
			throws RemoteException, FileNotFoundException, IOException {
		RemoteInputStreamServer istream = null;
		try {
			istream = new GZIPRemoteInputStream(new BufferedInputStream(new ByteArrayInputStream(fileName.getBytes())));
			// export stream for returning to client
			RemoteInputStream result = istream.export();
			istream = null;
			return result;
		} finally {
			// only close stream if server fails before
			// returning an exported stream
			if (istream != null)
				istream.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public RemoteOutputStream sendOutputStreamToFillFromClient(String fileName)
			throws RemoteException, FileNotFoundException {
		RemoteOutputStreamServer ostream = null;
		try {
			ostream = new GZIPRemoteOutputStream(new BufferedOutputStream(new ByteArrayOutputStream()));
			// export stream for returning to client
			RemoteOutputStream result = ostream.export();
			ostream = null;
			return result;
		} finally {
			// only close stream if server fails before
			// returning an exported stream
			if (ostream != null)
				ostream.close();
		}
	}

	@Override
	public void sendEmail(Authentication authentication, String emailMessage, String emailSubject)
			throws RemoteException {
		DataManager.sendEmail(emailMessage, emailSubject, authentication.getName());
	}

	@Override
	public void sendEmail(Authentication authentication, String emailMessage, String emailSubject, URL attachment)
			throws RemoteException {
		DataManager.sendEmail(emailMessage, emailSubject, authentication.getName(), attachment);

	}

}