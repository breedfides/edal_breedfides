/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
