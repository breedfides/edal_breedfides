/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
