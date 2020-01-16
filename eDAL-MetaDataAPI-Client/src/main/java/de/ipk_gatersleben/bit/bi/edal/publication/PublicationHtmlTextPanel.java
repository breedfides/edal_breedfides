/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Class to show a html page in a panel.
 * 
 * @author arendd
 */
public class PublicationHtmlTextPanel extends JPanel implements HyperlinkListener {

	private static final long serialVersionUID = 3713361378618251786L;

	private JEditorPane pane = new JEditorPane();

	public PublicationHtmlTextPanel() {

		pane.setContentType("text/html");

		try {
			pane.setText(PublicationVeloCityCreater.generateHtmlForHeadPage());
		} catch (EdalException e) {
			e.printStackTrace();
		}

		pane.setEditable(false);
		pane.addHyperlinkListener(this);
		pane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(pane);

	}

	protected void updateHtml() {

		try {
			pane.setText(PublicationVeloCityCreater.generateHtmlForHeadPage());
		} catch (EdalException e) {
			e.printStackTrace();
		}

		pane.revalidate();
		pane.repaint();

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String url = hyperlinkEvent.getURL().toString();
			EdalHelpers.openURL(url);
		}
	}
}