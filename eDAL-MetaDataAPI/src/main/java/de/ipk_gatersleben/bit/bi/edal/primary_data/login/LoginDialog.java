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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -8676977257195247443L;

	public static final int TRY_LOGIN = 1;

	public static final int ABORT = 0;

	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private JLabel labelUsername = new JLabel("Username: ");
	private JLabel labelPassword = new JLabel("Password: ");
	private JButton loginButtun = new JButton("Login");
	private JButton cancelButton = new JButton("Cancel");
	private int status = ABORT;

	public LoginDialog(Frame parent, String title, String user) {
		super(parent, title, true);

		this.isAlwaysOnTopSupported();

		this.setAlwaysOnTop(true);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();

		cs.fill = GridBagConstraints.HORIZONTAL;

		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		panel.add(labelUsername, cs);

		textFieldUsername = new JTextField(20);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		textFieldUsername.setText(user);
		panel.add(textFieldUsername, cs);

		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		panel.add(labelPassword, cs);

		passwordField = new JPasswordField(20);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		panel.add(passwordField, cs);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				int result = JOptionPane.showConfirmDialog(getContentPane(), "Do you want to close ?", "EXIT", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					status = ABORT;
					dispose();
				}

			}

			@Override
			public void windowOpened(WindowEvent e) {
				if (!textFieldUsername.getText().isEmpty()) {
					passwordField.requestFocus();
				}
			}
		});

		loginButtun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = TRY_LOGIN;
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int result = JOptionPane.showConfirmDialog(getContentPane(), "Do you want to close ?", "EXIT", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					status = ABORT;
					dispose();
				}
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loginButtun);
		buttonPanel.add(cancelButton);

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

		getRootPane().setDefaultButton(loginButtun);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	public int getStatus() {
		return this.status;
	}

	public String getUsername() {
		return textFieldUsername.getText().trim();
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

}