/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
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