/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
/**
 *  The <code>LegalPersonviewDialog</code> can be used to view <code>LegalPerson</code>,
 *  which implements the  <code>MetadataviewDialog</code> class, we can use it
 *  with a couple of lines of code:
 *  <pre>
 *  	LegalPersonviewDialog personviewDialog = new LegalPersonviewDialog(person); 
 *  	personviewDialog.showOpenDialog();
 *  </pre>
 * @version 1.0
 * @author Jinbo Chen
 */
public class LegalPersonviewDialog extends MetadataViewDialog{
	private static final long serialVersionUID = 1L;
	
	private JLabel addresslabel;
	private JTextField addresstext;
	private JLabel ziplabel;
	private JTextField ziptext;
	private JLabel countrylabel;
	private JTextField countrytext;
	private JLabel legalnamelabel;
	private JTextField legalnametext;
	private LegalPerson person;
	
	public LegalPersonviewDialog(LegalPerson person,String title)
	{
		super();
		
		this.person = person;
		
		setTitle(title);
		
		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());
		
		JPanel editPane = new JPanel();		
		GridBagLayout gridbag = new GridBagLayout();
		editPane.setLayout(gridbag);
		
		GridBagConstraints c = new GridBagConstraints();
		
		addresslabel = new JLabel("Address:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
        c.gridy=0;	
        c.gridheight=1;
        c.gridwidth=1;
        c.weightx= 0;
        c.insets = new Insets(0,0,3,0);
    	gridbag.setConstraints(addresslabel, c);
		editPane.add(addresslabel);
		
		addresstext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx=1;
        c.gridy=0;	
        c.gridheight=1;
        c.gridwidth=2;
        c.weightx=1.0;
        c.ipadx=1;
        c.ipady=1;
        addresstext.setEditable(false);
    	gridbag.setConstraints(addresstext, c);
		editPane.add(addresstext);
		
		ziplabel = new JLabel("Zip:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
        c.gridy=1;	
        c.gridheight=1;
        c.gridwidth=1;
        c.weightx= 0;
        c.ipadx=1;
        c.ipady=1;
    	gridbag.setConstraints(ziplabel, c);
		editPane.add(ziplabel);
		
		ziptext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx=1;
        c.gridy=1;	
        c.gridheight=1;
        c.gridwidth=2;
        c.weightx=1.0;
        c.ipadx=1;
        c.ipady=1;
        ziptext.setEditable(false);
    	gridbag.setConstraints(ziptext, c);		
		editPane.add(ziptext);
		
		countrylabel = new JLabel("Country:");
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
        c.gridy=2;	
        c.gridheight=1;
        c.gridwidth=1;
        c.weightx= 0;
    	gridbag.setConstraints(countrylabel, c);
		editPane.add(countrylabel);
		
		countrytext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx=1;
        c.gridy=2;	
        c.gridheight=1;
        c.gridwidth=2;
        c.weightx=1.0;
        countrytext.setEditable(false);
    	gridbag.setConstraints(countrytext, c);		
		editPane.add(countrytext);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
	    c.gridy=3;		
	    c.gridheight=1;
	    c.gridwidth=1;	
	    c.weightx= 0;
	    legalnamelabel = new JLabel("LegalName:");
		gridbag.setConstraints(legalnamelabel, c);
		editPane.add(legalnamelabel);
			
		legalnametext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx=1;
	    c.gridy=3;	
	    c.gridheight=1;
	    c.gridwidth=2;
	    c.weightx=1.0;
	    legalnametext.setEditable(false);
	    gridbag.setConstraints(legalnametext, c);
		editPane.add(legalnametext);

		contents.add(editPane,BorderLayout.CENTER);
		contents.add(createbuttonpanel(),BorderLayout.SOUTH);
		
		
		this.setMinimumSize(new Dimension(350,(int)(350*0.618)));
		
		initdata();
	}
	@Override
	public void initdata()
	{
		if(person!=null)
		{
			legalnametext.setText(person.getLegalName());
			addresstext.setText(person.getAddressLine());
			ziptext.setText(person.getZip());
			countrytext.setText(person.getCountry());
		}
	}
	
}
