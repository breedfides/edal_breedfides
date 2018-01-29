/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeTextArea;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;

/**
 * Listener to automatically remove the default values in text fields and
 * furthermore to trim
 * 
 * @author arendd
 *
 */
public class TextAreaValueChangedFocusListener implements FocusListener {

	private AttributeLabel label;
	private AttributeTextArea textArea;
	private String defaultString;

	public TextAreaValueChangedFocusListener(AttributeLabel label, AttributeTextArea titleArea, String defaultString) {
		this.label = label;
		this.textArea = titleArea;
		this.defaultString = defaultString;
	}

	@Override
	public void focusGained(FocusEvent event) {

		this.label.setForeground(PropertyLoader.LABEL_COLOR);
		if (this.textArea.getText().trim().equalsIgnoreCase(this.defaultString.trim())) {
			this.textArea.setText(new String());
		}

	}

	@Override
	public void focusLost(FocusEvent event) {

		if (Utils.checkIfStringIsEmpty(this.textArea.getText())) {
			this.textArea.setText(this.defaultString);
		} else {
			this.textArea.setText(this.textArea.getText().trim());
		}

		PublicationFrame.updateUI();
	}
}