/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class XStatusLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7780350176508210689L;

	public XStatusLabel() {
		this(null, null);
	}

	public XStatusLabel(String text) {
		this(text, null);
	}

	public XStatusLabel(Icon icon) {
		this(null, icon);
	}

	public XStatusLabel(String text, Icon icon) {
		super(text, icon, 10);
		init();
	}

	protected void init() {
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		setFont(ImageUtil.FONT_12_BOLD);
		setForeground(ImageUtil.DEFAULT_TEXT_COLOR);
		setVerticalAlignment(0);
		setVerticalTextPosition(0);
		setIconTextGap(5);
	}
}