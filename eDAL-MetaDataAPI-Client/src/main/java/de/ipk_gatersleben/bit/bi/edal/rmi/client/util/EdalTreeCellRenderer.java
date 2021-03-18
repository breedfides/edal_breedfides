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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class EdalTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = 1L;
	private static final Color TABLE_GRID_COLOR = new Color(0xd9d9d9);
	private static final Color TABLE_selectionForeground = new Color(51,51,51);
	private static final Color TABLE_selectionBackground = new Color(184,207,229);
	private final TreeCellRenderer renderer;
	public EdalTreeCellRenderer(TreeCellRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean isSelected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        JComponent c = (JComponent) renderer.getTreeCellRendererComponent(
                tree, value, isSelected, expanded, leaf, row, hasFocus);
        
        if(isSelected && !hasFocus)
        {
        	c.setOpaque(true);
        	c.setForeground(getTextNonSelectionColor());
        	c.setBackground(TABLE_GRID_COLOR);
        }
        else if(isSelected && hasFocus)
        {
        	c.setOpaque(true);
            c.setForeground(TABLE_selectionForeground);
            c.setBackground(TABLE_selectionBackground);
        }
        else
        {
        	c.setOpaque(true);
        	c.setForeground(getTextNonSelectionColor());
        	c.setBackground(getBackgroundNonSelectionColor());
        	
        }
        
        return c;
    }
}
