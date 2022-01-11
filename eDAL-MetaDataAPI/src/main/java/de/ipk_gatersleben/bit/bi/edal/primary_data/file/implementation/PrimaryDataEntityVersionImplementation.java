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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
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

	private static final String STRING_ID = "ID";
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

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder.createQuery(PrimaryDataFileImplementation.class);
		Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);

		fileCriteria.where(builder.and(builder.equal(fileRoot.type(),  PrimaryDataFileImplementation.class),
				builder.equal(fileRoot.get(STRING_ID), this.getPrimaryEntityId())));

		final PrimaryDataFile pdf = session.createQuery(fileCriteria).uniqueResult();
	
		if (pdf == null) {
						
			CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder.createQuery(PrimaryDataDirectoryImplementation.class);
			Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria.from(PrimaryDataDirectoryImplementation.class);

			directoryCriteria.where(builder.and(builder.equal(directoryRoot.type(),  PrimaryDataDirectoryImplementation.class),
					builder.equal(directoryRoot.get(STRING_ID), this.getPrimaryEntityId())));

			final PrimaryDataDirectory pdd = session.createQuery(directoryCriteria).uniqueResult();
			
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
	@JoinColumn(name = "METADATA_ID")
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
	public void setAllReferencesPublic(final InternetAddress emailNotificationAddress, final Calendar releaseDate)
			throws PublicReferenceException {

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
	 *            a {@link List} of {@link PublicReferenceImplementation} objects.
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