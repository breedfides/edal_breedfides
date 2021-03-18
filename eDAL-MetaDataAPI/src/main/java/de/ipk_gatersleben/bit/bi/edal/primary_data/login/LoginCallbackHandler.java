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

	private String domainname;

	private boolean alreadySetted = false;

	public LoginCallbackHandler() {
		super();
	}

	public LoginCallbackHandler(final String name, final String domainname) {
		super();
		this.username = name;
		this.domainname = domainname;

	}

	public LoginCallbackHandler(final String name, final String password, final String domainname) {
		super();
		this.username = name;
		this.password = password;
		this.domainname = domainname;
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
	 * @param callbacks the callbacks to handle
	 * @throws IOException if an input or output error occurs.
	 */
	public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		if (!alreadySetted) {

			LoginDialog loginDlg = new LoginDialog(null, "Login to " + this.domainname + "-Domain", this.username);
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
