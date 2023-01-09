/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeTextArea;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;

/**
 * 
 * Listener to recognize if the TITLE or DESCRIPTION field lost their focus to
 * save the entered values into the local user attributes
 * 
 * @author arendd
 *
 */
public class TextAreaSaveListener implements FocusListener {

	public TextAreaSaveListener() {

	}

	@Override
	public void focusGained(FocusEvent event) {

	}

	@Override
	public void focusLost(FocusEvent event) {

		AttributeTextArea field = (AttributeTextArea) event.getComponent();

		if (field.equals(PublicationMainPanel.titleField)) {
			PropertyLoader.setUserValue("TITLE", PublicationMainPanel.titleField.getText());
		}
		if (field.equals(PublicationMainPanel.descriptionField)) {
			PropertyLoader.setUserValue("DESCRIPTION", PublicationMainPanel.descriptionField.getText());
		}

	}

}
