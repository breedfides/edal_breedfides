/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;

@Entity
public class MyEdalDate implements Serializable {

	private static final long serialVersionUID = 6150419465225565484L;
	private int id;
	private Calendar startDate;
	private EdalDatePrecision startPrecision;
	private String event;

	/**
	 * Default constructor for {@link MyEdalDate} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyEdalDate() {
	}

	public MyEdalDate(Calendar startDate, EdalDatePrecision startPrecision,
			String event) {
		this.setEvent(event);
		this.setStartDate(startDate);
		this.setStartPrecision(startPrecision);
	}

	public MyEdalDate(EdalDate date) {

		this(date.getStartDate(), date.getStartPrecision(), date.getString());

	}

	/**
	 * @return the startDate
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the startPrecision
	 */
	@Enumerated(EnumType.ORDINAL)
	public EdalDatePrecision getStartPrecision() {
		return startPrecision;
	}

	/**
	 * @param startPrecision
	 *            the startPrecision to set
	 */
	public void setStartPrecision(EdalDatePrecision startPrecision) {
		this.startPrecision = startPrecision;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	public EdalDate toEdalTimePoint() {

		EdalDate point = new EdalDate(this.getStartDate(),
				this.getStartPrecision(), this.getEvent());

		return point;
	}

}
