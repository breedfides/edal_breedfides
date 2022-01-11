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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

/**
 * <code>EDALFileFilter</code> is an abstract class used by
 * {@code EDALFileChooser} for filtering the set of files shown to the user. See
 * {@code EDALFileNameExtensionFilter} for an implementation that filters using
 * the file name extension. A <code>EDALFileFilter</code> can be set on a
 * <code>EDALFileChooser</code> to keep unwanted files from appearing in the
 * directory listing.
 *
 * @see de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser#setFileFilter
 *
 * @version 1.0
 * @author Jinbo Chen
 */
public abstract class EdalAbstractFileFilter {
	/**
	 * Whether the given file is accepted by this filter.
	 * 
	 * @param file
	 *            the file to filter
	 * @return true if accepted
	 * 
	 */
	public abstract boolean accept(ClientPrimaryDataFile file);

	/**
	 * The description of this filter. For example: "JPG and GIF Images"
	 * 
	 * @return the description
	 */
	public abstract String getDescription();
}
