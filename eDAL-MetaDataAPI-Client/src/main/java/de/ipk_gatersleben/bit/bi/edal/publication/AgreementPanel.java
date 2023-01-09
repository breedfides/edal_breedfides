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

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class AgreementPanel extends JDialog implements HyperlinkListener {

	private static final long serialVersionUID = 3378495414715289398L;

	private JEditorPane pane = new JEditorPane();

	public AgreementPanel() {
		pane.setContentType("text/html");

		try {
			pane.setText(PublicationVeloCityCreater.generateHtmlForAgreement());
		} catch (EdalException e) {
			e.printStackTrace();
		}
		pane.setEditable(false);
		pane.addHyperlinkListener(this);
		pane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, PropertyLoader.HEADER_FOOTER_COLOR));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(pane);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});

		// scrollPane.getVerticalScrollBar().addAdjustmentListener(new
		// AdjustmentListener() {
		//
		// @Override
		// public void adjustmentValueChanged(AdjustmentEvent event) {
		// JScrollBar scrollBar = (JScrollBar) event.getAdjustable();
		// int extent = scrollBar.getModel().getExtent();
		// // System.out.println("1. Value: " + (scrollBar.getValue() +
		// // extent) + " Max: " + scrollBar.getMaximum());
		// if ((scrollBar.getValue() + extent) == scrollBar.getMaximum()) {
		// checkbox.setEnabled(true);
		// }
		//
		// }
		// });

		mainPanel.add(scrollPane);

		this.setContentPane(mainPanel);
		this.setPreferredSize(new Dimension(1024, 600));
		this.setTitle(
				"Deposition and License Agreement (DLA) for the Plant Genomics and Phenomics Research Data Repository (PGP)");
		this.pack();
		this.setLocationRelativeTo(PublicationFrame.getMainPanel());

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String url = hyperlinkEvent.getURL().toString();
			EdalHelpers.openURL(url);
		}
	}

	public String getContentHash() {
		return String.valueOf(this.pane.getText().hashCode());
	}

}
