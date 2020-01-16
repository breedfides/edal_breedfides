/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalDateTimePicker;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalTableModel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.Const;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalDateFormat;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalTable;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.UiUtil;

/**
 * The <code>DateEventsDialog</code> can be used to edit <code>DateEvents</code>
 * , which implements the <code>MetadataeditDialog</code> class, we can use it
 * with a couple of lines of code:
 * 
 * <pre>
 * DateEventsDialog dataeventsdialog = new DateEventsDialog(dateevents);
 * dataeventsdialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class DateEventsDialog extends MetaDataEditDialog {
	private static final long serialVersionUID = 1L;
	private int width = 770;
	private EdalDateTimePicker starttimetext;
	private EdalDateTimePicker endtimetext;
	private JTextArea eventtext;
	private DateEvents dateevents;
	private Set<EdalDate> memoryevents = new HashSet<EdalDate>();
	private EdalTableModel defaultModel = null;
	private JTable infotable = null;
	private Map<Integer, EdalDate> containmap = new HashMap<Integer, EdalDate>();

	/**
	 * Constructs a <code>DateEventsDialog</code> that is initialized with
	 * <code>dateevents</code>.
	 * 
	 * @param dateevents
	 *            DateEvents object to show in DateEventsDialog
	 * @param title
	 *            the title for the dialog
	 */
	public DateEventsDialog(DateEvents dateevents, String title) {
		super();
		this.dateevents = dateevents;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel showPane = new JPanel();
		showPane.setLayout(new BorderLayout());
		showPane.setPreferredSize(new Dimension((int) (width * 0.618), 100));
		this.setResizable(false);

		defaultModel = buildTable();
		infotable = new EdalTable(defaultModel);
		// infotable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		UiUtil.fitTableColumns(infotable);
		/*
		 * showPane.add(infotable.getTableHeader(), BorderLayout.PAGE_START);
		 * showPane.add(infotable, BorderLayout.CENTER);
		 */
		showPane.add(new JScrollPane(infotable), BorderLayout.CENTER);

		JPanel editPane = new JPanel();

		editPane.setLayout(new MigLayout("", "[10%!][10%!][10%!][10%!][10%!][10%!][10%!][10%!][10%!][10%!]", ""));

		JLabel starttimelabel = new JLabel("StartTime:");
		editPane.add(starttimelabel, "cell 0 0 1 1");

		starttimetext = new EdalDateTimePicker(null);
		editPane.add(starttimetext, "cell 1 0 4 1,width max(35%, 35%)");

		JLabel endtimelabel = new JLabel("EndTime:");
		editPane.add(endtimelabel, "cell 5 0 1 1");

		endtimetext = new EdalDateTimePicker(null);
		editPane.add(endtimetext, "cell 6 0 4 1,width max(35%, 35%)");

		JLabel eventlabel = new JLabel("Event:");
		editPane.add(eventlabel, "cell 0 1 1 1");

		int rows = 20;
		int cols = 30;
		eventtext = new JTextArea(rows, cols);
		editPane.add(eventtext, "cell 0 2 10 3,width max(100%, 100%)");

		JButton addbtn = new JButton(addAction);
		editPane.add(addbtn, "cell 4 5 1 3");

		JButton delbtn = new JButton(delAction);
		editPane.add(delbtn, "cell 5 5 1 3");

		final JSplitPane tableshowpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(showPane), editPane);
		tableshowpane.setDividerLocation(0.8);
		tableshowpane.setResizeWeight(0.8);
		tableshowpane.setBorder(BorderFactory.createTitledBorder(""));

		contents.add(tableshowpane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(width, (int) (width * 0.618)));

		initdata();
	}

	/**
	 * Returns the DateEvents inputted by user.
	 * 
	 * @return the DateEvents inputted by user.
	 */
	public DateEvents getDateEvents() {
		if (!memoryevents.isEmpty()) {
			this.dateevents.addAll(memoryevents);
		}
		return this.dateevents;
	}

	@Override
	public void initdata() {
	}

	private EdalTableModel buildTable() {
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
			eventset.addAll(memoryevents);
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
		return new EdalTableModel(rowData, columnNames);
	}

	private Action addAction = new AbstractAction("add") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			EdalDate basicdate = null;

			if (starttimetext.getCalendar() != null && starttimetext.getPrecision() != null
					&& endtimetext.getCalendar() != null && endtimetext.getPrecision() != null
					&& eventtext.getText().trim().length() > 0) {
				basicdate = new EdalDateRange(starttimetext.getCalendar(), starttimetext.getPrecision(),
						endtimetext.getCalendar(), endtimetext.getPrecision(), eventtext.getText());

			} else if (starttimetext.getCalendar() != null && starttimetext.getPrecision() != null
					&& eventtext.getText().trim().length() > 0) {
				basicdate = new EdalDate(starttimetext.getCalendar(), starttimetext.getPrecision(),
						eventtext.getText());

			} else {
				JOptionPane.showMessageDialog(null, "Please enter at least a startdate and event!",
						Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
				return;
			}

			memoryevents.add(basicdate);

			defaultModel = buildTable();
			infotable.setModel(defaultModel);
			defaultModel.fireTableDataChanged();
			UiUtil.fitTableColumns(infotable);

			starttimetext.reset();
			endtimetext.reset();
			eventtext.setText("");
		}
	};

	private Action delAction = new AbstractAction("remove") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (infotable.getSelectedRow() > -1) {
				int iselect = infotable.getSelectedRow();
				EdalDate basicdate2del = containmap.get(iselect);
				if (basicdate2del.getEvent().equals(EdalDate.STANDART_EVENT_TYPES.CREATED.toString())
						|| basicdate2del.getEvent().equals(EdalDate.STANDART_EVENT_TYPES.UPDATED.toString())) {
					JOptionPane.showMessageDialog(null, "event " + basicdate2del.getEvent() + " can't be deleted!",
							Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
					return;
				}
				EdalDate basicdate = containmap.remove(iselect);
				dateevents.remove(basicdate);
				memoryevents.remove(basicdate);
				defaultModel.removeRow(iselect);
				defaultModel.fireTableDataChanged();
			}
		}
	};

	@Override
	public void cancelbuttonevent(ActionEvent e) {
		dateevents.removeAll(memoryevents);
		memoryevents.clear();
	}

}
