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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link CheckSumType} for persistence with
 * <em>HIBERNATE</em>
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("11")
@Indexed
public final class MyCheckSumType extends MyUntypedData {

	private static final long serialVersionUID = 1L;

	private String algorithm;
	private String checkSum;

	/**
	 * Default constructor for {@link MyCheckSumType} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyCheckSumType() {
	}

	/**
	 * Copy constructor to convert public {@link CheckSumType} to private
	 * {@link MyCheckSumType}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyCheckSumType(final UntypedData edal) {

		super(edal);

		if (edal instanceof CheckSumType) {

			CheckSumType checkSumType = (CheckSumType) edal;

			this.setAlgorithm(checkSumType.getAlgorithm());
			this.setCheckSum(checkSumType.getCheckSum());
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

	/**
	 * Convert this {@link MyCheckSumType} to a public {@link CheckSumType}.
	 * 
	 * @return a {@link CheckSumType} object.
	 */
	public CheckSumType toCheckSumType() {

		CheckSumType checkSumType = new CheckSumType(this.getAlgorithm(),
				this.getCheckSum());

		return checkSumType;
	}

	@Override
	public String toString() {
		return "MyCheckSumType [algorithm=" + algorithm + ", checkSum="
				+ checkSum + "]";
	}
}