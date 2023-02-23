/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

public enum EnumIndexField {

		/** Metadata fields like EnumDublinCoreElements **/
		TITLE("Title"),
		
		DESCRIPTION("Description"),
		
		COVERAGE("Coverage"),
		
		IDENTIFIER("Identifier"),
		
		RELATEDIDENTIFIERTYPE("RelatedIdentifierType"),
		
		RELATIONTYPE("RelationType"),
		
		SIZE("Size"),
		
		TYPE("Type"),
		
		LANGUAGE("Language"),
		
		ALGORITHM("Algorithm"),
		
		CHECKSUM("Checksum"),
		
		VERSIONID("versionID"),
		
		SUBJECT("Subject"),
		
		RELATION("Relation"),
		
		MIMETYPE("Mimetype"),
		
		STARTDATE("Startdate"),
		
		ENDDATE("Enddate"),
		
		CREATOR("Creator"),
		
		CONTRIBUTOR("Contributor"),
		
		LEGALPERSON("Legalperson"),
		
		FILETYPE("Filetype"),
		
		CONTRIBUTORNAME("ContributorName"),
		
		CREATORNAME("CreatorName"),
		
		CREATION_DATE("VersionCreationDate"),
		
		ALL("Allfields"),
		
		
		/** Used as landing page URL of a related PrimaryEntityFile**/
		DOCID("DocId"),
		
		
		/** Internal document fields for example to filter results by type **/
		INTERNALID("InternalId"),
		
		ENTITYID("EntityId"),
		
		PRIMARYENTITYID("AssociatedPrimaryEntityID"),
		
		REVISION("Revision"),
		
		CONTENT("Content"),
		
		PUBLICID("PublicReference"),
		
		ENTITYTYPE("EntityType");
	 
	    private String value;
	 
	    EnumIndexField(String string) {
	        this.value = string;
	    }
	 
	    public String value() {
	        return value;
	    }
	}