/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import javax.mail.internet.InternetAddress;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationVeloCityCreater;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

public class ProgressSwingWorker extends SwingWorker<Object, Object> {

	private JProgressBar overallProgressBar;
	private JProgressBar fileProgressBar;
	private JButton submitButton;
	private JButton backButton;
	private JButton quitButton;
	private JEditorPane htmlPanel;

	private final Path files;
	private MetaData metaData;
	private ClientPrimaryDataDirectory userDirectory;
	private InternetAddress loggedUser;
	private Calendar embargoDate = null;
	private ClientDataManager clientDataManager;

	public ProgressSwingWorker(JProgressBar overAllProgressBar, JProgressBar fileProgressBar, JButton sendButton,
			JButton backButton, JButton quitButton, JEditorPane htmlPanel, Path files, MetaData metaData,
			ClientPrimaryDataDirectory userDirectory, InternetAddress loggedUser, Calendar embargoDate,
			ClientDataManager clientDataManager) {

		this.overallProgressBar = overAllProgressBar;
		this.fileProgressBar = fileProgressBar;
		this.submitButton = sendButton;
		this.backButton = backButton;
		this.quitButton = quitButton;
		this.htmlPanel = htmlPanel;
		this.files = files;
		this.metaData = metaData;
		this.userDirectory = userDirectory;
		this.loggedUser = loggedUser;
		this.embargoDate = embargoDate;
		this.clientDataManager = clientDataManager;

	}

	private void publishDirectory(ClientPrimaryDataDirectory directory) {
		try {
			directory.switchCurrentVersion(directory.getCurrentVersion());
			directory.addPublicReference(PersistentIdentifier.DOI);
			directory.getCurrentVersion().setAllReferencesPublic(this.loggedUser, this.embargoDate);
		} catch (RemoteException | PrimaryDataEntityException | PublicReferenceException
				| PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object doInBackground() throws Exception {
		storeFiles();
		return null;
	}

	@Override
	protected void done() {

		try {
			this.htmlPanel.setText(PublicationVeloCityCreater.generateFinishUploadPage());
		} catch (EdalException e) {
			e.printStackTrace();
		}

		((ProgressBarDialog) SwingUtilities.getRoot(this.submitButton))
				.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.submitButton.setEnabled(true);
		this.submitButton.setText("Submit another dataset");
		this.backButton.setEnabled(true);
		this.backButton.setText("Quit");
		this.backButton.getParent().remove(this.backButton);
		this.quitButton.setEnabled(true);
		this.submitButton.removeActionListener(this.submitButton.getActionListeners()[0]);
		this.submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {

				if (actionEvent.getSource().equals(submitButton)) {
					SwingUtilities.getWindowAncestor(submitButton).dispose();
				}

			}
		});

		this.backButton.removeActionListener(this.backButton.getActionListeners()[0]);
		this.backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "Close " + PropertyLoader.PROGRAM_NAME + " ?", "EXIT",
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					System.exit(0);
				}

			}
		});

		try {
			this.clientDataManager.sendEmail(
					PublicationVeloCityCreater.generateEmailForAgreement(this.metaData, Calendar.getInstance()),
					PropertyLoader.props.getProperty("CONFIRMATION_EMAIL_SUBJECT"), PropertyLoader.PGP_CONTRACT_URL);
		} catch (RemoteException | EdalException e) {
			JOptionPane.showMessageDialog(null, "Unable to send Confirmation e-Mail:", e.getMessage()
					+ "\n Please download and save the DLA here: '" + PropertyLoader.PGP_CONTRACT_URL.toString() + "'",
					0);
		}
	}

	private void storeFiles() {

		if (Files.isDirectory(this.files)) {

			try {

				CountDownLatch latch = new CountDownLatch(this.overallProgressBar.getMaximum());

				PublicationDirectoryVisitorWithMetaDataRmi edalVisitor = new PublicationDirectoryVisitorWithMetaDataRmi(
						this.overallProgressBar, this.fileProgressBar, this.userDirectory, this.files, this.metaData,
						PublicationButtonLinePanel.updatePublicationFlag, true, latch);

				Files.walkFileTree(this.files, edalVisitor);

				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				this.fileProgressBar.setValue(100);
				this.fileProgressBar.setString("100%");

				publishDirectory(edalVisitor.getRootDirectoryToPublish());

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		else {

			if (PublicationButtonLinePanel.updatePublicationFlag) {

				ClientPrimaryDataDirectory directory = null;

				ClientPrimaryDataFile file = null;
				try {

					/* get existing directory by TITLE */
					directory = (ClientPrimaryDataDirectory) this.userDirectory
							.getPrimaryDataEntity(metaData.getElementValue(EnumDublinCoreElements.TITLE).toString());

					setNewMetaData(directory, metaData);

					directory.switchCurrentVersion(directory.getCurrentVersion());

					/* create new file or get existing on */
					if (directory.exist(this.files.toFile().getName())) {
						file = (ClientPrimaryDataFile) directory.getPrimaryDataEntity(this.files.toFile().getName());

					} else {
						file = directory.createPrimaryDataFile(this.files.toFile().getName());
					}
				} catch (RemoteException | PrimaryDataDirectoryException | MetaDataException
						| PrimaryDataEntityVersionException e) {
					e.printStackTrace();
				}

				setNewMetaData(file, metaData);

				CountDownLatch latch = new CountDownLatch(1);

				FileStoreSwingWorker worker = new FileStoreSwingWorker(this.fileProgressBar, this.overallProgressBar,
						this.files, file, latch);

				worker.execute();

				try {

					latch.await();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				this.fileProgressBar.setValue(100);
				this.fileProgressBar.setString("100%");

				publishDirectory(directory);
			}

			else {
				try {
					ClientPrimaryDataDirectory directory = this.userDirectory.createPrimaryDataDirectory(
							metaData.getElementValue(EnumDublinCoreElements.TITLE).toString());

					setNewMetaData(directory, metaData);

					ClientPrimaryDataFile file = directory.createPrimaryDataFile(this.files.toFile().getName());

					CountDownLatch latch = new CountDownLatch(1);

					FileStoreSwingWorker worker = new FileStoreSwingWorker(this.fileProgressBar,
							this.overallProgressBar, this.files, file, latch);

					worker.execute();

					try {

						latch.await();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					this.fileProgressBar.setValue(100);
					this.fileProgressBar.setString("100%");

					publishDirectory(directory);
				} catch (RemoteException | PrimaryDataDirectoryException | MetaDataException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void setNewMetaData(ClientPrimaryDataEntity clientPrimaryDataEntity, MetaData newMetaData) {
		try {
			MetaData metaData = clientPrimaryDataEntity.getMetaData().clone();

			metaData.setElementValue(EnumDublinCoreElements.CREATOR,
					newMetaData.getElementValue(EnumDublinCoreElements.CREATOR));
			metaData.setElementValue(EnumDublinCoreElements.CONTRIBUTOR,
					newMetaData.getElementValue(EnumDublinCoreElements.CONTRIBUTOR));
			metaData.setElementValue(EnumDublinCoreElements.SUBJECT,
					newMetaData.getElementValue(EnumDublinCoreElements.SUBJECT));
			metaData.setElementValue(EnumDublinCoreElements.LANGUAGE,
					newMetaData.getElementValue(EnumDublinCoreElements.LANGUAGE));
			metaData.setElementValue(EnumDublinCoreElements.DESCRIPTION,
					newMetaData.getElementValue(EnumDublinCoreElements.DESCRIPTION));
			metaData.setElementValue(EnumDublinCoreElements.PUBLISHER,
					newMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER));
			metaData.setElementValue(EnumDublinCoreElements.RIGHTS,
					newMetaData.getElementValue(EnumDublinCoreElements.RIGHTS));

			clientPrimaryDataEntity.setMetaData(metaData);

		} catch (RemoteException | CloneNotSupportedException | PrimaryDataEntityVersionException
				| MetaDataException e) {
			e.printStackTrace();
		}

	}

}
