/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
