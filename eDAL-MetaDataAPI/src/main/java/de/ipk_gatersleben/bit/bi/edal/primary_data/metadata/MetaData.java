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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * Basic meta data comprising at least dublin core administrative meta data.
 * Objects of this class can only be instantiated by calling
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider#createMetaDataInstance()}
 * m which is implemented by the particular eDAL implementation e.g.
 * 
 * Instance MetaData or an extended subclass is not supported
 * 
 * @author lange
 * @author arendd
 */
@SuppressWarnings("unchecked")
public class MetaData implements Cloneable, Serializable {

	protected static final String UNKNOWN_STRING = "unknown";

	public static final EnumMap<EnumDublinCoreElements, List<Class<? extends UntypedData>>> ELEMENT_TYPE_MAP = new EnumMap<>(
			EnumDublinCoreElements.class);

	static {

		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.CHECKSUM,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(CheckSum.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.CONTRIBUTOR,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(Persons.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.COVERAGE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(UntypedData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.CREATOR,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(Persons.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.DATE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(DateEvents.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.DESCRIPTION,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(UntypedData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.FORMAT, new ArrayList<Class<? extends UntypedData>>(
				Arrays.asList(DataFormat.class, DirectoryMetaData.class, EmptyMetaData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.IDENTIFIER,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(Identifier.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.LANGUAGE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(EdalLanguage.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.PUBLISHER,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(LegalPerson.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.RELATION,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(IdentifierRelation.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.RIGHTS,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(UntypedData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.SIZE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(DataSize.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.SOURCE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(UntypedData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.SUBJECT,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(Subjects.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.TITLE,
				new ArrayList<Class<? extends UntypedData>>(Arrays.asList(UntypedData.class)));
		MetaData.ELEMENT_TYPE_MAP.put(EnumDublinCoreElements.TYPE, new ArrayList<Class<? extends UntypedData>>(
				Arrays.asList(DataType.class, DirectoryMetaData.class, EmptyMetaData.class)));

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constant for empty meta data values
	 */
	public static final UntypedData EMPTY = new EmptyMetaData();
	/**
	 * constant for unknown meta data values
	 */
	public static final UntypedData UNKNOWN = new UnknownMetaData();
	/**
	 * constant for directory meta data values
	 */
	public static final UntypedData DIRECTORY = new DirectoryMetaData();

	/**
	 * the store of all supported dublin core meta data
	 */
	protected EnumMap<? super EnumDublinCoreElements, UntypedData> metaDataValues;

	/**
	 * Construct empty MetaDataValues and initialize with default MetaDataTypes
	 */
	protected MetaData() {

		this.metaDataValues = this.constructEmptyMetaDataMap();
	}

	/**
	 * Clone the {@link MetaData} object.
	 * 
	 * @return the cloned {@link MetaData} object.
	 * @throws CloneNotSupportedException
	 *             if unable to clone.
	 */
	@Override
	public MetaData clone() throws CloneNotSupportedException {

		Constructor<? extends MetaData> cons = null;
		MetaData clone = null;
		try {
			cons = this.getClass().getConstructor((Class<?>[]) null);
			clone = cons.newInstance((Object[]) null);
		} catch (final Exception e) {
			throw new CloneNotSupportedException("unable to clone: can not load constructor for metadata object");
		}

		/**
		 * constructor create clone of an existing EnumMap but produce raw-types
		 * warning
		 * 
		 * <code>EnumMap<? super EnumDublinCoreElements, UntypedData> copy = new EnumMap(this.MetaDataValues);</code>
		 */

		final EnumMap<? super EnumDublinCoreElements, UntypedData> copy = this.constructEmptyMetaDataMap();

		for (final Map.Entry<? super EnumDublinCoreElements, UntypedData> entry : this.metaDataValues.entrySet()) {

			copy.put((EnumDublinCoreElements) entry.getKey(), entry.getValue());

		}

		clone.metaDataValues = copy;

		return clone;
	}

	/**
	 * generate a Map of {@link MetaData} initialized with default values
	 * 
	 * @return an empty {@link MetaData} map.
	 */
	private EnumMap<? super EnumDublinCoreElements, UntypedData> constructEmptyMetaDataMap() {
		EnumMap<? super EnumDublinCoreElements, UntypedData> newmap = new EnumMap<>(EnumDublinCoreElements.class);
		newmap = new EnumMap<>(EnumDublinCoreElements.class);

		newmap.put(EnumDublinCoreElements.CHECKSUM, new CheckSum());
		newmap.put(EnumDublinCoreElements.CONTRIBUTOR, new Persons());
		newmap.put(EnumDublinCoreElements.COVERAGE, new UntypedData());
		newmap.put(EnumDublinCoreElements.CREATOR, new Persons());
		newmap.put(EnumDublinCoreElements.DATE, new DateEvents(""));
		newmap.put(EnumDublinCoreElements.DESCRIPTION, new UntypedData());
		newmap.put(EnumDublinCoreElements.FORMAT, new DataFormat());
		newmap.put(EnumDublinCoreElements.IDENTIFIER, new Identifier());
		newmap.put(EnumDublinCoreElements.LANGUAGE, new EdalLanguage(Locale.getDefault()));
		newmap.put(EnumDublinCoreElements.PUBLISHER, new LegalPerson("", "", "", ""));
		newmap.put(EnumDublinCoreElements.RELATION, new IdentifierRelation());
		newmap.put(EnumDublinCoreElements.RIGHTS, new UntypedData(UntypedData.ALL_RIGHTS_RESERVED));
		newmap.put(EnumDublinCoreElements.SIZE, new DataSize());
		newmap.put(EnumDublinCoreElements.SOURCE, new UntypedData());
		newmap.put(EnumDublinCoreElements.SUBJECT, new Subjects());
		newmap.put(EnumDublinCoreElements.TITLE, new UntypedData("Default-TITLE"));
		newmap.put(EnumDublinCoreElements.TYPE, new DataType(EnumDCMIDataType.TEXT));
		return newmap;

	}

	@Override
	public boolean equals(final Object object) {

		if (object instanceof MetaData) {

			final MetaData otherMetaData = (MetaData) object;

			for (final Map.Entry<? super EnumDublinCoreElements, UntypedData> entry : this.metaDataValues.entrySet()) {

				try {
					if (entry.getValue().compareTo(otherMetaData
							.getElementValue(EnumDublinCoreElements.valueOf(entry.getKey().toString()))) == 0) {

					} else {
						return false;
					}
				} catch (final MetaDataException e) {
					DataManager.getImplProv().getLogger().error(e);
				}
			}
		}
		return true;
	}

	/**
	 * Getter for a value of an element of the {@link MetaData} object.
	 * 
	 * @param element
	 *            the element as {@link EnumDublinCoreElements} enum.
	 * @param <T>
	 *            the {@link UntypedData} type
	 * @return the value of a meta data element.
	 * @throws MetaDataException
	 *             if unable to load a value.
	 */
	public <T extends UntypedData> T getElementValue(final Enum<? extends EnumDublinCoreElements> element)
			throws MetaDataException {
		if (!this.metaDataValues.containsKey(element)) {
			throw new MetaDataException("no value for metadata element " + element);
		} else {
			T ret = null;
			try {
				ret = (T) this.metaDataValues.get(element);
			} catch (final ClassCastException ex) {
				throw new MetaDataException(ex);
			}
			return ret;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.metaDataValues == null ? 0 : this.metaDataValues.hashCode());
		return result;
	}

	/**
	 * Modify the value of a meta data element
	 * 
	 * @param key
	 *            the element to set.
	 * @param value
	 *            the new value for the element.
	 * @throws MetaDataException
	 *             if unable to set meta data element.
	 */
	public void setElementValue(final EnumDublinCoreElements key, final UntypedData value) throws MetaDataException {

		final List<Class<? extends UntypedData>> datatype = MetaData.ELEMENT_TYPE_MAP.get(key);

		if (!datatype.contains(value.getClass())) {
			throw new MetaDataException("It is not allowed to set a '" + value.getClass().getSimpleName()
					+ "' datatype for the element '" + key + "'");
		}
		try {
			this.metaDataValues.put(key, value);
		} catch (final Exception e) {
			throw new MetaDataException("unable to set metadata element : " + e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		// final int MaxStringLength = 43;
		final EnumMap<? super EnumDublinCoreElements, UntypedData> data = this.metaDataValues;
		final StringBuffer sb = new StringBuffer();
		final Persons persons = (Persons) data.get(EnumDublinCoreElements.CREATOR);
		Person creator = null;
		NaturalPerson creator_natural = null;
		LegalPerson creator_legal = null;

		if (persons.size() > 0) {
			creator = (Person) persons.toArray()[0];
			if (creator instanceof NaturalPerson) {
				creator_natural = (NaturalPerson) creator;
			} else {
				creator_legal = (LegalPerson) creator;
			}
		}
		final UntypedData title = data.get(EnumDublinCoreElements.TITLE);
		final DateEvents de = (DateEvents) data.get(EnumDublinCoreElements.DATE);
		final EdalDate date = de.isEmpty() ? null : (EdalDate) de.toArray()[0];

		data.get(EnumDublinCoreElements.FORMAT);

		sb.append(creator_natural != null
				? (creator_natural.getGivenName().length() > 0 ? creator_natural.getGivenName().substring(0, 1) + ". "
						: "")
						+ (creator_natural.getSureName().length() > 0
								? creator_natural.getSureName() + (persons.size() > 1 ? " et al." : "")
								: MetaData.UNKNOWN_STRING)
				: creator_legal != null ? creator_legal.getLegalName().length() > 0 ? creator_legal.getLegalName()
						: MetaData.UNKNOWN_STRING : MetaData.UNKNOWN_STRING);
		sb.append(" (");
		if (date != null) {
			sb.append(String.format("%tF", date.getStartDate()));
		} else {
			sb.append("null");
		}
		sb.append("): ");

		// sb.append(title.toString().length() > MaxStringLength ?
		// String.format(
		// "%1$." + (MaxStringLength - 4) + "s ...", title) : title);
		sb.append(title.toString());
		if (!title.toString().endsWith(".")) {
			sb.append(". ");
		} else {
			sb.append(" ");
		}

		final DataSize size = (DataSize) data.get(EnumDublinCoreElements.SIZE);
		if (!(data.get(EnumDublinCoreElements.TYPE) instanceof DirectoryMetaData)) {
			sb.append("Size: " + size);
		}

		return sb.toString();
	}

}