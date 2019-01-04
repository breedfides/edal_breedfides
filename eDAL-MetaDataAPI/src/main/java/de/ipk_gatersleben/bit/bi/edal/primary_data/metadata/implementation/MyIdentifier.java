/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link Identifier} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("8")
@Indexed
public final class MyIdentifier extends MyUntypedData {

	private static final long serialVersionUID = -460725495792281812L;

	private String identifier;

	/**
	 * Default constructor for {@link MyIdentifier} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyIdentifier() {
	}

	/**
	 * Constructor for {@link MyIdentifier} from a {@link Identifier} object.
	 * 
	 * @param id
	 *            a {@link Identifier} object.
	 */
	public MyIdentifier(Identifier id) {
		super();
		this.setIdentifier(id.getID());
	}

	/**
	 * Copy constructor to convert public {@link Identifier} to private
	 * {@link MyIdentifier}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyIdentifier(final UntypedData edal) {

		super(edal);

		if (edal instanceof Identifier) {
			Identifier i = (Identifier) edal;
			this.setIdentifier(i.getID());
		}
	}

	/**
	 * Getter for the field <code>identifier</code>.
	 * 
	 * @return the identifier
	 */
	@Field(index = Index.YES, store = Store.YES)
	public final String getIdentifier() {
		return identifier;
	}

	/**
	 * Setter for the field <code>identifier</code>.
	 * 
	 * @param identifier
	 *            the identifier to set
	 */
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Convert this {@link MyIdentifier} to a public {@link Identifier}.
	 * 
	 * @return a {@link Identifier} object.
	 */
	public final Identifier toIdentifier() {
		return new Identifier(this.getIdentifier());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "MyIdentifier [identifier=" + identifier + "]";
	}
}