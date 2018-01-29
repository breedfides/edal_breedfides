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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Class to define the layout of the {@link AttributeLabel}s on the left side of
 * the frame.
 * 
 * @author arendd
 */
public class AttributeLabel extends JLabel {

	private static final long serialVersionUID = 602825678934024556L;

	public AttributeLabel(String label, String tooltip) {

		super(label);

		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, PropertyLoader.ATTRIBUTE_LABEL_FONT_SIZE));
		this.setForeground(PropertyLoader.LABEL_COLOR);
		this.setOpaque(true);
		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		if (label.equals(PropertyLoader.props.getProperty("LICENSE_LABEL"))) {

			this.setCursor(new Cursor(Cursor.HAND_CURSOR));

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {

					String url = "http://creativecommons.org/licenses/";

					Utils.openURL(url);
				}
			});
		}
		EmptyBorder inBorder = new EmptyBorder(2, 5, 0, 0);
		EmptyBorder outBorder = new EmptyBorder(0, 0, 0, 0);
		this.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		this.setToolTipText(tooltip);
	}
}
