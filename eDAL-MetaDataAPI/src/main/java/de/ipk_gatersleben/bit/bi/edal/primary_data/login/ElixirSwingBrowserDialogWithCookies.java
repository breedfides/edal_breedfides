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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.swing.*;

import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import static javafx.concurrent.Worker.State.FAILED;

@SuppressWarnings("unused")
public class ElixirSwingBrowserDialogWithCookies extends JDialog {

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

	private static final long serialVersionUID = -3918755777891924805L;

	private final JFXPanel jfxPanel = new JFXPanel();
	private WebEngine engine;

	private final JPanel panel = new JPanel(new BorderLayout());
	private final JLabel lblStatus = new JLabel();

	private final JButton btnHelp = new JButton("Help");

	private final JTextField txtURL = new JTextField();
	private final JProgressBar progressBar = new JProgressBar();

	//private final Path CookieStore = Paths.get(System.getProperty("user.home"), ".eDAL", "cookie.txt");

	public ElixirSwingBrowserDialogWithCookies(Frame parent, String url) {
		super(parent, true);
		initComponents(parent);
		loadURL(url);
	}

	private void initComponents(Frame parent) {
		createScene();

		ActionListener al = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (e.getSource().equals(btnHelp)) {

					EdalHelpers.openURL("http://edal-pgp.ipk-gatersleben.de/document/elixir.html");

				} else {
					loadURL(txtURL.getText());
				}
			}
		};
		btnHelp.addActionListener(al);
		txtURL.addActionListener(al);

		progressBar.setPreferredSize(new Dimension(200, 10));
		progressBar.setStringPainted(true);

		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(btnHelp, BorderLayout.WEST);
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		panel.add(jfxPanel, BorderLayout.CENTER);
		panel.add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(panel);
		setPreferredSize(new Dimension(800, 850));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();

		setResizable(false);
		setLocationRelativeTo(parent);

	}

	private void createScene() {

		Platform.runLater(new Runnable() {

			public void run() {

				WebView view = new WebView();

//				CookieManager manager = new CookieManager(new CustomCookieStore(), CookiePolicy.ACCEPT_ALL);
//
//				if (!Files.exists(CookieStore)) {
//					try {
//						Files.createDirectories(CookieStore.getParent());
//						Files.createFile(CookieStore);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//
//				try {
//					for (String line : Files.readAllLines(CookieStore)) {
//						String[] values = line.split("~");
//
//						for (String header : values) {
//							HttpCookie cookie = CookieUtil.fromString(header);
//
//							if (cookie == null || cookie.hasExpired())
//								continue;
//
//							manager.getCookieStore().add(new URI(header.split("|")[0]), cookie);
//						}
//					}
//				} catch (IOException | URISyntaxException e) {
//					e.printStackTrace();
//				}
//
//				CookieHandler.setDefault(manager);

				engine = view.getEngine();

				engine.titleProperty().addListener(new ChangeListener<String>() {

					public void changed(ObservableValue<? extends String> observable, String oldValue,
							final String newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								ElixirSwingBrowserDialogWithCookies.this.setTitle(newValue);
							}
						});
					}
				});

				engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
					public void handle(final WebEvent<String> event) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								lblStatus.setText(event.getData());
							}
						});
					}
				});

				engine.locationProperty().addListener(new ChangeListener<String>() {

					public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								txtURL.setText(newValue);
							}
						});
					}
				});

				engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {

					public void changed(ObservableValue<? extends Number> observableValue, Number oldValue,
							final Number newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								progressBar.setValue(newValue.intValue());
							}
						});
					}
				});

				engine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

					public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
						if (engine.getLoadWorker().getState() == FAILED) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(panel,
											(value != null) ? engine.getLocation() + "\n" + value.getMessage()
													: engine.getLocation() + "\nUnexpected error.",
											"Loading error...", JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				});

				jfxPanel.setScene(new Scene(view));
			}
		});
	}

	public void loadURL(final String url) {

		Platform.runLater(new Runnable() {
			public void run() {
				String tmp = toURL(url);
				if (tmp == null) {
					tmp = toURL("http://" + url);
				}
				URI myuri = URI.create(tmp);
				Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
				headers.put("Set-Cookie", Arrays.asList("name=value"));
				try {
					java.net.CookieHandler.getDefault().put(myuri, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				 /** new version for Cookie-Store maybe for later use **/
				CookieManager manager = new CookieManager();
				manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
				CookieHandler.setDefault(manager);
				
//				/** old version JDK8  **/
//				CookieManager man = (CookieManager) CookieHandler.getDefault();
//				CookieStore store = man.getCookieStore();
//				try {
//
//					if (!Files.exists(CookieStore)) {
//						Files.createDirectories(CookieStore.getParent());
//						Files.createFile(CookieStore);
//					}
//
//					Files.write(CookieStore, ("").getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
//
//					for (URI uri : store.getURIs()) {
//						for (HttpCookie cookie : store.get(uri)) {
//							if (cookie.hasExpired())
//								continue;
//
//							Files.write(CookieStore,
//									(uri.toString() + "|" + CookieUtil.toString(cookie) + "~").getBytes(),
//									StandardOpenOption.APPEND);
//						}
//					}
//
//					Files.write(CookieStore, "\n".getBytes(), StandardOpenOption.APPEND);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

				engine.load(tmp);
			}
		});
	}

	private static String toURL(String str) {
		try {
			return new URL(str).toExternalForm();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

	public static void main(String[] args) {

		/**
		 * important to be able to call Platform.runLater() again and have access to
		 * JFXPanel
		 **/
		Platform.setImplicitExit(false);

		ElixirSwingBrowserDialogWithCookies browser = new ElixirSwingBrowserDialogWithCookies(null,
				"http://www.google.de");
		browser.setVisible(true);

		ElixirSwingBrowserDialogWithCookies browser2 = new ElixirSwingBrowserDialogWithCookies(null,
				"http://www.google.de");
		browser2.setVisible(true);

		/** important to set it back to close JFX Thread at the end **/
		Platform.setImplicitExit(true);

	}
}