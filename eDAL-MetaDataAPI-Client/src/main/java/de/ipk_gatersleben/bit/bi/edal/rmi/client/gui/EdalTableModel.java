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
