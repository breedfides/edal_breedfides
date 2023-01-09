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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 * Class RelatedIdentifiers.
 * 
 * @author arendd
 */
@XmlType
public class XmlRelatedIdentifiers implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Identifiers of related resources. Use this property to indicate subsets
	 * of properties, as appropriate.
	 */
	private List<XmlRelatedIdentifier> relatedIdentifierList;

	public XmlRelatedIdentifiers() {
		super();
		this.relatedIdentifierList = new ArrayList<XmlRelatedIdentifier>();
	}

	/**
	 * @param relatedIdentifier
	 *            the related identifier to add
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addRelatedIdentifier(final XmlRelatedIdentifier relatedIdentifier) throws IndexOutOfBoundsException {
		this.relatedIdentifierList.add(relatedIdentifier);
	}

	/**
	 * @param index
	 *            the index to add the related identifier
	 * @param relatedIdentifier
	 *            the related identifier to add
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addRelatedIdentifier(final int index, final XmlRelatedIdentifier relatedIdentifier)
			throws IndexOutOfBoundsException {
		this.relatedIdentifierList.add(index, relatedIdentifier);
	}

	/**
	 * Method enumerateRelatedIdentifier.
	 * 
	 * @return an Enumeration over all DataCite.RelatedIdentifier elements
	 */
	public Iterator<? extends XmlRelatedIdentifier> enumerateRelatedIdentifier() {
		return this.relatedIdentifierList.iterator();
	}

	/**
	 * Method getRelatedIdentifier.
	 * 
	 * @param index
	 *            the index to get the related identifier
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the RelatedIdentifier at the given index
	 */
	public XmlRelatedIdentifier getRelatedIdentifier(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.relatedIdentifierList.size()) {
			throw new IndexOutOfBoundsException("getRelatedIdentifier: Index value '" + index + "' not in range [0.."
					+ (this.relatedIdentifierList.size() - 1) + "]");
		}

		return (XmlRelatedIdentifier) relatedIdentifierList.get(index);
	}

	/**
	 * Method getRelatedIdentifier.Returns the contents of the collection in an
	 * Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public XmlRelatedIdentifier[] getRelatedIdentifier() {
		XmlRelatedIdentifier[] array = new XmlRelatedIdentifier[0];
		return (XmlRelatedIdentifier[]) this.relatedIdentifierList.toArray(array);
	}

	/**
	 * Method getRelatedIdentifierCount.
	 * 
	 * @return the size of this collection
	 */
	public int getRelatedIdentifierCount() {
		return this.relatedIdentifierList.size();
	}

	/**
	 * Method removeAllRelatedIdentifier.
	 */
	public void removeAllRelatedIdentifier() {
		this.relatedIdentifierList.clear();
	}

	/**
	 * Method removeRelatedIdentifier.
	 * 
	 * @param vRelatedIdentifier
	 *            the related identifier to remove
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeRelatedIdentifier(final XmlRelatedIdentifier vRelatedIdentifier) {
		return relatedIdentifierList.remove(vRelatedIdentifier);
	}

	/**
	 * Method removeRelatedIdentifierAt.
	 * 
	 * @param index
	 *            the index to remove the related identifier
	 * @return the element removed from the collection
	 */
	public XmlRelatedIdentifier removeRelatedIdentifierAt(final int index) {
		return (XmlRelatedIdentifier) this.relatedIdentifierList.remove(index);
	}

	/**
	 * @param index
	 *            the index to set the related identifier
	 * @param vRelatedIdentifier
	 *            the related identifier to set
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setRelatedIdentifier(final int index, final XmlRelatedIdentifier vRelatedIdentifier)
			throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.relatedIdentifierList.size()) {
			throw new IndexOutOfBoundsException("setRelatedIdentifier: Index value '" + index + "' not in range [0.."
					+ (this.relatedIdentifierList.size() - 1) + "]");
		}

		this.relatedIdentifierList.set(index, vRelatedIdentifier);
	}

	/**
	 * @param vRelatedIdentifierArray
	 *            the array of related identifiers to set
	 */
	public void setRelatedIdentifier(final XmlRelatedIdentifier[] vRelatedIdentifierArray) {
		// -- copy array
		relatedIdentifierList.clear();

		for (int i = 0; i < vRelatedIdentifierArray.length; i++) {
			this.relatedIdentifierList.add(vRelatedIdentifierArray[i]);
		}
	}
}