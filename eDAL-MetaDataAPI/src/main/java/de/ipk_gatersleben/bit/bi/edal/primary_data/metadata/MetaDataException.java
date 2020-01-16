/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Indicates an error with a {@link MetaData} object.
 * 
 * @author lange
 */
public class MetaDataException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 136451879717380826L;

	/**
	 * Constructor for {@link MetaDataException} without a specific message.
	 */
	public MetaDataException() {
	}

	/**
	 * Constructor for {@link MetaDataException} with a specific message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public MetaDataException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link MetaDataException} with a specific message and
	 * cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public MetaDataException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for {@link MetaDataException} with a specific cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public MetaDataException(final Throwable cause) {
		super(cause);
	}

}
