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

import java.util.Calendar;

/**
 * Data type to describe a time range.
 * 
 * @author arendd
 */
public class EdalDateRange extends EdalDate {

	private static final long serialVersionUID = 9203936460020980866L;

	private Calendar endDate;
	private EdalDatePrecision endPrecision;

	/**
	 * Default constructor to create a {@link EdalDateRange} with a specified
	 * startDate, startPrecision, endDate, endPrecision and event description.
	 * 
	 * @param startDate
	 *            the start date of the time range.
	 * @param startPrecision
	 *            the precision of the given start date.
	 * @param endDate
	 *            the end date of the time range
	 * @param endPrecision
	 *            the precision of the given end date.
	 * @param event
	 *            the description of the time range.
	 */
	public EdalDateRange(Calendar startDate, EdalDatePrecision startPrecision,
			Calendar endDate, EdalDatePrecision endPrecision, String event) {
		super(startDate, startPrecision, event);

		if (endDate.before(startDate)) {
			throw new IllegalArgumentException(
					"Can not set an endDate, that is before the startDate !");
		}

		this.setEndDate(endDate);
		this.setEndPrecision(endPrecision);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((endPrecision == null) ? 0 : endPrecision.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdalDateRange other = (EdalDateRange) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (endPrecision != other.endPrecision)
			return false;
		return true;
	}

	@Override
	public int compareTo(UntypedData object) {

		if (object instanceof EdalDateRange) {

			EdalDateRange timerange = (EdalDateRange) object;

			if (this.getStartDate().compareTo(timerange.getStartDate()) == 0) {
				if (this.getStartPrecision().compareTo(
						timerange.getStartPrecision()) == 0) {
					if (this.getEndDate().compareTo(timerange.getEndDate()) == 0) {
						if (this.getEndPrecision().compareTo(
								timerange.getEndPrecision()) == 0) {
							return super.compareTo(object);

						} else {
							return this.getEndPrecision().compareTo(
									timerange.getEndPrecision());
						}
					} else {
						return this.getEndDate().compareTo(
								timerange.getEndDate());
					}
				} else {
					return this.getStartPrecision().compareTo(
							timerange.getStartPrecision());
				}
			} else {
				return this.getStartDate().compareTo(timerange.getStartDate());
			}
		}
		return super.compareTo(object);
	}

	/**
	 * Getter for the endDate of this {@link EdalDateRange}.
	 * 
	 * @return the endDate
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Getter for the endPrecission of this {@link EdalDateRange}.
	 * 
	 * @return the endPrecision
	 */
	public EdalDatePrecision getEndPrecision() {
		return endPrecision;
	}

	/**
	 * Setter for the endDate of this {@link EdalDateRange}.
	 * 
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	/**
	 * Setter for the endPrecision of this {@link EdalDateRange}.
	 * 
	 * @param endPrecision
	 *            the endPrecision to set
	 */
	public void setEndPrecision(EdalDatePrecision endPrecision) {
		this.endPrecision = endPrecision;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (!this.getEvent().equals(UntypedData.EMPTY)) {
			sb.append(this.getEvent() + ": ");
		}
		if (this.getStartDate() != null && this.getEndDate() != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("TimeRange: From: " + this.getStartDate().getTime()
					+ " To: " + this.getEndDate().getTime());
		}

		return sb.toString();
	}

}
