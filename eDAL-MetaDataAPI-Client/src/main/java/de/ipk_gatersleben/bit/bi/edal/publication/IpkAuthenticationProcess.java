/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.security.Principal;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class IpkAuthenticationProcess extends AuthenticationProcess {

	public IpkAuthenticationProcess() throws EdalException {

		Subject winOrUnixSubject = null;
		try {
			winOrUnixSubject = EdalHelpers.authenticateWinOrUnixOrMacUser();
		} catch (EdalAuthenticateException e) {
			this.setUsername("");
		}

		if (winOrUnixSubject != null) {
			for (Principal princpal : winOrUnixSubject.getPrincipals()) {
				this.setUsername(princpal.getName());
				break;
			}
		}

		try {
			Subject subject = EdalHelpers.authenticateIPKKerberosUser(this.getUsername());
			if (subject != null) {
				this.setSubject(subject);
			} else {
				throw new EdalException("Authentication failed");
			}
		} catch (EdalAuthenticateException e) {
			throw new EdalException("Authentication failed");
		}

	}

}
