/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;

/**
 * Data type to manage MIME types.
 * <p>
 * For MIME type definition, we make use of the {@link Tika} library package.
 * 
 * @author lange
 * @author arendd
 */
public class DataFormat extends UntypedData {

	private String mimeType;

	private static DefaultDetector detector;

	/**
	 * generated serial ID
	 */
	private static final long serialVersionUID = 2100380388922795210L;
	/**
	 * The unknown format
	 */
	public static final DataFormat UNKNOWN_FORMAT;
	static {

		UNKNOWN_FORMAT = new DataFormat();

		detector = new DefaultDetector();

	}

	/**
	 * construct a DataFormat object with unknown type
	 */
	public DataFormat() {
		super();
		this.mimeType = MimeTypes.OCTET_STREAM;
	}

	/**
	 * Constructor for DataFormat.
	 * 
	 * @param mimeType
	 *            the data format as MIME type in the format
	 * 
	 *            <pre>
	 * major / minor
	 * </pre>
	 *            <p>
	 *            E.g. unknown is:
	 * 
	 *            <pre>
	 * application / octet - stream
	 * </pre>
	 * @throws DataFormatException
	 *             if using a non valid MIME type.
	 */
	public DataFormat(final String mimeType) throws DataFormatException {

		this();

		if (MimeType.isValid(mimeType)) {
			this.mimeType = mimeType;
		} else {
			throw new DataFormatException("unknown data format \"" + mimeType + "\". please use a valid mime type in the format major/minor. E.g.\"" + MimeTypes.OCTET_STREAM + "\"");
		}

	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof DataFormat) {

			DataFormat dataFormat = (DataFormat) datatype;

			if (this.getMimeType().compareTo(dataFormat.getMimeType()) == 0) {
				return super.compareTo(datatype);
			} else {
				return this.getMimeType().compareTo(dataFormat.getMimeType());
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	/**
	 * Guess the DataFormat for a given file
	 * 
	 * @param input
	 *            a {@link java.io.InputStream} object.
	 * @return the guessed DataFormat object; if format is not recognized then
	 *         the return {@link DataFormat#UNKNOWN_FORMAT}
	 */
	public static DataFormat guessDataFormat(final InputStream input) {

		try {
			return new DataFormat(detector.detect(TikaInputStream.get(input), new Metadata()).getBaseType().toString());
		} catch (DataFormatException | IOException e) {
			e.printStackTrace();
			return DataFormat.UNKNOWN_FORMAT;
		}

	}

	/**
	 * Getter for the field <code>mimeType</code>.
	 * 
	 * @return the mimeType
	 */
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

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.mimeType;
	}
}
