/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.util.Collection;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>IdentifierRelationViewer</code> wrappers class
 * <code>IdentifierRelationviewDialog</code> as a <code>MetadataViewer</code>
 * Dialog to edit IdentifierRelation datatype. we can use it with a couple of
 * lines of code:
 * 
 * <pre>
 * IdentifierRelationViewer identifierRelationViewer = new IdentifierRelationViewer();
 * identifierRelationViewer.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class IdentifierRelationViewer extends MetadataViewer {
	private Collection<Identifier> _relations;
	private String _title;

	/**
	 * pop up a IdentifierRelationviewDialog Dialog
	 */
	public int showOpenDialog() {
		IdentifierRelationViewDialog dlg = new IdentifierRelationViewDialog(this._relations, this._title);
		int returnVal = dlg.showOpenDialog();
		return returnVal;
	}

	/**
	 * set the IdentifierRelation value.
	 * 
	 * set the IdentifierRelation value.
	 */
	public void setValue(UntypedData relations) {
		if (relations != null) {
			this._relations = ((IdentifierRelation) relations).getRelations();
		}
	}

	@Override
	public void setTitle(String title) {
		_title = title;
	}
}
