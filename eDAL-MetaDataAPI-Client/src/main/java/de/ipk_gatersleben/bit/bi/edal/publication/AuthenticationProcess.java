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
