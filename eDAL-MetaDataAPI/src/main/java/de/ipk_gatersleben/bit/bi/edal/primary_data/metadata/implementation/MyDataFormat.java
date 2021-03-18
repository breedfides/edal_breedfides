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

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link DataFormat} for persistence with
 * <em>HIBERNATE</em>
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("3")
@Indexed
public final class MyDataFormat extends MyUntypedData {

	private static final long serialVersionUID = -9055352152889379069L;
	private String mimeType;

	/**
	 * Default constructor for {@link MyDataFormat} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyDataFormat() {
	}

	/**
	 * Copy constructor to convert public {@link DataFormat} to private
	 * {@link MyDataFormat}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDataFormat(UntypedData edal) {

		super(edal);

		if (edal instanceof DataFormat) {

			DataFormat dataFormat = (DataFormat) edal;
			this.setMimeType(dataFormat.getMimeType());
		}
	}

	/**
	 * Getter for the field <code>mimeType</code>.
	 * 
	 * @return the mimeType
	 */
	//@Field(index = Index.YES, store = Store.YES)
	@FullTextField(analyzer = "default",projectable = Projectable.YES)
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Setter for the field <code>mimeType</code>.
	 * 
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}