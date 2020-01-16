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

import java.net.InetSocketAddress;
import java.security.Principal;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class GoogleAuthenticationProcess extends AuthenticationProcess {
	public GoogleAuthenticationProcess() {

		InetSocketAddress proxy = EdalConfiguration.guessProxySettings();

		if (proxy != null) {

			Subject subject = null;
			try {
				subject = EdalHelpers.authenticateGoogleUser(proxy.getHostName(), proxy.getPort());

				if (subject != null) {
					this.setSubject(subject);

					for (Principal princpal : subject.getPrincipals()) {
						this.setUsername(princpal.getName());
						break;
					}
				}
			} catch (EdalAuthenticateException e) {
				this.setUsername("");
			}
		} else {
			Subject subject = null;
			try {
				subject = EdalHelpers.authenticateGoogleUser();
				if (subject != null) {
					this.setSubject(subject);

					for (Principal princpal : subject.getPrincipals()) {
						this.setUsername(princpal.getName());
						break;
					}
				}
			} catch (EdalAuthenticateException e) {
				this.setUsername("");
			}
		}

	}
}
