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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link IdentifierRelation} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("9")
@Indexed
public final class MyIdentifierRelation extends MyUntypedData {

	private static final long serialVersionUID = -4882543736251028874L;

	private List<MyIdentifier> relations;

	/**
	 * Default constructor for {@link MyIdentifierRelation} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyIdentifierRelation() {
		this.setRelations(new LinkedList<MyIdentifier>());
	}

	/**
	 * Copy constructor to convert public {@link IdentifierRelation} to private
	 * {@link MyIdentifierRelation}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyIdentifierRelation(final UntypedData edal) {

		super(edal);

		if (edal instanceof IdentifierRelation) {
			IdentifierRelation relation = (IdentifierRelation) edal;

			List<MyIdentifier> myRelation = new LinkedList<MyIdentifier>();

			for (Identifier id : relation.getRelations()) {
				myRelation.add(new MyIdentifier(id));
			}
			this.setRelations(myRelation);
		}
	}

	/**
	 * Getter for the field <code>relations</code>.
	 * 
	 * @return a {@link List} object.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "UntypedData_IdentifierRelations", joinColumns = @JoinColumn(name = "UNTYPEDDATA_ID"))
	@OrderBy
	public List<MyIdentifier> getRelations() {
		return relations;
	}

	/**
	 * Setter for the field <code>relations</code>.
	 * 
	 * @param relations
	 *            a {@link List} object.
	 */
	public void setRelations(List<MyIdentifier> relations) {
		this.relations = relations;
	}

	/**
	 * Convert this {@link MyIdentifierRelation} to a public
	 * {@link IdentifierRelation}.
	 * 
	 * @return a {@link IdentifierRelation} object.
	 */
	public IdentifierRelation toIdentifierRelation() {

		IdentifierRelation identifierRelation = new IdentifierRelation();

		for (MyIdentifier myid : this.getRelations()) {
			identifierRelation.add(myid.toIdentifier());
		}
		identifierRelation.setString(this.getString());

		return identifierRelation;
	}
}