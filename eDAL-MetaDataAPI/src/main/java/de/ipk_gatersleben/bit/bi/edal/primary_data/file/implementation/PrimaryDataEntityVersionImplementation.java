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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;

/**
 * Implementation of {@link PrimaryDataEntityVersion}.
 * 
 * @author arendd
 */
@Entity
@Table(name = "ENTITY_VERSIONS")
@TypeDefs(value = { @TypeDef(name = "BooleanType", typeClass = BooleanType.class) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "EdalVersion")
public class PrimaryDataEntityVersionImplementation extends PrimaryDataEntityVersion implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String primaryEntityId;
	private MetaDataImplementation metaData;
	private List<PublicReferenceImplementation> internReferences = new ArrayList<PublicReferenceImplementation>();
	private PrincipalImplementation owner;

	/**
	 * Default constructor for {@link PrimaryDataEntityVersionImplementation} is
	 * necessary for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public PrimaryDataEntityVersionImplementation() {
	}

	public void addPublicReference(final PublicReferenceImplementation publicReference) {
		this.internReferences.add(publicReference);
	}

	/** {@inheritDoc} */
	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreationDate() {
		return super.getCreationDate();
	}

	/**
	 * Getter for <code>isDeleted</code> flag.
	 * 
	 * @return a boolean.
	 */
	@Type(type = "BooleanType")
	public boolean getDeleted() {
		return super.isDeleted();
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	public PrimaryDataEntity getEntity() {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		
		session.setDefaultReadOnly(true);

		final Criteria fileQuery = session.createCriteria(PrimaryDataFileImplementation.class).add(Restrictions.eq("class", PrimaryDataFileImplementation.class)).add(Restrictions.eq("id", this.getPrimaryEntityId())).setCacheable(true);

		final PrimaryDataFile pdf = (PrimaryDataFile) fileQuery.uniqueResult();

		if (pdf == null) {
			final Criteria directoryQuery = session.createCriteria(PrimaryDataDirectoryImplementation.class).add(Restrictions.eq("class", PrimaryDataDirectoryImplementation.class)).add(Restrictions.eq("id", this.getPrimaryEntityId())).setCacheable(true);

			final PrimaryDataDirectory pdd = (PrimaryDataDirectory) directoryQuery.uniqueResult();
			session.close();
			return pdd;
		} else {
			session.close();
			return pdf;
		}

	}

	/**
	 * Getter for the field <code>id</code>.
	 * 
	 * @return a int.
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return this.id;
	}

	/**
	 * Getter for the field <code>internReferences</code>.
	 * 
	 * @return a {@link List} of {@link PublicReferenceImplementation} objects.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "version")
	public List<PublicReferenceImplementation> getInternReferences() {
		return this.internReferences;
	}

	/** {@inheritDoc} */
	@Override
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="METADATA_ID")
	public MetaDataImplementation getMetaData() {
		return this.metaData;
	}

	/**
	 * Getter for the field <code>primaryEntityId</code>.
	 * 
	 * @return a {@link String} object.
	 */
	@Column(columnDefinition = "char(40)")
	public String getPrimaryEntityId() {
		return this.primaryEntityId;
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	public List<PublicReference> getPublicReferences() {

		final List<PublicReference> publicList = new ArrayList<PublicReference>(this.getInternReferences());

		return publicList;
	}

	/** {@inheritDoc} */
	@Override
	public long getRevision() {
		return super.getRevision();
	}

	/** {@inheritDoc} */
	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getRevisionDate() {
		return super.getRevisionDate();
	}

	/** {@inheritDoc} */
	@Override
	public void setAllReferencesPublic(final InternetAddress emailNotificationAddress, final Calendar releaseDate) throws PublicReferenceException {

		for (final PublicReferenceImplementation publicReferenceImplementation : this.getInternReferences()) {
			publicReferenceImplementation.setPublic(emailNotificationAddress, releaseDate);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void setCreationDate(final Calendar creationDate) {
		super.setCreationDate(creationDate);
	}

	/** {@inheritDoc} */
	@Override
	protected void setDeleted(final boolean isDeleted) {
		super.setDeleted(isDeleted);
	}

	/**
	 * Setter for the field <code>id</code>.
	 * 
	 * @param id
	 *            a int.
	 */
	protected void setId(final int id) {
		this.id = id;
	}

	/**
	 * Setter for the field <code>internReferences</code>.
	 * 
	 * @param internReferences
	 *            a {@link List} of {@link PublicReferenceImplementation}
	 *            objects.
	 */
	protected void setInternReferences(final List<PublicReferenceImplementation> internReferences) {
		this.internReferences = internReferences;
	}

	/**
	 * Setter for the field <code>metaData</code>.
	 * 
	 * @param metaData
	 *            a {@link MetaDataImplementation} object.
	 */
	protected void setMetaData(final MetaDataImplementation metaData) {
		this.metaData = metaData;

		/* set metaData of internal version to metaData of public Version */
		super.setMetaData(metaData);
	}

	/**
	 * Setter for the field <code>primaryEntityId</code>.
	 * 
	 * @param primaryEntityId
	 *            a {@link String} object.
	 */
	protected void setPrimaryEntityId(final String primaryEntityId) {
		this.primaryEntityId = primaryEntityId;
	}

	/** {@inheritDoc} */
	@Override
	protected void setRevision(final long revision) {
		super.setRevision(revision);
	}

	/** {@inheritDoc} */
	@Override
	protected void setRevisionDate(final Calendar revisionDate) {
		super.setRevisionDate(revisionDate);
	}

	/**
	 * @return the owner
	 */
	@ManyToOne
	public PrincipalImplementation getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	protected void setOwner(PrincipalImplementation owner) {
		this.owner = owner;
	}
}