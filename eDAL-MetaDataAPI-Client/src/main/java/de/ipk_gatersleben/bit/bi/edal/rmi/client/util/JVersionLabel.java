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
