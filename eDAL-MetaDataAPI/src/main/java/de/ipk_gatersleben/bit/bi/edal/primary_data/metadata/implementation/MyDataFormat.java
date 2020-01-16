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

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link DataFormat} for persistence with
 * <em>HIBERNATE</em>
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("3")
@Indexed
public final class MyDataFormat extends MyUntypedData {

	private static final long serialVersionUID = -9055352152889379069L;
	private String mimeType;

	/**
	 * Default constructor for {@link MyDataFormat} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyDataFormat() {
	}

	/**
	 * Copy constructor to convert public {@link DataFormat} to private
	 * {@link MyDataFormat}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDataFormat(UntypedData edal) {

		super(edal);

		if (edal instanceof DataFormat) {

			DataFormat dataFormat = (DataFormat) edal;
			this.setMimeType(dataFormat.getMimeType());
		}
	}

	/**
	 * Getter for the field <code>mimeType</code>.
	 * 
	 * @return the mimeType
	 */
	@Field(index = Index.YES, store = Store.YES)
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Setter for the field <code>mimeType</code>.
	 * 
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}