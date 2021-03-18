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
import javax.persistence.Transient;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Indexed;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link DataType} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("5")
@Indexed
public final class MyDataType extends MyUntypedData {

	private static final long serialVersionUID = 3076515727137927942L;

	/**
	 * Default constructor for {@link MyDataType} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected MyDataType() {

	}

	/**
	 * Copy constructor to convert public {@link DataType} to private
	 * {@link MyDataType}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDataType(UntypedData edal) {
		super(edal);
		if (edal instanceof DataType) {
			DataType datatype = (DataType) edal;
			this.setDataType(datatype.getDataType());
		}
	}

	/**
	 * Getter for the field <code>_data_type</code>.
	 * 
	 * @return a {@link EnumDCMIDataType} object.
	 */
	@Transient
	public EnumDCMIDataType getDataType() {
		return EnumDCMIDataType.valueOf(getString());
	}

	/**
	 * Setter for the field <code>dataType</code>.
	 * 
	 * @param dataType
	 *            a {@link EnumDCMIDataType} object.
	 */
	public void setDataType(EnumDCMIDataType dataType) {
		this.setString(dataType.toString());
	}

}
