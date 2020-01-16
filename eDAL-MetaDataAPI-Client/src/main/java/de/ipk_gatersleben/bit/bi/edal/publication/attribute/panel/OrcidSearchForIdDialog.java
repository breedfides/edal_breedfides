/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.metal.MetalButtonUI;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationVeloCityCreater;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Dialog to show the result of the ORICD search for a given author name
 * 
 * @author arendd
 *
 */
public class OrcidSearchForIdDialog extends JDialog implements ActionListener, HyperlinkListener, WindowListener {

	private static final long serialVersionUID = 1381135721709418596L;

	private final JButton ignoreButton = new JButton("Ignore");
	private final JButton redirectButton = new JButton("Search");

	private String firstName;
	private String lastName;
	private JLabel searchLabel = new JLabel("Searching for ORCID...");
	private List<ORCID> list = null;
	private String selectedOrcid = "";
	private Component parent;

	public OrcidSearchForIdDialog(Component parent, String firstName, String lastName) {

		this.setModal(true);
		this.setAlwaysOnTop(true);
		this.firstName = firstName;
		this.lastName = lastName;
		this.ignoreButton.addActionListener(this);
		this.redirectButton.addActionListener(this);
		this.parent = parent;
		this.setLocationRelativeTo(parent);
		this.setTitle("Please wait...");
		this.setMinimumSize(new Dimension(750, 60));
		this.setResizable(false);
		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		this.getContentPane().add(searchLabel);
		this.addWindowListener(this);

		this.pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				searchOrcid();
			}
		});
		this.setVisible(true);
	}

	protected void searchOrcid() {

		try {
			this.list = ORCID.getOrcidsByName(firstName, lastName);
		} catch (ORCIDException e) {
			this.dispose();
		}

		GridBagLayout gridBagLayout = new GridBagLayout();

		final JPanel contentPanel = new JPanel(gridBagLayout);
		contentPanel.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.ipady = 0;
		c.ipadx = 0;

		if (this.list.size() < 10 && this.list.size() != 0) {

			this.setMinimumSize(new Dimension(750, 60 * this.list.size()));

			this.setTitle("Is this your ORCID, " + firstName + " " + lastName + " ?");
			this.getContentPane().remove(searchLabel);

			JPanel headPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
			headPanel.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

			JButton jbutton = new JButton("Choose");
			jbutton.setEnabled(false);
			jbutton.setOpaque(false);
			jbutton.setContentAreaFilled(false);
			jbutton.setBorderPainted(false);
			jbutton.setForeground(PropertyLoader.HEADER_FOOTER_COLOR);
			jbutton.setUI(new MetalButtonUI() {
				protected Color getDisabledTextColor() {
					return PropertyLoader.HEADER_FOOTER_COLOR;
				}
			});

			JEditorPane orcidHtmlHeadPane = null;
			try {
				orcidHtmlHeadPane = new JEditorPane("text/html",
						PublicationVeloCityCreater.generateHtmlForOrcidSearchHeader());
			} catch (EdalException e) {
				e.printStackTrace();
			}
			orcidHtmlHeadPane.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
			headPanel.add(jbutton);
			headPanel.add(orcidHtmlHeadPane);

			c.gridx = 0;
			c.gridy = 0;
			gridBagLayout.setConstraints(headPanel, c);
			contentPanel.add(headPanel);

			for (int i = 0; i < this.list.size(); i++) {
				NaturalPerson naturalPerson = null;
				try {
					naturalPerson = ORCID.getPersonByOrcid(this.list.get(i).getOrcid());
				} catch (ORCIDException e1) {
					e1.printStackTrace();
				}

				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
				panel.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
				OrcidButton button = new OrcidButton("Choose", this.list.get(i).getOrcid());
				button.setForeground(PropertyLoader.MAIN_FONT_COLOR);
				button.addActionListener(this);
				button.setAlignmentX(Component.CENTER_ALIGNMENT);

				JEditorPane orcidHtmlPane = null;
				try {
					orcidHtmlPane = new JEditorPane("text/html", PublicationVeloCityCreater
							.generateHtmlForOrcidSearch(this.list.get(i).getOrcid(), naturalPerson));
				} catch (EdalException e) {
					e.printStackTrace();
				}
				orcidHtmlPane.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
				orcidHtmlPane.setEditable(false);
				orcidHtmlPane.addHyperlinkListener(this);
				panel.add(button);
				panel.add(orcidHtmlPane);

				c.gridx = 0;
				c.gridy = i + 1;
				gridBagLayout.setConstraints(panel, c);
				contentPanel.add(panel);

			}
			this.ignoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.redirectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

			this.getContentPane().add(contentPanel);

			GridBagLayout gridBagLayoutButtonLine = new GridBagLayout();

			final JPanel contentPanelButtonLine = new JPanel(gridBagLayoutButtonLine);
			contentPanelButtonLine.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
			contentPanelButtonLine.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK),
							"", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(2, 2, 2, 2);
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.ipady = 0;
			gbc.ipadx = 0;

			JLabel label = new JLabel("ORCID is not present in list");
			label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			label.setForeground(PropertyLoader.MAIN_FONT_COLOR);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;

			gridBagLayoutButtonLine.setConstraints(label, gbc);
			contentPanelButtonLine.add(label);

			gbc.gridx = -1;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;

			gridBagLayoutButtonLine.setConstraints(ignoreButton, gbc);
			contentPanelButtonLine.add(ignoreButton);

			gbc.gridx = 1;
			gbc.gridy = 1;

			gridBagLayoutButtonLine.setConstraints(redirectButton, gbc);
			contentPanelButtonLine.add(redirectButton);

			contentPanelButtonLine.setMaximumSize(new Dimension(300, 100));
			this.getContentPane().add(contentPanelButtonLine);

			this.pack();
		} else if (this.list.size() > 5) {
			this.dispose();

			JEditorPane editorPane = new JEditorPane("text/html", "<html><body bgcolor=rgb("
					+ UIManager.getColor("Panel.background").getRed() + ","
					+ UIManager.getColor("Panel.background").getGreen() + ","
					+ UIManager.getColor("Panel.background").getBlue() + ")>" + "<font face='"
					+ PropertyLoader.DEFAULT_FONT.getFamily() + "' size='" + 3 + "'>"
					+ "Too many potential ORCID IDs matching your name.<br/> Please enter manual or search at  <a href='http://www.orcid.org'>www.orcid.org</a></font></body></html>");

			editorPane.setBorder(null);
			editorPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
					if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						String url = hyperlinkEvent.getURL().toString();
						EdalHelpers.openURL(url);
					}
				}
			});
			editorPane.setEditable(false);

			JOptionPane.showMessageDialog(parent, editorPane, "Too many ORCIDs found", JOptionPane.WARNING_MESSAGE);

		} else {
			this.dispose();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof OrcidButton) {

			OrcidButton button = (OrcidButton) e.getSource();
			this.setSelectedOrcid(button.getOrcid());
			dispose();

		} else {
			JButton button = (JButton) e.getSource();
			if (button.equals(this.ignoreButton)) {
				JOptionPane.showMessageDialog(this, "It is expressly recommended to enter the ORCID of all authors",
						"No ORCID selected", JOptionPane.WARNING_MESSAGE);
				dispose();
			} else if (button.equals(this.redirectButton)) {
				dispose();
				EdalHelpers.openURL("https://orcid.org/orcid-search/search");
			}
		}
	}

	public String getSelectedOrcid() {
		return selectedOrcid;
	}

	private void setSelectedOrcid(String selectedOrcid) {
		this.selectedOrcid = selectedOrcid;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String url = hyperlinkEvent.getURL().toString();
			EdalHelpers.openURL(url);
		}
	}

	private class OrcidButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String orcid;

		private OrcidButton(String text, String orcid) {
			super(text);
			setOrcid(orcid);
		}

		private String getOrcid() {
			return this.orcid;
		}

		private void setOrcid(String orcid) {
			this.orcid = orcid;
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		JOptionPane.showMessageDialog(this, "It is expressly recommended to enter the ORCID of all authors",
				"No ORCID selected", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
