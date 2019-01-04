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


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>DateEventsEditor</code> is a wrapper class for <code>DateEventsDialog</code>,
 *  we can use it  with a couple of lines of code: 
 *  <pre>
 *  	DateEventsEditor dateEventsEditor = new DateEventsEditor();
 *  	dateEventsEditor.setValue(dataevents); 
 *  	dateEventsEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class DateEventsEditor extends AbstractMetaDataEditor{
	private DateEvents _dateevents;
	private String _title;
	/**
	 *  pop up a DateEventsDialog Dialog
	 */
	public int showOpenDialog() {
		DateEventsDialog dlg = new DateEventsDialog(this._dateevents,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == DateEventsDialog.APPROVE_OPTION) {
			this._dateevents = dlg.getDateEvents();
		}
		return returnVal;
	}
	/**
	 * set the DateEvents value.
	 * 
	 * set the DateEvents value.
	 */
	public void setValue(UntypedData dataevents) {
		if(dataevents!=null)
		{
			this._dateevents = (DateEvents)dataevents;
		}
	}
	/**
	 * Returns the DateEvents inputted by user.
	 * 
	 * @return the DateEvents inputted by user.
	 */
	public UntypedData getValue() {		
		return this._dateevents;
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
