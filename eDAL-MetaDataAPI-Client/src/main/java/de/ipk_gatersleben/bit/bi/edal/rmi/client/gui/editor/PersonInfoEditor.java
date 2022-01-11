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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>PersonInfoEditor</code> is a wrapper class for <code>PersonDialog</code>,
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	PersonInfoEditor personInfoEditor = new PersonInfoEditor();
 *  	personInfoEditor.setValue(person); 
 *  	personInfoEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class PersonInfoEditor extends AbstractMetaDataEditor{
	private Persons _persons;
	private String _title;
	/**
	 * set the Persons value.
	 * 
	 * set the Persons value.
	 */
	public void setValue(UntypedData persons) {
		if(persons!=null)
		{
			this._persons = (Persons)persons;
		}
	}
	/**
	 *  pop up a PersonDialog Dialog
	 */
	public int showOpenDialog() {
		PersonsDialog dlg = new PersonsDialog(this._persons,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == PersonsDialog.APPROVE_OPTION) {
			if(dlg.getTable().getCellEditor()!=null)
			{
				dlg.getTable().getCellEditor().stopCellEditing();
			}
			this._persons = dlg.getPerson();
		}
		return returnVal;
	}
	/**
	 * Returns the Persons typed in by user.
	 * 
	 * @return the Persons typed in by user.
	 */
	public UntypedData getValue()
	{
		return this._persons;
	}
	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
