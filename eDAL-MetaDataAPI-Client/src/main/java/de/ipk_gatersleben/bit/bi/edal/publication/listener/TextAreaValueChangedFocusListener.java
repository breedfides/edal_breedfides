/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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