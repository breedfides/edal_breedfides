/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Collection of untyped relations to other {@link MetaData} objects.
 * 
 * @author lange
 * @author arendd
 * 
 */
public class IdentifierRelation extends UntypedData implements SortedSet<Identifier> {
	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = 2513489835142576007L;
	private TreeSet<Identifier> relations;

	/**
	 * Construct an IdentifierRelation with an empty list of relations.
	 */
	public IdentifierRelation() {
		this.relations = new TreeSet<Identifier>();
	}

	/**
	 * Add a {@link Identifier} to this {@link IdentifierRelation} set.
	 * 
	 * @param identifier
	 *            the identifier to add
	 * @return <code>true</code> if successful;<code>false</code> otherwise.
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(final Identifier identifier) {
		return this.relations.add(identifier);
	}

	/**
	 * Add a {@link Collection} of {@link Identifier}s to this
	 * {@link IdentifierRelation} set.
	 * 
	 * @param collection
	 *            the collection of identifiers to add
	 * @return <code>true</code> if successful;<code>false</code> otherwise.
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends Identifier> collection) {
		return this.relations.addAll(collection);
	}

	/**
	 * @param identifier
	 *            to identifier to match
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#ceiling(java.lang.Object)
	 */
	public Identifier ceiling(Identifier identifier) {
		return relations.ceiling(identifier);
	}

	/**
	 * Clear the {@link IdentifierRelation} {@link java.util.Set}.
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.relations.clear();
	}

	/**
	 * @return {@link Object}
	 * @see java.util.TreeSet#clone()
	 */
	public Object clone() {
		return relations.clone();
	}

	/**
	 * @return {@link Comparator} of {@link Identifier}
	 * @see java.util.TreeSet#comparator()
	 */
	public Comparator<? super Identifier> comparator() {
		return relations.comparator();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof IdentifierRelation) {

			IdentifierRelation identifierRelation = (IdentifierRelation) datatype;

			if (this.getRelations().equals(identifierRelation.getRelations())) {
				return 0;
			} else if (this.getRelations().containsAll(identifierRelation.getRelations())) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Check if an the {@link Object} is in the {@link IdentifierRelation} set.
	 * 
	 * @param object
	 *            to object to check
	 * @return <code>true</code> if the the {@link java.util.Set} contains the
	 *         object; <code>false</code> otherwise.
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object object) {
		return this.relations.contains(object);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Check if an the {@link Collection} is in the {@link IdentifierRelation}
	 * set.
	 * 
	 * @return <code>true</code> if the the {@link java.util.Set} contains the
	 *         {@link Collection}; <code>false</code> otherwise.
	 * 
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.relations.containsAll(c);
	}

	/**
	 * @return {@link Iterator} of {@link Identifier}
	 * @see java.util.TreeSet#descendingIterator()
	 */
	public Iterator<Identifier> descendingIterator() {
		return relations.descendingIterator();
	}

	/**
	 * @return {@link NavigableSet} of {@link Identifier}
	 * @see java.util.TreeSet#descendingSet()
	 */
	public NavigableSet<Identifier> descendingSet() {
		return relations.descendingSet();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object o) {
		return this.relations.equals(o);
	}

	/**
	 * @return first {@link Identifier}
	 * @see java.util.TreeSet#first()
	 */
	public Identifier first() {
		return relations.first();
	}

	/**
	 * @param identifier
	 *            the identifier to match
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#floor(java.lang.Object)
	 */
	public Identifier floor(Identifier identifier) {
		return relations.floor(identifier);
	}

	/**
	 * Getter for the field <code>_relations</code>.
	 * 
	 * @return the _relations
	 */
	public Collection<Identifier> getRelations() {
		return this.relations;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.relations.hashCode();
	}

	/**
	 * @param toElement
	 *            the identifier to match
	 * @return {@link SortedSet} of {@link Identifier}
	 * @see java.util.TreeSet#headSet(java.lang.Object)
	 */
	public SortedSet<Identifier> headSet(Identifier toElement) {
		return relations.headSet(toElement);
	}

	/**
	 * @param toElement
	 *            the identifier to match
	 * @param inclusive
	 *            if inclusive or not
	 * @return {@link NavigableSet} of {@link Identifier}
	 * @see java.util.TreeSet#headSet(java.lang.Object, boolean)
	 */
	public NavigableSet<Identifier> headSet(Identifier toElement, boolean inclusive) {
		return relations.headSet(toElement, inclusive);
	}

	/**
	 * @param identifier
	 *            the identifier to match
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#higher(java.lang.Object)
	 */
	public Identifier higher(Identifier identifier) {
		return relations.higher(identifier);
	}

	/**
	 * Check if the {@link Collection} of {@link IdentifierRelation} is empty.
	 * 
	 * @return <code>true</code> if {@link Collection} is empty;
	 *         <code>false</code> otherwise.
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.relations.isEmpty();
	}

	/**
	 * Iterator for the {@link Collection} of {@link IdentifierRelation}.
	 * 
	 * @return the {@link Iterator}
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<Identifier> iterator() {
		return this.relations.iterator();
	}

	/**
	 * @return last {@link Identifier}
	 * @see java.util.TreeSet#last()
	 */
	public Identifier last() {
		return relations.last();
	}

	/**
	 * @param identifier
	 *            the identifier to match
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#lower(java.lang.Object)
	 */
	public Identifier lower(Identifier identifier) {
		return relations.lower(identifier);
	}

	/**
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#pollFirst()
	 */
	public Identifier pollFirst() {
		return relations.pollFirst();
	}

	/**
	 * @return {@link Identifier}
	 * @see java.util.TreeSet#pollLast()
	 */
	public Identifier pollLast() {
		return relations.pollLast();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Remove the {@link Object} from the {@link IdentifierRelation} set.
	 * 
	 * @return <code>true</code> if successful; <code>false</code> otherwise.
	 * 
	 */
	@Override
	public boolean remove(final Object o) {
		return this.relations.remove(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Remove the {@link Collection} from the {@link IdentifierRelation} set.
	 * 
	 * @return <code>true</code> if successful; <code>false</code> otherwise.
	 * 
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.relations.removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Retains only the object in the {@link Collection} in the
	 * {@link IdentifierRelation} set.
	 * 
	 * Removes all other {@link Object}s from the {@link IdentifierRelation}
	 * set.
	 * 
	 * @return <code>true</code> if successful; <code>false</code> otherwise.
	 * 
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		return this.relations.retainAll(c);
	}

	/**
	 * Setter for the field <code>_relations</code>.
	 * 
	 * @param relations
	 *            the _relations to set
	 */
	public void setRelations(final TreeSet<Identifier> relations) {
		this.relations = relations;
	}

	/**
	 * Get the size of the {@link IdentifierRelation} {@link Collection}.
	 * 
	 * @return size the size of the relation collection
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return this.relations.size();
	}

	/**
	 * @param fromElement
	 *            the 'from' identifier to match
	 * @param fromInclusive
	 *            if inclusive or not
	 * @param toElement
	 *            the 'to' identifier to match
	 * @param toInclusive
	 *            if inclusive or not
	 * @return {@link NavigableSet} of {@link Identifier}
	 * @see java.util.TreeSet#subSet(java.lang.Object, boolean,
	 *      java.lang.Object, boolean)
	 */
	public NavigableSet<Identifier> subSet(Identifier fromElement, boolean fromInclusive, Identifier toElement,
			boolean toInclusive) {
		return relations.subSet(fromElement, fromInclusive, toElement, toInclusive);
	}

	/**
	 * @param fromElement
	 *            the 'from' identifier to match
	 * @param toElement
	 *            the 'to' identifier to match
	 * @return {@link SortedSet} of {@link Identifier}
	 * @see java.util.TreeSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet<Identifier> subSet(Identifier fromElement, Identifier toElement) {
		return relations.subSet(fromElement, toElement);
	}

	/**
	 * @param fromElement
	 *            the 'from' identifier to match
	 * @return {@link SortedSet} of {@link Identifier}
	 * @see java.util.TreeSet#tailSet(java.lang.Object)
	 */
	public SortedSet<Identifier> tailSet(Identifier fromElement) {
		return relations.tailSet(fromElement);
	}

	/**
	 * @param fromElement
	 *            the 'from' identifier to match
	 * @param inclusive
	 *            if inclusive or not
	 * 
	 * @return {@link NavigableSet} of {@link Identifier}
	 * @see java.util.TreeSet#tailSet(java.lang.Object, boolean)
	 */
	public NavigableSet<Identifier> tailSet(Identifier fromElement, boolean inclusive) {
		return relations.tailSet(fromElement, inclusive);
	}

	/**
	 * Convert the {@link Collection} to an array of {@link Object}s.
	 * 
	 * @return Object[]
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.relations.toArray();
	}

	/**
	 * Convert the {@link Collection} to an array of T objects.
	 * 
	 * @param a
	 *            an array of T objects.
	 * @return T[]
	 * @param <T>
	 *            a T object.
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return this.relations.toArray(a);
	}

	@Override
	public String toString() {
		if (this.relations.isEmpty()) {
			return "none";
		}
		return this.relations.toString();
	}

}
