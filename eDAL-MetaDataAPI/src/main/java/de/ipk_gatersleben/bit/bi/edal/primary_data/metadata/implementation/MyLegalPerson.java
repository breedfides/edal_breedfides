/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link LegalPerson} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("14")
@Indexed
public class MyLegalPerson extends MyPerson {

	private static final long serialVersionUID = -2818063618946751154L;
	private String legalName;

	/**
	 * Default constructor for {@link MyLegalPerson} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyLegalPerson() {
	}

	/**
	 * Copy constructor to convert public {@link LegalPerson} to private
	 * {@link MyLegalPerson}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyLegalPerson(final UntypedData edal) {

		super(edal);

		if (edal instanceof LegalPerson) {

			final LegalPerson legalPerson = (LegalPerson) edal;

			this.setLegalName(legalPerson.getLegalName());
			this.setAddressLine(legalPerson.getAddressLine());
			this.setCountry(legalPerson.getCountry());
			this.setZip(legalPerson.getZip());
		}
	}

	/**
	 * @return the legalName
	 */
	@Column(columnDefinition = "varchar(4000)")
	@Field(index = Index.YES, store = Store.YES)
	public String getLegalName() {
		return this.legalName;
	}

	/**
	 * @param legalName
	 *            the legalName to set
	 */
	public void setLegalName(final String legalName) {
		this.legalName = legalName;
	}

}
