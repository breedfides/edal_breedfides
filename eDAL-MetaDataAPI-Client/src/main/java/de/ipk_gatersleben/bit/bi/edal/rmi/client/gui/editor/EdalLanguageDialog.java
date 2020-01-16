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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;

/**
 * The <code>EdalLanguageDialog</code> can be used to edit
 * <code>EdalLanguage</code>, which implements the
 * <code>MetadataeditDialog</code> class, we can use it with a couple of lines
 * of code:
 * 
 * <pre>
 * EdalLanguageDialog languageDialog = new EdalLanguageDialog(language);
 * languageDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalLanguageDialog extends MetaDataEditDialog {
	private static final long serialVersionUID = 1L;
	private JLabel languagelabel;
	private JComboBox<String> languagecomboBox;
	private EdalLanguage language;
	private Locale[] availableLocales;

	public EdalLanguageDialog(EdalLanguage language, String title) {
		super();
		this.language = language;
		setTitle(title);
		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		final JPanel editPane = new JPanel();
		final GridBagLayout gridbag = new GridBagLayout();
		editPane.setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();

		languagelabel = new JLabel("Language:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(languagelabel, c);
		editPane.add(languagelabel);

		availableLocales = Locale.getAvailableLocales();
		List<String> languagelist = new ArrayList<String>();
		List<String> displaylanguagelist = new ArrayList<String>();
		for (Locale locale : availableLocales) {
			languagelist.add(locale.getLanguage());
			displaylanguagelist.add(locale.getDisplayName());
		}

		final String languagelabels[] = displaylanguagelist
				.toArray(new String[0]);
		final DefaultComboBoxModel<String> languagemodel = new DefaultComboBoxModel<String>(
				languagelabels);
		languagecomboBox = new JComboBox<String>(languagemodel);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 1.0;
		gridbag.setConstraints(languagecomboBox, c);
		editPane.add(languagecomboBox);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(280, (int) (280 * 0.618)));

		initdata();
	}

	@Override
	public void initdata() {
		if (language != null) {
			for (int i = 0; i < availableLocales.length; i++) {
				if (availableLocales[i] == (language.getLanguage())) {
					languagecomboBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	/**
	 * Returns the EdalLanguage inputted by user.
	 * 
	 * @return the EdalLanguage inputted by user.
	 */
	public EdalLanguage getLanguage() {
		return new EdalLanguage(
				availableLocales[languagecomboBox.getSelectedIndex()]);
	}

}
