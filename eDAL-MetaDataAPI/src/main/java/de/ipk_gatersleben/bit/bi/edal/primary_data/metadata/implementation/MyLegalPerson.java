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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

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
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
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
