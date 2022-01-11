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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * This is a abstract class. To create a concrete <code>MetadataViewer</code> as
 * a subclass,you need only provide implementations for the following three
 * methods:
 * 
 * <pre>
 * public abstract int showOpenDialog();
 * 
 * public abstract void setValue(UntypedData person);
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */

public abstract class MetadataViewer {
	/**
	 * pop up a Dialog
	 * 
	 * @return the result
	 */
	public abstract int showOpenDialog();

	/**
	 * set the original value of metadata
	 * 
	 * @param untypeddata
	 *            the value the set
	 */
	public abstract void setValue(UntypedData untypeddata);

	/**
	 * set the title of metadata dialog
	 * 
	 * @param title
	 *            the title for the dialog
	 */
	public abstract void setTitle(String title);
}
