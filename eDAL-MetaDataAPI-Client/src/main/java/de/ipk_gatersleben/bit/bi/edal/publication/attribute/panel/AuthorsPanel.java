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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Class to build the panel for defining the authors, their addresses and ORCIDs
 * of a dataset
 * 
 * @author arendd
 *
 */
public class AuthorsPanel extends JPanel implements ActionListener {

	protected class AuthorTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 8557354261856948625L;

		public AuthorTableModel() {
			this.setDataVector(this.loadUserValues(), AuthorsPanel.COL_NAMES);
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			if (column == 4) {
				if (!this.getValueAt(row, 2).toString().isEmpty() || !this.getValueAt(row, 3).toString().isEmpty()
						|| !this.getValueAt(row, 1).toString().isEmpty()) {
					return false;
				} else {
					return true;
				}
			} else if (column == 2 || column == 3 || column == 1) {
				if (!this.getValueAt(row, 4).toString().isEmpty()) {
					return false;
				} else {
					return true;
				}
			}
			return true;
		}

		private String[][] loadUserValues() {

			final String string = PropertyLoader.userValues.getProperty("AUTHORS");

			if (string == null || string.isEmpty()) {
				return new String[0][0];
			} else {
				final String[] authors = string.split(";");
				final String[][] data = new String[authors.length][8];

				for (int i = 0; i < authors.length; i++) {
					if (authors[i] == null || authors[i].isEmpty()) {
						return data;
					} else {
						String[] author = new String[8];

						String[] storedAuthors = authors[i].split("@");

						for (int j = 0; j < author.length; j++) {

							if (j < storedAuthors.length) {
								if (storedAuthors[j] != null || !storedAuthors[j].isEmpty()) {
									author[j] = storedAuthors[j];
								} else {
									author[j] = "";
								}
							} else {
								author[j] = "";
							}
						}

						if (author[0].equals(CREATOR) || author[0].equals(CONTRIBUTOR)) {
							for (int j = 0; j < author.length; j++) {
								if (author[j].isEmpty() || author[j].trim().isEmpty() || author[j] == null) {
									data[i][j] = "";
								} else {
									data[i][j] = author[j];
								}
							}
						} else {
							return new String[0][0];
						}
					}
				}
				return data;
			}
		}

		@Override
		public void setValueAt(final Object value, final int row, final int column) {
			super.setValueAt(value, row, column);

			AuthorsPanel.this.updateUI();

		}
	}

	private static final long serialVersionUID = 8109825692298261311L;

	private static final AttributeLabel AUTHORS_LABEL = new AttributeLabel(
			PropertyLoader.props.getProperty("AUTHORS_LABEL"), PropertyLoader.props.getProperty("AUTHORS_TOOLTIP"));

	private static final SmallButton OKAY_BUTTON = new SmallButton("OK");
	private static final SmallButton ADD_BUTTON = new SmallButton("ADD AUTHOR");
	private static final SmallButton REMOVE_BUTTON = new SmallButton("REMOVE AUTHOR");

	public static final String CREATOR = "Creator";
	public static final String CONTRIBUTOR = "Contributor";
	private static final String[] roles = { AuthorsPanel.CREATOR, AuthorsPanel.CONTRIBUTOR };

	private static String[] DEFAULT_AUTHOR_VALUE = new String[] { AuthorsPanel.roles[0], "", "", "", "",
			PropertyLoader.props.getProperty("DEFAULT_AUTHOR_VALUE_ADDRESS"),
			PropertyLoader.props.getProperty("DEFAULT_AUTHOR_VALUE_ZIP"),
			PropertyLoader.props.getProperty("DEFAULT_AUTHOR_VALUE_COUNTRY") };
	private static final String[] COL_NAMES = { "Role", "ORCID", "Given Name", "Surname", "Group Authors", "Address",
			"Zip", "Country" };

	private DefaultTableModel model;

	private JTable table;

	private final DefaultComboBoxModel<String> comboModel;

	private final JScrollPane scrollPane;

//	static {
//		if (PublicationFrame.loggedUser.toLowerCase().contains("ipk-gatersleben")) {
//			DEFAULT_AUTHOR_VALUE = new String[] { AuthorsPanel.roles[0], "", "", "", "",
//					"Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstraße 3",
//					"06466", "Germany" };
//		} else {
//			DEFAULT_AUTHOR_VALUE = new String[] { AuthorsPanel.roles[0], "", "", "", "", "", "", "" };
//		}
//	}

	public AuthorsPanel() {

		this.model = new AuthorTableModel();
		this.table = new JTable(this.model);
		// {
		// public Component prepareRenderer(TableCellRenderer renderer, int Index_row,
		// int Index_col) {
		// Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
		// comp.setForeground(Color.BLACK); // Default colour of cell
		// if (this.getSelectedRow() == Index_row || this.getSelectedColumn() ==
		// Index_col && comp.hasFocus()) {
		// comp.setBackground(Color.RED);
		// return comp;}
		// else {
		// comp.setBackground(Color.WHITE);
		// }
		//
		// return comp;
		// }
		// };

		// set the editor as default on every column
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			((DefaultCellEditor) this.table.getDefaultEditor(this.table.getColumnClass(i))).setClickCountToStart(1);
		}

		this.table.setFillsViewportHeight(true);
		this.table.setFont(PropertyLoader.DEFAULT_FONT);
		this.table.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.table.getTableHeader().setFont(PropertyLoader.DEFAULT_FONT);
		this.table.getTableHeader().setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		this.table.getColumnModel().getColumn(0).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 15);
		this.table.getColumnModel().getColumn(1).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 22);
		this.table.getColumnModel().getColumn(2).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 13);
		this.table.getColumnModel().getColumn(3).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 13);
		this.table.getColumnModel().getColumn(4).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 15);
		this.table.getColumnModel().getColumn(5).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 40);
		this.table.getColumnModel().getColumn(6).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 10);
		this.table.getColumnModel().getColumn(7).setPreferredWidth(PropertyLoader.ATTRIBUTE_PANEL_WIDTH / 100 * 10);

		this.comboModel = new DefaultComboBoxModel<String>(AuthorsPanel.roles);
		final JComboBox<String> comboRow = new JComboBox<String>();
		comboRow.setModel(this.comboModel);
		final TableColumn col = this.table.getColumnModel().getColumn(0);
		col.setCellEditor(new DefaultCellEditor(comboRow));

		this.table.getColumnModel().getColumn(1).setCellRenderer(new AuthorCellRenderer());
		this.table.getColumnModel().getColumn(2).setCellRenderer(new AuthorCellRenderer());
		this.table.getColumnModel().getColumn(3).setCellRenderer(new AuthorCellRenderer());
		this.table.getColumnModel().getColumn(4).setCellRenderer(new AuthorCellRenderer());

		this.table.getModel().addTableModelListener(new AuthorTableModelListener(this));

		new PushableTableHeader(this.table.getColumn("ORCID"), this.table.getTableHeader());

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		buttonPanel.add(AuthorsPanel.OKAY_BUTTON);
		buttonPanel.add(AuthorsPanel.ADD_BUTTON);
		buttonPanel.add(AuthorsPanel.REMOVE_BUTTON);

		AuthorsPanel.OKAY_BUTTON.addActionListener(this);
		AuthorsPanel.ADD_BUTTON.addActionListener(this);
		AuthorsPanel.REMOVE_BUTTON.addActionListener(this);

		this.scrollPane = new JScrollPane(this.table);
		this.scrollPane.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.AUTHOR_PANEL_HEIGHT));

		final GridBagLayout grid = new GridBagLayout();
		final JPanel tablePanel = new JPanel(grid);

		tablePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		tablePanel.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.AUTHOR_PANEL_HEIGHT));

		final EmptyBorder inBorder = new EmptyBorder(5, 5, 0, 5);
		final EmptyBorder outBorder = new EmptyBorder(5, 5, 0, 5);
		tablePanel.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		Utils.add(tablePanel, grid, this.scrollPane, 0, 0, 1, 1, 1, 0.95, 1, 1);
		Utils.add(tablePanel, grid, buttonPanel, 0, 1, 1, 1, 1, 0.05, 1, 1);

		final JPanel attributePanel = new JPanel(new GridLayout());

		AuthorsPanel.AUTHORS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		attributePanel.add(AuthorsPanel.AUTHORS_LABEL);
		attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributePanel.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.AUTHOR_PANEL_HEIGHT));

		this.setLayout(new BorderLayout());
		this.add(attributePanel, BorderLayout.WEST);
		this.add(tablePanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(final ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(AuthorsPanel.OKAY_BUTTON)) {

			final CellEditor cellEditor = this.table.getCellEditor();
			if (cellEditor != null && cellEditor.getCellEditorValue() == null) {
				cellEditor.stopCellEditing();
			}

			/* clear(unselect) selected rows */
			this.table.clearSelection();
			this.comboModel.setSelectedItem(this.comboModel.getSelectedItem());
			/* remove rows without any name */
			final ArrayList<Integer> emptyLines = new ArrayList<Integer>();

			for (int i = 0; i < this.table.getRowCount(); i++) {
				if (Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())
						&& Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
						&& Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {
					emptyLines.add(i);
				}
			}

			for (int i = emptyLines.size() - 1; i >= 0; i--) {
				this.model.removeRow(emptyLines.get(i));
			}

			List<String> missingOrcids = new ArrayList<>();

			for (int i = 0; i < this.table.getRowCount(); i++) {
				if ((!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())
						|| !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString()))
						&& (this.model.getValueAt(i, 1) == null
								|| Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 1).toString()))) {
					missingOrcids.add("'" + this.model.getValueAt(i, 2).toString() + " "
							+ this.model.getValueAt(i, 3).toString() + "'");
				}
			}

			if (missingOrcids.size() != 0) {

				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < missingOrcids.size(); i++) {
					buffer.append(missingOrcids.get(i));
					if (i < missingOrcids.size() - 1) {
						buffer.append(", ");
					}

				}

				JEditorPane editorPane = new JEditorPane("text/html", "<html><body bgcolor=rgb("
						+ UIManager.getColor("Panel.background").getRed() + ","
						+ UIManager.getColor("Panel.background").getGreen() + ","
						+ UIManager.getColor("Panel.background").getBlue() + ")>" + "<font face='"
						+ PropertyLoader.DEFAULT_FONT.getFamily() + "' size='" + 3 + "'>" + "The ORCID for "
						+ buffer.toString()
						+ " is not set.<br/>It is recommend to set, otherwise authors contribution could not be recorded in citation systems like PubMed, Crossref etc.<br/>"
						+ "Please enter manual or search at  <a href='http://www.orcid.org'>www.orcid.org</a></font></body></html>");
				editorPane.setBorder(null);

				editorPane.addHyperlinkListener(new HyperlinkListener() {
					@Override
					public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
						if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							String url = hyperlinkEvent.getURL().toString();
							EdalHelpers.openURL(url);
						}
					}
				});
				editorPane.setEditable(false);

				JOptionPane.showMessageDialog(this, editorPane, "No ORCID selected", JOptionPane.WARNING_MESSAGE);

			}

			PublicationMainPanel.authorPanel = this;

			PublicationMainPanel.authorsField.setText(this.getTableContent());

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.titleAuthorSplitPanel.setRightComponent(null);

			PropertyLoader.AUTHORS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			PublicationMainPanel.titleAuthorSplitPanel
					.setRightComponent(new AttributeLableAttributeTextAreaPanel(PropertyLoader.AUTHORS_LABEL,
							PublicationMainPanel.authorsField, PropertyLoader.AUTHOR_PANEL_HEIGHT));

			PublicationFrame.updateUI();
			// PublicationModul.getFrame().requestFocusInWindow();
			PublicationButtonLinePanel.getNextButton().requestFocus();

			this.saveUserValues();

		} else if (actionEvent.getSource().equals(AuthorsPanel.ADD_BUTTON)) {

			int currentRow = -1;
			int currentColumn = -1;

			if (table.getSelectedRow() != -1 && table.getSelectedColumn() != -1) {
				AuthorTableModelListener.leavedOrcidField = false;
				AuthorTableModelListener.leavedName = false;
				currentRow = table.convertRowIndexToView(table.getSelectedRow());
				currentColumn = table.convertColumnIndexToView(table.getSelectedColumn());
			}

			boolean correspondingAuthorSetted = false;

			for (int i = 0; i < this.table.getRowCount(); i++) {
				if (this.table.getValueAt(i, 0).toString().equals(AuthorsPanel.roles[0].toString())) {
					correspondingAuthorSetted = true;
					break;
				}
			}

			if (this.table.getSelectedRow() != -1) {
				if (correspondingAuthorSetted) {
					this.model.insertRow(this.table.getSelectedRow() + 1,
							new String[] { AuthorsPanel.roles[1], "", "", "", "",
									this.table.getValueAt(this.table.getSelectedRow(), 5).toString(),
									this.table.getValueAt(this.table.getSelectedRow(), 6).toString(),
									this.table.getValueAt(this.table.getSelectedRow(), 7).toString() });
				} else {
					this.model.insertRow(this.table.getSelectedRow() + 1,
							new String[] { AuthorsPanel.roles[0], "", "", "", "",
									this.table.getValueAt(this.table.getSelectedRow(), 5).toString(),
									this.table.getValueAt(this.table.getSelectedRow(), 6).toString(),
									this.table.getValueAt(this.table.getSelectedRow(), 7).toString() });
				}
			} else {
				if (correspondingAuthorSetted) {
					this.model.addRow(new String[] { AuthorsPanel.roles[1], "", "", "", "",
							this.table.getValueAt(this.table.getRowCount() - 1, 5).toString(),
							this.table.getValueAt(this.table.getRowCount() - 1, 6).toString(),
							this.table.getValueAt(this.table.getRowCount() - 1, 7).toString() });
				} else {
					if (this.table.getRowCount() != 0) {
						this.model.addRow(new String[] { AuthorsPanel.roles[0], "", "", "", "",
								this.table.getValueAt(this.table.getRowCount() - 1, 5).toString(),
								this.table.getValueAt(this.table.getRowCount() - 1, 6).toString(),
								this.table.getValueAt(this.table.getRowCount() - 1, 7).toString() });
					} else {
						this.model.addRow(AuthorsPanel.DEFAULT_AUTHOR_VALUE);
					}
				}
			}
			if (currentColumn != -1 && currentRow != -1) {
				AuthorTableModelListener.leavedOrcidField = false;
				AuthorTableModelListener.leavedName = false;
			}

			this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());

			if (currentColumn != -1 && currentRow != -1) {
				this.table.changeSelection(currentRow, currentColumn, false, false);
				this.table.editCellAt(currentRow, currentColumn);
			}
			AuthorTableModelListener.leavedOrcidField = true;
			AuthorTableModelListener.leavedName = true;

		}

		else if (actionEvent.getSource().equals(AuthorsPanel.REMOVE_BUTTON)) {

			AuthorTableModelListener.leavedOrcidField = false;
			AuthorTableModelListener.leavedName = false;

			int currentRow = -1;
			int currentColumn = -1;
			if (table.getSelectedRow() != -1 && table.getSelectedColumn() != -1) {
				currentRow = table.convertRowIndexToView(table.getSelectedRow());
				currentColumn = table.convertColumnIndexToView(table.getSelectedColumn());
			}

			final CellEditor cellEditor = this.table.getCellEditor();
			if (cellEditor != null && cellEditor.getCellEditorValue() == null) {
				cellEditor.stopCellEditing();
			}

			if (this.model.getRowCount() != 0) {
				if (this.table.getSelectedRow() == -1) {
					this.model.removeRow(this.model.getRowCount() - 1);
				} else {
					if (this.table.getSelectedRow() < this.model.getRowCount()) {
						this.model.removeRow(this.table.getSelectedRow());
					} else {
						this.model.removeRow(this.model.getRowCount() - 1);
					}
				}
			}

			if (currentColumn != -1 && currentRow != -1) {
				AuthorTableModelListener.leavedOrcidField = false;
				AuthorTableModelListener.leavedName = false;
			}
			this.scrollPane.getVerticalScrollBar().setValue(this.scrollPane.getVerticalScrollBar().getMaximum());

			if (currentColumn != -1 && currentRow != -1) {
				this.table.changeSelection(currentRow, currentColumn, false, false);
				this.table.editCellAt(currentRow, currentColumn);
			}
			AuthorTableModelListener.leavedOrcidField = true;
			AuthorTableModelListener.leavedName = true;
		}

	}

	public String getAuthors() {

		final String authorSeperator = new String("; ");
		final String nameSeperator = new String(", ");

		final StringBuffer buffer = new StringBuffer("");

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (this.model.getValueAt(i, 0).equals(AuthorsPanel.roles[0])) {
				if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
						&& !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())) {
					buffer.append(this.model.getValueAt(i, 3).toString());
					buffer.append(nameSeperator);
					buffer.append(this.model.getValueAt(i, 2).toString());
					buffer.append(authorSeperator);
				} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())) {
					buffer.append(this.model.getValueAt(i, 2).toString());
					buffer.append(authorSeperator);
				} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())) {
					buffer.append(this.model.getValueAt(i, 3).toString());
					buffer.append(authorSeperator);
				} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {
					buffer.append(this.model.getValueAt(i, 4).toString());
					buffer.append(authorSeperator);
				}
			}

		}

		if (buffer.toString().lastIndexOf(authorSeperator) != -1) {
			return buffer.toString().substring(0, buffer.toString().lastIndexOf(authorSeperator));
		} else {
			return PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING");
		}
	}

	public Persons getContributors() {

		final Persons contributors = new Persons();

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (this.model.getValueAt(i, 0).toString().equals(AuthorsPanel.CONTRIBUTOR)) {
				if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
						|| !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())) {

					if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 1).toString())) {
						try {
							contributors.add(new NaturalPerson(this.model.getValueAt(i, 2).toString(),
									this.model.getValueAt(i, 3).toString(), this.model.getValueAt(i, 5).toString(),
									this.model.getValueAt(i, 6).toString(), this.model.getValueAt(i, 7).toString(),
									new ORCID(this.model.getValueAt(i, 1).toString())));
						} catch (ORCIDException e) {
							e.printStackTrace();
						}
					} else {
						contributors.add(new NaturalPerson(this.model.getValueAt(i, 2).toString(),
								this.model.getValueAt(i, 3).toString(), this.model.getValueAt(i, 5).toString(),
								this.model.getValueAt(i, 6).toString(), this.model.getValueAt(i, 7).toString()));
					}
				} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {
					contributors.add(new LegalPerson(this.model.getValueAt(i, 4).toString(),
							this.model.getValueAt(i, 5).toString(), this.model.getValueAt(i, 6).toString(),
							this.model.getValueAt(i, 7).toString()));
				}
			}
		}

		return contributors;

	}

	public Persons getCreators() {
		final Persons creators = new Persons();

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (this.model.getValueAt(i, 0).toString().equals(AuthorsPanel.CREATOR)) {
				if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
						|| !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())) {

					if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 1).toString())) {

						try {
							creators.add(new NaturalPerson(this.model.getValueAt(i, 2).toString(),
									this.model.getValueAt(i, 3).toString(), this.model.getValueAt(i, 5).toString(),
									this.model.getValueAt(i, 6).toString(), this.model.getValueAt(i, 7).toString(),
									new ORCID(this.model.getValueAt(i, 1).toString())));
						} catch (ORCIDException e) {
							e.printStackTrace();
						}
					} else {
						creators.add(new NaturalPerson(this.model.getValueAt(i, 2).toString(),
								this.model.getValueAt(i, 3).toString(), this.model.getValueAt(i, 5).toString(),
								this.model.getValueAt(i, 6).toString(), this.model.getValueAt(i, 7).toString()));
					}
				} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {
					creators.add(new LegalPerson(this.model.getValueAt(i, 4).toString(),
							this.model.getValueAt(i, 5).toString(), this.model.getValueAt(i, 6).toString(),
							this.model.getValueAt(i, 7).toString()));
				}

			}
		}
		return creators;

	}

	private String getTableContent() {

		if (this.getCreators().size() == 0) {
			return PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING");
		}

		final String authorSeperator = new String("; ");
		final String nameSeperator = new String(", ");

		final StringBuffer buffer = new StringBuffer("");

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
					&& !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())) {
				buffer.append(this.model.getValueAt(i, 3).toString());
				buffer.append(nameSeperator);
				buffer.append(this.model.getValueAt(i, 2).toString());
				buffer.append(authorSeperator);
			} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())) {
				buffer.append(this.model.getValueAt(i, 2).toString());
				buffer.append(authorSeperator);
			} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())) {
				buffer.append(this.model.getValueAt(i, 3).toString());
				buffer.append(authorSeperator);
			} else if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {
				buffer.append(this.model.getValueAt(i, 4).toString());
				buffer.append(authorSeperator);
			}
		}

		if (buffer.toString().lastIndexOf(authorSeperator) == -1) {
			return PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING");
		} else {
			return buffer.toString().substring(0, buffer.toString().lastIndexOf(authorSeperator));
		}
	}

	public void reset() {
		this.model = new AuthorTableModel();
		this.table = new JTable(this.model);
	}

	private void saveUserValues() {

		final String authorSeperator = new String(";");
		final String nameSeperator = new String("@");

		final StringBuffer buffer = new StringBuffer("");

		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (!Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 3).toString())
					&& !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 2).toString())
					|| !Utils.checkIfStringIsEmpty(this.model.getValueAt(i, 4).toString())) {

				if (this.model.getValueAt(i, 0) != null) {
					buffer.append(this.model.getValueAt(i, 0).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 1) != null) {
					buffer.append(this.model.getValueAt(i, 1).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 2) != null) {
					buffer.append(this.model.getValueAt(i, 2).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 3) != null) {
					buffer.append(this.model.getValueAt(i, 3).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 4) != null) {
					buffer.append(this.model.getValueAt(i, 4).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 5) != null) {
					buffer.append(this.model.getValueAt(i, 5).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 6) != null) {
					buffer.append(this.model.getValueAt(i, 6).toString());
				} else {
					buffer.append("");
				}
				buffer.append(nameSeperator);
				if (this.model.getValueAt(i, 7) != null) {
					buffer.append(this.model.getValueAt(i, 7).toString());
				} else {
					buffer.append("");
				}
				buffer.append(authorSeperator);
			}
		}

		PropertyLoader.setUserValue("AUTHORS", buffer.toString());
	}

	public JTable getTable() {
		return this.table;

	}
}