/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
