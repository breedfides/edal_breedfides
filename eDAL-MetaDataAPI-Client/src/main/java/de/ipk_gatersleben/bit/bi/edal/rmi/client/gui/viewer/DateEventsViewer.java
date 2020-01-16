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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>DateEventsViewer</code> is a wrapper class for <code>DateEventsviewerDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	DateEventsViewer dateEventsViewer = new DateEventsViewer();
 *  	dateEventsViewer.setValue(dataevents); 
 *  	dateEventsViewer.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class DateEventsViewer extends MetadataViewer{
	private DateEvents _dateevents;
	private String _title;
	
	public int showOpenDialog() {
		DateEventsViewerDialog dlg = new DateEventsViewerDialog(this._dateevents,this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}


	public void setValue(UntypedData dataevents) {
		if(dataevents!=null)
		{
			this._dateevents = (DateEvents)dataevents;
		}
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
