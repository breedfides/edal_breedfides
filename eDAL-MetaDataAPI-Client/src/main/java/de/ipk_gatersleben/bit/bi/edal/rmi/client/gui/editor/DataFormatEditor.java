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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormatException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;

/**
 * <code>DataFormatEditor</code> provides a mechanism for the user to edit
 * DataFormat information.
 * 
 * The following code pops up a DataFormatEditor Dialog
 * 
 * <pre>
 * DataFormatEditor dataformateditor = new DataFormatEditor();
 * dataformateditor.showOpenDialog();
 * </pre>
 
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class DataFormatEditor extends AbstractMetaDataEditor {
	private String _text;	
	private String _title;

	/**
	 * pop up a Dialog
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
	 * set a DataFormat value
	 */
	public void setValue(UntypedData data) {
		if (data != null) {
			this._text = data.toString();
		}
	}

	/**
	 * get the DataFormat value
	 */
	public UntypedData getValue() {
		try {
			return new DataFormat(_text);
		} catch (DataFormatException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
			return new EmptyMetaData();
		}
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
