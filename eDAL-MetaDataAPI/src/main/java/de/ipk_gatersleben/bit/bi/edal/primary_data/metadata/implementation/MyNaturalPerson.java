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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link NaturalPerson} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("13")
//@Indexed
public class MyNaturalPerson extends MyPerson {

	private static final long serialVersionUID = -8540414353817218447L;
	private String givenName;
	private String sureName;
	private MyORCID orcid;

	/**
	 * Default constructor for {@link MyNaturalPerson} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyNaturalPerson() {

	}

	/**
	 * Copy constructor to convert public {@link NaturalPerson} to private
	 * {@link MyNaturalPerson}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyNaturalPerson(final UntypedData edal) {

		super(edal);

		if (edal instanceof NaturalPerson) {

			NaturalPerson naturalPerson = (NaturalPerson) edal;

			this.setGivenName(naturalPerson.getGivenName());
			this.setSureName(naturalPerson.getSureName());
			this.setAddressLine(naturalPerson.getAddressLine());
			this.setCountry(naturalPerson.getCountry());
			this.setZip(naturalPerson.getZip());

			if (naturalPerson.getOrcid() != null) {

				/** not working, because throwing NPE on client side when loading ImplProv **/
				// try {
				// Session session = ((FileSystemImplementationProvider)
				// DataManager.getImplProv()).getSession();
				// Transaction transaction = session.beginTransaction();
				//
				// MyORCID existingORCID = (MyORCID) session.createCriteria(MyORCID.class)
				// .add(Restrictions.eq("orcid",
				// naturalPerson.getOrcid().getOrcid())).uniqueResult();
				//
				// if (existingORCID != null) {
				// transaction.commit();
				// session.close();
				// this.setOrcid(existingORCID);
				//
				// } else {
				// MyORCID newORCID = new MyORCID(naturalPerson.getOrcid().getOrcid());
				// session.save(newORCID);
				// this.setOrcid(newORCID);
				// transaction.commit();
				// session.close();
				// }
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				this.setOrcid(new MyORCID(naturalPerson.getOrcid().getOrcid()));

			}
		}
	}

	/**
	 * @return the givenName
	 */
	@Column(columnDefinition = "varchar(4000)")
	//@Field(index = Index.YES, store = Store.YES)
	@FullTextField(analyzer = "default")
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName
	 *            the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the sureName
	 */
	@Column(columnDefinition = "varchar(4000)")
	//@Field(index = Index.YES, store = Store.YES)
	@FullTextField(analyzer = "default")
	public String getSureName() {
		return sureName;
	}

	/**
	 * @param sureName
	 *            the sureName to set
	 */
	public void setSureName(String sureName) {
		this.sureName = sureName;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "orcid_id")
	public MyORCID getOrcid() {
		return orcid;
	}

	public void setOrcid(MyORCID orcid) {
		this.orcid = orcid;
	}

}
