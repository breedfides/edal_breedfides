package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class SimpleCallbackHandler implements CallbackHandler{
	final String username;

	public SimpleCallbackHandler(String username) {
		this.username = username;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (final Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				final NameCallback nc = (NameCallback) callback;
				nc.setName(username);
			}
		}
	}
}
