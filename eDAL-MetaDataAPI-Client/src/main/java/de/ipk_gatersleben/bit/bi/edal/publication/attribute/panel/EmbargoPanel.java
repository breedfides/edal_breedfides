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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;
import de.ipk_gatersleben.bit.bi.edal.publication.Utils;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalDatePanel;

public class EmbargoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -2366315088718300542L;

	private static final AttributeLabel EMBARGO_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("EMBARGO_LABEL"), PropertyLoader.props.getProperty("EMBARGO_TOOLTIP"));;

	private static final SmallButton OKAY_BUTTON = new SmallButton("OK");
	private static final SmallButton CANCEL_BUTTON = new SmallButton("CANCEL");

	EdalDatePanel datePanel = null;

	public EmbargoPanel() {

		GridBagLayout grid = new GridBagLayout();

		JPanel tablePanel = new JPanel(grid);

		tablePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		tablePanel.setMinimumSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.EMBARGO_PANEL_HEIGHT));

		EmptyBorder inBorder = new EmptyBorder(5, 5, 0, 5);
		EmptyBorder outBorder = new EmptyBorder(5, 5, 0, 5);
		tablePanel.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		buttonPanel.add(OKAY_BUTTON);
		buttonPanel.add(CANCEL_BUTTON);

		OKAY_BUTTON.addActionListener(this);
		CANCEL_BUTTON.addActionListener(this);

		datePanel = new EdalDatePanel(null, false);

		JScrollPane scrollPane = new JScrollPane(datePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.EMBARGO_PANEL_HEIGHT));

		Utils.add(tablePanel, grid, scrollPane, 0, 0, 1, 1, 1, 1, 1, 1);
		Utils.add(tablePanel, grid, buttonPanel, 0, 1, 1, 1, 1, 1, 1, 1);

		JPanel attributePanel = new JPanel(new GridLayout());

		EmbargoPanel.EMBARGO_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		attributePanel.add(EmbargoPanel.EMBARGO_LABEL);
		attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributePanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.EMBARGO_PANEL_HEIGHT));

		this.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));

		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);

		this.setLayout(new BorderLayout());
		this.add(attributePanel, BorderLayout.WEST);
		this.add(tablePanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(OKAY_BUTTON)) {

			PublicationMainPanel.embargboPanel = this;

			this.datePanel.refreshtime();

			PublicationMainPanel.embargoField.setText(datePanel.getCalendar().getTime().toString());

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.embargoLanguageResourceLicensePanel.remove(((BorderLayout) PublicationMainPanel.embargoLanguageResourceLicensePanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH));

			PropertyLoader.EMBARGO_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			AttributeLableAttributeTextAreaPanel newEmbargoPanel = new AttributeLableAttributeTextAreaPanel(PropertyLoader.EMBARGO_LABEL, PublicationMainPanel.embargoField, PropertyLoader.TWO_LINE_HEIGHT);

			newEmbargoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

			PublicationMainPanel.embargoLanguageResourceLicensePanel.add(newEmbargoPanel, BorderLayout.SOUTH);

			PublicationFrame.updateUI();
//			PublicationModul.getFrame().requestFocusInWindow();
			PublicationButtonLinePanel.getNextButton().requestFocus();


		} else if (actionEvent.getSource().equals(CANCEL_BUTTON)) {

			PublicationMainPanel.embargboPanel = this;

			PublicationMainPanel.embargoField.setText(PublicationMainPanel.DEFAULT_EMBARGO_STRING);

			this.datePanel.reset();

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.embargoLanguageResourceLicensePanel.remove(((BorderLayout) PublicationMainPanel.embargoLanguageResourceLicensePanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH));

			PropertyLoader.EMBARGO_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			AttributeLableAttributeTextAreaPanel newEmbargoPanel = new AttributeLableAttributeTextAreaPanel(PropertyLoader.EMBARGO_LABEL, PublicationMainPanel.embargoField, PropertyLoader.TWO_LINE_HEIGHT);

			newEmbargoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

			PublicationMainPanel.embargoLanguageResourceLicensePanel.add(newEmbargoPanel, BorderLayout.SOUTH);
			PublicationFrame.updateUI();
//			PublicationModul.getFrame().requestFocusInWindow();

		}

	}

	public Calendar getEmbargoDate() {
		if (PublicationMainPanel.embargoField.getText().equals(PublicationMainPanel.DEFAULT_EMBARGO_STRING)) {
			return null;
		}
		return this.datePanel.getCalendar();
	}

}
