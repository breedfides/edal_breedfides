/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Calendar;

import javax.mail.internet.InternetAddress;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;

/**
 * VeloCityGenerator to create the eMails for the Approval-Service.
 * 
 * @author arendd
 */
class VeloCityEmailGenerator {

	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	
	/**
	 * Default constructor to load all VeloCity properties.
	 */
	VeloCityEmailGenerator() {
		Velocity.setProperty("resource.loader", "class");
		Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.setProperty("input.encoding", DEFAULT_CHARSET);
		Velocity.setProperty("output.encoding", DEFAULT_CHARSET);
		Velocity.init();
	}

	/**
	 * Generate the text for a request eMail.
	 * 
	 * @param entityVersion
	 *            the {@link PrimaryDataEntityVersion} that should be published.
	 * @param acceptURL
	 *            the {@link URL} to accept the request.
	 * @param rejectURL
	 *            the {@link URL} to reject the request.
	 * @param principal
	 *            the {@link Principal} of the user, that request the
	 *            {@link PublicReference}.
	 * @param mailAdress
	 *            the eMial address of the reviewer.
	 * @return the eMail output
	 * @throws VelocityException
	 *             if unable to create the output text.
	 */
	protected StringWriter generateRequestEmail(PrimaryDataEntityVersion entityVersion, URL acceptURL, URL rejectURL, Principal principal, InternetAddress mailAdress, ReviewerType reviewerType, URL reviewerURL, PersistentIdentifier idType, Calendar releaseDate) throws VelocityException {

		VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set ID type */
		context.put("id", idType.toString());
		/* set reviewer type */
		context.put("reviewerType", reviewerType.name());
		/* set reviewers eMail address */
		context.put("reviewer", mailAdress.toString());
		/* set requesting principal */
		context.put("principal", principal.getName());
		/* set URL to accept reference */
		context.put("acceptURL", acceptURL.toString());
		/* set URL to reject reference */
		context.put("rejectURL", rejectURL.toString());
		/* set the metadata for this object */
		context.put("infos", entityVersion.getMetaData());
		/* meta data String representation */
		context.put("infos_string", entityVersion.getMetaData().toString());
		/* set entity */
		context.put("entity", entityVersion.getEntity());
		/* set version */
		context.put("version", entityVersion);
		/* set meta data */
		context.put("allElements", MetaData.ELEMENT_TYPE_MAP.keySet());
		/* set link to the landing page */
		context.put("landingpage", reviewerURL.toString());
		/* set link to the landing page */
		if (releaseDate != null) {
			context.put("embargo", releaseDate.getTime());
		}
		
		StringWriter output = new StringWriter();

		try {
			switch (reviewerType.name()) {
			case "MANAGING":
				Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/RequestEmailManagingTemplate.xml", DEFAULT_CHARSET.toString(), context, output);
				break;
			case "SCIENTIFIC":
				Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/RequestEmailScientificTemplate.xml", DEFAULT_CHARSET.toString(), context, output);
				break;
			case "SUBSTITUTE":
				Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/RequestEmailSubstituteTemplate.xml", DEFAULT_CHARSET.toString(), context, output);
				break;
			}

			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new VelocityException("unable to write eMail output", e);
		}
		return output;

	}

	/**
	 * Generate the text for a accepted eMail.
	 * 
	 * @param newId
	 *            the new id for the accepted {@link PublicReference}.
	 * @param landingPage
	 *            the link to the landing page of the {@link PublicReference}.
	 * @param publicReference
	 *            the public reference object
	 * @return the eMail output
	 * @throws VelocityException
	 *             if unable to create the eMail text.
	 */
	protected StringWriter generateAcceptedEmail(String newId, URL landingPage, PublicReference publicReference) throws VelocityException {

		VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set the new id for the accepted object */
		context.put("newId", newId);
		/* set the link to the landing page of the accepted object */
		context.put("landingPage", landingPage);
		/* meta data infos */
		context.put("infos", publicReference.getVersion().getMetaData().toString());
		StringWriter output = new StringWriter();

		try {
			Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/AcceptedEmailTemplate.xml", DEFAULT_CHARSET.toString(), context, output);

			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new VelocityException("unable to write eMail output", e);
		}
		return output;
	}

	/**
	 * Generate the text for a rejected eMail.
	 * 
	 * @param publicReference
	 *            the public reference object
	 * 
	 * @return the eMail output
	 * 
	 * @throws VelocityException
	 *             if unable to create the eMail text.
	 */
	protected StringWriter generateRejectedEmail(PublicReference publicReference) throws VelocityException {

		VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* metadata infos */
		context.put("infos", publicReference.getVersion().getMetaData().toString());

		StringWriter output = new StringWriter();

		try {
			Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/RejectedEmailTemplate.xml", DEFAULT_CHARSET.toString(), context, output);

			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new VelocityException("unable to write eMail output", e);
		}
		return output;
	}

	/**
	 * Generate the text for a status eMail.
	 * 
	 * @param reference
	 *            the requested {@link PublicReference}
	 * 
	 * @return the eMail output
	 * 
	 * @throws VelocityException
	 *             if unable to create the eMail text.
	 */
	protected StringWriter generateStatusEmail(PublicReference reference) throws VelocityException {

		VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set id type */
		context.put("id", reference.getIdentifierType().toString());
		/* set reviewers name */
		context.put("user", reference.getRequestedPrincipal().getName());
		/* set the PublicReference */
		context.put("reference", reference);
		/* set version */
		context.put("version", reference.getVersion());
		/* set meta data info */
		context.put("infos", reference.getVersion().getMetaData().toString());
		/* set meta data */
		context.put("allElements", MetaData.ELEMENT_TYPE_MAP.keySet());

		StringWriter output = new StringWriter();

		try {
			Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/StatusEmailTemplate.xml", DEFAULT_CHARSET.toString(), context, output);

			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new VelocityException("unable to write eMail output", e);
		}
		return output;
	}

	protected StringWriter generateReviewSuccessfulMail(URL acceptUrl, URL rejectUrl, URL landingPage, PublicReference reference, int reviewerCode, int remainderCycle) {

		VelocityContext context = new VelocityContext();
		/* set the charset */
		context.put("charset", DEFAULT_CHARSET.toString());
		/* set the acceptURL of the object */
		context.put("acceptURL", acceptUrl);
		/* set the acceptURL of the object */
		context.put("rejectURL", rejectUrl);
		/* set id type */
		context.put("id", reference.getIdentifierType().toString());
		/* set the link to the landing page of the accepted object */
		context.put("landingPage", landingPage);
		/* set meta data info */
		context.put("infos", reference.getVersion().getMetaData().toString());
		/* set remainder cycle info */
		context.put("remainderCycle", remainderCycle);

		StringWriter output = new StringWriter();

		try {
			Velocity.mergeTemplate("de/ipk_gatersleben/bit/bi/edal/primary_data/file/implementation/ReviewSuccessfulEmailTemplate.xml", DEFAULT_CHARSET.toString(), context, output);

			output.flush();
			output.close();
		} catch (final IOException e) {
			throw new VelocityException("unable to write eMail output", e);
		}
		return output;
	}
}