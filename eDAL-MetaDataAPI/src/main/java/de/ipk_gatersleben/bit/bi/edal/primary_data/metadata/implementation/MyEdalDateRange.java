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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;

@Entity
public class MyEdalDateRange extends MyEdalDate {

	private static final long serialVersionUID = -7530496611195743725L;
	private Calendar endDate;
	private EdalDatePrecision endPrecision;

	/**
	 * Default constructor for {@link MyEdalDateRange} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyEdalDateRange() {
	}

	public MyEdalDateRange(Calendar startDate,
			EdalDatePrecision startPrecision, Calendar endDate,
			EdalDatePrecision endPrecision, String event) {
		super(startDate, startPrecision, event);
		this.setEndDate(endDate);
		this.setEndPrecision(endPrecision);
	}

	public MyEdalDateRange(EdalDateRange range) {
		this(range.getStartDate(), range.getStartPrecision(), range
				.getEndDate(), range.getEndPrecision(), range.getString());
	}

	/**
	 * @return the endDate
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the endPrecision
	 */
	@Enumerated(EnumType.ORDINAL)
	public EdalDatePrecision getEndPrecision() {
		return endPrecision;
	}

	/**
	 * @param endPrecision
	 *            the endPrecision to set
	 */
	public void setEndPrecision(EdalDatePrecision endPrecision) {
		this.endPrecision = endPrecision;
	}

	public EdalDateRange toEdalTimeRange() {

		EdalDateRange range = new EdalDateRange(this.getStartDate(),
				this.getStartPrecision(), this.getEndDate(),
				this.getEndPrecision(), this.getEvent());

		return range;
	}
}
