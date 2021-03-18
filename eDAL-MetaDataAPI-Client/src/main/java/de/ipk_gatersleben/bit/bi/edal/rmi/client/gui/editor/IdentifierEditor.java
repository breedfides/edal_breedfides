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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;

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

		return new Identifier(_text,null,null);

	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
