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
