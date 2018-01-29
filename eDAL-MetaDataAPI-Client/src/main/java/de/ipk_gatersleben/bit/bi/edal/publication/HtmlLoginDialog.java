/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

public class HtmlLoginDialog extends JDialog implements HyperlinkListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3443678788942797280L;

	public static enum LOGINS {
		GOOGLE, IPK;
	}

	private JEditorPane pane = new JEditorPane();
	private LOGINS selectedLogin = null;

	public HtmlLoginDialog() {

		pane.setContentType("text/html");
		try {
			pane.setText(PublicationVeloCityCreater.generateHtmlForLoginDialog());
		} catch (EdalException e) {
			e.printStackTrace();
		}
		pane.setEditable(false);
		pane.addHyperlinkListener(this);
		pane.setBorder(BorderFactory.createLineBorder(PropertyLoader.HEADER_FOOTER_COLOR , 10));
		
		this.setTitle("Login to e!DAL");
		this.setModal(true);
		this.add(pane);
		this.setResizable(false);
		this.setMinimumSize(new Dimension(320, 260));
		this.setLocationRelativeTo(null);
		this.pack();

	}

	protected void updateHtml() {

		try {
			pane.setText(PublicationVeloCityCreater.generateHtmlForLoginDialog());
		} catch (EdalException e) {
			e.printStackTrace();
		}

		pane.revalidate();
		pane.repaint();

	}

	public LOGINS getSelectedLoginModule() {
		return this.selectedLogin;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String description = hyperlinkEvent.getDescription().toString();
			this.selectedLogin = LOGINS.valueOf(description);
			this.dispose();
		}
	}
}