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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;

/**
 * Create a Dialog if there is an problem with the connection to the eDAL server
 * and ask the user if the system should retry or quit the connection try.
 * 
 * @author arendd
 */
public class ServerErrorDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 2288426728436421340L;

	private JButton retryButton = new JButton("RETRY");
	private JButton cancelButton = new JButton("CANCEL");

	private int returnValue = 0;

	public ServerErrorDialog(Frame parent, String errorMessage, String serverAddress, int registryPort) {

		super(parent, PropertyLoader.PROGRAM_NAME, true);
		this.setFocusable(true);
		this.setIconImage(PropertyLoader.EDAL_LOGO);

		JEditorPane textPane = new JEditorPane();

		textPane.setContentType("text/html");

		try {
			textPane.setText(
					PublicationVeloCityCreater.generateServerErrorDialog(errorMessage, serverAddress, registryPort));
		} catch (EdalException e) {
			e.printStackTrace();
		}

		textPane.setEditable(false);
		textPane.setBorder(BorderFactory.createEmptyBorder());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(retryButton);
		buttonPanel.add(cancelButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(textPane);
		mainPanel.add(buttonPanel);

		this.setContentPane(mainPanel);
		this.setResizable(false);
		this.setPreferredSize(new Dimension(400, 200));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.retryButton.addActionListener(this);
		this.cancelButton.addActionListener(this);
		this.pack();
		this.setLocationRelativeTo(parent);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource().equals(this.retryButton)) {
			this.returnValue = 1;
			this.dispose();
		}

		else if (actionEvent.getSource().equals(this.cancelButton)) {
			this.returnValue = 0;
			this.dispose();
		}
	}

	/**
	 * @return the returnValue
	 */
	public int getReturnValue() {
		return returnValue;
	}

	public Object showDialog() {
		setVisible(true);
		return this;

	}
}