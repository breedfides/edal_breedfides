/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>IdentifierEditor</code> wrappers class <code>TextDialog</code> as a
 * <code>MetadataEditor</code> Dialog to edit <code>Identifier</code> datatype.
 * we can use it with a couple of lines of code:
 * 
 * <pre>
 * IdentifierEditor identifierEditor = new IdentifierEditor();
 * identifierEditor.showOpenDialog();
 * </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class IdentifierEditor extends AbstractMetaDataEditor {
	private String _text;
	private String _title;

	/**
	 * pop up a IdentifierEditor Dialog
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
	 * set the Identifier value.
	 * 
	 * set the Identifier value.
	 */
	public void setValue(UntypedData data) {
		if (data != null) {
			this._text = data.toString();
		}
	}

	/**
	 * Returns the Identifier inputted by user.
	 * 
	 * @return the Identifier inputted by user.
	 */
	public UntypedData getValue() {

		return new Identifier(_text);

	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
