/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

/**
 * Class indicates an error with a {@link PrimaryDataEntityVersion}.
 * 
 * @author lange
 */
public class PrimaryDataEntityVersionException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = -8020187568707326112L;

	/**
	 * Constructor for {@link PrimaryDataEntityVersionException} without a
	 * specific message.
	 */
	public PrimaryDataEntityVersionException() {
	}

	/**
	 * Constructor for {@link PrimaryDataEntityVersionException} with a specific
	 * message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public PrimaryDataEntityVersionException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link PrimaryDataEntityVersionException} with a specific
	 * message and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public PrimaryDataEntityVersionException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for {@link PrimaryDataEntityVersionException} with a specific
	 * cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public PrimaryDataEntityVersionException(final Throwable cause) {
		super(cause);
	}
}