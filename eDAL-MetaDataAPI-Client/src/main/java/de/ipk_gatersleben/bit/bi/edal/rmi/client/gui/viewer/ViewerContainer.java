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
