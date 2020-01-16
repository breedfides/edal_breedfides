/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;


import java.awt.Cursor;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JToolTip;

public class JVersionLabel extends JButton{
	private static final long serialVersionUID = 1L;
	private String plaintext;
	private Cursor defautcursor;

	public JVersionLabel(String text) {
		super(text);
		this.plaintext = text;
		defautcursor = this.getCursor();
		/*
		UIDefaults jbuttonDefaults = new UIDefaults();
	    this.putClientProperty("Nimbus.Overrides",jbuttonDefaults);
	    this.putClientProperty("Nimbus.Overrides.InheritDefaults",false);
	    */
		setBorderPainted(true);
		setSelect(false);
		setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
        setOpaque(false);
	}
	
	@Override
	public JToolTip createToolTip() {
		JToolTip tip = new HyperLinkToolTip();
		tip.setComponent(this);
		return tip;
	}
	
	public void setSelect(boolean isselected)
	{
		if(isselected)
		{
			this.setText(plaintext);
			this.setCursor(defautcursor);
		}
		else
		{
			this.setText("<html><a href='#'>"+plaintext+"</a></html>");
			this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		}
	}
	
}
