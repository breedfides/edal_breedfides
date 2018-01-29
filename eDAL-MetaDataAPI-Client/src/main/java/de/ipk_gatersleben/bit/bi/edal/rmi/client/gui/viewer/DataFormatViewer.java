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
 * <code>DataFormatViewer</code> provides a mechanism for the user to
 * view DataFormat information.
 *
 * The following code pops up a DataFormatViewer Dialog 
 * <pre>
 *    DataFormatViewer dataFormatViewer = new DataFormatViewer();
 *    dataFormatViewer.showOpenDialog();
 * </pre>
 *
 * @version 1.0
 * @author Jinbo Chen
 *
 */
public class DataFormatViewer extends MetadataViewer{
	private String _text;
	private String _title;
	/**
	* pop up a Dialog to view the DataFormat
	*/
	public int showOpenDialog() {
		TextViewDialog dlg = new TextViewDialog(this._text,this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}
	/**
	 * set a DataFormat value 
	 */
	public void setValue(UntypedData data) {
		if(data!=null)
		{
			this._text = data.toString();
		}
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
