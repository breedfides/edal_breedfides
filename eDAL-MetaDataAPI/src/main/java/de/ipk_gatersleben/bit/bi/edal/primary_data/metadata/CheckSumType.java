/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * Data type to define a Checksum for a file.
 * 
 * @author arendd
 */
public class CheckSumType extends UntypedData {

	private static final long serialVersionUID = 1L;

	private String algorithm;

	private String checkSum;

	/**
	 * Default constructor with specified algorithm and the corresponding
	 * checksum
	 * 
	 * @param algorithm
	 *            the algorithm to calculate the checksum.
	 * @param checkSum
	 *            the calculated checksum.
	 */
	public CheckSumType(String algorithm, String checkSum) {
		super();

		this.algorithm = algorithm;
		this.checkSum = checkSum;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof CheckSumType) {

			CheckSumType checkSumType = (CheckSumType) datatype;

			if (this.getAlgorithm().compareTo(checkSumType.getAlgorithm()) == 0) {
				if (this.getCheckSum().compareTo(checkSumType.getCheckSum()) == 0) {
					return super.compareTo(checkSumType);
				} else {
					return this.getCheckSum().compareTo(
							checkSumType.getCheckSum());
				}
			} else {
				return this.getAlgorithm().compareTo(
						checkSumType.getAlgorithm());
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the checkSum
	 */
	public String getCheckSum() {
		return checkSum;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @param checkSum
	 *            the checkSum to set
	 */
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	@Override
	public String toString() {
		return "CheckSumType [algorithm=" + algorithm + ", checkSum="
				+ checkSum + "]";
	}

}
