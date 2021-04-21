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
