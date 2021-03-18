/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UnknownMetaData}
 * for persistence with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("10")
@Indexed
public final class MyUnknownMetaData extends MyUntypedData {

	private static final long serialVersionUID = -6571988459150163982L;
	private static final String UNKNOWN_STRING = "unknown";

	/**
	 * Default constructor for {@link MyUnknownMetaData} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyUnknownMetaData() {
	}

	/**
	 * Copy constructor to convert public
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UnknownMetaData}
	 * to private {@link MyUnknownMetaData}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyUnknownMetaData(UntypedData edal) {
		super();
		this.setString(UNKNOWN_STRING);
	}
}