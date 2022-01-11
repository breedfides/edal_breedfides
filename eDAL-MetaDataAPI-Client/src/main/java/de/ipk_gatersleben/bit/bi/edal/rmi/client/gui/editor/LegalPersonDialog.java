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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;

/**
 * The <code>LegalPersonDialog</code> can be used to edit
 * <code>LegalPerson</code>, which implements the
 * <code>MetadataeditDialog</code> class, we can use it with a couple of lines
 * of code:
 * 
 * <pre>
 * LegalPersonDialog personDialog = new LegalPersonDialog(person, title);
 * personDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class LegalPersonDialog extends MetaDataEditDialog {
	private static final long serialVersionUID = 1L;

	private JLabel surnamelabel;
	private JTextField surnametext;
	private JLabel givennamelabel;
	private JTextField givennametext;
	private JLabel addresslabel;
	private JTextField addresstext;
	private JLabel ziplabel;
	private JTextField ziptext;
	private JLabel countrylabel;
	private JTextField countrytext;
	private JLabel legalnamelabel;
	private JTextField legalnametext;
	private LegalPerson person;
	private int layoutindex = 1;

	/**
	 * Constructs a <code>LegalPersonDialog</code> that is initialized with
	 * <code>person</code>.
	 *
	 * @param person
	 *            Person object to show in LegalPersonDialog
	 * @param title
	 *            the title for the dialog
	 */
	public LegalPersonDialog(LegalPerson person, String title) {
		super();

		this.person = person;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		final JPanel editPane = new JPanel();
		final GridBagLayout gridbag = new GridBagLayout();
		editPane.setLayout(gridbag);

		buildlegalperonui(gridbag, editPane);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(400, (int) (400 * 0.618)));

		initdata();
	}

	private void buildcommonsui(GridBagLayout gridbag, JPanel editPane) {
		if (addresslabel != null) {
			editPane.remove(addresslabel);
		}
		if (addresstext != null) {
			editPane.remove(addresstext);
		}

		if (ziplabel != null) {
			editPane.remove(ziplabel);
		}
		if (ziptext != null) {
			editPane.remove(ziptext);
		}

		if (countrylabel != null) {
			editPane.remove(countrylabel);
		}
		if (countrytext != null) {
			editPane.remove(countrytext);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 3, 0);

		addresslabel = new JLabel("Address:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(addresslabel, c);
		editPane.add(addresslabel);

		addresstext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gridbag.setConstraints(addresstext, c);
		editPane.add(addresstext);

		layoutindex++;

		ziplabel = new JLabel("Zip:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(ziplabel, c);
		editPane.add(ziplabel);

		ziptext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gridbag.setConstraints(ziptext, c);
		editPane.add(ziptext);

		layoutindex++;

		countrylabel = new JLabel("Country:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(countrylabel, c);
		editPane.add(countrylabel);

		countrytext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gridbag.setConstraints(countrytext, c);
		editPane.add(countrytext);

	}

	private void buildlegalperonui(GridBagLayout gridbag, JPanel editPane) {
		if (surnamelabel != null) {
			editPane.remove(surnamelabel);
		}
		if (surnametext != null) {
			editPane.remove(surnametext);
		}
		if (givennamelabel != null) {
			editPane.remove(givennamelabel);
		}
		if (givennametext != null) {
			editPane.remove(givennametext);
		}

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 3, 0);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		legalnamelabel = new JLabel("LegalName:");
		gridbag.setConstraints(legalnamelabel, c);
		editPane.add(legalnamelabel);

		legalnametext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = layoutindex;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gridbag.setConstraints(legalnametext, c);
		editPane.add(legalnametext);

		layoutindex++;

		buildcommonsui(gridbag, editPane);
	}

	@Override
	public void initdata() {
		if (person != null) {
			legalnametext.setText(person.getLegalName());
			addresstext.setText(person.getAddressLine());
			ziptext.setText(person.getZip());
			countrytext.setText(person.getCountry());
		}
	}

	/**
	 * Returns the Person inputted by user.
	 * 
	 * @return the Person inputted by user.
	 */
	public LegalPerson getPerson() {

		LegalPerson newperson = new LegalPerson(legalnametext.getText().trim(), addresstext.getText().trim(),
				ziptext.getText().trim(), countrytext.getText().trim());
		return newperson;
	}

}
