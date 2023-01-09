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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;

/**
 * Internal representation of {@link Identifier} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("8")
@Indexed
public final class MyIdentifier extends MyUntypedData {

	private static final long serialVersionUID = -460725495792281812L;

	private String identifier;
	private RelatedIdentifierType relatedIdentifierType;
	private RelationType relationType;

	/**
	 * Default constructor for {@link MyIdentifier} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyIdentifier() {
	}

	/**
	 * Copy constructor to convert public {@link Identifier} to private
	 * {@link MyIdentifier}.
	 * 
	 * @param edal the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyIdentifier(final UntypedData edal) {

		super(edal);

		if (edal instanceof Identifier) {
			Identifier id = (Identifier) edal;
			this.setIdentifier(id.getIdentifier());
			this.setRelatedIdentifierType(id.getRelatedIdentifierType());
			this.setRelationType(id.getRelationType());
		}
	}

	/**
	 * Getter for the field <code>identifier</code>.
	 * 
	 * @return the identifier
	 */
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public final String getIdentifier() {
		return identifier;
	}

	/**
	 * Setter for the field <code>identifier</code>.
	 * 
	 * @param identifier the identifier to set
	 */
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Convert this {@link MyIdentifier} to a public {@link Identifier}.
	 * 
	 * @return a {@link Identifier} object.
	 */
	public final Identifier toIdentifier() {
		
		Identifier identifier = new Identifier(this.getIdentifier(),this.getRelatedIdentifierType(),this.getRelationType());
		
		return identifier;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "MyIdentifier [identifier=" + identifier + "]";
	}

	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	@Enumerated(EnumType.STRING)
	public RelatedIdentifierType getRelatedIdentifierType() {
		return relatedIdentifierType;
	}

	public void setRelatedIdentifierType(RelatedIdentifierType relatedIdentifierType) {
		this.relatedIdentifierType = relatedIdentifierType;
	}
	
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	@Enumerated(EnumType.STRING)
	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}
}