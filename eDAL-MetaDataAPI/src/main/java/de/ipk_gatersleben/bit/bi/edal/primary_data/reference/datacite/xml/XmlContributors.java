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
import javax.xml.bind.annotation.XmlType;

/**
 * XML mapping class for the 'contributors' element.
 * 
 * @author arendd
 */
@XmlType(name = "contributors")
public class XmlContributors implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The institution or person responsible for collecting, creating, or
	 * otherwise contributing to the development of the data set.
	 */
	private List<XmlContributor> contributorList;

	/**
	 * Default constructor.
	 */
	public XmlContributors() {
		super();
		this.contributorList = new ArrayList<XmlContributor>();
	}

	/**
	 * Add a {@link XmlContributor} to the {@link List} of
	 * {@link XmlContributor}.
	 * 
	 * @param contributor
	 *            the {@link XmlContributor} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addContributor(final XmlContributor contributor)
			throws IndexOutOfBoundsException {
		this.contributorList.add(contributor);
	}

	/**
	 * Add a {@link XmlContributor} to the {@link List} of
	 * {@link XmlContributor} at a specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlContributor}.
	 * @param contributor
	 *            the {@link XmlContributor} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addContributor(final int index, final XmlContributor contributor)
			throws IndexOutOfBoundsException {
		this.contributorList.add(index, contributor);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlContributor}.
	 * 
	 * @return an Iterator over all {@link XmlContributor} elements.
	 */
	public Iterator<? extends XmlContributor> enumerateContributor() {
		return this.contributorList.iterator();
	}

	/**
	 * Return the {@link XmlContributor} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlContributor}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlContributor} at the given index
	 */
	public XmlContributor getContributor(final int index)
			throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.contributorList.size()) {
			throw new IndexOutOfBoundsException("getContributor: Index value '"
					+ index + "' not in range [0.."
					+ (this.contributorList.size() - 1) + "]");
		}

		return (XmlContributor) contributorList.get(index);
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
	public XmlContributor[] getContributor() {
		XmlContributor[] array = new XmlContributor[0];
		return (XmlContributor[]) this.contributorList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlContributor}.
	 * 
	 * @return the size of this collection
	 */
	public int getContributorCount() {
		return this.contributorList.size();
	}

	/**
	 * Remove all {@link XmlContributor} in the {@link List}.
	 */
	public void removeAllContributor() {
		this.contributorList.clear();
	}

	/**
	 * Remove a specified {@link XmlContributor}.
	 * 
	 * @param contributor
	 *            the {@link XmlContributor} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeContributor(final XmlContributor contributor) {
		return contributorList.remove(contributor);
	}

	/**
	 * Remove a {@link XmlContributor} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlContributor} to remove.
	 * @return the element removed from the collection
	 */
	public XmlContributor removeContributorAt(final int index) {
		return (XmlContributor) this.contributorList.remove(index);
	}

	/**
	 * Set the {@link XmlContributor} at the specified position of the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlContributor}.
	 * @param contributor
	 *            the {@link XmlContributor} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setContributor(final int index, final XmlContributor contributor)
			throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.contributorList.size()) {
			throw new IndexOutOfBoundsException("setContributor: Index value '"
					+ index + "' not in range [0.."
					+ (this.contributorList.size() - 1) + "]");
		}

		this.contributorList.set(index, contributor);
	}

	/**
	 * Set a all {@link XmlContributor} to the {@link List}.
	 * 
	 * @param contributorArray
	 *            the array of {@link XmlContributor} to set.
	 */
	public void setContributor(final XmlContributor[] contributorArray) {
		// -- copy array
		contributorList.clear();

		for (int i = 0; i < contributorArray.length; i++) {
			this.contributorList.add(contributorArray[i]);
		}
	}
}