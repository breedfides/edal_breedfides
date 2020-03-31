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

import de.ipk_gatersleben.bit.bi.edal.primary_data.login.SamplePrincipal;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class JKIAuthenticationProcess extends AuthenticationProcess {

	public JKIAuthenticationProcess() {

		try {
			Subject tmpJKISubject = EdalHelpers.authenticateJKIKerberosUser(null);

			for (Principal principal : tmpJKISubject.getPrincipals()) {

				String name = principal.getName();

				String email = name.subSequence(0, name.indexOf("@")).toString() + "@julius-kuehn.de";

				Subject newSubject = new Subject();
				newSubject.getPrincipals().add(new SamplePrincipal(email));

				this.setSubject(newSubject);
				break;
			}
		} catch (EdalAuthenticateException e) {
			e.printStackTrace();
		}
	}
}