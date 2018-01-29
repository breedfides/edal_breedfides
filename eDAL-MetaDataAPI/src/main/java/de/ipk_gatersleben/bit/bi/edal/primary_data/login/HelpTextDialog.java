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

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

public class HelpTextDialog extends JDialog {

	private static final long serialVersionUID = 3378495414715289398L;

	private JEditorPane pane = new JEditorPane();

	public HelpTextDialog() {
		pane.setContentType("text/html");

		try {
			pane.setText(LoginVeloCityCreater.generateHtmlForHeadPage());
		} catch (EdalException e) {
			e.printStackTrace();
		}
		pane.setEditable(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(pane);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});

		mainPanel.add(scrollPane);

		this.setContentPane(mainPanel);
		this.setPreferredSize(new Dimension(1024, 600));
		this.setTitle("HELP");
		this.pack();

	}

}
