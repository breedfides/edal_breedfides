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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Calendar;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.Referenceable;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * Provide a public, long term stable ID for data referencing.
 * 
 * @author arendd
 */
public class PublicReference implements Serializable {

	private static final long serialVersionUID = 1L;

	private PrimaryDataEntityVersion version;
	protected PersistentIdentifier identifierType;
	private boolean isPublic;
	private Calendar creationDate;
	private Calendar requestedDate;
	private Calendar acceptedDate;
	private Calendar rejectedDate;
	private Calendar releaseDate;
	private PublicationStatus publicationStatus;
	private Principal requestedPrincipal;
	private String assignedID;
	private String internalID;

	/**
	 * Default constructor, only for internal use.
	 */
	protected PublicReference() {
		this.version = null;
		this.identifierType = null;
		this.isPublic = false;
		this.creationDate = Calendar.getInstance();
		this.assignedID = null;
		this.setPublicationStatus(PublicationStatus.SUBMITTED);
		this.releaseDate = null;
	}

	/**
	 * Constructor generate a new {@link PublicReference} object and use the
	 * class that belongs to the {@link PersistentIdentifier}. Before creating a
	 * new {@link PublicReference} object the constructor calls the
	 * {@link Referenceable#validateMetaData(PrimaryDataEntityVersion)}
	 * method.
	 * 
	 * @param entityVersion
	 *            the current {@link PrimaryDataEntityVersion} for this
	 *            {@link PublicReference}.
	 * @param identifierType
	 *            the type of the {@link PublicReference}.
	 * @throws EdalException
	 *             if unable to create new {@link PublicReference}.
	 */
	protected PublicReference(final PrimaryDataEntityVersion entityVersion, final PersistentIdentifier identifierType)
			throws EdalException {

		this();
		try {
			Method validateMethod = identifierType.getImplClass().getMethod("validateMetaData",
					PrimaryDataEntityVersion.class);

			validateMethod.invoke(identifierType.getImplClass().getDeclaredConstructor().newInstance(), entityVersion);

		} catch (ReflectiveOperationException | SecurityException |  IllegalArgumentException e) {
			throw new EdalException("unable to validate metadata: " + e.getCause().getMessage(), e);
		}
		this.setIdentifierType(identifierType);
		this.setVersion(entityVersion);

		/**
		 * get Principal of the current user; loop breaks after first Principal
		 */
		for (final Principal p : DataManager.getSubject().getPrincipals()) {
			this.setRequestedPrincipal(p);
			break;
		}
		this.setInternalID(UUID.randomUUID().toString());
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		/** if (getClass() != obj.getClass()) return false; */

		final PublicReference other = (PublicReference) obj;
		if (this.getIdentifierType() != other.getIdentifierType()) {
			return false;
		}
		// if (this.getCreationDate() != other.getCreationDate()) {
		// return false;
		// }
		// if (this.getRequestedDate() != other.getRequestedDate()) {
		// return false;
		// }
		// if (this.getAcceptedDate() != other.getAcceptedDate()) {
		// return false;
		// }
		// if (this.getRejectedDate() != other.getRejectedDate()) {
		// return false;
		// }
		// if (this.getPublicationStatus() != other.getPublicationStatus()) {
		// return false;
		// }
		return true;
	}

	/**
	 * Getter for the accepted Date of this {@link PublicReference}
	 * 
	 * @return the accepted date or null if the {@link PublicReference} was not
	 *         yet accepted.
	 */
	public Calendar getAcceptedDate() {
		return this.acceptedDate;
	}

	/**
	 * @return the assignedID
	 * 
	 * @throws PublicReferenceException
	 *             if no ID set
	 */
	public String getAssignedID() throws PublicReferenceException {
		if (this.assignedID == null) {
			throw new PublicReferenceException("id not yet assigned. please request approval first");
		} else {
			return this.assignedID;
		}
	}

	/**
	 * Getter for the creation Date of this {@link PublicReference}
	 * 
	 * @return the creation date.
	 */
	public Calendar getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Getter for the {@link PersistentIdentifier} of this
	 * {@link PublicReference}.
	 * 
	 * @return the {@link PersistentIdentifier} to this {@link PublicReference}.
	 */
	public PersistentIdentifier getIdentifierType() {
		return this.identifierType;
	}

	/**
	 * Function to check the publication status of this {@link PublicReference}.
	 * 
	 * @return the current status of the publication process of this
	 *         {@link PublicReference}.
	 */
	public PublicationStatus getPublicationStatus() {
		return this.publicationStatus;
	}

	/**
	 * Getter for the {@link Referenceable} type of this
	 * {@link PublicReference}:
	 * 
	 * @return the corresponding {@link Referenceable} object.
	 * @throws EdalException
	 *             if unable to load the {@link Referenceable} class.
	 */
	public Referenceable getReferencable() throws EdalException {
		try {
			return this.identifierType.getImplClass().getDeclaredConstructor().newInstance();
		} catch (final Exception e) {
			throw new EdalException("unable to load the class for the identifierType '" + this.identifierType + "' : "
					+ e.getMessage());
		}
	}

	/**
	 * Getter for the rejected Date of this {@link PublicReference}
	 * 
	 * @return the rejected date or null if the {@link PublicReference} was not
	 *         yet rejected.
	 */
	public Calendar getRejectedDate() {
		return this.rejectedDate;
	}

	/**
	 * Getter for the requested Date of this {@link PublicReference}
	 * 
	 * @return the requested date or null if the {@link PublicReference} was not
	 *         yet requested.
	 */
	public Calendar getRequestedDate() {
		return requestedDate;
	}

	/**
	 * Get the {@link Principal} of the person, who requested this
	 * {@link PublicReference}.
	 * 
	 * @return the {@link Principal} that requested this {@link PublicReference}
	 *         .
	 */
	public Principal getRequestedPrincipal() {
		return this.requestedPrincipal;
	}

	/**
	 * Getter for the {@link PrimaryDataEntityVersion} of this
	 * {@link PublicReference}.
	 * 
	 * @return the {@link PrimaryDataEntityVersion} to this
	 *         {@link PublicReference}.
	 */
	public PrimaryDataEntityVersion getVersion() {
		return this.version;
	}

	// /** {@inheritDoc} */
	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime
	// * result
	// + (this.getIdentifierType() == null ? 0 : this
	// .getIdentifierType().hashCode());
	// return result;
	// }

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.getInternalID() == null ? 0 : this.getInternalID().hashCode());
		return result;
	}

	/**
	 * Getter for the <code>isPublic</code> field. Convenience function for
	 * external use.
	 * 
	 * @return the <code>isPublic</code> field.
	 */
	public boolean isPublic() {
		return this.isPublic;
	}

	/**
	 * Setter for the accepted date of this {@link PublicReference}.
	 * 
	 * @param acceptedDate
	 *            the date to set.
	 */
	protected void setAcceptedDate(final Calendar acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	/**
	 * @param assignedID
	 *            the assignedID to set
	 */
	protected void setAssignedID(final String assignedID) {
		this.assignedID = assignedID;
	}

	/**
	 * Setter for the creation date of this {@link PublicReference}.
	 * 
	 * @param creationDate
	 *            the {@link java.util.Date} to set as creation date.
	 */
	protected void setCreationDate(final Calendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Setter for the {@link PersistentIdentifier} of this
	 * {@link PublicReference}.
	 * 
	 * @param identifierType
	 *            the {@link PersistentIdentifier} to set.
	 */
	private void setIdentifierType(final PersistentIdentifier identifierType) {
		this.identifierType = identifierType;
	}

	/**
	 * Setter for the <code>isPublic</code> field. Convenience function for
	 * external use.
	 * 
	 * @param emailNotificationAddress
	 *            the address for email notifications
	 * @param releaseDate
	 *            the release date
	 * @throws PublicReferenceException
	 *             if unable to request this {@link PublicReference} to set
	 *             public.
	 * 
	 */
	public void setPublic(final InternetAddress emailNotificationAddress, final Calendar releaseDate)
			throws PublicReferenceException {

		setReleaseDate(releaseDate);

		try {
			emailNotificationAddress.validate();
		} catch (final AddressException e) {
			throw new PublicReferenceException("could not validate eMail adress", e);
		}

		try {
			final ApprovalServiceProvider appService = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance();

			appService.approve(this, emailNotificationAddress);

		} catch (ReflectiveOperationException | IllegalArgumentException | EdalException e) {
			throw new PublicReferenceException("unable to request approval", e);
		}
	}

	/**
	 * Setter for the <code>isPublic</code> field and use the default Workgroup.
	 * Convenience function for external use.
	 * 
	 * @param emailNotificationAddress
	 *            the address for email notifications
	 * 
	 * @throws PublicReferenceException
	 *             if unable to request this {@link PublicReference} to set
	 *             public.
	 * 
	 */
	public void setPublic(final InternetAddress emailNotificationAddress) throws PublicReferenceException {

		setPublic(emailNotificationAddress, null);
	}

	/**
	 * Setter for the rejected date of this {@link PublicReference}.
	 * 
	 * @param rejectedDate
	 *            the date to set.
	 */
	protected void setRejectedDate(final Calendar rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	/**
	 * Setter for the requested date of this {@link PublicReference}.
	 * 
	 * @param requestedDate
	 *            the {@link java.util.Date} to set as requested Date.
	 */
	protected void setRequestedDate(Calendar requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * Setter for the {@link Principal} that requested this
	 * {@link PublicReference}.
	 * 
	 * @param requestedPrincipal
	 *            the {@link Principal} to set.
	 */
	protected void setRequestedPrincipal(final Principal requestedPrincipal) {
		this.requestedPrincipal = requestedPrincipal;
	}

	/**
	 * Setter for the {@link PrimaryDataEntityVersion} of this
	 * {@link PublicReference}.
	 * 
	 * @param version
	 *            the {@link PrimaryDataEntityVersion} to set.
	 */
	public void setVersion(final PrimaryDataEntityVersion version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "PublicReference [version=" + version + ", identifierType=" + identifierType + ", isPublic=" + isPublic
				+ ", creationDate=" + creationDate.getTime() + "]";
	}

	/**
	 * Internal setter for the {@link PublicationStatus} of this
	 * PublicReference.
	 * 
	 * @param publicationStatus
	 *            the publicationStatus to set
	 */
	protected void setPublicationStatus(PublicationStatus publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	/**
	 * @return the internalID
	 */
	public String getInternalID() {
		return internalID;
	}

	/**
	 * @param internalID
	 *            the internalID to set
	 */
	protected void setInternalID(String internalID) {
		this.internalID = internalID;
	}

	/**
	 * @return the releaseDate
	 */
	public Calendar getReleaseDate() {
		return releaseDate;
	}

	/**
	 * @param releaseDate
	 *            the releaseDate to set
	 */
	protected void setReleaseDate(Calendar releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void changeReleaseDate(Calendar releaseDate) {
		this.releaseDate = releaseDate;
	}

}