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