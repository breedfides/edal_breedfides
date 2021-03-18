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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor.PersonsDialog;

public class PersonTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	private PersonsDialog personDialog;

	public void setTable(PersonsDialog personDialog) {
		this.personDialog = personDialog;
	}

	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
		if (column == 0) {
			if (personDialog!=null && personDialog.getTable() != null) {
				if (getValueAt(row, 0).toString().equals("LegalPerson")) {
					personDialog.getTable().getColumnModel().getColumn(1)
							.setCellEditor(new ReadonlyTableCellEditor(false));
					personDialog.getTable().getColumnModel().getColumn(2)
							.setCellEditor(new ReadonlyTableCellEditor(false));
					personDialog.getTable().getColumnModel().getColumn(3)
							.setCellEditor(new ReadonlyTableCellEditor(true));
				} else if(getValueAt(row, 0).toString().equals("NaturalPerson")){
					personDialog.getTable().getColumnModel().getColumn(1)
							.setCellEditor(new ReadonlyTableCellEditor(true));
					personDialog.getTable().getColumnModel().getColumn(2)
							.setCellEditor(new ReadonlyTableCellEditor(true));
					personDialog.getTable().getColumnModel().getColumn(3)
							.setCellEditor(new ReadonlyTableCellEditor(false));
				}
				personDialog.refreshcolor();
				personDialog.getTable().setModel(this);
				fireTableDataChanged();
			}
		}
	}
}
