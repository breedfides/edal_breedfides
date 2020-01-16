/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client;

/**
 * Class to provide current client context to server.
 * 
 * @author arendd
 */
public abstract class ClientContext {

	protected ClientDataManager clientDataManager;

	/**
	 * Default constructor set current {@link ClientDataManager}.
	 * 
	 * @param client
	 *            the current {@link ClientDataManager} for this client context.
	 */
	protected ClientContext(ClientDataManager client) {
		this.clientDataManager = client;

	}

}
