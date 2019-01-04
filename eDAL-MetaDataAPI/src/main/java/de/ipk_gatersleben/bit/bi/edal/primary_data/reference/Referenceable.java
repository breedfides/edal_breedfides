/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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