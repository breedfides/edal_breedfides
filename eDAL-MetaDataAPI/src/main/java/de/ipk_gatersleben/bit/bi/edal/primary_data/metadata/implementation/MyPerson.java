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

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link Person} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
/*
 * @DiscriminatorValue("11")
 * 
 * no DiscriminatorValue necessary because Person is abstract
 */
@Indexed
public class MyPerson extends MyUntypedData {

	private static final long serialVersionUID = -6403638807931955763L;

	private String addressLine;
	private String zip;
	private String country;

	/**
	 * Default constructor for {@link MyPerson} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyPerson() {
	}

	/**
	 * Copy constructor to convert public {@link Person} to private
	 * {@link MyPerson}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyPerson(final UntypedData edal) {

		super(edal);

		if (edal instanceof Person) {
			Person person = (Person) edal;

			this.setAddressLine(person.getAddressLine());
			this.setCountry(person.getCountry());
			this.setZip(person.getZip());
		}
	}

	/**
	 * Getter for the field <code>addressLine</code>.
	 * 
	 * @return address line of the {@link Person}.
	 */
	@Column(columnDefinition = "varchar(4000)")
	//@Field(index = Index.YES, store = Store.YES)
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getAddressLine() {
		return addressLine;
	}

	/**
	 * Getter for the field <code>country</code>.
	 * 
	 * @return country of the {@link Person}.
	 */
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	//@Field(index = Index.YES, store = Store.YES)
	public String getCountry() {
		return country;
	}

	/**
	 * Getter for the field <code>zip</code>.
	 * 
	 * @return zip of the {@link Person}.
	 */
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	//@Field(index = Index.YES, store = Store.YES)
	public String getZip() {
		return zip;
	}

	/**
	 * Setter for the field <code>addressLine</code>.
	 * 
	 * @param addressLine
	 *            the address line of the {@link Person}.
	 */
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	/**
	 * Setter for the field <code>country</code>.
	 * 
	 * @param country
	 *            the country of the {@link Person}.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Setter for the field <code>zip</code>.
	 * 
	 * @param zip
	 *            the zip of the {@link Person}.
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.addressLine + ", " + this.zip + ", " + this.country;
	}
}