/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;

/**
 * The <code>PersonviewDialog</code> can be used to view <code>Person</code>,
 * which implements the <code>MetadataviewDialog</code> class, we can use it
 * with a couple of lines of code:
 * 
 * <pre>
 * PersonviewDialog personviewDialog = new PersonviewDialog(person);
 * personviewDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class PersonsViewDialog extends MetadataViewDialog {
	private static final long serialVersionUID = 1L;

	private DefaultTableModel model;
	private JTable table;
	private Persons persons;

	/**
	 * Constructs a <code>PersonDialog</code> that is initialized with
	 * <code>person</code>.
	 * 
	 * @param persons
	 *            {@link Persons} object to show in PersonDialog
	 * @param title
	 *            the title for the dialog
	 */
	public PersonsViewDialog(Persons persons, String title) {
		super();

		this.persons = persons;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model.addColumn("Persontype");
		model.addColumn("Surname");
		model.addColumn("Givenname");
		model.addColumn("Legalname");
		model.addColumn("Address");
		model.addColumn("Zip");
		model.addColumn("Country");
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(24);

		JScrollPane scrollPane = new JScrollPane(table);

		contents.add(scrollPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(720, (int) (720 * 0.618)));

		initdata();
	}

	@Override
	public void initdata() {
		if (persons != null) {
			for (Person person : persons) {
				if (person instanceof de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson) {
					model.insertRow(table.getRowCount(),
							new String[] { "NaturePerson", ((NaturalPerson) person).getSureName(),
									((NaturalPerson) person).getGivenName(), "", person.getAddressLine(),
									person.getZip(), person.getCountry() });
				} else {
					model.insertRow(table.getRowCount(),
							new String[] { "LegalPerson", "", "", ((LegalPerson) person).getLegalName(),
									person.getAddressLine(), person.getZip(), person.getCountry() });
				}
			}
		}
	}

}
