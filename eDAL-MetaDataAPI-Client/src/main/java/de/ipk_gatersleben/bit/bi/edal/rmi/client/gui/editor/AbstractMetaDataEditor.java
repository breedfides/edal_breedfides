/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
