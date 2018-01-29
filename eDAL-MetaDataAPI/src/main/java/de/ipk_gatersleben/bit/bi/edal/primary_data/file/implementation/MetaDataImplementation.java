/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormatException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DirectoryMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UnknownMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyCheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDirectoryMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEmptyMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyLegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyNaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPersons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MySubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUnknownMetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;

/**
 * Implementation of {@link MetaData}.
 * 
 * @author arendd
 */
@Entity
@Table(name = "METADATA")
public class MetaDataImplementation extends MetaData implements Cloneable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	private static final Map<Class<? extends UntypedData>, Class<? extends MyUntypedData>> DATATYPE_MAP = Collections
			.unmodifiableMap(new HashMap<Class<? extends UntypedData>, Class<? extends MyUntypedData>>() {
				{
					this.put(DataFormat.class, MyDataFormat.class);
					this.put(DataType.class, MyDataType.class);
					this.put(DirectoryMetaData.class, MyDirectoryMetaData.class);
					this.put(EmptyMetaData.class, MyEmptyMetaData.class);
					this.put(Identifier.class, MyIdentifier.class);
					this.put(IdentifierRelation.class, MyIdentifierRelation.class);
					this.put(NaturalPerson.class, MyNaturalPerson.class);
					this.put(LegalPerson.class, MyLegalPerson.class);
					this.put(UnknownMetaData.class, MyUnknownMetaData.class);
					this.put(UntypedData.class, MyUntypedData.class);
					this.put(DataSize.class, MyDataSize.class);
					this.put(CheckSum.class, MyCheckSum.class);
					this.put(EdalLanguage.class, MyEdalLanguage.class);
					this.put(DateEvents.class, MyDateEvents.class);
					this.put(Subjects.class, MySubjects.class);
					this.put(Persons.class, MyPersons.class);
				}
			});

	private int id;

	private Map<EnumDublinCoreElements, MyUntypedData> myMap;

	/**
	 * Default constructor for {@link MetaDataImplementation} is necessary for
	 * PojoInstantiator of <em>HIBERNATE</em>.
	 */
	public MetaDataImplementation() {
		super();
		this.myMap = new HashMap<>();

		/* put default values into the internal Map */

		for (EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
			try {
				this.myMap.put(element, this.convertToPrivateUntypedData(super.getElementValue(element)));
			} catch (MetaDataException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Clone the {@link MetaDataImplementation} object.
	 * 
	 * @return a {@link MetaDataImplementation} object.
	 * @throws CloneNotSupportedException
	 *             if unable to clone the object.
	 */
	public MetaDataImplementation clone() throws CloneNotSupportedException {

		MetaDataImplementation clone = (MetaDataImplementation) super.clone();

		for (EnumDublinCoreElements element : EnumDublinCoreElements.values()) {

			try {
				clone.myMap.put(element, clone.convertToPrivateUntypedData(clone.getElementValue(element)));
			} catch (MetaDataException e) {
				throw new CloneNotSupportedException(
						"Unable to clone MetaDataImplementation object! " + e.getMessage());
			}
		}
		return clone;
	}

	/**
	 * Convert public {@link UntypedData} into private {@link MyUntypedData} to
	 * save in database.
	 * 
	 * @param value
	 *            the {@link UntypedData} object to convert.
	 * @return {@link MyUntypedData}
	 */

	private MyUntypedData convertToPrivateUntypedData(final UntypedData value) {
		try {
			Constructor<? extends MyUntypedData> constructor = MetaDataImplementation.DATATYPE_MAP.get(value.getClass())
					.getConstructor(UntypedData.class);

			return constructor.newInstance(value);

		} catch (final Exception e) {
			/* should never happen! */
			e.printStackTrace();
			DataManager.getImplProv().getLogger()
					.fatal("Error while convert to private UntypedData " + value.getClass().getSimpleName(), e);
			return new MyUntypedData();
		}
	}

	/**
	 * Convert private {@link MyUntypedData} from database back into public
	 * {@link UntypedData}.
	 * 
	 * @param myUntypedData
	 *            the {@link MyUntypedData} object to convert.
	 * @return {@link UntypedData}
	 * @throws ORCIDException 
	 */

	private UntypedData convertToPublicUntypedData(MyUntypedData myUntypedData) throws ORCIDException {

		if (myUntypedData.getClass().equals(MyUntypedData.class)) {
			return new UntypedData(myUntypedData.getString());

		} else if (myUntypedData.getClass().equals(MyNaturalPerson.class)) {
			MyNaturalPerson mnp = (MyNaturalPerson) myUntypedData;

			NaturalPerson np = new NaturalPerson(mnp.getGivenName(), mnp.getSureName(), mnp.getAddressLine(),
					mnp.getZip(), mnp.getCountry());

			np.setOrcid(mnp.getOrcid().toORCID());
			return np;

		} else if (myUntypedData.getClass().equals(MyLegalPerson.class)) {
			MyLegalPerson p = (MyLegalPerson) myUntypedData;
			return new LegalPerson(p.getLegalName(), p.getAddressLine(), p.getZip(), p.getCountry());

		} else if (myUntypedData.getClass().equals(MyDataType.class)) {
			MyDataType myDataType = (MyDataType) myUntypedData;
			return new DataType(myDataType.getDataType());

		} else if (myUntypedData.getClass().equals(MyDataFormat.class)) {
			MyDataFormat myDataFormat = (MyDataFormat) myUntypedData;
			try {
				return new DataFormat(myDataFormat.getMimeType());
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
			return null;

		} else if (myUntypedData.getClass().equals(MyEmptyMetaData.class)) {

			return MetaData.EMPTY;
		} else if (myUntypedData.getClass().equals(MyDirectoryMetaData.class)) {

			return MetaData.DIRECTORY;
		} else if (myUntypedData.getClass().equals(MyUnknownMetaData.class)) {

			return MetaData.UNKNOWN;
		} else if (myUntypedData.getClass().equals(MyIdentifier.class)) {

			return new Identifier(((MyIdentifier) myUntypedData).getIdentifier());
		} else if (myUntypedData.getClass().equals(MyIdentifierRelation.class)) {

			return ((MyIdentifierRelation) myUntypedData).toIdentifierRelation();
		} else if (myUntypedData.getClass().equals(MyDateEvents.class)) {

			return ((MyDateEvents) myUntypedData).toDates();
		} else if (myUntypedData.getClass().equals(MyDataSize.class)) {

			return ((MyDataSize) myUntypedData).toDataSize();
		} else if (myUntypedData.getClass().equals(MyCheckSum.class)) {

			return ((MyCheckSum) myUntypedData).toCheckSum();

		} else if (myUntypedData.getClass().equals(MyEdalLanguage.class)) {

			return new EdalLanguage(((MyEdalLanguage) myUntypedData).getLanguage());

		} else if (myUntypedData.getClass().equals(MySubjects.class)) {

			return ((MySubjects) myUntypedData).toSubjets();
		} else if (myUntypedData.getClass().equals(MyPersons.class)) {

			return ((MyPersons) myUntypedData).toPerson();
		}

		else {
			return null;
		}
	}

	/**
	 * Getter for the field <code>id</code>.
	 * 
	 * @return a int.
	 */
	@Id
	@GeneratedValue
	@Column(name = "ID")
	protected int getId() {
		return this.id;
	}

	/**
	 * Getter for the field <code>myMap</code>.
	 * 
	 * @return a {@link Map} object.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKeyEnumerated(value = EnumType.ORDINAL)
	@JoinTable(name = "METADATA_MAP", joinColumns = @JoinColumn(name = "METADATA_ID"))
	@Fetch(FetchMode.SELECT)
	protected Map<EnumDublinCoreElements, MyUntypedData> getMyMap() {
		return this.myMap;
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	public void setElementValue(final EnumDublinCoreElements key, final UntypedData value) throws MetaDataException {

		/* override super.method */
		/* set values in EnumMap */
		super.setElementValue(key, value);

		/* set values in myMap */
		this.myMap.put(key, this.convertToPrivateUntypedData(value));
	}

	/**
	 * Setter for the field <code>id</code>.
	 * 
	 * @param id
	 *            a int.
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Setter for the field <code>myMap</code>.
	 * 
	 * @param myMap
	 *            a {@link Map} object.
	 */
	protected void setMyMap(Map<EnumDublinCoreElements, MyUntypedData> myMap) {
		this.myMap = myMap;

		/* myMap values from database have to set to this.myMap */
		for (Map.Entry<EnumDublinCoreElements, MyUntypedData> entry : myMap.entrySet()) {
			try {
				super.setElementValue(entry.getKey(), this.convertToPublicUntypedData(entry.getValue()));
			} catch (Exception e) {

				DataManager.getImplProv().getLogger().fatal(
						"Error while convert to public UntypedData " + entry.getValue().getClass().getSimpleName());
				DataManager.getImplProv().getLogger().error(e);
			}
		}
	}
}