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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link CheckSum} for persistence with
 * <em>HIBERNATE</em>
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("12")
public final class MyCheckSum extends MyUntypedData {

	private static final long serialVersionUID = 1L;

	private Set<MyCheckSumType> dataSet;

	/**
	 * Default constructor for {@link MyCheckSum} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyCheckSum() {
	}

	/**
	 * Copy constructor to convert public {@link CheckSum} to private
	 * {@link MyCheckSum}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyCheckSum(final UntypedData edal) {

		super(edal);

		if (edal instanceof CheckSum) {
			CheckSum checkSum = (CheckSum) edal;

			Set<MyCheckSumType> myCheckSumSet = new HashSet<MyCheckSumType>();

			if (!checkSum.getSet().isEmpty()) {
				for (CheckSumType checkSumType : checkSum.getSet()) {
					myCheckSumSet.add(new MyCheckSumType(checkSumType));
				}
			}

			this.setDataSet(myCheckSumSet);
		}
	}

	/**
	 * @return the dataSet
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UntypedData_CheckSum", joinColumns = @JoinColumn(name = "UNTYPEDDATA_ID"))
	public Set<MyCheckSumType> getDataSet() {
		return dataSet;
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public void setDataSet(Set<MyCheckSumType> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * Convert this {@link MyCheckSum} to a public {@link CheckSum}.
	 * 
	 * @return a {@link CheckSum} object.
	 */
	public CheckSum toCheckSum() {

		TreeSet<CheckSumType> checkSumSet = new TreeSet<CheckSumType>();

		for (MyCheckSumType myCheckSumType : this.getDataSet()) {
			checkSumSet.add(myCheckSumType.toCheckSumType());
		}

		CheckSum checkSum = new CheckSum();
		checkSum.setSet(checkSumSet);

		return checkSum;

	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "MyCheckSum [_set=" + dataSet + "]";
	}
}
