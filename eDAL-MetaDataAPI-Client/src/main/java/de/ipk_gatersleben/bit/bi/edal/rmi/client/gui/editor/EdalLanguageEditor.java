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
