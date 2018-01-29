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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestInt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern p = Pattern.compile(".*\\/.*\\.v([0-9]+)\\..*$");
		Matcher m = p.matcher("//optimasdata//Http11Protocol.java//Http11Protocol.java.v1.properties.xml");
		if (m.matches()) {
			System.out.println("ok:"+m.group(1));
		}
	}

}
