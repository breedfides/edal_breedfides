/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
	EDAL, LOGIN, DOI, URL, LOGO, ZIP, CSS, JS, USER_ACCEPT, USER_REJECT, REPORT, OAI;
}
