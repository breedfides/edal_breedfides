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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Definition of a list of approved terms that may be used to identify the genre
 * of a resource.
 * 
 * The types are defined in <a
 * href="http://dublincore.org/documents/dcmi-type-vocabulary"
 * target="_blank">DCMITYPE</a><a href="http://dublincore.org/documents/dces/"
 * target="_blank">dublin core meta data</a>
 * <p>
 * We provide a toString() implementation to get the description of the data
 * type data element in the pattern <pre>ELEMENT:DESCRIPTION</pre>
 * 
 * @author lange
 */
public enum EnumDCMIDataType {
	/**
	 * An aggregation of resources. A collection is described as a group; its
	 * parts may also be separately described
	 */
	COLLECTION {

	},
	/**
	 * Data encoded in a defined structure. Examples include lists, tables, and
	 * databases. A dataset may be useful for direct machine processing
	 */
	DATASET {

	},
	/**
	 * A non-persistent, time-based occurrence. Metadata for an event provides
	 * descriptive information that is the basis for discovery of the purpose,
	 * location, duration, and responsible agents associated with an event.
	 * Examples include an exhibition, webcast, conference, workshop, open day,
	 * performance, battle, trial, wedding, tea party, conflagration
	 */
	EVENT {

	},
	/**
	 * A visual representation other than text. Examples include images and
	 * photographs of physical objects, paintings, prints, drawings, other
	 * images and graphics, animations and moving pictures, film, diagrams,
	 * maps, musical notation. Note that Image may include both electronic and
	 * physical representations
	 */
	IMAGE {

	},
	/**
	 * A resource requiring interaction from the user to be understood,
	 * executed, or experienced. Examples include forms on Web pages, applets,
	 * multimedia learning objects, chat services, or virtual reality
	 * environments
	 */
	INTERACTIVERESOURCE {

	},
	/**
	 * A series of visual representations imparting an impression of motion when
	 * shown in succession. Examples include animations, movies, television
	 * programs, videos, zoetropes, or visual output from a simulation.
	 * Instances of the type Moving Image must also be describable as instances
	 * of the broader type Image
	 */
	MOVINGIMAGE {

	},
	/**
	 * An inanimate, three-dimensional object or substance. Note that digital
	 * representations of, or surrogates for, these objects should use Image,
	 * Text or one of the other types
	 */
	PHYSICALOBJECT {

	},
	/**
	 * A system that provides one or more functions. : Examples include a
	 * photocopying service, a banking service, an authentication service,
	 * interlibrary loans, a Z39.50 or Web server
	 * 
	 */
	SERVICE {

	},
	/**
	 * A computer program in source or compiled form. Examples include a C
	 * source file, MS-Windows .exe executable, or Perl script
	 */
	SOFTWARE {

	},
	/**
	 * A resource primarily intended to be heard. Examples include a music
	 * playback file format, an audio compact disc, and recorded speech or
	 * sounds
	 */
	SOUND {

	},
	/**
	 * A static visual representation. Examples include paintings, drawings,
	 * graphic designs, plans and maps. Recommended best practice is to assign
	 * the type Text to images of textual materials. Instances of the type Still
	 * Image must also be describable as instances of the broader type Image
	 */
	STILLIMAGE {

	},
	/**
	 * A resource consisting primarily of words for reading. Examples include
	 * books, letters, dissertations, poems, newspapers, articles, archives of
	 * mailing lists. Note that facsimiles or images of texts are still of the
	 * genre Text
	 */
	TEXT {

	}
}
