/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class XStatusSeparator extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5230559281277224588L;
	private ImageIcon imageIcon;

	public XStatusSeparator() {
		imageIcon = ImageUtil.createImageIcon("statusbar_separator.png", "");
		init();
	}

	private void init() {
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		setOpaque(false);
		setIcon(imageIcon);
	}
}
