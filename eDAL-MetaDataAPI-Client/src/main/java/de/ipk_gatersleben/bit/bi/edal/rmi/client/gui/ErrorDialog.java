/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * @author arendd
 */

public class ErrorDialog extends JDialog {

	private static final long serialVersionUID = -4967851274829031411L;
	private static final String SHOW_DETAILS_TEXT = "Show Details ...";
	private static final String HIDE_DETAILS_TEXT = "Hide Details";
	private JButton jButtonClose;
	private JButton jButtonShowHideDetails;
	private JPanel jPanelBottom;
	private JPanel jPanelCenter;
	private JPanel jPanelTop;
	private JScrollPane jScrollPaneErrorMsg;
	private JTextPane jTextPaneErrorMsg;
	private JScrollPane jScrollPaneException;
	private JTextArea jTextAreaException;

	public ErrorDialog(String errorMessage) {
		this(errorMessage, null);
	}

	private static Image iconToImage(Icon icon) {
		if (icon instanceof ImageIcon) {
			return ((ImageIcon) icon).getImage();
		} else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}

	public ErrorDialog(String errorMessage, Throwable exception) {

		this.setIconImage(ErrorDialog.iconToImage(UIManager.getLookAndFeel().getDefaults().getIcon("OptionPane.errorIcon")));

		this.setTitle("Error");
		this.setModal(true);
		this.setResizable(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 200));
		// this.setPreferredSize(new Dimension(400, 200));

		jPanelTop = new JPanel();
		jPanelTop.setLayout(new BorderLayout());
		jPanelTop.setPreferredSize(new Dimension(500, 80));
		jPanelTop.setBorder(null);

		jTextPaneErrorMsg = new JTextPane();
		jTextPaneErrorMsg.setEditable(false);
		jTextPaneErrorMsg.setContentType("text/html");
		jTextPaneErrorMsg.setBorder(null);

		jScrollPaneErrorMsg = new JScrollPane(jTextPaneErrorMsg);
		jScrollPaneErrorMsg.setBorder(null);

		jPanelTop.add(jScrollPaneErrorMsg, BorderLayout.CENTER);

		jPanelCenter = new JPanel(new BorderLayout());
		jPanelCenter.setPreferredSize(new Dimension(500, 120));

		jTextAreaException = new JTextArea();

		jScrollPaneException = new JScrollPane(jTextAreaException);

		jPanelCenter.add(jScrollPaneException);

		jPanelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jButtonShowHideDetails = new JButton();
		jButtonClose = new JButton("Close");

		jButtonShowHideDetails.setText(SHOW_DETAILS_TEXT);
		jButtonShowHideDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showHideExceptionDetails();
			}
		});
		jPanelBottom.add(jButtonShowHideDetails);

		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		jPanelBottom.add(jButtonClose);

		// this.setLayout(new BoxLayout(this.getContentPane(),
		// BoxLayout.Y_AXIS));
		// this.add(jPanelTop);
		// this.add(jPanelCenter);
		// this.add(jPanelBottom);

		this.setLayout(new BorderLayout());
		this.add(jPanelTop, BorderLayout.NORTH);
		this.add(jPanelCenter, BorderLayout.CENTER);
		this.add(jPanelBottom, BorderLayout.SOUTH);

		Color color = UIManager.getColor("Panel.background");

		this.jTextPaneErrorMsg.setText("<html><body bgcolor='rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")'><p/><div style='text-align:center;font-family:Arial'>" + errorMessage + "</div></body></html>");
		this.jPanelCenter.setVisible(false);

		if (exception != null) {
			String exceptionText = getStackTraceAsString(exception);
			jTextAreaException.setText(exceptionText);
			jTextAreaException.setEditable(false);
			jTextAreaException.setCaretPosition(0);
		} else {
			this.jButtonShowHideDetails.setVisible(false);
		}

		// Make [Escape] key as close button
		this.registerEscapeKey();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		this.pack();
		centerDialogOnTheScreen();
	}

	private void centerDialogOnTheScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = this.getSize();
		int centerPosX = (screenSize.width - dialogSize.width) / 2;
		int centerPosY = (screenSize.height - dialogSize.height) / 2;
		setLocation(centerPosX, centerPosY);
	}

	private void showHideExceptionDetails() {
		if (this.jPanelCenter.isVisible()) {
			// Hide the exception details
			this.jButtonShowHideDetails.setText(SHOW_DETAILS_TEXT);
			this.jPanelCenter.setVisible(false);
			this.pack();
			centerDialogOnTheScreen();
		} else {
			// Show the exception details
			this.jButtonShowHideDetails.setText(HIDE_DETAILS_TEXT);
			this.jPanelCenter.setVisible(true);
			this.pack();
			centerDialogOnTheScreen();
		}
	}

	private String getStackTraceAsString(Throwable exception) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * Make the [Escape] key to behave like the [Close] button.
	 */
	public void registerEscapeKey() {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				jButtonClose.doClick();
			}
		};

		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		this.rootPane.getActionMap().put("ESCAPE", escapeAction);
	}

	public void hideAndDisposeDialog() {
		this.setVisible(false);
		this.dispose();
	}

	private static void showError(String errorMessage, Throwable throwable) {
		ErrorDialog errorDialog = new ErrorDialog(errorMessage, throwable);
		errorDialog.setVisible(true);
	}

	@SuppressWarnings("unused")
	private static void showError(String errorMessage) {
		ErrorDialog.showError(errorMessage, null);
	}

	public static void showError(Exception exception) {
		ErrorDialog.showError(exception.getMessage(), exception);
	}

	public static void main(String[] args) {
		ErrorDialog.showError("This is an error message.", new Exception());
	}
}
