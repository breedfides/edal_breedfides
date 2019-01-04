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

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;

public class URLReference implements Referenceable {

	@Override
	public String acceptApprovalRequest(PublicReference publicReference) throws ReferenceableException {

		String url = null;
		try {
			url = DataManager.getImplProv().getApprovalServiceProvider().getDeclaredConstructor().newInstance().getNewURL(publicReference);
		} catch (ReflectiveOperationException | EdalApprovalException e) {
			throw new ReferenceableException("unable to get a new URL for the PublicReference");
		}
		return url;
	}

	@Override
	public void rejectApprovalRequest(PublicReference publicReference) throws ReferenceableException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateMetaData(PrimaryDataEntityVersion entityVersion) throws EdalPublicationMetaDataException {
		// TODO Auto-generated method stub

	}

	@Override
	public StringBuffer negotiateContent(PublicReference publicReference, ContentNegotiationType type) {
		// TODO Auto-generated method stub
		return null;
	}

}