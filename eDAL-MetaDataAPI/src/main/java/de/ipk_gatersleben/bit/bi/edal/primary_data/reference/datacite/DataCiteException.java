/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite;

import javax.ws.rs.core.Response.Status;

/**
 * Exception class to describe problems with the DataCite client.
 * 
 * @author arendd
 */
public class DataCiteException extends Exception {

	private static final long serialVersionUID = 1L;
	private Status status;

	/**
	 * Constructor for DataCiteException.
	 * 
	 * @param status
	 *            a {@link Status} object.
	 */
	public DataCiteException(final Status status) {
		super("failed to register DOI/URL/Metadata: " + status.getReasonPhrase());
		this.status = status;
	}

	/**
	 * Constructor for DataCiteException.
	 * 
	 * @param message
	 *            a {@link String} object.
	 */
	public DataCiteException(String message) {
		super("failed to register DOI/URL/Metadata: " + message);
	}

	/**
	 * Constructor for DataCiteException.
	 * 
	 * @param message
	 *            a {@link String} object.
	 * @param cause
	 *            a {@link Throwable} object.
	 */
	public DataCiteException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for DataCiteException.
	 * 
	 * @param cause
	 *            a {@link Throwable} object.
	 */
	public DataCiteException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Get the status of the {@link DataCiteException}
	 * 
	 * @return the _status
	 */
	public final Status getStatus() {
		return this.status;
	}
}