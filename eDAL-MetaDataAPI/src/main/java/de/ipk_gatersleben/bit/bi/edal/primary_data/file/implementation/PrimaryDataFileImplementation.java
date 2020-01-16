/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;

/**
 * Implementation of {@link PrimaryDataFile}.
 * 
 * @author arendd
 */

@Entity
@Table(name = "ENTITIES")
@DiscriminatorColumn(columnDefinition = "char(1)", name = "TYPE", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("F")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "PrimaryDataFile")
public class PrimaryDataFileImplementation extends PrimaryDataFile {

	private SortedSet<PrimaryDataEntityVersionImplementation> versionList;

	/**
	 * Default constructor for {@link PrimaryDataFileImplementation} is necessary
	 * for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected PrimaryDataFileImplementation() {
	}

	/**
	 * Constructor for PrimaryDataFileImplementation.
	 * 
	 * @param path
	 *            a {@link PrimaryDataDirectory} object.
	 * @param name
	 *            a {@link String} object.
	 * @throws PrimaryDataFileException
	 *             if unable to set data type.
	 * @throws PrimaryDataEntityVersionException
	 *             if unable to store initial version.
	 * @throws PrimaryDataDirectoryException
	 *             if no parent {@link PrimaryDataDirectory} is found.
	 * @throws MetaDataException
	 *             if the
	 *             {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData}
	 *             object of the parent {@link PrimaryDataDirectory} is not
	 *             clone-able.
	 */
	public PrimaryDataFileImplementation(final PrimaryDataDirectory path, final String name)
			throws PrimaryDataFileException, PrimaryDataEntityVersionException, PrimaryDataDirectoryException,
			MetaDataException {
		super(path, name);
	}

	/**
	 * Delete a stored version during a roll back.
	 * 
	 * @param version
	 *            the {@link PrimaryDataEntityVersion} to delete.
	 * @throws PrimaryDataFileException
	 *             if unable to delete {@link java.io.File} in the file system.
	 */
	private void deleteVersion(final PrimaryDataEntityVersion version) throws PrimaryDataFileException {

		final Path path = this.getPathToLocalFile(version);

		try {
			Files.deleteIfExists(path);
		} catch (final IOException e) {
			throw new PrimaryDataFileException("unable to delete File", e);
		}
		this.getImplementationProvider().getLogger().info("rollback FileSytem");

	}

	/** {@inheritDoc} */
	@Override
	protected boolean existData() {
		return Files.exists(this.getPathToLocalFile(this.getCurrentVersion()), LinkOption.NOFOLLOW_LINKS);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em> HIBERNATE : constant length cause it is an {@link java.util.UUID}</em>
	 */
	@Override
	@Id
	@Column(columnDefinition = "char(40)")
	public String getID() {
		return super.getID();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em> HIBERNATE : FetchType.EAGER for getPath()</em>
	 */
	@Override
	@OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	public PrimaryDataDirectoryImplementation getParentDirectory() throws PrimaryDataDirectoryException {
		return (PrimaryDataDirectoryImplementation) super.getParentDirectory();
	}

	/**
	 * Load the {@link Path} to the local stored file from the file system.
	 *
	 * @param version
	 *            the version information to find the correct {@link Path} to the
	 *            local stored {@link File}.
	 * @return the loaded {@link java.io.File}.
	 * 
	 */
	protected Path getPathToLocalFile(final PrimaryDataEntityVersion version) {

		final Path path = Paths.get(
				((FileSystemImplementationProvider) this.getImplementationProvider()).getDataPath().toString(),
				String.valueOf(version.getCreationDate().get(Calendar.YEAR)),
				String.valueOf(version.getCreationDate().get(Calendar.MONTH)),
				String.valueOf(version.getCreationDate().get(Calendar.DATE)),
				String.valueOf(version.getCreationDate().get(Calendar.HOUR_OF_DAY)),
				String.valueOf(version.getCreationDate().get(Calendar.MINUTE)),
				this.getID() + "-" + version.getRevision() + ".dat");

		return path;

	}

	/** {@inheritDoc} */
	@Override
	@Transient
	protected Map<Principal, List<EdalPermission>> getPermissionsImpl() throws PrimaryDataEntityException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<EdalPermissionImplementation> criteria = builder.createQuery(EdalPermissionImplementation.class);
		Root<EdalPermissionImplementation> rootPermission = criteria.from(EdalPermissionImplementation.class);

		criteria.where(
				builder.and(
				builder.equal(rootPermission.get("internId"), this.getID()),
				builder.equal(rootPermission.get("internVersion"), this.getCurrentVersion().getRevision())
				));

		final List<EdalPermissionImplementation> privatePerms = session.createQuery(criteria).getResultList();

		final Map<Principal, List<EdalPermission>> publicMap = new HashMap<>();

		try {
			for (final EdalPermissionImplementation p : privatePerms) {

				if (!publicMap.containsKey(p.getPrincipal().toPrincipal())) {

					CriteriaQuery<EdalPermissionImplementation> tmpQuery= builder.createQuery(EdalPermissionImplementation.class);
					Root<EdalPermissionImplementation> rootTempPermission = criteria.from(EdalPermissionImplementation.class);

					tmpQuery.where(
							builder.and(
									builder.and(
											builder.equal(rootTempPermission.get("internId"), this.getID()),
											builder.equal(rootTempPermission.get("internVersion"), this.getCurrentVersion().getRevision())),
									builder.equal(rootTempPermission.get("principal"), p.getPrincipal())
									));
					
					
					final List<EdalPermissionImplementation> userPerms = session.createQuery(tmpQuery).getResultList();
					final List<EdalPermission> publicPerms = new ArrayList<>(privatePerms.size());

					for (final EdalPermissionImplementation permission : userPerms) {
						publicPerms.add(permission.toEdalPermission());
					}
					publicMap.put(p.getPrincipal().toPrincipal(), publicPerms);
				}
			}
		} catch (final Exception e) {
			session.close();
			throw new PrimaryDataEntityException("Unable to load permissions !", e);
		}
		session.close();

		return publicMap;
	}

	/**
	 * Getter for the field <code>versionList</code>.
	 * 
	 * @return a {@link SortedSet} object.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "primaryEntityId")
	@SortNatural
	protected SortedSet<PrimaryDataEntityVersionImplementation> getVersionList() {
		return this.versionList;
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	protected SortedSet<PrimaryDataEntityVersion> getVersionsImpl() {

		if (this.getVersionList() == null) {
			return Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersion>());
		} else {
			return Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersion>(this.getVersionList()));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void moveImpl(final PrimaryDataDirectory destinationDirectory) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final Transaction transaction = session.beginTransaction();

		this.setParentDirectory(destinationDirectory);

		/* update to database */
		session.update(this);

		transaction.commit();
		session.close();
	}

	/** {@inheritDoc} */
	@Override
	protected void readImpl(final OutputStream outputStream) throws PrimaryDataFileException {

		final Path path = this.getPathToLocalFile(this.getCurrentVersion());
		try {
			byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];
			FileInputStream inputStream = new FileInputStream(path.toFile());
			for (int length; (length = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, length);
				outputStream.flush();
			}
			inputStream.close();

			this.getImplementationProvider().getLogger().info("File read: " + path);
		} catch (final IOException e) {

			if (e.getMessage() == null) {
				throw new PrimaryDataFileException("Can not read File '" + this + "': OutputStream no longer available",
						e);
			} else {
				throw new PrimaryDataFileException("Can not read '" + this + "': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Setter for the field <code>versionList</code>.
	 * 
	 * @param versionList
	 *            a {@link SortedSet} object.
	 */
	protected void setVersionList(final SortedSet<PrimaryDataEntityVersionImplementation> versionList) {
		this.versionList = Collections.synchronizedSortedSet(versionList);
		this.setCurrentVersion(this.versionList.last());
	}

	/** {@inheritDoc} */
	@Override
	protected void storeImpl(final InputStream inputStream, final PrimaryDataEntityVersion currentVersion)
			throws PrimaryDataFileException {

		final Path path = this.getPathToLocalFile(currentVersion);
		try {
			if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(path.getParent());
			}

			byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];
			FileOutputStream outputStream = new FileOutputStream(path.toFile());
			for (int length; (length = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.close();

			this.getImplementationProvider().getLogger().info("File saved: " + path);
		} catch (final IOException e) {
			throw new PrimaryDataFileException("Can not store File : " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void storeVersion(final PrimaryDataEntityVersion publicVersion) throws PrimaryDataEntityVersionException {

		final MetaDataImplementation metadata = (MetaDataImplementation) publicVersion.getMetaData();

		/* create new version */
		final PrimaryDataEntityVersionImplementation privateVersion = new PrimaryDataEntityVersionImplementation();

		privateVersion.setCreationDate(publicVersion.getCreationDate());
		privateVersion.setPrimaryEntityId(this.getID());
		privateVersion.setMetaData(metadata);
		privateVersion.setRevision(publicVersion.getRevision());
		privateVersion.setDeleted(publicVersion.isDeleted());

		final List<PublicReferenceImplementation> list = new ArrayList<PublicReferenceImplementation>();

		for (final PublicReference publicReference : publicVersion.getPublicReferences()) {
			final PublicReferenceImplementation privateReference = new PublicReferenceImplementation(publicReference);
			privateReference.setVersion(privateVersion);
			list.add(privateReference);
		}
		privateVersion.setInternReferences(list);

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final Transaction transaction = session.beginTransaction();

		try {
			/* saveOrUpdate the finished directory */
			session.saveOrUpdate(this);

			/* saveOrUpdate version -> saves automatically meta data */
			session.saveOrUpdate(privateVersion);

			transaction.commit();
		} catch (final Exception e) {

			e.printStackTrace();

			if (transaction != null) {
				transaction.rollback();
				this.getImplementationProvider().getLogger().info(
						"Unable to store PrimaryDataEntityVersion : " + e.getMessage() + " --> rollback Transaction");
				try {
					this.deleteVersion(publicVersion);
				} catch (final PrimaryDataFileException e1) {
					e1.printStackTrace();
				}
				session.close();
				throw new PrimaryDataEntityVersionException(
						"Can not store Version into Database - rollback successful");
			}
		}

		if (this.versionList == null) {
			this.versionList = Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersionImplementation>());
			this.versionList.add(privateVersion);
			Collections.synchronizedSortedSet(this.versionList);
		} else {
			this.versionList.add(privateVersion);
			Collections.synchronizedSortedSet(this.versionList);
		}

		this.setCurrentVersion(privateVersion);

		try {
			this.setDefaultPermissions();
		} catch (final PrimaryDataEntityException e) {
			throw new PrimaryDataEntityVersionException("Unable to store default permissions : " + e.getMessage(), e);
		}

		for (final Principal principal : DataManager.getSubject().getPrincipals()) {

			final Transaction transaction2 = session.beginTransaction();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<PrincipalImplementation> criteria= builder.createQuery(PrincipalImplementation.class);
			Root<PrincipalImplementation> root = criteria.from(PrincipalImplementation.class);
			
			criteria.where(
					builder.and(
					builder.equal(root.get("name"), principal.getName()),
					builder.equal(root.get("type"), principal.getClass().getSimpleName())
					));
			
			final PrincipalImplementation existingPrincipal = session.createQuery(criteria).uniqueResult();
			
			if (existingPrincipal != null) {
				privateVersion.setOwner(existingPrincipal);
			} else {
				throw new PrimaryDataEntityVersionException("Unable to load existing Principal");
			}

			/** version to add owner */
			session.saveOrUpdate(privateVersion);
			transaction2.commit();
			session.close();
			break;
		}

		this.setCurrentVersion(privateVersion);

	}
}