/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
