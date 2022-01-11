/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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