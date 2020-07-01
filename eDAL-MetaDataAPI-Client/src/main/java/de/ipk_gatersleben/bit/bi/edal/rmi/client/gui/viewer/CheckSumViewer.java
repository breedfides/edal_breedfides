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
