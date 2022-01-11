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
