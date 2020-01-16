/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DirectoryMetaData}
 * for persistence with <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("6")
@Indexed
public final class MyDirectoryMetaData extends MyUntypedData {

	private static final long serialVersionUID = 6713690306176488008L;
	private static final String DIRECTORY = "directory";

	/**
	 * Default constructor for {@link MyDirectoryMetaData} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyDirectoryMetaData() {
	}

	/**
	 * Copy constructor to convert public
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DirectoryMetaData}
	 * to private {@link MyDirectoryMetaData}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDirectoryMetaData(UntypedData edal) {
		super();
		this.setString(DIRECTORY);
	}
}
