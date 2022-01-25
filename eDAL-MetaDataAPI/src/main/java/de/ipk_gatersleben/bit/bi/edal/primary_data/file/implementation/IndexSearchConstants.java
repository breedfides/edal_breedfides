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
	
	public static final String TITLE = "Title";
	public static final String DESCRIPTION = "Description";
	public static final String COVERAGE = "Coverage";
	public static final String IDENTIFIER = "Identifier";
	public static final String RELATEDIDENTIFIERTYPE = "RelatedIdentifierType";
	public static final String RELATIONTYPE = "RelationType";
	public static final String SIZE = "Size";
	public static final String TYPE = "Type";
	public static final String LANGUAGE = "Language";
	public static final String ALGORITHM = "Algorithm";
	public static final String CHECKSUM = "Checksum";
	public static final String VERSIONID = "versionID";
	public static final String SUBJECT = "Subject";
	public static final String RELATION = "Relation";
	public static final String MIMETYPE = "Mimetype";
	public static final String STARTDATE = "Startdate";
	public static final String ENDDATE = "Enddate";
	public static final String PERSON = "Creator";
	public static final String CONTRIBUTOR = "Contributor";
	public static final String LEGALPERSON = "Legalperson";
	public static final String FILETYPE = "Filetype";
	public static final String CONTRIBUTORNAME = "ContributorName";
	public static final String CREATORNAME = "CreatorName";
	public static final String CREATION_DATE = "VersionCreationDate";
	public static final String ALL = "Allfields";
	public static final String DOCID = "docid";
	public static final String INTERNALID = "internalId";
	//to map a Document to the corresponding publicReferenceEntity
	public static final String ENTITYID = "entityId";
	//to map a Document to it's corresponding DataEntity
	public static final String PRIMARYENTITYID = "associatedPrimaryEntityID";

	//to distinguish publicReferenceEntities and single Files/Folders
	public static final String ENTITYTYPE = "entityType";
	
	
	
	/* constants for indexed lucene fields that are used for public searching */
	public static final String FILE = "file";
	public static final String REVISION = "revision";
	public static final String PUBLICREFERENCE = "dataset";
	public static final String INDIVIDUALFILE = "singledata";
	public static final String DIRECTORY = "directory";
	public static final String CONTENT = "Content";
	public static final String PUBLICID = "PublicReference";	
	public static final String PUBLIC_LAST_ID = "last_id_publicreference.dat";
	public static final String NATIVE_INDEXER_LAST_ID = "last_id_publicreference.dat";
	
	public static final String[] METADATAFIELDS = { IndexSearchConstants.TITLE, IndexSearchConstants.SIZE,
			IndexSearchConstants.VERSIONID, IndexSearchConstants.ENTITYID, IndexSearchConstants.PRIMARYENTITYID,
			IndexSearchConstants.ENTITYTYPE, IndexSearchConstants.DOCID,
			PUBLICID, IndexSearchConstants.REVISION,
			IndexSearchConstants.PUBLICREFERENCE, INTERNALID,
			IndexSearchConstants.CONTENT, IndexSearchConstants.FILETYPE };

}
