/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;
import java.util.HashMap;
import java.util.Map;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
/**
 * <code>EditorContainer</code> provides a Container for all the <code>MetadataEditor</code>,
 * this class is used in <code>EDALFileChooser</code>.
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class EditorContainer {
	private static Map<EnumDublinCoreElements,AbstractMetaDataEditor> map = new HashMap<EnumDublinCoreElements,AbstractMetaDataEditor>();
	/**
     * register editor for element
     *
     * @param     element   the metadatatype
     * @param     editor    the metadatatype corresponding editor
     */ 
	public static void registerEditor(EnumDublinCoreElements element,AbstractMetaDataEditor editor)
	{
		map.put(element, editor);
	}
	/**
     * Returns <code>MetadataEditor</code>
     *
     * @param     element   metadatatype.
     * @return    the  corresponding editor.
     */ 
	public static AbstractMetaDataEditor getEditor(EnumDublinCoreElements element)
	{
		return map.get(element);
	}
	/**
	 * cleanup the EditorContainer
	 */
	public static void clear()
	{
		map.clear();
	}
}
