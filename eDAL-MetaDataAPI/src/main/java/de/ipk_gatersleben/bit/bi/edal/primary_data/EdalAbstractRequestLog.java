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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.pathmap.PathMappings;
//import org.eclipse.jetty.server.AbstractNCSARequestLog;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CalculateDirectorySizeThread;

public abstract class EdalAbstractRequestLog extends AbstractLifeCycle implements RequestLog {

	protected static final Logger LOG = Log.getLogger(CustomRequestLog.class);

	// private static final List<String> FILTER_KEYWORD =
	// Arrays.asList("nagios-plugins", "Yahoo! Slurp", "Googlebot",
	// "bingbot", "Baiduspider", "zc.qq.com", "www.baidu.com", "www.alipay.com");

	private static final List<String> FILTER_KEYWORD = Arrays.asList("nagios-plugins");

	private static ThreadLocal<StringBuilder> _buffers = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder(256);
		}
	};

	private String[] _ignorePaths;
	private boolean _extended;
	private transient PathMappings<String> _ignorePathMap;
	private boolean _logLatency = false;
	private boolean _logCookies = false;
	private boolean _logServer = false;
	private boolean _preferProxiedForAddress;
	private transient DateCache _logDateCache;
	private String _logDateFormat = "dd/MM/yyyy:HH:mm:ss Z";
	private Locale _logLocale = Locale.getDefault();
	private String _logTimeZone = "GMT";

	/* ------------------------------------------------------------ */

	/**
	 * Is logging enabled
	 * 
	 * @return true if is enabled
	 */
	protected abstract boolean isEnabled();

	/* ------------------------------------------------------------ */

	/**
	 * Write requestEntry out. (to disk or slf4j log)
	 * 
	 * @param requestEntry
	 *            the request entry string
	 * 
	 * @throws IOException
	 *             if failed to write
	 */
	public abstract void write(String requestEntry) throws IOException;

	/* ------------------------------------------------------------ */

	/**
	 * Writes the request and response information to the output stream.
	 *
	 * @see org.eclipse.jetty.server.RequestLog#log(org.eclipse.jetty.server.Request,
	 *      org.eclipse.jetty.server.Response)
	 */
	@Override
	public void log(Request request, Response response) {

		// System.out.println("DOING LOG");

		try {
			if (_ignorePathMap != null && _ignorePathMap.getMatch(request.getRequestURI()) != null)
				return;

			if (!isEnabled())
				return;

			for (String keyword : FILTER_KEYWORD) {

				if (request.getHeader(HttpHeader.USER_AGENT.toString()) != null) {
					if (request.getHeader(HttpHeader.USER_AGENT.toString()).contains(keyword)) {
						return;
					}
				}

				if (request.getServerName() != null) {
					if (request.getServerName().contains(keyword)) {
						return;
					}
				}

			}

			StringBuilder buf = _buffers.get();
			buf.setLength(0);

			if (_logServer) {
				buf.append(request.getServerName());
				buf.append("\t");
			}

			String addr = null;
			if (_preferProxiedForAddress) {
				addr = request.getHeader(HttpHeader.X_FORWARDED_FOR.toString());
			}

			if (addr == null) {
				addr = request.getRemoteAddr();
			}
			buf.append(addr);
			buf.append("\t");
			buf.append("-");
			buf.append("\t");
			Authentication authentication = request.getAuthentication();
			if (authentication instanceof Authentication.User)
				buf.append(((Authentication.User) authentication).getUserIdentity().getUserPrincipal().getName());
			else {
				buf.append("-");
				buf.append("\t");
			}

			buf.append("[");
			if (_logDateCache != null) {
				buf.append(_logDateCache.format(request.getTimeStamp()));
			} else {
				buf.append(request.getTimeStamp());
			}

			buf.append("]\t");
			buf.append(request.getMethod());
			buf.append(' ');
			buf.append(request.getOriginalURI());
			buf.append(' ');
			buf.append(request.getProtocol());
			buf.append("\t");

			int status = response.getStatus();
			if (status <= 0)
				status = 404;
			buf.append((char) ('0' + ((status / 100) % 10)));
			buf.append((char) ('0' + ((status / 10) % 10)));
			buf.append((char) ('0' + (status % 10)));
			buf.append("\t");

			long responseLength = 0;

			if (request.getOriginalURI().endsWith("ZIP")) {
				String uri = request.getOriginalURI();
				String publicReferenceID = uri.split("/")[2];
				String entitiyID = uri.split("/")[3];

				if (CalculateDirectorySizeThread.directorySizes.containsKey(publicReferenceID + "/" + entitiyID)) {
					responseLength = CalculateDirectorySizeThread.directorySizes
							.get(publicReferenceID + "/" + entitiyID);
				}
			} else {
				responseLength = response.getLongContentLength();
			}

			if (responseLength >= 0) {
				if (responseLength > 99999) {
					buf.append(responseLength);
				} else {
					if (responseLength > 9999)
						buf.append((char) ('0' + ((responseLength / 10000) % 10)));
					if (responseLength > 999)
						buf.append((char) ('0' + ((responseLength / 1000) % 10)));
					if (responseLength > 99)
						buf.append((char) ('0' + ((responseLength / 100) % 10)));
					if (responseLength > 9)
						buf.append((char) ('0' + ((responseLength / 10) % 10)));
					buf.append((char) ('0' + (responseLength) % 10));
				}
				buf.append("\t");
			} else {
				buf.append("0\t");
			}

			if (_extended)
				logExtended(request, response, buf);

			if (_logCookies) {
				Cookie[] cookies = request.getCookies();
				if (cookies == null || cookies.length == 0)
					buf.append(" -");
				else {
					buf.append(" \"");
					for (int i = 0; i < cookies.length; i++) {
						if (i != 0)
							buf.append(';');
						buf.append(cookies[i].getName());
						buf.append('=');
						buf.append(cookies[i].getValue());
					}
					buf.append('\"');
				}
			}

			if (_logLatency) {
				long now = System.currentTimeMillis();

				if (_logLatency) {
					buf.append(' ');
					buf.append(now - request.getTimeStamp());
				}
			}

			String log = buf.toString();
			write(log);
		} catch (IOException e) {
			LOG.warn(e);
		}
	}

	/* ------------------------------------------------------------ */

	/**
	 * Writes extended request and response information to the output stream.
	 *
	 * @param request
	 *            request object
	 * @param response
	 *            response object
	 * @param b
	 *            StringBuilder to write to
	 * @throws IOException
	 *             if failed
	 */
	protected void logExtended(Request request, Response response, StringBuilder b) throws IOException {
		String referer = request.getHeader(HttpHeader.REFERER.toString());
		if (referer == null)
			b.append("\t");
		else {
			// b.append('"');
			b.append(referer);
			// b.append("\" ");
			b.append("\t");
		}

		String agent = request.getHeader(HttpHeader.USER_AGENT.toString());
		if (agent == null)
			b.append("-");
		else {
			// b.append('"');
			b.append(agent);
			// b.append('"');
		}
	}

	/**
	 * Set request paths that will not be logged.
	 *
	 * @param ignorePaths
	 *            array of request paths
	 */
	public void setIgnorePaths(String[] ignorePaths) {
		_ignorePaths = ignorePaths;
	}

	/**
	 * Retrieve the request paths that will not be logged.
	 *
	 * @return array of request paths
	 */
	public String[] getIgnorePaths() {
		return _ignorePaths;
	}

	/**
	 * Controls logging of the request cookies.
	 *
	 * @param logCookies
	 *            true - values of request cookies will be logged, false - values of
	 *            request cookies will not be logged
	 */
	public void setLogCookies(boolean logCookies) {
		_logCookies = logCookies;
	}

	/**
	 * Retrieve log cookies flag
	 *
	 * @return value of the flag
	 */
	public boolean getLogCookies() {
		return _logCookies;
	}

	/**
	 * Controls logging of the request hostname.
	 *
	 * @param logServer
	 *            true - request hostname will be logged, false - request hostname
	 *            will not be logged
	 */
	public void setLogServer(boolean logServer) {
		_logServer = logServer;
	}

	/**
	 * Retrieve log hostname flag.
	 *
	 * @return value of the flag
	 */
	public boolean getLogServer() {
		return _logServer;
	}

	/**
	 * Controls logging of request processing time.
	 *
	 * @param logLatency
	 *            true - request processing time will be logged false - request
	 *            processing time will not be logged
	 */
	public void setLogLatency(boolean logLatency) {
		_logLatency = logLatency;
	}

	/**
	 * Retrieve log request processing time flag.
	 *
	 * @return value of the flag
	 */
	public boolean getLogLatency() {
		return _logLatency;
	}

	/**
	 * @deprecated use {@link StatisticsHandler}
	 * 
	 * @param value
	 *            value to set
	 */
	public void setLogDispatch(boolean value) {
	}

	/**
	 * @deprecated use {@link StatisticsHandler}
	 * @return the set value
	 */
	public boolean isLogDispatch() {
		return false;
	}

	/**
	 * Controls whether the actual IP address of the connection or the IP address
	 * from the X-Forwarded-For header will be logged.
	 *
	 * @param preferProxiedForAddress
	 *            true - IP address from header will be logged, false - IP address
	 *            from the connection will be logged
	 */
	public void setPreferProxiedForAddress(boolean preferProxiedForAddress) {
		_preferProxiedForAddress = preferProxiedForAddress;
	}

	/**
	 * Retrieved log X-Forwarded-For IP address flag.
	 *
	 * @return value of the flag
	 */
	public boolean getPreferProxiedForAddress() {
		return _preferProxiedForAddress;
	}

	/**
	 * Set the extended request log format flag.
	 *
	 * @param extended
	 *            true - log the extended request information, false - do not log
	 *            the extended request information
	 */
	public void setExtended(boolean extended) {
		_extended = extended;
	}

	/**
	 * Retrieve the extended request log format flag.
	 *
	 * @return value of the flag
	 */
	@ManagedAttribute("use extended NCSA format")
	public boolean isExtended() {
		return _extended;
	}

	/**
	 * Set up request logging and open log file.
	 *
	 * @see org.eclipse.jetty.util.component.AbstractLifeCycle#doStart()
	 */
	@Override
	protected synchronized void doStart() throws Exception {
		if (_logDateFormat != null) {
			_logDateCache = new DateCache(_logDateFormat, _logLocale, _logTimeZone);
		}

		if (_ignorePaths != null && _ignorePaths.length > 0) {
			_ignorePathMap = new PathMappings<String>();
			for (int i = 0; i < _ignorePaths.length; i++)
				_ignorePathMap.put(_ignorePaths[i], _ignorePaths[i]);
		} else
			_ignorePathMap = null;

		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		_logDateCache = null;
		super.doStop();
	}

	/**
	 * Set the timestamp format for request log entries in the file. If this is not
	 * set, the pre-formated request timestamp is used.
	 *
	 * @param format
	 *            timestamp format string
	 */
	public void setLogDateFormat(String format) {
		_logDateFormat = format;
	}

	/**
	 * Retrieve the timestamp format string for request log entries.
	 *
	 * @return timestamp format string.
	 */
	public String getLogDateFormat() {
		return _logDateFormat;
	}

	/**
	 * Set the locale of the request log.
	 *
	 * @param logLocale
	 *            locale object
	 */
	public void setLogLocale(Locale logLocale) {
		_logLocale = logLocale;
	}

	/**
	 * Retrieve the locale of the request log.
	 *
	 * @return locale object
	 */
	public Locale getLogLocale() {
		return _logLocale;
	}

	/**
	 * Set the timezone of the request log.
	 *
	 * @param tz
	 *            timezone string
	 */
	public void setLogTimeZone(String tz) {
		_logTimeZone = tz;
	}

	/**
	 * Retrieve the timezone of the request log.
	 *
	 * @return timezone string
	 */
	@ManagedAttribute("the timezone")
	public String getLogTimeZone() {
		return _logTimeZone;
	}

}
