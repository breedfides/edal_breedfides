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