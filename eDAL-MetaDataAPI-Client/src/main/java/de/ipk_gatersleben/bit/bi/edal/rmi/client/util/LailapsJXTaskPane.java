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

import javax.swing.JSplitPane;
import org.jdesktop.swingx.JXTaskPane;

public class LailapsJXTaskPane extends JXTaskPane{
	private static final long serialVersionUID = 1L;
	private JSplitPane tableshowpane;
	private LailapsJXTaskPane friendpanel = null;
	
	public void setJSplitPane(JSplitPane tableshowpane)
	{
		this.tableshowpane = tableshowpane;
	}
	
	public void setFriendpanel(LailapsJXTaskPane friendpanel)
	{
		this.friendpanel = friendpanel;
	}
	
	public void setCollapsed(boolean collapsed) {
		super.setCollapsed(collapsed);
		if(tableshowpane!=null )
		{
			if(collapsed)
			{
				if(friendpanel!=null && friendpanel.isCollapsed())
				{
					tableshowpane.setDividerLocation(0.75);
					tableshowpane.setResizeWeight(0.70);
				}
			}
			else
			{
				tableshowpane.setDividerLocation(0.25);
				tableshowpane.setResizeWeight(0.70);
			}
			tableshowpane.resetToPreferredSizes();
			tableshowpane.updateUI();
		}
	}
}
