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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RelatedIdentifierPanel extends JPanel {

	JTextField filePathField = new JTextField();

	public JTextField getFilePathField() {
		return filePathField;
	}

	private void setFilePathField(JTextField filePathField) {
		this.filePathField = filePathField;
	}

	public RelatedIdentifierPanel() {

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		filePathField.setBorder(new EmptyBorder(0, 0, 0, 0));

		this.add(filePathField , BorderLayout.CENTER);

	}

}
