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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "geoLocations")
public class XmlGeoLocations implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Different geoLocations relevant to the work.
	 */
	private List<XmlGeoLocation> geoLocationList;

	/**
	 * Add a {@link XmlGeoLocation} to the {@link List} of
	 * {@link XmlGeoLocation}.
	 * 
	 * @param geoLocation
	 *            the {@link XmlGeoLocation} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addGeoLocation(final XmlGeoLocation geoLocation) throws IndexOutOfBoundsException {
		this.geoLocationList.add(geoLocation);
	}

	/**
	 * Add a {@link XmlGeoLocation} to the {@link List} of
	 * {@link XmlGeoLocation} at a specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlGeoLocation}.
	 * @param geoLocation
	 *            the {@link XmlGeoLocation} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addGeoLocation(final int index, final XmlGeoLocation geoLocation) throws IndexOutOfBoundsException {
		this.geoLocationList.add(index, geoLocation);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlGeoLocation}.
	 * 
	 * @return an Iterator over all {@link XmlGeoLocation} elements.
	 */
	public Iterator<? extends XmlGeoLocation> enumerateGeoLocation() {
		return this.geoLocationList.iterator();
	}

	/**
	 * Return the {@link XmlGeoLocation} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlGeoLocation}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlGeoLocation} at the given index
	 */
	public XmlGeoLocation getGeoLocation(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.geoLocationList.size()) {
			throw new IndexOutOfBoundsException("getGeoLocation: Index value '" + index + "' not in range [0.."
					+ (this.geoLocationList.size() - 1) + "]");
		}

		return (XmlGeoLocation) geoLocationList.get(index);
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
	public XmlGeoLocation[] getgeoLocation() {
		XmlGeoLocation[] array = new XmlGeoLocation[0];
		return (XmlGeoLocation[]) this.geoLocationList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlGeoLocation}.
	 * 
	 * @return the size of this collection
	 */
	public int getGeoLocationCount() {
		return this.geoLocationList.size();
	}

	/**
	 * Remove all {@link XmlGeoLocation} in the {@link List}.
	 */
	public void removeAllGeoLocation() {
		this.geoLocationList.clear();
	}

	/**
	 * Remove a specified {@link XmlGeoLocation}.
	 * 
	 * @param geoLocation
	 *            the {@link XmlGeoLocation} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeGeoLocation(final XmlGeoLocation geoLocation) {
		return geoLocationList.remove(geoLocation);
	}

	/**
	 * Remove a {@link XmlGeoLocation} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlGeoLocation} to remove.
	 * @return the element removed from the collection
	 */
	public XmlGeoLocation removeGeoLocationAt(final int index) {
		return (XmlGeoLocation) this.geoLocationList.remove(index);
	}

	/**
	 * Set the {@link XmlGeoLocation} at the specified position of the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlGeoLocation}.
	 * @param geoLocation
	 *            the {@link XmlGeoLocation} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setGeoLocation(final int index, final XmlGeoLocation geoLocation) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.geoLocationList.size()) {
			throw new IndexOutOfBoundsException("setGeoLocation: Index value '" + index + "' not in range [0.."
					+ (this.geoLocationList.size() - 1) + "]");
		}

		this.geoLocationList.set(index, geoLocation);
	}

	/**
	 * Set a all {@link XmlGeoLocation} to the {@link List}.
	 * 
	 * @param geoLocationArray
	 *            the array of geolocations to set
	 */
	public void setGeoLocation(final XmlGeoLocation[] geoLocationArray) {
		// -- copy array
		geoLocationList.clear();

		for (int i = 0; i < geoLocationArray.length; i++) {
			this.geoLocationList.add(geoLocationArray[i]);
		}
	}
}
