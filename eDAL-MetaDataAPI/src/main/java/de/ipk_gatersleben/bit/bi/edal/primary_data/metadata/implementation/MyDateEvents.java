/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

@Entity
@DiscriminatorValue("2")
@Indexed
public class MyDateEvents extends MyUntypedData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2965465551221490026L;

	private Set<MyEdalDate> set;

	/**
	 * Default constructor for {@link MyDateEvents} is necessary for PojoInstantiator
	 * of <em>HIBERNATE</em>.
	 */
	public MyDateEvents() {
		this.setString("DATES");
		this.set = new HashSet<MyEdalDate>();
	}

	/**
	 * Copy constructor to convert public {@link DateEvents} to private
	 * {@link MyDateEvents}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDateEvents(final UntypedData edal) {

		super(edal);

		if (edal instanceof DateEvents) {

			DateEvents dateEvents = (DateEvents) edal;

			Set<MyEdalDate> myDateSet = new HashSet<MyEdalDate>();

			if (!dateEvents.getSet().isEmpty()) {
				for (EdalDate date : dateEvents.getSet()) {

					if (date instanceof EdalDateRange) {
						myDateSet
								.add(new MyEdalDateRange((EdalDateRange) date));

					} else if (date instanceof EdalDate) {
						myDateSet.add(new MyEdalDate(date));

					}
				}
			}
			this.setSet(myDateSet);
		}
	}

	/**
	 * @return the set
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UntypedData_MyEdalDate", joinColumns = @JoinColumn(name = "UNTYPEDDATA_ID"))
	public Set<MyEdalDate> getSet() {
		return set;
	}

	/**
	 * @param set
	 *            the set to set
	 */
	public void setSet(Set<MyEdalDate> set) {
		this.set = set;
	}

	public DateEvents toDates() {

		DateEvents dates = new DateEvents(this.getString());

		Set<EdalDate> set = new HashSet<EdalDate>();

		for (MyEdalDate myEdalTimePoint : this.getSet()) {

			if(myEdalTimePoint instanceof MyEdalDateRange){
				set.add(((MyEdalDateRange) myEdalTimePoint).toEdalTimeRange());
			}
			else if(myEdalTimePoint instanceof MyEdalDate){
				set.add(myEdalTimePoint.toEdalTimePoint());
			}
			
		}
		dates.setSet(set);

		return dates;
	}

}
