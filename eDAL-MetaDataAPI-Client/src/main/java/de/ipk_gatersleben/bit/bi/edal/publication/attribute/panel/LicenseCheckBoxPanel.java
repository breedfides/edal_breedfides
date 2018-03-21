/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationVeloCityCreater;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class LicenseCheckBoxPanel extends JPanel implements HyperlinkListener, ItemListener {

	private static final long serialVersionUID = -5576026891561912008L;

	private static AttributeLabel LICENSE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("LICENSE_LABEL"),
			PropertyLoader.props.getProperty("LICENSE_TOOLTIP"));

	private JCheckBox licenceCheckBox1 = new JCheckBox();
	private JCheckBox licenceCheckBox2 = new JCheckBox();
	private JCheckBox licenceCheckBox3 = new JCheckBox();
	private JCheckBox licenceCheckBox4 = new JCheckBox();
	private JCheckBox licenceCheckBox5 = new JCheckBox();
	private JCheckBox licenceCheckBox6 = new JCheckBox();
	private JCheckBox licenceCheckBox7 = new JCheckBox();
	private final JLabel licenseLabel1 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(0));
	private final JLabel licenseLabel2 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(1));
	private final JLabel licenseLabel3 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(2));
	private final JLabel licenseLabel4 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(3));
	private final JLabel licenseLabel5 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(4));
	private final JLabel licenseLabel6 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(5));
	private final JLabel licenseLabel7 = new JLabel(PropertyLoader.SUPPORTED_LICENSES.get(6));

	LayoutManager checkBoxPanelLayout = new FlowLayout(FlowLayout.LEADING, 1, 1);

	JPanel checkBoxPanel1 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel2 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel3 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel4 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel5 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel6 = new JPanel(checkBoxPanelLayout);
	JPanel checkBoxPanel7 = new JPanel(checkBoxPanelLayout);

	private JEditorPane licenseHtmlPane1, licenseHtmlPane2, licenseHtmlPane3, licenseHtmlPane4, licenseHtmlPane5,
			licenseHtmlPane6, licenseHtmlPane7;

	public LicenseCheckBoxPanel() {

		this.checkBoxPanel1.add(licenceCheckBox1);
		this.checkBoxPanel1.add(licenseLabel1);
		this.checkBoxPanel1.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel2.add(licenceCheckBox2);
		this.checkBoxPanel2.add(licenseLabel2);
		this.checkBoxPanel2.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel3.add(licenceCheckBox3);
		this.checkBoxPanel3.add(licenseLabel3);
		this.checkBoxPanel3.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel4.add(licenceCheckBox4);
		this.checkBoxPanel4.add(licenseLabel4);
		this.checkBoxPanel4.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel5.add(licenceCheckBox5);
		this.checkBoxPanel5.add(licenseLabel5);
		this.checkBoxPanel5.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel6.add(licenceCheckBox6);
		this.checkBoxPanel6.add(licenseLabel6);
		this.checkBoxPanel6.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.checkBoxPanel7.add(licenceCheckBox7);
		this.checkBoxPanel7.add(licenseLabel7);
		this.checkBoxPanel7.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		try {
			this.licenseHtmlPane1 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(0),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(0)));
			this.licenseHtmlPane2 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(1),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(1)));
			this.licenseHtmlPane3 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(2),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(2)));
			this.licenseHtmlPane4 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(3),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(3)));
			this.licenseHtmlPane5 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(4),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(4)));
			this.licenseHtmlPane6 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(5),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(5)));
			this.licenseHtmlPane7 = new JEditorPane("text/html",
					PublicationVeloCityCreater.generateHtmlForLicense(
							PropertyLoader.SUPPORTED_LICENSES_LEGAL_CODE_URL.get(6),
							PropertyLoader.SUPPORTED_LICENSES_HUMAN_READABLE_URL.get(6)));

		} catch (EdalException e) {
			e.printStackTrace();
		}

		GridBagLayout gridBagLayout = new GridBagLayout();

		final JPanel attributeContentPanel = new JPanel(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();

		attributeContentPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributeContentPanel.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.LICENSE_PANEL_HEIGHT));

		this.licenseHtmlPane1.setEditable(false);
		this.licenseHtmlPane1.addHyperlinkListener(this);
		this.licenseHtmlPane2.setEditable(false);
		this.licenseHtmlPane2.addHyperlinkListener(this);
		this.licenseHtmlPane3.setEditable(false);
		this.licenseHtmlPane3.addHyperlinkListener(this);
		this.licenseHtmlPane4.setEditable(false);
		this.licenseHtmlPane4.addHyperlinkListener(this);
		this.licenseHtmlPane5.setEditable(false);
		this.licenseHtmlPane5.addHyperlinkListener(this);
		this.licenseHtmlPane6.setEditable(false);
		this.licenseHtmlPane7.addHyperlinkListener(this);
		this.licenseHtmlPane7.setEditable(false);
		this.licenseHtmlPane7.addHyperlinkListener(this);

		this.licenceCheckBox1.addItemListener(this);
		this.licenceCheckBox2.addItemListener(this);
		this.licenceCheckBox3.addItemListener(this);
		this.licenceCheckBox4.addItemListener(this);
		this.licenceCheckBox5.addItemListener(this);
		this.licenceCheckBox6.addItemListener(this);
		this.licenceCheckBox7.addItemListener(this);

		this.licenceCheckBox1.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox2.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox3.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox4.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox5.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox6.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenceCheckBox7.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.licenseLabel1.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
		this.licenseLabel1.setFont(PropertyLoader.OPEN_ACCESS_FONT);
		this.licenseLabel2.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
		this.licenseLabel2.setFont(PropertyLoader.OPEN_ACCESS_FONT);
		this.licenseLabel3.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
		this.licenseLabel3.setFont(PropertyLoader.OPEN_ACCESS_FONT);
		this.licenseLabel4.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseLabel5.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseLabel6.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseLabel7.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseLabel1.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel2.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel3.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel4.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel5.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel6.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel7.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.licenseLabel1.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel2.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel3.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel4.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel5.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel6.setFont(PropertyLoader.DEFAULT_FONT);
		this.licenseLabel7.setFont(PropertyLoader.DEFAULT_FONT);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.ipady = -4;
		c.ipadx = -4;
		c.gridx = 0;
		c.gridy = 0;
		gridBagLayout.setConstraints(this.checkBoxPanel1, c);
		attributeContentPanel.add(this.checkBoxPanel1);
		c.gridx = 1;
		c.gridy = 0;
		gridBagLayout.setConstraints(this.licenseHtmlPane1, c);
		attributeContentPanel.add(this.licenseHtmlPane1, c);
		c.gridx = 0;
		c.gridy = 1;
		gridBagLayout.setConstraints(this.checkBoxPanel2, c);
		attributeContentPanel.add(this.checkBoxPanel2);
		c.gridx = 1;
		c.gridy = 1;
		gridBagLayout.setConstraints(this.licenseHtmlPane2, c);
		attributeContentPanel.add(this.licenseHtmlPane2);
		c.gridx = 0;
		c.gridy = 2;
		gridBagLayout.setConstraints(this.checkBoxPanel3, c);
		attributeContentPanel.add(this.checkBoxPanel3);
		c.gridx = 1;
		c.gridy = 2;
		gridBagLayout.setConstraints(this.licenseHtmlPane3, c);
		attributeContentPanel.add(this.licenseHtmlPane3);
		c.gridx = 0;
		c.gridy = 3;
		gridBagLayout.setConstraints(this.checkBoxPanel4, c);
		attributeContentPanel.add(this.checkBoxPanel4);
		c.gridx = 1;
		c.gridy = 3;
		gridBagLayout.setConstraints(this.licenseHtmlPane4, c);
		attributeContentPanel.add(this.licenseHtmlPane4);
		c.gridx = 0;
		c.gridy = 4;
		gridBagLayout.setConstraints(this.checkBoxPanel5, c);
		attributeContentPanel.add(this.checkBoxPanel5);
		c.gridx = 1;
		c.gridy = 4;
		gridBagLayout.setConstraints(this.licenseHtmlPane5, c);
		attributeContentPanel.add(this.licenseHtmlPane5);
		c.gridx = 0;
		c.gridy = 5;
		gridBagLayout.setConstraints(this.checkBoxPanel6, c);
		attributeContentPanel.add(this.checkBoxPanel6);
		c.gridx = 1;
		c.gridy = 5;
		gridBagLayout.setConstraints(this.licenseHtmlPane6, c);
		attributeContentPanel.add(this.licenseHtmlPane6);
		c.gridx = 0;
		c.gridy = 6;
		gridBagLayout.setConstraints(this.checkBoxPanel7, c);
		attributeContentPanel.add(this.checkBoxPanel7);
		c.gridx = 1;
		c.gridy = 6;
		gridBagLayout.setConstraints(this.licenseHtmlPane7, c);
		attributeContentPanel.add(this.licenseHtmlPane7);

		final JPanel attributeLabelPanel = new JPanel(new GridLayout());

		attributeLabelPanel.add(LicenseCheckBoxPanel.LICENSE_LABEL);
		attributeLabelPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributeLabelPanel.setPreferredSize(
				new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.LICENSE_PANEL_HEIGHT));

		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.LICENSE_PANEL_HEIGHT));

		this.setLayout(new BorderLayout());
		this.add(attributeContentPanel, BorderLayout.CENTER);

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String url = hyperlinkEvent.getURL().toString();
			EdalHelpers.openURL(url);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {

		PropertyLoader.LICENSE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PublicationFrame.updateUI();

		if (event.getSource().equals(this.licenceCheckBox1)) {
			if (this.licenceCheckBox1.isSelected()) {
				blockLicence2();
				blockLicence3();
				blockLicence4();
				blockLicence5();
				blockLicence6();
				blockLicence7();
			} else {
				releaseLicence2();
				releaseLicence3();
				releaseLicence4();
				releaseLicence5();
				releaseLicence6();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox2)) {
			if (this.licenceCheckBox2.isSelected()) {
				blockLicence1();
				blockLicence3();
				blockLicence4();
				blockLicence5();
				blockLicence6();
				blockLicence7();
			} else {
				releaseLicence1();
				releaseLicence3();
				releaseLicence4();
				releaseLicence5();
				releaseLicence6();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox3)) {
			if (this.licenceCheckBox3.isSelected()) {
				blockLicence1();
				blockLicence2();
				blockLicence4();
				blockLicence5();
				blockLicence6();
				blockLicence7();
			} else {
				releaseLicence1();
				releaseLicence2();
				releaseLicence4();
				releaseLicence5();
				releaseLicence6();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox4)) {
			if (this.licenceCheckBox4.isSelected()) {
				blockLicence1();
				blockLicence2();
				blockLicence3();
				blockLicence5();
				blockLicence6();
				blockLicence7();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING"),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING_TITLE"),
								JOptionPane.WARNING_MESSAGE);
					}
				});

			} else {
				releaseLicence1();
				releaseLicence2();
				releaseLicence3();
				releaseLicence5();
				releaseLicence6();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox5)) {
			if (this.licenceCheckBox5.isSelected()) {
				blockLicence1();
				blockLicence2();
				blockLicence3();
				blockLicence4();
				blockLicence6();
				blockLicence7();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING"),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING_TITLE"),
								JOptionPane.WARNING_MESSAGE);
					}
				});
			} else {
				releaseLicence1();
				releaseLicence2();
				releaseLicence3();
				releaseLicence4();
				releaseLicence6();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox6)) {
			if (this.licenceCheckBox6.isSelected()) {
				blockLicence1();
				blockLicence2();
				blockLicence3();
				blockLicence4();
				blockLicence5();
				blockLicence7();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING"),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING_TITLE"),
								JOptionPane.WARNING_MESSAGE);
					}
				});
			} else {
				releaseLicence1();
				releaseLicence2();
				releaseLicence3();
				releaseLicence4();
				releaseLicence5();
				releaseLicence7();
			}
		}
		if (event.getSource().equals(this.licenceCheckBox7)) {
			if (this.licenceCheckBox7.isSelected()) {
				blockLicence1();
				blockLicence2();
				blockLicence3();
				blockLicence4();
				blockLicence5();
				blockLicence6();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING"),
								PropertyLoader.props.getProperty("NON_OPEN_ACCESS_WARNING_TITLE"),
								JOptionPane.WARNING_MESSAGE);
					}
				});
			} else {
				releaseLicence1();
				releaseLicence2();
				releaseLicence3();
				releaseLicence4();
				releaseLicence5();
				releaseLicence6();
			}
		}

	}

	private void blockLicence1() {
		this.licenceCheckBox1.setSelected(false);
		this.licenceCheckBox1.setEnabled(false);
		this.licenseLabel1.setEnabled(false);
		this.licenseHtmlPane1.setEnabled(false);
	}

	private void blockLicence2() {
		this.licenceCheckBox2.setSelected(false);
		this.licenceCheckBox2.setEnabled(false);
		this.licenseLabel2.setEnabled(false);
		this.licenseHtmlPane2.setEnabled(false);
	}

	private void blockLicence3() {
		this.licenceCheckBox3.setSelected(false);
		this.licenceCheckBox3.setEnabled(false);
		this.licenseLabel3.setEnabled(false);
		this.licenseHtmlPane3.setEnabled(false);
	}

	private void blockLicence4() {
		this.licenceCheckBox4.setSelected(false);
		this.licenceCheckBox4.setEnabled(false);
		this.licenseLabel4.setEnabled(false);
		this.licenseHtmlPane4.setEnabled(false);
	}

	private void blockLicence5() {
		this.licenceCheckBox5.setSelected(false);
		this.licenceCheckBox5.setEnabled(false);
		this.licenseLabel5.setEnabled(false);
		this.licenseHtmlPane5.setEnabled(false);
	}

	private void blockLicence6() {
		this.licenceCheckBox6.setSelected(false);
		this.licenceCheckBox6.setEnabled(false);
		this.licenseLabel6.setEnabled(false);
		this.licenseHtmlPane6.setEnabled(false);
	}

	private void blockLicence7() {
		this.licenceCheckBox7.setSelected(false);
		this.licenceCheckBox7.setEnabled(false);
		this.licenseLabel7.setEnabled(false);
		this.licenseHtmlPane7.setEnabled(false);
	}

	private void releaseLicence1() {
		this.licenceCheckBox1.setSelected(false);
		this.licenceCheckBox1.setEnabled(true);
		this.licenseLabel1.setEnabled(true);
		this.licenseHtmlPane1.setEnabled(true);
	}

	private void releaseLicence2() {
		this.licenceCheckBox2.setSelected(false);
		this.licenceCheckBox2.setEnabled(true);
		this.licenseLabel2.setEnabled(true);
		this.licenseHtmlPane2.setEnabled(true);
	}

	private void releaseLicence3() {
		this.licenceCheckBox3.setSelected(false);
		this.licenceCheckBox3.setEnabled(true);
		this.licenseLabel3.setEnabled(true);
		this.licenseHtmlPane3.setEnabled(true);
	}

	private void releaseLicence4() {
		this.licenceCheckBox4.setSelected(false);
		this.licenceCheckBox4.setEnabled(true);
		this.licenseLabel4.setEnabled(true);
		this.licenseHtmlPane4.setEnabled(true);
	}

	private void releaseLicence5() {
		this.licenceCheckBox5.setSelected(false);
		this.licenceCheckBox5.setEnabled(true);
		this.licenseLabel5.setEnabled(true);
		this.licenseHtmlPane5.setEnabled(true);
	}

	private void releaseLicence6() {
		this.licenceCheckBox6.setSelected(false);
		this.licenceCheckBox6.setEnabled(true);
		this.licenseLabel6.setEnabled(true);
		this.licenseHtmlPane6.setEnabled(true);
	}

	private void releaseLicence7() {
		this.licenceCheckBox7.setSelected(false);
		this.licenceCheckBox7.setEnabled(true);
		this.licenseLabel7.setEnabled(true);
		this.licenseHtmlPane7.setEnabled(true);
	}

	private void enableLicence1() {
		this.licenceCheckBox1.setEnabled(true);
		this.licenceCheckBox1.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane1.setEnabled(true);
		this.licenseLabel1.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
	}

	private void enableLicence2() {
		this.licenceCheckBox2.setEnabled(true);
		this.licenceCheckBox2.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane2.setEnabled(true);
		this.licenseLabel2.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
	}

	private void enableLicence3() {
		this.licenceCheckBox3.setEnabled(true);
		this.licenceCheckBox3.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane3.setEnabled(true);
		this.licenseLabel3.setForeground(PropertyLoader.OPEN_ACCESS_COLOR);
	}

	private void enableLicence4() {
		this.licenceCheckBox4.setEnabled(true);
		this.licenceCheckBox4.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane4.setEnabled(true);
		this.licenseLabel4.setForeground(PropertyLoader.MAIN_FONT_COLOR);
	}

	private void enableLicence5() {
		this.licenceCheckBox5.setEnabled(true);
		this.licenceCheckBox5.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane5.setEnabled(true);
		this.licenseLabel5.setForeground(PropertyLoader.MAIN_FONT_COLOR);
	}

	private void enableLicence6() {
		this.licenceCheckBox6.setEnabled(true);
		this.licenceCheckBox6.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane6.setEnabled(true);
		this.licenseLabel6.setForeground(PropertyLoader.MAIN_FONT_COLOR);
	}

	private void enableLicence7() {
		this.licenceCheckBox7.setEnabled(true);
		this.licenceCheckBox7.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.licenseHtmlPane7.setEnabled(true);
		this.licenseLabel7.setForeground(PropertyLoader.MAIN_FONT_COLOR);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			if (this.licenceCheckBox1.isSelected()) {
				enableLicence1();
			} else if (this.licenceCheckBox2.isSelected()) {
				enableLicence2();
			} else if (this.licenceCheckBox3.isSelected()) {
				enableLicence3();
			} else if (this.licenceCheckBox4.isSelected()) {
				enableLicence4();
			} else if (this.licenceCheckBox5.isSelected()) {
				enableLicence5();
			} else if (this.licenceCheckBox6.isSelected()) {
				enableLicence6();
			} else if (this.licenceCheckBox7.isSelected()) {
				enableLicence7();
			} else {
				enableLicence1();
				enableLicence2();
				enableLicence3();
				enableLicence4();
				enableLicence5();
				enableLicence6();
				enableLicence7();
			}

		} else {

			this.licenceCheckBox1.setEnabled(false);
			this.licenceCheckBox1.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel1.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox2.setEnabled(false);
			this.licenceCheckBox2.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel2.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox3.setEnabled(false);
			this.licenceCheckBox3.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel3.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox4.setEnabled(false);
			this.licenceCheckBox4.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel4.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox5.setEnabled(false);
			this.licenceCheckBox5.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel5.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox6.setEnabled(false);
			this.licenceCheckBox6.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel6.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenceCheckBox7.setEnabled(false);
			this.licenceCheckBox7.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
			this.licenseLabel7.setForeground(PropertyLoader.DISABLED_FONT_COLOR);

			this.licenseHtmlPane1.setEnabled(false);
			this.licenseHtmlPane2.setEnabled(false);
			this.licenseHtmlPane3.setEnabled(false);
			this.licenseHtmlPane4.setEnabled(false);
			this.licenseHtmlPane5.setEnabled(false);
			this.licenseHtmlPane6.setEnabled(false);
			this.licenseHtmlPane7.setEnabled(false);

		}
	}

	public boolean isLicenseSelected() {

		if (!this.licenceCheckBox1.isSelected() && !this.licenceCheckBox2.isSelected()
				&& !this.licenceCheckBox3.isSelected() && !this.licenceCheckBox4.isSelected()
				&& !this.licenceCheckBox5.isSelected() && !this.licenceCheckBox6.isSelected()
				&& !this.licenceCheckBox6.isSelected()) {
			return false;
		} else {
			return true;
		}
	}

	public String getLicense() {
		if (this.licenceCheckBox1.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(0);
		} else if (this.licenceCheckBox2.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(1);
		} else if (this.licenceCheckBox3.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(2);
		} else if (this.licenceCheckBox4.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(3);
		} else if (this.licenceCheckBox5.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(4);
		} else if (this.licenceCheckBox6.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(5);
		} else if (this.licenceCheckBox7.isSelected()) {
			return PropertyLoader.SUPPORTED_LICENSES.get(6);
		}
		return null;

	}
}