/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * Class to create html pages for the frame header with the help of
 * {@link Velocity}
 * 
 * @author arendd
 */
public class LoginVeloCityCreater {

	private static final String CODING_UTF_8 = "UTF-8";
	private static final String RESOURCES = "de/ipk_gatersleben/bit/bi/edal/primary_data/login/";
	private static final String HEAD_TEMPLATE = RESOURCES + "help.html";
	

	static {
		Velocity.setProperty("resource.loader", "class");
		Velocity.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}

	/**
	 * Generate the HTML output for the welcome page.
	 * 
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException
	 *             if unable to create output.
	 */
	protected static String generateHtmlForHeadPage() throws EdalException {

		VelocityContext context = new VelocityContext();

		URL imageUrl1 = LoginVeloCityCreater.class.getResource("aai01.png");
		URL imageUrl2 = LoginVeloCityCreater.class.getResource("aai02.png");
		URL imageUrl3 = LoginVeloCityCreater.class.getResource("aai03.png");
		URL imageUrl4 = LoginVeloCityCreater.class.getResource("aai04.png");
		URL imageUrl5 = LoginVeloCityCreater.class.getResource("aai05.png");


		context.put("image1", imageUrl1);
		context.put("image2", imageUrl2);
		context.put("image3", imageUrl3);
		context.put("image4", imageUrl4);
		context.put("image5", imageUrl5);


		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(HEAD_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	

}
