/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.sample.gui;

import javax.security.auth.Subject;
import javax.swing.JFrame;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class StartGUI {

	private static final int SERVER_PORT = 1099;
	private static final String SERVER_ADDRESS = "localhost";

	public static void main(final String[] args) throws Exception {

		/** use windows or unix login **/
		Subject subject = EdalHelpers.authenticateWinOrUnixOrMacUser();

		/** alternatively use Google+ login **/
		// Subject subject = EdalHelpers.authenticateGoogleUser("", 3128);

		/** connect to running EDAL server on "localhost" **/
		ClientDataManager dataManagerClient = new ClientDataManager(SERVER_ADDRESS, SERVER_PORT, new Authentication(subject));

		EdalFileChooser dialog = new EdalFileChooser(new JFrame(), dataManagerClient);

		dialog.setFileSelectionMode(EdalFileChooser.FILES_AND_DIRECTORIES);
		dialog.showConnectionButton(false);

		dialog.showOpenDialog();

	}

}
