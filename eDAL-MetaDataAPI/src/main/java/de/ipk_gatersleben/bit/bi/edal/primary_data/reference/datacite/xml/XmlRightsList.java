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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

@XmlType(name = "rightsList")
public class XmlRightsList implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Different dates relevant to the work.
	 */
	private List<XmlRights> rightsList;

	/**
	 * Default constructor
	 */
	public XmlRightsList() {
		super();
		this.rightsList = new ArrayList<XmlRights>();
	}

	public XmlRightsList(UntypedData elementValue) {
		this();
		XmlRights rights = new XmlRights(elementValue.toString());
		this.addRights(rights);
	}

	/**
	 * Add a {@link XmlRights} to the {@link List} of {@link XmlRights}.
	 * 
	 * @param rights
	 *            the {@link XmlRights} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addRights(final XmlRights rights) throws IndexOutOfBoundsException {
		this.rightsList.add(rights);
	}

	/**
	 * Add a {@link XmlRights} to the {@link List} of {@link XmlRights} at a
	 * specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlRights}.
	 * @param rigths
	 *            the {@link XmlRights} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addRights(final int index, final XmlRights rigths) throws IndexOutOfBoundsException {
		this.rightsList.add(index, rigths);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlRights}.
	 * 
	 * @return an Iterator over all {@link XmlRights} elements.
	 */
	public Iterator<? extends XmlRights> enumerateRights() {
		return this.rightsList.iterator();
	}

	/**
	 * Return the {@link XmlRights} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlRights}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlRights} at the given index
	 */
	public XmlRights getRights(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.rightsList.size()) {
			throw new IndexOutOfBoundsException(
					"getDate: Index value '" + index + "' not in range [0.." + (this.rightsList.size() - 1) + "]");
		}

		return (XmlRights) rightsList.get(index);
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
	public XmlRights[] getRights() {
		XmlRights[] array = new XmlRights[0];
		return (XmlRights[]) this.rightsList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlRights}.
	 * 
	 * @return the size of this collection
	 */
	public int getRightsCount() {
		return this.rightsList.size();
	}

	/**
	 * Remove all {@link XmlRights} in the {@link List}.
	 */
	public void removeAllRights() {
		this.rightsList.clear();
	}

	/**
	 * Remove a specified {@link XmlRights}.
	 * 
	 * @param date
	 *            the {@link XmlRights} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeRights(final XmlRights date) {
		return rightsList.remove(date);
	}

	/**
	 * Remove a {@link XmlRights} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlRights} to remove.
	 * @return the element removed from the collection
	 */
	public XmlRights removeRightAt(final int index) {
		return (XmlRights) this.rightsList.remove(index);
	}

	/**
	 * Set the {@link XmlRights} at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlRights}.
	 * @param rights
	 *            the {@link XmlRights} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setRights(final int index, final XmlRights rights) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.rightsList.size()) {
			throw new IndexOutOfBoundsException(
					"setRights: Index value '" + index + "' not in range [0.." + (this.rightsList.size() - 1) + "]");
		}

		this.rightsList.set(index, rights);
	}

	/**
	 * Set a all {@link XmlRights} to the {@link List}.
	 * 
	 * @param rightsArray
	 *            the array of rights to set
	 */
	public void setRights(final XmlRights[] rightsArray) {
		// -- copy array
		rightsList.clear();

		for (int i = 0; i < rightsArray.length; i++) {
			this.rightsList.add(rightsArray[i]);
		}
	}

}
