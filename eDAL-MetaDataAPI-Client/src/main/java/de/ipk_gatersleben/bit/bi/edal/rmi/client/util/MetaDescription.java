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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.util.HashMap;
import java.util.Map;

public class MetaDescription {
    private static Map<String,String> descmap = new HashMap<String,String>();
    static
    {
    	descmap.put("CONTRIBUTOR", "An entity responsible for making contributions to the resource");
    	descmap.put("COVERAGE", "The spatial or temporal topic of the resource,<br>the spatial applicability of the resource,<br>or the jurisdiction under which the resource is relevant");
    	descmap.put("CREATOR", "An entity primarily responsible for making the resource");
    	descmap.put("DATE", "A point or period of time associated with an event in the lifecycle of the resource");
    	descmap.put("DESCRIPTION", "An account of the resource");
    	descmap.put("FORMAT", "The file format, physical medium, or dimensions of the resource specified as <a href=\"http://www.iana.org/assignments/media-types/\">MIME</a>");
    	descmap.put("IDENTIFIER", "An unambiguous reference to the resource within a given context");
    	descmap.put("LANGUAGE", "A language of the resource. we use <a href=\"http://www.ietf.org/rfc/rfc4646.txt\" target=\"_blank\">RFC4646</a>");
    	descmap.put("PUBLISHER", "An entity responsible for making the resource available");
    	descmap.put("RELATION", "A related resource");
    	descmap.put("RIGHTS", "Information about rights held in and over the resource<br>includes a statement about various property rights associated with the<br>resource, including intellectual property rights");
    	descmap.put("SIZE", "Unstructured size information about the resource");
    	descmap.put("SOURCE", "A related resource from which the described resource is derived");
    	descmap.put("SUBJECT", "The topic of the resource, e.g. keywords");
    	descmap.put("TITLE", "A name given to the resource");
    	descmap.put("TYPE", " The nature or genre of the resource. we use use the DCMI Type <br>Vocabulary <a href=\"http://dublincore.org/documents/dcmi-type-vocabulary/\" target=\"_blank\">DCMITYPE</a>");
    	descmap.put("CHECKSUM", "An entity to provide a generated checksum for the corresponding file.");
    }
    
    public static String getDescription(String elementname)
    {
    	String desc = descmap.get(elementname);
    	if(desc==null){
    		return "";
    	}
    	else
    	{
    		return desc;
    	}
    }
}
