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
 * XML mapping class for the 'alternativeIdentifiers' element.
 * 
 * @author arendd
 */
@XmlType(name = "alternativeIdentifiers")
public class XmlAlternateIdentifiers implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A {@link List} of {@link XmlAlternateIdentifier}.
	 */
	private List<XmlAlternateIdentifier> alternateIdentifierList;

	/**
	 * Default constructor.
	 */
	public XmlAlternateIdentifiers() {
		super();
		this.alternateIdentifierList = new ArrayList<XmlAlternateIdentifier>();
	}

	/**
	 * Add a {@link XmlAlternateIdentifier} to the {@link List} of
	 * {@link XmlAlternateIdentifier}.
	 * 
	 * @param alternateIdentifier
	 *            the {@link XmlAlternateIdentifier} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addAlternateIdentifier(final XmlAlternateIdentifier alternateIdentifier)
			throws IndexOutOfBoundsException {
		this.alternateIdentifierList.add(alternateIdentifier);
	}

	/**
	 * Add a {@link XmlAlternateIdentifier} to the {@link List} of
	 * {@link XmlAlternateIdentifier} at a specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlAlternateIdentifier}.
	 * @param alternateIdentifier
	 *            the {@link XmlAlternateIdentifier} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addAlternateIdentifier(final int index, final XmlAlternateIdentifier alternateIdentifier)
			throws IndexOutOfBoundsException {
		this.alternateIdentifierList.add(index, alternateIdentifier);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlAlternateIdentifier}.
	 * 
	 * @return an Iterator over all {@link XmlAlternateIdentifier} elements.
	 */
	public Iterator<? extends XmlAlternateIdentifier> enumerateAlternateIdentifier() {
		return this.alternateIdentifierList.iterator();
	}

	/**
	 * Return the {@link XmlAlternateIdentifier} at the specified position in
	 * the {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlAlternateIdentifier}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlAlternateIdentifier} at the given
	 *         index
	 */
	public XmlAlternateIdentifier getAlternateIdentifier(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.alternateIdentifierList.size()) {
			throw new IndexOutOfBoundsException("getAlternateIdentifier: Index value '" + index + "' not in range [0.."
					+ (this.alternateIdentifierList.size() - 1) + "]");
		}

		return (XmlAlternateIdentifier) alternateIdentifierList.get(index);
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
	public XmlAlternateIdentifier[] getAlternateIdentifier() {
		XmlAlternateIdentifier[] array = new XmlAlternateIdentifier[0];
		return (XmlAlternateIdentifier[]) this.alternateIdentifierList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlAlternateIdentifier}.
	 * 
	 * @return the size of this collection
	 */
	public int getAlternateIdentifierCount() {
		return this.alternateIdentifierList.size();
	}

	/**
	 * Remove all {@link XmlAlternateIdentifier} in the {@link List}.
	 */
	public void removeAllAlternateIdentifier() {
		this.alternateIdentifierList.clear();
	}

	/**
	 * Remove a specified {@link XmlAlternateIdentifier}.
	 * 
	 * @param alternateIdentifier
	 *            the {@link XmlAlternateIdentifier} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeAlternateIdentifier(final XmlAlternateIdentifier alternateIdentifier) {
		return alternateIdentifierList.remove(alternateIdentifier);
	}

	/**
	 * Remove a {@link XmlAlternateIdentifier} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlAlternateIdentifier} to remove.
	 * @return the element removed from the collection
	 */
	public XmlAlternateIdentifier removeAlternateIdentifierAt(final int index) {
		return (XmlAlternateIdentifier) this.alternateIdentifierList.remove(index);
	}

	/**
	 * Set the {@link XmlAlternateIdentifier} at the specified position of the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlAlternateIdentifier}.
	 * @param alternateIdentifier
	 *            the {@link XmlAlternateIdentifier} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setAlternateIdentifier(final int index, final XmlAlternateIdentifier alternateIdentifier)
			throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.alternateIdentifierList.size()) {
			throw new IndexOutOfBoundsException("setAlternateIdentifier: Index value '" + index + "' not in range [0.."
					+ (this.alternateIdentifierList.size() - 1) + "]");
		}

		this.alternateIdentifierList.set(index, alternateIdentifier);
	}

	/**
	 * Set a all {@link XmlAlternateIdentifier} to the {@link List}.
	 * 
	 * @param alternateIdentifierArray
	 *            the array of alternate identifiers to set
	 */
	public void setAlternateIdentifier(final XmlAlternateIdentifier[] alternateIdentifierArray) {
		// -- copy array
		alternateIdentifierList.clear();

		for (int i = 0; i < alternateIdentifierArray.length; i++) {
			this.alternateIdentifierList.add(alternateIdentifierArray[i]);
		}
	}
}