/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
