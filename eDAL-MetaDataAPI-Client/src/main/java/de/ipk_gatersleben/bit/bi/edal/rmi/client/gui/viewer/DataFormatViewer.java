/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
