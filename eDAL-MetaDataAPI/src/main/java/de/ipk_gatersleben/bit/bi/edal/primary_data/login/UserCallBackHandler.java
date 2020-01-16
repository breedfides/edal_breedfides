/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * The application implements the CallbackHandler.
 * 
 * This application is text-based. Therefore it displays information to the user
 * using the OutputStreams System.out and System.err, and gathers input from the
 * user using the InputStream System.in.
 */
public class UserCallBackHandler implements CallbackHandler {

	private String name = "test";
	private char[] password = { 't', 'e', 's', 't' };

	public UserCallBackHandler(String name, String password) {
		this.name = name;
		this.password = password.toCharArray();

	}

	/**
	 * Invoke an array of Callbacks.
	 * 
	 * @param callbacks
	 *            an array of <code>Callback</code> objects which contain the
	 *            information requested by an underlying security service to be
	 *            retrieved or displayed.
	 * 
	 * @exception IOException
	 *                if an input or output error occurs.
	 * 
	 * @exception UnsupportedCallbackException
	 *                if the implementation of this method does not support one
	 *                or more of the Callbacks specified in the
	 *                <code>callbacks</code> parameter.
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof TextOutputCallback) {

				/** display the message according to the specified type */
				TextOutputCallback toc = (TextOutputCallback) callbacks[i];
				switch (toc.getMessageType()) {
				case TextOutputCallback.INFORMATION:
					System.out.println(toc.getMessage());
					break;
				case TextOutputCallback.ERROR:
					System.out.println("ERROR: " + toc.getMessage());
					break;
				case TextOutputCallback.WARNING:
					System.out.println("WARNING: " + toc.getMessage());
					break;
				default:
					throw new IOException("Unsupported message type: "
							+ toc.getMessageType());
				}

			} else if (callbacks[i] instanceof NameCallback) {

				/** prompt the user for a username */
				NameCallback nc = (NameCallback) callbacks[i];

				System.err.print(nc.getPrompt());
				System.err.flush();
				nc.setName(readName());

			} else if (callbacks[i] instanceof PasswordCallback) {

				/** prompt the user for sensitive information */
				PasswordCallback pc = (PasswordCallback) callbacks[i];
				System.err.print(pc.getPrompt());
				System.err.flush();
				pc.setPassword(readPassword());

			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}

	private char[] readPassword() {
		return this.password;
	}

	private String readName() {
		return this.name;
	}
}