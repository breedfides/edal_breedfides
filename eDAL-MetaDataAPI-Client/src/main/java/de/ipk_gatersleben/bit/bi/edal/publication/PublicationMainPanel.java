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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.AuthorsPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.EmbargoPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.LanguagePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.LicenseCheckBoxPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.ResourcePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.PublisherPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.SubjectPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.NonFreeTextPanelMouseAdapter;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.NonFreeTextPanelMouseAdapter.PanelType;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.TextAreaCheckFocusListener;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.TextAreaValueChangedFocusListener;

public class PublicationMainPanel extends JPanel {

	protected static class BlockedFieldMouseAdapter extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
					PropertyLoader.props.getProperty("PUBLICATION_PANEL_BLOCKED_WARNING"),
					PropertyLoader.props.getProperty("PUBLICATION_PANEL_BLOCKED_WARNING_TITLE"),
					JOptionPane.INFORMATION_MESSAGE);
		}

		public void mousePressed(MouseEvent e) {
			JOptionPane.showMessageDialog(PublicationFrame.getMainPanel(),
					PropertyLoader.props.getProperty("PUBLICATION_PANEL_BLOCKED_WARNING"),
					PropertyLoader.props.getProperty("PUBLICATION_PANEL_BLOCKED_WARNING_TITLE"),
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private static final long serialVersionUID = -894068063132047509L;
	public static final String DEFAULT_UPLOAD_PATH_STRING = PropertyLoader.loadUploadPathString();
	public static final String DEFAULT_TITLE_STRING = PropertyLoader.props.getProperty("DEFAULT_TITLE_STRING");
	public static final String DEFAULT_LANGUAGE_STRING = PropertyLoader.loadLanguageString();
	public static final String DEFAULT_RESOURCE_STRING = PropertyLoader.loadResourceString();
	public static final String DEFAULT_PUBLISHER_STRING = PropertyLoader.loadPublisherString();
	public static final String DEFAULT_DESCRIPTION_STRING = PropertyLoader.props
			.getProperty("DEFAULT_DESCRIPTION_STRING");
	public static final String DEFAULT_AUTHORS_STRING = PropertyLoader.loadAuthorsString();
	public static final String DEFAULT_SUBJECTS_STRING = PropertyLoader.loadSubjectsString();

	public static final String DEFAULT_EMBARGO_STRING = PropertyLoader.props.getProperty("DEFAULT_EMBARGO_STRING");
	public static AttributeTextArea uploadPathField = new AttributeTextArea(DEFAULT_UPLOAD_PATH_STRING, false, true);
	public static AttributeTextArea titleField = new AttributeTextArea(DEFAULT_TITLE_STRING, true, true);
	public static AttributeTextArea descriptionField = new AttributeTextArea(DEFAULT_DESCRIPTION_STRING, true, false);
	public static AttributeTextArea authorsField = new AttributeTextArea(DEFAULT_AUTHORS_STRING, false, true);
	public static AttributeTextArea subjectsField = new AttributeTextArea(DEFAULT_SUBJECTS_STRING, false, true);
	public static AttributeTextArea languageField = new AttributeTextArea(DEFAULT_LANGUAGE_STRING, false, true);

	public static AttributeTextArea resourceField = new AttributeTextArea(DEFAULT_RESOURCE_STRING, false, true);
	public static AttributeTextArea publisherField = new AttributeTextArea(DEFAULT_PUBLISHER_STRING, false, true);

	public static AttributeTextArea embargoField = new AttributeTextArea(DEFAULT_EMBARGO_STRING, false, true);
	public static AuthorsPanel authorPanel;
	public static LanguagePanel languagePanel;
	public static SubjectPanel subjectPanel;
	public static PublisherPanel publisherPanel;
	public static EmbargoPanel embargboPanel;
	public static ResourcePanel resourcePanel;

	public static LicenseCheckBoxPanel licensePanel;
	private static NonFreeTextPanelMouseAdapter openAuthorPanelListener;
	private static NonFreeTextPanelMouseAdapter openLanguagePanelListener;
	private static NonFreeTextPanelMouseAdapter openUploadPanelListener;
	private static NonFreeTextPanelMouseAdapter openSubjectPanelListener;
	private static NonFreeTextPanelMouseAdapter openPublisherPanelListener;
	private static NonFreeTextPanelMouseAdapter openEmbargoPanelListener;
	private static NonFreeTextPanelMouseAdapter openResourcePanelListener;

	static BlockedFieldMouseAdapter blockedFieldMouseAdapter = new BlockedFieldMouseAdapter();

	private static JPanel mainPanel;

	static {
		authorPanel = new AuthorsPanel();
		languagePanel = new LanguagePanel();
		publisherPanel = new PublisherPanel();
		subjectPanel = new SubjectPanel();
		embargboPanel = new EmbargoPanel();
		licensePanel = new LicenseCheckBoxPanel();
		resourcePanel = new ResourcePanel();

		openAuthorPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.AUTHOR_PANEL);
		openLanguagePanelListener = new NonFreeTextPanelMouseAdapter(PanelType.LANGUAGE_PANEL);
		openUploadPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.UPLOAD_PANEL);
		openSubjectPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.SUBJECT_PANEL);
		openPublisherPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.PUBLISHER_PANEL);
		openEmbargoPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.EMBARGO_PANEL);
		openResourcePanelListener = new NonFreeTextPanelMouseAdapter(PanelType.RESOURCE_PANEL);
	}
	public static AttributeSplitPane titleAuthorSplitPanel = null;
	public static AttributeSplitPane authorDescriptionSplitPanel = null;
	public static AttributeSplitPane descriptionSubjectsSplitPanel = null;
	public static AttributeSplitPane subjectsPublisherPanel = null;

	public static JPanel embargoLanguageResourceLicensePanel = null;
	public static JPanel languageResourcePanel = null;

	/**
	 * Block all textfields except the Authorsfield
	 * 
	 */
	public static void blockForAuthorsField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeLanguageFieldListener();
		removeUploadPanelListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();

	}

	public static void blockForEmbargoField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeAuthorPanelListener();
		removeUploadPanelListener();
		removeLanguageFieldListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
	}

	/**
	 * Block all fields except the LanguageField
	 */
	public static void blockForLanguageField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeAuthorPanelListener();
		removeUploadPanelListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
	}

	/**
	 * Block all fields except the ResourceField
	 */
	public static void blockForResourceField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeAuthorPanelListener();
		removeUploadPanelListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeEmbargoFieldListener();
		removeLanguageFieldListener();
		removeLicenseFieldListener();
	}

	public static void blockForPublisherField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeAuthorPanelListener();
		removeUploadPanelListener();
		removeLanguageFieldListener();
		removeSubjectFieldListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
	}

	public static void blockForSubjectsField() {
		removeTitleFieldListener();
		removeDescriptionFieldListener();
		removeLanguageFieldListener();
		removeUploadPanelListener();
		removeAuthorPanelListener();
		removePublisherPanelListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
	}

	public static void blockForTitleField() {
		removeDescriptionFieldListener();
		removeAuthorPanelListener();
		removeUploadPanelListener();
		removeLanguageFieldListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
	}

	public static JPanel getMainPanel() {
		return PublicationMainPanel.mainPanel;
	}

	public static void releaseAllBlockedFields() {

		titleField.setEditable(true);
		titleField.setFocusable(true);
		titleField.setEnabled(true);

		descriptionField.setEditable(true);
		descriptionField.setFocusable(true);
		descriptionField.setEnabled(true);

		uploadPathField.setFocusable(true);

		boolean hasLanguageListener = false;

		for (MouseListener iterable_element : languageField.getMouseListeners()) {
			if (iterable_element.equals(openLanguagePanelListener)) {
				hasLanguageListener = true;
			}
		}

		if (!hasLanguageListener) {
			languageField.addMouseListener(openLanguagePanelListener);
			languageField.setEnabled(true);
		}

		boolean hasResourceListener = false;

		for (MouseListener iterable_element : resourceField.getMouseListeners()) {
			if (iterable_element.equals(openResourcePanelListener)) {
				hasResourceListener = true;
			}
		}

		if (!hasResourceListener) {
			resourceField.addMouseListener(openResourcePanelListener);
			resourceField.setEnabled(true);
		}

		boolean hasAuthorListener = false;

		for (MouseListener iterable_element : authorsField.getMouseListeners()) {
			if (iterable_element.equals(openAuthorPanelListener)) {
				hasAuthorListener = true;
			}
		}

		if (!hasAuthorListener) {
			authorsField.addMouseListener(openAuthorPanelListener);
			authorsField.setEnabled(true);
		}

		boolean hasUploadPathListener = false;

		for (MouseListener iterable_element : uploadPathField.getMouseListeners()) {
			if (iterable_element.equals(openUploadPanelListener)) {
				hasUploadPathListener = true;
			}
		}

		if (!hasUploadPathListener) {
			uploadPathField.addMouseListener(openUploadPanelListener);
			uploadPathField.setEnabled(true);
		}

		boolean hasSubjectListener = false;

		for (MouseListener iterable_element : subjectsField.getMouseListeners()) {
			if (iterable_element.equals(openSubjectPanelListener)) {
				hasSubjectListener = true;
			}
		}

		if (!hasSubjectListener) {
			subjectsField.addMouseListener(openSubjectPanelListener);
			subjectsField.setEnabled(true);
		}

		boolean hasPublisherListener = false;

		for (MouseListener iterable_element : publisherField.getMouseListeners()) {
			if (iterable_element.equals(openPublisherPanelListener)) {
				hasPublisherListener = true;
			}
		}

		if (!hasPublisherListener) {
			publisherField.addMouseListener(openPublisherPanelListener);
			publisherField.setEnabled(true);
		}

		boolean hasEmbargoListener = false;

		for (MouseListener iterable_element : embargoField.getMouseListeners()) {
			if (iterable_element.equals(openEmbargoPanelListener)) {
				hasEmbargoListener = true;
			}
		}

		if (!hasEmbargoListener) {
			embargoField.addMouseListener(openEmbargoPanelListener);
			embargoField.setEnabled(true);
		}

		licensePanel.setEnabled(true);
	}

	private static void removeAuthorPanelListener() {
		for (MouseListener iterable_element : authorsField.getMouseListeners()) {
			if (iterable_element.equals(openAuthorPanelListener)) {
				authorsField.removeMouseListener(openAuthorPanelListener);
				authorsField.setEnabled(false);
			}
		}
	}

	private static void removeDescriptionFieldListener() {
		descriptionField.setEditable(false);
		descriptionField.setFocusable(false);
		descriptionField.setEnabled(false);
	}

	private static void removeEmbargoFieldListener() {
		for (MouseListener iterable_element : embargoField.getMouseListeners()) {
			if (iterable_element.equals(openEmbargoPanelListener)) {
				embargoField.removeMouseListener(openEmbargoPanelListener);
				embargoField.setEnabled(false);
			}
		}
	}

	private static void removeLanguageFieldListener() {
		for (MouseListener iterable_element : languageField.getMouseListeners()) {
			if (iterable_element.equals(openLanguagePanelListener)) {
				languageField.removeMouseListener(openLanguagePanelListener);
				languageField.setEnabled(false);
			}
		}
	}

	private static void removeResourceFieldListener() {
		for (MouseListener iterable_element : resourceField.getMouseListeners()) {
			if (iterable_element.equals(openResourcePanelListener)) {
				resourceField.removeMouseListener(openResourcePanelListener);
				resourceField.setEnabled(false);
			}
		}
	}

	private static void removeLicenseFieldListener() {
		licensePanel.setEnabled(false);
	}

	private static void removePublisherPanelListener() {
		for (MouseListener iterable_element : publisherField.getMouseListeners()) {
			if (iterable_element.equals(openPublisherPanelListener)) {
				publisherField.removeMouseListener(openPublisherPanelListener);
				publisherField.setEnabled(false);
			}
		}
	}

	private static void removeSubjectFieldListener() {
		for (MouseListener iterable_element : subjectsField.getMouseListeners()) {
			if (iterable_element.equals(openSubjectPanelListener)) {
				subjectsField.removeMouseListener(openSubjectPanelListener);
				subjectsField.setEnabled(false);
			}
		}
	}

	private static void removeTitleFieldListener() {
		titleField.setEditable(false);
		titleField.setFocusable(false);
		titleField.setEnabled(false);
	}

	private static void removeUploadPanelListener() {
		for (MouseListener iterable_element : uploadPathField.getMouseListeners()) {
			if (iterable_element.equals(openUploadPanelListener)) {
				uploadPathField.removeMouseListener(openUploadPanelListener);
				uploadPathField.setEnabled(false);
			}
		}
	}

	public static void reset() {
		uploadPathField.cleanTextArea();
		titleField.cleanTextArea();
		descriptionField.cleanTextArea();
	}

	public PublicationMainPanel(boolean showPublisherField, boolean showResourceTypeField) {

		/* set ValueChangedListener */

		titleField.addFocusListener(
				new TextAreaValueChangedFocusListener(PropertyLoader.TITLE_LABEL, titleField, DEFAULT_TITLE_STRING));

		descriptionField.addFocusListener(new TextAreaValueChangedFocusListener(PropertyLoader.DESCRIPTION_LABEL,
				descriptionField, DEFAULT_DESCRIPTION_STRING));

		publisherField.addFocusListener(new TextAreaCheckFocusListener(PropertyLoader.PUBLISHER_LABEL, publisherField,
				DEFAULT_PUBLISHER_STRING));

		/* end ValueChangedListener */

		/* set other Listeners */

		authorsField.addMouseListener(openAuthorPanelListener);
		languageField.addMouseListener(openLanguagePanelListener);
		uploadPathField.addMouseListener(openUploadPanelListener);
		subjectsField.addMouseListener(openSubjectPanelListener);
		publisherField.addMouseListener(openPublisherPanelListener);
		embargoField.addMouseListener(openEmbargoPanelListener);
		resourceField.addMouseListener(openResourcePanelListener);

		/* end other Listeners */

		AttributeLableAttributeTextAreaPanel descriptionPanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.DESCRIPTION_LABEL, descriptionField, PropertyLoader.DESCRIPTION_PANEL_HEIGHT);

		AttributeLableAttributeTextAreaPanel titleAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.TITLE_LABEL, titleField, PropertyLoader.TITLE_PANEL_HEIGHT);

		AttributeLableAttributeTextAreaPanel uploadAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.UPLOADPATH_LABEL, uploadPathField, PropertyLoader.TWO_LINE_HEIGHT);

		AttributeLableAttributeTextAreaPanel authorsAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.AUTHORS_LABEL, authorsField, PropertyLoader.AUTHOR_PANEL_HEIGHT);

		AttributeLableAttributeTextAreaPanel subjectsAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.SUBJECTS_LABEL, subjectsField, PropertyLoader.SUBJECTS_PANEL_HEIGHT);

		AttributeLableAttributeTextAreaPanel languageAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.LANGUAGE_LABEL, languageField, PropertyLoader.TWO_LINE_HEIGHT);

		AttributeLableAttributeTextAreaPanel publisherAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.PUBLISHER_LABEL, publisherField, PropertyLoader.PUBLISHER_PANEL_HEIGHT);

		AttributeLableAttributeTextAreaPanel embargoAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.EMBARGO_LABEL, embargoField, PropertyLoader.TWO_LINE_HEIGHT);

		AttributeLableAttributeTextAreaPanel resourceAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.RESOURCE_LABEL, resourceField, PropertyLoader.TWO_LINE_HEIGHT);

		AttributeLabelAttributePanel licenseCheckboxAttributePanel = new AttributeLabelAttributePanel(
				PropertyLoader.LICENSE_LABEL, licensePanel, PropertyLoader.LICENSE_PANEL_HEIGHT);

		embargoAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		languageAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
		uploadAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		licenseCheckboxAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		resourceAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		titleAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

		titleAuthorSplitPanel = new AttributeSplitPane(titleAttributePanel, authorsAttributePanel);
		titleAuthorSplitPanel.setResizeWeight(0.0);
		authorDescriptionSplitPanel = new AttributeSplitPane(titleAuthorSplitPanel, descriptionPanel);
		authorDescriptionSplitPanel.setResizeWeight(0.9);
		descriptionSubjectsSplitPanel = new AttributeSplitPane(authorDescriptionSplitPanel, subjectsAttributePanel);
		descriptionSubjectsSplitPanel.setResizeWeight(0.9);
		subjectsPublisherPanel = new AttributeSplitPane(descriptionSubjectsSplitPanel, publisherAttributePanel);
		subjectsPublisherPanel.setResizeWeight(0.6);

		mainPanel.add(uploadAttributePanel, BorderLayout.NORTH);

		if (showPublisherField) {
			mainPanel.add(subjectsPublisherPanel, BorderLayout.CENTER);
		} else {
			mainPanel.add(descriptionSubjectsSplitPanel, BorderLayout.CENTER);
		}

		embargoLanguageResourceLicensePanel = new JPanel(new BorderLayout());

		languageResourcePanel = new JPanel(new BorderLayout());

		languageResourcePanel.add(languageAttributePanel, BorderLayout.NORTH);
		if (showResourceTypeField) {
			languageResourcePanel.add(resourceAttributePanel, BorderLayout.SOUTH);
		}
		embargoLanguageResourceLicensePanel.add(languageResourcePanel, BorderLayout.NORTH);
		embargoLanguageResourceLicensePanel.add(licenseCheckboxAttributePanel, BorderLayout.CENTER);
		embargoLanguageResourceLicensePanel.add(embargoAttributePanel, BorderLayout.SOUTH);

		mainPanel.add(embargoLanguageResourceLicensePanel, BorderLayout.SOUTH);

		JScrollPane scrollableMainPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableMainPane.setPreferredSize(new Dimension(400, 400));
		this.setLayout(new GridLayout(1, 1));
		this.add(mainPanel);

	}

	public void disableAll() {

		removeUploadPanelListener();
		removeAuthorPanelListener();
		removeDescriptionFieldListener();
		removeTitleFieldListener();
		removeSubjectFieldListener();
		removePublisherPanelListener();
		removeLanguageFieldListener();
		removeEmbargoFieldListener();
		removeLicenseFieldListener();
		removeResourceFieldListener();
		PropertyLoader.UPLOADPATH_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.AUTHORS_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.TITLE_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.DESCRIPTION_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.SUBJECTS_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.PUBLISHER_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.LANGUAGE_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.LICENSE_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.EMBARGO_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PropertyLoader.RESOURCE_LABEL.setForeground(PropertyLoader.DISABLED_FONT_COLOR);
		PublicationFrame.updateUI(PropertyLoader.DISABLED_FONT_COLOR);

		uploadPathField.addMouseListener(blockedFieldMouseAdapter);
		titleField.addMouseListener(blockedFieldMouseAdapter);
		authorsField.addMouseListener(blockedFieldMouseAdapter);
		descriptionField.addMouseListener(blockedFieldMouseAdapter);
		embargoField.addMouseListener(blockedFieldMouseAdapter);
		subjectsField.addMouseListener(blockedFieldMouseAdapter);
		publisherField.addMouseListener(blockedFieldMouseAdapter);
		languageField.addMouseListener(blockedFieldMouseAdapter);
		licensePanel.addMouseListener(blockedFieldMouseAdapter);
		resourceField.addMouseListener(blockedFieldMouseAdapter);

	}

	public void enableAll() {
		releaseAllBlockedFields();
		PropertyLoader.UPLOADPATH_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.AUTHORS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.TITLE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.DESCRIPTION_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.SUBJECTS_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.PUBLISHER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.LANGUAGE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.LICENSE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.EMBARGO_LABEL.setForeground(PropertyLoader.LABEL_COLOR);
		PropertyLoader.RESOURCE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		uploadPathField.removeMouseListener(blockedFieldMouseAdapter);
		titleField.removeMouseListener(blockedFieldMouseAdapter);
		authorsField.removeMouseListener(blockedFieldMouseAdapter);
		descriptionField.removeMouseListener(blockedFieldMouseAdapter);
		embargoField.removeMouseListener(blockedFieldMouseAdapter);
		subjectsField.removeMouseListener(blockedFieldMouseAdapter);
		publisherField.removeMouseListener(blockedFieldMouseAdapter);
		languageField.removeMouseListener(blockedFieldMouseAdapter);
		licensePanel.removeMouseListener(blockedFieldMouseAdapter);
		resourceField.removeMouseListener(blockedFieldMouseAdapter);

		PublicationFrame.updateUI();

	}
}
