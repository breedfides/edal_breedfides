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

/**
 * XML mapping class for the 'titles' element.
 * 
 * @author arendd
 */
@XmlType(name = "titles")
public class XmlTitles implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A name or title by which a resource is known.
	 */
	private List<XmlTitle> titleList;

	/**
	 * Default constructor.
	 */
	public XmlTitles() {
		super();
		this.titleList = new ArrayList<XmlTitle>();
	}

	/**
	 * Add a {@link XmlTitle} to the {@link List} of {@link XmlTitle}.
	 * 
	 * @param title
	 *            the {@link XmlTitle} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addTitle(final XmlTitle title) throws IndexOutOfBoundsException {
		this.titleList.add(title);
	}

	/**
	 * Add a {@link XmlTitle} to the {@link List} of {@link XmlTitle} at a
	 * specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlTitle}.
	 * @param title
	 *            the {@link XmlTitle} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addTitle(final int index, final XmlTitle title) throws IndexOutOfBoundsException {
		this.titleList.add(index, title);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlTitle}.
	 * 
	 * @return an Iterator over all {@link XmlTitle} elements.
	 */
	public Iterator<? extends XmlTitle> enumerateTitle() {
		return this.titleList.iterator();
	}

	/**
	 * Return the {@link XmlTitle} at the specified position in the {@link List}
	 * .
	 * 
	 * @param index
	 *            the position of the searched {@link XmlTitle}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlTitle} at the given index
	 */
	public XmlTitle getTitle(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.titleList.size()) {
			throw new IndexOutOfBoundsException(
					"getTitle: Index value '" + index + "' not in range [0.." + (this.titleList.size() - 1) + "]");
		}

		return (XmlTitle) titleList.get(index);
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
	public XmlTitle[] getTitle() {
		XmlTitle[] array = new XmlTitle[0];
		return (XmlTitle[]) this.titleList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlTitle}.
	 * 
	 * @return the size of this collection
	 */
	public int getTitleCount() {
		return this.titleList.size();
	}

	/**
	 * Remove all {@link XmlTitle} in the {@link List}.
	 */
	public void removeAllTitle() {
		this.titleList.clear();
	}

	/**
	 * Remove a specified {@link XmlTitle}.
	 * 
	 * @param title
	 *            the {@link XmlTitle} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeTitle(final XmlTitle title) {
		return titleList.remove(title);
	}

	/**
	 * Remove a {@link XmlTitle} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlTitle} to remove.
	 * @return the element removed from the collection
	 */
	public XmlTitle removeTitleAt(final int index) {
		return (XmlTitle) this.titleList.remove(index);
	}

	/**
	 * Set the {@link XmlTitle} at the specified position of the {@link List}.
	 * 
	 * @param title
	 *            the position to set the {@link XmlTitle}.
	 * @param index
	 *            the position to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setTitle(final int index, final XmlTitle title) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.titleList.size()) {
			throw new IndexOutOfBoundsException(
					"setTitle: Index value '" + index + "' not in range [0.." + (this.titleList.size() - 1) + "]");
		}

		this.titleList.set(index, title);
	}

	/**
	 * Set a all {@link XmlTitle} to the {@link List}.
	 * 
	 * @param titleArray
	 *            the array of titles to set
	 */
	public void setTitle(final XmlTitle[] titleArray) {
		// -- copy array
		titleList.clear();

		for (int i = 0; i < titleArray.length; i++) {
			this.titleList.add(titleArray[i]);
		}
	}
}