/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.MetadataDialog;
/**
 *  This abstract class provides default implementations for the <code>MetadataDialog</code> class. 
 *  To create a concrete <code>MetadataviewDialog</code> as a subclass,you need only provide implementations 
 *  for the the method:
 *  <pre>
 *  public void initdata();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public abstract class MetadataViewDialog extends MetadataDialog{
	private static final long serialVersionUID = 1L;
	private JButton savebtn;
	
	
	public MetadataViewDialog()
	{
		super();
	}
	
	
	public JPanel createbuttonpanel()
	{
		savebtn = new JButton(okAction);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		buttonPane.add(savebtn);
		this.getRootPane().setDefaultButton(savebtn);
		
		return buttonPane;
	}
	
	private Action okAction = new AbstractAction("Ok") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = APPROVE_OPTION;
			dispose();
		}
	};
}
