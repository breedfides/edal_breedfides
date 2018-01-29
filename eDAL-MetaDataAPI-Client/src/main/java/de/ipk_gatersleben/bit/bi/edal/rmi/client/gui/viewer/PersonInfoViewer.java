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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>PersonInfoViewer</code> is a wrapper class for
 * <code>PersonviewDialog</code>, we can use it with a couple of lines of code:
 * 
 * <pre>
 * PersonInfoViewer personInfoViewer = new PersonInfoViewer();
 * personInfoViewer.setValue(person);
 * personInfoViewer.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class PersonInfoViewer extends MetadataViewer {
	private Persons _persons;
	private String _title;

	/**
	 * set the Person value.
	 * 
	 * set the Person value.
	 */
	public void setValue(UntypedData persons) {
		if (persons != null) {
			this._persons = (Persons) persons;
		}
	}

	/**
	 * pop up a PersonviewDialog Dialog
	 */
	public int showOpenDialog() {
		PersonsViewDialog dlg = new PersonsViewDialog(this._persons, this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}

	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
