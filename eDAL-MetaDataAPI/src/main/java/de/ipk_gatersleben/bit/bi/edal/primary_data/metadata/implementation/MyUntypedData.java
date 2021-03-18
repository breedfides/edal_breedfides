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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * Internal representation of {@link UntypedData} for persistence with
 * <em>HIBERNATE</em>.
 * 
 * @author arendd
 */
@Entity
@Indexed(index = "UntypedData")
@Table(name = "UntypedData", indexes = { @Index(name = "index_string", columnList = "string") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(columnDefinition = "char(2)", name = "UntypedDataType", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("1")
public class MyUntypedData extends UntypedData implements Serializable {

	private static final long serialVersionUID = 7997693675858702512L;
	private int dataId;

	/**
	 * Default constructor for {@link MyUntypedData} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MyUntypedData() {
		super();
	}

	/**
	 * Constructor for MyUntypedData with specified string.
	 * 
	 * @param string
	 *            a {@link String} object.
	 */
	public MyUntypedData(final String string) {
		super(string);
	}

	/**
	 * Copy constructor to convert public {@link UntypedData} to private
	 * {@link MyUntypedData}.
	 * 
	 * @param edal
	 *            the EDAL public {@link UntypedData} object to be cloned
	 */
	public MyUntypedData(final UntypedData edal) {
		this.setString(edal.getString());
	}

	/**
	 * Getter for the id.
	 * 
	 * @return a int.
	 */
	@Id
	@GeneratedValue
	public int getId() {
		return this.dataId;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Index.TOKENIZED = ignore case
	 * <p>
	 * '@Analyzer(impl=WhitespaceAnalyzer.class)'
	 * <p>
	 * WhitespaceAnalyzer is necessary to find object with dots at the beginning
	 */
	@Override
	@Column(columnDefinition = "varchar(4000)")
	@Field(index = org.hibernate.search.annotations.Index.YES, store = Store.YES)
	public String getString() {
		return this.string;
	}

	/**
	 * Setter for the id.
	 * 
	 * @param id
	 *            a int.
	 */
	public void setId(final int id) {
		this.dataId = id;
	}

	/** {@inheritDoc} */
	@Override
	public void setString(final String string) {
		this.string = string;
	}
}