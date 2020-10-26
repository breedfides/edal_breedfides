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
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.FieldBridge;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;
//import org.hibernate.search.bridge.builtin.LongBridge;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.LanguageBridge;
import ralfs.de.ipk_gatersleben.bit.bi.edal.examples.LongBridge;

/**
 * Internal representation of {@link DataSize} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@DiscriminatorValue("4")
//@Indexed
public final class MyDataSize extends MyUntypedData {

	private static final long serialVersionUID = 6243943469009086574L;
	private Long size = null;

	/**
	 * Default constructor for {@link MyDataSize} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyDataSize() {
	}

	/**
	 * Copy constructor to convert public {@link DataSize} to private
	 * {@link MyDataSize}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyDataSize(final UntypedData edal) {

		super(edal);

		if (edal instanceof DataSize) {
			DataSize dataSize = (DataSize) edal;

			this.setSize(dataSize.getFileSize());
		}
	}

	/**
	 * Convert this {@link MyDataSize} to a public {@link DataSize}.
	 * 
	 * @return a {@link DataSize} object.
	 */
	public DataSize toDataSize() {
		return new DataSize(this.getSize());
	}

	/**
	 * Setter for the field <code>size</code>.
	 * 
	 * @param size
	 *            the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * Getter for the field <code>size</code>.
	 * 
	 * @return size
	 */
	//@Field(store = Store.YES, bridge = @FieldBridge(impl = LongBridge.class))
    @FullTextField( 
    		analyzer = "default",projectable = Projectable.YES, valueBridge = @ValueBridgeRef(type = LongBridge.class)
    )
	public Long getSize() {
		return size;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (this.getSize() != null) {
			return this.getSize().toString();
		} else {
			return "null";
		}
	}
}
