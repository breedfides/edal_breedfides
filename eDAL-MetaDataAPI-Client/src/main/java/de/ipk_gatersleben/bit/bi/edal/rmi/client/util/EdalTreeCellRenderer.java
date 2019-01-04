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
