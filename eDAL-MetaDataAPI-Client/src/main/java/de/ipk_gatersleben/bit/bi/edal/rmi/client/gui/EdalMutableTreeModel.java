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

import javax.swing.tree.DefaultMutableTreeNode;
/**
 * Defines the requirements for an object that can be used as a
 * tree node in a JTree.
 * Implementations of <code>DefaultMutableTreeNode</code> that override <code>isLeaf</code>
 * Because when we build the directory tree, we use lazy load, we want all the files looks like 
 * a directroy, so the isLeaf() method return false
 * Refer to {@link javax.swing.tree.TreeModel} for more information.
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalMutableTreeModel extends DefaultMutableTreeNode{
	private static final long serialVersionUID = 1L;
	public EdalMutableTreeModel(Object obj)
	{
		super(obj);
	}
	@Override
	public boolean isLeaf() {
		return false;
	}

}
