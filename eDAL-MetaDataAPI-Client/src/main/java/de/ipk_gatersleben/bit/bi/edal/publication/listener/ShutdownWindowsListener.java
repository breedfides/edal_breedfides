/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;

/**
 * {@link WindowAdapter} to ask for the user confirmation before closing a
 * window.
 * 
 * @author arendd
 * 
 */
public class ShutdownWindowsListener extends WindowAdapter {

	public ShutdownWindowsListener() {
		super();
	}

	public void windowClosing(WindowEvent e) {
		int result = JOptionPane.showConfirmDialog(null, "Close " + PropertyLoader.PROGRAM_NAME + " ?", "EXIT", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

}