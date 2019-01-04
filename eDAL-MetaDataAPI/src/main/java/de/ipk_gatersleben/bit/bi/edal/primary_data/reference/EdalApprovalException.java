/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
