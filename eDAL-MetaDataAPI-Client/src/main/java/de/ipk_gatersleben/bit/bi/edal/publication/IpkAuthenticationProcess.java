/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
