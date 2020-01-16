/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 *  The <code>TextViewer</code> is a wrapper class for <code>TextviewDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	TextViewer textViewer = new TextViewer();
 *  	textViewer.setValue(text); 
 *  	textViewer.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class TextViewer extends MetadataViewer{
	private String _text;
	private String _title;
	/**
	 *  pop up a TextviewDialog Dialog
	 */
	public int showOpenDialog() {
		TextViewDialog dlg = new TextViewDialog(this._text,this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}
	/**
	 * set the Text value.
	 * 
	 * set the Text value.
	 */
	public void setValue(UntypedData data) {
		if(data!=null)
		{
			this._text = data.getString();
		}
	}
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
