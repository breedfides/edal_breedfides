/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

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
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.RelatedIdentifierPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.SubjectPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.NonFreeTextPanelMouseAdapter;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.NonFreeTextPanelMouseAdapter.PanelType;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.TextAreaCheckFocusListener;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.TextAreaSaveListener;
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
	public static final String DEFAULT_RELATED_IDENTIFIER_PATH_STRING = PropertyLoader
			.loadRelatedIdentifierPathString();
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
	public static AttributeTextArea relatedIdentifierPathField = new AttributeTextArea(
			DEFAULT_RELATED_IDENTIFIER_PATH_STRING, false, true);

	public static AuthorsPanel authorPanel;
	public static LanguagePanel languagePanel;
	public static SubjectPanel subjectPanel;
	public static PublisherPanel publisherPanel;
	public static EmbargoPanel embargboPanel;
	public static ResourcePanel resourcePanel;
	public static RelatedIdentifierPanel relatedIdentifierPanel;

	public static LicenseCheckBoxPanel licensePanel;
	private static NonFreeTextPanelMouseAdapter openAuthorPanelListener;
	private static NonFreeTextPanelMouseAdapter openLanguagePanelListener;
	private static NonFreeTextPanelMouseAdapter openUploadPanelListener;
	private static NonFreeTextPanelMouseAdapter openSubjectPanelListener;
	private static NonFreeTextPanelMouseAdapter openPublisherPanelListener;
	private static NonFreeTextPanelMouseAdapter openEmbargoPanelListener;
	private static NonFreeTextPanelMouseAdapter openResourcePanelListener;
	private static NonFreeTextPanelMouseAdapter openRelatedIdentifierPanelListener;

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
		relatedIdentifierPanel = new RelatedIdentifierPanel();

		openAuthorPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.AUTHOR_PANEL);
		openLanguagePanelListener = new NonFreeTextPanelMouseAdapter(PanelType.LANGUAGE_PANEL);
		openUploadPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.UPLOAD_PANEL);
		openSubjectPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.SUBJECT_PANEL);
		openPublisherPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.PUBLISHER_PANEL);
		openEmbargoPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.EMBARGO_PANEL);
		openResourcePanelListener = new NonFreeTextPanelMouseAdapter(PanelType.RESOURCE_PANEL);
		openRelatedIdentifierPanelListener = new NonFreeTextPanelMouseAdapter(PanelType.RELATED_IDENTIFIER_PANEL);

	}
	public static AttributeSplitPane titleAuthorSplitPanel = null;
	public static AttributeSplitPane authorDescriptionSplitPanel = null;
	public static AttributeSplitPane descriptionSubjectsSplitPanel = null;
	public static AttributeSplitPane subjectsRelatedIdentiferPanel = null;
	public static AttributeSplitPane relatedIdentifierPublisherPanel = null;

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
		relatedIdentifierPathField.setFocusable(true);

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

		boolean hasRelatedIdentifierPathListener = false;

		for (MouseListener iterable_element : relatedIdentifierPathField.getMouseListeners()) {
			if (iterable_element.equals(openRelatedIdentifierPanelListener)) {
				hasUploadPathListener = true;
			}
		}

		if (!hasUploadPathListener) {
			relatedIdentifierPathField.addMouseListener(openRelatedIdentifierPanelListener);
			relatedIdentifierPathField.setEnabled(true);
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

		titleField.addFocusListener(new TextAreaSaveListener());

		descriptionField.addFocusListener(new TextAreaValueChangedFocusListener(PropertyLoader.DESCRIPTION_LABEL,
				descriptionField, DEFAULT_DESCRIPTION_STRING));

		descriptionField.addFocusListener(new TextAreaSaveListener());

		publisherField.addFocusListener(new TextAreaCheckFocusListener(PropertyLoader.PUBLISHER_LABEL, publisherField,
				DEFAULT_PUBLISHER_STRING));

		/* end ValueChangedListener */

		loadUserValues();

		/* set other Listeners */

		authorsField.addMouseListener(openAuthorPanelListener);
		languageField.addMouseListener(openLanguagePanelListener);
		uploadPathField.addMouseListener(openUploadPanelListener);
		subjectsField.addMouseListener(openSubjectPanelListener);
		publisherField.addMouseListener(openPublisherPanelListener);
		embargoField.addMouseListener(openEmbargoPanelListener);
		resourceField.addMouseListener(openResourcePanelListener);
		
		//KANN WEG WEIL ES KEIN MAUSKLICK EVENT GIBT
		
		relatedIdentifierPathField.addMouseListener(openRelatedIdentifierPanelListener);

		/* end other Listeners */
		
		
		//DRAGDROPLISTENER
		
//		relatedIdentifierPathField.setDragEnabled(true);
//		
//		new DropTarget(relatedIdentifierPathField, new RelatedIdDragDropListener());
		    
		
		

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

		AttributeLableAttributeTextAreaPanel relatedIdentifierAttributePanel = new AttributeLableAttributeTextAreaPanel(
				PropertyLoader.RELATED_IDENTIFIER_LABEL, relatedIdentifierPathField,
				PropertyLoader.RELATED_IDENTIFIER_PANEL_HEIGHT);

		titleAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		embargoAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		languageAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
		uploadAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		licenseCheckboxAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		resourceAttributePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
//		relatedIdentifierAttributePanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(uploadAttributePanel, BorderLayout.NORTH);

		titleAuthorSplitPanel = new AttributeSplitPane(titleAttributePanel, authorsAttributePanel);
		titleAuthorSplitPanel.setResizeWeight(0.0);
		authorDescriptionSplitPanel = new AttributeSplitPane(titleAuthorSplitPanel, descriptionPanel);
		authorDescriptionSplitPanel.setResizeWeight(0.9);
		descriptionSubjectsSplitPanel = new AttributeSplitPane(authorDescriptionSplitPanel, subjectsAttributePanel);
		descriptionSubjectsSplitPanel.setResizeWeight(0.9);
		subjectsRelatedIdentiferPanel = new AttributeSplitPane(descriptionSubjectsSplitPanel,
				relatedIdentifierAttributePanel);
		subjectsRelatedIdentiferPanel.setResizeWeight(0.6);

//		relatedIdentifierPublisherPanel = new AttributeSplitPane(subjectsRelatedIdentiferPanel,
//				publisherAttributePanel);
//		relatedIdentifierPublisherPanel.setResizeWeight(0.6);
//
//		if (showPublisherField) {
//			mainPanel.add(relatedIdentifierPublisherPanel, BorderLayout.CENTER);
//		} else {
//			mainPanel.add(subjectsRelatedIdentiferPanel, BorderLayout.CENTER);
//		}

		// developmet //
		mainPanel.add(descriptionSubjectsSplitPanel, BorderLayout.CENTER);
		//            //
		
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

	private void loadUserValues() {

		final String title = PropertyLoader.userValues.getProperty("TITLE");

		if (title == null || title.isEmpty()) {
		} else {
			titleField.setText(title);
		}

		final String description = PropertyLoader.userValues.getProperty("DESCRIPTION");

		if (description == null || description.isEmpty()) {
		} else {
			descriptionField.setText(description);
		}

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
