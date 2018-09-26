/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

public class ElixirCallBackHandler implements CallbackHandler {

	static {

		try {
			Class.forName("javafx.embed.swing.JFXPanel");
			Class.forName("javafx.application.Platform");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Please use an Oracle_JRE to run this module", "No Oracle_JRE found",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		PropertyConfigurator.configure(EdalConfiguration.class.getResource("log4j.properties"));
	}

	private static final int LOCALHOST_HTTP_PORT = 6789;

	private static String username = null;
	private static Server server = null;

	private final static String CLIENT_ID = "YzEyOThmNzctZWEzNC00NDJiLTgyNDQtNGE2YTE0OGQyZGMx";
	private final static String CLIENT_SECRET = "QU9sSk5IcWZfLV84NEEwVTd1MkI0LWJ5V0N0djBjdGVhTVN1Q3Brd2RPRkp4TUZ4YkFyOWZaS241WjV5RjNEdzA3MmZNeGt0R0thVTBib3VLU2lxYnJF";
	private final static String REDIRECT_URI = "http://localhost:" + LOCALHOST_HTTP_PORT + "/callback";

	public ElixirCallBackHandler() {
		super();
	}

	public ElixirCallBackHandler(final String httpProxyHost, final int httpProxyPort) {
		super();

		try {

			if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

				System.setProperty("http.proxyHost", httpProxyHost);
				System.setProperty("http.proxyPort", String.valueOf(httpProxyPort));
				System.setProperty("https.proxyHost", httpProxyHost);
				System.setProperty("https.proxyPort", String.valueOf(httpProxyPort));
			}
			
			ElixirSwingBrowserDialogWithCookies browser = new ElixirSwingBrowserDialogWithCookies(null,
					"https://login.elixir-czech.org/oidc/authorize?" + "&response_type=code"
							+ "&scope=email%20profile%20openid" + "&client_id="
							+ new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8") + "&redirect_uri="
							+ REDIRECT_URI);

			initServer(httpProxyHost, httpProxyPort, browser);

			browser.setVisible(true);

			server.stop();

		} catch (Exception e) {
			e.printStackTrace();
		}

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

		for (final Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				final NameCallback nc = (NameCallback) callback;
				nc.setName(username);
			}
		}
	}

	private void initServer(final String httpProxyHost, final int httpProxyPort, ElixirSwingBrowserDialogWithCookies browser)
			throws Exception {

		if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

			System.setProperty("http.proxyHost", httpProxyHost);
			System.setProperty("http.proxyPort", String.valueOf(httpProxyPort));
			System.setProperty("https.proxyHost", httpProxyHost);
			System.setProperty("https.proxyPort", String.valueOf(httpProxyPort));
		}
		server = new Server(LOCALHOST_HTTP_PORT);

		ContextHandler contextHandler = new ContextHandler("/callback");

		contextHandler.setHandler(new MyHandler(httpProxyHost, httpProxyPort, browser));

		server.setHandler(contextHandler);

		server.start();
	}

	private static class MyHandler extends AbstractHandler {

		private String httpProxyHost;
		private int httpProxyPort;
		private ElixirSwingBrowserDialogWithCookies browser;

		public MyHandler(String httpProxyHost, int httpProxyPort, ElixirSwingBrowserDialogWithCookies browser) {
			this.httpProxyHost = httpProxyHost;
			this.httpProxyPort = httpProxyPort;
			this.browser = browser;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			CloseableHttpClient httpclient;
					
			String code = request.getParameter("code");
			try {

				if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

					httpclient = HttpClientBuilder.create().setProxy(new HttpHost(httpProxyHost, httpProxyPort))
							.setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				} else {
					httpclient = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				}

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("client_id", new String(Base64.getDecoder().decode(CLIENT_ID))));
				data.add(new BasicNameValuePair("client_secret", new String(Base64.getDecoder().decode(CLIENT_SECRET))));
				data.add(new BasicNameValuePair("grant_type", "authorization_code"));
				data.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
				data.add(new BasicNameValuePair("code", code));
				
				HttpPost httpPost = new HttpPost("https://login.elixir-czech.org/oidc/token");
			
				httpPost.setEntity(new UrlEncodedFormEntity(data));
								
				CloseableHttpResponse responseForAuth = httpclient.execute(httpPost);


				if (responseForAuth.getStatusLine().getStatusCode() == HttpStatus.OK_200) {

					String resultForAuthentication = EntityUtils.toString(responseForAuth.getEntity());
					
					String access_token = ((JSONObject) new JSONParser().parse(resultForAuthentication)).get("access_token").toString();

					String resultOfUserinformationRequest = Client.create()
							.resource("https://login.elixir-czech.org/oidc/userinfo")
							.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token).get(String.class);

					JSONObject jsonobj = (JSONObject) new JSONParser().parse(resultOfUserinformationRequest);

					String fullname = jsonobj.get("name").toString();
					String email = jsonobj.get("email").toString();

					String test = "<html>" + "<head></head>" + "<body>" + "Hello " + fullname
							+ " your email address is [" + email
							+ "] and <br/>this window will be closed automatically after 3 seconds</body>" + "</html>";

					response.setStatus(HttpStatus.OK_200);
					response.setContentType("text/html");

					final OutputStream responseBody = response.getOutputStream();
					responseBody.write(test.getBytes());
					responseBody.close();

					username = email;
					/** wait 3 seconds before closing the browser window **/
					Thread.sleep(3000);

					this.browser.dispose();
					this.browser = null;

				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}
	}

}
