/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.util.Calendar;

/**
 * Data type to describe a time point.
 * 
 * @author arendd
 */
public class EdalDate extends UntypedData {

	/**
	 * @author lange non exclusive list of event standard types
	 */
	static public enum STANDART_EVENT_TYPES {
		CREATED, UPDATED, CHANGED
	}

	private static final long serialVersionUID = -1172719176052596677L;
	private Calendar startDate;

	private EdalDatePrecision startPrecision;

	/**
	 * Default constructor to create a {@link EdalDate} with a specified date,
	 * precision and event description.
	 * 
	 * @param startDate
	 *            the date of the time point.
	 * @param startPrecision
	 *            the precision of the given date.
	 * @param event
	 *            the description of the time point.
	 */
	public EdalDate(final Calendar startDate,
			final EdalDatePrecision startPrecision, final String event) {
		super(event);
		this.setStartDate(startDate);
		this.setStartPrecision(startPrecision);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((startPrecision == null) ? 0 : startPrecision.hashCode());
		result = prime * result
				+ ((string == null) ? 0 : string.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdalDate other = (EdalDate) obj;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (startPrecision != other.startPrecision)
			return false;
		if(!this.string.equals(other.string)){
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final UntypedData object) {

		if (object instanceof EdalDate) {

			final EdalDate timepoint = (EdalDate) object;

			if (this.getStartDate().compareTo(timepoint.getStartDate()) == 0) {
				if (this.getStartPrecision().compareTo(
						timepoint.getStartPrecision()) == 0) {
					return super.compareTo(object);
				} else {
					return this.getStartPrecision().compareTo(
							timepoint.getStartPrecision());
				}
			} else {
				return this.getStartDate().compareTo(timepoint.getStartDate());
			}
		}
		return super.compareTo(object);
	}

	/**
	 * Getter for the event of this {@link EdalDate}.
	 * 
	 * @return the event of this {@link EdalDate}.
	 */
	public String getEvent() {
		return super.string;
	}

	/**
	 * Getter for the startDate of this {@link EdalDate}.
	 * 
	 * @return the startDate
	 */
	public Calendar getStartDate() {
		return this.startDate;
	}

	/**
	 * Getter for the precision of this {@link EdalDate}.
	 * 
	 * @return the startPrecision
	 */
	public EdalDatePrecision getStartPrecision() {
		return this.startPrecision;
	}

	/**
	 * Setter for the event of this {@link EdalDate}.
	 * 
	 * @param event
	 *            the event to set.
	 */
	public void setEvent(final String event) {
		super.setString(event);
	}

	/**
	 * Setter for the startDate of this {@link EdalDate}.
	 * 
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(final Calendar startDate) {
		this.startDate = startDate;
	}

	/**
	 * Setter for the precision of this {@link EdalDate}.
	 * 
	 * @param startPrecision
	 *            the startPrecision to set
	 */
	public void setStartPrecision(final EdalDatePrecision startPrecision) {
		this.startPrecision = startPrecision;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		if (!this.getEvent().equals(UntypedData.EMPTY)) {
			sb.append(this.getEvent()+": ");
		}
		if (this.getStartDate() != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("TimePoint: " + this.getStartDate().getTime());
		}

		return sb.toString();
	}
}