/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

public class AuthenticationProcess {

	private Subject subject = null;

	private String username = "";

	public AuthenticationProcess() {

	}

	/**
	 * @return the subject
	 * @throws EdalException
	 *             if unable to get {@link Subject}
	 */
	public Subject getSubject() throws EdalException {
		if (this.subject != null) {
			return this.subject;
		} else {
			throw new EdalException("No subject authenticated");
		}
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	protected void setSubject(Subject subject) {
		this.subject = subject;
	}

	/**
	 * @return the username
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	protected void setUsername(String username) {
		this.username = username;
	}
}
