/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

/**
 * @author arendd
 * 
 */
public class PublicReferenceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8797060467736415754L;

	/**
	 * Constructor for {@link PublicReferenceException} without a specific
	 * message.
	 */
	public PublicReferenceException() {
		super();
	}

	/**
	 * Constructor for {@link PublicReferenceException} with a specific message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public PublicReferenceException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link PublicReferenceException} with a specific message
	 * and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public PublicReferenceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for {@link PublicReferenceException} with a specific cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public PublicReferenceException(final Throwable cause) {
		super(cause);
	}
}