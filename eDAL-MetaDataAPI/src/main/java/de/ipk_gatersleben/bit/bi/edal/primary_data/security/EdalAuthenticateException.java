/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
