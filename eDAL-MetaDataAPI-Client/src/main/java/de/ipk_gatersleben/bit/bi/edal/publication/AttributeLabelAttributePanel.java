/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class AttributeLabelAttributePanel extends JPanel {

	private static final long serialVersionUID = 8109825692298261311L;

	public AttributeLabelAttributePanel(AttributeLabel attributeLabel, JPanel attributePanel, int height) {

		JScrollPane scrollableContentPanel = new JScrollPane(attributePanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableContentPanel.setBorder(BorderFactory.createEmptyBorder());

		JPanel labelPanel = new JPanel(new GridLayout());

		labelPanel.add(attributeLabel);
		labelPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		labelPanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, height));

		this.setMinimumSize(new Dimension(PropertyLoader.MINIMUM_DIM_PUBLICATION_FRAME.width, height));
		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.setLayout(new BorderLayout());
		this.add(labelPanel, BorderLayout.WEST);
		this.add(scrollableContentPanel, BorderLayout.CENTER);

	}

}
