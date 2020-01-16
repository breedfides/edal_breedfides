/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * Create a Dialog if there is an problem with the connection to the eDAL server
 * and ask the user if the system should retry or quit the connection try.
 * 
 * @author arendd
 */
public class ServerErrorDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 2288426728436421340L;

	private JButton retryButton = new JButton("RETRY");
	private JButton cancelButton = new JButton("CANCEL");

	private int returnValue = 0;

	public ServerErrorDialog(Frame parent, String errorMessage, String serverAddress, int registryPort) {

		super(parent, PropertyLoader.PROGRAM_NAME, true);
		this.setFocusable(true);
		this.setIconImage(PropertyLoader.EDAL_LOGO);

		JEditorPane textPane = new JEditorPane();

		textPane.setContentType("text/html");

		try {
			textPane.setText(
					PublicationVeloCityCreater.generateServerErrorDialog(errorMessage, serverAddress, registryPort));
		} catch (EdalException e) {
			e.printStackTrace();
		}

		textPane.setEditable(false);
		textPane.setBorder(BorderFactory.createEmptyBorder());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(retryButton);
		buttonPanel.add(cancelButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(textPane);
		mainPanel.add(buttonPanel);

		this.setContentPane(mainPanel);
		this.setResizable(false);
		this.setPreferredSize(new Dimension(400, 200));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.retryButton.addActionListener(this);
		this.cancelButton.addActionListener(this);
		this.pack();
		this.setLocationRelativeTo(parent);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(this.retryButton)) {
			this.returnValue = 1;
			this.dispose();
		}

		else if (actionEvent.getSource().equals(this.cancelButton)) {
			this.returnValue = 0;
			this.dispose();
		}
	}

	/**
	 * @return the returnValue
	 */
	public int getReturnValue() {
		return returnValue;
	}

	public Object showDialog() {
		setVisible(true);
		return this;

	}
}