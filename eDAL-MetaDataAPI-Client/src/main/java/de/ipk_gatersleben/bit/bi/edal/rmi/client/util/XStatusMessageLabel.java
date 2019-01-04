/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import javax.swing.ImageIcon;


public class XStatusMessageLabel extends XStatusLabel
{
	private static final long serialVersionUID = 1L;
	private static final ImageIcon ICON_GREEN = ImageUtil.createImageIcon("statusbar_message_light_green_10x10.png","");

	public XStatusMessageLabel()
	{
		setText("Server is connected");
	}

	protected void init()
	{
		super.init();
		setFont(ImageUtil.FONT_14_BOLD);
		setGreenLight();
		initMockers();
	}

	
	public void setGreenLight()
	{
		setIcon(ICON_GREEN);
	}

	
	private void initMockers()
	{
		setGreenLight();
		setText("Server is connected");
	}
}
