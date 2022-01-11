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

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GoogleCallBackHandler implements CallbackHandler {

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

	private final static String CLIENT_ID = "NTUxMDE2MzE3NzQyLWoycDZjcTBzcmRvMTBiYWtoYTRkNHFrOWM5b3EwdjdzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29t";
	private final static String CLIENT_SECRET = "Q0pYVGJLSTVSWllCZFN4Qk81MW5xamVr";
	private final static String REDIRECT_URI = "http://localhost:" + LOCALHOST_HTTP_PORT + "/oauthpath?op=google";

	public GoogleCallBackHandler() {
		super();
	}

	public GoogleCallBackHandler(final String httpProxyHost, final int httpProxyPort) {
		super();

		try {

			SwingBrowserDialog browser = new SwingBrowserDialog(null,
					"https://accounts.google.com/o/oauth2/auth?client_id="
							+ new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8")
							+ "&scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile&redirect_uri="
							+ REDIRECT_URI + "&response_type=code",
					false, 550, 650);

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

		public MyHandler(String httpProxyHost, int httpProxyPort, SwingBrowserDialog browser) {
			this.httpProxyHost = httpProxyHost;
			this.httpProxyPort = httpProxyPort;
			this.browser = browser;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			String code = request.getParameter("code");
			try {

				CloseableHttpClient httpclient;

				if (httpProxyHost != null && !httpProxyHost.isEmpty()) {

					httpclient = HttpClientBuilder.create().setProxy(new HttpHost(httpProxyHost, httpProxyPort))
							.setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				} else {
					httpclient = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore())
							.setRedirectStrategy(new LaxRedirectStrategy()).build();
				}

				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("code", code));
				nvps.add(new BasicNameValuePair("client_id",
						new String(Base64.getDecoder().decode(CLIENT_ID), "UTF-8")));
				nvps.add(new BasicNameValuePair("client_secret",
						new String(Base64.getDecoder().decode(CLIENT_SECRET), "UTF-8")));
				nvps.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
				nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));

				HttpPost data = new HttpPost("https://accounts.google.com/o/oauth2/token");

				data.setEntity(new UrlEncodedFormEntity(nvps));

				CloseableHttpResponse httpPostResponse = httpclient.execute(data);

				JSONParser parser = new JSONParser();

				JSONObject jsonobj = (JSONObject) parser.parse(EntityUtils.toString(httpPostResponse.getEntity()));

				String access_token = jsonobj.get("access_token").toString();

				HttpGet httpGet = new HttpGet(
						"https://www.googleapis.com/plus/v1/people/me?access_token=" + access_token);

				CloseableHttpResponse httpGetResponse = httpclient.execute(httpGet);

				JSONObject jsonobj2 = (JSONObject) parser.parse(EntityUtils.toString(httpGetResponse.getEntity()));

				String emailaddress = null;

				if (jsonobj2.get("emails") != null) {
					emailaddress = ((JSONObject) (((JSONArray) (jsonobj2.get("emails"))).get(0))).get("value")
							.toString();
				}

				String test = "<html>" + "<head></head>" + "<body>" + "Hello " + emailaddress
						+ ", this window will be closed automatically after 3 seconds</body>" + "</html>";

				response.setStatus(HttpStatus.OK_200);
				response.setContentType("text/html");

				final OutputStream responseBody = response.getOutputStream();
				responseBody.write(test.getBytes());
				responseBody.close();

				username = emailaddress;
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
