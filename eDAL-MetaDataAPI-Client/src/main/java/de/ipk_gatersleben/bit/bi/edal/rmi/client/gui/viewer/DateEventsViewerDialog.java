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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalDateFormat;

/**
 * The <code>DateEventsviewerDialog</code> can be used to view
 * <code>DateEvents</code>, which implements the <code>MetadataviewDialog</code>
 * class, we can use it with a couple of lines of code:
 * 
 * <pre>
 * DateEventsviewerDialog dateEventsviewerDialog = new DateEventsviewerDialog(dateevents);
 * dateEventsviewerDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class DateEventsViewerDialog extends MetadataViewDialog {
	private static final long serialVersionUID = 1L;
	private DateEvents dateevents;
	private DefaultTableModel defaultModel = null;
	private JTable infotable = null;
	private Map<Integer, EdalDate> containmap = new HashMap<Integer, EdalDate>();

	/**
	 * Constructs a <code>DateEventsviewerDialog</code> that is initialized with
	 * <code>dateevents</code>.
	 *
	 * @param dateevents
	 *            DateEvents object to show in DateEventsviewerDialog
	 * @param title
	 *            the title for the dialog
	 */
	public DateEventsViewerDialog(DateEvents dateevents, String title) {
		super();
		this.dateevents = dateevents;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel showPane = new JPanel();
		showPane.setLayout(new BorderLayout());
		showPane.setPreferredSize(new Dimension((int) (700 * 0.618), 100));

		defaultModel = buildTable();
		infotable = new JTable(defaultModel);
		infotable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		infotable.setEnabled(false);

		fitTableColumns(infotable);
		/*
		 * showPane.add(infotable.getTableHeader(), BorderLayout.PAGE_START);
		 * showPane.add(infotable, BorderLayout.CENTER);
		 * 
		 */
		showPane.add(new JScrollPane(infotable), BorderLayout.CENTER);
		contents.add(showPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(400, (int) (400 * 0.618)));

		initdata();
	}

	@Override
	public void initdata() {

	}

	private void fitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();

		Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) myTable.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column);
			column.setWidth(width + myTable.getIntercellSpacing().width);
		}
	}

	private DefaultTableModel buildTable() {
		List<String> columnlist = new ArrayList<String>();
		columnlist.add("DateType");
		columnlist.add("StartTime");
		columnlist.add("EndTime");
		columnlist.add("Event");

		final Object[] columnNames = columnlist.toArray(new String[0]);
		List<List<String>> datalist = new ArrayList<List<String>>();

		if (dateevents != null) {
			containmap.clear();

			Set<EdalDate> eventset = dateevents.getSet();
			int i = 0;
			for (EdalDate basicdate : eventset) {
				containmap.put(i++, basicdate);

				List<String> rowlist = new ArrayList<String>();

				if (basicdate instanceof de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange) {
					rowlist.add("DateRange");
					if (basicdate.getStartDate() != null && basicdate.getStartPrecision() != null) {
						rowlist.add(EdalDateFormat.getDefaultDateFormat(basicdate.getStartPrecision())
								.format(basicdate.getStartDate().getTime()));
					} else {
						rowlist.add("");
					}

					if (((EdalDateRange) basicdate).getEndDate() != null
							&& ((EdalDateRange) basicdate).getEndPrecision() != null) {
						rowlist.add(EdalDateFormat.getDefaultDateFormat(((EdalDateRange) basicdate).getEndPrecision())
								.format(((EdalDateRange) basicdate).getEndDate().getTime()));
					} else {
						rowlist.add("");
					}
				} else {
					rowlist.add("Timepoint");
					if (basicdate.getStartDate() != null && basicdate.getStartPrecision() != null) {
						rowlist.add(EdalDateFormat.getDefaultDateFormat(basicdate.getStartPrecision())
								.format(basicdate.getStartDate().getTime()));
					} else {
						rowlist.add("");
					}

					rowlist.add("");
				}

				if (basicdate.getString() != null) {
					rowlist.add(basicdate.getString());
				} else {
					rowlist.add("");
				}

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
		return new DefaultTableModel(rowData, columnNames);
	}

}
