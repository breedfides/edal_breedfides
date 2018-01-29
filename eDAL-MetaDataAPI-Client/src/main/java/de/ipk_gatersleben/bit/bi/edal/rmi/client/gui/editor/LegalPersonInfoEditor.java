/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>LegalPersonInfoEditor</code> is a wrapper class for <code>LegalPersonDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	PersonInfoEditor personInfoEditor = new PersonInfoEditor();
 *  	personInfoEditor.setValue(person); 
 *  	personInfoEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class LegalPersonInfoEditor extends AbstractMetaDataEditor{
	private Person _person;
	private String _title;
	/**
	 * set the Person value.
	 * 
	 * set the Person value.
	 */
	public void setValue(UntypedData person) {
		if(person!=null)
		{
			this._person = (Person)person;
		}
	}
	/**
	 *  pop up a PersonDialog Dialog
	 */
	public int showOpenDialog() {
		LegalPersonDialog dlg = new LegalPersonDialog((LegalPerson)this._person,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == LegalPersonDialog.APPROVE_OPTION) {
			this._person = dlg.getPerson();
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
		return this._person;
	}
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
