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

import org.apache.lucene.document.Field.Store;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.LanguageBridge;
@Entity
@Indexed
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
    @GenericField()
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
