/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Data type to manage the different {@link CheckSumType}s of a file.
 * 
 * @author arendd
 */
public class CheckSum extends UntypedData implements SortedSet<CheckSumType> {

	private static final long serialVersionUID = 1L;
	private TreeSet<CheckSumType> set;

	/**
	 * Construct a {@link CheckSum} object with an empty list of
	 * {@link CheckSumType}s.
	 */
	public CheckSum() {
		this.set = new TreeSet<CheckSumType>();

	}

	/** {@inheritDoc} */
	@Override
	public boolean add(CheckSumType e) {
		return this.set.add(e);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(Collection<? extends CheckSumType> c) {
		return this.set.addAll(c);
	}

	/**
	 * @param type
	 *            the type to match
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#ceiling(java.lang.Object)
	 */
	public CheckSumType ceiling(CheckSumType type) {
		return set.ceiling(type);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.set.clear();
	}

	/**
	 * @return {@link Object}
	 * @see java.util.TreeSet#clone()
	 */
	public Object clone() {
		return set.clone();
	}

	/**
	 * @return {@link Comparator} of {@link CheckSumType}
	 * @see java.util.TreeSet#comparator()
	 */
	public Comparator<? super CheckSumType> comparator() {
		return set.comparator();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof CheckSum) {

			CheckSum checkSum = (CheckSum) datatype;

			if (this.getSet().equals(checkSum.getSet())) {
				return 0;
			} else if (this.getSet().containsAll(checkSum.getSet())) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		return this.set.contains(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.set.containsAll(c);
	}

	/**
	 * @return {@link Iterator} of {@link CheckSumType}
	 * @see java.util.TreeSet#descendingIterator()
	 */
	public Iterator<CheckSumType> descendingIterator() {
		return set.descendingIterator();
	}

	/**
	 * @return {@link NavigableSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#descendingSet()
	 */
	public NavigableSet<CheckSumType> descendingSet() {
		return set.descendingSet();
	}

	/**
	 * @param object
	 *            the object to compare
	 * @return true or false
	 * @see java.util.AbstractSet#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return set.equals(object);
	}

	/**
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#first()
	 */
	public CheckSumType first() {
		return set.first();
	}

	/**
	 * @param type
	 *            the type to match
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#floor(java.lang.Object)
	 */
	public CheckSumType floor(CheckSumType type) {
		return set.floor(type);
	}

	/**
	 * @return the _set
	 */
	public Set<CheckSumType> getSet() {
		return set;
	}

	/**
	 * @return hashCode
	 * @see java.util.AbstractSet#hashCode()
	 */
	public int hashCode() {
		return set.hashCode();
	}

	/**
	 * @param toElement
	 *            the type to match
	 * @return {@link SortedSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#headSet(java.lang.Object)
	 */
	public SortedSet<CheckSumType> headSet(CheckSumType toElement) {
		return set.headSet(toElement);
	}

	/**
	 * @param toElement
	 *            the type to match
	 * @param inclusive
	 *            if inclusive or not
	 * @return {@link NavigableSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#headSet(java.lang.Object, boolean)
	 */
	public NavigableSet<CheckSumType> headSet(CheckSumType toElement, boolean inclusive) {
		return set.headSet(toElement, inclusive);
	}

	/**
	 * @param type
	 *            the type to match
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#higher(java.lang.Object)
	 */
	public CheckSumType higher(CheckSumType type) {
		return set.higher(type);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<CheckSumType> iterator() {
		return this.set.iterator();
	}

	/**
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#last()
	 */
	public CheckSumType last() {
		return set.last();
	}

	/**
	 * @param type
	 *            the type to match
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#lower(java.lang.Object)
	 */
	public CheckSumType lower(CheckSumType type) {
		return set.lower(type);
	}

	/**
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#pollFirst()
	 */
	public CheckSumType pollFirst() {
		return set.pollFirst();
	}

	/**
	 * @return {@link CheckSumType}
	 * @see java.util.TreeSet#pollLast()
	 */
	public CheckSumType pollLast() {
		return set.pollLast();
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(Object o) {
		return this.set.remove(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.set.removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.set.retainAll(c);
	}

	/**
	 * @param set
	 *            the _set to set
	 */
	public void setSet(TreeSet<CheckSumType> set) {
		this.set = set;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.set.size();
	}

	/**
	 * @param fromElement
	 *            the "from" type to match
	 * @param fromInclusive
	 *            if inclusive or not
	 * @param toElement
	 *            the "to" type to match
	 * @param toInclusive
	 *            if inclusive or not
	 * @return {@link NavigableSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#subSet(java.lang.Object, boolean,
	 *      java.lang.Object, boolean)
	 */
	public NavigableSet<CheckSumType> subSet(CheckSumType fromElement, boolean fromInclusive, CheckSumType toElement,
			boolean toInclusive) {
		return set.subSet(fromElement, fromInclusive, toElement, toInclusive);
	}

	/**
	 * @param fromElement
	 *            the "from" type to match
	 * @param toElement
	 *            the "to" type to match
	 * @return {@link SortedSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet<CheckSumType> subSet(CheckSumType fromElement, CheckSumType toElement) {
		return set.subSet(fromElement, toElement);
	}

	/**
	 * @param fromElement
	 *            the "from" type to match
	 * @return {@link SortedSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#tailSet(java.lang.Object)
	 */
	public SortedSet<CheckSumType> tailSet(CheckSumType fromElement) {
		return set.tailSet(fromElement);
	}

	/**
	 * @param fromElement
	 *            the "from" type to match
	 * @param inclusive
	 *            if inclusive or not
	 * @return {@link NavigableSet} of {@link CheckSumType}
	 * @see java.util.TreeSet#tailSet(java.lang.Object, boolean)
	 */
	public NavigableSet<CheckSumType> tailSet(CheckSumType fromElement, boolean inclusive) {
		return set.tailSet(fromElement, inclusive);
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.set.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.set.toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.set.isEmpty()) {
			return "none";
		} else {

			StringBuffer stringBuffer = new StringBuffer();
		
			int size = this.getSet().size();
			int count = 0;
			
			for (CheckSumType checkSumType : this.getSet()) {
				stringBuffer.append(checkSumType.getAlgorithm() + " : " + checkSumType.getCheckSum());
				count++;
				if(count<size) {
					stringBuffer.append("\n");
				}
			}
			return stringBuffer.toString();
		}
	}
}