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
