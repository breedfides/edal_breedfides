/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.ShutdownWindowsListener;
import de.ipk_gatersleben.bit.bi.edal.publication.metadata.MetaDataCollector;
import de.ipk_gatersleben.bit.bi.edal.publication.metadata.ProgressBarDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;

public class PublicationButtonLinePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -6295685143671113656L;
	private static SmallButton NEXT_BUTTON = new SmallButton("Next");
	private static SmallButton QUIT_BUTTON = new SmallButton("Quit");
	public static boolean updatePublicationFlag = false;
	private ClientDataManager clientDataManager = null;

	public PublicationButtonLinePanel(ClientDataManager clientDataManager) {

		this.clientDataManager = clientDataManager;

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

		JPanel field = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel("* - required fields");

		label.setForeground(Color.RED);
		label.setFont(PropertyLoader.DEFAULT_FONT);
		field.add(label);
		field.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		buttonPanel.add(NEXT_BUTTON);
		buttonPanel.add(QUIT_BUTTON);
		buttonPanel.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		NEXT_BUTTON.addActionListener(this);
		QUIT_BUTTON.addActionListener(this);

		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		this.setLayout(new GridLayout(1, 1));
		this.add(field);
		this.add(buttonPanel);
		this.setBorder(new MatteBorder(2, 0, 0, 0, Color.BLACK));

	}

	public static JButton getNextButton() {
		return NEXT_BUTTON;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		boolean everythingCorrect = true;

		if (actionEvent.getSource().equals(NEXT_BUTTON)) {

			if (PublicationMainPanel.uploadPathField.getText()
					.equalsIgnoreCase(PropertyLoader.props.getProperty("DEFAULT_UPLOAD_PATH_STRING"))) {

				PropertyLoader.UPLOADPATH_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (PublicationMainPanel.descriptionField.getText().trim()
					.equalsIgnoreCase(PropertyLoader.props.getProperty("DEFAULT_DESCRIPTION_STRING").trim())) {
				PropertyLoader.DESCRIPTION_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (PublicationMainPanel.authorsField.getText().trim()
					.equalsIgnoreCase(PropertyLoader.props.getProperty("DEFAULT_AUTHORS_STRING").trim())) {

				PropertyLoader.AUTHORS_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (Utils.checkIfStringIsEmpty(PublicationMainPanel.publisherField.getText())) {

				PropertyLoader.PUBLISHER_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (PublicationMainPanel.subjectsField.getText().trim()
					.equalsIgnoreCase(PropertyLoader.props.getProperty("DEFAULT_SUBJECTS_STRING").trim())) {

				PropertyLoader.SUBJECTS_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (!PublicationMainPanel.licensePanel.isLicenseSelected()) {
				PropertyLoader.LICENSE_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			}

			if (PublicationMainPanel.titleField.getText().trim()
					.equalsIgnoreCase(PublicationMainPanel.DEFAULT_TITLE_STRING.trim())) {

				PropertyLoader.TITLE_LABEL.setForeground(Color.RED);
				PublicationFrame.updateUI();
				everythingCorrect = false;
			} else {
				try {

					if (PublicationMainPanel.titleField.getText().length() > 4000) {
						JOptionPane.showMessageDialog(null,
								"The title is too long. Please reduce the length");
						PublicationButtonLinePanel.updatePublicationFlag = false;
						PublicationMainPanel.titleField.grabFocus();
						everythingCorrect = false;
					} else {
						PublicationFrame.getUserDirectory()
								.getPrimaryDataEntity(PublicationMainPanel.titleField.getText());
						PropertyLoader.TITLE_LABEL.setForeground(Color.RED);

						JOptionPane.showMessageDialog(null,
								"A dataset with this title already exist. Please cancel the previous request or choose a different title.");
						PublicationButtonLinePanel.updatePublicationFlag = false;
						PublicationMainPanel.titleField.grabFocus();
						everythingCorrect = false;
					}
					// Object[] options = { "Yes", "No" };
					// int result = JOptionPane.showOptionDialog(null,
					// "Would you like to update the version? If not please change the title
					// (recommended)!",
					// "A dataset with this title already exist !", JOptionPane.YES_NO_OPTION,
					// JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					// if (result == JOptionPane.YES_OPTION) {
					// PublicationButtonLinePanel.updatePublicationFlag = true;
					// }
					// if (result == JOptionPane.NO_OPTION) {
					// PublicationButtonLinePanel.updatePublicationFlag = false;
					// PublicationMainPanel.titleField.grabFocus();
					// everythingCorrect = false;
					// }
				} catch (RemoteException | PrimaryDataDirectoryException e) {

				}
			}

			if (everythingCorrect) {

				MetaDataCollector collector = new MetaDataCollector();
				collector.collectAllMetaData();

				MetaData metaData = this.clientDataManager.createMetadataInstance();

				try {
					metaData.setElementValue(EnumDublinCoreElements.CREATOR, collector.getCreators());
					metaData.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, collector.getContributors());
					metaData.setElementValue(EnumDublinCoreElements.SUBJECT, collector.getSubjects());
					metaData.setElementValue(EnumDublinCoreElements.LANGUAGE, collector.getLanguage());
					metaData.setElementValue(EnumDublinCoreElements.RIGHTS, collector.getLicense());
					metaData.setElementValue(EnumDublinCoreElements.DESCRIPTION, collector.getDescription());
					metaData.setElementValue(EnumDublinCoreElements.PUBLISHER, collector.getPublisher());
					metaData.setElementValue(EnumDublinCoreElements.TITLE, collector.getTitle());
				} catch (MetaDataException e) {
					e.printStackTrace();
				}

				try {

					int numberOfFiles = Utils.countObjectsWithErrorsAndWarnings(null,
							Paths.get(PublicationMainPanel.uploadPathField.getText()));

					if (numberOfFiles != 0) {

						ProgressBarDialog dialog = new ProgressBarDialog(null, numberOfFiles,
								Paths.get(PublicationMainPanel.uploadPathField.getText()), metaData,
								PublicationFrame.getUserDirectory(), new InternetAddress(PublicationFrame.loggedUser),
								collector.getEmbargoDate(), this.clientDataManager);

						dialog.showDialog();

						if (dialog.getReturnValue() == 1) {
							PublicationMainPanel.reset();
						}
					}

				} catch (AddressException e) {
					e.printStackTrace();
				}

			}

		} else if (actionEvent.getSource().equals(QUIT_BUTTON)) {
			ShutdownWindowsListener shutdown = new ShutdownWindowsListener();
			shutdown.windowClosing(null);
		}
	}

}
