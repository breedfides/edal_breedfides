/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Example{

    public Example() {
    }

    public static void main(String args[]) {
       JFrame f = new JFrame("GridBag Layout Example");

       GridBagLayout gridbag = new GridBagLayout();
       GridBagConstraints c = new GridBagConstraints();
       f.setLayout(gridbag);
//添加按钮1
       c.fill = GridBagConstraints.BOTH;
       c.gridx=0;
       c.gridy=0;	
       c.gridheight=1;
       c.gridwidth=1;
     
       JButton jButton1 = new JButton("按钮1");
       gridbag.setConstraints(jButton1, c);
       f.add(jButton1);
//添加按钮2       
       c.fill = GridBagConstraints.BOTH;
       c.gridwidth=2;
       c.gridx=1;
       c.gridy=0;	
       c.gridheight=1;
       c.weightx=0.1;
 
       JTextField jButton2 = new JTextField("");
       gridbag.setConstraints(jButton2, c);
       f.add(jButton2);


       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.setSize(500,500);
       f.setVisible(true);
    }
}
