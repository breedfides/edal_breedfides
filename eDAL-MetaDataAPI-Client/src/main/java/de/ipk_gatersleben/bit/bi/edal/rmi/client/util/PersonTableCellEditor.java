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
