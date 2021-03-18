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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;

@Entity
@DiscriminatorValue("17")
@Indexed
public class MyPersons extends MyPerson {

	private static final long serialVersionUID = 3989021444510959618L;
	private Collection<MyPerson> persons;

	/**
	 * Default constructor is necessary for <em>HIBERNATE</em> PojoInstantiator.
	 */
	public MyPersons() {
		this.setPersons(new LinkedList<MyPerson>());
	}

	/**
	 * Copy constructor convert public {@link Persons} to private
	 * {@link MyPersons}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyPersons(final UntypedData edal) {

		super(edal);

		if (edal instanceof Persons) {

			Persons publicPersons = (Persons) edal;

			List<MyPerson> privatePersons = new LinkedList<MyPerson>();

			if (!publicPersons.getPersons().isEmpty()) {

				for (Person data : publicPersons.getPersons()) {

					if (data instanceof NaturalPerson) {
						privatePersons.add(new MyNaturalPerson(data));
					}
					if (data instanceof LegalPerson) {
						privatePersons.add(new MyLegalPerson(data));
					}
				}
			}

			this.setPersons(privatePersons);
		}
	}

	public Persons toPerson() throws ORCIDException {

		Persons publicPersons = new Persons();

		for (MyPerson person : this.getPersons()) {

			if (person instanceof MyNaturalPerson) {
				if (((MyNaturalPerson) person).getOrcid() != null) {
					publicPersons.add(new NaturalPerson(((MyNaturalPerson) person).getGivenName(),
							((MyNaturalPerson) person).getSureName(), ((MyNaturalPerson) person).getAddressLine(),
							((MyNaturalPerson) person).getZip(), ((MyNaturalPerson) person).getCountry(),
							((MyNaturalPerson) person).getOrcid().toORCID()));
				}
				else{
					publicPersons.add(new NaturalPerson(((MyNaturalPerson) person).getGivenName(),
							((MyNaturalPerson) person).getSureName(), ((MyNaturalPerson) person).getAddressLine(),
							((MyNaturalPerson) person).getZip(), ((MyNaturalPerson) person).getCountry()));
				}
			} else if (person instanceof MyLegalPerson) {
				publicPersons.add(new LegalPerson(((MyLegalPerson) person).getLegalName(),
						((MyLegalPerson) person).getAddressLine(), ((MyLegalPerson) person).getZip(),
						((MyLegalPerson) person).getCountry()));
			}

		}

		publicPersons.setString(this.getString());

		return publicPersons;
	}

	/**
	 * @return the persons
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UntypedData_Persons", joinColumns = @JoinColumn(name = "UNTYPEDDATA_ID"))
	@OrderBy
	public Collection<MyPerson> getPersons() {
		return persons;
	}

	/**
	 * @param persons
	 *            the persons to set
	 */
	public void setPersons(Collection<MyPerson> persons) {
		this.persons = persons;
	}
}
