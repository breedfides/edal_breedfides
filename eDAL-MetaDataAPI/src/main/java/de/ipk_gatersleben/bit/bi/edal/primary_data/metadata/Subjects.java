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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Subjects extends UntypedData implements Set<UntypedData> {

	private static final long serialVersionUID = -5326637568531513414L;

	private LinkedHashSet<UntypedData> subjects;

	public Subjects() {
		super();
		this.subjects = new LinkedHashSet<UntypedData>();
	}

	@Override
	public boolean add(UntypedData e) {
		return subjects.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends UntypedData> c) {
		return subjects.addAll(c);
	}

	@Override
	public void clear() {
		subjects.clear();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Subjects) {

			Subjects subjects = (Subjects) datatype;

			if (this.getSubjects().equals(subjects.getSubjects())) {
				return 0;
			} else if (this.getSubjects().containsAll(subjects.getSubjects())) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	@Override
	public boolean contains(Object o) {
		return subjects.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return subjects.containsAll(c);
	}

	/**
	 * @return the subjects
	 */
	public Set<UntypedData> getSubjects() {
		return subjects;
	}

	@Override
	public boolean isEmpty() {
		return subjects.isEmpty();
	}

	@Override
	public Iterator<UntypedData> iterator() {
		return subjects.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return subjects.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return subjects.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return subjects.retainAll(c);
	}

	/**
	 * @param subjects
	 *            the subjects to set
	 */
	public void setSubjects(LinkedHashSet<UntypedData> subjects) {
		this.subjects = subjects;
	}

	@Override
	public int size() {
		return subjects.size();
	}

	@Override
	public Object[] toArray() {
		return subjects.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return subjects.toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public String toHTML() {
		return this.toString();
	}

	@Override
	public String toString() {
		if (this.subjects.isEmpty()) {
			return "none";
		}

		String subjects = this.subjects.toString();

		return subjects.substring(subjects.indexOf("[")+1, subjects.indexOf("]"));
	}

}
