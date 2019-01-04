/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;

public class TextAreaCheckFocusListener implements FocusListener {

	private AttributeLabel label;
	private JTextComponent textArea;
	private String defaultString;

	public TextAreaCheckFocusListener(AttributeLabel label,
			JTextComponent titleArea, String defaultString) {
		this.label = label;
		this.textArea = titleArea;
		this.defaultString = defaultString;
	}

	@Override
	public void focusGained(FocusEvent e) {
		label.setForeground(PropertyLoader.LABEL_COLOR);

//		if (MyUtils.cleanUpString(this.textArea.getText()).equalsIgnoreCase(
//				MyUtils.cleanUpString(this.defaultString))) {
//			this.textArea.setText("");
//		}
	}

	@Override
	public void focusLost(FocusEvent e) {

		if (Utils.checkIfStringIsEmpty(this.textArea.getText())) {
			this.textArea.setText(this.defaultString);
		}
	}
}
