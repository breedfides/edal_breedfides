/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
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
