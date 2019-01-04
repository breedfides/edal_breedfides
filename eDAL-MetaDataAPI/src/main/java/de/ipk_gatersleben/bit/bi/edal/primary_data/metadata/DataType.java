/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Management of DCMI data types.
 * <p>
 * It is a general, cross-domain list of approved terms that may be used to
 * identify the genre of a resource.
 * <p>
 * The types are defined in <a
 * href="http://dublincore.org/documents/dcmi-type-vocabulary"
 * target="_blank">DCMITYPE</a>.
 * 
 * @author lange
 * @author arendd
 */
public class DataType extends UntypedData {

	private static final long serialVersionUID = -2435046027195248969L;

	/**
	 * Construct a {@link DataType} object using a {@link Enum} type of
	 * {@link EnumDCMIDataType}.
	 * 
	 * @param dataType
	 *            the {@link EnumDCMIDataType} enum.
	 */
	public DataType(final EnumDCMIDataType dataType) {
		/**
		 * Store the EnumDCMIDataType as String in the string value of the
		 * UntypedData super class
		 */
		super(dataType.toString());
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof DataType) {

			DataType type = (DataType) datatype;

			if (this.getDataType().compareTo(type.getDataType()) == 0) {
				return super.compareTo(datatype);
			} else {
				return this.getDataType().compareTo(type.getDataType());
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * Getter for the field <code>dataType</code>.
	 * 
	 * @return the data_type.
	 */
	public EnumDCMIDataType getDataType() {

		/**
		 * Store the EnumDCMIDataType as String in the string value of the
		 * UntypedData super class
		 */
		return EnumDCMIDataType.valueOf(this.getString());
	}

	/**
	 * Setter for the field <code>dataType</code>.
	 * 
	 * @param dataType
	 *            the dataType to set.
	 */
	public void setDataType(final EnumDCMIDataType dataType) {
		/**
		 * load the EnumDCMIDataType as String from the string value of the
		 * UntypedData super class
		 */
		this.setString(dataType.toString());
	}
}
