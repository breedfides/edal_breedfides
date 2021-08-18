/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.publication.listener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeSplitPane;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeTextArea;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.AuthorsPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.RelatedIdentifierPanel;

public class NonFreeTextPanelMouseAdapter extends MouseAdapter {

	private static String lastUploadPath = "";
	private static String lastRelatedIdentifierPath = "";

	private static final String MESSAGE_OF_UPLOAD_HINT = "<html>Please note: <ul><li>to upload a set of files, please supply and select a directory</li><li>please upload only uncompressed folders and files</li></ul></html>";
	private static final int HOURS_TO_SHOW_HINT_DIALOG_AGAIN = 24;

	static {
		lastUploadPath = "none";
	}

	public enum PanelType {
		AUTHOR_PANEL, PUBLISHER_PANEL, LANGUAGE_PANEL, SUBJECT_PANEL, UPLOAD_PANEL, EMBARGO_PANEL, RESOURCE_PANEL,
		RELATED_IDENTIFIER_PANEL;
	}

	private enum ARCHIVE_TYPES {
		ZIP(".zip"), ZIPX(".zipx"), TAR(".tar"), TARGZ(".tar.gz"), SEVENZ(".7z"), RAR(".rar"), TGZ(".tgz"), ARJ(".arj"),
		BZIP(".bzip"), BZIP2("bzip2"), BZ(".bz"), BZ2("bz2");

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
			((AttributeSplitPane) PublicationMainPanel.subjectsRelatedIdentiferPanel).setRightComponent(null);
			((AttributeSplitPane) PublicationMainPanel.subjectsRelatedIdentiferPanel)
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
						|| PropertyLoader.userValues.containsKey("dontShowUploadHintTime") && ChronoUnit.HOURS.between(
								LocalDateTime.parse(PropertyLoader.userValues.getProperty("dontShowUploadHintTime")),
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

		case RELATED_IDENTIFIER_PANEL:

			PropertyLoader.RELATED_IDENTIFIER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			AttributeTextArea focusedFieldForRelatedIdentifier = (AttributeTextArea) mouseEvent.getComponent();

			String pathForRelatedIdentifier = "";
			if (!PublicationMainPanel.relatedIdentifierPathField.getText()
					.equals(PropertyLoader.props.getProperty("DEFAULT_RELATED_IDNETIFIER_PATH_STRING"))) {
				pathForRelatedIdentifier = PublicationMainPanel.relatedIdentifierPathField.getText();
			}

			if (!NonFreeTextPanelMouseAdapter.lastRelatedIdentifierPath.equals("none")) {
				pathForRelatedIdentifier = NonFreeTextPanelMouseAdapter.lastRelatedIdentifierPath;
			}

			final JFileChooser chooserForRelatedIdentifier = new JFileChooser(pathForRelatedIdentifier);

			chooserForRelatedIdentifier.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			boolean readyForRelatedIdentifier = false;

			while (!readyForRelatedIdentifier) {

				if (chooserForRelatedIdentifier.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					pathForRelatedIdentifier = chooserForRelatedIdentifier.getSelectedFile().getAbsolutePath();
					if (pathForRelatedIdentifier != null) {

						if (chooserForRelatedIdentifier.getSelectedFile().isFile()) {
							System.out.println(pathForRelatedIdentifier);
							readyForRelatedIdentifier = true;

							try {

								FileInputStream fileInputStream = new FileInputStream(
										chooserForRelatedIdentifier.getSelectedFile());

								Workbook workbook = new XSSFWorkbook(fileInputStream);

								Sheet sheet = workbook.getSheetAt(0);

								XSSFRow firstRow = (XSSFRow) sheet.getRow(0);

								int colNum = firstRow.getLastCellNum();

								// with +1 to start also with header
								int rowNum = sheet.getLastRowNum();// + 1;

								Object[][] datatable = new Object[rowNum][colNum];
								String[] colNames = new String[colNum];

								// to separate colNames from data
								int skipHeader = 0;

								for (Row row : sheet) {

									if (skipHeader != 0) {
										// fill data table
										for (Cell cell : row) {

											System.out.println("Zeile: " + (row.getRowNum() - 1) + " Spalte: "
													+ cell.getColumnIndex());
											switch (cell.getCellType()) {

											case STRING:
												System.out.println(cell.getStringCellValue());
												datatable[row.getRowNum() - 1][cell.getColumnIndex()] = cell
														.getStringCellValue();
												break;
											default:
												;
											}
										}
									} else {
										// fill header array
										for (Cell cell : row) {
											colNames[cell.getColumnIndex()] = cell.getStringCellValue();
										}
										skipHeader++;

									}

								}
								workbook.close();

								for (int zeilennummer = 0; zeilennummer < datatable.length; zeilennummer++) {
									for (int spaltennummer = 0; spaltennummer < datatable[zeilennummer].length; spaltennummer++) {
										System.out.println("DATA (" + zeilennummer + ")(" + spaltennummer + ")"
												+ datatable[zeilennummer][spaltennummer]);
									}

								}

								JScrollPane scrollPane = new JScrollPane(new JTable(datatable, colNames));

								scrollPane.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
								scrollPane.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH,
										PropertyLoader.AUTHOR_PANEL_HEIGHT));

								final EmptyBorder inBorder = new EmptyBorder(5, 5, 0, 5);
								final EmptyBorder outBorder = new EmptyBorder(5, 5, 0, 5);
								scrollPane.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

								PublicationMainPanel.relatedIdentifierPanel.setScrollPane(scrollPane);

								((AttributeSplitPane) PublicationMainPanel.subjectsRelatedIdentiferPanel)
										.setRightComponent(PublicationMainPanel.relatedIdentifierPanel);

								final JPanel attributePanel = new JPanel(new GridLayout());

								RelatedIdentifierPanel.RELATED_IDENTIFIER_LABEL
										.setForeground(PropertyLoader.LABEL_COLOR);

								attributePanel.add(RelatedIdentifierPanel.RELATED_IDENTIFIER_LABEL);
								attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
								attributePanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH,
										PropertyLoader.RELATED_IDENTIFIER_PANEL_HEIGHT));

								PublicationMainPanel.relatedIdentifierPanel.add(attributePanel, BorderLayout.WEST);
								PublicationMainPanel.relatedIdentifierPanel.add(scrollPane, BorderLayout.CENTER);

							} catch (IOException e) {
								e.printStackTrace();
							}

						} else {
							readyForRelatedIdentifier = true;
						}
						if (readyForRelatedIdentifier) {
							if (Files.exists(chooserForRelatedIdentifier.getSelectedFile().toPath(),
									LinkOption.NOFOLLOW_LINKS)) {
								focusedFieldForRelatedIdentifier.setText(pathForRelatedIdentifier);
								focusedFieldForRelatedIdentifier.setToolTipText(pathForRelatedIdentifier);
								NonFreeTextPanelMouseAdapter.lastRelatedIdentifierPath = pathForRelatedIdentifier;
							}
						}
					}
				} else {
					ready = true;
				}
			}

			PropertyLoader.RELATED_IDENTIFIER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
			PropertyLoader.setUserValue("RELATED_IDENTIFIER_PATH", lastRelatedIdentifierPath);

			break;
		}
		PublicationFrame.updateUI();

	}

}
