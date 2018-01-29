/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.util.Set;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>SubjectViewer</code> wrappers class <code>SubjectviewDialog</code>
 * as a <code>MetadataViewer</code> Dialog to edit Subjects datatype. we can use
 * it with a couple of lines of code:
 * 
 * <pre>
 * SubjectViewer subjectViewer = new SubjectViewer();
 * subjectViewer.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class SubjectViewer extends MetadataViewer {
	private Set<UntypedData> _subjects;
	private String _title;

	/**
	 * pop up a SubjectviewDialog Dialog
	 */
	public int showOpenDialog() {
		SubjectsViewDialog dlg = new SubjectsViewDialog(this._subjects, this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}

	/**
	 * set the Subjects value.
	 * 
	 * set the Subjects value.
	 */
	public void setValue(UntypedData subjects) {
		if (subjects != null) {
			this._subjects = ((Subjects) subjects).getSubjects();
		}
	}

	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
