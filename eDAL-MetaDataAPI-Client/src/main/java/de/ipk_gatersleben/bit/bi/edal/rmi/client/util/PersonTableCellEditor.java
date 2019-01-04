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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class PersonTableCellEditor extends JComboBox<String> implements TableCellEditor{
	private static final long serialVersionUID = 1L;
	private JTable table;
	private int row;
	private int col;

	public PersonTableCellEditor(final String[] values,final boolean bindevent) {
        super();
        
        for(String value:values)
        {
        	addItem(value);
        }
        
       
        this.addItemListener(new ItemListener() {
                 public void itemStateChanged(ItemEvent e) {
                     if (e.getStateChange() == ItemEvent.SELECTED) {
                    	 if(bindevent)
                    	 {
                    		 if(e.getItem().toString().equals(values[0]))
                             {
                             	table.getColumnModel().getColumn(1).setCellEditor(new ReadonlyTableCellEditor(false));
                             	table.getColumnModel().getColumn(2).setCellEditor(new ReadonlyTableCellEditor(false));
                             	table.getColumnModel().getColumn(3).setCellEditor(new ReadonlyTableCellEditor(true));
                             }
                             else
                             {
                             	table.getColumnModel().getColumn(1).setCellEditor(new ReadonlyTableCellEditor(true));
                             	table.getColumnModel().getColumn(2).setCellEditor(new ReadonlyTableCellEditor(true));
                             	table.getColumnModel().getColumn(3).setCellEditor(new ReadonlyTableCellEditor(false));
                             }
                    	 }                        
                         table.getModel().setValueAt(e.getItem(), row, col);
                         // System.out.println("new data:"+e.getItem());
                         table.updateUI();
                         ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                     }
                 }
        });
    }
	
	public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row,
            int column) {
		this.table = table;
		this.row = row;
		this.col = column;
		return this;
	}

	public void cancelCellEditing() {
	}

	public boolean stopCellEditing() {
		return true;
	}

	public Object getCellEditorValue() {
        return this.getSelectedItem();
    }
	
	public void setValue(final Object value) {
        this.setSelectedItem(value);
    }

	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
            final MouseEvent e = (MouseEvent) anEvent;
            return e.getID() != MouseEvent.MOUSE_DRAGGED;
        }
        return true;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void removeCellEditorListener(CellEditorListener l) {
	}
}
