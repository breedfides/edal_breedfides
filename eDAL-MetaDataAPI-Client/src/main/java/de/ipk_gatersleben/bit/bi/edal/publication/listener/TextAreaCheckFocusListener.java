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
