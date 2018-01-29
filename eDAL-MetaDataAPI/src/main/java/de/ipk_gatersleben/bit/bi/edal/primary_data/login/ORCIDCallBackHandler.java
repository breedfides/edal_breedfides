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
import java.io.StringReader;
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
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;

public class ORCIDCallBackHandler implements CallbackHandler {

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
	private static String userEmail = null;
	private static String orcid = null;

	private static final String CLIENT_ID = "QVBQLU9ONEgwSUcwWjYyQUJRUUI=";
	private static final String CLIENT_SECRET = "MjU3MDhjNjItZGI1Ny00NTBlLThkMmYtYjk1ZmQ3OTYzMTli";
	private static final String REDIRECT_URI = "http://localhost:" + LOCALHOST_HTTP_PORT + "/oauthpath";

	public ORCIDCallBackHandler() {
		super();
	}

	public ORCIDCallBackHandler(final String httpProxyHost, final int httpProxyPort) {
		super();

		try {

			SwingBrowserDialog browser = new SwingBrowserDialog(null,
					"https://orcid.org/oauth/authorize?client_id="
							+ new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8")
							+ "&response_type=code&scope=/authenticate&redirect_uri=" + REDIRECT_URI);

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

	private void initServer(final String httpProxyHost, final int httpProxyPort, SwingBrowserDialog browser)
			throws Exception {

		if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

			System.setProperty("http.proxyHost", httpProxyHost);
			System.setProperty("http.proxyPort", String.valueOf(httpProxyPort));
			System.setProperty("https.proxyHost", httpProxyHost);
			System.setProperty("https.proxyPort", String.valueOf(httpProxyPort));
		}
		server = new Server(LOCALHOST_HTTP_PORT);

		ContextHandler contextHandler = new ContextHandler("/oauthpath");

		contextHandler.setHandler(new MyHandler(httpProxyHost, httpProxyPort, browser));

		server.setHandler(contextHandler);

		server.start();
	}

	private static class MyHandler extends AbstractHandler {

		private String httpProxyHost;
		private int httpProxyPort;
		private SwingBrowserDialog browser;
		private String accessToken;

		public MyHandler(String httpProxyHost, int httpProxyPort, SwingBrowserDialog browser) {
			this.httpProxyHost = httpProxyHost;
			this.httpProxyPort = httpProxyPort;
			this.browser = browser;
		}

		private void getUserName(HttpServletRequest request) throws IOException {
			try {

				String code = request.getParameter("code");

				CloseableHttpClient httpclient;

				if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

					httpclient = HttpClientBuilder.create().setProxy(new HttpHost(httpProxyHost, httpProxyPort))
							.setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				} else {
					httpclient = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				}

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("code", code));
				data.add(new BasicNameValuePair("client_id",
						new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8")));
				data.add(new BasicNameValuePair("client_secret",
						new String(Base64.getDecoder().decode(CLIENT_SECRET), "UTF-8")));
				data.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
				data.add(new BasicNameValuePair("grant_type", "authorization_code"));

				HttpPost httpPost = new HttpPost("https://orcid.org/oauth/token");

				httpPost.setEntity(new UrlEncodedFormEntity(data));

				CloseableHttpResponse responseForAuth = httpclient.execute(httpPost);

				if (responseForAuth.getStatusLine().getStatusCode() == HttpStatus.OK_200) {

					String result = EntityUtils.toString(responseForAuth.getEntity());

					JSONObject jsonObject = (JSONObject) new JSONParser().parse(result);

					if (jsonObject.containsKey("name")) {
						String name = (String) jsonObject.get("name");
						if (jsonObject.containsKey("orcid")) {
							orcid = (String) jsonObject.get("orcid");
							username = name + " (" + orcid + ")";
						}
					}
				}
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}

		}

		private void getUserEmail() throws IOException {

			try {

				Client client = Client.create();

				WebResource resource = client.resource("https://pub.orcid.org/v2.0/" + orcid + "/email");

				final ClientResponse response = resource.type("application/vnd.orcid+xml ")
						.header("Authorization", "Bearer " + accessToken).get(ClientResponse.class);

				if (response.getStatus() == 200) {

					String result = response.getEntity(String.class);

					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse(new InputSource(new StringReader(result)));

					NodeList foundedEmails = document.getElementsByTagName("email:email");

					if (foundedEmails.getLength() <= 1) {
						throw new Exception("no registered email founded");
					} else if (foundedEmails.getLength() > 1) {

						userEmail = foundedEmails.item(0).getChildNodes().item(5).getTextContent();
					} else {
						throw new Exception("more than one email founded");
					}

				} else {
					throw new Exception("request failed");
				}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}

		private void requestNewToken() throws IOException {
			Client client = Client.create();

			Form input = new Form();
			input.add("client_id", new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8"));
			input.add("client_secret", new String(Base64.getDecoder().decode(CLIENT_SECRET), "UTF-8"));
			input.add("scope", "/read-public");
			input.add("grant_type", "client_credentials");

			WebResource resource = client.resource("https://pub.orcid.org/oauth/token");

			final ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);

			if (response.getStatus() == 200) {
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) new JSONParser().parse((String) response.getEntity(String.class));
				} catch (ClientHandlerException | UniformInterfaceException | ParseException e) {
					throw new IOException("Parsing of user token failed: " + e);
				}

				if (jsonObject.containsKey("access_token")) {
					accessToken = (String) jsonObject.get("access_token");
				}
			} else {
				throw new IOException("Request for access token failed");
			}
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			getUserName(request);

			requestNewToken();

			getUserEmail();

			try {

				String message = null;

				if (userEmail != null) {
					message = "<html>" + "<head></head>" + "<body>" + "Hello " + username + " - " + userEmail
							+ ",<br/> this window will be closed automatically after 3 seconds</body>" + "</html>";
				} else {
					message = "<html>" + "<head></head>" + "<body>" + "Hello " + username
							+ " unfortunately your email address is private"
							+ ",<br/> this window will be closed automatically after 3 seconds</body>" + "</html>";
				}

				response.setStatus(200);
				response.setContentType("text/html");

				final OutputStream responseBody = response.getOutputStream();
				responseBody.write(message.getBytes());
				responseBody.close();

				/** wait 3 seconds before closing the browser window **/
				Thread.sleep(3000);

				this.browser.dispose();
				this.browser = null;

			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}

	}

}
