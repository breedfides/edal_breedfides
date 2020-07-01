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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLableAttributeTextAreaPanel;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationButtonLinePanel;
import de.ipk_gatersleben.bit.bi.edal.publication.SmallButton;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationFrame;
import de.ipk_gatersleben.bit.bi.edal.publication.PublicationMainPanel;

@SuppressWarnings("unchecked")
public class LanguagePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 8109825692298261311L;

	private static AttributeLabel LANGUAGE_LABEL = new AttributeLabel(PropertyLoader.props.getProperty("LANGUAGE_LABEL"), PropertyLoader.props.getProperty("LANGUAGE_TOOLTIP"));

	private static SmallButton OKAY_BUTTON = new SmallButton("OK");

	private static JComboBox<String> comboBox;

	private static Locale[] LOCALE_LIST;

	static {

		LOCALE_LIST = Locale.getAvailableLocales();

		Arrays.sort(LOCALE_LIST, new Comparator<Locale>() {

			@Override
			public int compare(Locale o1, Locale o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		String[] strings = new String[LOCALE_LIST.length];

		for (int i = 0; i < LOCALE_LIST.length; i++) {

			if (!LOCALE_LIST[i].getDisplayCountry().isEmpty()) {
				strings[i] = LOCALE_LIST[i].getDisplayLanguage() + " - " + LOCALE_LIST[i].getDisplayCountry();
			} else {
				strings[i] = LOCALE_LIST[i].getDisplayLanguage();
			}
		}

		comboBox = new JComboBox<String>(strings);

		comboBox.setRenderer(new CustomComboBox());

		comboBox.setBorder(BorderFactory.createEmptyBorder());

		comboBox.setSelectedItem(PropertyLoader.loadLanguageString());
	}

	@SuppressWarnings("rawtypes")
	private static class CustomComboBox extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 9120777908811491201L;

		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		private final static Dimension preferredSize = new Dimension(250, 14);

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Color) {
				renderer.setBackground((Color) value);
			}
			renderer.setPreferredSize(preferredSize);
			return renderer;
		}
	}

	public LanguagePanel() {

		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

		mainPanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		mainPanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.TWO_LINE_HEIGHT));

		EmptyBorder inBorder = new EmptyBorder(1, 2, 0, 2);
		EmptyBorder outBorder = new EmptyBorder(1, 2, 0, 2);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		comboBox.setSelectedItem(PublicationMainPanel.DEFAULT_LANGUAGE_STRING);

		OKAY_BUTTON.addActionListener(this);

		mainPanel.add(comboBox);
		mainPanel.add(OKAY_BUTTON);

		JPanel attributePanel = new JPanel(new GridLayout());

		attributePanel.add(LANGUAGE_LABEL);
		attributePanel.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		attributePanel.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_LABEL_WIDTH, PropertyLoader.TWO_LINE_HEIGHT));

		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.setBorder(new MatteBorder(0, 0, 0, 0, Color.GRAY));
		this.setPreferredSize(new Dimension(PropertyLoader.ATTRIBUTE_PANEL_WIDTH, PropertyLoader.TWO_LINE_HEIGHT));

		this.setLayout(new BorderLayout());
		this.add(attributePanel, BorderLayout.WEST);
		this.add(mainPanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(OKAY_BUTTON)) {

			PublicationMainPanel.languagePanel = this;

			PublicationMainPanel.languageField.setText(getTableContent());

			PublicationMainPanel.releaseAllBlockedFields();

			PublicationMainPanel.languageResourcePanel.remove(((BorderLayout) PublicationMainPanel.languageResourcePanel.getLayout()).getLayoutComponent(BorderLayout.NORTH));

			PropertyLoader.LANGUAGE_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

			AttributeLableAttributeTextAreaPanel newLanguagePanel = new AttributeLableAttributeTextAreaPanel(PropertyLoader.LANGUAGE_LABEL, PublicationMainPanel.languageField, PropertyLoader.TWO_LINE_HEIGHT);

			newLanguagePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));

			PublicationMainPanel.languageResourcePanel.add(newLanguagePanel, BorderLayout.NORTH);

			PublicationFrame.updateUI();
			// PublicationModul.getFrame().requestFocusInWindow();
			PublicationButtonLinePanel.getNextButton().requestFocus();

			saveUserValues();

		}

	}

	private void saveUserValues() {
		PropertyLoader.setUserValue("LANGUAGE", comboBox.getSelectedItem().toString());

	}

	private String getTableContent() {
		return LanguagePanel.comboBox.getSelectedItem().toString();
	}

	public Locale getLanguage() {
		return LOCALE_LIST[LanguagePanel.comboBox.getSelectedIndex()];
	}
}