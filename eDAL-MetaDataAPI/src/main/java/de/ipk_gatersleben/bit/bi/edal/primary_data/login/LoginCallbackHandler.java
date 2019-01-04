/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Password callback handler for resolving password/usernames for a JAAS login.
 * 
 * @author arendd
 */
public class LoginCallbackHandler implements CallbackHandler {

	private String password;

	private String username;

	private boolean alreadySetted = false;

	public LoginCallbackHandler() {
		super();
	}

	public LoginCallbackHandler(final String name) {
		super();
		this.username = name;

	}

	public LoginCallbackHandler(final String name, final String password) {
		super();
		this.username = name;
		this.password = password;

	}

	@SuppressWarnings("unused")
	private String getConsoleString(final String prompt) {
		System.out.print(prompt);
		final Scanner in = new Scanner(System.in);

		// Reads a single line from the console
		// and stores into name variable

		final String name = in.nextLine();

		// Reads a integer from the console
		// and stores into age variable

		in.close();
		return name;
	}

	/**
	 * Handles the callbacks, and sets the user/password detail.
	 * 
	 * @param callbacks
	 *            the callbacks to handle
	 * @throws IOException
	 *             if an input or output error occurs.
	 */
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		if (!alreadySetted) {

			LoginDialog loginDlg = new LoginDialog(null, "Login to IPK-Domain (LDAP)", this.username);
			loginDlg.setVisible(true);

			if (loginDlg.getStatus() == LoginDialog.TRY_LOGIN) {
				this.username = loginDlg.getUsername();
				this.password = loginDlg.getPassword();

				alreadySetted = true;
			}
			if (loginDlg.getStatus() == LoginDialog.ABORT) {
				alreadySetted = true;

			}

		}
		for (final Callback callback : callbacks) {
			if (callback instanceof NameCallback) {

				final NameCallback nc = (NameCallback) callback;

				nc.setName(this.username);

			} else if (callback instanceof PasswordCallback) {

				final PasswordCallback pc = (PasswordCallback) callback;

				pc.setPassword(this.password.toCharArray());
			}
		}

	}

}
