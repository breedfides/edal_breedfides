/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
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