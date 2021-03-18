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


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>EdalLanguageEditor</code> is a wrapper class for <code>EdalLanguageDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	EdalLanguageEditor edalLanguageEditor = new EdalLanguageEditor();
 *  	edalLanguageEditor.setValue(language); 
 *  	edalLanguageEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalLanguageEditor extends AbstractMetaDataEditor{
	private EdalLanguage language;
	private String _title;
	/**
	 * set the EdalLanguage value.
	 * 
	 * set the EdalLanguage value.
	 */
	public void setValue(UntypedData language) {
		if(language!=null)
		{
			this.language = (EdalLanguage)language;
		}
	}
	/**
	 *  pop up a PersonDialog Dialog
	 */
	public int showOpenDialog() {
		EdalLanguageDialog dlg = new EdalLanguageDialog(this.language,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == EdalLanguageDialog.APPROVE_OPTION) {
			this.language = dlg.getLanguage();
		}
		return returnVal;
	}
	/**
	 * Returns the Person inputted by user.
	 * 
	 * @return the Person inputted by user.
	 */
	public UntypedData getValue()
	{
		return this.language;
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
