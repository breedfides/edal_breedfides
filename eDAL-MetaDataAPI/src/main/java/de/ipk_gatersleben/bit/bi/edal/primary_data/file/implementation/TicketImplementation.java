/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class to persist tickets for the ApprovalService with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity(name = "TICKETS")
public class TicketImplementation {

	private String ticket;
	private PublicReferenceImplementation reference;
	private String emailNotificationAddress;
	private Calendar reminderEmailSend;
	private Calendar userReminderEmailSend;
	private Boolean userAnswer;

	/**
	 * Default constructor for {@link TicketImplementation} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected TicketImplementation() {
	}

	/**
	 * Constructor for {@link TicketImplementation}.
	 * 
	 * @param ticket
	 *            the ticket number.
	 * @param publicReference
	 *            the corresponding {@link PublicReferenceImplementation}.
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user.
	 */
	protected TicketImplementation(final String ticket, final PublicReferenceImplementation publicReference, final String emailNotificationAddress) {
		super();
		this.setTicket(ticket);
		this.setReference(publicReference);
		this.setEmailNotificationAddress(emailNotificationAddress);
		this.setReminderEmailSend(Calendar.getInstance());
	}

	/**
	 * Getter for the field <code>emailNotificationAddress</code>.
	 * 
	 * @return the emailNotificationAddress
	 */
	protected String getEmailNotificationAddress() {
		return this.emailNotificationAddress;
	}

	/**
	 * Getter for the field <code>reference</code>.
	 * 
	 * @return the reference
	 */
	@OneToOne
	protected PublicReferenceImplementation getReference() {
		return this.reference;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getReminderEmailSend() {
		return this.reminderEmailSend == null ? Calendar.getInstance() : this.reminderEmailSend;
	}

	/**
	 * Getter for the field <code>ticket</code>.
	 * 
	 * @return the ticket
	 */
	@Id
	protected String getTicket() {
		return this.ticket;
	}

	/**
	 * Setter for the field <code>emailNotificationAddress</code>.
	 * 
	 * @param emailNotificationAddress
	 *            the emailNotificationAddress to set
	 */
	protected void setEmailNotificationAddress(final String emailNotificationAddress) {
		this.emailNotificationAddress = emailNotificationAddress;
	}

	/**
	 * Setter for the field <code>reference</code>.
	 * 
	 * @param reference
	 *            the reference to set
	 */
	protected void setReference(final PublicReferenceImplementation reference) {
		this.reference = reference;
	}

	public void setReminderEmailSend(final Calendar reminderEmailSend) {
		this.reminderEmailSend = reminderEmailSend;
	}

	/**
	 * Setter for the field <code>ticket</code>.
	 * 
	 * @param ticket
	 *            the ticket to set
	 */
	protected void setTicket(final String ticket) {
		this.ticket = ticket;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getUserReminderEmailSend() {
		return this.userReminderEmailSend;
	}

	public void setUserReminderEmailSend(Calendar userReminderEmailSend) {
		this.userReminderEmailSend = userReminderEmailSend;
	}

	public Boolean isUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(Boolean userAnswer) {
		this.userAnswer = userAnswer;
	}

}
