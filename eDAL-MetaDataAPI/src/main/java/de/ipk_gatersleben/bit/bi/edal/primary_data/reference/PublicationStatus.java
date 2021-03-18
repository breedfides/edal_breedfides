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