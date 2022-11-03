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

/*
 * For classification of a species
 */
public class Taxon {

	private String[] synonyms;
	private String name;
	private String taxonRank;
	private String parentTaxon;
	private String vernacularName;
	private String id;


	public Taxon(String name, String taxonRank, String parentTaxon, String vernacularName, String[] synonyms, String id) {
		this.name = name;
		this.taxonRank = taxonRank;
		this.parentTaxon = parentTaxon;
		this.vernacularName = vernacularName;
		this.synonyms = synonyms;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String[] getSynonyms() {
		return synonyms;
	}

	public String getTaxonRank() {
		return taxonRank;
	}

	public String getParentTaxon() {
		return parentTaxon;
	}

	public String getVernacularName() {
		return vernacularName;
	}
	
	public String getId() {
		return id;
	}

}