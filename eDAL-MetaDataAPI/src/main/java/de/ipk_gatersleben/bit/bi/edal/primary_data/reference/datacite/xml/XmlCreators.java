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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 * XML Mapping class for the 'creators' element.
 * 
 * @author arendd
 */
@XmlType(name = "creators")
public class XmlCreators implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The main researchers involved working on the data, or the authors of the
	 * publication in priority order. May be a corporate/institutional or
	 * personal name.
	 * 
	 */
	private List<XmlCreator> creatorList;

	/**
	 * Default constructor
	 */
	public XmlCreators() {
		super();
		this.creatorList = new ArrayList<XmlCreator>();
	}

	/**
	 * Add a {@link XmlCreator} to the {@link List} of {@link XmlCreators}.
	 * 
	 * @param creator
	 *            the {@link XmlCreator} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addCreator(final XmlCreator creator) throws IndexOutOfBoundsException {
		this.creatorList.add(creator);
	}

	/**
	 * Add a {@link XmlCreator} to the {@link List} of {@link XmlCreators} at a
	 * specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlCreator}.
	 * @param creator
	 *            the {@link XmlCreator} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addCreator(final int index, final XmlCreator creator) throws IndexOutOfBoundsException {
		this.creatorList.add(index, creator);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlCreator}.
	 * 
	 * @return an Iterator over all {@link XmlCreator} elements.
	 */
	public Iterator<? extends XmlCreator> enumerateCreator() {
		return this.creatorList.iterator();
	}

	/**
	 * Return the {@link XmlCreator} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlCreator}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlCreator} at the given index
	 */
	public XmlCreator getCreator(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.creatorList.size()) {
			throw new IndexOutOfBoundsException(
					"getCreator: Index value '" + index + "' not in range [0.." + (this.creatorList.size() - 1) + "]");
		}

		return (XmlCreator) creatorList.get(index);
	}

	/**
	 * Returns the contents of the {@link List} in an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return an Array of {@link XmlCreator}.
	 */
	public XmlCreator[] getCreator() {
		XmlCreator[] array = new XmlCreator[0];
		return (XmlCreator[]) this.creatorList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlCreator}.
	 * 
	 * @return the size of this collection
	 */
	public int getCreatorCount() {
		return this.creatorList.size();
	}

	/**
	 * Remove all {@link XmlCreator} in the {@link List}.
	 */
	public void removeAllCreator() {
		this.creatorList.clear();
	}

	/**
	 * Remove a specified {@link XmlCreator}.
	 * 
	 * @param creator
	 *            the {@link XmlCreator} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeCreator(final XmlCreator creator) {
		return creatorList.remove(creator);
	}

	/**
	 * Remove a {@link XmlCreator} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlCreator} to remove.
	 * @return the element removed from the collection
	 */
	public XmlCreator removeCreatorAt(final int index) {
		return (XmlCreator) this.creatorList.remove(index);
	}

	/**
	 * Set the {@link XmlCreator} at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlCreator}.
	 * @param creator
	 *            the {@link XmlCreator} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setCreator(final int index, final XmlCreator creator) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.creatorList.size()) {
			throw new IndexOutOfBoundsException(
					"setCreator: Index value '" + index + "' not in range [0.." + (this.creatorList.size() - 1) + "]");
		}

		this.creatorList.set(index, creator);
	}

	/**
	 * Set a all {@link XmlCreator} to the {@link List}.
	 * 
	 * @param creatorArray
	 *            the array of creators to set
	 */
	public void setCreator(final XmlCreator[] creatorArray) {
		// -- copy array
		creatorList.clear();

		for (int i = 0; i < creatorArray.length; i++) {
			this.creatorList.add(creatorArray[i]);
		}
	}
}