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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.rmi.ConnectException;

import javax.security.auth.Subject;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class PublicationModulMain {

	private static final String SERVER = "localhost";

	private static final int REGISTRY_PORT = EdalServer.DEFAULT_REGISTRY_PORT;

	private static PublicationFrame frame;

	public static ClientDataManager clientDataManager;

	public static PublicationFrame getFrame() {
		return PublicationModulMain.frame;
	}

	private static void setCurrentFrame(PublicationFrame frame) {
		PublicationModulMain.frame = frame;
	}

	public static void main(String[] args) throws Exception {

		/* test connect to eDAL server */
		PropertyLoader.initialize("ipk_properties.txt");

		Boolean retry = true;

		while (retry) {
			try {
				Authentication testAuthentication = new Authentication(EdalHelpers.authenticateSampleUser());
				clientDataManager = new ClientDataManager(SERVER, REGISTRY_PORT, testAuthentication);
				clientDataManager.getRootDirectory();

				retry = false;
			} catch (ConnectException e) {

				ServerErrorDialog dialog = new ServerErrorDialog(null, e.getMessage(), SERVER, REGISTRY_PORT);

				dialog.showDialog();

				if (dialog.getReturnValue() == 1) {
					retry = true;
				} else {
					System.exit(0);
				}
			}
		}

		Subject ipkSubject = new IpkAuthenticationProcess().getSubject();

		if (ipkSubject != null) {
			Authentication authentication = new Authentication(ipkSubject);

			retry = true;

			while (retry) {
				try {
					clientDataManager = new ClientDataManager(SERVER, REGISTRY_PORT, authentication);
					PublicationFrame.rootDirectory = clientDataManager.getRootDirectory();

					retry = false;
				} catch (Exception e) {

					e.printStackTrace();

					ServerErrorDialog dialog = new ServerErrorDialog(null, e.getMessage(), SERVER, REGISTRY_PORT);

					dialog.showDialog();

					if (dialog.getReturnValue() == 1) {
						retry = true;
					} else {
						EdalServer.stopServer(SERVER, REGISTRY_PORT);

						System.exit(0);
					}
				}
			}

			PublicationModulMain.setCurrentFrame(new PublicationFrame(clientDataManager,false, false));
		} else {
			System.exit(0);
		}

	}
}
