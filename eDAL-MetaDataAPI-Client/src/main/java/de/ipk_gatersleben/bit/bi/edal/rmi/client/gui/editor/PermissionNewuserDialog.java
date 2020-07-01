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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.PrincipalUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;

/**
 * <code>PermissionNewuserDialog</code> provides a mechanism for the user to add
 * new users and manage there permissions of EDAL File System.
 * 
 * The following code pops up a PermissionNewuserDialog for the user's directory
 * 
 * <pre>
 * PermissionNewuserDialog permission = new PermissionNewuserDialog(directory, client);
 * permission.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class PermissionNewuserDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	private JButton savebtn;
	private JButton cancelbtn;

	public int returnvalue;

	private ClientPrimaryDataEntity dataentry = null;
	private ClientDataManager client;
	private List<Methods> allavailablemethods = new ArrayList<Methods>();
	private List<String> principals = new ArrayList<String>();
	private List<Class<? extends Principal>> supportedprincipals;
	private JTextField usertext = new JTextField();
	private JComboBox<String> principalcomboBox;
	private Principal principal;

	/**
	 * Constructs a <code>PermissionNewuserDialog</code> that is initialized
	 * with <code>dataentry</code> as the EDAL dataentry, and
	 * <code>client</code> as the rmi client datamanager. If any of the
	 * parameters are <code>null</code> this method will not initialize.
	 * 
	 * @param dataentry
	 *            the EDAL dataentry
	 * @param client
	 *            the rmi client datamanager
	 */
	public PermissionNewuserDialog(ClientPrimaryDataEntity dataentry, ClientDataManager client) {
		this.dataentry = dataentry;
		this.client = client;
		loadconstants();

		setTitle("Add NewUser");

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel userselectpanel = new JPanel();
		userselectpanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
		JLabel principallabel = new JLabel("UserName:", SwingConstants.CENTER);
		userselectpanel.add(principallabel);

		usertext.setPreferredSize(new Dimension(240, 28));

		userselectpanel.add(usertext);

		principalcomboBox = new JComboBox<String>();
		principalcomboBox.setName("principal");
		for (Class<? extends Principal> obj : supportedprincipals) {
			principalcomboBox.addItem(obj.getSimpleName());
			principals.add(obj.getName());
		}
		principalcomboBox.addItemListener(new ItemChangeListener());

		userselectpanel.add(principalcomboBox);

		JPanel editPane = new JPanel();
		editPane.setLayout(new BorderLayout());
		editPane.add(userselectpanel, BorderLayout.NORTH);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setSize(new Dimension(600, 120));
		this.setResizable(false);
	}

	private class ItemChangeListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				Object item = event.getItem();
				if ("ALLPrincipal".equals(item.toString())) {
					usertext.setText("ALL");
					usertext.setEditable(false);
				} else {
					usertext.setText("");
					usertext.setEditable(true);
				}
			}
		}
	}

	/**
	 * pop up a PermissionNewuserDialog Dialog
	 * 
	 * @return the result
	 */
	public int showOpenDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}

	public Principal getPrincipal() {
		return principal;
	}

	private void loadconstants() {
		try {
			if (dataentry != null) {
				List<Methods> commonskeysets = GrantableMethods.ENTITY_METHODS;
				for (GrantableMethods.Methods m : commonskeysets) {
					allavailablemethods.add(m);
				}

				if (dataentry.isDirectory()) {
					List<Methods> keysets = GrantableMethods.DIRECTORY_METHODS;
					for (GrantableMethods.Methods m : keysets) {
						allavailablemethods.add(m);
					}
				} else {
					List<Methods> keysets = GrantableMethods.FILE_METHODS;
					for (GrantableMethods.Methods m : keysets) {
						allavailablemethods.add(m);
					}
				}
			}

			supportedprincipals = client.getSupportedPrincipals();
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (EdalException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
	}

	private JPanel createbuttonpanel() {
		savebtn = new JButton(okAction);
		cancelbtn = new JButton(cancelAction);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);

		return buttonPane;
	}

	private void assignpermission() {
		// get principal
		String currentuser = usertext.getText().trim();
		if (currentuser.length() == 0) {
			JOptionPane.showMessageDialog(null, "Please Input UserName!", "EdalFileChooser", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String currentprincipalname = principals.get(principalcomboBox.getSelectedIndex());
		principal = PrincipalUtil.getInstance(currentprincipalname, currentuser);
		JOptionPane.showMessageDialog(null, "we created a new user:" + currentuser + "!", "EdalFileChooser",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private Action okAction = new AbstractAction("Ok") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			assignpermission();
			returnvalue = APPROVE_OPTION;
			dispose();
		}
	};

	private Action cancelAction = new AbstractAction("Cancel") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = CANCEL_OPTION;
			dispose();
		}
	};
}