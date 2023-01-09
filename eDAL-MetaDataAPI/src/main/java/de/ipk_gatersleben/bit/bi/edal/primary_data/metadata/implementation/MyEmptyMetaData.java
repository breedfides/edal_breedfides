/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData}
 * for persistence with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("7")
public final class MyEmptyMetaData extends MyUntypedData {

	private static final long serialVersionUID = 7842671382148442580L;
	private static final String EMPTY_STRING = "none";

	/**
	 * Default constructor for {@link MyEmptyMetaData} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyEmptyMetaData() {
	}

	/**
	 * Copy constructor to convert public
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData}
	 * to private {@link MyEmptyMetaData}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyEmptyMetaData(UntypedData edal) {
		super();
		this.setString(EMPTY_STRING);
	}
}