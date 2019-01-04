/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *  This is a abstract class. 
 *  To create a concrete <code>MetadataDialog</code> as a subclass,you need only provide implementations 
 *  for the following two methods: *
 *  <pre>
 *  public JPanel createbuttonpanel();
 *  public void initdata();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */

public abstract class MetadataDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;
	
	public int returnvalue;
	
	public MetadataDialog()
	{
		addWindowListener(createAppCloser());
	}
	
	public abstract JPanel createbuttonpanel();
	public abstract void initdata();
	
	public int showOpenDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}
	
	private WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				returnvalue = CANCEL_OPTION;
				dispose();
			}
		};
	}
}
