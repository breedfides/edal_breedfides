/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalFileHelper;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;

/**
 * A class which implements TreeWillExpandListener interface, The listener
 * that's notified when a tree expands or collapses a node.
 * 
 * @version 1.0
 * @author Jinbo Chen
 */

public class TreeWillExpandListenerAction implements TreeWillExpandListener {
    private final JTree tree;
    private final ClientPrimaryDataDirectory rootDirectory;

    /**
     * Constructs a <code>TreeWillExpandListenerAction</code> that is
     * initialized with <code>tree</code> as the jtree component, and
     * <code>rootDirectory</code> as the EDAL Directory. If any of the
     * parameters are <code>null</code> this method will not initialize.
     * 
     * @param tree
     *            the jtree component
     * @param rootDirectory
     *            the EDAL Directory
     */
    public TreeWillExpandListenerAction(final JTree tree,
	    final ClientPrimaryDataDirectory rootDirectory) {
	this.tree = tree;
	this.rootDirectory = rootDirectory;
    }

    @Override
    public void treeWillCollapse(final TreeExpansionEvent e)
	    throws ExpandVetoException {
	this.tree.scrollPathToVisible(e.getPath());
    }

    @Override
    public void treeWillExpand(final TreeExpansionEvent e)
	    throws ExpandVetoException {
	DefaultMutableTreeNode node;
	node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
	final EdalNode mynodeobj = (EdalNode) node.getUserObject();

	try {
	    node.removeAllChildren();
	    final ClientPrimaryDataDirectory curdir = (ClientPrimaryDataDirectory) EdalFileHelper
		    .getEntity(mynodeobj.getPath(), this.rootDirectory);
	    final Map<String, ClientPrimaryDataEntity> dirnamemap = new HashMap<String, ClientPrimaryDataEntity>();

	    if (curdir.isDirectory()) {
		final List<ClientPrimaryDataEntity> dirlist = curdir
			.listPrimaryDataEntities();
		final List<String> dirnamelist = new ArrayList<String>();

		if (dirlist != null) {
		    for (final ClientPrimaryDataEntity dir : dirlist) {
			if (dir.isDirectory()
				&& !dir.getCurrentVersion().isDeleted()) {
			    dirnamelist.add(dir.getName());
			    dirnamemap.put(dir.getName(), dir);
			}
		    }
		}
		Collections.sort(dirnamelist, String.CASE_INSENSITIVE_ORDER);

		for (final String dirname : dirnamelist) {
		    final DefaultMutableTreeNode child = new EdalMutableTreeModel(
			    new EdalNode(dirname,
				    ((ClientPrimaryDataDirectory) dirnamemap
					    .get(dirname)).getPath()));
		    node.add(child);

		}
	    }
	    this.tree.scrollPathToVisible(e.getPath());
	} catch (final RemoteException e1) {
	    ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
	    ErrorDialog.showError(e1);
	} catch (final PrimaryDataDirectoryException e1) {
	    ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
	    ErrorDialog.showError(e1);
	} catch (final NotBoundException e1) {
	    ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
	    ErrorDialog.showError(e1);
	} catch (final AccessControlException e1) {
		ErrorDialog.showError(e1);
	}
    }

}
