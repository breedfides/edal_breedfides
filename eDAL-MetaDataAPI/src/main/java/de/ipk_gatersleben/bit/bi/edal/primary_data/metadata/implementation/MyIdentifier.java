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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link Identifier} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("8")
public final class MyIdentifier extends MyUntypedData {

	private static final long serialVersionUID = -460725495792281812L;

	private String identifier;

	/**
	 * Default constructor for {@link MyIdentifier} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyIdentifier() {
	}

	/**
	 * Constructor for {@link MyIdentifier} from a {@link Identifier} object.
	 * 
	 * @param id
	 *            a {@link Identifier} object.
	 */
	public MyIdentifier(Identifier id) {
		super();
		this.setIdentifier(id.getID());
	}

	/**
	 * Copy constructor to convert public {@link Identifier} to private
	 * {@link MyIdentifier}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyIdentifier(final UntypedData edal) {

		super(edal);

		if (edal instanceof Identifier) {
			Identifier i = (Identifier) edal;
			this.setIdentifier(i.getID());
		}
	}

	/**
	 * Getter for the field <code>identifier</code>.
	 * 
	 * @return the identifier
	 */
	//@Field(index = Index.YES, store = Store.YES)
	//@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public final String getIdentifier() {
		return identifier;
	}

	/**
	 * Setter for the field <code>identifier</code>.
	 * 
	 * @param identifier
	 *            the identifier to set
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
		return new Identifier(this.getIdentifier());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "MyIdentifier [identifier=" + identifier + "]";
	}
}