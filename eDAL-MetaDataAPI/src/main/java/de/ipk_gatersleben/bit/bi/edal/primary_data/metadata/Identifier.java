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

import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;

/**
 * Unique identifier, which is ensures to be global uniqueness defined at
 * <a href="http://www.iso.ch/cate/d2229.html" target="_blank">ISO/IEC
 * 11578:1996</a>.
 * 
 * @author lange
 * @author arendd
 */
public class Identifier extends UntypedData {

	public static final String UNKNOWN_ID = "Unknown_ID";
	/**
	 * the generated serial ID
	 */
	private static final long serialVersionUID = 3380495549321758121L;
	private RelatedIdentifierType relatedIdentifierType;
	private RelationType relationType;
	private final String identifier;

	/**
	 * Constructor for Identifier.
	 * 
	 * Set the id to "Unkown_ID".
	 */
	public Identifier() {
		this.identifier = UNKNOWN_ID;
	}

	/**
	 * Constructor for Identifier with specified id, type and relationType
	 * 
	 * @param id the id to set.
	 * @param relatedIdentifierType the type to set
	 * @param relationtype the relationType
	 */
	public Identifier(String id, RelatedIdentifierType relatedIdentifierType, RelationType relationType) {
		this.identifier = id;
		this.relatedIdentifierType = relatedIdentifierType;
		this.relationType = relationType;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof Identifier) {

			Identifier id = (Identifier) datatype;

			if (identifier.compareTo(id.identifier) == 0) {
				return super.compareTo(id);
			} else {
				return identifier.compareTo(id.identifier);
			}
		} else {
			return super.compareTo(datatype);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for the identifier.
	 * 
	 * @return the identifier.
	 */
	public final String getIdentifier() {
		return this.identifier;
	}

	public RelationType getRelationType() {
		return relationType;
	}

	public RelatedIdentifierType getRelatedIdentifierType() {
		return relatedIdentifierType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	public void setRelatedIdentifierType(RelatedIdentifierType relatedIdentiferType) {
		this.relatedIdentifierType = relatedIdentiferType;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.identifier;
	}

}
