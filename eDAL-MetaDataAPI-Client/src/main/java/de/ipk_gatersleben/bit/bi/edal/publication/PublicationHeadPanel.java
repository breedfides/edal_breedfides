/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Class to head panel of the publication tool.
 * 
 * @author arendd
 */
public class PublicationHeadPanel extends JPanel {

	private static final long serialVersionUID = 3713361378618251786L;

	private static PublicationHtmlTextPanel htmlPanel;
	private static PublicationAgreementPanelSwingView agreementPanel;
	private static PublicationCitationHeadPanel citationPanel;

	public PublicationHtmlTextPanel getHtmlPanel() {
		return htmlPanel;
	}

	public PublicationCitationHeadPanel getCitationPanel() {
		return citationPanel;
	}

	public PublicationHeadPanel() {
		PublicationHeadPanel.htmlPanel = new PublicationHtmlTextPanel();
		PublicationHeadPanel.agreementPanel = new PublicationAgreementPanelSwingView();
		PublicationHeadPanel.citationPanel = new PublicationCitationHeadPanel();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(htmlPanel);
		this.add(agreementPanel);
		this.add(citationPanel);
	}
}