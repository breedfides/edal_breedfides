/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

public enum ContentNegotiationType {

	RDFTURTLE("text/turtle", "ttl"), RDFXML("application/rdf+xml", "xml"), BIBTEX("application/x-bibtex", "bib"), DATACITE(
			"application/vnd.datacite.datacite+xml", "xml"), RIS("application/x-research-info-systems",
					"ris"), TEXT("text/x-bibliography", "txt"), SCHEMA("application/vnd.schemaorg.ld+json", "jsonld");

	private final String type;
	private final String fileEnding;

	private ContentNegotiationType(String type, String fileEnding) {
		this.type = type;
		this.fileEnding = fileEnding;
	}

	public String getType() {
		return this.type;
	}

	public String getFileEnding() {
		return this.fileEnding;
	}

}
