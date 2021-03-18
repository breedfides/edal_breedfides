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
