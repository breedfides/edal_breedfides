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

import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.HttpServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

/**
 * Implementation of {@link HttpServiceProvider} interface.
 * 
 * @author arendd
 *
 */
public class HttpServiceProviderImplementation implements HttpServiceProvider {

    /** {@inheritDoc} */
    @Override
    public PrimaryDataEntity getPrimaryDataEntityByID(final String uuid,
	    final long versionNumber) throws EdalException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();
	
	CriteriaBuilder builder = session.getCriteriaBuilder();
	
	CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder.createQuery(PrimaryDataFileImplementation.class);
	Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);
	fileCriteria.where(builder.and(
			builder.equal(fileRoot.type(),PrimaryDataFileImplementation.class)),
			builder.equal(fileRoot.get("ID"), uuid));
	
	final PrimaryDataFileImplementation file = session.createQuery(fileCriteria).uniqueResult();

	if (file == null) {
	    
	    CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder.createQuery(PrimaryDataDirectoryImplementation.class);
		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria.from(PrimaryDataDirectoryImplementation.class);
		directoryCriteria.where(builder.and(
				builder.equal(directoryRoot.type(),PrimaryDataDirectoryImplementation.class)),
				builder.equal(directoryRoot.get("ID"), uuid));

		final PrimaryDataDirectoryImplementation directory = session.createQuery(directoryCriteria).uniqueResult();

	    if (directory == null) {
		throw new EdalException(
			"found no entity with ID '" + uuid + "'");
	    } else {

		PrimaryDataEntityVersion version;
		try {
		    version = directory
			    .getVersionByRevisionNumber(versionNumber);
		} catch (final PrimaryDataEntityVersionException e) {
		    /** found no version with this number */
		    throw new EdalException(e.getMessage(), e);
		}

		try {
		    directory.switchCurrentVersion(version);
		} catch (final PrimaryDataEntityVersionException e) {
		    throw new EdalException(
			    "unable to switch the version with the number "
				    + versionNumber,
			    e);
		}

	    }

	    final List<PublicReference> list = directory.getCurrentVersion()
		    .getPublicReferences();

	    for (final PublicReference publicReference : list) {
		if (publicReference.isPublic()) {
		    if (publicReference.getReleaseDate() == null) {
			return directory;
		    } else {
			if (publicReference.getReleaseDate()
				.after(Calendar.getInstance())) {
			    throw new EdalException(this.isLockedMessage(
				    directory, publicReference));
			} else {
			    return directory;
			}
		    }
		}
	    }
	    throw new EdalException(
		    "found no PublicReference for this version of "
			    + directory.getName());

	} else {

	    PrimaryDataEntityVersion version;
	    try {
		version = file.getVersionByRevisionNumber(versionNumber);
	    } catch (final PrimaryDataEntityVersionException e) {
		/** found no version with this number */
		throw new EdalException(e.getMessage(), e);
	    }

	    try {
		file.switchCurrentVersion(version);
	    } catch (final PrimaryDataEntityVersionException e) {
		throw new EdalException(
			"unable to switch the version with the number "
				+ versionNumber,
			e);
	    }

	    final List<PublicReference> list = file.getCurrentVersion()
		    .getPublicReferences();
	    for (final PublicReference publicReference : list) {
		if (publicReference.isPublic()) {

		    if (publicReference.getReleaseDate() == null) {
			return file;
		    } else {
			if (publicReference.getReleaseDate()
				.after(Calendar.getInstance())) {
			    throw new EdalException(this.isLockedMessage(file,
				    publicReference));
			} else {
			    return file;
			}
		    }
		}
	    }
	    throw new EdalException(
		    "found no PublicReference for this version of "
			    + file.getName());
	}

    }

    @Override
    public PrimaryDataEntity getPrimaryDataEntityForPersistentIdentifier(
	    final String uuid, final long versionNumber,
	    final PersistentIdentifier persistentIdentifier)
	    throws EdalException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	CriteriaBuilder builder = session.getCriteriaBuilder();
	
	CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder.createQuery(PrimaryDataFileImplementation.class);
	Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);
	fileCriteria.where(builder.and(
			builder.equal(fileRoot.type(),PrimaryDataFileImplementation.class)),
			builder.equal(fileRoot.get("ID"), uuid));
	
	final PrimaryDataFileImplementation file = session.createQuery(fileCriteria).uniqueResult();

	if (file == null) {

	    CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder.createQuery(PrimaryDataDirectoryImplementation.class);
		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria.from(PrimaryDataDirectoryImplementation.class);
		directoryCriteria.where(builder.and(
				builder.equal(directoryRoot.type(),PrimaryDataDirectoryImplementation.class)),
				builder.equal(directoryRoot.get("ID"), uuid));

		final PrimaryDataDirectoryImplementation directory = session.createQuery(directoryCriteria).uniqueResult();

	    if (directory == null) {
		session.close();
		throw new EdalException(
			"no entity with ID '" + uuid + "' found !");
	    } else {

		try {
		    final PublicReference reference = directory
			    .getVersionByRevisionNumber(versionNumber)
			    .getPublicReference(persistentIdentifier);
		    if (reference.isPublic()) {
			if (reference.getReleaseDate() == null) {
			    session.close();
			    return directory;
			} else {
			    if (reference.getReleaseDate()
				    .after(Calendar.getInstance())) {
				session.close();
				throw new EdalException(this
					.isLockedMessage(directory, reference));
			    } else {
				session.close();
				return directory;
			    }
			}

		    } else {
			session.close();
			return this.searchRekursiveForPersistentIdentifiers(
				directory, versionNumber, persistentIdentifier);
		    }
		} catch (final PrimaryDataEntityVersionException e) {
		    session.close();
		    return this.searchRekursiveForPersistentIdentifiers(
			    directory, versionNumber, persistentIdentifier);
		}
	    }
	} else {
	    try {
		final PublicReference reference = file
			.getVersionByRevisionNumber(versionNumber)
			.getPublicReference(persistentIdentifier);

		if (reference.isPublic()) {
		    if (reference.getReleaseDate() == null) {
			session.close();
			return file;
		    } else {
			if (reference.getReleaseDate()
				.after(Calendar.getInstance())) {
			    session.close();
			    throw new EdalException(
				    this.isLockedMessage(file, reference));
			} else {
			    session.close();
			    return file;
			}
		    }
		} else {
		    session.close();
		    return this.searchRekursiveForPersistentIdentifiers(file,
			    versionNumber, persistentIdentifier);
		}
	    } catch (final PrimaryDataEntityVersionException e) {
		session.close();
		return this.searchRekursiveForPersistentIdentifiers(file,
			versionNumber, persistentIdentifier);
	    }
	}
    }

    /** {@inheritDoc} */
    @Override
    public PrimaryDataEntity getPrimaryDataEntityForReviewer(final String uuid,
	    final long versionNumber, final String internalId,
	    final int reviewerCode) throws EdalException {

	final Session session = ((FileSystemImplementationProvider) DataManager
		.getImplProv()).getSession();

	CriteriaBuilder builder = session.getCriteriaBuilder();
	 
    CriteriaQuery<ReviewersImplementation> reviewerCriteria = builder.createQuery(ReviewersImplementation.class);
	Root<ReviewersImplementation> reviewerRoot = reviewerCriteria.from(ReviewersImplementation.class);
	reviewerCriteria.where(builder.equal(reviewerRoot.get("hashCode"),reviewerCode));

	final ReviewersImplementation reviewer = session.createQuery(reviewerCriteria).uniqueResult();

	if (reviewer != null) {
		
		CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder.createQuery(PrimaryDataFileImplementation.class);
		Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);
		fileCriteria.where(builder.and(
				builder.equal(fileRoot.type(),PrimaryDataFileImplementation.class)),
				builder.equal(fileRoot.get("ID"), uuid));
		
		final PrimaryDataFileImplementation file = session.createQuery(fileCriteria).uniqueResult();


	    if (file != null) {
		try {
		    final PrimaryDataEntityVersion version = file
			    .getVersionByRevisionNumber(versionNumber);
		    file.switchCurrentVersion(version);
		    session.close();
		    return file;
		} catch (final PrimaryDataEntityVersionException e) {
		    session.close();
		    throw new EdalException(e.getMessage(), e);
		}
	    } else {
	    	
	    	CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder.createQuery(PrimaryDataDirectoryImplementation.class);
	  		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria.from(PrimaryDataDirectoryImplementation.class);
	  		directoryCriteria.where(builder.and(
	  				builder.equal(directoryRoot.type(),PrimaryDataDirectoryImplementation.class)),
	  				builder.equal(directoryRoot.get("ID"), uuid));

	  		final PrimaryDataDirectoryImplementation directory = session.createQuery(directoryCriteria).uniqueResult();
	  		
		if (directory == null) {
		    session.close();
		    throw new EdalException(
			    "no entity with ID '" + uuid + "' found !");
		} else {
		    try {
			final PrimaryDataEntityVersion version = directory
				.getVersionByRevisionNumber(versionNumber);
			directory.switchCurrentVersion(version);
			session.close();
			return directory;
		    } catch (final PrimaryDataEntityVersionException e) {
			session.close();
			throw new EdalException(e.getMessage(), e);
		    }
		}
	    }
	} else {
	    session.close();
	    throw new EdalException(
		    "no reviewer with ID '" + reviewerCode + "' found !");
	}
    }

    @Override
    public PrimaryDataEntity getPrimaryDataEntityRekursiveForPersistenIdentifier(
	    final PrimaryDataEntity entity, final long versionNumber,
	    final PersistentIdentifier persistentIdentifier)
	    throws EdalException {

	PrimaryDataEntityVersion version = null;
	try {
	    version = entity.getVersionByRevisionNumber(versionNumber);
	} catch (final PrimaryDataEntityVersionException e) {
	    throw new EdalException(e.getMessage());
	}
	boolean ready = false;

	PrimaryDataEntity parent = null;

	try {
	    parent = entity.getParentDirectory();
	    if (parent == null) {
		/* root directory */
		throw new EdalException("no PublicReference for entity '"
			+ entity.getName() + "' publicated");
	    }
	} catch (final PrimaryDataDirectoryException e1) {
	    throw new EdalException("no PublicReference for entity '"
		    + entity.getName() + "' publicated");
	}
	while (!ready) {

	    final SortedSet<PrimaryDataEntityVersion> set = parent
		    .getVersions();

	    boolean found = false;

	    for (final PrimaryDataEntityVersion primaryDataEntityVersion : set) {
		try {
		    if (primaryDataEntityVersion
			    .getPublicReference(persistentIdentifier)
			    .isPublic()) {
			if (primaryDataEntityVersion.getRevisionDate()
				.after(version.getRevisionDate())) {

			    /**
			     * check if the public reference of the parent
			     * directory is not locked
			     **/
			    if (primaryDataEntityVersion
				    .getPublicReference(persistentIdentifier)
				    .getReleaseDate() != null
				    && primaryDataEntityVersion
					    .getPublicReference(
						    persistentIdentifier)
					    .getReleaseDate()
					    .after(Calendar.getInstance())) {
				throw new EdalException(this.isLockedMessage(
					parent,
					primaryDataEntityVersion
						.getPublicReference(
							persistentIdentifier)));
			    }

			    found = true;
			}
		    }
		} catch (final PrimaryDataEntityVersionException e) {
		    DataManager.getImplProv().getLogger()
			    .debug("no public reference found for '" + parent
				    + "', trying next");
		}
	    }

	    if (!found) {
		try {
		    parent = parent.getParentDirectory();
		} catch (final PrimaryDataDirectoryException e) {
		    throw new EdalException("no PublicReference for entity '"
			    + entity.getName() + "' publicated");
		}
		if (parent == null) {
		    throw new EdalException("no PublicReference for entity '"
			    + entity.getName() + "' publicated");
		}
	    } else {
		ready = true;
		return parent;
	    }
	}
	return entity;
    }

    /**
     * @param primary
     *            _data the version of {@link PrimaryDataEntity} that is under
     *            embargo
     * @param publicReference
     *            the {@link PublicReference}
     * @return a message String that inform how long the entity is locked
     */
    private final String isLockedMessage(final PrimaryDataEntity primary_data,
	    final PublicReference publicReference) {
	return "The access to '" + primary_data.getName()
		+ "' is under embargo until "
		+ publicReference.getReleaseDate().getTime();
    }

    private PrimaryDataEntity searchRekursiveForPersistentIdentifiers(
	    final PrimaryDataEntity entity, final long versionNumber,
	    final PersistentIdentifier persistentIdentifier)
	    throws EdalException {

	PrimaryDataEntityVersion version = null;
	try {
	    version = entity.getVersionByRevisionNumber(versionNumber);
	} catch (final PrimaryDataEntityVersionException e) {
	    throw new EdalException(e.getMessage());
	}
	boolean ready = false;

	PrimaryDataEntity parent = null;

	try {
	    parent = entity.getParentDirectory();
	    if (parent == null) {
		/* root directory */
		throw new EdalException("no PublicReference for entity '"
			+ entity.getName() + "' publicated");
	    }
	} catch (final PrimaryDataDirectoryException e1) {
	    throw new EdalException("no PublicReference for entity '"
		    + entity.getName() + "' publicated");
	}
	while (!ready) {

	    final SortedSet<PrimaryDataEntityVersion> set = parent
		    .getVersions();

	    boolean found = false;

	    for (final PrimaryDataEntityVersion primaryDataEntityVersion : set) {
		try {
		    if (primaryDataEntityVersion
			    .getPublicReference(persistentIdentifier)
			    .isPublic()) {
			if (primaryDataEntityVersion.getRevisionDate()
				.after(version.getRevisionDate())) {

			    /**
			     * check if the public reference of the parent
			     * directory is not locked
			     **/
			    if (primaryDataEntityVersion
				    .getPublicReference(persistentIdentifier)
				    .getReleaseDate() != null
				    && primaryDataEntityVersion
					    .getPublicReference(
						    persistentIdentifier)
					    .getReleaseDate()
					    .after(Calendar.getInstance())) {
				throw new EdalException(this.isLockedMessage(
					parent,
					primaryDataEntityVersion
						.getPublicReference(
							persistentIdentifier)));
			    }

			    found = true;
			}
		    }
		} catch (final PrimaryDataEntityVersionException e) {
		    DataManager.getImplProv().getLogger()
			    .debug("no public reference found for '" + parent
				    + "', trying next");
		}
	    }

	    if (!found) {
		try {
		    parent = parent.getParentDirectory();
		} catch (final PrimaryDataDirectoryException e) {
		    throw new EdalException("no PublicReference for entity '"
			    + entity.getName() + "' publicated");
		}
		if (parent == null) {
		    throw new EdalException("no PublicReference for entity '"
			    + entity.getName() + "' publicated");
		}
	    } else {
		ready = true;
	    }
	}
	return entity;
    }

}
