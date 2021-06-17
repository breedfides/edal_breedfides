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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

/**
 * {@link Enum} to collect all function that are provided by the
 * {@link EdalHttpServer}.
 * 
 * @author arendd
 */
public enum EdalHttpFunctions {
	/**
	 * Accept the request for a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 */
	ACCEPT {
	},
	/**
	 * Reject the request for a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
	 */
	REJECT {
	},
	/**
	 * Download a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile}.
	 */
	DOWNLOAD {
	},
	/**
	 * Access a landing page for a
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity}
	 * .
	 */
	EDAL, LOGIN, DOI, URL, LOGO, ZIP, CSS, JS, USER_ACCEPT, USER_REJECT, REPORT, OAI, LATEST, SEARCH, HOME;
}
