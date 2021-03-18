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
 * XML mapping class for the 'subjects' element.
 * 
 * @author arendd
 */

@XmlType(name = "subjects")
public class XmlSubjects implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Subject, keywords, classification codes, or key phrases describing the
	 * resource.
	 */
	private List<XmlSubject> subjectList;

	/**
	 * Default constructor.
	 */
	public XmlSubjects() {
		super();
		this.subjectList = new ArrayList<XmlSubject>();
	}

	/**
	 * Add a {@link XmlSubject} to the {@link List} of {@link XmlSubject}.
	 * 
	 * @param subject
	 *            the {@link XmlSubject} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addSubject(final XmlSubject subject) throws IndexOutOfBoundsException {
		this.subjectList.add(subject);
	}

	/**
	 * Add a {@link XmlSubject} to the {@link List} of {@link XmlSubject} at a
	 * specified position.
	 * 
	 * @param subject
	 *            the position to insert the {@link XmlSubject}.
	 * @param index
	 *            the position of the {@link XmlSubject} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addSubject(final int index, final XmlSubject subject) throws IndexOutOfBoundsException {
		this.subjectList.add(index, subject);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlSubject}.
	 * 
	 * @return an Iterator over all {@link XmlSubject} elements.
	 */
	public Iterator<? extends XmlSubject> enumerateSubject() {
		return this.subjectList.iterator();
	}

	/**
	 * Return the {@link XmlSubject} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlSubject}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlSubject} at the given index
	 */
	public XmlSubject getSubject(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.subjectList.size()) {
			throw new IndexOutOfBoundsException(
					"getSubject: Index value '" + index + "' not in range [0.." + (this.subjectList.size() - 1) + "]");
		}

		return (XmlSubject) subjectList.get(index);
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
	public XmlSubject[] getSubject() {
		XmlSubject[] array = new XmlSubject[0];
		return (XmlSubject[]) this.subjectList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlSubject}.
	 * 
	 * @return the size of this collection
	 */
	public int getSubjectCount() {
		return this.subjectList.size();
	}

	/**
	 * Remove all {@link XmlSubject} in the {@link List}.
	 */
	public void removeAllSubject() {
		this.subjectList.clear();
	}

	/**
	 * Remove a specified {@link XmlSubject}.
	 * 
	 * @param subject
	 *            the {@link XmlSubject} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeSubject(final XmlSubject subject) {
		return subjectList.remove(subject);
	}

	/**
	 * Remove a {@link XmlSubject} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlSubject} to remove.
	 * @return the element removed from the collection
	 */
	public XmlSubject removeSubjectAt(final int index) {
		return (XmlSubject) this.subjectList.remove(index);
	}

	/**
	 * Set the {@link XmlSubject} at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlSubject}.
	 * @param subject
	 *            the {@link XmlSubject} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setSubject(final int index, final XmlSubject subject) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.subjectList.size()) {
			throw new IndexOutOfBoundsException(
					"setSubject: Index value '" + index + "' not in range [0.." + (this.subjectList.size() - 1) + "]");
		}

		this.subjectList.set(index, subject);
	}

	/**
	 * Set a all {@link XmlSubject} to the {@link List}.
	 * 
	 * @param subjectArray
	 *            the array of subjects to set
	 */
	public void setSubject(final XmlSubject[] subjectArray) {
		// -- copy array
		subjectList.clear();

		for (int i = 0; i < subjectArray.length; i++) {
			this.subjectList.add(subjectArray[i]);
		}
	}
}