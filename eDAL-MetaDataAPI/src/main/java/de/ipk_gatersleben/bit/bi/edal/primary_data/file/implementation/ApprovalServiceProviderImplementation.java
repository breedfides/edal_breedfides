/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.exception.VelocityException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalApprovalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ReferenceableException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewProcess;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewResult;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewStatusType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review.ReviewStatus.ReviewerType;

/**
 * Implementation of {@link ApprovalServiceProvider} for the storage back end.
 * 
 * @author arendd
 */

public class ApprovalServiceProviderImplementation implements ApprovalServiceProvider {

	private static final String UNABLE_TO_GENERATE_E_MAIL = "unable to generate e-mail";
	private static final String EMAIL_SENDER = "eDAL-Service <" + DataManager.getConfiguration().getEdalEmailAddress()
			+ ">";

	/**
	 * number of days to wait between reminder to send to reviewer
	 */
	private static final int REMINDER_CYCLE = 14;

	/**
	 * HashMap to store the changes PublicReference objects for the current session
	 */
	private static Map<String, PublicReferenceImplementation> synchronizedMap;

	/**
	 * static VelocityGenerator
	 */
	private static VeloCityEmailGenerator velocityGenerator;

	static {

		velocityGenerator = new VeloCityEmailGenerator();

		ApprovalServiceProviderImplementation.synchronizedMap = Collections
				.synchronizedMap(new HashMap<String, PublicReferenceImplementation>());

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final Transaction transaction = session.beginTransaction();

		@SuppressWarnings("unchecked")
		final List<TicketImplementation> ticketImplementation = session.createCriteria(TicketImplementation.class)
				.list();

		for (final TicketImplementation ticket : ticketImplementation) {
			ApprovalServiceProviderImplementation.synchronizedMap.put(ticket.getTicket(), ticket.getReference());
		}

		transaction.commit();
		session.close();

	}

	/** {@inheritDoc} */
	@Override
	public void accept(final String ticket, final int reviewerID) throws EdalApprovalException {
		this.changeReviewStatus(ticket, reviewerID, ReviewStatusType.ACCEPTED);

	}

	@Override
	public void acceptTicketByUser(final String ticket, final int reviewerHashCode) throws EdalApprovalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final TicketImplementation ticketImplementation = (TicketImplementation) session
				.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

		session.close();

		if (ticketImplementation != null) {
			if (ticketImplementation.getEmailNotificationAddress().hashCode() == reviewerHashCode) {
				ticketImplementation.setUserAnswer(true);
				this.updateTicket(ticketImplementation);
			} else {
				throw new EdalApprovalException("wrong ticket number for this reviewer code");
			}
		} else {
			throw new EdalApprovalException("no ticket with this number found");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void approve(final PublicReference reference, final InternetAddress emailNotificationAddress)
			throws EdalApprovalException {

		if (reference instanceof PublicReferenceImplementation) {

			final PublicReferenceImplementation internalRef = (PublicReferenceImplementation) reference;

			internalRef.setPublicationStatus(PublicationStatus.UNDER_REVIEW);

			final ReviewResult result = ReviewProcess.review(new ArrayList<ReviewStatus>());

			final Set<ReviewStatusImplementation> reviewSet = new HashSet<ReviewStatusImplementation>();

			if (result != null) {
				for (final ReviewStatus status : result.getReviewerStatusList()) {

					final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

					final Transaction transaction = session.beginTransaction();

					final ReviewStatusImplementation reviewStatus = new ReviewStatusImplementation();

					reviewStatus.setPublicReference(internalRef);
					reviewStatus.setEmailAddress(status.getEmailAddress().getAddress());
					reviewStatus.setRequestedDate(Calendar.getInstance());
					reviewStatus.setStatusType(ReviewStatusType.UNDECIDED);
					reviewStatus.setReviewerType(status.getReviewerType());
					reviewSet.add(reviewStatus);

					session.save(reviewStatus);

					transaction.commit();
					session.close();
				}
			}

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final Transaction transaction = session.beginTransaction();

			internalRef.setReviewStatusSet(reviewSet);

			session.saveOrUpdate(internalRef);

			transaction.commit();
			session.close();

			final String ticket = UUID.randomUUID().toString();

			this.storeNewTicket(ticket, reference, emailNotificationAddress);

			ApprovalServiceProviderImplementation.synchronizedMap.put(ticket, internalRef);

			try {
				for (final ReviewStatus reviewStatus : result.getReviewerStatusList()) {
					this.sendRequestApprovalMail(ticket, reference, reviewStatus.getEmailAddress(),
							reviewStatus.getReviewerType());
				}
				this.sendStatusMailToRequestedPerson(emailNotificationAddress, reference);
			} catch (final EdalException e) {
				throw new EdalApprovalException("unable to send requestApprovalEmails", e);
			}
		} else {
			throw new EdalApprovalException("reference object is no instanceof PublicReferenceImplementation");
		}
	}

	/**
	 * Change the {@link ReviewStatus} of a {@link PublicReference} given by the
	 * ticket number.
	 * 
	 * @param ticket
	 *            to identify the {@link PublicReference}
	 * @param reviewerCode
	 *            to identify the reviewer.
	 * @param status
	 *            the new {@link ReviewStatus}.
	 * @throws EdalApprovalException
	 *             if unable to find reviewer or ticket.
	 */
	private void changeReviewStatus(final String ticket, final int reviewerCode, final ReviewStatusType status)
			throws EdalApprovalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		final ReviewersImplementation reviewerImplementation = (ReviewersImplementation) session
				.createCriteria(ReviewersImplementation.class).add(Restrictions.eq("hashCode", reviewerCode))
				.uniqueResult();

		if (reviewerImplementation == null) {
			throw new EdalApprovalException("unable to find a reviewer with this code");
		}

		final TicketImplementation ticketImplementation = (TicketImplementation) session
				.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

		if (ticketImplementation == null) {
			throw new EdalApprovalException("unable to find a ticket with this code");
		}

		@SuppressWarnings("unchecked")
		final List<ReviewStatusImplementation> reviewStatusList = session
				.createCriteria(ReviewStatusImplementation.class)
				.add(Restrictions.eq("emailAddress", reviewerImplementation.getEmailAddress()))
				.add(Restrictions.eq("publicReference", ticketImplementation.getReference())).list();

		for (final ReviewStatusImplementation reviewStatusImplementation : reviewStatusList) {
			reviewStatusImplementation.setStatusType(status);
			session.saveOrUpdate(reviewStatusImplementation);
		}

		session.getTransaction().commit();
		session.close();
	}

	/** {@inheritDoc} */
	@Override
	public void checkOpenReviews(final Map<PublicReference, List<ReviewStatus>> results) throws EdalApprovalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		for (final Entry<PublicReference, List<ReviewStatus>> result : results.entrySet()) {

			final TicketImplementation ticket = (TicketImplementation) session
					.createCriteria(TicketImplementation.class).add(Restrictions.eq("reference", result.getKey()))
					.uniqueResult();

			DataManager.getImplProv().getLogger()
					.debug("CheckOpenReviewProcess for Ticket : " + "\t" + ticket.getTicket());

			try {
				this.updateReviewStatus(result.getKey(), ReviewProcess.review(result.getValue()));
			} catch (final EdalApprovalException e) {
				session.close();
				throw new EdalApprovalException("unable to check open review processes: " + e.getMessage(),
						e.getCause());
			}
		
		}
		session.close();
	}

	/**
	 * Create the landing page string without the server part (host and port) for a
	 * given {@link PublicReference}.
	 * 
	 * @param reference
	 *            the {@link PublicReference} for this landing page.
	 * @return the landing page {@link String}
	 */
	private String createLandingPageString(final PublicReference reference) {

		return EdalHttpServer.EDAL_PATH_SEPARATOR + reference.getIdentifierType().toString()
				+ EdalHttpServer.EDAL_PATH_SEPARATOR + reference.getInternalID() + EdalHttpServer.EDAL_PATH_SEPARATOR
				+ reference.getVersion().getEntity().getID() + EdalHttpServer.EDAL_PATH_SEPARATOR
				+ reference.getVersion().getRevision();

	}

	/**
	 * Create the complete landing page {@link URL} including server part to send it
	 * to the requested author.
	 * 
	 * @param reference
	 *            the {@link PublicReference} corresponding to this {@link URL}.
	 * @return the complete {@link URL}
	 * @throws EdalApprovalException
	 *             if unable to create the {@link URL}.
	 */
	private URL createLandingPageURL(final PublicReferenceImplementation reference) throws EdalApprovalException {
		try {

			/** changed FZJ **/
			// return new URL(EdalJettyServer.getServerURL(),
			// this.createLandingPageString(reference));
			return new URL(EdalHttpServer.getServerURL().toString() + this.createLandingPageString(reference));

		} catch (EdalException | MalformedURLException e) {
			throw new EdalApprovalException("unable to create URL for the landing page : " + e.getMessage(), e);
		}
	}

	/**
	 * Delete all existing {@link ReviewStatusImplementation} after a
	 * {@link PublicReference} was accepted or rejected.
	 * 
	 * @param reference
	 *            the {@link PublicReference} to delete all
	 *            {@link ReviewStatusImplementation}.
	 */
	private void deleteReviewStatus(final PublicReferenceImplementation reference) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		final List<ReviewStatusImplementation> reviewStatus = session.createCriteria(ReviewStatusImplementation.class)
				.add(Restrictions.eq("publicReference", reference)).list();

		for (final ReviewStatusImplementation reviewStatusImplementation : reviewStatus) {
			session.delete(reviewStatusImplementation);
		}

		session.getTransaction().commit();
		session.close();

	}

	/**
	 * Delete the {@link TicketImplementation} in the database, after it was
	 * accepted or rejected.
	 * 
	 * @param ticket
	 *            the {@link TicketImplementation} to delete.
	 */
	private void deleteTicket(final String ticket) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		final TicketImplementation ticketImplementation = (TicketImplementation) session
				.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

		session.delete(ticketImplementation);
		session.getTransaction().commit();
		session.close();

		EdalHttpHandler.deleteTicketFromReviewerHashMap(ticket);
		EdalHttpHandler.deleteTicketFromUserHashMap(ticket);

	}

	/** {@inheritDoc} */
	@Override
	public Map<PublicReference, List<ReviewStatus>> getAllOpenReviews() {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		@SuppressWarnings("unchecked")
		final List<PublicReferenceImplementation> publicReferences = session
				.createCriteria(PublicReferenceImplementation.class).add(Restrictions.isNull("acceptedDate"))
				.add(Restrictions.isNull("rejectedDate"))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

		DataManager.getImplProv().getLogger().debug("open PublicReferences : " + publicReferences.size());

		session.close();

		final Map<PublicReference, List<ReviewStatus>> openReviewProcesses = new HashMap<PublicReference, List<ReviewStatus>>(
				publicReferences.size());

		for (final PublicReferenceImplementation publicReference : publicReferences) {

			final Set<ReviewStatusImplementation> privateSet = publicReference.getReviewStatusSet();

			DataManager.getImplProv().getLogger().debug("open ReviewStatus : " + privateSet.size());

			if (!privateSet.isEmpty()) {
				final List<ReviewStatus> publicSet = new ArrayList<ReviewStatus>(privateSet.size());

				for (final ReviewStatusImplementation reviewStatusImplementation : privateSet) {
					publicSet.add(reviewStatusImplementation.toReviewStatus());

				}
				openReviewProcesses.put(publicReference, publicSet);
			}
		}

		DataManager.getImplProv().getLogger().debug("open ReviewProcesses : " + openReviewProcesses.size());

		return openReviewProcesses;
	}

	@Override
	public String getNewURL(final PublicReference reference) throws EdalApprovalException {

		String url = "";

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final Transaction transaction = session.beginTransaction();

		final int year = Calendar.getInstance().get(Calendar.YEAR);

		final int numberOfStoredIds = session.createCriteria(UrlImplementation.class).add(Restrictions.eq("year", year))
				.list().size();

		try {

			url = EdalHttpServer.getServerURL() + EdalHttpServer.EDAL_PATH_SEPARATOR + year
					+ EdalHttpServer.EDAL_PATH_SEPARATOR + numberOfStoredIds + EdalHttpServer.EDAL_PATH_SEPARATOR
					+ reference.getVersion().getMetaData().getElementValue(EnumDublinCoreElements.TITLE).toString();

		} catch (EdalException | MetaDataException e) {
			e.printStackTrace();
		}

		final PublicReferenceImplementation publicReferenceImplementation = (PublicReferenceImplementation) reference;
		publicReferenceImplementation.setVersion(reference.getVersion());
		publicReferenceImplementation.setAcceptedDate(Calendar.getInstance());
		publicReferenceImplementation.setAssignedID(url);
		publicReferenceImplementation.setPublicationStatus(PublicationStatus.ACCEPTED);
		publicReferenceImplementation.setPublic(true);
		publicReferenceImplementation.setIdentifierType(PersistentIdentifier.URL);

		final UrlImplementation urlImplementation = new UrlImplementation();

		urlImplementation.setReference(publicReferenceImplementation);
		urlImplementation.setUrl(url);
		urlImplementation.setYear(year);

		session.update(publicReferenceImplementation);
		session.save(urlImplementation);

		transaction.commit();

		session.close();

		return url;
	}

	@Override
	public PublicReference getPublicReferenceByInternalId(final String internalId) throws EdalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final PublicReferenceImplementation publicReference = (PublicReferenceImplementation) session
				.createCriteria(PublicReferenceImplementation.class).add(Restrictions.eq("internalID", internalId))
				.uniqueResult();

		session.close();

		if (publicReference != null) {
			return publicReference;
		} else {
			throw new EdalException("no PublicReference with ID '" + internalId + "' found !");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reject(final String ticket, final int reviewerID) throws EdalApprovalException {

		this.changeReviewStatus(ticket, reviewerID, ReviewStatusType.REJECTED);

	}

	@Override
	public void rejectTicketByUser(final String ticket, final int reviewerHashCode) throws EdalApprovalException {
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final TicketImplementation ticketImplementation = (TicketImplementation) session
				.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

		session.close();
		if (ticketImplementation != null) {
			if (ticketImplementation.getEmailNotificationAddress().hashCode() == reviewerHashCode) {
				ticketImplementation.setUserAnswer(false);
				this.updateTicket(ticketImplementation);
			} else {
				throw new EdalApprovalException("wrong ticket number for this reviewer code");
			}
		} else {
			throw new EdalApprovalException("no ticket with this number found");
		}
	}

	/**
	 * Send an eMail to the user that his request was accepted.
	 * 
	 * @param newId
	 *            the new assigned ID
	 * @param emailAddress
	 *            the eMail address of the user.
	 * @param landingPage
	 *            the link to the eDAL landingPage.
	 * @throws EdalApprovalException
	 *             if unable to generate the eMail.
	 */
	private void sendAcceptedMail(final String newId, final String emailAddress, final URL landingPage,
			final PublicReference reference) throws EdalApprovalException {

		String message;
		try {
			message = ApprovalServiceProviderImplementation.velocityGenerator
					.generateAcceptedEmail(newId, landingPage, reference).toString();
		} catch (final VelocityException e) {
			throw new EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
		}

		this.sendEmail(message,
				"[e!DAL] data publication released online: " + reference.getVersion().getMetaData().toString(),
				emailAddress);

	}

	/**
	 * Function to send an eMail to the given recipient.
	 * 
	 * @param emailAddress
	 *            the eMail address of the recipient.
	 */
	private void sendEmail(final String message, final String subject, final String emailAddress) {

		javax.mail.Session session = null;

		InternetAddress addressFrom = null;

		final Properties properties = new Properties();

		properties.put("mail.smtp.host", DataManager.getConfiguration().getMailSmtpHost());

		if (DataManager.getConfiguration().getMailSmtpLogin() == null
				|| DataManager.getConfiguration().getMailSmtpLogin().isEmpty()) {

			session = javax.mail.Session.getDefaultInstance(properties);

			try {
				addressFrom = new InternetAddress(DataManager.getConfiguration().getEdalEmailAddress(),
						ApprovalServiceProviderImplementation.EMAIL_SENDER);
			} catch (final UnsupportedEncodingException e) {
				DataManager.getConfiguration().getErrorLogger().fatal(emailAddress + " : " + e.getMessage());
			}

		} else {

			properties.put("mail.smtp.auth", "true");

			final Authenticator authenticator = new Authenticator() {
				private PasswordAuthentication authentication;

				{
					this.authentication = new PasswordAuthentication(DataManager.getConfiguration().getMailSmtpLogin(),
							DataManager.getConfiguration().getMailSmtpPassword());
				}

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return this.authentication;
				}
			};

			session = javax.mail.Session.getInstance(properties, authenticator);

			try {
				addressFrom = new InternetAddress(DataManager.getConfiguration().getMailSmtpLogin(),
						ApprovalServiceProviderImplementation.EMAIL_SENDER);
			} catch (final UnsupportedEncodingException e) {
				DataManager.getConfiguration().getErrorLogger().fatal(emailAddress + " : " + e.getMessage());
			}

		}

		final Message mail = new MimeMessage(session);
		try {

			mail.setFrom(addressFrom);
			final InternetAddress addressTo = new InternetAddress(emailAddress);
			mail.setRecipient(Message.RecipientType.TO, addressTo);
			mail.setSubject(subject);
			mail.setContent(message, "text/html; charset=UTF-8");

			Transport.send(mail);

		} catch (final MessagingException e) {
			DataManager.getConfiguration().getErrorLogger().fatal(emailAddress + " : " + e.getMessage());
		}
	}

	// /**
	// * Send an eMail to the user that his request was rejected.
	// *
	// * @param emailAddress
	// * the eMail address of the user.
	// * @throws EdalApprovalException
	// * if unable to generate the eMail.
	// */
	// private void sendRejectedMail(final String emailAddress, final
	// PublicReference reference)
	// throws EdalApprovalException {
	//
	// System.out.println("send reject");
	//
	// String message;
	// try {
	// message =
	// ApprovalServiceProviderImplementation.velocityGenerator.generateRejectedEmail(reference)
	// .toString();
	// } catch (final VelocityException e) {
	// throw new
	// EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL,
	// e);
	// }
	//
	// this.sendEmail(message,
	// "[e!DAL] - data publication rejected: " +
	// reference.getVersion().getMetaData().toString(),
	// emailAddress);
	//
	// }

	/**
	 * Send an eMail to the user that his request was rejected.
	 * 
	 * @param emailAddress
	 *            the eMail address of the user.
	 * @throws EdalApprovalException
	 *             if unable to generate the eMail.
	 */
	private void sendRejectedMail(final String message, final String emailAddress, final String title)
			throws EdalApprovalException {

		this.sendEmail(message, "[e!DAL] - data publication rejected: " + title, emailAddress);

	}

	/**
	 * Send all reviewers a Request-eMail.
	 * 
	 * @throws EdalApprovalException
	 *             if unable to generate the eMails
	 */
	private void sendRequestApprovalMail(final String ticket, final PublicReference reference,
			final InternetAddress emailAddress, final ReviewerType reviewerType) throws EdalApprovalException {

		DataManager.getImplProv().getLogger().debug("Send Requestmail :" + emailAddress);

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final int hashCode = emailAddress.getAddress().hashCode();

		ReviewersImplementation reviewer = (ReviewersImplementation) session
				.createCriteria(ReviewersImplementation.class).add(Restrictions.eq("hashCode", hashCode))
				.add(Restrictions.eq("emailAddress", emailAddress.getAddress())).uniqueResult();

		if (reviewer == null) {

			reviewer = new ReviewersImplementation(emailAddress.getAddress(), hashCode);
			final Transaction transaction = session.beginTransaction();
			session.save(reviewer);
			transaction.commit();
		}

		session.close();

		try {
			final StringWriter stringWriter = ApprovalServiceProviderImplementation.velocityGenerator
					.generateRequestEmail(reference.getVersion(),
							EdalHttpServer.generateMethodURL(ticket, emailAddress.hashCode(), EdalHttpFunctions.ACCEPT),
							EdalHttpServer.generateMethodURL(ticket, emailAddress.hashCode(), EdalHttpFunctions.REJECT),
							reference.getRequestedPrincipal(), emailAddress, reviewerType,
							EdalHttpServer.generateReviewerURL(
									this.createLandingPageURL((PublicReferenceImplementation) reference), hashCode),
							reference.getIdentifierType());

			this.sendEmail(stringWriter.toString(),
					"[e!DAL] review request: " + reference.getVersion().getMetaData().toString(),
					emailAddress.getAddress());
		} catch (final VelocityException | EdalException e) {
			throw new EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
		}

	}

	/**
	 * Send an eMail to the user that his review was successful.
	 * 
	 * @param ticket
	 *            the ticket for this {@link PublicReference}.
	 * @param reference
	 *            the {@link PublicReference} object which was accepted
	 * @param reminder
	 *            true if it is a reminder email
	 * @throws EdalApprovalException
	 *             if unable to generate the eMail.
	 */
	private void sendReviewSuccessfulMail(final TicketImplementation ticket, final PublicReference reference,
			boolean reminder) throws EdalApprovalException {

		final int reviewerCode = ticket.getEmailNotificationAddress().hashCode();

		String message;

		/**
		 * Use ReviewerCode from Scientific Reviewer for landing page
		 */
		try {
			message = ApprovalServiceProviderImplementation.velocityGenerator.generateReviewSuccessfulMail(
					EdalHttpServer.generateMethodURL(ticket.getTicket(), reviewerCode, EdalHttpFunctions.USER_ACCEPT),
					EdalHttpServer.generateMethodURL(ticket.getTicket(), reviewerCode, EdalHttpFunctions.USER_REJECT),
					EdalHttpServer.generateReviewerURL(
							this.createLandingPageURL((PublicReferenceImplementation) reference),
							DataManager.getConfiguration().getReviewerScientific().toString().hashCode()),
					reference, reviewerCode, ApprovalServiceProviderImplementation.REMINDER_CYCLE).toString();
		} catch (final VelocityException | EdalException | EdalConfigurationException e) {
			throw new EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
		}

		if (!reminder) {
			this.sendEmail(message, "[e!DAL] data publication approved successfully: "
					+ reference.getVersion().getMetaData().toString(), ticket.getEmailNotificationAddress());
		} else {
			this.sendEmail(message, "[e!DAL] reminder to publish your already approved data publication :"
					+ reference.getVersion().getMetaData().toString(), ticket.getEmailNotificationAddress());
		}

	}

	/**
	 * Send a status eMail to a requesting person.
	 * 
	 * @param emailAddress
	 *            the eMail address of the person who requested the
	 *            {@link PublicReference} to send the eMail.
	 * @param publicReference
	 *            the {@link PublicReference}
	 * @throws EdalApprovalException
	 *             if unable to generate the eMail.
	 */
	private void sendStatusMailToRequestedPerson(final InternetAddress emailAddress, final PublicReference reference)
			throws EdalApprovalException {

		try {
			final StringWriter stringWriter = ApprovalServiceProviderImplementation.velocityGenerator
					.generateStatusEmail(reference);
			this.sendEmail(stringWriter.toString(),
					"[e!DAL] submission under review: " + reference.getVersion().getMetaData().toString(),
					emailAddress.getAddress());
		} catch (final VelocityException e) {
			throw new EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
		}

	}

	/**
	 * Update the isPublic flag of the {@link PublicReferenceImplementation} to the
	 * given ticket to false;
	 * 
	 * @param ticket
	 *            the ticket for the corresponding
	 *            {@link PublicReferenceImplementation}.
	 * @throws EdalApprovalException
	 *             if unable to set the {@link PublicReference} to false.
	 */
	private void setPublicReferenceToFalse(final String ticket) throws EdalApprovalException {
		final PublicReferenceImplementation publicRef = ApprovalServiceProviderImplementation.synchronizedMap
				.get(ticket);

		if (publicRef == null) {

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final Transaction transaction = session.beginTransaction();

			final TicketImplementation ticketImplementation = (TicketImplementation) session
					.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

			final PublicReferenceImplementation publicReference = ticketImplementation.getReference();

			transaction.commit();
			session.close();

			try {
				publicReference.getReferencable().rejectApprovalRequest(publicReference);
			} catch (final EdalException e) {
				throw new EdalApprovalException("unable to reject PublicReference", e);
			} catch (ReferenceableException e) {
				throw new EdalApprovalException("unable to reject PublicReference: " + e.getMessage(), e.getCause());
			}

			final Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final Transaction transaction2 = session2.beginTransaction();

			publicReference.setRejectedDate(Calendar.getInstance());
			publicReference.setPublicationStatus(PublicationStatus.REJECTED);
			session2.update(publicReference);

			transaction2.commit();
			session2.close();

		} else {

			try {
				publicRef.getReferencable().rejectApprovalRequest(publicRef);
			} catch (final EdalException e) {
				throw new EdalApprovalException("unable to reject PublicReference", e);
			} catch (ReferenceableException e) {
				throw new EdalApprovalException("unable to reject PublicReference: " + e.getMessage(), e.getCause());
			}

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final Transaction transaction = session.beginTransaction();

			publicRef.setRejectedDate(Calendar.getInstance());
			publicRef.setPublicationStatus(PublicationStatus.REJECTED);
			session.update(publicRef);
			transaction.commit();
			session.close();

		}

	}

	/**
	 * Update the isPublic flag of the {@link PublicReferenceImplementation} to the
	 * given ticket to true; Store the new ID to the database;
	 * 
	 * @param ticket
	 *            the ticket for the corresponding
	 *            {@link PublicReferenceImplementation}.
	 * @return the new registered ID.
	 * @throws EdalApprovalException
	 *             if unable to get a new ID.
	 */
	private String setPublicReferenceToTrue(final String ticket) throws EdalApprovalException {

		final PublicReferenceImplementation publicRef = ApprovalServiceProviderImplementation.synchronizedMap
				.get(ticket);

		if (publicRef == null) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final Transaction transaction = session.beginTransaction();

			final TicketImplementation ticketImplementation = (TicketImplementation) session
					.createCriteria(TicketImplementation.class).add(Restrictions.eq("ticket", ticket)).uniqueResult();

			final PublicReferenceImplementation publicReference = ticketImplementation.getReference();

			transaction.commit();
			session.close();

			String newId = "";
			try {

				newId = publicReference.getReferencable().acceptApprovalRequest(publicReference);
				final Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

				final Transaction transaction2 = session2.beginTransaction();

				publicReference.setAssignedID(newId);
				publicReference.setPublic(true);
				publicReference.setAcceptedDate(Calendar.getInstance());
				publicReference.setPublicationStatus(PublicationStatus.ACCEPTED);
				session2.update(publicReference);

				transaction2.commit();
				session2.close();

				return newId;
			} catch (final EdalException e) {
				throw new EdalApprovalException("unable to get new ID: " + e.getMessage(), e.getCause());
			} catch (ReferenceableException e) {
				throw new EdalApprovalException("unable to assign new ID: " + e.getMessage(), e.getCause());
			}

		} else {

			String newId = "";
			try {
				newId = publicRef.getReferencable().acceptApprovalRequest(publicRef);
				publicRef.setAssignedID(newId);
				publicRef.setPublic(true);
				publicRef.setAcceptedDate(Calendar.getInstance());
				publicRef.setPublicationStatus(PublicationStatus.ACCEPTED);

				final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

				final Transaction transaction = session.beginTransaction();

				session.update(publicRef);

				transaction.commit();
				session.close();

				return newId;
			} catch (final EdalException e) {
				throw new EdalApprovalException("unable to get new ID: " + e.getMessage(), e.getCause());
			} catch (ReferenceableException e) {
				throw new EdalApprovalException("unable to assign new ID: " + e.getMessage(), e.getCause());
			}

		}
	}

	@Override
	public String storePersistentID(final PublicReference reference, final String doi, final int year)
			throws EdalApprovalException {

		if (reference instanceof PublicReferenceImplementation) {

			final PublicReferenceImplementation publicReferenceImplementation = (PublicReferenceImplementation) reference;
			publicReferenceImplementation.setVersion(reference.getVersion());
			publicReferenceImplementation.setAcceptedDate(Calendar.getInstance());
			publicReferenceImplementation.setAssignedID(doi);
			publicReferenceImplementation.setPublicationStatus(PublicationStatus.ACCEPTED);
			publicReferenceImplementation.setPublic(true);
			publicReferenceImplementation.setIdentifierType(reference.getIdentifierType());

			final DoiImplementation doiImplementation = new DoiImplementation();

			doiImplementation.setReference(publicReferenceImplementation);
			doiImplementation.setUrl(doi);
			doiImplementation.setYear(year);

			try {

				final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

				final Transaction transaction = session.beginTransaction();

				session.saveOrUpdate(publicReferenceImplementation);

				transaction.commit();
				session.close();

				final Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

				final Transaction transaction2 = session2.beginTransaction();

				session2.save(doiImplementation);

				transaction2.commit();
				session2.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}

			return doi;
		} else {
			throw new EdalApprovalException("reference object is no instanceof PublicReferenceImplementation");
		}
	}

	/**
	 * Store an new {@link TicketImplementation} into the database.
	 * 
	 * @param ticket
	 *            the ticket to store into the database.
	 * @param reference
	 *            the corresponding {@link PublicReference}
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user
	 */
	private void storeNewTicket(final String ticket, final PublicReference reference,
			final InternetAddress emailNotificationAddress) {

		final PublicReferenceImplementation publicReference = (PublicReferenceImplementation) reference;

		publicReference.setRequestedDate(Calendar.getInstance());

		publicReference.setLandingPage(this.createLandingPageString(reference));

		final TicketImplementation ticketImplementation = new TicketImplementation(ticket, publicReference,
				emailNotificationAddress.getAddress());

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final Transaction transaction = session.beginTransaction();
		session.update(publicReference);
		session.save(ticketImplementation);
		transaction.commit();
		session.close();

	}

	/**
	 * Update the review Status of the given {@link PublicReference}.
	 * 
	 * @param publicReference
	 * @param result
	 * @throws EdalApprovalException
	 */
	private void updateReviewStatus(final PublicReference publicReference, final ReviewResult result)
			throws EdalApprovalException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final TicketImplementation ticket = (TicketImplementation) session.createCriteria(TicketImplementation.class)
				.add(Restrictions.eq("reference", publicReference)).uniqueResult();

		session.close();

		switch (result.getReviewResult()) {

		case ACCEPTED:

			if (!((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration().isReadOnly()) {

				// System.out.println("Reference was accepted !!!");

				// send remainder notification to reviewer
				final Calendar timePoint = Calendar.getInstance();

				if (ticket.isUserAnswer() == null) {
					if (ticket.getUserReminderEmailSend() == null
							|| timePoint.getTimeInMillis() - ticket.getUserReminderEmailSend()
									.getTimeInMillis() > (long) ApprovalServiceProviderImplementation.REMINDER_CYCLE
											* 1000 * 60 * 60 * 24) {

						if (ticket.getUserReminderEmailSend() == null) {

							this.sendReviewSuccessfulMail(ticket, publicReference, false);

							ticket.setUserReminderEmailSend(timePoint);

							this.updateTicket(ticket);
						} else {
							this.sendReviewSuccessfulMail(ticket, publicReference, true);

							ticket.setUserReminderEmailSend(timePoint);

							this.updateTicket(ticket);
						}
					}
				} else {
					synchronized (ApprovalServiceProviderImplementation.synchronizedMap) {
						if (ticket.isUserAnswer() == true) {

							if (ApprovalServiceProviderImplementation.synchronizedMap.containsKey(ticket.getTicket())) {

								// System.out.println("contains ticket");
								String newId = "";
								try {
									newId = this.setPublicReferenceToTrue(ticket.getTicket());
									this.deleteReviewStatus(ticket.getReference());
									this.deleteTicket(ticket.getTicket());

									try {
										this.sendAcceptedMail(newId, ticket.getEmailNotificationAddress(),
												this.createLandingPageURL(ticket.getReference()), publicReference);
									} catch (final EdalApprovalException e) {
										throw new EdalApprovalException("unable to send Accepted-Email: " + e.getMessage(),
												e.getCause());
									}

									ApprovalServiceProviderImplementation.synchronizedMap.remove(ticket.getTicket());
								} catch (final EdalException e) {
									throw new EdalApprovalException(
											"unable to set PublicReference to TRUE: " + e.getMessage(), e.getCause());
								}

								
							}

						} else {
							try {
								this.setPublicReferenceToFalse(ticket.getTicket());
							} catch (final EdalApprovalException e) {
								throw new EdalApprovalException(
										"unable to set PublicReference to FALSE: " + e.getMessage(), e.getCause());
							}

							/** collect information for reject email */
							String message;
							try {
								message = ApprovalServiceProviderImplementation.velocityGenerator
										.generateRejectedEmail(publicReference).toString();
							} catch (final VelocityException e) {
								throw new EdalApprovalException(
										ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
							}
							String title = publicReference.getVersion().getMetaData().toString();
							String emailAdress = ticket.getEmailNotificationAddress();
							/**********************************************************************************/

							this.deleteReviewStatus(ticket.getReference());
							this.deleteTicket(ticket.getTicket());

							ApprovalServiceProviderImplementation.synchronizedMap.remove(ticket.getTicket());

							ServiceProvider service = null;

							try {
								service = DataManager.getImplProv().getServiceProvider().newInstance();
							} catch (InstantiationException | IllegalAccessException e) {
								DataManager.getImplProv().getLogger().error(e);
								throw new EdalApprovalException(
										"Unable to load ApprovalServiceProvider: " + e.getMessage(), e.getCause());
							}
							service.cleanUpForRejectedEntities();

							/** after final clean up send reject email */
							try {
								this.sendRejectedMail(message, emailAdress, title);
							} catch (final EdalApprovalException e) {
								throw new EdalApprovalException("unable to send Rejected-Email: " + e.getMessage(),
										e.getCause());
							}
							/**********************************************************************************/
						}
					}
				}
			}

			break;

		case REJECTED:

			// System.out.println("Reference was rejected !!!");
			synchronized (ApprovalServiceProviderImplementation.synchronizedMap) {
				try {
					this.setPublicReferenceToFalse(ticket.getTicket());
				} catch (final EdalApprovalException e) {
					throw new EdalApprovalException("unable to set PublicReference to FALSE: " + e.getMessage(),
							e.getCause());
				}

				/** collect information for reject email */
				String message;
				try {
					message = ApprovalServiceProviderImplementation.velocityGenerator
							.generateRejectedEmail(publicReference).toString();
				} catch (final VelocityException e) {
					throw new EdalApprovalException(ApprovalServiceProviderImplementation.UNABLE_TO_GENERATE_E_MAIL, e);
				}
				String title = publicReference.getVersion().getMetaData().toString();
				String emailAdress = ticket.getEmailNotificationAddress();
				/**********************************************************************************/

				this.deleteReviewStatus(ticket.getReference());
				this.deleteTicket(ticket.getTicket());

				ApprovalServiceProviderImplementation.synchronizedMap.remove(ticket.getTicket());

				ServiceProvider service = null;

				try {
					service = DataManager.getImplProv().getServiceProvider().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					DataManager.getImplProv().getLogger().error(e);
					throw new EdalApprovalException("Unable to load ApprovalServiceProvider: " + e.getMessage(),
							e.getCause());
				}
				service.cleanUpForRejectedEntities();

				/** after final clean up send reject email */
				try {
					this.sendRejectedMail(message, emailAdress, title);
				} catch (final EdalApprovalException e) {
					throw new EdalApprovalException("unable to send Rejected-Email: " + e.getMessage(), e.getCause());
				}
				/**********************************************************************************/
			}
			break;

		case UNDECIDED:

			// System.out.println("Reference is still undecided.");

			// send remainder notification to reviewer
			final Calendar now = Calendar.getInstance();

			if (now.getTimeInMillis() - ticket.getReminderEmailSend()
					.getTimeInMillis() > (long) ApprovalServiceProviderImplementation.REMINDER_CYCLE * 1000 * 60 * 60
							* 24) {
				// System.out
				// .println("Sending remainder notification to reviewer");
				for (final ReviewStatus reviewStatus : result.getReviewerStatusList()) {
					// send remainder email, if we reach remainder cycle
					if (reviewStatus.getStatusType().equals(ReviewStatusType.UNDECIDED)) {
						try {
							this.sendRequestApprovalMail(ticket.getTicket(), ticket.getReference(),
									reviewStatus.getEmailAddress(), reviewStatus.getReviewerType());
						} catch (final EdalApprovalException e) {
							throw new EdalApprovalException(
									"unable to send notification email to reviewer " + reviewStatus.getEmailAddress(),
									e.getCause());
						}
					}

				}
				ticket.setReminderEmailSend(now);
				this.updateTicket(ticket);
			}

			break;

		default:
			break;
		}

	}

	/**
	 * Update the ticket into database.
	 * 
	 * @param ticket
	 *            the ticket to update
	 */
	private void updateTicket(final TicketImplementation ticket) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final Transaction transaction = session.beginTransaction();
		session.update(ticket);
		transaction.commit();
		session.close();
	}
}