/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalTableModel;

/**
 * The <code>CheckSumviewDialog</code> can be used to view <code>CheckSum</code>
 * Object, which implements the <code>MetadataviewDialog</code> class, we can
 * use it with a couple of lines of code:
 * 
 * <pre>
 * CheckSumviewDialog checkSumviewDialog = new CheckSumviewDialog(checkSum);
 * checkSumviewDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class CheckSumviewDialog extends MetadataViewDialog {
	private static final long serialVersionUID = 1L;
	private CheckSum checksum;

	private JTable table;
	private EdalTableModel dm;

	/**
	 * Constructs a <code>CheckSumviewDialog</code> that is initialized with
	 * <code>CheckSum</code>.
	 * 
	 * @param checksum
	 *            <code>checkSum</code> Object
	 * @param title
	 *            the title for the dialog
	 */
	public CheckSumviewDialog(CheckSum checksum, String title) {
		super();
		this.checksum = checksum;
		setTitle(title);

		initUi();
	}

	@Override
	public void initdata() {

	}

	private void initUi() {
		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel editPane = new JPanel();
		editPane.setLayout(new BorderLayout());

		List<String> columnlist = new ArrayList<String>();
		columnlist.add("Algorithm");
		columnlist.add("CheckSum");
		final Object[] columnNames = columnlist.toArray(new String[0]);

		dm = buildtable(checksum.getSet(), columnNames);
		table = new JTable(dm);
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setColumnHeader(null);
		scrollTable.setMinimumSize(new Dimension(320, 80));

		Box tableBox = new Box(BoxLayout.Y_AXIS);
		tableBox.add(scrollTable);

		editPane.add(tableBox, BorderLayout.CENTER);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(420, (int) (420 * 0.618)));
	}

	private EdalTableModel buildtable(Set<CheckSumType> set, Object[] columnNames) {
		List<List<String>> datalist = new ArrayList<List<String>>();
		if (set != null) {
			for (CheckSumType checkSumType : set) {
				List<String> rowlist = new ArrayList<String>();
				rowlist.add(checkSumType.getAlgorithm());
				rowlist.add(checkSumType.getCheckSum());
				datalist.add(rowlist);
			}
		}
		Object[][] rowData = new Object[datalist.size()][columnNames.length];
		for (int i = 0; i < datalist.size(); i++) {
			List<String> rowlist = datalist.get(i);
			for (int j = 0; j < rowlist.size(); j++) {
				rowData[i][j] = rowlist.get(j);
			}
		}
		return new EdalTableModel(rowData, columnNames);
	}
}
