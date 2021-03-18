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
package de.ipk_gatersleben.bit.bi.edal.sample;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.EdalLoginConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.ElixirCallBackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.GoogleCallBackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.GoogleLoginModule;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.LoginCallbackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.ORCIDCallBackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.ORCIDLoginModule;
import de.ipk_gatersleben.bit.bi.edal.primary_data.login.UserCallBackHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import inet.ipaddr.IPAddressString;
import javafx.application.Platform;

/**
 * Provide some helpful function for the first experience with eDAL.
 * 
 * @author arendd
 */

public class EdalHelpers {

	/**
	 * Authenticate user using the {@link GoogleLoginModule}.
	 * 
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateGoogleUser() throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithGoogle("Google", "", 0);
	}

	/**
	 * Authenticate user using the {@link ORCIDLoginModule}.
	 * 
	 * @param httpProxyHost the address of the HTTP proxy host
	 * @param httpProxyPort the address of the HTTP proxy port
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateORCIDUser(String httpProxyHost, int httpProxyPort)
			throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithORCID("ORCID", httpProxyHost, httpProxyPort);
	}

	/**
	 * Authenticate user using the {@link GoogleLoginModule}.
	 * 
	 * @param httpProxyHost the address of the HTTP proxy host
	 * @param httpProxyPort the address of the HTTP proxy port
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateElixirUser(String httpProxyHost, int httpProxyPort)
			throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithElixir("Elixir", httpProxyHost, httpProxyPort);
	}

	private static Subject authenticateSubjectWithElixir(String loginModule, String httpProxyHost, int httpProxyPort) {
		Configuration.setConfiguration(new EdalLoginConfiguration());

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;

		boolean retry = true;
		Platform.setImplicitExit(false);
		while (retry) {
			try {
				ctx = new LoginContext(loginModule, new ElixirCallBackHandler(httpProxyHost, httpProxyPort));
				ctx.login();
				retry = false;

				Thread.currentThread().setContextClassLoader(currentClassLoader);
				Platform.setImplicitExit(true);
				return ctx.getSubject();
			} catch (final LoginException e) {

				int result = 0;
				if (e.getCause() == null) {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + "no null name allowed"
									+ "\nTry again ?",
							"Login to Elixir", JOptionPane.YES_NO_OPTION);
				} else {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + e.getMessage() + "\nTry again ?",
							"Login to Elixir+", JOptionPane.YES_NO_OPTION);
				}
				if (result == JOptionPane.YES_OPTION) {
					retry = true;
				} else if (result == JOptionPane.NO_OPTION) {
					Thread.currentThread().setContextClassLoader(currentClassLoader);
					Platform.setImplicitExit(true);
					return null;
				}

			}
		}
		Platform.setImplicitExit(true);
		return null;
	}

	/**
	 * Authenticate user using the {@link GoogleLoginModule}.
	 * 
	 * @param httpProxyHost the address of the HTTP proxy host
	 * @param httpProxyPort the address of the HTTP proxy port
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateGoogleUser(String httpProxyHost, int httpProxyPort)
			throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithGoogle("Google", httpProxyHost, httpProxyPort);
	}

	private static Subject authenticateSubjectWithGoogle(String loginModule, String httpProxyHost, int httpProxyPort) {
		Configuration.setConfiguration(new EdalLoginConfiguration());

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;

		boolean retry = true;
		Platform.setImplicitExit(false);
		while (retry) {
			try {
				ctx = new LoginContext(loginModule, new GoogleCallBackHandler(httpProxyHost, httpProxyPort));
				ctx.login();
				retry = false;

				Thread.currentThread().setContextClassLoader(currentClassLoader);
				Platform.setImplicitExit(true);
				return ctx.getSubject();
			} catch (final LoginException e) {

				int result = 0;
				if (e.getCause() == null) {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + "no null name allowed"
									+ "\nTry again ?",
							"Login to Google+", JOptionPane.YES_NO_OPTION);
				} else {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + e.getMessage() + "\nTry again ?",
							"Login to Google+", JOptionPane.YES_NO_OPTION);
				}
				if (result == JOptionPane.YES_OPTION) {
					retry = true;
				} else if (result == JOptionPane.NO_OPTION) {
					Thread.currentThread().setContextClassLoader(currentClassLoader);
					Platform.setImplicitExit(true);
					return null;
				}

			}
		}
		Platform.setImplicitExit(true);
		return null;
	}

	private static Subject authenticateSubjectWithORCID(String loginModule, String httpProxyHost, int httpProxyPort) {
		Configuration.setConfiguration(new EdalLoginConfiguration());

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;

		boolean retry = true;
		Platform.setImplicitExit(false);
		while (retry) {
			try {
				ctx = new LoginContext(loginModule, new ORCIDCallBackHandler(httpProxyHost, httpProxyPort));
				ctx.login();
				retry = false;

				Thread.currentThread().setContextClassLoader(currentClassLoader);
				Platform.setImplicitExit(true);
				return ctx.getSubject();
			} catch (final LoginException e) {

				int result = 0;
				if (e.getCause() == null) {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + "no null name allowed"
									+ "\nTry again ?",
							"Login to ORCID", JOptionPane.YES_NO_OPTION);
				} else {
					result = (Integer) JOptionPane.showConfirmDialog(null,
							"Your login attempt was not successful !\nReason: " + e.getMessage() + "\nTry again ?",
							"Login to ORCDI", JOptionPane.YES_NO_OPTION);
				}
				if (result == JOptionPane.YES_OPTION) {
					retry = true;
				} else if (result == JOptionPane.NO_OPTION) {
					Thread.currentThread().setContextClassLoader(currentClassLoader);
					Platform.setImplicitExit(true);
					return null;
				}

			}
		}
		Platform.setImplicitExit(true);
		return null;
	}

	/**
	 * Authenticate user using the IPK Kerberos-LoginModule.
	 * 
	 * @param user user name.
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateIPKKerberosUser(String user) throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithKerberos("IPK-GATERSLEBEN.DE", "auth1.ipk-gatersleben.de", user);
	}

	/**
	 * Authenticate user using the JKI Kerberos-LoginModule.
	 * 
	 * @param user the user name to login
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateJKIKerberosUser(String user) throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubjectWithJKIKerberos(user);
	}

	/**
	 * Authenticate user using the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.login.UserLoginModule}
	 * -LoginModule.
	 * 
	 * @param name     the user name to authenticate
	 * @param password the password of the user to authenticate
	 * 
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateUser(String name, String password) throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubject("User", new UserCallBackHandler(name, password));
	}

	/**
	 * Authenticate user using the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.login.SampleUserLoginModule}
	 * -LoginModule.
	 * 
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateSampleUser() throws EdalAuthenticateException {

		return EdalHelpers.authenticateSubject("Sample", null);
	}

	/**
	 * Authenticate user using specified loginmodule.properties an
	 * {@link CallbackHandler}.
	 * 
	 * @param loginModule     the choose
	 *                        {@link javax.security.auth.spi.LoginModule}.
	 * @param callbackhandler a {@link CallbackHandler} for the specified
	 *                        {@link javax.security.auth.spi.LoginModule}
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	private static Subject authenticateSubject(final String loginModule, final CallbackHandler callbackhandler)
			throws EdalAuthenticateException {

		Configuration.setConfiguration(new EdalLoginConfiguration());

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;
		try {
			if (callbackhandler == null) {
				ctx = new LoginContext(loginModule);
			} else {
				ctx = new LoginContext(loginModule, callbackhandler);
			}

			ctx.login();
		} catch (final LoginException e) {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
			throw new EdalAuthenticateException("can't login using LoginModule", e);
		}

		Thread.currentThread().setContextClassLoader(currentClassLoader);
		return ctx.getSubject();

	}

	/**
	 * Authenticate user using the specified Kerberos-LoginModule.
	 * 
	 * @param kerberosRealm the Kerberos realm
	 * @param kerberosKDC   the Kerberos KDC
	 * @param user          optional username
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	public static Subject authenticateSubjectWithKerberos(final String kerberosRealm, final String kerberosKDC,
			String user) throws EdalAuthenticateException {

		Configuration.setConfiguration(new EdalLoginConfiguration());

		System.setProperty("java.security.krb5.realm", kerberosRealm);
		System.setProperty("java.security.krb5.kdc", kerberosKDC);

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

		LoginContext ctx = null;

		boolean retry = true;

		while (retry) {
			try {
				ctx = new LoginContext("Kerberos", new LoginCallbackHandler(user, kerberosRealm));
				ctx.login();

				Thread.currentThread().setContextClassLoader(currentClassLoader);

				retry = false;
				return ctx.getSubject();
			} catch (final Exception e) {
				e.printStackTrace();
				if (e.getCause() == null) {
					Thread.currentThread().setContextClassLoader(currentClassLoader);
					return null;
				} else {

					int result = 0;

					if (e.getCause() instanceof UnknownHostException) {
						result = (Integer) JOptionPane.showConfirmDialog(null,
								"Your login attempt was not successful, try again? \nReason: Can not connect to LDAP-Provider,\nplease check your network/VPN configuration",
								"Login to " + kerberosRealm + "-Domain (LDAP)", JOptionPane.YES_NO_OPTION);
					}

					if (result == JOptionPane.YES_OPTION) {
						retry = true;
					} else if (result == JOptionPane.NO_OPTION) {
						Thread.currentThread().setContextClassLoader(currentClassLoader);
						retry = false;
						return null;
					}
				}
			}
		}
		return null;

	}

	/**
	 * Convenient method to check if an IP is in a certain network /IP range using
	 * the 'ipaddress' library
	 * 
	 * @param network   the IP range
	 * @param ipAddress the IP to check
	 * @return true if IP is in network otherwise false
	 */

	private static boolean contains(String network, String ipAddress) {

		System.out.println(new IPAddressString(network) + " contains " + new IPAddressString(ipAddress) + " "
				+ new IPAddressString(network).contains(new IPAddressString(ipAddress)));
		return new IPAddressString(network).contains(new IPAddressString(ipAddress));
	}

	/**
	 * Authenticate user using the specified Kerberos-LoginModule.
	 * 
	 * @param user optional username
	 * @return the authenticated {@link Subject}
	 * @throws EdalAuthenticateException if unable to run
	 *                                   {@link javax.security.auth.spi.LoginModule}
	 *                                   successful.
	 */
	private static Subject authenticateSubjectWithJKIKerberos(String user) throws EdalAuthenticateException {

		String kerberosRealm = null;
		String kerberosKDC = null;

		List<InetAddress> ipAddressList = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		InetAddress localhost = null;

		try {
			localhost = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface ifc = interfaces.nextElement();
			Enumeration<InetAddress> addressesOfAnInterface = ifc.getInetAddresses();

			while (addressesOfAnInterface.hasMoreElements()) {
				InetAddress address = addressesOfAnInterface.nextElement();

				if (!address.equals(localhost) && !address.toString().contains(":")) {
					ipAddressList.add(address);
					System.out.println("FOUND ADDRESS ON NIC: " + address.getHostAddress());
				}
			}
		}

//		try {

			for (InetAddress inetAddress : ipAddressList) {

				String localIPAddress = inetAddress.getHostAddress();
				System.out.println(localIPAddress);

				if (contains("172.31.0.0/16", localIPAddress) || contains("192.168.168.0/24", localIPAddress)
						|| contains("192.168.0.0/24", localIPAddress) || contains("172.19.0.0/16", localIPAddress)
						|| contains("192.168.120.0/24", localIPAddress)) {

					kerberosRealm = "QUEDLINBURG.BBA.INTERN";
					kerberosKDC = "172.31.16.1";
					break;

				} else if (contains("172.16.0.0/16", localIPAddress) || contains("172.21.0.0/16", localIPAddress)
						|| contains("192.168.200.0/24", localIPAddress)) {

					kerberosRealm = "BRAUNSCHWEIG.BBA.INTERN";
					kerberosKDC = "172.16.4.128";
					break;

				} else if (contains("172.18.0.0/16", localIPAddress)) {

					kerberosRealm = "BERLIN.BBA.INTERN";
					kerberosKDC = "172.18.1.4";
					break;

				} else if (contains("172.17.0.0/16", localIPAddress) || contains("192.168.115.0/24", localIPAddress)
						|| contains("192.168.2.0/24", localIPAddress)) {

					kerberosRealm = "KLEINMACHNOW.BBA.INTERN";
					kerberosKDC = "172.17.21.20";
					break;
				} 
			}
			
			if(kerberosKDC==null && kerberosRealm==null) {
				throw new EdalAuthenticateException(
						"You are not in a valid IP range to access the Kerberos service provider");
			}
//		} catch (UnknownHostException e1) {
//			throw new EdalAuthenticateException("Cannnot determine you local IP address");
//		}

		boolean foundKerberosService = false;

		try {
			InetAddress.getByName(kerberosKDC);

			foundKerberosService = true;

			Configuration.setConfiguration(new EdalLoginConfiguration());

			final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

			Thread.currentThread().setContextClassLoader(EdalHelpers.class.getClassLoader());

			System.setProperty("java.security.krb5.realm", kerberosRealm);
			System.setProperty("java.security.krb5.kdc", kerberosKDC);

			LoginContext ctx = null;

			boolean retry = true;

			while (retry) {
				try {
					ctx = new LoginContext("Kerberos", new LoginCallbackHandler(user, "JKI"));
					ctx.login();
					Thread.currentThread().setContextClassLoader(currentClassLoader);

					retry = false;

					return ctx.getSubject();

				} catch (final Exception e) {
					e.printStackTrace();
					if (e.getCause() == null) {
						Thread.currentThread().setContextClassLoader(currentClassLoader);
						return null;
					} else {

						int result = 0;

						if (e.getCause() instanceof UnknownHostException) {
							result = (Integer) JOptionPane.showConfirmDialog(null,
									"Your login attempt was not successful, try again? \nReason: Can not connect to LDAP-Provider,\nplease check your network/VPN configuration",
									"Login to JKI-Domain", JOptionPane.YES_NO_OPTION);

							break;

						}

						if (result == JOptionPane.YES_OPTION) {
							retry = true;
						} else if (result == JOptionPane.NO_OPTION) {
							Thread.currentThread().setContextClassLoader(currentClassLoader);
							retry = false;
							return null;
						}
					}
				}
			}

		} catch (UnknownHostException e) {
			System.out.println(kerberosKDC + " cannot be resolved");
		}

		if (!foundKerberosService) {
			throw new EdalAuthenticateException("Cannot find any Kerberos Service, please check your DNS settings");
		}

		return null;

	}

	/**
	 * Authenticate user using the Windows- or Unix- or MAC-LoginModule.
	 * 
	 * @return authenticated {@link Subject}
	 * @throws EdalAuthenticateException if the authentication failed
	 */
	public static Subject authenticateWinOrUnixOrMacUser() throws EdalAuthenticateException {

		if (SystemUtils.IS_OS_UNIX) {
			return EdalHelpers.authenticateSubject("Unix", null);
		} else if (SystemUtils.IS_OS_MAC) {
			return EdalHelpers.authenticateSubject("Unix", null);
		} else if (SystemUtils.IS_OS_WINDOWS) {
			return EdalHelpers.authenticateSubject("Windows", null);
		} else {
			throw new EdalAuthenticateException("You do not use a Windows or Unix or MAC OS !");
		}

	}

	/**
	 * Clean all files in directory.
	 * 
	 * @param path a {@link Path} object.
	 * @throws EdalException if unable to clean mount path.
	 */
	public static void cleanMountPath(final Path path) throws EdalException {
		try {
			EdalHelpers.deleteDirectory(path);
		} catch (EdalException e) {
			throw new EdalException("Can not clean mount path: " + e.getMessage());
		}
	}

	/**
	 * Delete all files in a directory recursively using {@link IOUtils}.
	 * 
	 * @param directory to delete
	 * @throws EdalException if unable to clean directory
	 */
	private static void deleteDirectory(final Path directory) throws EdalException {
		try {
			if (Files.exists(directory)) {
				FileDeleteStrategy.FORCE.delete(directory.toFile());
				// FileUtils.deleteDirectory(directory.toFile());
			}
		} catch (IOException e) {
			System.gc();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {

				if (Files.exists(directory)) {
					FileDeleteStrategy.FORCE.delete(directory.toFile());
				}
				// FileUtils.deleteDirectory(directory.toFile());
			} catch (IOException e1) {
				if (e.getCause() != null) {
					throw new EdalException("could not delete directory: " + e.getCause());
				}
			}

		}
	}

	/**
	 * Delete all files in a directory recursively.
	 * 
	 * @param directory to delete
	 * @throws EdalException if unable to clean directory
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void deleteDir(final Path directory) throws EdalException {

		if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exception) throws IOException {

						if (exception == null) {

							while (dir.toFile().delete()) {
								synchronized (this) {
									try {
										this.wait(10);
									} catch (InterruptedException e) {
										throw new IOException("could not wait to delete file: " + e.getMessage(), e);
									}
								}
							}
							return FileVisitResult.CONTINUE;
						} else {
							throw exception;
						}
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						while (file.toFile().delete()) {
							synchronized (this) {
								try {
									this.wait(10);
								} catch (InterruptedException e) {
									throw new IOException("could not wait to delete file: " + e.getMessage(), e);
								}
							}
						}
						return FileVisitResult.CONTINUE;
					}

				});
			} catch (IOException e) {
				throw new EdalException("could not delete file: " + e.getMessage());
			}
		}
	}

	/**
	 * Get a new {@link FileSystemImplementationProvider}. The path to use for file
	 * storage and database is set to users home/edal_test
	 * 
	 * @param cleanMountPathBefore true if you want to clean the database before
	 *                             start eDAL.
	 * @param config               the {@link EdalConfiguration} class.
	 * @return new {@link FileSystemImplementationProvider}
	 * @throws EdalException if unable to clean mount path or create new mount path.
	 */
	public static ImplementationProvider getFileSystemImplementationProvider(final boolean cleanMountPathBefore,
			EdalConfiguration config) throws EdalException {

		if (cleanMountPathBefore) {
			try {
				EdalHelpers.cleanMountPath(config.getMountPath());
				EdalHelpers.cleanMountPath(config.getDataPath());

			} catch (EdalException e) {
				throw new EdalException("Can not clean mount path before starting eDAL: " + e.getMessage(), e);
			}
		}

		if (Files.notExists(config.getMountPath(), LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(config.getMountPath());
			} catch (IOException e) {
				throw new EdalException("Can not create mount path before starting eDAL: " + e.getMessage(), e);
			}
		}
		if (Files.notExists(config.getDataPath(), LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(config.getDataPath());
			} catch (IOException e) {
				throw new EdalException("Can not create data path before start eDAL: " + e.getMessage(), e);
			}
		}

		final ImplementationProvider mountPoint = new FileSystemImplementationProvider(config);

		return mountPoint;
	}

	/**
	 * Print SearchStatistic.
	 * 
	 * @param statistics the {@link Statistics} to get information
	 */
	public static void getSearchStatistic(final Statistics statistics) {

		try {
			if (!statistics.isStatisticsEnabled()) {
				System.out.println("WARN: statistics disabled");
			}
			System.out.println("\n******** Search-Statistic ********");

			CacheRegionStatistics metaDataStatistics = null;
			try {
				metaDataStatistics = statistics.getDomainDataRegionStatistics("search.metadata");

				final long metaDataHit = metaDataStatistics.getHitCount();

				final long metaDataMiss = metaDataStatistics.getMissCount();

				final double metaDataRatio = (double) metaDataHit / (double) (metaDataHit + metaDataMiss);

				System.out.println("\nsearch.metadata-Cache:");
				System.out.println("Hit-Ratio : " + metaDataRatio);
				System.out.println("Hits : " + metaDataHit);
				System.out.println("Miss : " + metaDataMiss);
			} catch (final NullPointerException e) {
				System.out.println("didnt use search.metadata-Cache");
			}

			CacheRegionStatistics versionStatistics = null;

			try {
				versionStatistics = statistics.getDomainDataRegionStatistics("search.version");

				final long versionHit = versionStatistics.getHitCount();

				final long versionMiss = versionStatistics.getMissCount();

				final double versionRatio = (double) versionHit / (double) (versionHit + versionMiss);

				System.out.println("\nsearch.version-Cache:");
				System.out.println("Hit-Ratio : " + versionRatio);
				System.out.println("Hits : " + versionHit);
				System.out.println("Miss : " + versionMiss);
			} catch (final NullPointerException e) {
				System.out.println("didnt use search.version-Cache");
			}

			CacheRegionStatistics entityStatistics = null;

			try {
				entityStatistics = statistics.getDomainDataRegionStatistics("search.entity");
				final long entityHit = entityStatistics.getHitCount();

				final long entityMiss = entityStatistics.getMissCount();

				final double entityRatio = (double) entityHit / (double) (entityHit + entityMiss);

				System.out.println("\nsearch.entity-Cache:");
				System.out.println("Hit-Ratio : " + entityRatio);
				System.out.println("Hits : " + entityHit);
				System.out.println("Miss : " + entityMiss);
			} catch (final NullPointerException e) {
				System.out.println("didnt use search.entity-Cache");
			}

			System.out.println("\n**********************************\n");
		} catch (final NullPointerException e) {
			System.out.println("couldnt found statistic");
		}
	}

	/**
	 * Print PermissionStatistic.
	 * 
	 * @param statistics the {@link Statistics} to get information
	 */
	public static void getStatistic(final Statistics statistics) {

		try {
			if (!statistics.isStatisticsEnabled()) {
				System.out.println("WARN: statistics disabled");
			}
			System.out.println("\n******** Permission-Statistic ********");

			CacheRegionStatistics rootStatistics = null;

			try {
				rootStatistics = statistics.getDomainDataRegionStatistics("query.root");
				final long rootHit = rootStatistics.getHitCount();

				final long rootMiss = rootStatistics.getMissCount();

				final double rootRatio = (double) rootHit / (double) (rootHit + rootMiss);

				System.out.println("\nquery.root-Cache:");
				System.out.println("Hit-Ratio : " + rootRatio);
				System.out.println("Hits : " + rootHit);
				System.out.println("Miss : " + rootMiss);

			} catch (final IllegalArgumentException e) {
				System.out.println("didnt use query.root-Cache");
			}

			CacheRegionStatistics permissionStatistics = null;

			try {
				permissionStatistics = statistics.getDomainDataRegionStatistics("query.permission");
				final long permissionHit = permissionStatistics.getHitCount();

				final long permissionMiss = permissionStatistics.getMissCount();

				final double permissionRatio = (double) permissionHit / (double) (permissionHit + permissionMiss);

				System.out.println("\nquery.permission-Cache:");
				System.out.println("Hit-Ratio : " + permissionRatio);
				System.out.println("Hits : " + permissionHit);
				System.out.println("Miss : " + permissionMiss);

			} catch (final IllegalArgumentException e) {
				System.out.println("didnt use query.permission-Cache");
			}

			CacheRegionStatistics principalStatistics = null;

			try {
				principalStatistics = statistics.getDomainDataRegionStatistics("query.principal");
				final long principalHit = principalStatistics.getHitCount();
				final long principalMiss = principalStatistics.getMissCount();

				final double principalRatio = (double) principalHit / (double) (principalHit + principalMiss);

				System.out.println("\nquery.principal-Cache:");
				System.out.println("Hit-Ratio : " + principalRatio);
				System.out.println("Hits : " + principalHit);
				System.out.println("Miss : " + principalMiss);
				System.out.println("\n**************************************");

			} catch (final IllegalArgumentException e) {
				System.out.println("didnt use query.principal-Cache");
			}
		} catch (final NullPointerException e) {
			System.out.println("couldnt found statistic");
		}
	}

	/**
	 * Function to open a browser and loading the given URL.
	 * 
	 * @param url the URL to open in a browser
	 * @return true if the function was able to open the browser
	 */
	public static boolean openURL(String url) {

		InetSocketAddress proxy = EdalConfiguration.guessProxySettings();
		if (proxy != null) {
			System.setProperty("http.proxyHost", proxy.getHostName());
			System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
			System.setProperty("https.proxyHost", proxy.getHostName());
			System.setProperty("https.proxyPort", String.valueOf(proxy.getPort()));
		}

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			try {
				if (url.startsWith("mailto")) {
					Desktop.getDesktop().mail(URI.create(url));
				} else {
					Desktop.getDesktop().browse(URI.create(url));
				}
				return true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "unable to open Browser :",
						e.getMessage() + "\n please enter the URL manually into your browser: '" + url + "'", 0);
				return false;
			}
		} else {
			String os = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();

			try {

				if (os.indexOf("win") >= 0) {

					// this doesn't support showing urls in the form of
					// "page.html#nameLink"
					rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

				} else if (os.indexOf("mac") >= 0) {

					rt.exec("open " + url);

				} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

					// Do a best guess on unix until we get a platform
					// independent way
					// Build a list of browsers to try, in this order.
					String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links",
							"lynx" };

					// Build a command string which looks like "browser1
					// "url" || browser2 "url" ||..."
					StringBuffer cmd = new StringBuffer();
					for (int i = 0; i < browsers.length; i++)
						cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

					rt.exec(new String[] { "sh", "-c", cmd.toString() });

				} else {
					JOptionPane.showMessageDialog(null, "unable to find OS versino to open Browser ",
							"\n Please enter the URL manually into your browser: '" + url + "'", 0);
					return false;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "unable to open Browser :",
						e.getMessage() + "\n please enter the URL manually into your browser: '" + url + "'", 0);
				return false;
			}
			return true;
		}

	}
}
