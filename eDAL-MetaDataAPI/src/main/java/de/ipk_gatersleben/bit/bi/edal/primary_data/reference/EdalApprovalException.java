/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * Exception is thrown on failures of the ApprovalService.
 * 
 * @author arendd
 */
public class EdalApprovalException extends EdalException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for {@link EdalApprovalException} without a specific message.
	 */
	public EdalApprovalException() {
		super();
	}

	/**
	 * Constructor for {@link EdalApprovalException} with a specific message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public EdalApprovalException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link EdalApprovalException} with a specific message and
	 * cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public EdalApprovalException(final String message, final Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor for {@link EdalApprovalException} with a specific cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public EdalApprovalException(final Throwable cause) {
		super(cause);
	}
}
