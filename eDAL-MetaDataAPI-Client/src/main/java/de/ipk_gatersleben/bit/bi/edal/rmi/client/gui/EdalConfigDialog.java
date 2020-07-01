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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.Const;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class EdalConfigDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	public int returnvalue;

	public static ClientDataManager client;
	public static ClientPrimaryDataDirectory rootDirectory;
	private boolean islogin = false;

	private JPanel detialpanel = new JPanel();

	private JButton savebtn;
	private JButton cancelbtn;
	private JLabel infolabel;

	private Action okAction = new AbstractAction(Const.SAVE_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			infolabel.setText("");
			if (!checkerror()) {
				return;
			}
			if (loginremoteserver(getServeraddress(), getServerport(),
					getUsername(), getPassword())) {
				returnvalue = APPROVE_OPTION;
				dispose();
			}
		}
	};

	private Action cancelAction = new AbstractAction(Const.CANCEL_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = CANCEL_OPTION;
			dispose();
		}
	};

	private JLabel serveraddresslabel;

	private JTextField serveraddresstxt;

	private JLabel serverportlabel;

	private JLabel serverdatgaportlabel;

	private JTextField serverporttxt;

	private JTextField serverdataporttxt;

	private JLabel serverusernamelabel;

	private JTextField serverusernametxt;

	private JLabel serverpasswordlabel;

	private JTextField serverpasswordtxt;

	public EdalConfigDialog() {
		addWindowListener(createAppCloser());

		setTitle(Const.CONFIGURATION_TITLE_STR);

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		detialpanel.setLayout(new MigLayout("", "[100!][90%!]", ""));

		serveraddresslabel = new JLabel("Server Address:");
		detialpanel.add(serveraddresslabel);

		serveraddresstxt = new JTextField();
		detialpanel.add(serveraddresstxt, "wrap,width max(70%, 70%)");

		serverportlabel = new JLabel("Server Port:");
		detialpanel.add(serverportlabel);

		serverporttxt = new JTextField();
		detialpanel.add(serverporttxt, "wrap,width max(70%, 70%)");

		serverdatgaportlabel = new JLabel("Server Dataport:");
		detialpanel.add(serverdatgaportlabel);

		serverdataporttxt = new JTextField();
		detialpanel.add(serverdataporttxt, "wrap,width max(70%, 70%)");

		serverusernamelabel = new JLabel("User Name:");
		detialpanel.add(serverusernamelabel);

		serverusernametxt = new JTextField();
		detialpanel.add(serverusernametxt, "wrap,width max(70%, 70%)");

		serverpasswordlabel = new JLabel("Password:");
		detialpanel.add(serverpasswordlabel);

		serverpasswordtxt = new JPasswordField();
		detialpanel.add(serverpasswordtxt, "wrap,width max(70%, 70%)");

		infolabel = new JLabel("");
		infolabel.setForeground(Color.RED);
		detialpanel.add(infolabel, "width max(80%, 80%)");

		contents.add(detialpanel, BorderLayout.CENTER);

		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setSize(new Dimension(400, (int) (400 * 0.618)));
		this.setMinimumSize(new Dimension(400, (int) (400 * 0.618)));
	}

	private boolean checkerror() {
		final String serveraddresstxtText = serveraddresstxt.getText();
		if (serveraddresstxtText.trim().length() == 0) {
			mustfill(serveraddresslabel);
			JOptionPane.showMessageDialog(null, "Please input Server Address!",
					Const.CONFIGURATION_TITLE_STR, JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			resetfill(serveraddresslabel);
		}

		final String serverporttxtText = serverporttxt.getText();
		if (serverporttxtText.trim().length() == 0) {
			mustfill(serverportlabel);
			JOptionPane.showMessageDialog(null, "Please input Server Port!",
					Const.CONFIGURATION_TITLE_STR, JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			resetfill(serverportlabel);
		}

		try {
			Integer.parseInt(serverporttxtText.trim());
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null,
					"Server Port should be Integer!",
					Const.CONFIGURATION_TITLE_STR, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		final String serverusernametxtText = serverusernametxt.getText();
		if (serverusernametxtText.trim().length() == 0) {
			mustfill(serverusernamelabel);
			JOptionPane.showMessageDialog(null, "Please input User Name!",
					Const.CONFIGURATION_TITLE_STR, JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			resetfill(serverusernamelabel);
		}

		final String serverpasswordtxtText = serverpasswordtxt.getText();
		if (serverpasswordtxtText.trim().length() == 0) {
			mustfill(serverpasswordlabel);
			JOptionPane.showMessageDialog(null, "Please input Password!",
					Const.CONFIGURATION_TITLE_STR, JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			resetfill(serverpasswordlabel);
		}

		return true;
	}

	private WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				returnvalue = CANCEL_OPTION;
				dispose();
			}
		};
	}

	private JPanel createbuttonpanel() {
		savebtn = new JButton(okAction);
		cancelbtn = new JButton(cancelAction);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);

		this.getRootPane().setDefaultButton(savebtn);

		return buttonPane;
	}

	public String getPassword() {
		return serverpasswordtxt.getText().trim();
	}

	public String getServeraddress() {
		return serveraddresstxt.getText().trim();
	}

	public int getServerport() {
		return Integer.parseInt(serverporttxt.getText().trim());
	}

	public String getUsername() {
		return serverusernametxt.getText().trim();
	}

	private boolean loginremoteserver(String serveraddress, int serverport,
			String username, String password) {
		try {
			Authentication auth = new Authentication(
					EdalHelpers.authenticateUser(username, password));
			client = new ClientDataManager(serveraddress, serverport, auth);
			rootDirectory = client.getRootDirectory();
			islogin = true;
		} catch (Exception e) {
			islogin = false;
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			infolabel.setText(e.getMessage());
		}

		return islogin;
	}

	private void mustfill(JLabel label) {
		label.setForeground(Color.RED);
		label.setText("*" + label.getText());
	}

	private void resetfill(JLabel label) {
		label.setForeground(Color.BLACK);
		String originaltext = label.getText();
		if (originaltext.startsWith("*")) {
			originaltext = originaltext.substring(1);
		}
		label.setText(originaltext);
	}

	public int showOpenDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}
}
