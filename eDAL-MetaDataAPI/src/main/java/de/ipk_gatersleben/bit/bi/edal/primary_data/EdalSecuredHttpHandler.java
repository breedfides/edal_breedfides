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
