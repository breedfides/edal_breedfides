/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Indexed;
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
