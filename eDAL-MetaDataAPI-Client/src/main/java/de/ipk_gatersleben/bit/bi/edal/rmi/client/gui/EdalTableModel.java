/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import javax.swing.table.DefaultTableModel;
/**
 *  The <code>EDALTableModel</code> can be set up to display any data
 *  model which implements the 
 *  <code>TableModel</code> interface with a couple of lines of code:
 *  <pre>
 *  	EDALTableModel myData = new EDALTableModel(data,columnNames); 
 *  	JTable table = new JTable(myData);
 *  </pre>
 * we implement this class only because we want to make the table is readonly,
 * so the isCellEditable function return false
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalTableModel extends DefaultTableModel{
	private static final long serialVersionUID = 1L;
	
	public EdalTableModel(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
