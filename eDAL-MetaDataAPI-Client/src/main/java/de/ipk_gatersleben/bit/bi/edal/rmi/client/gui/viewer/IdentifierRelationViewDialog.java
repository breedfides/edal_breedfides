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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalTableModel;

/**
 * The <code>IdentifierRelationviewDialog</code> can be used to view
 * <code>Identifier</code> relations, which implements the
 * <code>MetadataviewDialog</code> class, we can use it with a couple of lines
 * of code:
 * 
 * <pre>
 * IdentifierRelationviewDialog identifierRelationviewDialog = new IdentifierRelationviewDialog(relations);
 * identifierRelationviewDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class IdentifierRelationViewDialog extends MetadataViewDialog {
	private static final long serialVersionUID = 1L;
	private Collection<Identifier> relations;

	private JTable table;
	private EdalTableModel dm;

	/**
	 * Constructs a <code>IdentifierRelationviewDialog</code> that is
	 * initialized with <code>relations</code>.
	 *
	 * @param relations
	 *            identifier Collection
	 * @param title
	 *            the title for the dialog
	 */
	public IdentifierRelationViewDialog(Collection<Identifier> relations, String title) {
		super();
		this.relations = relations;
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
		columnlist.add("");
		final Object[] columnNames = columnlist.toArray(new String[0]);

		dm = buildtable(relations, columnNames);
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

		this.setMinimumSize(new Dimension(320, (int) (320 * 0.618)));
	}

	private EdalTableModel buildtable(Collection<Identifier> strs, Object[] columnNames) {
		List<List<String>> datalist = new ArrayList<List<String>>();
		if (strs != null) {
			for (Identifier idenstr : strs) {
				List<String> rowlist = new ArrayList<String>();
				rowlist.add(idenstr.getIdentifier());
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
