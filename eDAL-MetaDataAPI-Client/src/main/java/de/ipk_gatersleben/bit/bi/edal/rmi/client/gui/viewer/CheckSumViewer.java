/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>CheckSumViewer</code> wrappers class <code>CheckSumviewDialog</code> as
 *  a <code>MetadataViewer</code> Dialog to view CheckSum datatype.
 *  we can use it  with a couple of lines of code: 
 *  <pre>
 *  	CheckSumViewer checkSumViewer = new CheckSumViewer();
 *  	checkSumViewer.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class CheckSumViewer extends MetadataViewer{
	private CheckSum checkSum;
	private String _title;
	/**
	 *  pop up a CheckSumviewDialog Dialog
	 */
	public int showOpenDialog() {
		CheckSumviewDialog dlg = new CheckSumviewDialog(this.checkSum,this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}
	/**
	 * set the CheckSum value.
	 * 
	 * set the CheckSum value.
	 */
	public void setValue(UntypedData checkSum) {
		if(checkSum!=null)
		{
			this.checkSum = (CheckSum)checkSum;
		}
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
