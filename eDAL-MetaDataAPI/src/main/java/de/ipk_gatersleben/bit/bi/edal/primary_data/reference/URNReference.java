/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

public class URNReference implements Referenceable {

	@Override
	public String acceptApprovalRequest(PublicReference publicReference) throws ReferenceableException {
		// TODO Auto-generated method stub
		return null;
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