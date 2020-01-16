/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;

@SuppressWarnings("unchecked")
public class ResourcePanel extends JPanel implements ActionListener {

    @SuppressWarnings("rawtypes")
    private static class CustomComboBox extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 9120777908811491201L;

	private final static Dimension preferredSize = new Dimension(250, 14);
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	public Component getListCellRendererComponent(final JList list,
		final Object value, final int index, final boolean isSelected,
		final boolean cellHasFocus) {
	    final JLabel renderer = (JLabel) this.defaultRenderer
		    .getListCellRendererComponent(list, value, index,
			    isSelected, cellHasFocus);
	    if (value instanceof Color) {
		renderer.setBackground((Color) value);
	    }
	    renderer.setPreferredSize(CustomComboBox.preferredSize);
	    return renderer;
	}
    }

    private static final long serialVersionUID = 8109825692298261311L;

    private static AttributeLabel RESOURCE_LABEL = new AttributeLabel(
	    PropertyLoader.props.getProperty("RESOURCE_LABEL"),
	    PropertyLoader.props.getProperty("RESOURCE_TOOLTIP"));

    private static SmallButton OKAY_BUTTON = new SmallButton("OK");

    private static JComboBox<String> comboBox;

    static {

	final String[] strings = PropertyLoader.RESOURCE_TYPES
		.toArray(new String[PropertyLoader.RESOURCE_TYPES.size()]);
	ResourcePanel.comboBox = new JComboBox<String>(strings);

	ResourcePanel.comboBox.setRenderer(new CustomComboBox());

	ResourcePanel.comboBox.setBorder(BorderFactory.createEmptyBorder());

	ResourcePanel.comboBox
		.setSelectedItem(PropertyLoader.loadResourceString());
    }

    public ResourcePanel() {

	final JPanel mainPanel = new JPanel(
		new FlowLayout(FlowLayout.LEFT, 10, 0));

	mainPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
	mainPanel.setPreferredSize(
		new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH,
			PropertyLoader.TWO_LINE_HEIGHT));

	final EmptyBorder inBorder = new EmptyBorder(1, 2, 0, 2);
	final EmptyBorder outBorder = new EmptyBorder(1, 2, 0, 2);
	mainPanel.setBorder(
		BorderFactory.createCompoundBorder(outBorder, inBorder));

	ResourcePanel.comboBox
		.setSelectedItem(PublicationMainPanel.DEFAULT_RESOURCE_STRING);

	ResourcePanel.OKAY_BUTTON.addActionListener(this);

	mainPanel.add(ResourcePanel.comboBox);
	mainPanel.add(ResourcePanel.OKAY_BUTTON);

	final JPanel attributePanel = new JPanel(new GridLayout());

	attributePanel.add(ResourcePanel.RESOURCE_LABEL);
	attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
	attributePanel.setPreferredSize(
		new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH,
			PropertyLoader.TWO_LINE_HEIGHT));

	this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
	this.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));
	this.setPreferredSize(
		new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH,
			PropertyLoader.TWO_LINE_HEIGHT));

	this.setLayout(new BorderLayout());
	this.add(attributePanel, BorderLayout.WEST);
	this.add(mainPanel, BorderLayout.CENTER);

    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {

	if (actionEvent.getSource().equals(ResourcePanel.OKAY_BUTTON)) {

	    PublicationMainPanel.resourceField.setText(this.getTableContent());

	    PublicationMainPanel.releaseAllBlockedFields();

	    PublicationMainPanel.languageResourcePanel
		    .remove(((BorderLayout) PublicationMainPanel.languageResourcePanel
			    .getLayout())
				    .getLayoutComponent(BorderLayout.SOUTH));

	    PropertyLoader.RESOURCE_LABEL
		    .setForeground(PropertyLoader.LABEL_COLOR);

	    final AttributeLableAttributeTextAreaPanel newLicensePanel = new AttributeLableAttributeTextAreaPanel(
		    PropertyLoader.RESOURCE_LABEL,
		    PublicationMainPanel.resourceField,
		    PropertyLoader.TWO_LINE_HEIGHT);

	    newLicensePanel.setBorder(
		    BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

	    PublicationMainPanel.languageResourcePanel
		    .add(newLicensePanel, BorderLayout.SOUTH);

	    PublicationFrame.updateUI();
	    // PublicationModul.getFrame().requestFocusInWindow();
	    PublicationButtonLinePanel.getNextButton().requestFocus();

	    this.saveUserValues();
	}
    }

    public String getResourceType() {
	return (String) ResourcePanel.comboBox.getSelectedItem();
    }

    private String getTableContent() {
	return ResourcePanel.comboBox.getSelectedItem().toString();
    }

    private void saveUserValues() {
	PropertyLoader.setUserValue("RESOURCE_TYPE",
		ResourcePanel.comboBox.getSelectedItem().toString());
    }
}