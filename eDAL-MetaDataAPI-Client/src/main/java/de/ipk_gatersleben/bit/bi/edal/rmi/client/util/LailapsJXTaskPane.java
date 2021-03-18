/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
