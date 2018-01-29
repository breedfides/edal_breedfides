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

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;


/**
 * @author arendd
 */
public class SmallButton extends JButton {

	private static final long serialVersionUID = 8212698968883397292L;

	public SmallButton(String text) {

		super(text);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, PropertyLoader.SMALL_BUTTON_FONT_SIZE));
		this.setMargin(new Insets(0, 5, 0, 5));

	}

}
