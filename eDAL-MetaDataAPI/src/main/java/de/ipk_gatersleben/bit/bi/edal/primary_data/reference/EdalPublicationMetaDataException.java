/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

/**
 * Exception is thrown on failures of the check of the
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} for a
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
 * object.
 * 
 * @author arendd
 */
public class EdalPublicationMetaDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} without a
	 * specific message.
	 */
	public EdalPublicationMetaDataException() {
		super();
	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public EdalPublicationMetaDataException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * message and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public EdalPublicationMetaDataException(final String message,
			final Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public EdalPublicationMetaDataException(final Throwable cause) {
		super(cause);
	}
}
