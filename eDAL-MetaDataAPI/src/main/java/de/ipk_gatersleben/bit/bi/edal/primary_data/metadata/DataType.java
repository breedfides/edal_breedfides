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
