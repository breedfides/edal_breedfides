/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
 */
package de.ipk_gatersleben.bit.bi.edal.rest.server;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalRestServer {

	public static ImplementationProvider implProv= null;
	
	
	public static void startRestServer() {
		
	}
	
	public static void main(String[] args) throws AddressException, EdalConfigurationException,
			PrimaryDataDirectoryException, EdalAuthenticateException {

		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));

		configuration.setUseSSL(false);

		EdalRestServer.implProv = new FileSystemImplementationProvider(configuration);

		DataManager.getRootDirectory(EdalRestServer.implProv, EdalHelpers.authenticateWinOrUnixOrMacUser());

		EdalHttpServer server = DataManager.getHttpServer();


		HandlerCollection currentHandler = server.getHandlers();

		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		restHandler.setContextPath("/rest");
		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
				"de.ipk_gatersleben.bit.bi.edal.rest.server");
		jerseyServlet.setInitParameter("javax.ws.rs.Application", "de.ipk_gatersleben.bit.bi.edal.rest.server.CustomApplication");

		currentHandler.prependHandler(restHandler);
		try {
			restHandler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public static String getEntityMetadata(String uuid, long versionNumber) {
//		PrimaryDataEntity ent=null;
//		
//		/** REMOVE PACKAGE VISABILITY FROM DATAMANAGER FUNCTION **/
//		try {
//			ent = DataManager.getPrimaryDataEntityByID(uuid, versionNumber);
//		} catch (EdalException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ent.getMetaData().toString();	
//	}

}
