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

import java.rmi.RemoteException;
import java.util.Locale;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

/**
 * An implementation of {@code EDALFileFilter} that filters using a specified
 * set of extensions. The extension for a file is the portion of the file name
 * after the last ".". Files whose name does not contain a "." have no file name
 * extension. File name extension comparisons are case insensitive.
 * The following example creates a {@code EDALFileNameExtensionFilter} that will
 * show {@code jpg} files:
 * 
 * <pre>
 * EDALFileFilter filter = new EDALFileNameExtensionFilter("JPEG file", "jpg", "jpeg");
 * EDALFileChooser fileChooser = ...;
 * fileChooser.setFileFilter(filter);
 * </pre>
 * 
 * @see de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser#setFileFilter
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalFileNameExtensionFilter extends EdalAbstractFileFilter {

	// Description of this filter.
	private final String description;
	// Known extensions.
	private final String[] extensions;
	// Cached ext
	private final String[] lowerCaseExtensions;

	/**
	 * Creates a {@code EDALFileNameExtensionFilter} with the specified
	 * description and file name extensions. The returned
	 * {@code EDALFileNameExtensionFilter} will accept all directories and any
	 * file with a file name extension contained in {@code extensions}.
	 * 
	 * @param description
	 *            textual description for the filter, may be {@code null}
	 * @param extensions
	 *            the accepted file name extensions
	 * @throws IllegalArgumentException
	 *             if extensions is {@code null}, empty, contains {@code null},
	 *             or contains an empty string
	 * @see #accept
	 */
	public EdalFileNameExtensionFilter(String description, String... extensions) {
		if (extensions == null || extensions.length == 0) {
			throw new IllegalArgumentException(
					"Extensions must be non-null and not empty");
		}
		this.description = description;
		this.extensions = new String[extensions.length];
		this.lowerCaseExtensions = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			if (extensions[i] == null || extensions[i].length() == 0) {
				throw new IllegalArgumentException(
						"Each extension must be non-null and not empty");
			}
			this.extensions[i] = extensions[i];
			lowerCaseExtensions[i] = extensions[i].toLowerCase(Locale.ENGLISH);
		}
	}

	/**
	 * Tests the specified file, returning true if the file is accepted, false
	 * otherwise. True is returned if the extension matches one of the file name
	 * extensions of this {@code EDALFileFilter}, or the file is a directory.
	 * 
	 * @param f
	 *            the {@code File} to test
	 * @return true if the file is to be accepted, false otherwise
	 */
	@Override
	public boolean accept(ClientPrimaryDataFile f) {
		if (f != null) {
			String fileName;
			try {
				fileName = f.getName();
				int i = fileName.lastIndexOf('.');
				if (i > 0 && i < fileName.length() - 1) {
					String desiredExtension = fileName.substring(i + 1)
							.toLowerCase(Locale.ENGLISH);
					for (String extension : lowerCaseExtensions) {
						if (desiredExtension.equals(extension)) {
							return true;
						}
					}
				}
			} catch (RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			}
		}
		return false;
	}

	/**
	 * The description of this filter. For example: "JPG and GIF Images."
	 * 
	 * @return the description of this filter
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the set of file name extensions files are tested against.
	 * 
	 * @return the set of file name extensions files are tested against
	 */
	public String[] getExtensions() {
		String[] result = new String[extensions.length];
		System.arraycopy(extensions, 0, result, 0, extensions.length);
		return result;
	}

	/**
	 * Returns a string representation of the
	 * {@code EDALFileNameExtensionFilter}. This method is intended to be used
	 * for debugging purposes, and the content and format of the returned string
	 * may vary between implementations.
	 * 
	 * @return a string representation of this
	 *         {@code EDALFileNameExtensionFilter}
	 */
	public String toString() {
		return super.toString() + "[description=" + getDescription()
				+ " extensions=" + java.util.Arrays.asList(getExtensions())
				+ "]";
	}

}
