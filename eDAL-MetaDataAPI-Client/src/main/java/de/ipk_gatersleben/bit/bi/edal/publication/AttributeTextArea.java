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

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class AttributeTextArea extends JTextArea {

	private static final long serialVersionUID = -7407205597285624266L;

	private String defaultText;

	public AttributeTextArea(String defaultText, boolean editable,
			boolean onlyOneLineAllowed) {
		super(1, 1);
		this.setWrapStyleWord(true);
		this.setFont(new Font(Font.SANS_SERIF, 0,
				PropertyLoader.DEFAULT_FONT_SIZE));
		this.setForeground(PropertyLoader.MAIN_FONT_COLOR);
		this.setBackground(PropertyLoader.MAIN_BACKGROUND_COLOR);
		this.setDisabledTextColor(PropertyLoader.DISABLED_FONT_COLOR);
		this.setLineWrap(true);
		this.setEditable(editable);
		this.setToolTipText("please click to edit");

		EmptyBorder inBorder = new EmptyBorder(3, 5, 0, 0);
		EmptyBorder outBorder = new EmptyBorder(0, 0, 0, 0);
		this.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		if (onlyOneLineAllowed) {
			this.setDocument(new JTextFieldLimit(4000));
			this.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		} else {
			this.setDocument(new JTextFieldLimit(4000));
		}
		this.defaultText = defaultText;
		this.setText(defaultText);
	}

	public class JTextFieldLimit extends PlainDocument {

		private static final long serialVersionUID = 8208493037022310906L;
		private int limit;

		JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr)
				throws BadLocationException {

			if (str == null)
				return;

			/* permit the insert of a Tabulator */
			if (str.equals("\t")) {
				return;
			}

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}

		}
	}

	public void cleanTextArea() {
		this.setText(defaultText);
	}

}
