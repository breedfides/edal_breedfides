/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
