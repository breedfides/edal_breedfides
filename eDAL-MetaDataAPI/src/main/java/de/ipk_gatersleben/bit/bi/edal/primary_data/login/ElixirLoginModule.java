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
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class ElixirLoginModule implements LoginModule {

	/** initial state */
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map<String, ?> sharedState;
	private Map<String, ?> options;

	/** the authentication status */
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	/** username and password */
	private String username;
	private char[] password;

	/** ElixirPrincipal */
	private ElixirPrincipal principal;

	/**
	 * Initialize this <code>LoginModule</code>.
	 * 
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the end
	 *            user (prompting for user names and passwords, for example).
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 * @param options
	 *            options specified in the login <code>Configuration</code> for
	 *            this particular <code>LoginModule</code>.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.setSharedState(sharedState);
		this.setOptions(options);

	}

	/**
	 * Authenticate the user by prompting for a user name and password.
	 * 
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	public boolean login() throws LoginException {

		/** prompt for a user name and password */
		if (callbackHandler == null) {
			throw new LoginException(
					"Error: no CallbackHandler available " + "to get authentication information from the user");
		}

		Callback[] callbacks = new Callback[1];
		try {
			callbacks[0] = new NameCallback(" ");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();

		} catch (IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: " + uce.getCallback().toString()
					+ " not available to garner authentication information " + "from the user");
		}

		succeeded = true;
		return true;
	}

	/**
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded).
	 * 
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method),
	 * then this method associates a <code>ElixirPrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 * 
	 * @exception LoginException
	 *                if the commit fails.
	 * 
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	public boolean commit() throws LoginException {
		if (!succeeded) {
			return false;
		} else {

			/** add a principal (authenticated identity) to the Subject */
			principal = new ElixirPrincipal(username);

			if (!subject.getPrincipals().contains(principal)) {
				subject.getPrincipals().add(principal);
			}

			/** in any case, clean out state */
			username = null;

			commitSucceeded = true;
			return true;
		}
	}

	/**
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed).
	 * 
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that
	 * was originally saved.
	 * 
	 * @exception LoginException
	 *                if the abort fails.
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	public boolean abort() throws LoginException {
		if (!succeeded) {
			return false;
		} else if (succeeded && !commitSucceeded) {
			/** login succeeded but overall authentication failed */
			succeeded = false;
			username = null;
			if (password != null) {
				for (int i = 0; i < password.length; i++) {
					password[i] = ' ';
				}
				password = null;
			}
			principal = null;
		} else {
			/**
			 * overall authentication succeeded and commit succeeded, but
			 * someone else's commit failed
			 */
			logout();
		}
		return true;
	}

	/**
	 * Logout the user.
	 * 
	 * This method removes the <code>OAuthPrincipal</code> that was added by the
	 * <code>commit</code> method.
	 * 
	 * @exception LoginException
	 *                if the logout fails.
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 */
	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(principal);
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++) {
				password[i] = ' ';
			}
			password = null;
		}
		principal = null;
		return true;
	}

	/**
	 * @return the sharedState
	 */
	public Map<String, ?> getSharedState() {
		return sharedState;
	}

	/**
	 * @param sharedState
	 *            the sharedState to set
	 */
	public void setSharedState(Map<String, ?> sharedState) {
		this.sharedState = sharedState;
	}

	/**
	 * @return the options
	 */
	public Map<String, ?> getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Map<String, ?> options) {
		this.options = options;
	}
}