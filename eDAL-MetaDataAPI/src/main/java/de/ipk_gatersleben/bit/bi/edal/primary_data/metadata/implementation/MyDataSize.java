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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.annotations.Indexed;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link DataSize} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("4")
@Indexed
public final class MyDataSize extends MyUntypedData {

	private static final long serialVersionUID = 6243943469009086574L;
	private Long size = null;

	/**
	 * Default constructor for {@link MyDataSize} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyDataSize() {
	}

	/**
	 * Copy constructor to convert public {@link DataSize} to private
	 * {@link MyDataSize}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDataSize(final UntypedData edal) {

		super(edal);

		if (edal instanceof DataSize) {
			DataSize dataSize = (DataSize) edal;

			this.setSize(dataSize.getFileSize());
		}
	}

	/**
	 * Convert this {@link MyDataSize} to a public {@link DataSize}.
	 * 
	 * @return a {@link DataSize} object.
	 */
	public DataSize toDataSize() {
		return new DataSize(this.getSize());
	}

	/**
	 * Setter for the field <code>size</code>.
	 * 
	 * @param size
	 *            the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * Getter for the field <code>size</code>.
	 * 
	 * @return size
	 */
	public Long getSize() {
		return size;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.getSize() != null) {
			return this.getSize().toString();
		} else {
			return "null";
		}
	}
}
