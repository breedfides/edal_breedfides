/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.util.HashMap;
import java.util.Map;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
/**
 * <code>ViewerContainer</code> provides a Container for all the <code>MetadataViewer</code>,
 * this class is used in <code>EDALFileChooser</code>.
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class ViewerContainer {
private static Map<EnumDublinCoreElements,MetadataViewer> map = new HashMap<EnumDublinCoreElements,MetadataViewer>();
	/**
	 * register viewer for element
	 *
	 * @param     element   the metadatatype
	 * @param     viewer    the metadatatype corresponding viewer
	 */ 
	public static void registerViewer(EnumDublinCoreElements element,MetadataViewer viewer)
	{
		map.put(element, viewer);
	}
	/**
     * Returns <code>MetadataViewer</code>
     *
     * @param     element   metadatatype.
     * @return    the  corresponding viewer.
     */ 
	public static MetadataViewer getViewer(EnumDublinCoreElements element)
	{
		return map.get(element);
	}
	/**
	 * cleanup the ViewerContainer
	 */
	public static void clear()
	{
		map.clear();
	}
}
