/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.PersonTableCellEditor;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.PersonTableModel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.ReadonlyTableCellEditor;

/**
 * The <code>PersonDialog</code> can be used to edit <code>Person</code>, which
 * implements the <code>MetadataeditDialog</code> class, we can use it with a
 * couple of lines of code:
 * 
 * <pre>
 * PersonDialog personDialog = new PersonDialog(person);
 * personDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class PersonsDialog extends MetaDataEditDialog {
	private static final long serialVersionUID = 1L;

	private static final String NATURALPERSON = "NaturalPerson";
	private static final String LEGALPERSON = "LegalPerson";

	private static final String[] value = { LEGALPERSON, NATURALPERSON };

	private PersonTableModel model;
	private JTable table;
	private Persons persons;

	private static final JButton ADD_BUTTON = new JButton("Add Person");
	private static final JButton REMOVE_BUTTON = new JButton("Remove Person");
	private static final String[] DEFAULT_CONTRIBUTOR_VALUE = new String[] { value[0], "", "", "", "Corrensstraße 3",
			"06466", "Germany" };

	private static final Set<Integer> legalrows = new HashSet<Integer>();

	/**
	 * Constructs a <code>PersonDialog</code> that is initialized with
	 * <code>person</code>.
	 * 
	 * @param persons
	 *            {@link Persons} object to show in PersonDialog
	 * @param title
	 *            the title for the dialog
	 */
	public PersonsDialog(Persons persons, String title) {
		super();

		this.persons = persons;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		final JPanel editPane = new JPanel(new BorderLayout());

		model = new PersonTableModel();
		model.addColumn("Persontype");
		model.addColumn("Givenname");
		model.addColumn("Surname");
		model.addColumn("Legalname");
		model.addColumn("Address");
		model.addColumn("Zip");
		model.addColumn("Country");
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(24);
		TableColumn col = table.getColumnModel().getColumn(0);
		// col.setCellEditor(new DefaultCellEditor(comboRow));
		col.setCellEditor(new PersonTableCellEditor(value, true));
		col.setMinWidth(90);
		table.getColumnModel().getColumn(4).setMinWidth(150);
		model.setTable(this);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		buttonPanel.add(ADD_BUTTON);
		buttonPanel.add(REMOVE_BUTTON);

		ADD_BUTTON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.insertRow(table.getSelectedRow() + 1, DEFAULT_CONTRIBUTOR_VALUE);
				table.getColumnModel().getColumn(1).setCellEditor(new ReadonlyTableCellEditor(false));
				table.getColumnModel().getColumn(2).setCellEditor(new ReadonlyTableCellEditor(false));
				table.getColumnModel().getColumn(3).setCellEditor(new ReadonlyTableCellEditor(true));

				refreshcolor();
			}
		});

		REMOVE_BUTTON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (model.getRowCount() != 0) {
					if (table.getSelectedRow() != -1) {
						model.removeRow(table.getSelectedRow());
					} else {
						model.removeRow(model.getRowCount() - 1);
					}
				}
				refreshcolor();
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		// scrollPane.setPreferredSize(new Dimension(500, 160));
		editPane.add(scrollPane, BorderLayout.CENTER);
		editPane.add(buttonPanel, BorderLayout.SOUTH);

		contents.add(editPane, BorderLayout.CENTER);
		editPane.setBorder(BorderFactory.createTitledBorder(""));
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setPreferredSize(new Dimension(720, (int) (720 * 0.618)));
		this.setMinimumSize(new Dimension(720, (int) (720 * 0.618)));

		initdata();
	}

	@Override
	public void initdata() {
		String[] locales = Locale.getISOCountries();
		String[] countrynames = new String[locales.length];
		for (int i = 0; i < locales.length; i++) {
			Locale obj = new Locale("", locales[i]);
			/*
			 * System.out.println("Country Code = " + obj.getCountry() +
			 * ", Country Name = " + obj.getDisplayCountry());
			 */
			countrynames[i] = obj.getDisplayCountry();
		}
		TableColumn col = table.getColumnModel().getColumn(6);
		Arrays.sort(countrynames);
		col.setCellEditor(new PersonTableCellEditor(countrynames, false));
		/*
		 * JTableHeader header = table.getTableHeader();
		 * header.setResizingColumn(col); col.setWidth(80);
		 */
		if (persons != null) {
			for (Person person : persons) {
				if (person instanceof de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson) {
					model.insertRow(table.getRowCount(),
							new String[] { NATURALPERSON, ((NaturalPerson) person).getGivenName(),
									((NaturalPerson) person).getSureName(), "", person.getAddressLine(),
									person.getZip(), person.getCountry() });
					table.getColumnModel().getColumn(1).setCellEditor(new ReadonlyTableCellEditor(true));
					table.getColumnModel().getColumn(2).setCellEditor(new ReadonlyTableCellEditor(true));
					table.getColumnModel().getColumn(3).setCellEditor(new ReadonlyTableCellEditor(false));
				} else {
					int row = table.getRowCount();
					model.insertRow(row, new String[] { LEGALPERSON, "", "", ((LegalPerson) person).getLegalName(),
							person.getAddressLine(), person.getZip(), person.getCountry() });
					table.getColumnModel().getColumn(1).setCellEditor(new ReadonlyTableCellEditor(false));
					table.getColumnModel().getColumn(2).setCellEditor(new ReadonlyTableCellEditor(false));
					table.getColumnModel().getColumn(3).setCellEditor(new ReadonlyTableCellEditor(true));
				}
			}
			refreshcolor();
		}
		// UiUtil.fitTableColumns(table);
	}

	/**
	 * Returns the Person inputted by user.
	 * 
	 * @return the Person inputted by user.
	 */
	public Persons getPerson() {
		Persons persons = new Persons();
		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (this.model.getValueAt(i, 0).toString().equals("LegalPerson")) {
				LegalPerson person = new LegalPerson(this.model.getValueAt(i, 3).toString(),
						this.model.getValueAt(i, 4).toString(), this.model.getValueAt(i, 5).toString(),
						this.model.getValueAt(i, 6).toString());
				persons.add(person);
			} else {
				NaturalPerson person = new NaturalPerson(this.model.getValueAt(i, 1).toString(),
						this.model.getValueAt(i, 2).toString(), this.model.getValueAt(i, 4).toString(),
						this.model.getValueAt(i, 5).toString(), this.model.getValueAt(i, 6).toString());
				persons.add(person);
			}
		}
		return persons;
	}

	public void refreshcolor() {
		legalrows.clear();
		table.getColumnModel().getColumn(1).setCellRenderer(new ReadonlyRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new ReadonlyRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new ReadonlyRenderer());
		for (int i = 0; i < this.model.getRowCount(); i++) {
			if (this.model.getValueAt(i, 0).toString().equals("LegalPerson")) {
				legalrows.add(i);
				model.setValueAt("", i, 1);
				model.setValueAt("", i, 2);
			} else {
				model.setValueAt("", i, 3);
			}
		}
	}

	public JTable getTable() {
		return table;
	}

	private class ReadonlyRenderer extends DefaultTableCellRenderer implements Icon {
		private static final long serialVersionUID = 1L;
		private static final int SIZE = 32;
		private int row;
		private int column;

		public ReadonlyRenderer() {
			this.setIcon(this);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			/*
			 * if(legalrows.contains(row)) { if(column==1 || column==2) {
			 * cell.setBackground(Color.RED); } else {
			 * cell.setBackground(Color.GREEN); } } else { if(column==3) {
			 * cell.setBackground(Color.RED); } else {
			 * cell.setBackground(Color.GREEN); } }
			 */
			this.row = row;
			this.column = column;
			return cell;
		}

		protected void setValue(Object value) {
			setText((value == null) ? "" : value.toString());
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (legalrows.contains(row)) {
				if (column == 1 || column == 2) {
					drawreadonlyline(g, x, y);
				} else {
					drawreadonlytext(g, x, y);
				}
			} else {
				if (column == 3) {
					drawreadonlyline(g, x, y);
				} else {
					drawreadonlytext(g, x, y);
				}
			}

		}

		public int getIconWidth() {
			return SIZE;
		}

		public int getIconHeight() {
			return SIZE;
		}

		private void drawreadonlyline(Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(2f));
			g2d.setColor(Color.red);
			g2d.drawLine(x - SIZE, y, x + this.getWidth(), y + this.getHeight() + 2);
		}

		private void drawreadonlytext(Graphics g, int x, int y) {

		}
	}

}
