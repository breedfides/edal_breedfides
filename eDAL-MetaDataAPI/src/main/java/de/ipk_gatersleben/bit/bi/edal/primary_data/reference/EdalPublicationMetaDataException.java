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
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference;

/**
 * Exception is thrown on failures of the check of the
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} for a
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}
 * object.
 * 
 * @author arendd
 */
public class EdalPublicationMetaDataException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} without a
	 * specific message.
	 */
	public EdalPublicationMetaDataException() {
		super();
	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public EdalPublicationMetaDataException(final String message) {
		super(message);
	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * message and cause.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause.
	 */
	public EdalPublicationMetaDataException(final String message,
			final Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor for {@link EdalPublicationMetaDataException} with a specific
	 * cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public EdalPublicationMetaDataException(final Throwable cause) {
		super(cause);
	}
}
