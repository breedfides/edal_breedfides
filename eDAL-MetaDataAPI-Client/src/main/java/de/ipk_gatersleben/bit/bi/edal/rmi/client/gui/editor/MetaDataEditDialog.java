/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.MetadataDialog;
/**
 *  This abstract class provides default implementations for the <code>MetadataDialog</code> class. 
 *  To create a concrete <code>MetadataeditDialog</code> as a subclass,you need only provide implementations 
 *  for the the method:
 *  <pre>
 *  public void initdata();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public abstract class MetaDataEditDialog extends MetadataDialog{
	private static final long serialVersionUID = 1L;
	private JButton savebtn;
	private JButton cancelbtn;
	
	
	public MetaDataEditDialog()
	{
		super();
	}
	
	
	public JPanel createbuttonpanel()
	{
		savebtn = new JButton(okAction);
		cancelbtn = new JButton(cancelAction);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);
		
		this.getRootPane().setDefaultButton(savebtn);
		
		return buttonPane;
	}
	
	public void okbuttonevent(ActionEvent e)
	{
		
	}
	
	public void cancelbuttonevent(ActionEvent e)
	{
		
	}
	
	private Action okAction = new AbstractAction("Ok") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			okbuttonevent(e);
			returnvalue = APPROVE_OPTION;
			dispose();
		}
	};
	
	private Action cancelAction = new AbstractAction("Cancel") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			cancelbuttonevent(e);
			returnvalue = CANCEL_OPTION;
			dispose();
		}
	};
	
}
