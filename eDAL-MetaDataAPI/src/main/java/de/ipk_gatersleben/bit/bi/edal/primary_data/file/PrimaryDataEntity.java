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

import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * Abstract PrimaryDataEntity class.
 * <p>
 * Super class for {@link PrimaryDataDirectory} and {@link PrimaryDataFile}.
 * 
 * @author lange
 * @author arendd
 */
public abstract class PrimaryDataEntity implements
		Comparable<PrimaryDataEntity> {

	private PrimaryDataEntityVersion currentVersion;

	private PrimaryDataDirectory parentDirectory;

	private String id;

	/**
	 * Constructor for PrimaryDataEntity.
	 */
	protected PrimaryDataEntity() {

	}

	/**
	 * Construct a {@link PrimaryDataEntity} object.
	 * 
	 * Set the {@link PrimaryDataEntityVersion} to the latest one.
	 * <p>
	 * If it is an initial {@link PrimaryDataEntityVersion} , a new default
	 * {@link MetaData} object is instantiated
	 * 
	 * @param name
	 *            the name for this object
	 * @param path
	 *            the parent {@link PrimaryDataDirectory} of this
	 *            {@link PrimaryDataEntity}.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to set current {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException
	 *             if no parent {@link PrimaryDataDirectory} is found.
	 * @throws MetaDataException
	 *             if the {@link MetaData} object of the parent
	 *             {@link PrimaryDataDirectory} is not clone-able.
	 */
	protected PrimaryDataEntity(final PrimaryDataDirectory path,
			final String name) throws PrimaryDataEntityVersionException,
			PrimaryDataDirectoryException, MetaDataException {

		this.setParentDirectory(path);

		MetaData metadata = null;

		if (this.getParentDirectory() == null) {
			metadata = this.getImplementationProvider()
					.createMetaDataInstance();
		} else {
			/**
			 * important: clone the meta data of the parentDirectory to get a
			 * new object with the same values
			 */
			try {
				metadata = this.getParentDirectory().getMetaData().clone();

			} catch (final CloneNotSupportedException e) {
				DataManager.getImplProv().getLogger().fatal(e.getMessage());
				throw new MetaDataException(
						"Can not clone metadata of parent directory: "
								+ e.getMessage());
			}
		}

		try {
			metadata.setElementValue(EnumDublinCoreElements.TITLE,
					new UntypedData(name));

			this.setCurrentVersion(new PrimaryDataEntityVersion(this, false,
					metadata));

		} catch (final Exception e) {
			DataManager.getImplProv().getLogger().fatal(e.getMessage());
			throw new PrimaryDataEntityVersionException(
					"Can not set current Version: " + e.getMessage());
		}
		this.setID(UUID.randomUUID().toString());
	}

	/**
	 * Add a public identifier to the currentVersion of this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @param identifierType
	 *            the type of the new Identifier.
	 * @throws PrimaryDataEntityException
	 *             if unable to add the {@link PublicReference} to this
	 *             {@link PrimaryDataEntity}.
	 */
	public void addPublicReference(final PersistentIdentifier identifierType)
			throws PrimaryDataEntityException {
		this.getCurrentVersion().addPublicReference(identifierType, this);
	}

	/**
	 * Internal function to commit a {@link PrimaryDataEntityVersion} to the
	 * storage back-end. Use the CREATED {@link EdalDate} of the current
	 * {@link PrimaryDataEntityVersion} and remove the UPDATED {@link EdalDate}
	 * to add a new one.
	 * 
	 * @param version
	 *            the {@link PrimaryDataEntityVersion} to store.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store the {@link PrimaryDataEntityVersion}.
	 */
	protected void commitVersion(final PrimaryDataEntityVersion version)
			throws PrimaryDataEntityVersionException {

		MetaData metaData = version.getMetaData();

		try {
			if (version.getRevision() == 0) {

				DateEvents dateEvents = new DateEvents("event");

				dateEvents.add(new EdalDate(version.getCreationDate(),
						EdalDatePrecision.MILLISECOND,
						EdalDate.STANDART_EVENT_TYPES.CREATED.name()));

				dateEvents.add(new EdalDate(version.getCreationDate(),
						EdalDatePrecision.MILLISECOND,
						EdalDate.STANDART_EVENT_TYPES.UPDATED.name()));

				metaData.setElementValue(EnumDublinCoreElements.DATE,
						dateEvents);
			} else {

				DateEvents dateEvents = metaData
						.getElementValue(EnumDublinCoreElements.DATE);

				Set<EdalDate> removeSet = new HashSet<EdalDate>();

				for (EdalDate edalDate : dateEvents.getSet()) {
					if (edalDate
							.getEvent()
							.toString()
							.equalsIgnoreCase(
									EdalDate.STANDART_EVENT_TYPES.UPDATED
											.toString())) {
						removeSet.add(edalDate);
					}
				}

				dateEvents.removeAll(removeSet);

				dateEvents.add(new EdalDate(version.getRevisionDate(),
						EdalDatePrecision.MILLISECOND,
						EdalDate.STANDART_EVENT_TYPES.UPDATED.name()));

				metaData.setElementValue(EnumDublinCoreElements.DATE,
						dateEvents);
			}

			version.setMetaData(metaData);

		} catch (final MetaDataException e) {
			throw new PrimaryDataEntityVersionException(
					"exception while set default metadata", e);
		}

		final Lock lock = this.currentVersion.getReadWriteLock().writeLock();
		if (lock.tryLock()) {
			try {
				this.storeVersion(version);
				this.setCurrentVersion(this.getVersions().last());
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Compares PrimaryDataEntity based on toLowerCase(getName());
	 */
	@Override
	public int compareTo(final PrimaryDataEntity other) {

		return this.getName().compareToIgnoreCase(other.getName());

	}

	/**
	 * Delete the {@link PrimaryDataEntity} object by generation of new
	 * {@link PrimaryDataEntityVersion} and set the isDeleted flag to
	 * <code>true</code>.
	 * <p>
	 * <em>NOTE: It is not allowed delete the root {@link PrimaryDataDirectory}!</em>
	 * <p>
	 * <em>NOTE: It is not allowed delete a non-current {@link PrimaryDataEntityVersion}!</em>
	 * 
	 * @throws PrimaryDataEntityVersionException
	 *             if trying to delete the root {@link PrimaryDataDirectory}
	 * @throws PrimaryDataDirectoryException
	 *             if trying to delete a non-current
	 *             {@link PrimaryDataEntityVersion}.
	 */
	public final void delete() throws PrimaryDataEntityVersionException,
			PrimaryDataDirectoryException {

		if (this.getParentDirectory() == null) {
			throw new PrimaryDataDirectoryException(
					"Can not delete root directory !");
		}

		final PrimaryDataEntityVersion lastVersion = this.getVersions().last();

		if (this.getCurrentVersion().compareTo(this.getVersions().last()) != 0) {
			throw new PrimaryDataEntityVersionException(
					"Can not delete non-current version !");
		}
		final PrimaryDataEntityVersion newFileVersion = new PrimaryDataEntityVersion(
				this, true, lastVersion.getMetaData());
		this.commitVersion(newFileVersion);

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
		if (!(obj instanceof PrimaryDataEntity)) {
			return false;
		}
		final PrimaryDataEntity other = (PrimaryDataEntity) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for the current {@link PrimaryDataEntityVersion}.
	 * 
	 * @return the currently set {@link PrimaryDataEntityVersion}
	 * 
	 */
	public PrimaryDataEntityVersion getCurrentVersion() {
		return this.currentVersion;
	}

	/**
	 * Getter for the ID of this {@link PrimaryDataEntity}.
	 * 
	 * @see UUID
	 * 
	 * @return the ID of this {@link PrimaryDataEntity}
	 * 
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Getter for the current {@link ImplementationProvider}.
	 * 
	 * @return the {@link ImplementationProvider} from current thread
	 */
	protected ImplementationProvider getImplementationProvider() {
		return DataManager.getImplProv();
	}

	/**
	 * Getter for the {@link MetaData} object of the current
	 * {@link PrimaryDataEntityVersion} of this {@link PrimaryDataEntity}.
	 * 
	 * @return {@link MetaData} object of the current
	 *         {@link PrimaryDataEntityVersion} of this
	 *         {@link PrimaryDataEntity}.
	 */
	public MetaData getMetaData() {

		return this.getCurrentVersion().getMetaData();

	}

	/**
	 * Getter for the name of this {@link PrimaryDataEntity} .
	 * 
	 * @return the file name as delegator to
	 * 
	 *         <pre>
	 * this.getMetaData().getElementValue(EnumDublinCoreElements.TITLE)
	 * </pre>
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
	 * Getter for the parent {@link PrimaryDataDirectory} of this
	 * {@link PrimaryDataEntity} .
	 * 
	 * <p>
	 * <em>NOTE: It is not allowed to access the parent {@link PrimaryDataDirectory} of the root {@link PrimaryDataDirectory}!</em>
	 * 
	 * @return the parent {@link PrimaryDataDirectory} of this
	 *         {@link PrimaryDataEntity}.
	 * 
	 * @throws PrimaryDataDirectoryException
	 *             if trying to access the parent {@link PrimaryDataDirectory}
	 *             of the root {@link PrimaryDataDirectory}.
	 * 
	 */
	public PrimaryDataDirectory getParentDirectory()
			throws PrimaryDataDirectoryException {
		if (this.parentDirectory == null) {
			throw new PrimaryDataDirectoryException(
					"Can not access parent directory of root directory !");
		} else {
			if (this.getCurrentVersion() == null) {
				// do nothing --> only the case if constructor call
			} else {
				try {

					this.parentDirectory
							.switchCurrentVersion(this.parentDirectory
									.getVersionByDate(this.currentVersion
											.getRevisionDate()));

				} catch (PrimaryDataEntityVersionException e) {
					throw new PrimaryDataDirectoryException(
							"Can not switch to correct parent version");
				}
				return this.parentDirectory;
			}
			return this.parentDirectory;
		}
	}

	/**
	 * Getter for the path of this {@link PrimaryDataEntity} as {@link String}.
	 * 
	 * @return the complete path of the {@link PrimaryDataEntity} object as
	 *         {@link String}
	 */
	public String getPath() {
		/** catch NullPointerException when arrived rootDirectory */
		try {
			return this.getParentDirectory().getPath()
					+ PrimaryDataDirectory.PATH_SEPARATOR + this.getName();
		} catch (final PrimaryDataDirectoryException e) {
			return PrimaryDataDirectory.PATH_SEPARATOR + this.getName();
		} catch (final NullPointerException e) {
			return PrimaryDataDirectory.PATH_SEPARATOR;
		}
	}

	/**
	 * Getter for a {@link List} containing all {@link EdalPermission} objects.
	 * 
	 * @return an unmodifiable {@link List} containing all
	 *         {@link EdalPermission}
	 * @throws PrimaryDataEntityException
	 *             if unable to load all permissions.
	 */
	public Map<Principal, List<EdalPermission>> getPermissions()
			throws PrimaryDataEntityException {
		return Collections.unmodifiableMap(this.getPermissionsImpl());
	}

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataEntity#getPermissions()}.
	 * 
	 * @throws PrimaryDataEntityException
	 *             if unable to load all permissions.
	 * @return an unmodifiable {@link List} containing all
	 *         {@link EdalPermission}
	 */
	protected abstract Map<Principal, List<EdalPermission>> getPermissionsImpl()
			throws PrimaryDataEntityException;

	/**
	 * Getter for a {@link List} of all stored {@link PublicReference}s of the
	 * current {@link PrimaryDataEntityVersion} of this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @return a {@link List} with all stored {@link PublicReference}s of the
	 *         current {@link PrimaryDataEntityVersion} of this
	 *         {@link PrimaryDataEntity}.
	 */
	public List<PublicReference> getPublicReferences() {
		return this.getCurrentVersion().getPublicReferences();
	}

	/**
	 * Getter for the current {@link Subject}.
	 * 
	 * @return the {@link Subject} from current thread.
	 */
	protected Subject getSubject() {
		return DataManager.getSubject();
	}

	/**
	 * Convenience function to get a {@link PrimaryDataEntityVersion} of this
	 * {@link PrimaryDataEntity} by the revision date.
	 * 
	 * @param date
	 *            the date of the {@link PrimaryDataEntityVersion}
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws PrimaryDataEntityVersionException
	 *             if there are only older {@link PrimaryDataEntityVersion}
	 *             stored for this {@link PrimaryDataEntity}
	 */
	public PrimaryDataEntityVersion getVersionByDate(final Calendar date)
			throws PrimaryDataEntityVersionException {

		if (date == null) {
			throw new PrimaryDataEntityVersionException(
					"The given date is null !");
		}

		final SortedSet<PrimaryDataEntityVersion> versions = this.getVersions();

		PrimaryDataEntityVersion version = null;

		for (final PrimaryDataEntityVersion primaryDataEntityVersion : versions) {

			if (primaryDataEntityVersion.getRevisionDate().before(date)
					|| primaryDataEntityVersion.getRevisionDate().equals(date)) {
				version = primaryDataEntityVersion;
			} else {
				break;
			}
		}

		if (version == null) {
			throw new PrimaryDataEntityVersionException(
					"All version are older than the given version");
		}

		return version;

	}

	/**
	 * Convenience function to get a {@link PrimaryDataEntityVersion} of this
	 * {@link PrimaryDataEntity} by the revision number.
	 * 
	 * @param revisionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @return the specified {@link PrimaryDataEntityVersion}
	 * @throws PrimaryDataEntityVersionException
	 *             if there is no {@link PrimaryDataEntityVersion} with this
	 *             version stored.
	 */
	public PrimaryDataEntityVersion getVersionByRevisionNumber(
			final long revisionNumber) throws PrimaryDataEntityVersionException {

		final SortedSet<PrimaryDataEntityVersion> versions = this.getVersions();

		for (final PrimaryDataEntityVersion primaryDataEntityVersion : versions) {
			final Long version = primaryDataEntityVersion.getRevision();
			if (version.equals(revisionNumber)) {
				return primaryDataEntityVersion;
			}
		}
		throw new PrimaryDataEntityVersionException(
				"found no version with number '" + revisionNumber + "' !");

	}

	/**
	 * Getter for a {@link SortedSet} containing all
	 * {@link PrimaryDataEntityVersion} objects of this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @return an unmodifiable {@link SortedSet} containing all
	 *         {@link PrimaryDataEntity}
	 */
	public SortedSet<PrimaryDataEntityVersion> getVersions() {
		return Collections.unmodifiableSortedSet(this.getVersionsImpl());

	}

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataEntity#getVersions()}.
	 * 
	 * @return an unmodifiable {@link SortedSet} containing all
	 *         {@link PrimaryDataEntity}.
	 */
	protected abstract SortedSet<PrimaryDataEntityVersion> getVersionsImpl();

	/**
	 * Grant a {@link EdalPermission} to a {@link Principal} and check before
	 * grant if the method exists.
	 * 
	 * @param principal
	 *            to grant this method
	 * @param method
	 *            {@link Enum} for a the method
	 * @throws PrimaryDataEntityException
	 *             if can not found method to grant.
	 */
	public void grantPermission(final Principal principal, final Methods method)
			throws PrimaryDataEntityException {

		final Class<? extends PermissionProvider> permProv = this
				.getImplementationProvider().getPermissionProvider();

		/** grantPermission(Principal,ALL) */
		if (method.equals(Methods.ALL)) {
			try {
				permProv.getMethod("grantPermission", String.class,
						String.class, PrimaryDataEntity.class).invoke(
						permProv.getDeclaredConstructor().newInstance(),
						principal.getClass().getSimpleName(),
						principal.getName(), this);
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				throw new PrimaryDataEntityException(
						"Unable to call grantPermission() function", e);
			}

		}
		/** grantPermission(Principal, Method) */
		else {
			try {
				@SuppressWarnings("unchecked")
				final EdalPermission edalPermission = new EdalPermission(
						this.getID(), this.getCurrentVersion().getRevision(),
						(Class<? extends PrimaryDataEntity>) method
								.getImplClass(this.getClass())
								.getDeclaringClass(), method.getImplClass(this
								.getClass()));

				permProv.getMethod("grantPermission", String.class,
						String.class, EdalPermission.class).invoke(
						permProv.getDeclaredConstructor().newInstance(),
						principal.getClass().getSimpleName(),
						principal.getName(), edalPermission);
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				throw new PrimaryDataEntityException(
						"Unable to call grantPermission() function", e);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Check if the {@link PrimaryDataEntity} is a {@link PrimaryDataDirectory}
	 * or not.
	 * 
	 * @return <code>true</code> when it is a {@link PrimaryDataDirectory};<br>
	 *         <code>false</code> otherwise.
	 */
	public boolean isDirectory() {
		try {
			if (this.getMetaData().getElementValue(EnumDublinCoreElements.TYPE)
					.toString().equals(MetaData.DIRECTORY.toString())) {
				return true;
			}
		} catch (final MetaDataException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Move this {@link PrimaryDataEntity} to another
	 * {@link PrimaryDataDirectory}.
	 * <p>
	 * <em>NOTE: It is not allowed to move the root {@link PrimaryDataDirectory}!</em>
	 * 
	 * @param destinationDirectory
	 *            the new parent {@link PrimaryDataDirectory} for this
	 *            {@link PrimaryDataEntity}.
	 * @throws PrimaryDataDirectoryException
	 *             if trying to move the root {@link PrimaryDataDirectory}.
	 */
	public void move(final PrimaryDataDirectory destinationDirectory)
			throws PrimaryDataDirectoryException {

		if (this.getParentDirectory() == null) {
			throw new PrimaryDataDirectoryException(
					"You can not move the root directory !");
		}
		this.moveImpl(destinationDirectory);
	}

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataEntity#move(PrimaryDataDirectory)}.
	 * 
	 * @param destinationDirectory
	 *            the new {@link PrimaryDataDirectory} for this
	 *            {@link PrimaryDataEntity}.
	 */
	protected abstract void moveImpl(
			final PrimaryDataDirectory destinationDirectory);

	/**
	 * Rename the current {@link PrimaryDataEntity}.
	 * <p>
	 * <em>NOTE: Can not rename if another {@link PrimaryDataEntity} with this name already exists!</em>
	 * 
	 * @param name
	 *            the new name for this {@link PrimaryDataEntity}.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to set the new {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException
	 *             if an {@link PrimaryDataEntity} with this name already
	 *             exists.
	 */
	public final void rename(final String name)
			throws PrimaryDataEntityVersionException,
			PrimaryDataDirectoryException {

		if (this.parentDirectory.exist(name)) {
			throw new PrimaryDataDirectoryException("Can not rename, object '"
					+ name + "' already exists !");
		}

		MetaData metadata = null;
		try {
			metadata = this.getMetaData().clone();
		} catch (final CloneNotSupportedException e) {
			throw new PrimaryDataEntityVersionException("Unable to clone", e);
		}
		try {
			metadata.setElementValue(EnumDublinCoreElements.TITLE,
					new UntypedData(name));
			this.setMetaData(metadata);

		} catch (final MetaDataException e) {
			throw new PrimaryDataEntityVersionException("Can not rename", e);
		}

	}

	/**
	 * Revoke a {@link EdalPermission} to a {@link Principal} and check before
	 * revoke if the method exists.
	 * <p>
	 * <em>NOTE: Can not revoke 
	 * {@link PrimaryDataEntity#grantPermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)} 
	 * method for your own {@link PrimaryDataEntity}!</em>
	 * 
	 * @param principal
	 *            to revoke this method.
	 * @param method
	 *            {@link Enum} for a the method.
	 * @throws PrimaryDataEntityException
	 *             if trying to revoke the {@link EdalPermission} for
	 *             {@link PrimaryDataEntity#grantPermission(Principal, de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods)}
	 *             of your own {@link PrimaryDataEntity}.
	 */
	public void revokePermission(final Principal principal, final Methods method)
			throws PrimaryDataEntityException {

		/** get Principal of the current user; loop breaks after first Principal */
		Principal currentPrincipal = null;

		for (final Principal p : this.getSubject().getPrincipals()) {
			currentPrincipal = p;
			break;
		}
		if (currentPrincipal == null) {
			throw new PrimaryDataEntityException(
					"could not get the current pricipal from the authenticated subject");
		}
		if (currentPrincipal.equals(principal)
				&& (method.equals(Methods.grantPermission) || method
						.equals(Methods.ALL))) {
			throw new PrimaryDataEntityException(
					"you couldn't revoke the method grantPermission for your own Entity");
		}

		final Class<? extends PermissionProvider> permProv = this
				.getImplementationProvider().getPermissionProvider();

		/** revokePermission(Principal,ALL) */
		if (method.equals(Methods.ALL)) {

			try {
				permProv.getMethod("revokePermission", String.class,
						String.class, PrimaryDataEntity.class).invoke(
						permProv.getDeclaredConstructor().newInstance(),
						principal.getClass().getSimpleName(),
						principal.getName(), this);
			} catch (final Exception e) {
				throw new PrimaryDataEntityException(
						"unable to call revokePermission() function:" + e);
			}
		}
		/** revokePermission(Principal,Method) */
		else {
			try {
				@SuppressWarnings("unchecked")
				final EdalPermission edalPermission = new EdalPermission(this
						.getID().toString(), this.getCurrentVersion()
						.getRevision(),
						(Class<? extends PrimaryDataEntity>) method
								.getImplClass(this.getClass())
								.getDeclaringClass(), method.getImplClass(this
								.getClass()));

				permProv.getMethod("revokePermission", String.class,
						String.class, EdalPermission.class).invoke(
						permProv.newInstance(),
						principal.getClass().getSimpleName(),
						principal.getName(), edalPermission);
			} catch (final Exception e) {
				throw new PrimaryDataEntityException(
						"unable to call revokePermission() function:" + e);
			}
		}
	}

	/**
	 * Setter for the current {@link PrimaryDataEntityVersion}.
	 * 
	 * @param version
	 *            the new current {@link PrimaryDataEntityVersion} object.
	 */
	protected void setCurrentVersion(final PrimaryDataEntityVersion version) {
		this.currentVersion = version;
	}

	/**
	 * Set initial and default {@link EdalPermission} for this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @throws PrimaryDataEntityException
	 *             if unable to set the permissions.
	 */
	protected final void setDefaultPermissions()
			throws PrimaryDataEntityException {

		/** get Principal of the current user; loop breaks after first Principal */
		Principal principal = null;

		for (final Principal p : this.getSubject().getPrincipals()) {
			principal = p;
			break;
		}

		/** grant Permissions for all methods of this class; */
		for (final Methods method : GrantableMethods.ENTITY_METHODS) {
			try {
				this.grantPermission(principal, method);
			} catch (final PrimaryDataEntityException e) {
				throw new PrimaryDataEntityException(
						"Unable to set default entity permissions", e);
			}
		}

		if (this.isDirectory()) {
			for (final Methods method : GrantableMethods.DIRECTORY_METHODS) {
				try {
					this.grantPermission(principal, method);
				} catch (final PrimaryDataEntityException e) {
					throw new PrimaryDataEntityException(
							"Unable to set default directory permissions", e);
				}
			}
		}

		else {
			for (final Methods method : GrantableMethods.FILE_METHODS) {
				try {
					this.grantPermission(principal, method);
				} catch (final PrimaryDataEntityException e) {
					throw new PrimaryDataEntityException(
							"Unable to set default file permissions", e);
				}
			}
		}

		/**
		 * set default session wide permission (set by
		 * DataManager.setDefaultPermissions)
		 */
		this.setUserPermissions();
	}

	/**
	 * Setter for the ID of the current {@link PrimaryDataEntity}
	 * 
	 * @param id
	 *            the ID to set
	 */
	protected void setID(final String id) {
		this.id = id;
	}

	/**
	 * Internal function to reload a data type of the old {@link MetaData} for
	 * the current version, after the user set a wrong value.
	 * 
	 * @param element
	 *            the Element to reload from the old {@link MetaData}
	 * @return the original value of the given element.
	 * 
	 * @throws MetaDataException
	 *             if unable to reload the old {@link MetaData}.
	 */
	protected UntypedData reloadOldDataType(EnumDublinCoreElements element)
			throws MetaDataException {
		MetaData oldMetaData = null;
		try {
			oldMetaData = DataManager
					.getImplProv()
					.reloadPrimaryDataEntityByID(this.getID(),
							this.getCurrentVersion().getRevision())
					.getMetaData();
		} catch (EdalException e) {
			throw new MetaDataException("unable to reload the old metadata: "
					+ e.getMessage(), e);

		}
		this.getCurrentVersion().getMetaData()
				.setElementValue(element, oldMetaData.getElementValue(element));

		return oldMetaData.getElementValue(element);
	}

	/**
	 * Setter for the {@link MetaData} object of this {@link PrimaryDataEntity}
	 * object.
	 * 
	 * Create a new {@link PrimaryDataEntityVersion} with a new {@link MetaData}
	 * set.
	 * 
	 * @param newMetadata
	 *            the new {@link MetaData} object to set.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store {@link PrimaryDataEntityVersion}.
	 * @throws MetaDataException
	 *             if there are non valid value for element in the
	 *             {@link MetaData} object.
	 */
	public void setMetaData(final MetaData newMetadata)
			throws PrimaryDataEntityVersionException, MetaDataException {

		/** always clone the meta data to generate a new object */
		MetaData cloneNewMetaData = null;
		try {
			cloneNewMetaData = newMetadata.clone();
		} catch (CloneNotSupportedException e) {
			throw new MetaDataException("unable to clone: " + e.getMessage(), e);
		}

		MetaData oldMetaData = null;
		try {
			oldMetaData = DataManager
					.getImplProv()
					.reloadPrimaryDataEntityByID(this.getID(),
							this.getCurrentVersion().getRevision())
					.getMetaData();
		} catch (EdalException e) {
			throw new MetaDataException("unable to reload metadata: "
					+ e.getMessage(), e);
		}

		checkDateEventConsitency(oldMetaData, cloneNewMetaData);

		/** store only "new" meta data objects */
		if (!oldMetaData.equals(cloneNewMetaData)) {

			final PrimaryDataEntityVersion newVersion = new PrimaryDataEntityVersion(
					this, false, cloneNewMetaData);
			this.commitVersion(newVersion);

		} else {
			DataManager.getImplProv().getLogger()
					.debug("tried to set the same metadata again !");
		}
	}

	/**
	 * Internal function to check if the user did not overwrite/modify existing
	 * DataEvents.
	 * 
	 * @param oldMetaData
	 *            the current {@link MetaData}.
	 * @param newMetaData
	 *            the new {@link MetaData} to set
	 * @throws MetaDataException
	 *             if any value of the new {@link Metadata} is not allowed.
	 */
	private void checkDateEventConsitency(MetaData oldMetaData,
			MetaData newMetaData) throws MetaDataException {

		DateEvents oldDateEvents = oldMetaData
				.getElementValue(EnumDublinCoreElements.DATE);
		DateEvents newDateEvents = newMetaData
				.getElementValue(EnumDublinCoreElements.DATE);

		boolean consistCreateDate = false;

		boolean consistModifiedDate = false;

		for (EdalDate edalDate : newDateEvents) {
			if (edalDate.getEvent().equalsIgnoreCase(
					EdalDate.STANDART_EVENT_TYPES.CREATED.toString())) {
				consistCreateDate = true;
			}
			if (edalDate.getEvent().equalsIgnoreCase(
					EdalDate.STANDART_EVENT_TYPES.UPDATED.toString())) {
				consistModifiedDate = true;
			}
		}
		if (!consistCreateDate) {

			reloadOldDataType(EnumDublinCoreElements.DATE);

			throw new MetaDataException(
					"not allowed to set a DateEvent with no CREATED date");
		}
		if (!consistModifiedDate && this.currentVersion.getRevision() != 0) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.DATE);

			throw new MetaDataException(
					"not allowed to set a DateEvent with no UPDATED date ! Rollback to "
							+ originalData);
		}

		DateEvents tempDateEvents = new DateEvents("temp");

		if (!oldDateEvents.getSet().equals(newDateEvents.getSet())) {

			for (EdalDate newEdalDate : newDateEvents) {
				if (!oldDateEvents.contains(newEdalDate)) {
					tempDateEvents.add(newEdalDate);
				}

			}

			for (EdalDate edalDate : tempDateEvents) {
				if (edalDate.getEvent().equalsIgnoreCase(
						EdalDate.STANDART_EVENT_TYPES.CREATED.toString())) {

					UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.DATE);

					throw new MetaDataException(
							"it is not allowed to set or change the CREATED Date of an object ! Rollback to "
									+ originalData);
				}
				if (edalDate.getEvent().equalsIgnoreCase(
						EdalDate.STANDART_EVENT_TYPES.UPDATED.toString())) {

					UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.DATE);

					throw new MetaDataException(
							"it is not allowed to set or change the UPDATED Date of an object ! Rollback to "
									+ originalData);
				}
			}

		}

	}

	/**
	 * Setter for the parent {@link PrimaryDataDirectory} of this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @param parentDirectory
	 *            the parent {@link PrimaryDataDirectory} to set
	 */
	protected void setParentDirectory(final PrimaryDataDirectory parentDirectory) {
		this.parentDirectory = parentDirectory;
	}

	/**
	 * Set the user specified {@link EdalPermission} for this
	 * {@link PrimaryDataEntity}.
	 * 
	 * @throws PrimaryDataEntityException
	 *             if unable to set user permissions
	 */
	private void setUserPermissions() throws PrimaryDataEntityException {

		final Map<Principal, List<Methods>> permissions = DataManager
				.getDefaultPermissions();

		if (permissions != null) {
			for (final Map.Entry<Principal, List<Methods>> entry : permissions
					.entrySet()) {
				for (final Methods method : entry.getValue()) {
					if (GrantableMethods.ENTITY_METHODS.contains(method)) {
						try {
							this.grantPermission(entry.getKey(), method);
						} catch (final PrimaryDataEntityException e) {
							throw new PrimaryDataEntityException(
									"Unable to set user permissions :"
											+ e.getMessage());
						}
					} else if (this.isDirectory()
							&& GrantableMethods.DIRECTORY_METHODS
									.contains(method)) {
						try {
							this.grantPermission(entry.getKey(), method);
						} catch (final PrimaryDataEntityException e) {
							throw new PrimaryDataEntityException(
									"Unable to set user permissions :"
											+ e.getMessage());
						}
					} else if (!this.isDirectory()
							&& GrantableMethods.FILE_METHODS.contains(method)) {
						try {
							this.grantPermission(entry.getKey(), method);
						} catch (final PrimaryDataEntityException e) {
							throw new PrimaryDataEntityException(
									"Unable to set user permissions :"
											+ e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Abstract function for storing a new {@link PrimaryDataEntityVersion} for
	 * an {@link PrimaryDataEntity} object.
	 * 
	 * @param newVersion
	 *            the new {@link PrimaryDataEntityVersion} to store.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store {@link PrimaryDataEntityVersion}.
	 */
	protected abstract void storeVersion(PrimaryDataEntityVersion newVersion)
			throws PrimaryDataEntityVersionException;

	/**
	 * Switch the current {@link PrimaryDataEntityVersion} with this
	 * {@link PrimaryDataEntityVersion}.
	 * <p>
	 * <em>NOTE: Can not switch when the current {@link PrimaryDataEntityVersion} is marked as deleted !</em>
	 * <p>
	 * <em>NOTE: Can not switch when the {@link PrimaryDataEntityVersion} not in the {@link SortedSet} of this {@link PrimaryDataEntity} !</em>
	 * 
	 * @param version
	 *            the new {@link PrimaryDataEntityVersion} to set as current
	 *            {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataEntityVersionException
	 *             if requested {@link PrimaryDataEntityVersion} is not
	 *             available or marked as deleted.
	 */
	public void switchCurrentVersion(final PrimaryDataEntityVersion version)
			throws PrimaryDataEntityVersionException {

		if (!this.getVersions().contains(version)) {
			throw new PrimaryDataEntityVersionException(
					"Requested version not available !");
		}
		/**
		 * if (!this.getVersions().last().equals(version) &&
		 * this.getVersions().last().isDeleted()) { throw new
		 * PrimaryDataEntityVersionException(
		 * "Requested version is marked as deleted !"); }
		 */
		this.setCurrentVersion(version);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getName();
	}
}