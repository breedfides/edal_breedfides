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