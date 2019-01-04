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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.UIManager;

public class UIManagerColorKeys {
	public static void main(String[] args) throws Exception
	  {
	    List<String> colorKeys = new ArrayList<String>();
	    Set<Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet();
		for (Entry<?, ?> entry : entries)
	    {
	      if (entry.getValue() instanceof Color)
	      {
	        colorKeys.add((String) entry.getKey());
	      }
	    }

	    // sort the color keys
	    Collections.sort(colorKeys);
	    
	    // print the color keys
	    for (String colorKey : colorKeys)
	    {
	      System.out.println(colorKey+":"+UIManager.getLookAndFeelDefaults().getColor(colorKey));
	    }

	  }
}
