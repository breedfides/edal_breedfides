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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;
public class PublisherPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8109825692298261311L;

	private static final AttributeLabel PUBLISHER_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("PUBLISHER_LABEL"), PropertyLoader.props.getProperty("PUBLISHER_TOOLTIP"));;

	private static final SmallButton OKAY_BUTTON = new SmallButton("OK");

	private JTable table;

	private static final String[] DEFAULT_PUBLISHER = new String[] { "Leibniz Institute of Plant Genetics and Crop Plant Research (IPK)", "Stadt Seeland, OT Gatersleben, Corrensstraße 3", "06466", "Germany" };

	private static final String[] COL_NAMES = { "Name", "Address", "Zip", "Country" };

	public PublisherPanel() {

		/* create panel with JTable */
		table = new JTable(new MyTableModel());

		// set the editor as default on every column
		for (int i = 0; i < table.getColumnCount(); i++) {
			((DefaultCellEditor) table.getDefaultEditor(table.getColumnClass(i))).setClickCountToStart(1);
		}

		table.setFillsViewportHeight(true);
		table.setFont(PropertyLoader.DEFAULT_FONT);
		table.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		table.getTableHeader().setFont(PropertyLoader.DEFAULT_FONT);
		table.getTableHeader().setForeground(PropertyLoader.MAIN_FONT_COLOR);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		table.getColumnModel().getColumn(0).setPreferredWidth((PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100) * 50);
		table.getColumnModel().getColumn(1).setPreferredWidth((PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100) * 30);
		table.getColumnModel().getColumn(2).setPreferredWidth((PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100) * 5);
		table.getColumnModel().getColumn(3).setPreferredWidth((PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100) * 10);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		buttonPanel.add(OKAY_BUTTON);

		OKAY_BUTTON.addActionListener(this);

		JScrollPane scrollPane = new JScrollPane(table);

		scrollPane.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.PUBLISHER_PANEL_HEIGHT));

		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		/* create inner main panel */
		GridBagLayout gridBagLayout = new GridBagLayout();

		JPanel tablePanel = new JPanel(gridBagLayout);
		tablePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		tablePanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.PUBLISHER_PANEL_HEIGHT));

		EmptyBorder inBorder = new EmptyBorder(2, 2, 0, 2);
		EmptyBorder outBorder = new EmptyBorder(2, 2, 0, 2);
		tablePanel.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		Utils.add(tablePanel, gridBagLayout, scrollPane, 0, 0, 1, 1, 1, 0.95, 1, 1);
		Utils.add(tablePanel, gridBagLayout, buttonPanel, 0, 1, 1, 1, 1, 0.05, 1, 1);

		JPanel attributePanel = new JPanel(new GridLayout());

		PublisherPanel.PUBLISHER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		attributePanel.add(PublisherPanel.PUBLISHER_LABEL);
		attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributePanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.PUBLISHER_PANEL_HEIGHT));

		/* create PublisherPanel */

		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.setLayout(new BorderLayout());
		this.add(attributePanel, BorderLayout.WEST);
		this.add(tablePanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(OKAY_BUTTON)) {

			CellEditor cellEditor = this.table.getCellEditor();
			if (cellEditor != null && cellEditor.getCellEditorValue() == null) {
				cellEditor.stopCellEditing();
			}

			/* clear(unselect) selected rows */
			this.table.clearSelection();

			PublicationMainPanel.publisherPanel = this;

			PublicationMainPanel.publisherField.setText(getTableContent());

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.subjectsRelatedIdentiferPanel.setRightComponent(null);

			PropertyLoader.PUBLISHER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			PublicationMainPanel.subjectsRelatedIdentiferPanel.setRightComponent(new AttributeLableAttributeTextAreaPanel(PropertyLoader.PUBLISHER_LABEL, PublicationMainPanel.publisherField, (PropertyLoader.PUBLISHER_PANEL_HEIGHT)));

			PublicationFrame.updateUI();
//			PublicationModul.getFrame().requestFocusInWindow();
			PublicationButtonLinePanel.getNextButton().requestFocus();

			saveUserValues();
		}
	}

	private void saveUserValues() {

		StringBuffer buffer = new StringBuffer();
		String seperator = new String(";");

		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 0).toString())) {
			buffer.append(this.table.getValueAt(0, 0).toString());
		}
		buffer.append(seperator);
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 1).toString())) {
			buffer.append(this.table.getValueAt(0, 1).toString());
		}
		buffer.append(seperator);
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 2).toString())) {
			buffer.append(this.table.getValueAt(0, 2).toString());
		}
		buffer.append(seperator);
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 3).toString())) {
			buffer.append(this.table.getValueAt(0, 3).toString());
		}

		PropertyLoader.setUserValue("PUBLISHER", buffer.toString());
	}

	private class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 8557354261856948625L;

		public MyTableModel() {

			setDataVector(loadUserValues(), COL_NAMES);
		}

		private Object[][] loadUserValues() {

			String string = PropertyLoader.userValues.getProperty("PUBLISHER");

			if (string == null || string.isEmpty()) {

				String[] authors = PropertyLoader.props.getProperty("DEFAULT_PUBLISHER_STRING").split(";");

				Object[][] data = new Object[1][4];

				for (int i = 0; i < authors.length; i++) {
					data[0][i] = authors[i];
				}
				return data;

			} else {
				String[] authors = string.split(";");

				Object[][] data = new Object[1][4];

				for (int i = 0; i < authors.length; i++) {
					data[0][i] = authors[i];
				}
				return data;
			}
		}
	}

	private String getTableContent() {

		String seperator = new String(", ");

		StringBuffer buffer = new StringBuffer("");

		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 0).toString())) {

			buffer.append(this.table.getValueAt(0, 0).toString());
			buffer.append(seperator);
		}
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 1).toString())) {

			buffer.append(this.table.getValueAt(0, 1).toString());
			buffer.append(seperator);
		}
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 2).toString())) {

			buffer.append(this.table.getValueAt(0, 2).toString());
			buffer.append(seperator);
		}

		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 3).toString())) {

			buffer.append(this.table.getValueAt(0, 3).toString());
		}

		if (Utils.checkIfStringIsEmpty(buffer.toString())) {

			Object[][] data = { DEFAULT_PUBLISHER };
			((MyTableModel) this.table.getModel()).setDataVector(data, COL_NAMES);
			return PropertyLoader.props.getProperty("DEFAULT_PUBLISHER_STRING");
		} else {
			return buffer.toString();
		}

	}

	public LegalPerson getPublisher() {

		String legalName = "unkown";
		String addressLine = "unkown";
		String zip = "unkown";
		String country = "unkown";

		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 0).toString())) {
			legalName = this.table.getValueAt(0, 0).toString();
		}
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 1).toString())) {
			addressLine = this.table.getValueAt(0, 1).toString();
		}
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 2).toString())) {
			zip = this.table.getValueAt(0, 2).toString();
		}
		if (!Utils.checkIfStringIsEmpty(this.table.getValueAt(0, 3).toString())) {
			country = this.table.getValueAt(0, 3).toString();
		}

		LegalPerson legalPerson = new LegalPerson(legalName, addressLine, zip, country);

		return legalPerson;
	}
}
