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
