/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
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
