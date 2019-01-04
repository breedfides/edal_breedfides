/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
/*
l * Copyright (c) 2014 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import javax.mail.internet.InternetAddress;
import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import javafx.application.Platform;

@SuppressWarnings("restriction")
public class PublicationModulTest {

	private static final String SERVER = "localhost";

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

		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"));

		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, false,
				false);

		/* test connect to eDAL server */
		PropertyLoader.initialize("ipk_properties.txt");

//		Boolean retry = true;
//
//		while (retry) {
//			try {
//				Authentication testAuthentication = new Authentication(EdalHelpers.authenticateSampleUser());
//				clientDataManager = new ClientDataManager(SERVER, PORT, testAuthentication);
//				clientDataManager.getRootDirectory();
//				retry = false;
//			} catch (Exception e) {
//
//				ServerErrorDialog errorDialog = new ServerErrorDialog(null, e.getMessage(), SERVER, PORT);
//
//				errorDialog.showDialog();
//
//				if (errorDialog.getReturnValue() == 1) {
//					retry = true;
//				} else {
//					System.exit(0);
//				}
//			}
//		}
//
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
//
//		Platform.setImplicitExit(true);

		Subject s = new Subject();
		s.getPrincipals().add(new SamplePrincipal("arendd@IPK-GATERSLEBEN.DE"));

		Authentication testAuthentication = new Authentication(s);
		clientDataManager = new ClientDataManager(SERVER, PORT, testAuthentication);

		PublicationFrame.rootDirectory = clientDataManager.getRootDirectory();

		PublicationModulTest.setCurrentFrame(new PublicationFrame(clientDataManager, false, false));
		// PublicationModulTest.setCurrentFrame(new PublicationFrame("arendd",
		// clientDataManager));
	}
}
