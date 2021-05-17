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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.ipk_gatersleben.bit.bi.edal.publication.AttributeLabel;
import de.ipk_gatersleben.bit.bi.edal.publication.PropertyLoader;

public class RelatedIdentifierPanel extends JPanel {

	private JScrollPane scrollPane;

	public static final AttributeLabel RELATED_IDENTIFIER_LABEL = new AttributeLabel(
			PropertyLoader.props.getProperty("RELATED_IDENTIFIER_LABEL"),
			PropertyLoader.props.getProperty("RELATED_IDENTIFIER_LABEL"));

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
		this.add(this.scrollPane, BorderLayout.CENTER);

	}

	public RelatedIdentifierPanel() {

		RelatedIdentifierPanel.RELATED_IDENTIFIER_LABEL.setForeground(PropertyLoader.LABEL_COLOR);

		BorderLayout layout = new BorderLayout();
		
	
		
		this.setLayout(layout);
	}

}
