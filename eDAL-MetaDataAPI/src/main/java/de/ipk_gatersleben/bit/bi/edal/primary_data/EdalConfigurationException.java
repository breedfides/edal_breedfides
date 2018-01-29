/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

/**
 * Exception is thrown on failure with the {@link EdalConfiguration} class.
 * 
 * @author arendd
 */
public class EdalConfigurationException extends Exception {

	private static final long serialVersionUID = -1740005536933106532L;

	/**
	 * Constructor for {@link EdalConfigurationException} without a specific
	 * message.
	 */
	public EdalConfigurationException() {
		super();
	}

	/**
	 * Constructor for {@link EdalConfigurationException} with a specific
	 * message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public EdalConfigurationException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link EdalConfigurationException} with a specific
	 * message and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public EdalConfigurationException(final String message,
			final Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor for {@link EdalConfigurationException} with a specific cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public EdalConfigurationException(final Throwable cause) {
		super(cause);
	}

}
