/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package test;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;

/**
 * {@link RMIClientSocketFactory} for RMI Client without SSL and a constant
 * port.
 * 
 * @deprecated
 * @author arendd
 */
public class EdalRmiClientSocketFactory implements Serializable,
		RMIClientSocketFactory {

	private static final long serialVersionUID = 6343160730303196078L;

	/** the default RMI socket port */
	private static final int DEFAULT_RMI_SOCKET_PORT = 1098;

	/** the fixed RMI data port to be used */
	private int rmiDataPort;

	/** Default constructor use default socket port */
	public EdalRmiClientSocketFactory() {
		this.rmiDataPort = DEFAULT_RMI_SOCKET_PORT;
	}

	/**
	 * Constructor use the defined port to create a socket.
	 * 
	 * @param port
	 *            the port for creating sockets.
	 */
	public EdalRmiClientSocketFactory(int port) {
		this.rmiDataPort = port;
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		if (port == 0) {
			return RMISocketFactory.getDefaultSocketFactory().createSocket(host, this.rmiDataPort);
		} else {
			return RMISocketFactory.getDefaultSocketFactory().createSocket(host, port);
		}
	}
}