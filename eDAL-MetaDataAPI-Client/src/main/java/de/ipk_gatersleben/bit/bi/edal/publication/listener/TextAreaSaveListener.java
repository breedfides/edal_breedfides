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
