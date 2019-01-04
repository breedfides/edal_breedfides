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

import java.util.Collection;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
/**
 *  The <code>IdentifierRelationEditor</code> wrappers class <code>IdentifierRelationDialog</code> as
 *  a <code>MetadataEditor</code> Dialog to edit IdentifierRelation datatype.
 *  we can use it  with a couple of lines of code:
 *  <pre>
 *  	IdentifierRelationEditor identifierRelationEditor = new IdentifierRelationEditor();
 *  	identifierRelationEditor.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class IdentifierRelationEditor extends AbstractMetaDataEditor{
	private Collection<Identifier> _relations;
	private String _title;
	/**
	 *  pop up a IdentifierRelationDialog Dialog
	 */
	public int showOpenDialog() {
		IdentifierRelationDialog dlg = new IdentifierRelationDialog(this._relations,this._title);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == IdentifierRelationDialog.APPROVE_OPTION) {
			this._relations = dlg.getRelations();
		}
		return returnVal;
	}
	/**
	 * set the IdentifierRelation value.
	 * 
	 * set the IdentifierRelation value.
	 */
	public void setValue(UntypedData relations) {
		if(relations!=null)
		{
			this._relations = ((IdentifierRelation)relations).getRelations();
		}
	}
	/**
	 * Returns the IdentifierRelation inputted by user.
	 * 
	 * @return the IdentifierRelation inputted by user.
	 */
	public UntypedData getValue() {
		IdentifierRelation identifierRelation = new IdentifierRelation();
		identifierRelation.addAll(this._relations);
		return identifierRelation;
	}
	
	@Override
	public void setTitle(String title) {
		_title = title;
	}

}
