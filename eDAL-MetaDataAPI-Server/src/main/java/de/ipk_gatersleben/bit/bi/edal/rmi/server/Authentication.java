/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.server;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;

/**
 * Container to hold all necessary authentication token, i.e. login and password
 * 
 * @author arendd
 */
public class Authentication implements Serializable {

    private static final long serialVersionUID = 1L;

    private Subject subject = null;

    private String name;
    private String password;

    public Authentication(final Subject subject)
	    throws EdalAuthenticateException {

	if (subject == null) {
	    throw new EdalAuthenticateException(
		    "Its not allowed to create an Authentication object with a null Subject");
	}
	this.subject = subject;
    }

    /**
     * @return the login name
     */
    public String getName() {

	for (final Principal principal : this.subject.getPrincipals()) {
	    this.name = principal.getName();
	    break;
	}

	return this.name;
    }

    /**
     * @return the password
     */
    public String getPassword() {

	return this.password;
    }

    /**
     * @return the subject
     */
    public Subject getSubject() {
	return this.subject;
    }

}
