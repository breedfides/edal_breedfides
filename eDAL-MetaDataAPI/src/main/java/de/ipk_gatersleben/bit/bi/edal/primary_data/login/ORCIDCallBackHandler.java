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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

				JerseyClient client = JerseyClientBuilder.createClient();

				WebTarget resource = client.target("https://pub.orcid.org/v2.0/" + orcid + "/email");

				final Response response = resource.request("application/vnd.orcid+xml ")
						.header("Authorization", "Bearer " + accessToken).get();

				if (response.getStatus() == 200) {

					String result = response.readEntity(String.class);

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
					client.close();
					throw new Exception("request failed");
				}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}

		private void requestNewToken() throws IOException {
			JerseyClient client = JerseyClientBuilder.createClient();

			Form input = new Form();
			input.param("client_id", new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8"));
			input.param("client_secret", new String(Base64.getDecoder().decode(CLIENT_SECRET), "UTF-8"));
			input.param("scope", "/read-public");
			input.param("grant_type", "client_credentials");

			WebTarget resource = client.target("https://pub.orcid.org/oauth/token");

			final Response response = resource.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED));

			if (response.getStatus() == 200) {
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) new JSONParser().parse((String) response.readEntity(String.class));
				} catch (ParseException e) {
					throw new IOException("Parsing of user token failed: " + e);
				}

				if (jsonObject.containsKey("access_token")) {
					accessToken = (String) jsonObject.get("access_token");
				}
			} else {
				client.close();
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
