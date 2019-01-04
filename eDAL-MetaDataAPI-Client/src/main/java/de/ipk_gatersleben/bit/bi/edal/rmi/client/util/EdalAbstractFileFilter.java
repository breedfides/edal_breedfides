/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
