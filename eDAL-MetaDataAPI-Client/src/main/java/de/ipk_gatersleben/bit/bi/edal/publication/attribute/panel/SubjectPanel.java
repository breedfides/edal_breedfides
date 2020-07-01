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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;

public class SubjectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8109825692298261311L;

	private static final AttributeLabel SUBJECTS_LABEL = new AttributeLabel(
			PropertyLoader.props.getProperty("SUBJECTS_LABEL"), PropertyLoader.props.getProperty("SUBJECTS_TOOLTIP"));;

	private static final SmallButton OKAY_BUTTON = new SmallButton("OK");
	private static final SmallButton ADD_BUTTON = new SmallButton("ADD KEYWORD");
	private static final SmallButton REMOVE_BUTTON = new SmallButton("REMOVE KEYWORD");

	private DefaultTableModel model;
	private JTable table;
	private JScrollPane scrollPane;

	private static final String[] DEFAULT_VALUE = new String[] { "" };
	private static final String[] COL_NAMES = { "Keywords" };

	public SubjectPanel() {

		GridBagLayout gridBagLayout = new GridBagLayout();

		JPanel tablePanel = new JPanel(gridBagLayout);

		tablePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		tablePanel.setMinimumSize(
				new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.SUBJECTS_PANEL_HEIGHT));

		EmptyBorder inBorder = new EmptyBorder(2, 2, 0, 2);
		EmptyBorder outBorder = new EmptyBorder(2, 2, 0, 2);
		tablePanel.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		model = new MyTableModel();
		table = new JTable(model);

		// set the editor as default on every column
		for (int i = 0; i < table.getColumnCount(); i++) {
			((DefaultCellEditor) table.getDefaultEditor(table.getColumnClass(i))).setClickCountToStart(1);
		}

		table.setFillsViewportHeight(true);
		table.setFont(PropertyLoader.DEFAULT_FONT);
		table.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		table.setTableHeader(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer(model));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		buttonPanel.add(OKAY_BUTTON);
		buttonPanel.add(ADD_BUTTON);
		buttonPanel.add(REMOVE_BUTTON);

		OKAY_BUTTON.addActionListener(this);
		ADD_BUTTON.addActionListener(this);
		REMOVE_BUTTON.addActionListener(this);

		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.SUBJECTS_PANEL_HEIGHT));

		Utils.add(tablePanel, gridBagLayout, scrollPane, 0, 0, 1, 1, 1, 1, 1, 1);
		Utils.add(tablePanel, gridBagLayout, buttonPanel, 0, 1, 1, 1, 1, 1, 1, 1);

		JPanel attributePanel = new JPanel(new GridLayout());

		SubjectPanel.SUBJECTS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		attributePanel.add(SubjectPanel.SUBJECTS_LABEL);
		attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributePanel.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.SUBJECTS_PANEL_HEIGHT));

		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.setLayout(new BorderLayout());
		this.add(attributePanel, BorderLayout.WEST);
		this.add(tablePanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(OKAY_BUTTON)) {

			/** stop cell editing */
			CellEditor cellEditor = this.table.getCellEditor();
			if (cellEditor != null && cellEditor.getCellEditorValue() == null) {
				cellEditor.stopCellEditing();
			}

			/** clear(unselect) selected rows */
			this.table.clearSelection();

			/** remove rows without any name */
			ArrayList<Integer> emptyLines = new ArrayList<Integer>();

			for (int i = 0; i < this.table.getRowCount(); i++) {
				if (Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 0).toString())) {
					emptyLines.add(i);
				}
			}

			for (int i = emptyLines.size() - 1; i >= 0; i--) {
				this.model.removeRow(emptyLines.get(i));
			}

			PublicationMainPanel.subjectPanel = this;

			PublicationMainPanel.subjectsField.setText(getTableContent());

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.descriptionSubjectsSplitPanel.setRightComponent(null);

			PropertyLoader.SUBJECTS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			PublicationMainPanel.descriptionSubjectsSplitPanel
					.setRightComponent(new AttributeLableAttributeTextAreaPanel(PropertyLoader.SUBJECTS_LABEL,
							PublicationMainPanel.subjectsField, PropertyLoader.SUBJECTS_PANEL_HEIGHT));

			PublicationFrame.updateUI();
			// PublicationModul.getFrame().requestFocusInWindow();
			PublicationButtonLinePanel.getNextButton().requestFocus();

			saveUserValues();

		} else if (actionEvent.getSource().equals(ADD_BUTTON)) {

			if (this.table.getSelectedRow() == -1) {
				/** no row is selected */
				this.model.addRow(DEFAULT_VALUE);
			} else {
				/** a row is selected */
				this.model.insertRow(this.table.getSelectedRow() + 1, DEFAULT_VALUE);
			}
			this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
		}

		else if (actionEvent.getSource().equals(REMOVE_BUTTON)) {

			/** stop cell editing */
			CellEditor cellEditor = this.table.getCellEditor();
			if (cellEditor != null && cellEditor.getCellEditorValue() == null) {
				cellEditor.stopCellEditing();
			}

			/** at least one row is available */
			if (this.model.getRowCount() > 0) {
				if (this.table.getSelectedRow() == -1) {
					/** no row is selected */
					this.model.removeRow(this.table.getRowCount() - 1);
				} else {
					/** a row is selected */
					this.model.removeRow(this.table.getSelectedRow());
				}
			}
			this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());
		}

	}

	private void saveUserValues() {

		StringBuffer buffer = new StringBuffer();
		String[] subjects = getSubjects();

		for (int i = 0; i < subjects.length; i++) {
			buffer.append(subjects[i]);
			if (i != subjects.length - 1) {
				buffer.append(",");
			}
		}
		PropertyLoader.setUserValue("SUBJECTS", buffer.toString());
	}

	private class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 8557354261856948625L;

		public MyTableModel() {

			setDataVector(loadUserValues(), COL_NAMES);
		}

		private Object[][] loadUserValues() {

			String string = PropertyLoader.userValues.getProperty("SUBJECTS");

			if (string == null || string.isEmpty()) {
				return new Objects[0][0];
			} else {
				String[] subjects = string.split(",");

				Object[][] data = new Object[subjects.length][1];

				for (int j = 0; j < subjects.length; j++) {
					data[j][0] = subjects[j];
				}
				return data;
			}
		}
	}

	private String getTableContent() {

		String nameSeperator = new String(", ");

		StringBuffer buffer = new StringBuffer("");

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 0).toString())) {
				buffer.append(this.model.getValueAt(i, 0).toString());
				buffer.append(nameSeperator);
			}
		}

		if (buffer.toString().lastIndexOf(nameSeperator) != -1) {
			return buffer.toString().substring(0, buffer.toString().lastIndexOf(nameSeperator));
		} else {
			return PropertyLoader.props.getProperty("DEFAULT_SUBJECTS_STRING");
		}
	}

	public String[] getSubjects() {

		String[] subjects = new String[this.model.getRowCount()];

		for (int i = 0; i < this.model.getRowCount(); i++) {
			subjects[i] = this.model.getValueAt(i, 0).toString();
		}
		return subjects;
	}

	private static class CustomRenderer extends DefaultTableCellRenderer {

		/**
		* 
		*/
		private static final long serialVersionUID = 8309932008829066343L;

		public CustomRenderer(DefaultTableModel model) {
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int col) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			if (isSelected) {
				c.setBackground(PropertyLoader.TABLE_HAS_VALUE_BACKGROUND_COLOR);
			} else {
				c.setBackground(PropertyLoader.TABLE_HAS_VALUE_BACKGROUND_COLOR);
			}
			return c;
		}
	}

}