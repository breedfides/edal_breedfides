/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * Store version information of a {@link PrimaryDataEntity}
 * 
 * @author lange
 * @author arendd
 */
public class PrimaryDataEntityVersion implements
		Comparable<PrimaryDataEntityVersion>, Serializable {

	private static final long serialVersionUID = 1L;

	private Calendar creationDate;
	private long revision;
	private boolean isDeleted;
	private Calendar revisionDate;
	private MetaData metaData;
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private List<PublicReference> publicReferences;
	private PrimaryDataEntity entity;
	private Principal owner;

	/**
	 * non public constructor for internal use only
	 */
	protected PrimaryDataEntityVersion() {
		this.creationDate = Calendar.getInstance();
		this.revision = 0;
		this.isDeleted = false;
		this.revisionDate = this.creationDate;
		this.metaData = null;
		this.publicReferences = new ArrayList<PublicReference>(0);
	}

	/**
	 * Construct a new {@link PrimaryDataEntityVersion} for a
	 * {@link PrimaryDataEntity}.
	 * 
	 * <code>Revision</code> is increased from previous <code>revision</code>.
	 * 
	 * @param entity
	 *            the {@link PrimaryDataEntity} for this
	 *            {@link PrimaryDataEntityVersion}.
	 * @param delete
	 *            set this {@link PrimaryDataEntityVersion} to be deleted.
	 * @param metadata
	 *            the {@link MetaData} for this {@link PrimaryDataEntityVersion}
	 *            .
	 * @throws PrimaryDataEntityVersionException
	 *             if the last {@link PrimaryDataEntityVersion} is already
	 *             deleted.
	 */
	protected PrimaryDataEntityVersion(final PrimaryDataEntity entity,
			final boolean delete, final MetaData metadata)
			throws PrimaryDataEntityVersionException {
		this();
		this.metaData = metadata;
		if (entity.getVersions() == null || entity.getVersions().isEmpty()) {
			this.creationDate = Calendar.getInstance();
			this.revision = 0;
			this.isDeleted = delete;
			this.revisionDate = this.creationDate;

		} else {
			final PrimaryDataEntityVersion lastVersion = entity.getVersions()
					.last();
			if (lastVersion.isDeleted()) {
				throw new PrimaryDataEntityVersionException("entity "
						+ entity.getName() + " was already deleted at: "
						+ lastVersion.getRevisionDate());
			}
			this.creationDate = lastVersion.getCreationDate();
			this.revision = lastVersion.getRevision() + 1;
			this.revisionDate = Calendar.getInstance();
			this.isDeleted = delete;
			this.setEntity(entity);
		}
		
		for (Principal principal : DataManager.getSubject().getPrincipals()) {
			this.setOwner(principal);
			break;
		}
		
	}

	protected void addPublicReference(
			final PersistentIdentifier identifierType,
			final PrimaryDataEntity entity) throws PrimaryDataEntityException {

		final List<PublicReference> list = this.getPublicReferences();

		/**
		 * for (final PublicReference pub : list) { if
		 * (pub.getReferenceType().equals(identifierType)) { throw new
		 * PrimaryDataEntityException(
		 * "There is already a PublicReference of the type '" +
		 * identifierType.toString() + "' stored !"); } }
		 */
		PublicReference pub;
		try {
			pub = new PublicReference(this, identifierType);
		} catch (final EdalException e) {
			throw new PrimaryDataEntityException(
					"unable to create PublicReference: " + e.getMessage(), e);
		}

		if (list.contains(pub)) {
			throw new PrimaryDataEntityException(
					"There is already a PublicReference of the type '"
							+ identifierType.toString() + "' stored !");
		}

		PrimaryDataEntityVersion newFileVersion = null;

		try {

			newFileVersion = new PrimaryDataEntityVersion(entity, false, this
					.getMetaData().clone());

			for (final PublicReference publicReference : this
					.getPublicReferences()) {
				newFileVersion.publicReferences.add(publicReference);
			}

			newFileVersion.publicReferences.add(pub);

			entity.commitVersion(newFileVersion);

		} catch (final Exception e) {
			throw new PrimaryDataEntityException(
					"unable to store PublicReference", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Compares by <code>revision</code>
	 */
	@Override
	public int compareTo(final PrimaryDataEntityVersion o) {
		if (o.revision == this.revision) {
			return 0;
		}
		return this.revision < o.revision ? -1 : 1;
	}

	/**
	 * Getter for the field <code>creationDate</code>.
	 * 
	 * @return the creationDate.
	 */
	public Calendar getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Getter for the field <code>entity</code>.
	 * 
	 * @return entity.
	 */
	public PrimaryDataEntity getEntity() {
		return this.entity;
	}

	/**
	 * Convenience function to get the {@link EnumDublinCoreElements#TITLE} of
	 * the {@link MetaData} object
	 * 
	 * @return the name of the {@link PrimaryDataEntityVersion} from the
	 *         {@link MetaData}.
	 */
	public final String getName() {
		try {
			return this.getMetaData()
					.getElementValue(EnumDublinCoreElements.TITLE).toString();
		} catch (final MetaDataException e) {
			return "";
		}

	}

	/**
	 * Getter for the {@link MetaData} of this {@link PrimaryDataEntityVersion}.
	 * 
	 * @return the {@link MetaData} of this {@link PrimaryDataEntityVersion}.
	 */
	public MetaData getMetaData() {
		return this.metaData;
	}

	/**
	 * Return a specific PublicReference defined by the
	 * {@link PersistentIdentifier} type.
	 * 
	 * @param identifierType
	 *            of the {@link PublicReference}
	 * @return the found {@link PublicReference}
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PublicReference} with this
	 *             {@link PersistentIdentifier} defined.
	 */
	public PublicReference getPublicReference(
			PersistentIdentifier identifierType)
			throws PrimaryDataEntityVersionException {

		List<PublicReference> list = this.getPublicReferences();
		if (list.isEmpty()) {
			throw new PrimaryDataEntityVersionException(
					"no PublicReference of type '" + identifierType
							+ "' for this version defined");
		} else {
			for (PublicReference publicReference : list) {
				if (publicReference.getIdentifierType().equals(identifierType)) {
					return publicReference;
				}
			}
		}

		throw new PrimaryDataEntityVersionException(
				"no PublicReference of type '" + identifierType
						+ "' for this version defined");

	}

	/**
	 * @return read only {@link List} of {@link PublicReference}
	 */
	public List<PublicReference> getPublicReferences() {
		return Collections.unmodifiableList(this.publicReferences);
	}

	/**
	 * @return the ReadWritLock for access synchronizations.
	 */
	final ReentrantReadWriteLock getReadWriteLock() {
		return this.readWriteLock;
	}

	/**
	 * Getter for the field <code>revision</code>.
	 * 
	 * @return the revision.
	 */
	public long getRevision() {
		return this.revision;
	}

	/**
	 * Getter for the field <code>revisionDate</code>.
	 * 
	 * @return revisionDate.
	 */
	public Calendar getRevisionDate() {
		return this.revisionDate;
	}

	/**
	 * Getter for the {@link URL} of this {@link PrimaryDataEntityVersion} to
	 * access over the {@link EdalHttpServer}.
	 * 
	 * @return the {@link URL}
	 * @throws PrimaryDataEntityException
	 *             if unable to load the {@link URL}.
	 */
	public URL getURL() throws PrimaryDataEntityException {
		int port = 0;
		try {
			port = DataManager.getConfiguration().getHttpPort();
		} catch (EdalConfigurationException e) {
			throw new PrimaryDataEntityException("unable to get HTTP port", e);
		}
		final String requestURL = EdalHttpFunctions.EDAL.toString();
		final String pathSeparator = EdalHttpServer.EDAL_PATH_SEPARATOR;

		final InetSocketAddress addr = new InetSocketAddress(port);
		String hostName = "";
		try {
			addr.getAddress();
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (final UnknownHostException e) {
			throw new PrimaryDataEntityException("unable to load host URL", e);
		}
		URL url;
		try {

			String useSSL = "http://";
			if (DataManager.getConfiguration().isUseSSL()) {
				useSSL = "https://";
			}

			url = new URL(useSSL + hostName + ":" + port + pathSeparator
					+ requestURL + pathSeparator + this.getEntity().getID()
					+ pathSeparator + this.getRevision());
		} catch (final MalformedURLException e) {
			throw new PrimaryDataEntityException(
					"unable to generate version URL", e);
		}

		return url;
	}

	/**
	 * Check if this {@link PrimaryDataEntityVersion} is marked as deleted.
	 * 
	 * @return <code>true</code> if the {@link PrimaryDataEntityVersion} is
	 *         marked as deleted;<code>false</code> otherwise.
	 */
	public boolean isDeleted() {
		return this.isDeleted;
	}

	/**
	 * Set all {@link PublicReference}s of this {@link PrimaryDataEntityVersion}
	 * of this {@link PrimaryDataEntity} will be requested to set public.
	 * 
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user.
	 * @param releaseDate
	 *            the release date for the {@link PublicReference}s.
	 * @throws PublicReferenceException
	 *             if unable to request the {@link PublicReference} to set
	 *             public.
	 */
	public void setAllReferencesPublic(
			final InternetAddress emailNotificationAddress,
			final Calendar releaseDate) throws PublicReferenceException {

		for (final PublicReference publicReference : this.getPublicReferences()) {
			publicReference.setPublic(emailNotificationAddress, releaseDate);
		}
	}

	/**
	 * Set all {@link PublicReference}s of this {@link PrimaryDataEntityVersion}
	 * of this {@link PrimaryDataEntity} will be requested to set public without
	 * lock date.
	 * 
	 * @param emailNotificationAddress
	 *            the eMail address of the requesting user.
	 * @throws PublicReferenceException
	 *             if unable to request the {@link PublicReference} to set
	 *             public.
	 */
	public void setAllReferencesPublic(
			final InternetAddress emailNotificationAddress)
			throws PublicReferenceException {

		setAllReferencesPublic(emailNotificationAddress, null);
	}

	/**
	 * Setter for the field <code>creationDate</code>.
	 * 
	 * @param creationDate
	 *            the creationDate to set.
	 */
	protected void setCreationDate(final Calendar creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Setter for the <code>isDeleted</code> flag.
	 * 
	 * @param isDeleted
	 *            the isDeleted to set.
	 */
	protected void setDeleted(final boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Setter for the field <code>entity</code>.
	 * 
	 * @param entity
	 *            the entity to set
	 */
	protected void setEntity(final PrimaryDataEntity entity) {
		this.entity = entity;
	}

	/**
	 * Setter for the {@link MetaData} of this {@link PrimaryDataEntityVersion}.
	 * 
	 * @param metadata
	 *            the {@link MetaData} to set.
	 */
	protected void setMetaData(final MetaData metadata) {
		this.metaData = metadata;
	}

	protected void setPublicReferences(final List<PublicReference> references) {
		this.publicReferences = references;
	}

	/**
	 * Setter for the field <code>revision</code>.
	 * 
	 * @param revision
	 *            the revision to set
	 */
	protected void setRevision(final long revision) {
		this.revision = revision;
	}

	/**
	 * Setter for the field <code>revisionDate</code>.
	 * 
	 * @param revisionDate
	 *            the revisionDate to set.
	 */
	protected void setRevisionDate(final Calendar revisionDate) {
		this.revisionDate = revisionDate;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {

		String output = "";
		try {
			output = "PrimaryDataEntityVersion [creationDate="
					+ this.getCreationDate().getTime()
					+ ", revision="
					+ this.getRevision()
					+ ", isDeleted="
					+ this.isDeleted()
					+ ", revisionDate="
					+ this.getRevisionDate().getTime()
					+ ", metaData="
					+ this.getMetaData().getElementValue(
							EnumDublinCoreElements.TITLE) + "]";
		} catch (final MetaDataException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * @return the owner
	 */
	public Principal getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	protected void setOwner(Principal owner) {
		this.owner = owner;
	}
}