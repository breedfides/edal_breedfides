/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
