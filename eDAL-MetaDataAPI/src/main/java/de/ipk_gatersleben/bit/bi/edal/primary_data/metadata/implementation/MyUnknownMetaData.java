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

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UnknownMetaData}
 * for persistence with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("10")
@Indexed
public final class MyUnknownMetaData extends MyUntypedData {

	private static final long serialVersionUID = -6571988459150163982L;
	private static final String UNKNOWN_STRING = "unknown";

	/**
	 * Default constructor for {@link MyUnknownMetaData} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyUnknownMetaData() {
	}

	/**
	 * Copy constructor to convert public
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UnknownMetaData}
	 * to private {@link MyUnknownMetaData}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyUnknownMetaData(UntypedData edal) {
		super();
		this.setString(UNKNOWN_STRING);
	}
}