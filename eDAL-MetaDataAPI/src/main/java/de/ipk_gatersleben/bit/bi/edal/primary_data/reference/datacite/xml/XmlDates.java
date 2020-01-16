/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.DateType;

/**
 * XML mapping class for the 'dates' element.
 * 
 * @author arendd
 */
@XmlType(name = "dates")
public class XmlDates implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Different dates relevant to the work.
	 */
	private List<XmlDate> dateList;

	/**
	 * Default constructor
	 */
	public XmlDates() {
		super();
		this.dateList = new ArrayList<XmlDate>();
	}

	public XmlDates(DateEvents elementValue) {
		this();

		Set<EdalDate> set = elementValue.getSet();

		for (EdalDate timePoint : set) {

			if (timePoint instanceof EdalDateRange) {

				EdalDateRange range = (EdalDateRange) timePoint;

				XmlDate xmlDate = new XmlDate();
				xmlDate.setValue(String.format("%tF %<tR", range.getStartDate().getTime()) + "/"
						+ String.format("%tF %<tR", range.getEndDate().getTime()));
				xmlDate.setDateType(DateType.Created);
				this.addDate(xmlDate);

			} else if (timePoint instanceof EdalDate) {
				XmlDate xmlDate = new XmlDate();
				xmlDate.setValue(String.format("%tF %<tR", timePoint.getStartDate().getTime()));
				xmlDate.setDateType(DateType.Created);
				this.addDate(xmlDate);
			}
		}

	}

	/**
	 * Add a {@link XmlDate} to the {@link List} of {@link XmlDate}.
	 * 
	 * @param date
	 *            the {@link XmlDate} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addDate(final XmlDate date) throws IndexOutOfBoundsException {
		this.dateList.add(date);
	}

	/**
	 * Add a {@link XmlDate} to the {@link List} of {@link XmlDate} at a
	 * specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlDate}.
	 * @param date
	 *            the {@link XmlDate} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addDate(final int index, final XmlDate date) throws IndexOutOfBoundsException {
		this.dateList.add(index, date);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlDate}.
	 * 
	 * @return an Iterator over all {@link XmlDate} elements.
	 */
	public Iterator<? extends XmlDate> enumerateDate() {
		return this.dateList.iterator();
	}

	/**
	 * Return the {@link XmlDate} at the specified position in the {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlDate}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlDate} at the given index
	 */
	public XmlDate getDate(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.dateList.size()) {
			throw new IndexOutOfBoundsException(
					"getDate: Index value '" + index + "' not in range [0.." + (this.dateList.size() - 1) + "]");
		}

		return (XmlDate) dateList.get(index);
	}

	/**
	 * Returns the contents of the collection in an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public XmlDate[] getDate() {
		XmlDate[] array = new XmlDate[0];
		return (XmlDate[]) this.dateList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlDate}.
	 * 
	 * @return the size of this collection
	 */
	public int getDateCount() {
		return this.dateList.size();
	}

	/**
	 * Remove all {@link XmlDate} in the {@link List}.
	 */
	public void removeAllDate() {
		this.dateList.clear();
	}

	/**
	 * Remove a specified {@link XmlDate}.
	 * 
	 * @param date
	 *            the {@link XmlDate} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeDate(final XmlDate date) {
		return dateList.remove(date);
	}

	/**
	 * Remove a {@link XmlDate} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlDate} to remove.
	 * @return the element removed from the collection
	 */
	public XmlDate removeDateAt(final int index) {
		return (XmlDate) this.dateList.remove(index);
	}

	/**
	 * Set the {@link XmlDate} at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlDate}.
	 * @param date
	 *            the {@link XmlDate} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setDate(final int index, final XmlDate date) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.dateList.size()) {
			throw new IndexOutOfBoundsException(
					"setDate: Index value '" + index + "' not in range [0.." + (this.dateList.size() - 1) + "]");
		}

		this.dateList.set(index, date);
	}

	/**
	 * Set a all {@link XmlDate} to the {@link List}.
	 * 
	 * @param dateArray
	 *            the array of dates to set
	 */
	public void setDate(final XmlDate[] dateArray) {
		// -- copy array
		dateList.clear();

		for (int i = 0; i < dateArray.length; i++) {
			this.dateList.add(dateArray[i]);
		}
	}
}