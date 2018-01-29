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
 * XML mapping class for the 'descriptions' element.
 * 
 * @author arendd
 */
@XmlType(name = "descriptions")
public class XmlDescriptions implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Field descriptionList.
	 */
	private List<XmlDescription> descriptionList;

	/**
	 * Default constructor.
	 */
	public XmlDescriptions() {
		super();
		this.descriptionList = new ArrayList<XmlDescription>();
	}

	/**
	 * Add a {@link XmlDescription} to the {@link List} of
	 * {@link XmlDescription}.
	 * 
	 * @param description
	 *            the {@link XmlDescription} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addDescription(final XmlDescription description) throws IndexOutOfBoundsException {
		this.descriptionList.add(description);
	}

	/**
	 * Add a {@link XmlDescription} to the {@link List} of
	 * {@link XmlDescription} at a specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlDescription}.
	 * @param description
	 *            the {@link XmlDescription} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addDescription(final int index, final XmlDescription description) throws IndexOutOfBoundsException {
		this.descriptionList.add(index, description);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlDescription}.
	 * 
	 * @return an Iterator over all {@link XmlDescription} elements.
	 */
	public Iterator<? extends XmlDescription> enumerateDescription() {
		return this.descriptionList.iterator();
	}

	/**
	 * Return the {@link XmlDescription} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlDescription}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlDescription} at the given index
	 */
	public XmlDescription getDescription(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.descriptionList.size()) {
			throw new IndexOutOfBoundsException("getDescription: Index value '" + index + "' not in range [0.."
					+ (this.descriptionList.size() - 1) + "]");
		}

		return (XmlDescription) descriptionList.get(index);
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
	public XmlDescription[] getDescription() {
		XmlDescription[] array = new XmlDescription[0];
		return (XmlDescription[]) this.descriptionList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlDescription}.
	 * 
	 * @return the size of this collection
	 */
	public int getDescriptionCount() {
		return this.descriptionList.size();
	}

	/**
	 * Remove all {@link XmlDescription} in the {@link List}.
	 */
	public void removeAllDescription() {
		this.descriptionList.clear();
	}

	/**
	 * Remove a specified {@link XmlDescription}.
	 * 
	 * @param description
	 *            the {@link XmlDescription} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeDescription(final XmlDescription description) {
		return descriptionList.remove(description);
	}

	/**
	 * Remove a {@link XmlDescription} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlDescription} to remove.
	 * @return the element removed from the collection
	 */
	public XmlDescription removeDescriptionAt(final int index) {
		return (XmlDescription) this.descriptionList.remove(index);
	}

	/**
	 * Set the {@link XmlDescription} at the specified position of the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlDescription}.
	 * @param description
	 *            the {@link XmlDescription} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setDescription(final int index, final XmlDescription description) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.descriptionList.size()) {
			throw new IndexOutOfBoundsException("setDescription: Index value '" + index + "' not in range [0.."
					+ (this.descriptionList.size() - 1) + "]");
		}

		this.descriptionList.set(index, description);
	}

	/**
	 * Set a all {@link XmlDescription} to the {@link List}.
	 * 
	 * @param descriptionArray
	 *            the array of descriptions to set
	 */
	public void setDescription(final XmlDescription[] descriptionArray) {
		// -- copy array
		descriptionList.clear();

		for (int i = 0; i < descriptionArray.length; i++) {
			this.descriptionList.add(descriptionArray[i]);
		}
	}
}