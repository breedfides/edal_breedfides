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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Persons extends UntypedData implements Set<Person> {

	private static final long serialVersionUID = 3723245589910167736L;

	private LinkedHashSet<Person> persons;

	public Persons() {
		super();
		this.setPersons(new LinkedHashSet<Person>());
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Persons) {

			Persons personlist = (Persons) datatype;

			if (this.getPersons().equals(personlist.getPersons())) {
				return 0;
			} else if (this.getPersons().containsAll(personlist.getPersons())) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 1;
		}
	}

	@Override
	public boolean add(Person e) {
		return persons.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Person> c) {
		return persons.addAll(c);
	}

	@Override
	public void clear() {
		persons.clear();
	}

	@Override
	public boolean contains(Object o) {
		return persons.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return persons.containsAll(c);
	}

	/**
	 * @return the persons
	 */
	public Set<Person> getPersons() {
		return persons;
	}

	@Override
	public boolean isEmpty() {
		return persons.isEmpty();
	}

	@Override
	public Iterator<Person> iterator() {
		return persons.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return persons.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return persons.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return persons.retainAll(c);
	}

	/**
	 * @param persons
	 *            the persons to set
	 */
	public void setPersons(LinkedHashSet<Person> persons) {
		this.persons = persons;
	}

	@Override
	public int size() {
		return persons.size();
	}

	@Override
	public Object[] toArray() {
		return persons.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return persons.toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public String toHTML() {
		if (this.getPersons().isEmpty()) {
			return "none";
		} else {
			Iterator<Person> it = this.iterator();
			if (!it.hasNext()) {
				return "[]";
			}

			StringBuilder sb = new StringBuilder();

			for (;;) {
				Person e = it.next();

				if (e instanceof NaturalPerson) {
					ORCID orcid = ((NaturalPerson) e).getOrcid();
					if (orcid != null) {
						sb.append("<a href=http://orcid.org/"+orcid.getOrcid()+">"+e.toHTML()+"</a>");
					} else {
						sb.append(e);
					}
				} else {
					sb.append(e);
				}

				if (!it.hasNext()) {
					return sb.append("").toString();
				}
				sb.append(';').append("<br/>");
			}
		}
	}

	@Override
	public String toString() {
		if (this.persons.isEmpty()) {
			return "none";
		}
		return this.persons.toString();
	}
}
