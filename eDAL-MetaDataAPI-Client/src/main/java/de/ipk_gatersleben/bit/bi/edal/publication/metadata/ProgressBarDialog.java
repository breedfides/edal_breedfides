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
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Path;
import java.util.Calendar;

import javax.mail.internet.InternetAddress;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationVeloCityCreater;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class ProgressBarDialog extends JDialog implements ActionListener, ItemListener, MouseListener {

	private static final long serialVersionUID = 332545922334445010L;

	private JProgressBar overallProgressBar;
	private JProgressBar fileProgressBar;
	private JLabel overallProgressLabel = new JLabel("overall process:");
	private JLabel fileProgressLabel = new JLabel("file process:");

	private JButton submitButton = new JButton("Submit");
	private JButton backButton = new JButton("Back");
	private JButton quitButton = new JButton("Quit");
	public JCheckBox checkBox = new JCheckBox("I accept the Deposition and License Agreement");
	private JLabel label1 = new JLabel("[");
	private JLabel pdfLabel = new JLabel(
			"<html><a href=\"" + PropertyLoader.PGP_CONTRACT_URL.toString() + "\">PDF</a></html>",
			PropertyLoader.PDF_ICON, SwingConstants.CENTER);
	private JLabel adobeLabel = new JLabel("file, get free Adobe Reader");
	private JLabel adobeLink = new JLabel("<html><a href=\"https://get.adobe.com/de/reader/\">here</a></html>",
			PropertyLoader.ADOBE_ICON, SwingConstants.CENTER);
	private JLabel label2 = new JLabel("]");

	private JEditorPane htmlPanel = new JEditorPane();

	private Path files;
	private MetaData metaData;
	private ClientPrimaryDataDirectory userDirectory;
	private InternetAddress loggedUser;
	private Calendar embargoDate;
	private ClientDataManager clientDataManager;

	private int returnValue = 0;

	public ProgressBarDialog(Frame parent, int numberOfFiles, Path files, MetaData metaData,
			ClientPrimaryDataDirectory userDirectory, InternetAddress loggedUser, Calendar embargoDate,
			ClientDataManager clientDataManager) {

		super(parent, "Upload : '" + files.getFileName().toString() + "'", true);

		this.checkBox.setEnabled(true);
		this.checkBox.addItemListener(this);

		this.submitButton.setEnabled(false);

		this.setFocusable(true);
		this.setIconImage(PropertyLoader.EDAL_ICON);
		this.files = files;
		this.submitButton.addActionListener(this);
		this.backButton.addActionListener(this);

		this.overallProgressBar = new JProgressBar(0, Math.max(1, numberOfFiles));
		this.overallProgressBar.setValue(0);
		this.overallProgressBar.setStringPainted(true);

		this.fileProgressBar = new JProgressBar();
		this.fileProgressBar.setValue(0);
		this.fileProgressBar.setString("0%");
		this.fileProgressBar.setStringPainted(true);
		this.metaData = metaData;
		this.userDirectory = userDirectory;
		this.loggedUser = loggedUser;
		this.embargoDate = embargoDate;
		this.clientDataManager = clientDataManager;

		this.quitButton.addActionListener(this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		final JPanel barPanel = new JPanel(gridBagLayout);

		barPanel.setMaximumSize(new Dimension(800, 80));

		Utils.addComponent(barPanel, gridBagLayout, this.overallProgressLabel, 0, 0, 1, 1, 0.05, 1, 1, 1,
				new Insets(5, 5, 5, 5));
		Utils.addComponent(barPanel, gridBagLayout, this.fileProgressLabel, 0, 1, 1, 1, 0.05, 1, 1, 1,
				new Insets(5, 5, 5, 5));
		Utils.addComponent(barPanel, gridBagLayout, this.overallProgressBar, 1, 0, 1, 1, 1, 1, 1, 1,
				new Insets(5, 5, 5, 5));
		Utils.addComponent(barPanel, gridBagLayout, this.fileProgressBar, 1, 1, 1, 1, 1, 1, 1, 1,
				new Insets(5, 5, 5, 5));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		this.htmlPanel.setContentType("text/html");

		try {
			htmlPanel.setText(PublicationVeloCityCreater.generateHtmlForProcessDialog());
		} catch (EdalException e) {
			e.printStackTrace();
		}

		this.htmlPanel.setEditable(false);
		this.htmlPanel.setBorder(BorderFactory.createEmptyBorder());

		mainPanel.add(this.htmlPanel);
		mainPanel.add(barPanel);

		this.pdfLabel.addMouseListener(this);
		this.adobeLink.addMouseListener(this);
		this.pdfLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.adobeLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		checkBoxPanel.add(this.checkBox);
		checkBoxPanel.add(this.label1);
		checkBoxPanel.add(this.pdfLabel);
		checkBoxPanel.add(this.adobeLabel);
		checkBoxPanel.add(this.adobeLink);
		checkBoxPanel.add(this.label2);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

		buttonPanel.add(this.submitButton);
		buttonPanel.add(this.backButton);
		buttonPanel.add(this.quitButton);
		mainPanel.add(checkBoxPanel);
		mainPanel.add(buttonPanel);

		this.setContentPane(mainPanel);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(500,400));
		this.pack();

		this.setLocationRelativeTo(null);

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(this.submitButton)) {

			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

			this.submitButton.setEnabled(false);
			this.backButton.setEnabled(false);
			this.quitButton.setEnabled(false);
			this.checkBox.setEnabled(false);

			ProgressSwingWorker worker = new ProgressSwingWorker(this.overallProgressBar, this.fileProgressBar,
					this.submitButton, this.backButton, this.quitButton, this.htmlPanel, this.files, this.metaData, this.userDirectory,
					this.loggedUser, this.embargoDate, this.clientDataManager);

			worker.execute();

			this.returnValue = 1;

		}

		else if (actionEvent.getSource().equals(this.backButton)) {
			this.dispose();
		} else if (actionEvent.getSource().equals(this.quitButton)) {
			int result = JOptionPane.showConfirmDialog(null, "Close " + PropertyLoader.PROGRAM_NAME + " ?", "EXIT",
					JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}

	}

	public void showDialog() {
		if (this.overallProgressBar != null) {
			this.setVisible(true);
		} else {
			this.dispose();
		}
	}

	public int getReturnValue() {
		return this.returnValue;
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getSource().equals(this.checkBox)) {
			if (this.checkBox.isEnabled()) {
				if (this.checkBox.isSelected()) {
					this.submitButton.setEnabled(true);
				}
				if (!this.checkBox.isSelected()) {
					this.submitButton.setEnabled(false);

				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getSource().equals(this.pdfLabel)) {
			EdalHelpers.openURL(PropertyLoader.PGP_CONTRACT_URL.toString());
		}
		if (event.getSource().equals(this.adobeLink)) {
			EdalHelpers.openURL(PropertyLoader.ADOBE_GET_URL.toString());
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}