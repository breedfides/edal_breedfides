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
 * XML Mapping class for the 'formats' element.
 * 
 * @author arendd
 */
@XmlType(name = "formats")
public class XmlFormats implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Technical format of the resource.
	 */
	private List<String> formatList;

	/**
	 * Default constructor
	 */
	public XmlFormats() {
		super();
		this.formatList = new ArrayList<String>();
	}

	/**
	 * Add a {@link XmlFormats} to the {@link List} of {@link XmlCreators}.
	 * 
	 * @param format
	 *            the {@link XmlFormats} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection.
	 */
	public void addFormat(final String format) throws IndexOutOfBoundsException {
		this.formatList.add(format);
	}

	/**
	 * Add a {@link XmlFormats} to the {@link List} of {@link XmlCreators} at a
	 * specified position.
	 * 
	 * @param index
	 *            the position to insert the {@link XmlFormats}.
	 * @param format
	 *            the {@link XmlFormats} to add.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addFormat(final int index, final String format)
			throws IndexOutOfBoundsException {
		this.formatList.add(index, format);
	}

	/**
	 * Return an {@link Iterator} of all {@link XmlFormats}.
	 * 
	 * @return an Iterator over all {@link XmlFormats} elements.
	 */
	public Iterator<? extends String> enumerateFormat() {
		return this.formatList.iterator();
	}

	/**
	 * Return the {@link XmlFormats} at the specified position in the
	 * {@link List}.
	 * 
	 * @param index
	 *            the position of the searched {@link XmlFormats}.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the {@link XmlFormats} at the given index
	 */
	public String getFormat(final int index) throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.formatList.size()) {
			throw new IndexOutOfBoundsException("getFormat: Index value '"
					+ index + "' not in range [0.."
					+ (this.formatList.size() - 1) + "]");
		}

		return (String) formatList.get(index);
	}

	/**
	 * Returns the contents of the {@link List} in an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return an Array of {@link XmlFormats}.
	 */
	public String[] getFormat() {
		String[] array = new String[0];
		return (String[]) this.formatList.toArray(array);
	}

	/**
	 * Return the number of the {@link XmlFormats}.
	 * 
	 * @return the size of this collection
	 */
	public int getFormatCount() {
		return this.formatList.size();
	}

	/**
	 * Remove all {@link XmlFormats} in the {@link List}.
	 */
	public void removeAllFormat() {
		this.formatList.clear();
	}

	/**
	 * Remove a specified {@link XmlFormats}.
	 * 
	 * @param format
	 *            the {@link XmlFormats} to remove.
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeFormat(final String format) {
		return formatList.remove(format);
	}

	/**
	 * Remove a {@link XmlFormats} at the specified position.
	 * 
	 * @param index
	 *            the position of the {@link XmlFormats} to remove.
	 * @return the element removed from the collection
	 */
	public String removeFormatAt(final int index) {
		return (String) this.formatList.remove(index);
	}

	/**
	 * Set the {@link XmlFormats} at the specified position of the {@link List}.
	 * 
	 * @param index
	 *            the position to set the {@link XmlFormats}.
	 * @param format
	 *            the {@link XmlFormats} to set.
	 * @throws IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setFormat(final int index, final String format)
			throws IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.formatList.size()) {
			throw new IndexOutOfBoundsException("setFormat: Index value '"
					+ index + "' not in range [0.."
					+ (this.formatList.size() - 1) + "]");
		}

		this.formatList.set(index, format);
	}

	/**
	 * Set a all {@link XmlFormats} to the {@link List}.
	 * 
	 * @param formatArray the array of formats to set
	 */
	public void setFormat(final String[] formatArray) {
		// -- copy array
		formatList.clear();

		for (int i = 0; i < formatArray.length; i++) {
			this.formatList.add(formatArray[i]);
		}
	}

}
