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
