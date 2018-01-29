/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.security;

/**
 * Exception is thrown on authentication failures.
 * 
 * @author lange
 */
public class EdalAuthenticateException extends Exception {

	private static final long serialVersionUID = -1740005536933106532L;

	/**
	 * Constructor for {@link EdalAuthenticateException} without a specific
	 * message.
	 */
	public EdalAuthenticateException() {
		super();
	}

	/**
	 * Constructor for {@link EdalAuthenticateException} with a specific
	 * message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public EdalAuthenticateException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link EdalAuthenticateException} with a specific message
	 * and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public EdalAuthenticateException(final String message, final Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor for {@link EdalAuthenticateException} with a specific cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public EdalAuthenticateException(final Throwable cause) {
		super(cause);
	}

}
