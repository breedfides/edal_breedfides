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
import java.security.Principal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.type.BooleanType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.Referenceable;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * Class for the persistence of the {@link PublicReference} class with
 * HIBERNATE.
 * 
 * @author arendd
 */
@Entity
@TypeDefs(value = { @TypeDef(name = "BooleanType", typeClass = BooleanType.class) })
@Table(name = "PublicReferences")
public final class PublicReferenceImplementation extends PublicReference
		implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private PrimaryDataEntityVersionImplementation version;
	private PersistentIdentifier identifierType;
	private boolean isPublic;
	private String assignedID;
	private String landingPage;
	private String internalID;

	private PrincipalImplementation privateRequestedPrincipal;

	private Set<ReviewStatusImplementation> reviewStatusSet = new HashSet<ReviewStatusImplementation>();

	/**
	 * Default constructor for {@link PublicReferenceImplementation} is
	 * necessary for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected PublicReferenceImplementation() {
		super();
	}

	/**
	 * Constructor for a {@link PublicReferenceImplementation} from the given
	 * {@link PublicReference} object. The considering
	 * {@link PrimaryDataEntityVersionImplementation} will be set in the
	 * {@link PrimaryDataDirectoryImplementation#storeVersion(de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion)}
	 * function.
	 * 
	 * @param pub
	 *            the initial {@link PublicReference} object.
	 */
	protected PublicReferenceImplementation(final PublicReference pub) {
		this.identifierType = pub.getIdentifierType();
		this.isPublic = pub.isPublic();

		// principal muss schon vorhanden sein :
		final Session session = ((FileSystemImplementationProvider) DataManager
				.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.where(builder.and(builder.equal(principalRoot.get("name"),pub.getRequestedPrincipal().getName())),builder.equal(principalRoot.get("type"), pub.getRequestedPrincipal().getClass().getSimpleName()));

		PrincipalImplementation principal = session.createQuery(principalCriteria).setCacheable(true).setCacheRegion("query.principal").uniqueResult();
		
		this.privateRequestedPrincipal = principal;
		this.internalID = pub.getInternalID();
		session.close();
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getAcceptedDate() {
		return super.getAcceptedDate();
	}

	/**
	 * @return the assignedID
	 */
	public String getAssignedID() throws PublicReferenceException {
		return this.assignedID;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreationDate() {
		return super.getCreationDate();
	}

	/**
	 * Getter for the <code>id</code> field of this
	 * {@link PublicReferenceImplementation} to persist with HIBERNATE.
	 * 
	 * @return id the id of this {@link PublicReferenceImplementation}.
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return this.id;
	}

	/** {@inheritDoc} */
	@Override
	@Enumerated(EnumType.STRING)
	public PersistentIdentifier getIdentifierType() {
		return this.identifierType;
	}

	@ManyToOne
	protected PrincipalImplementation getPrivateRequestedPrincipal() {
		return this.privateRequestedPrincipal;
	}

	@Override
	@Enumerated(EnumType.STRING)
	public PublicationStatus getPublicationStatus() {
		return super.getPublicationStatus();
	}

	@Override
	@Transient
	public Referenceable getReferencable() throws EdalException {
		try {
			return this.identifierType.getImplClass().getDeclaredConstructor().newInstance();
		} catch (final Exception e) {
			throw new EdalException(
					"unable to load the class for the identifierType '"
							+ this.identifierType + "' : " + e.getMessage());
		}
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getReleaseDate() {
		return super.getReleaseDate();
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getRejectedDate() {
		return super.getRejectedDate();
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getRequestedDate() {
		return super.getRequestedDate();
	}

	@Override
	@Transient
	public Principal getRequestedPrincipal() {
		try {
			return this.getPrivateRequestedPrincipal().toPrincipal();
		} catch (final EdalException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	@ManyToOne(cascade=CascadeType.ALL)
	public PrimaryDataEntityVersionImplementation getVersion() {
		return this.version;
	}

	/** {@inheritDoc} */
	@Override
	@Type(type = "BooleanType")
	public boolean isPublic() {

		return this.isPublic;
	}

	@Override
	protected void setAcceptedDate(final Calendar acceptedDate) {
		super.setAcceptedDate(acceptedDate);
	}

	/**
	 * @param assignedID
	 *            the assignedID to set
	 */
	protected void setAssignedID(final String assignedID) {
		this.assignedID = assignedID;
	}

	@Override
	protected void setCreationDate(final Calendar creationDate) {
		super.setCreationDate(creationDate);
	}

	/**
	 * Setter for the <code>id</code> field of this
	 * {@link PublicReferenceImplementation} to persist with HIBERNATE.
	 * 
	 * @param id
	 *            the id to set.
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Setter for the {@link PersistentIdentifier} of this
	 * {@link PublicReferenceImplementation}.
	 * 
	 * @param identifierType
	 *            the {@link PersistentIdentifier} to set.
	 */
	public void setIdentifierType(final PersistentIdentifier identifierType) {
		this.identifierType = identifierType;
	}

	protected void setPrivateRequestedPrincipal(
			final PrincipalImplementation requestedPrincipal) {
		this.privateRequestedPrincipal = requestedPrincipal;
	}

	/**
	 * Setter for the <code>isPublic</code> field of this
	 * {@link PublicReferenceImplementation}.
	 * 
	 * @param isPublic
	 *            the {@link Boolean} to set.
	 */
	protected void setPublic(final boolean isPublic) {
		this.isPublic = isPublic;
	}

	@Override
	protected void setPublicationStatus(
			final PublicationStatus publicationStatus) {
		super.setPublicationStatus(publicationStatus);
	}

	@Override
	protected void setRejectedDate(final Calendar rejectedDate) {
		super.setRejectedDate(rejectedDate);
	}

	@Override
	protected void setRequestedDate(final Calendar requestedDate) {
		super.setRequestedDate(requestedDate);
	}

	@Override
	protected void setReleaseDate(final Calendar releaseDate) {
		super.setReleaseDate(releaseDate);

	}

	@Override
	public void changeReleaseDate(Calendar releaseDate) {
		super.changeReleaseDate(releaseDate);
		final Session session = ((FileSystemImplementationProvider) DataManager
				.getImplProv()).getSession();

		Transaction transaction = session.beginTransaction();

		session.update(this);

		transaction.commit();
		session.close();
	}

	/**
	 * Setter for the {@link PrimaryDataEntityVersionImplementation} of this
	 * {@link PublicReferenceImplementation}.
	 * 
	 * @param version
	 *            the {@link PrimaryDataEntityVersionImplementation} object to
	 *            set.
	 */
	protected void setVersion(
			final PrimaryDataEntityVersionImplementation version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "PublicReferenceImplementation [version=" + version
				+ ", identifierType=" + identifierType + ", isPublic="
				+ isPublic + "]";
	}

	/**
	 * @return the reviewStatusSet
	 */
	@OneToMany(fetch = FetchType.EAGER, targetEntity = ReviewStatusImplementation.class, mappedBy = "publicReference")
	public Set<ReviewStatusImplementation> getReviewStatusSet() {
		return reviewStatusSet;
	}

	/**
	 * @param reviewStatusSet
	 *            the reviewStatusSet to set
	 */
	public void setReviewStatusSet(
			Set<ReviewStatusImplementation> reviewStatusSet) {
		this.reviewStatusSet = reviewStatusSet;
	}

	/**
	 * @return the landingPage
	 */
	public String getLandingPage() {
		return landingPage;
	}

	/**
	 * @param landingPage
	 *            the landingPage to set
	 */
	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	/**
	 * @return the internalID
	 */
	@Override
	public String getInternalID() {
		return this.internalID;
	}

	/**
	 * @param internalID
	 *            the internalID to set
	 */
	@Override
	protected void setInternalID(String internalID) {
		this.internalID = internalID;
	}

}