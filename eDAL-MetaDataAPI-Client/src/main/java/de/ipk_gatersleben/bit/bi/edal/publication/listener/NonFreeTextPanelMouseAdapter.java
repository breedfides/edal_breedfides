/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.listener;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeSplitPane;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeTextArea;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;

public class NonFreeTextPanelMouseAdapter extends MouseAdapter {

	private static String lastUploadPath = "";

	private static final String MESSAGE_OF_UPLOAD_HINT = "<html>Please note: <ul><li>to upload a set of files, please supply and select a directory</li><li>please upload only uncompressed folders and files</li></ul></html>";
	private static final int HOURS_TO_SHOW_HINT_DIALOG_AGAIN = 24;

	static {
		lastUploadPath = "none";
	}

	public enum PanelType {
		AUTHOR_PANEL, PUBLISHER_PANEL, LANGUAGE_PANEL, SUBJECT_PANEL, UPLOAD_PANEL, EMBARGO_PANEL, RESOURCE_PANEL;
	}

	private enum ARCHIVE_TYPES {
		ZIP(".zip"), ZIPX(".zipx"), TAR(".tar"), TARGZ(".tar.gz"), SEVENZ(".7z"), RAR(".rar"), TGZ(".tgz"), ARJ(
				".arj"), BZIP(".bzip"), BZIP2("bzip2"), BZ(".bz"), BZ2("bz2");

		private String string;

		ARCHIVE_TYPES(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	private PanelType panelType;

	public NonFreeTextPanelMouseAdapter(PanelType panelType) {
		this.panelType = panelType;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.mouseClicked(e);
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {

		switch (this.panelType) {

		case AUTHOR_PANEL:

			PublicationMainPanel.blockForAuthorsField();
			((AttributeSplitPane) PublicationMainPanel.titleAuthorSplitPanel).setRightComponent(null);
			((AttributeSplitPane) PublicationMainPanel.titleAuthorSplitPanel)
					.setRightComponent(PublicationMainPanel.authorPanel);
			break;

		case PUBLISHER_PANEL:
			PublicationMainPanel.blockForPublisherField();
			((AttributeSplitPane) PublicationMainPanel.subjectsPublisherPanel).setRightComponent(null);
			((AttributeSplitPane) PublicationMainPanel.subjectsPublisherPanel)
					.setRightComponent(PublicationMainPanel.publisherPanel);
			break;

		case LANGUAGE_PANEL:
			PublicationMainPanel.blockForLanguageField();
			PublicationMainPanel.languageResourcePanel
					.remove(((BorderLayout) PublicationMainPanel.languageResourcePanel.getLayout())
							.getLayoutComponent(BorderLayout.NORTH));
			PublicationMainPanel.languageResourcePanel.add(PublicationMainPanel.languagePanel, BorderLayout.NORTH);

			break;

		case RESOURCE_PANEL:

			PublicationMainPanel.blockForResourceField();
			PublicationMainPanel.languageResourcePanel
					.remove(((BorderLayout) PublicationMainPanel.languageResourcePanel.getLayout())
							.getLayoutComponent(BorderLayout.SOUTH));
			PublicationMainPanel.languageResourcePanel.add(PublicationMainPanel.resourcePanel, BorderLayout.SOUTH);

			break;

		case SUBJECT_PANEL:

			PublicationMainPanel.blockForSubjectsField();
			((AttributeSplitPane) PublicationMainPanel.descriptionSubjectsSplitPanel).setRightComponent(null);
			((AttributeSplitPane) PublicationMainPanel.descriptionSubjectsSplitPanel)
					.setRightComponent(PublicationMainPanel.subjectPanel);

			break;

		case UPLOAD_PANEL:

			PropertyLoader.UPLOADPATH_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			AttributeTextArea focusedField = (AttributeTextArea) mouseEvent.getComponent();

			String path = "";
			if (!PublicationMainPanel.uploadPathField.getText()
					.equals(PropertyLoader.props.getProperty("DEFAULT_UPLOAD_PATH_STRING"))) {
				path = PublicationMainPanel.uploadPathField.getText();
			}

			if (!NonFreeTextPanelMouseAdapter.lastUploadPath.equals("none")) {
				path = NonFreeTextPanelMouseAdapter.lastUploadPath;
			}

			final JFileChooser chooser = new JFileChooser(path);

			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			// boolean ready = false;
			//
			// while (!ready) {
			// if (chooser.showOpenDialog(null) ==
			// JFileChooser.APPROVE_OPTION) {
			// path = chooser.getSelectedFile().getAbsolutePath();
			// if (path != null) {
			// if (chooser.getSelectedFile().isFile()) {
			// if (chooser.getSelectedFile().getName().endsWith(".zip")) {
			// Object[] options = { "Ignore", "Change" };
			//
			// int selected = JOptionPane.showOptionDialog(null,
			// "Please note that publishing files as archive avoid access to
			// single elements and requires a complete download, \neven if
			// only one file is required by the user. Thus, please
			// uncompress your data and upload as folder. \ne!DAL will
			// provide features to download folders as zip archives at whole
			// at the web site.",
			// "Warning: File archive selected", JOptionPane.DEFAULT_OPTION,
			// JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			//
			// if (selected == 0) {
			// ready = true;
			// }
			// }
			// } else {
			// ready = true;
			// }
			// if (ready) {
			// if (Files.exists(chooser.getSelectedFile().toPath(),
			// LinkOption.NOFOLLOW_LINKS)) {
			// focusedField.setText(path);
			// focusedField.setToolTipText(path);
			// NonFreeTextPanelMouseAdapter.lastUploadPath = path;
			// }
			// }
			// }
			// } else {
			// ready = true;
			// }
			// }

			boolean ready = false;

			while (!ready) {

				if (!PropertyLoader.userValues.containsKey("dontShowUploadHint")
						|| (PropertyLoader.userValues.containsKey("dontShowUploadHint")
								&& PropertyLoader.userValues.getProperty("dontShowUploadHint").equals("false"))
						|| PropertyLoader.userValues.containsKey("dontShowUploadHintTime")
								&& ChronoUnit.HOURS.between(
										LocalDateTime
												.parse(PropertyLoader.userValues.getProperty("dontShowUploadHintTime")),
										LocalDateTime.now()) >= HOURS_TO_SHOW_HINT_DIALOG_AGAIN) {

					JCheckBox checkbox = new JCheckBox("Do not show this message again.");
					JEditorPane editorPane = new JEditorPane();

					editorPane.setContentType("text/html");
					editorPane.setText(MESSAGE_OF_UPLOAD_HINT);
					// String message = MESSAGE_OF_UPLOAD_HINT;
					Object[] params = { MESSAGE_OF_UPLOAD_HINT, checkbox };
					JOptionPane.showMessageDialog(null, params, "Did you already know ?",
							JOptionPane.INFORMATION_MESSAGE);
					boolean dontShow = checkbox.isSelected();

					if (dontShow) {
						PropertyLoader.userValues.setProperty("dontShowUploadHint", "true");
						PropertyLoader.userValues.setProperty("dontShowUploadHintTime", LocalDateTime.now().toString());
					} else {
						PropertyLoader.userValues.setProperty("dontShowUploadHint", "false");
						PropertyLoader.userValues.setProperty("dontShowUploadHintTime", LocalDateTime.now().toString());
					}
				}
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					path = chooser.getSelectedFile().getAbsolutePath();
					if (path != null) {
						if (chooser.getSelectedFile().isFile()) {

							for (ARCHIVE_TYPES element : ARCHIVE_TYPES.values()) {
								if (chooser.getSelectedFile().getName().endsWith(element.string)) {
									Object[] options = { "Change" };

									int selected = JOptionPane.showOptionDialog(null,
											"Please note that publishing files as archive avoid access to single elements and requires a complete download, \neven if only one file is required by the user. Thus, please uncompress your data and upload as folder. \ne!DAL will provide features to download folders as zip archives at whole at the web site.",
											"Warning: File archive selected", JOptionPane.DEFAULT_OPTION,
											JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

									if (selected == 0) {
										ready = false;
										break;
									}
								} else {
									ready = true;
								}
							}

						} else {
							ready = true;
						}
						if (ready) {
							if (Files.exists(chooser.getSelectedFile().toPath(), LinkOption.NOFOLLOW_LINKS)) {
								focusedField.setText(path);
								focusedField.setToolTipText(path);
								NonFreeTextPanelMouseAdapter.lastUploadPath = path;
							}
						}
					}
				} else {
					ready = true;
				}
			}

			PropertyLoader.UPLOADPATH_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
			PropertyLoader.setUserValue("UPLOAD_PATH", lastUploadPath);
			break;

		case EMBARGO_PANEL:

			PublicationMainPanel.blockForEmbargoField();
			PublicationMainPanel.embargoLanguageResourceLicensePanel
					.remove(((BorderLayout) PublicationMainPanel.embargoLanguageResourceLicensePanel.getLayout())
							.getLayoutComponent(BorderLayout.SOUTH));
			PublicationMainPanel.embargoLanguageResourceLicensePanel.add(PublicationMainPanel.embargboPanel,
					BorderLayout.SOUTH);
			break;

		}
		PublicationFrame.updateUI();

	}

}
