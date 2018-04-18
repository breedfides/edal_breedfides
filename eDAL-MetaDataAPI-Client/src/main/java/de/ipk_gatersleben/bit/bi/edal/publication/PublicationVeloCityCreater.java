/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;

/**
 * Class to create html pages for the frame header with the help of
 * {@link Velocity}
 * 
 * @author arendd
 */
public class PublicationVeloCityCreater {

	private static final String CODING_UTF_8 = "UTF-8";
	private static final String RESOURCES = "de/ipk_gatersleben/bit/bi/edal/publication/";
	private static final String HEAD_TEMPLATE = RESOURCES + "head.html";
	private static final String CITATION_TEMPLATE = RESOURCES + "citation.html";
	private static final String PROCESS_TEMPLATE = RESOURCES + "process.html";
	private static final String FINISH_TEMPLATE = RESOURCES + "finish.html";
	private static final String SERVER_ERROR_TEMPLATE = RESOURCES + "server_error.html";
	private static final String AGREEMENT_TEMPLATE = RESOURCES + "agreement.html";
	private static final String AGREEMENT_EMAIL_TEMPLATE = RESOURCES + "confirmation_mail.html";
	private static final String LICENSE_TEMPLATE = RESOURCES + "license.html";
	private static final String ORCID_SEARCH_TEMPLATE = RESOURCES + "orcid_search.html";
	private static final String ORCID_SEARCH_HEADER_TEMPLATE = RESOURCES + "orcid_search_header.html";

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

		URL edalLogoUrl = PublicationFrame.class.getResource("edal_scaled.png");
		URL ipkLogoUrl = PublicationFrame.class.getResource("ipk_scaled.png");

		context.put("edal", edalLogoUrl);
		context.put("ipk", ipkLogoUrl);
		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);

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

	/**
	 * Generate the HTML output for the welcome page.
	 * 
	 * @param color
	 *            the text color
	 * 
	 * @return the HTML output in a {@link StringWriter}.
	 * @throws EdalException
	 *             if unable to create output.
	 */
	protected static String generateHtmlForHeadCitationPage(Color color) throws EdalException {

		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);
		context.put("fgcolor", color);
		context.put("authors", PublicationMainPanel.authorPanel.getAuthors());
		context.put("year", Calendar.getInstance().get(Calendar.YEAR));
		context.put("title", PublicationMainPanel.titleField.getText());
		context.put("publisher", PublicationMainPanel.publisherField.getText());

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(CITATION_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateHtmlForProcessDialog() throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);
		context.put("authors", PublicationMainPanel.authorPanel.getAuthors());
		context.put("year", Calendar.getInstance().get(Calendar.YEAR));
		context.put("title", PublicationMainPanel.titleField.getText());
		context.put("publisher", PublicationMainPanel.publisherField.getText());
		context.put("license", PublicationMainPanel.licensePanel.getLicense());
		context.put("directories", Utils.NumberOfDirectories);
		context.put("files", Utils.NumberOfFiles);
		context.put("path", PublicationMainPanel.uploadPathField.getText());

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(PROCESS_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateFinishUploadPage() throws EdalException {

		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);
		context.put("authors", PublicationMainPanel.authorPanel.getAuthors());
		context.put("year", Calendar.getInstance().get(Calendar.YEAR));
		context.put("title", PublicationMainPanel.titleField.getText());
		context.put("publisher", PublicationMainPanel.publisherField.getText());
		context.put("license", PublicationMainPanel.licensePanel.getLicense());
		context.put("directories", Utils.NumberOfDirectories);
		context.put("files", Utils.NumberOfFiles);
		context.put("path", PublicationMainPanel.uploadPathField.getText());

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(FINISH_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	protected static String generateServerErrorDialog(String errorMessage, String serverAddress, int registryPort)
			throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);
		context.put("errorMessage", errorMessage);
		context.put("serverAddress", serverAddress);
		context.put("registryPort", registryPort);
		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(SERVER_ERROR_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	protected static String generateHtmlForAgreement() throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(AGREEMENT_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create html page : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateEmailForAgreement(MetaData metadata, Calendar now) throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("timepoint", now.getTime());
		context.put("authors", PublicationMainPanel.authorPanel.getAuthors());
		context.put("year", Calendar.getInstance().get(Calendar.YEAR));
		context.put("title", PublicationMainPanel.titleField.getText());
		context.put("publisher", PublicationMainPanel.publisherField.getText());
		context.put("license", PublicationMainPanel.licensePanel.getLicense());
		context.put("directories", Utils.NumberOfDirectories);
		context.put("files", Utils.NumberOfFiles);
		context.put("path", PublicationMainPanel.uploadPathField.getText());

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(AGREEMENT_EMAIL_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create agreement email : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateHtmlForLicense(String legalCodeUrl, String humanReadableUrl) throws EdalException {

		StringWriter output = new StringWriter();

		VelocityContext context = new VelocityContext();

		context.put("legalcode", legalCodeUrl);
		context.put("humanreadable", humanReadableUrl);

		Velocity.mergeTemplate(LICENSE_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create license HTML : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateHtmlForOrcidSearch(String orcid, NaturalPerson naturalPerson) throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);
		context.put("orcid", orcid);
		context.put("givenname", naturalPerson.getGivenName());
		context.put("surename", naturalPerson.getSureName());

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(ORCID_SEARCH_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create license HTML : " + e.getMessage(), e);
		}
		return output.toString();
	}

	public static String generateHtmlForOrcidSearchHeader() throws EdalException {
		VelocityContext context = new VelocityContext();

		context.put("bgcolor", PropertyLoader.HEADER_FOOTER_COLOR);

		StringWriter output = new StringWriter();

		Velocity.mergeTemplate(ORCID_SEARCH_HEADER_TEMPLATE, CODING_UTF_8, context, output);

		try {
			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new EdalException("unable to create license HTML : " + e.getMessage(), e);
		}
		return output.toString();
	}

}
