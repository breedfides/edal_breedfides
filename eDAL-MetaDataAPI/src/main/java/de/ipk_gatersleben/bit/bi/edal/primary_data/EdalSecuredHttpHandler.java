/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
//import org.eclipse.jetty.jaas.JAASUserPrincipal;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

@SuppressWarnings("unused")
public class EdalSecuredHttpHandler extends AbstractHandler {

	static VeloCityHtmlGenerator velocityReportGenerator;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (request.getUserPrincipal() != null) {

//			Subject subject = ((JAASUserPrincipal) request.getUserPrincipal()).getSubject();
//
//			for (Principal principal : subject.getPrincipals()) {
//				System.out.println("Authenticated user '" + principal.getName() + "(" + principal.getClass().getName() + ")" + "' !");
//				break;
//			}

			final StringTokenizer tokenizer = new StringTokenizer(request.getRequestURI().toString(), EdalHttpServer.EDAL_PATH_SEPARATOR);

			if (!tokenizer.hasMoreTokens()) {
				/* no method defined -> break */
				// this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
				// "no ID and version specified");
			} else {
				final String methodToken = tokenizer.nextToken().toUpperCase();
				System.out.println(methodToken);
			}

		}
		response.flushBuffer();

	}

//	/**
//	 * Send a response with the given message and responseCode.
//	 * 
//	 * @param response
//	 *            the corresponding HTTP exchange.
//	 * @param responseCode
//	 *            the response code for this message (e.g. 200,404...).
//	 * @param message
//	 *            the message to send.
//	 */
//	private void sendMessage(final HttpServletResponse response, final HttpStatus.Code responseCode, final String message) {
//
//		try {
//
//			final String htmlOutput = velocityReportGenerator.generateHtmlForReport(responseCode).toString();
//
//			response.setStatus(responseCode.getCode());
//
//			response.setContentType("text/html");
//
//			final OutputStream responseBody = response.getOutputStream();
//			responseBody.write(htmlOutput.getBytes());
//			responseBody.close();
//		} catch (Exception e) {
//
//			DataManager.getImplProv().getLogger().error("unable to send " + responseCode + "-message : " + e.getClass());
//		}
//	}

}
