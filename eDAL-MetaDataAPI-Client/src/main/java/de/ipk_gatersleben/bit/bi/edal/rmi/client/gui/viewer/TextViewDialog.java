/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The <code>TextviewDialog</code> can be used to edit text information, which
 * implements the <code>MetadataviewDialog</code> class, we can use it with a
 * couple of lines of code:
 * 
 * <pre>
 * TextviewDialog textviewDialog = new TextviewDialog(text);
 * textviewDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class TextViewDialog extends MetadataViewDialog {
	private static final long serialVersionUID = 1L;
	private JTextArea textarea;
	private String _text;

	/**
	 * Constructs a <code>TextviewDialog</code> that is initialized with String
	 *
	 * @param text
	 *            text information to show in TextViewDialog
	 * @param title
	 *            the title for the dialog
	 */
	public TextViewDialog(String text, String title) {
		super();

		this._text = text;

		setTitle(title);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel editPane = new JPanel();
		editPane.setLayout(new BorderLayout());

		textarea = new JTextArea();
		textarea.setLineWrap(true);
		textarea.setEditable(false);

		JScrollPane scroll = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		editPane.add(scroll, BorderLayout.CENTER);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(350, (int) (350 * 0.618)));

		initdata();
	}

	@Override
	public void initdata() {
		if (_text != null) {
			textarea.setText(_text);
		}
	}

	/**
	 * Returns the Text information inputted by user.
	 * 
	 * @return the Text information inputted by user.
	 */
	public String getText() {
		return textarea.getText();
	}
}
