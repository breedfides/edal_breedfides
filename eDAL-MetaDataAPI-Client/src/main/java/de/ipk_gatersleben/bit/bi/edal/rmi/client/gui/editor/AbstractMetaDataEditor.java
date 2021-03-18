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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * This is a abstract class. To create a concrete <code>MetadataEditor</code> as
 * a subclass,you need only provide implementations for the following three
 * methods: *
 * 
 * <pre>
 * public abstract int showOpenDialog();
 * 
 * public abstract void setValue(UntypedData person);
 * 
 * public abstract UntypedData getValue();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */

public abstract class AbstractMetaDataEditor {
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
	 *            the value to set
	 */
	public abstract void setValue(UntypedData untypeddata);

	/**
	 * get the metadata value after user edited
	 * 
	 * @return the {@link UntypedData} value
	 */
	public abstract UntypedData getValue();

	/**
	 * set the title of metadata dialog
	 * 
	 * @param title
	 *            the title of the dialog
	 */
	public void setTitle(String title) {

	}
}
