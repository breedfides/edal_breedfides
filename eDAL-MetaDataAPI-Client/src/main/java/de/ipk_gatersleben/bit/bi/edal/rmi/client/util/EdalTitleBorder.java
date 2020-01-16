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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

public class EdalTitleBorder implements Border {

	public Insets getBorderInsets(Component c) {
		return new Insets(2, 2, 4, 3);
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(new Color(238,238,238));
		g.fill3DRect(0, 0, width, height, true);
	}

}