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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * Abstract PrimaryDataDirectory class.
 * 
 * @author lange
 * @author arendd
 */
public abstract class PrimaryDataDirectory extends PrimaryDataEntity {

	/**
	 * The standard path separator
	 */
	public static final String PATH_SEPARATOR = "/";

	/**
	 * The initial step to connect to a EDAL file service.
	 * <p>
	 * <em>NOTE: no PrimaryDataObject can be accessed outside a root directory object !</em>
	 * <p>
	 * 
	 * @param supportedPrincipals
	 *            the supported {@link Principal}s
	 * 
	 * @return the root EDAL {@link PrimaryDataDirectory} object, which is a
	 *         container for access all managed {@link PrimaryDataEntity}
	 *         objects
	 * 
	 * @throws PrimaryDataDirectoryException
	 *             if unable to mount system.
	 */
	public static PrimaryDataDirectory getRootDirectory(final List<Class<? extends Principal>> supportedPrincipals)
			throws PrimaryDataDirectoryException {

		DataManager.getImplProv().getLogger().info("Loading RootDirectory...");

		final PrimaryDataDirectory dir = DataManager.getImplProv().mount(supportedPrincipals);

		return dir;

	}

	/**
	 * Constructor for PrimaryDataDirectory.
	 */
	protected PrimaryDataDirectory() {
		super();
	}

	/**
	 * Constructor for PrimaryDataDirectory.
	 * 
	 * @param path
	 *            a {@link PrimaryDataDirectory} object.
	 * @param name
	 *            a {@link String} object.
	 * @throws PrimaryDataEntityVersionException
	 *             if can not set current {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException
	 *             if no parent {@link PrimaryDataDirectory} is found.
	 * @throws MetaDataException
	 *             if unable to set the {@link MetaData} object of the
	 *             {@link PrimaryDataDirectory}.
	 */
	protected PrimaryDataDirectory(final PrimaryDataDirectory path, final String name)
			throws PrimaryDataEntityVersionException, PrimaryDataDirectoryException, MetaDataException {
		super(path, name);
		try {
			this.getMetaData().setElementValue(EnumDublinCoreElements.TYPE, MetaData.DIRECTORY);
			this.getMetaData().setElementValue(EnumDublinCoreElements.FORMAT, MetaData.EMPTY);
		} catch (final MetaDataException e) {
			throw new PrimaryDataDirectoryException(
					"unable to set initial meta data for directory '" + path + "' : " + e.getMessage());
		}

		final PrimaryDataEntityVersion newFileVersion = new PrimaryDataEntityVersion(this, false, this.getMetaData());

		this.commitVersion(newFileVersion);
	}

	/**
	 * Create an new sub {@link PrimaryDataDirectory} in the current
	 * {@link PrimaryDataDirectory} .
	 * 
	 * <p>
	 * <em>NOTE: It is not allowed to create a new sub
	 * {@link PrimaryDataDirectory}
	 * when the currentVersion of this
	 * {@link PrimaryDataDirectory}
	 * is marked as deleted !</em>
	 * <p>
	 * <em>NOTE: It is not allowed to create a new sub
	 * {@link PrimaryDataDirectory}
	 * when there is already a {@link PrimaryDataEntity}
	 * with the same name !</em>
	 * </p>
	 * 
	 * @param path
	 *            the name of the new {@link PrimaryDataDirectory}.
	 * @return the new {@link PrimaryDataDirectory} object.<br>
	 *         <code>null</code> if failed
	 * @throws PrimaryDataDirectoryException
	 *             if the current {@link PrimaryDataEntityVersion} of this
	 *             {@link PrimaryDataDirectory} is marked as deleted or if there
	 *             is already a {@link PrimaryDataEntity} with the same name.
	 */
	public PrimaryDataDirectory createPrimaryDataDirectory(String path) throws PrimaryDataDirectoryException {

		path = path.replaceAll("[:\\\\/*?|<>]", "_");

		if (this.getCurrentVersion().isDeleted()) {
			throw new PrimaryDataDirectoryException("The currentVersion of '" + this.getName()
					+ "' is marked as deleted: can not create new PrimaryDataDirectory !");
		}

		if (this.exist(path)) {
			throw new PrimaryDataDirectoryException(
					"An object '" + path + "' already exists: please choose new name or store new version !");

		}

		PrimaryDataDirectory directory = null;
		try {
			final Constructor<? extends PrimaryDataDirectory> constructor = this.getClass()
					.getConstructor(PrimaryDataDirectory.class, String.class);

			directory = constructor.newInstance(this, path);

		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException
				| InvocationTargetException | IllegalAccessException e) {
			throw new PrimaryDataDirectoryException(
					"Unable to create new directory '" + path + "' : " + e.getMessage());
		}
		return directory;
	}

	/**
	 * Create an new {@link PrimaryDataFile} in the current
	 * {@link PrimaryDataDirectory} using the same implementation as this
	 * {@link PrimaryDataDirectory} .
	 * <p>
	 * <em>NOTE: It is not allowed to create a new {@link PrimaryDataFile} when the currentVersion of this {@link PrimaryDataDirectory} is marked as deleted !</em>
	 * <p>
	 * <em>NOTE: It is not allowed to create a new {@link PrimaryDataFile} when there is already a {@link PrimaryDataEntity} with the same name !</em>
	 * 
	 * @param name
	 *            of the new {@link PrimaryDataFile} object.
	 * @return the new {@link PrimaryDataFile}.<br>
	 *         <code>null</code> if failed
	 * @throws PrimaryDataDirectoryException
	 *             if the current {@link PrimaryDataEntityVersion} of this
	 *             {@link PrimaryDataDirectory} is marked as deleted or if there
	 *             is already a {@link PrimaryDataEntity} with the same name.
	 */
	public PrimaryDataFile createPrimaryDataFile(String name) throws PrimaryDataDirectoryException {

		name = name.replaceAll("[:\\\\/*?|<>]", "_");

		if (this.getCurrentVersion().isDeleted()) {
			throw new PrimaryDataDirectoryException("The currentVersion of '" + this.getName()
					+ "' is marked as deleted: can not create new PrimaryDataFile !");
		}

		if (this.exist(name)) {
			throw new PrimaryDataDirectoryException(
					"An object '" + name + "' already exists: please choose new name or store new version");
		}

		PrimaryDataFile file = null;
		try {

			final Constructor<? extends PrimaryDataFile> constructor = this.getImplementationProvider()
					.getPrimaryDataFileProvider().getConstructor(PrimaryDataDirectory.class, String.class);

			file = constructor.newInstance(this, name);

		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException
				| InvocationTargetException | IllegalAccessException e) {
			throw new PrimaryDataDirectoryException("Unable to create new file '" + name + "': " + e.getMessage(), e);
		}
		return file;
	}

	/**
	 * Check if a {@link PrimaryDataEntity} exists with the same path in this
	 * {@link PrimaryDataDirectory} .
	 * 
	 * @param path
	 *            the name of the {@link PrimaryDataEntity} to check.
	 * @return <code>true</code> if there is already a PrimartyDataEntity with
	 *         the same path;<br>
	 *         <code>false</code> otherwise
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all {@link PrimaryDataEntity} objects in
	 *             this {@link PrimaryDataDirectory} to check if the name
	 *             already exists.
	 */
	public boolean exist(final String path) throws PrimaryDataDirectoryException {

		return this.existImpl(path);
	}

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataDirectory#exist(String)}
	 * 
	 * @param path
	 *            the name of the {@link PrimaryDataEntity} to check.
	 * @return <code>true</code> if there is already a PrimartyDataEntity with
	 *         the same path;<br>
	 *         <code>false</code> otherwise
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all {@link PrimaryDataEntity} objects in
	 *             this {@link PrimaryDataDirectory} to check if the name
	 *             already exists.
	 */
	protected abstract boolean existImpl(String path) throws PrimaryDataDirectoryException;

	/**
	 * Convenience and free function to get only {@link PrimaryDataEntity}s with
	 * published {@link PublicReference} objects.
	 * 
	 * @return a {@link List} of all {@link PrimaryDataEntity} with a
	 *         {@link PublicReference}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load objcts.
	 */
	public List<PrimaryDataEntity> getAllPublishedEntities() throws PrimaryDataDirectoryException {
		return this.searchByPublicationStatus(PublicationStatus.ACCEPTED);
	}

	/**
	 * Get a specified {@link PrimaryDataEntity} in this
	 * {@link PrimaryDataDirectory} by name.
	 * 
	 * @param name
	 *            name of the {@link PrimaryDataEntity} in this
	 *            {@link PrimaryDataDirectory}.
	 * @return the found {@link PrimaryDataEntity} object.
	 * @throws PrimaryDataDirectoryException
	 *             if no such {@link PrimaryDataEntity} exists.
	 */
	public PrimaryDataEntity getPrimaryDataEntity(final String name) throws PrimaryDataDirectoryException {
		PrimaryDataEntity entity = this.getPrimaryDataEntityImpl(name);
		if (entity == null) {
			throw new PrimaryDataDirectoryException("No such file or directory found !");
		} else {
			return entity;
		}
	}

	/**
	 * Abstract function for the implementation of
	 * {@link PrimaryDataDirectory#getPrimaryDataEntity(String)}.
	 * 
	 * @param name
	 *            name of the {@link PrimaryDataEntity} in this
	 *            {@link PrimaryDataDirectory}.
	 * @return the found {@link PrimaryDataEntity} object.
	 * @throws PrimaryDataDirectoryException
	 *             if no such {@link PrimaryDataEntity} exists.
	 */
	protected abstract PrimaryDataEntity getPrimaryDataEntityImpl(final String name)
			throws PrimaryDataDirectoryException;

	/**
	 * Get a {@link List} containing all {@link PrimaryDataEntity} objects in
	 * this {@link PrimaryDataDirectory}.
	 * 
	 * @return a {@link List} containing all {@link PrimaryDataEntity}
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all {@link PrimaryDataEntity} objects in
	 *             this {@link PrimaryDataDirectory}.
	 */
	public List<PrimaryDataEntity> listPrimaryDataEntities() throws PrimaryDataDirectoryException {

		PrimaryDataEntityVersion currentVersion = this.getCurrentVersion();
		PrimaryDataEntityVersion nextVersion = null;

		if (this.getVersions().size() - 1 == currentVersion.getRevision()) {

		} else {
			try {
				nextVersion = this.getVersionByRevisionNumber(currentVersion.getRevision() + 1);
			} catch (PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException("Can not get next Version :" + e.getMessage());
			}

		}

		if (nextVersion != null) {
			return this.listPrimaryDataEntitiesImpl(currentVersion.getRevisionDate(), nextVersion.getRevisionDate());
		} else {
			return this.listPrimaryDataEntitiesImpl(currentVersion.getRevisionDate(), null);
		}

	}

	/**
	 * Abstract function for the implementation of
	 * {@link PrimaryDataDirectory#listPrimaryDataEntities()}.
	 * 
	 * @param currentVersionDate
	 *            the date of the current {@link PrimaryDataEntityVersion}
	 * @param nextVersionDate
	 *            the date of the next {@link PrimaryDataEntityVersion}
	 * 
	 * @return a {@link List} containing all {@link PrimaryDataEntity}
	 * @throws PrimaryDataDirectoryException
	 *             if unable to load all {@link PrimaryDataEntity} objects in
	 *             this {@link PrimaryDataDirectory}.
	 */
	protected abstract List<PrimaryDataEntity> listPrimaryDataEntitiesImpl(Calendar currentVersionDate,
			Calendar nextVersionDate) throws PrimaryDataDirectoryException;

	/**
	 * A convenience method for search only for a specific
	 * {@link EnumDublinCoreElements} and {@link UntypedData} in this
	 * {@link PrimaryDataDirectory}.
	 * 
	 * The caller function ensure that the result is an unmodifiable
	 * {@link List} containing all found {@link PrimaryDataEntity} objects
	 * 
	 * @param element
	 *            the {@link EnumDublinCoreElements} for query.
	 * @param data
	 *            {@link UntypedData} parameter for search.
	 * @param fuzzy
	 *            <code>true</code>: exact search;<br>
	 *            <code>false</code>: fuzzy search.
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub directories
	 *            recursively;<br>
	 *            <code>false</code>: search only in the current.
	 *            {@link PrimaryDataDirectory} object
	 * @return an unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the parameter.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 */
	public List<PrimaryDataEntity> searchByDublinCoreElement(final EnumDublinCoreElements element,
			final UntypedData data, final boolean fuzzy, final boolean recursiveIntoSubdirectories)
					throws PrimaryDataDirectoryException {

		return Collections.unmodifiableList(
				this.searchByDublinCoreElementImpl(element, data, fuzzy, recursiveIntoSubdirectories));
	}
	

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataDirectory#searchByDublinCoreElement(EnumDublinCoreElements, UntypedData, boolean, boolean)}
	 * .
	 * 
	 * @param element
	 *            the {@link EnumDublinCoreElements} for query
	 * @param data
	 *            {@link UntypedData} parameter for search
	 * @param fuzzy
	 *            <code>true</code>: exact search; <code>false</code>: fuzzy
	 *            search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub directories
	 *            recursively; <code>false</code>: search only in the current
	 *            {@link PrimaryDataDirectory} object
	 * @return an unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the parameter
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 */
	protected abstract List<PrimaryDataEntity> searchByDublinCoreElementImpl(EnumDublinCoreElements element,
			UntypedData data, final boolean fuzzy, final boolean recursiveIntoSubdirectories)
					throws PrimaryDataDirectoryException;

	/**
	 * The method search in the current {@link PrimaryDataDirectory} by
	 * {@link MetaData}.
	 * 
	 * @param query
	 *            a {@link MetaData} object for query.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search;<br>
	 *            <code>false</code>: exact search.
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively;<br>
	 *            <code>false</code> search only in the current
	 *            {@link PrimaryDataDirectory} object.
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the {@link MetaData} parameter.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 * @throws MetaDataException
	 *             if there are non valid values for some {@link MetaData}
	 *             elements.
	 */
	public final List<PrimaryDataEntity> searchByMetaData(final MetaData query, final boolean fuzzy,
			final boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException, MetaDataException {
		return Collections.unmodifiableList(this.searchByMetaDataImpl(query, fuzzy, recursiveIntoSubdirectories));
	}

	/**
	 * Abstract function for implementation of
	 * {@link PrimaryDataDirectory#searchByMetaData(MetaData, boolean, boolean)}
	 * .
	 * 
	 * @param query
	 *            a {@link MetaData} object.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search; <code>false</code>: exact
	 *            search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively; <code>false</code>
	 *            search only in the current {@link PrimaryDataDirectory} object
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the {@link MetaData} parameter
	 * @throws PrimaryDataDirectoryException
	 *             if unable to find {@link PrimaryDataEntity} object or if
	 *             there are too much results.
	 * @throws MetaDataException
	 *             if there are non valid values for some {@link MetaData}
	 *             elements.
	 */
	protected abstract List<PrimaryDataEntity> searchByMetaDataImpl(MetaData query, boolean fuzzy,
			boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException, MetaDataException;

	/**
	 * Function to get all {@link PrimaryDataEntity}s in this
	 * {@link PrimaryDataDirectory}, which have a {@link PublicReference}.
	 * Filtered by the {@link PublicationStatus}.
	 * 
	 * @param publicationStatus
	 *            the {@link PublicationStatus} of the searched
	 *            {@link PrimaryDataEntity}.
	 * @return a {@link List} of all {@link PrimaryDataEntity} with a
	 *         {@link PublicReference}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for {@link PublicationStatus}
	 */
	public List<PrimaryDataEntity> searchByPublicationStatus(PublicationStatus publicationStatus)
			throws PrimaryDataDirectoryException {

		return Collections.unmodifiableList(this.searchByPublicationStatusImpl(publicationStatus));
	}

	/**
	 * Abstract function for the implementation of
	 * {@link PrimaryDataDirectory#searchByPublicationStatus(PublicationStatus)}
	 * .
	 * 
	 * @param publicationStatus
	 *            the {@link PublicationStatus} of the searched
	 *            {@link PrimaryDataEntity}.
	 * @return a {@link List} of all {@link PrimaryDataEntity} with a
	 *         {@link PublicReference}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for {@link PublicationStatus}
	 */
	protected abstract List<? extends PrimaryDataEntity> searchByPublicationStatusImpl(
			PublicationStatus publicationStatus) throws PrimaryDataDirectoryException;

	/**
	 * Function to get all {@link PrimaryDataEntity}s containing the keyword.
	 * 
	 * @param keyword
	 *            the term to search over all elements.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search; <code>false</code>: exact
	 *            search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively; <code>false</code>
	 *            search only in the current {@link PrimaryDataDirectory} object
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the keyword.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for the keyword.
	 */
	public List<PrimaryDataEntity> searchByKeyword(String keyword, boolean fuzzy, boolean recursiveIntoSubdirectories)
			throws PrimaryDataDirectoryException {

		return Collections.unmodifiableList(this.searchByKeywordImpl(keyword, fuzzy, recursiveIntoSubdirectories));
	}

	/**
	 * Abstract function for the implementation of
	 * {@link PrimaryDataDirectory#searchByKeyword(String, boolean, boolean)}
	 * 
	 * @param keyword
	 *            the term to search over all elements.
	 * @param fuzzy
	 *            <code>true</code>: fuzzy search; <code>false</code>: exact
	 *            search
	 * @param recursiveIntoSubdirectories
	 *            <code>true</code>: include also all sub
	 *            {@link PrimaryDataDirectory} recursively; <code>false</code>
	 *            search only in the current {@link PrimaryDataDirectory} object
	 * @return a unmodifiable {@link List} of {@link PrimaryDataEntity} that
	 *         match the keyword.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to search for the keyword.
	 */
	protected abstract List<? extends PrimaryDataEntity> searchByKeywordImpl(String keyword, boolean fuzzy,
			boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException;

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em>Check before if the {@link EnumDublinCoreElements#TYPE} is a
	 * {@link MetaData#DIRECTORY} object.</em>
	 * <p>
	 * <em>Check before if the {@link EnumDublinCoreElements#FORMAT} is a
	 * {@link MetaData#EMPTY} object.</em>
	 */
	@Override
	public void setMetaData(final MetaData newMetadata) throws PrimaryDataEntityVersionException, MetaDataException {

		if (!newMetadata.getElementValue(EnumDublinCoreElements.TYPE).toString()
				.equals(MetaData.DIRECTORY.toString())) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.TYPE);

			throw new MetaDataException("valid value for meta data element in PrimaryDataDirectory: "
					+ EnumDublinCoreElements.TYPE.name() + " is only MetaData.DIRECTORY "
					+ newMetadata.getElementValue(EnumDublinCoreElements.TYPE) + "! Rollback to " + originalData);
		}

		if (!newMetadata.getElementValue(EnumDublinCoreElements.FORMAT).toString().equals(MetaData.EMPTY.toString())) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.FORMAT);

			throw new MetaDataException("valid value for meta data element in PrimaryDataDirectory: "
					+ EnumDublinCoreElements.FORMAT.name() + " is only MetaData.EMPTY "
					+ newMetadata.getElementValue(EnumDublinCoreElements.FORMAT) + "! Rollback to " + originalData);
		}

		super.setMetaData(newMetadata);
	}
}