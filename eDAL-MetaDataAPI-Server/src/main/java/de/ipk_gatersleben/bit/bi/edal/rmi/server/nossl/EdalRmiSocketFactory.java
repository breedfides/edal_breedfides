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
package de.ipk_gatersleben.bit.bi.edal.rmi.server.nossl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * eDAL specific RMISocketFactory to define a constant data port.
 * 
 * @author arendd
 */
public class EdalRmiSocketFactory extends RMISocketFactory {

	private static final int DEFAULT_RMI_SOCKET_PORT = 1098;

	/**
	 * the fixed RMI data port to be used
	 */
	private int rmiDataPort;

	/**
	 * construct a new FixedPortRMISocketFactory with standard port 1098
	 */
	public EdalRmiSocketFactory() {
		this(DEFAULT_RMI_SOCKET_PORT);
	}

	/**
	 * construct a new FixedPortRMISocketFactory
	 * 
	 * @param port
	 *            the fixed RMI data port to be used
	 */
	public EdalRmiSocketFactory(int port) {
		super();
		rmiDataPort = port;
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	/** {@inheritDoc} */
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {

		if (port == 0) {
			return new ServerSocket(rmiDataPort);
		} else {
			return new ServerSocket(port);
		}
	}
}