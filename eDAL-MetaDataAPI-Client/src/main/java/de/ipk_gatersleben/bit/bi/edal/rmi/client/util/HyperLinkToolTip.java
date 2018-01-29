/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ToolTipUI;

public class HyperLinkToolTip extends JToolTip{
	private static final long serialVersionUID = 1L;
	private JEditorPane theEditorPane;
 
	public HyperLinkToolTip() {
		setLayout(new BorderLayout());
		LookAndFeel.installBorder(this, "ToolTip.border");
		LookAndFeel.installColors(this, "ToolTip.background", "ToolTip.foreground");
		theEditorPane = new JEditorPane();
		theEditorPane.setContentType("text/html");
		theEditorPane.setEditable(false);
		/*
		int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
		ToolTipManager.sharedInstance().setDismissDelay(dismissDelay*2);
		*/
		theEditorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					openWebpage(e.getURL());
				}
			}
		});
		add(theEditorPane);
	}
 
	public void setTipText(String tipText) {
		theEditorPane.setText(tipText);
	}
	
	private void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}

 
	public void updateUI() {
		setUI(new ToolTipUI() {});
	}
 
}
