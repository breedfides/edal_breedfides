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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class AttributeLableAttributeTextAreaPanel extends JPanel {

	private static final long serialVersionUID = 8109825692298261311L;

	public AttributeLableAttributeTextAreaPanel(AttributeLabel attributeLabel, AttributeTextArea attributeTextArea,
			int height) {

		JScrollPane scrollableContentPanel = new JScrollPane(attributeTextArea,
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
