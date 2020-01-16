/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

/**
 * {@link Enum} to represent the current status of the approval process for a
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
 * 
 * @author arendd
 */
public enum PublicationStatus {

	/**
	 * the corresponding
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * is not yet requested.
	 */
	SUBMITTED,
	/**
	 * the corresponding
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * is under review.
	 */
	UNDER_REVIEW,
	/**
	 * the corresponding
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * is checked and accepted.
	 */
	ACCEPTED,
	/**
	 * the corresponding
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 * is checked and rejected.
	 */
	REJECTED;
}