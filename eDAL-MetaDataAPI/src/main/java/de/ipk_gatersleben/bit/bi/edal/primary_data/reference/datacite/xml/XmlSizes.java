/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import javax.xml.bind.annotation.XmlType;

/**
 * XML mapping class for the 'sizes' element.
 * 
 * @author arendd
 */
@XmlType(name = "sizes")
public class XmlSizes implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unstructured size information about the resource.
	 */
	private List<String> sizeList;

	public XmlSizes() {
		super();
		this.sizeList = new ArrayList<String>();
	}

	/**
	 * Add a size to the {@link List} of sizes.
	 * 
	 * @param bytes
	 *            the number of bytes to convert into a {@link String}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addSize(final long bytes) throws IndexOutOfBoundsException {
		this.sizeList.add(humanReadableByteCount(bytes, true));
	}

	/**
	 * Add a size to the {@link List} of sizes.
	 * 
	 * @param size
	 *            the size to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addSize(final String size) throws IndexOutOfBoundsException {
		this.sizeList.add(size);
	}

	/**
	 * Add a size to the {@link List} of size at a specified position.
	 * 
	 * @param index
	 *            the position to insert the size.
	 * @param size
	 *            the size to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addSize(final int index, final String size) throws IndexOutOfBoundsException {
		this.sizeList.add(index, size);
	}

	/**
	 * Return an {@link Iterator} of all sizes.
	 * 
	 * @return an Iterator over all sizes elements.
	 */
	public Iterator<? extends String> enumerateSize() {
		return this.sizeList.iterator();
	}

	/**
	 * Return the size at the specified position in the {@link List}.
	 * 
	 * @param index
	 *            the position of the searched size.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the size at the given index
	 */
	public String getSize(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.sizeList.size()) {
			throw new IndexOutOfBoundsException(
					"getSize: Index value '" + index + "' not in range [0.." + (this.sizeList.size() - 1) + "]");
		}

		return (String) sizeList.get(index);
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
	public String[] getSize() {
		String[] array = new String[0];
		return (String[]) this.sizeList.toArray(array);
	}

	/**
	 * Return the number of the sizes.
	 * 
	 * @return the size of this collection
	 */
	public int getSizeCount() {
		return this.sizeList.size();
	}

	/**
	 * Remove all sizes in the {@link List}.
	 */
	public void removeAllSize() {
		this.sizeList.clear();
	}

	/**
	 * Remove a specified size.
	 * 
	 * @param size
	 *            the size to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeSize(final String size) {
		return sizeList.remove(size);
	}

	/**
	 * Remove a size at the specified position.
	 * 
	 * @param index
	 *            the position of the size to remove.
	 * @return the element removed from the collection
	 */
	public String removeSizeAt(final int index) {
		return (String) this.sizeList.remove(index);
	}

	/**
	 * Set the size at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the size.
	 * @param size
	 *            the size to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setSize(final int index, final String size) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.sizeList.size()) {
			throw new IndexOutOfBoundsException(
					"setSize: Index value '" + index + "' not in range [0.." + (this.sizeList.size() - 1) + "]");
		}

		this.sizeList.set(index, size);
	}

	/**
	 * Set a all sizes to the {@link List}.
	 * 
	 * @param sizeArray
	 *            teh array of sizes to set
	 */
	public void setSize(final String[] sizeArray) {
		// -- copy array
		sizeList.clear();

		for (int i = 0; i < sizeArray.length; i++) {
			this.sizeList.add(sizeArray[i]);
		}
	}

	private String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
