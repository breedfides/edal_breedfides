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

/*
 * Enumeration with names and related synonyms for the classification of a species.
 */
public enum EnumTaxon {

	BARLEY(new Taxon("Hordeum spontaneum", "species", "Hordeum", "barley",
			new String[] { "barley", "Hordeum vulgare", "Hordeum spontaneum" },
			"https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=info&id=4513")),

	WHEAT(new Taxon("Triticum vulgare", "species", "Triticum", "wheat",
			new String[] { "wheat", "Triticum aestivum", "Triticum vulgare" },
			"https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=info&id=4565")),

	ARABIDOPSIS(new Taxon("Arabidopsis thaliana", "species", "Arabidopsis", "arabidopsis",
			new String[] { "arabidopsis", "Arabidopsis thaliana", "arabidopsis thaliana" },
			"https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=info&id=3702"));

	EnumTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public Taxon value() {
		return taxon;
	}

	Taxon taxon;

}