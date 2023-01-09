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
