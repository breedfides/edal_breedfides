/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.ServerErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import javafx.application.Platform;

public class PublicationModulTest {

	private static final String SERVER = "doi.ipk-gatersleben.de";

	private static final int PORT = 1099;

	private static PublicationFrame frame;

	public static ClientDataManager clientDataManager;

	public static PublicationFrame getFrame() {
		return PublicationModulTest.frame;
	}

	private static void setCurrentFrame(PublicationFrame frame) {
		PublicationModulTest.frame = frame;
	}

	public static void main(String[] args) throws Exception {

		Platform.setImplicitExit(false);

//		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
//				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
//				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));
//
//		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, false,
//				false);

		/* test connect to eDAL server */
		PropertyLoader.initialize("ipk_properties.txt");

//		}

//		Subject subject = null;
//		try {
//			subject = new IpkAuthenticationProcess().getSubject();
//		} catch (EdalException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//
//		if (subject != null) {
//			retry = true;
//			while (retry) {
//				try {
//					clientDataManager = new ClientDataManager(SERVER, PORT, new Authentication(subject));
//					PublicationFrame.rootDirectory = clientDataManager.getRootDirectory();
//					retry = false;
//
//				} catch (Exception e) {
//
//					ServerErrorDialog errorDialog = new ServerErrorDialog(null, e.getMessage(), SERVER, PORT);
//
//					errorDialog.showDialog();
//
//					if (errorDialog.getReturnValue() == 1) {
//						retry = true;
//					} else {
//						EdalServer.stopServer(SERVER, PORT);
//						System.exit(0);
//					}
//				}
//			}
//			PublicationModulTest.setCurrentFrame(new PublicationFrame(clientDataManager, false, false));
//		}

		Platform.setImplicitExit(true);

		Subject s = new Subject();
		s.getPrincipals().add(new SamplePrincipal("daar86@googlemail.com"));

	//	Authentication testAuthentication = new Authentication(s);
		
//		clientDataManager = new ClientDataManager(SERVER, PORT, testAuthentication);

	//	PublicationFrame.rootDirectory = clientDataManager.getRootDirectory();

		PublicationModulTest.setCurrentFrame(new PublicationFrame(null, false, false));
		// PublicationModulTest.setCurrentFrame(new PublicationFrame("arendd",
		// clientDataManager));
	}
}
