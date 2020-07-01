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

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;

/**
 * Interface to define the functions of an identifier system.
 * 
 * @author arendd
 */
public interface Referenceable {

	/**
	 * Accept the Approval of the {@link PublicReference}.
	 * 
	 * @param publicReference
	 *            the {@link PublicReference} to accept.
	 * @return the generated identifier
	 * 
	 * @throws ReferenceableException
	 *             if unable to accept the request.
	 */
	String acceptApprovalRequest(PublicReference publicReference) throws ReferenceableException;

	/**
	 * Reject the Approval of the {@link PublicReference}.
	 * 
	 * @param publicReference
	 *            the {@link PublicReference} to reject.
	 * @throws ReferenceableException
	 *             if unable to reject the request.
	 */
	void rejectApprovalRequest(PublicReference publicReference) throws ReferenceableException;

	/**
	 * Validate the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} schema
	 * of the {@link PrimaryDataEntityVersion} if it is valid for this
	 * {@link PersistentIdentifier} type.
	 * 
	 * @param entityVersion
	 *            the {@link PrimaryDataEntityVersion} to check the
	 *            {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData}
	 *            .
	 * @throws EdalPublicationMetaDataException
	 *             if validation failed.
	 */
	void validateMetaData(PrimaryDataEntityVersion entityVersion) throws EdalPublicationMetaDataException;

	StringBuffer negotiateContent(PublicReference publicReference, ContentNegotiationType type);

}