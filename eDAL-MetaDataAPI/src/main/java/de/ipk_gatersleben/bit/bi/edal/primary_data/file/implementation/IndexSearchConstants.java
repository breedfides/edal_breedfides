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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

public final class IndexSearchConstants {
		
	/* constants for indexed lucene fields that are used for public searching */
	public static final String FILE = "file";
	public static final String PUBLICREFERENCE = "dataset";
	public static final String INDIVIDUALFILE = "singledata";
	public static final String DIRECTORY = "directory";
	public static final String CONTENT = "Content";
	public static final String PUBLICID = "PublicReference";	
	public static final String PUBLIC_LAST_ID = "last_id_publicreference.dat";
	public static final String NATIVE_INDEXER_LAST_ID = "last_id_publicreference.dat";
	
	public static final String[] METADATAFIELDS = { EnumIndexField.TITLE.value(), EnumIndexField.SIZE.value(),
			EnumIndexField.VERSIONID.value(), EnumIndexField.ENTITYID.value(), EnumIndexField.PRIMARYENTITYID.value(),
			EnumIndexField.ENTITYTYPE.value(), EnumIndexField.DOCID.value(),
			PUBLICID, EnumIndexField.REVISION.value(),
			IndexSearchConstants.PUBLICREFERENCE, EnumIndexField.INTERNALID.value(),
			IndexSearchConstants.CONTENT, EnumIndexField.FILETYPE.value() };

}
