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
 *  The <code>TextEditor</code> is a wrapper class for <code>TextDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	TextEditor textEditor = new TextEditor();
 *  	textEditor.setValue(text); 
 *  	textEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class TextEditor extends AbstractMetaDataEditor{
	private String _text;
	private String _title;
	/**
	 *  pop up a TextDialog Dialog
	 */
	public int showOpenDialog() {
		TextDialog dlg = new TextDialog(this._text,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == TextDialog.APPROVE_OPTION) {
			this._text = dlg.getText();
		}
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
	/**
	 * Returns the Text informaiton inputted by user.
	 * 
	 * @return the Text informaiton inputted by user.
	 */
	public UntypedData getValue() {
		return new UntypedData(_text);
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
