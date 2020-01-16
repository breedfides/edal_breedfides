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

import javax.swing.JLabel;
import javax.swing.JToolTip;

public class JLinkLabel extends JLabel{
	private static final long serialVersionUID = 1L;
	
	public JLinkLabel(String text, int horizontalAlignment) {
		super(text,horizontalAlignment);
	}
	@Override
	public JToolTip createToolTip() {
		JToolTip tip = new HyperLinkToolTip();
		tip.setComponent(this);
		return tip;
	}
}
