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

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
