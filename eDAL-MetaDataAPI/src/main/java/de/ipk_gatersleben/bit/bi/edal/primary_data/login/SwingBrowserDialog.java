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
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import static javafx.concurrent.Worker.State.FAILED;

public class SwingBrowserDialog extends JDialog {

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
	
	/** new version for Cookie-Store maybe for later use **/
	public static CookieManager manager = new CookieManager();

	private static final long serialVersionUID = -3918755777891924805L;

	private final JFXPanel jfxPanel = new JFXPanel();
	private WebEngine engine;

	private final JPanel panel = new JPanel(new BorderLayout());
	private final JLabel lblStatus = new JLabel();

	private final JButton btnGo = new JButton("Go");
	private final JTextField txtURL = new JTextField();
	private final JProgressBar progressBar = new JProgressBar();

	public SwingBrowserDialog(Frame parent, String url, boolean showAdressLine, int width, int heigth) {
		super(parent, true);
		initComponents(parent, showAdressLine, width, heigth);
		loadURL(url);
	}

	public SwingBrowserDialog(Frame parent, String url) {
		super(parent, true);
		initComponents(parent, true, 800, 800);
		loadURL(url);
	}

	private void initComponents(Frame parent, boolean showAdressLine, int width, int heigth) {
		createScene();

		ActionListener al = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				loadURL(txtURL.getText());
			}
		};
		btnGo.addActionListener(al);
		txtURL.addActionListener(al);

		progressBar.setPreferredSize(new Dimension(150, 18));
		progressBar.setStringPainted(true);

		JPanel topBar = new JPanel(new BorderLayout(5, 0));
		topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		topBar.add(txtURL, BorderLayout.CENTER);
		topBar.add(btnGo, BorderLayout.EAST);

		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		if (showAdressLine) {
			panel.add(topBar, BorderLayout.NORTH);
		}
		panel.add(jfxPanel, BorderLayout.CENTER);
		panel.add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(panel);
		setPreferredSize(new Dimension(width, heigth));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();

		setResizable(false);
		setLocationRelativeTo(parent);

	}

	private void createScene() {

		Platform.runLater(new Runnable() {

			public void run() {

				WebView view = new WebView();

				engine = view.getEngine();

				engine.titleProperty().addListener(new ChangeListener<String>() {

					public void changed(ObservableValue<? extends String> observable, String oldValue,
							final String newValue) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								SwingBrowserDialog.this.setTitle(newValue);
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

				URI uri = URI.create(tmp);
				Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
				headers.put("Set-Cookie", Arrays.asList("name=value"));
				try {
					java.net.CookieHandler.getDefault().put(uri, headers);
				} catch (IOException e) {
					e.printStackTrace();
				}

				/** new version for Cookie-Store maybe for later use **/
				// manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
				// CookieHandler.setDefault(manager);

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

		SwingBrowserDialog browser = new SwingBrowserDialog(null, "http://www.google.de", false, 600, 600);
		browser.setVisible(true);

		SwingBrowserDialog browser2 = new SwingBrowserDialog(null, "http://www.google.de", false, 600, 600);
		browser2.setVisible(true);

		/** important to set it back to close JFX Thread at the end **/
		Platform.setImplicitExit(true);

	}
}