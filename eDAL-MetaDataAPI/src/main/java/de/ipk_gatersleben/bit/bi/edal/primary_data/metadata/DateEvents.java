/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Collection of {@link EdalDate} objects to define the dates and ranges of
 * different events,e.g. date of creation...
 * 
 * @author arendd
 */
public class DateEvents extends UntypedData implements Set<EdalDate> {

	private static final long serialVersionUID = 8853287738100670768L;
	private Set<EdalDate> set;

	/**
	 * Default constructor to create a {@link DateEvents} object with a defined
	 * description.
	 * 
	 * @param description
	 *            the event for this {@link DateEvents}.
	 */
	public DateEvents(String description) {
		super(description);
		this.set = new HashSet<EdalDate>();
	}

	/**
	 * @param date
	 *            the date to add
	 * @return true or false
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(EdalDate date) {
		return set.add(date);
	}

	/**
	 * @param collection
	 *            the collection to add
	 * @return true or false
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends EdalDate> collection) {
		return set.addAll(collection);
	}

	/**
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		set.clear();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof DateEvents) {

			DateEvents dateEvents = (DateEvents) datatype;

			if (this.getSet().equals(dateEvents.getSet())) {
				if (this.getString().equals(dateEvents.getString())) {
					return 0;
				} else {
					return this.getString().compareTo(dateEvents.getString());
				}
			} else if (this.getSet().containsAll(dateEvents.getSet())) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * @param object
	 *            the object to match
	 * @return true or false
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object object) {
		return set.contains(object);
	}

	/**
	 * @param collection
	 *            the collection to match
	 * @return true or false
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> collection) {
		return set.containsAll(collection);
	}

	/**
	 * @param object
	 *            the object to compare
	 * @return true or false
	 * @see java.util.Set#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (this.compareTo((UntypedData) object) == 0) {
			return true;
		}
		return false;

	}

	/**
	 * @return the set
	 */
	public Set<EdalDate> getSet() {
		return set;
	}

	/**
	 * @return hashCode
	 * @see java.util.Set#hashCode()
	 */
	public int hashCode() {
		return set.hashCode();
	}

	/**
	 * @return true or false
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * @return {@link Iterator} of {@link EdalDate}
	 * @see java.util.Set#iterator()
	 */
	public Iterator<EdalDate> iterator() {
		return set.iterator();
	}

	/**
	 * @param object
	 *            the object to remove
	 * @return true or false
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return set.remove(object);
	}

	/**
	 * @param collection
	 *            the collection to remove
	 * @return true or false
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> collection) {
		return set.removeAll(collection);
	}

	/**
	 * @param collection
	 *            the collection to retain
	 * @return true or false
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> collection) {
		return set.retainAll(collection);
	}

	/**
	 * @param set
	 *            the set to set
	 */
	public void setSet(Set<EdalDate> set) {
		this.set = set;
	}

	/**
	 * @return size
	 * @see java.util.Set#size()
	 */
	public int size() {
		return set.size();
	}

	/**
	 * @return {@link Object} array
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return set.toArray();
	}

	/**
	 * @param array
	 *            the array to convert
	 * @return array
	 * @see java.util.Set#toArray(Object[])
	 */
	public <T> T[] toArray(T[] array) {
		return set.toArray(array);
	}

	/** {@inheritDoc} */

	@Override
	public String toString() {
		if (this.getSet().isEmpty()) {
			return "none";
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("Event: " + this.getString() + ",");
			for (EdalDate date : this.getSet()) {
				stringBuffer.append(date.toString() + ",");
			}
			return stringBuffer.toString();
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toHTML() {
		if (this.getSet().isEmpty()) {
			return "none";
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("Event: " + this.getString() + "<br/>");
			for (EdalDate date : this.getSet()) {
				stringBuffer.append(date.toString() + "<br/>");
			}
			return stringBuffer.toString();
		}
	}

}
