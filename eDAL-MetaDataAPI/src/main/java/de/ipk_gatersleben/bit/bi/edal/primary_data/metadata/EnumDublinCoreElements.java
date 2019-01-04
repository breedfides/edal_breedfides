/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Definition of <a href="http://dublincore.org/documents/dces/"
 * target="_blank">dublin core meta data</a> elements.
 * 
 * We provide a toString() implementation to get the description of the meta
 * data element in the pattern <pre>ELEMENT:DESCRIPTION</pre>
 * 
 * @author lange
 * @author arendd
 */
public enum EnumDublinCoreElements {

	/**
	 * An entity to provide a generated checksum for the corresponding file.
	 */
	CHECKSUM {

	},
	/**
	 * An entity responsible for making contributions to the resource
	 */
	CONTRIBUTOR {

	},
	/**
	 * The spatial or temporal topic of the resource, the spatial applicability
	 * of the resource, or the jurisdiction under which the resource is relevant
	 */
	COVERAGE {

	},
	/**
	 * An entity primarily responsible for making the resource
	 */
	CREATOR {

	},
	/**
	 * A point or period of time associated with an event in the lifecycle of
	 * the resource.
	 */
	DATE {

	},
	/**
	 * An account of the resource
	 */
	DESCRIPTION {

	},
	/**
	 * The file format, physical medium, or dimensions of the resource specified
	 * as <a href="http://www.iana.org/assignments/media-types/">MIME</a>
	 */
	FORMAT {

	},
	/**
	 * An unambiguous reference to the resource within a given context.
	 */
	IDENTIFIER {

	},
	/**
	 * A language of the resource. we use <a
	 * href="http://www.ietf.org/rfc/rfc4646.txt" target="_blank">RFC4646</a>
	 * 
	 */
	LANGUAGE {

	},
	/**
	 * An entity responsible for making the resource available.
	 */
	PUBLISHER {

	},
	/**
	 * A related resource.
	 */
	RELATION {

	},
	/**
	 * Information about rights held in and over the resource includes a
	 * statement about various property rights associated with the resource,
	 * including intellectual property rights.
	 */
	RIGHTS {

	},
	/**
	 * Unstructured size information about the resource.
	 */
	SIZE {

	},
	/**
	 * A related resource from which the described resource is derived.
	 */
	SOURCE {

	},
	/**
	 * The topic of the resource, e.g. keywords
	 */
	SUBJECT {

	},
	/**
	 * A name given to the resource.
	 */
	TITLE {

	},
	/**
	 * The nature or genre of the resource. we use use the DCMI Type Vocabulary
	 * <a href="http://dublincore.org/documents/dcmi-type-vocabulary/"
	 * target="_blank">DCMITYPE</a>
	 */
	TYPE {

	}
}
