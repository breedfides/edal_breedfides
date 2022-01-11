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

import java.util.Set;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
/**
 *  The <code>SubjectsEditor</code> wrappers class <code>SubjectsDialog</code> as
 *  a <code>MetadataEditor</code> Dialog to edit Subjects datatype.
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	SubjectsEditor subjectsEditor = new SubjectsEditor();
 *  	subjectsEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class SubjectsEditor extends AbstractMetaDataEditor{
	private Set<UntypedData> _subjects;
	private String _title;
	/**
	 *  pop up a SubjectsDialog Dialog
	 */
	public int showOpenDialog() {
		SubjectsDialog dlg = new SubjectsDialog(this._subjects,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == SubjectsDialog.APPROVE_OPTION) {
			this._subjects = dlg.getSubjects();
		}
		return returnVal;
	}
	/**
	 * set the Subjects value.
	 * 
	 * set the Subjects value.
	 */
	public void setValue(UntypedData subjects) {
		if(subjects!=null)
		{
			this._subjects = ((Subjects)subjects).getSubjects();
		}
	}
	/**
	 * Returns the Subjects typed in by user.
	 * 
	 * @return the Subjects typed in by user.
	 */
	public UntypedData getValue() {
		Subjects subjects = new Subjects();
		subjects.addAll(this._subjects);
		return subjects;
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
