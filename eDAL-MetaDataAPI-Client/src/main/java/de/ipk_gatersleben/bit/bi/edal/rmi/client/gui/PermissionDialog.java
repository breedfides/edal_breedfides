/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods;
import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor.PermissionNewuserDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.Const;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.PrincipalUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;

/**
 * <code>PermissionDialog</code> provides a mechanism for the user to manage the
 * permissions of EDAL File System.
 * 
 * The following code pops up a PermissionDialog for the user's directory
 * 
 * <pre>
 * PermissionDialog permission = new PermissionDialog(directory, client);
 * permission.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class PermissionDialog extends JDialog implements ItemListener {
	private static final long serialVersionUID = 1L;
	private int dialogwidth = 780;
	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	private static final String DEFAULT_SOURCE_CHOICE_LABEL = "Available Permissions";
	private static final String DEFAULT_DEST_CHOICE_LABEL = "Assigned Permissions";
	private static final String ADD_BUTTON_LABEL = "Add >>";
	private static final String REMOVE_BUTTON_LABEL = "<< Remove";
	private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	private JComboBox<String> principalcomboBox;

	private int returnvalue;

	private JPanel detialpanel = new JPanel();
	private JList<String> sourceList;
	private JList<String> destList;
	private SortedListModel sourceListModel;
	private SortedListModel destListModel;
	private JButton removeButton;
	private JLabel destLabel;

	private ClientPrimaryDataEntity dataentry = null;

	private List<Methods> allavailablemethods = new ArrayList<Methods>();
	private List<String> methods = new ArrayList<String>();
	private List<String> userprincipals = new ArrayList<String>();
	private List<String> users = new ArrayList<String>();
	private List<String> principals = new ArrayList<String>();
	private ClientDataManager client;
	
	private LinkedHashSet<String> todoprincipals = new LinkedHashSet<String>();

	/**
	 * Constructs a <code>PermissionDialog</code> that is initialized with
	 * <code>dataentry</code> as the EDAL dataentry, and <code>client</code> as
	 * the rmi client datamanager. If any of the parameters are
	 * <code>null</code> this method will not initialize.
	 * 
	 * @param dataentry
	 *            the EDAL dataentry
	 * @param client
	 *            the rmi client datamanager
	 */
	public PermissionDialog(ClientPrimaryDataEntity dataentry,
			ClientDataManager client) {
		addWindowListener(createAppCloser());

		allavailablemethods.clear();
		methods.clear();

		this.client = client;

		this.dataentry = dataentry;

		loadconstants();

		try {
			setTitle("Permissions Information:"+dataentry.getPath());
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		detialpanel.setLayout(new BorderLayout());

		JPanel userselectpanel = new JPanel();

		inituserprincipallist(null);

		JLabel principallabel = new JLabel("Principals at:",
				SwingConstants.CENTER);
		userselectpanel.add(principallabel);

		principalcomboBox = new JComboBox<String>(userprincipals.toArray(new String[0]));
		principalcomboBox.setName("principal");

		userselectpanel.add(principalcomboBox);
		principalcomboBox.addItemListener(this);

		JButton applyButton = new JButton(applyAction);
		userselectpanel.add(applyButton);

		JButton addButton = new JButton(addnewAction);
		userselectpanel.add(addButton);

		JPanel permissioneditpanel = new JPanel();
		initpermissionpanel(permissioneditpanel);

		detialpanel.add(userselectpanel, BorderLayout.NORTH);
		detialpanel.add(permissioneditpanel, BorderLayout.CENTER);

		contents.add(detialpanel, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);
		
		this.setSize(new Dimension(dialogwidth, (int) (dialogwidth * 0.618)));
		this.setMinimumSize(new Dimension(dialogwidth, (int) (dialogwidth * 0.618)));
		this.setResizable(false);

		if (principalcomboBox.getItemCount() > 0) {
			refreshPermissiontable(dataentry, users.get(0), principals.get(0));
		}
	}
	
	private JPanel createbuttonpanel() {
		JButton savebtn = new JButton(okAction);
		JButton cancelbtn = new JButton(cancelAction);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);

		return buttonPane;
	}
	
	private Action okAction = new AbstractAction("Ok") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(todoprincipals.size()>0)
			{
				StringBuilder sb = new StringBuilder();
				for(String str:todoprincipals)
				{
					sb.append(str);
					sb.append(",");
				}
				if(sb.length()>0)
				{
					sb.deleteCharAt(sb.length()-1);
				}
				
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog(null, "you have Principals:"+sb.toString()+" to grant permissions, do you want to grant permissions?", Const.EDAL_TITLE_STR,dialogButton);
				if(dialogResult == JOptionPane.YES_OPTION){
					return;
				}
			}
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

	private void inituserprincipalcombox() {
		principalcomboBox.removeAllItems();
		for (String str : userprincipals) {
			principalcomboBox.addItem(str);
		}

		if (principalcomboBox.getItemCount() > 0) {
			refreshPermissiontable(dataentry, users.get(0), principals.get(0));
		}
	}

	private void inituserprincipallist(Principal principal) {
		users.clear();
		principals.clear();
		userprincipals.clear();

		if(principal!=null)
		{
			users.add(principal.getName());
			principals.add(principal.getClass().getName());
			userprincipals.add(principal.getName() + ":" + principal.getClass().getSimpleName());
			
			todoprincipals.add(principal.getName() + ":" + principal.getClass().getSimpleName());
		}
		
		Map<Principal, List<Methods>> permissionmap;
		try {
			permissionmap = dataentry.getPermissions();
			if (permissionmap != null) {
				Iterator<Map.Entry<Principal, List<Methods>>> it = permissionmap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Principal, List<Methods>> entry = (Map.Entry<Principal, List<Methods>>) it.next();
					Principal key = entry.getKey();
					users.add(key.getName());
					principals.add(key.getClass().getName());
					userprincipals.add(key.getName() + ":"
							+ key.getClass().getSimpleName());
				}
			}
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (PrimaryDataEntityException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
	}

	private void initpermissionpanel(JPanel permissioneditpanel) {
		permissioneditpanel.setBorder(BorderFactory.createEtchedBorder());
		permissioneditpanel.setLayout(new GridBagLayout());
		JLabel sourceLabel = new JLabel(DEFAULT_SOURCE_CHOICE_LABEL);
		sourceListModel = new SortedListModel();
		sourceList = new JList<String>(sourceListModel);
		permissioneditpanel.add(sourceLabel, new GridBagConstraints(0, 0, 1, 1,
				0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		JScrollPane sourcepane = new JScrollPane(sourceList);
		sourcepane.setPreferredSize(new Dimension(220, 100));
		permissioneditpanel.add(sourcepane,
				new GridBagConstraints(0, 1, 1, 5, .5, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
						EMPTY_INSETS, 0, 0));

		JButton addButton = new JButton(ADD_BUTTON_LABEL);
		addButton.setPreferredSize(new Dimension(110, 30));
		
		permissioneditpanel.add(addButton, new GridBagConstraints(1, 2, 1, 2,
				0, .25, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		addButton.addActionListener(new AddListener());
		removeButton = new JButton(REMOVE_BUTTON_LABEL);
		removeButton.setPreferredSize(new Dimension(110, 30));
		permissioneditpanel.add(removeButton, new GridBagConstraints(1, 4, 1,
				2, 0, .25, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		removeButton.addActionListener(new RemoveListener());

		destLabel = new JLabel(DEFAULT_DEST_CHOICE_LABEL);
		destListModel = new SortedListModel();
		destList = new JList<String>(destListModel);
		permissioneditpanel.add(destLabel, new GridBagConstraints(2, 0, 1, 1,
				0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		JScrollPane destpane = new JScrollPane(destList);
		destpane.setPreferredSize(new Dimension(220, 100));
		permissioneditpanel.add(destpane,
				new GridBagConstraints(2, 1, 1, 5, .5, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
						EMPTY_INSETS, 0, 0));
	}

	public int showOpenDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}

	
	private void fillListModel(SortedListModel model, ListModel<String> newValues) {
		int size = newValues.getSize();
		for (int i = 0; i < size; i++) {
			model.add(newValues.getElementAt(i));
		}
	}

	private void fillListModel(SortedListModel model, String[] newValues) {
		model.addAll(newValues);
	}

	public void addDestinationElements(String[] newValues) {
		fillListModel(destListModel, newValues);
	}

	public void addSourceElements(ListModel<String> newValue) {
		fillListModel(sourceListModel, newValue);
	}

	public void addSourceElements(String newValue[]) {
		fillListModel(sourceListModel, newValue);
	}

	private void addSourceElement(String newValue) {
		sourceListModel.add(newValue);
	}

	private void addDestinationElement(String newValue) {
		destListModel.add(newValue);
	}

	private void clearSourceListModel() {
		sourceListModel.clear();
	}

	private void clearDestinationListModel() {
		destListModel.clear();
	}

	private void clearSourceSelected() {
		if(sourceList.getSelectedValuesList().size()>0)
		{
			Object selected[] = sourceList.getSelectedValuesList().toArray();
			for (int i = selected.length - 1; i >= 0; --i) {
				sourceListModel.removeElement(selected[i]);
			}
		}		
		sourceList.getSelectionModel().clearSelection();
	}

	private void clearDestinationSelected() {
		if(destList.getSelectedValuesList().size()>0)
		{
			Object selected[] = destList.getSelectedValuesList().toArray();
			for (int i = selected.length - 1; i >= 0; --i) {
				destListModel.removeElement(selected[i]);
			}
		}		
		destList.getSelectionModel().clearSelection();
	}


	private class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selected[] = sourceList.getSelectedValuesList().toArray(new String[0]);
			addDestinationElements(selected);
			clearSourceSelected();
		}
	}

	private class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selected[] = destList.getSelectedValuesList().toArray(new String[0]);
			addSourceElements(selected);
			clearDestinationSelected();
		}
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

		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			refreshPermissiontable(dataentry,
					users.get(principalcomboBox.getSelectedIndex()),
					principals.get(principalcomboBox.getSelectedIndex()));
		}
	}

	private void refreshPermissiontable(ClientPrimaryDataEntity dataentry,
			String username, String principalname) {
		methods.clear();

		Map<Principal, List<Methods>> permissionmap;
		try {
			permissionmap = dataentry.getPermissions();
			if (permissionmap != null) {
				Iterator<Map.Entry<Principal, List<Methods>>> it = permissionmap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Principal, List<Methods>> entry = (Map.Entry<Principal, List<Methods>>) it
							.next();
					Principal key = entry.getKey();
					List<Methods> value = entry.getValue();
					if (key.getName().equals(username)
							&& key.getClass().getName().equals(principalname)) {
						for (Methods method : value) {
							methods.add(method.toString());
						}
					}
				}
			}
			clearSourceListModel();
			clearDestinationListModel();

			List<String> allavailablenoselectedmethods = new ArrayList<String>();

			for (Methods method : allavailablemethods) {
				if (!methods.contains(method.toString())) {
					allavailablenoselectedmethods.add(method.toString());
				}
			}

			addSourceElements(allavailablenoselectedmethods.toArray(new String[0]));
			addDestinationElements(methods.toArray(new String[0]));
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (PrimaryDataEntityException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
	}



	private Action addnewAction = new AbstractAction("Add NewUser") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			PermissionNewuserDialog permission = new PermissionNewuserDialog(
					dataentry, client);
			int returnval = permission.showOpenDialog();
			if (returnval == PermissionNewuserDialog.APPROVE_OPTION && permission.getPrincipal()!=null) {
				// fresh
				inituserprincipallist(permission.getPrincipal());
				inituserprincipalcombox();
			}
		}
	};

	private Action applyAction = new AbstractAction("Apply") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			// get principal
			String currentuser = users
					.get(principalcomboBox.getSelectedIndex());
			String currentprincipalname = principals.get(principalcomboBox
					.getSelectedIndex());
			Principal currentprincipal = PrincipalUtil.getInstance(
					currentprincipalname, currentuser);

			if (currentprincipal != null) {
				List<String> destinationlist = new ArrayList<String>();
				List<String> sourcelist = new ArrayList<String>();
				// first delete all the permissions
				int souresize = sourceListModel.getSize();
				for (int i = 0; i < souresize; i++) {
					try {
						boolean isin = PrincipalUtil.checkPermission(dataentry,
								currentprincipalname, currentuser,
								sourceListModel.getElementAt(i).toString());
						if (isin) {
							dataentry.revokePermission(currentprincipal,
									Methods.valueOf(sourceListModel
											.getElementAt(i).toString()));
						}
					} catch (RemoteException ex) {
						ClientDataManager.logger.error(StackTraceUtil
								.getStackTrace(ex));
						ErrorDialog.showError(ex);
						// we should move the method to destincationlist
						destinationlist.add(sourceListModel.getElementAt(i));
					}
					catch(PrimaryDataEntityException ex)
					{
						// we should move the method to destincationlist
						destinationlist.add(sourceListModel.getElementAt(i));
					}
					catch (Exception ex) {
						ClientDataManager.logger.error(StackTraceUtil
								.getStackTrace(ex));
						ErrorDialog.showError(ex);
						// we should move the method to destincationlist
						destinationlist.add(sourceListModel.getElementAt(i));
					}

				}

				// grant permissions to user
				int destsize = destListModel.getSize();
				for (int i = 0; i < destsize; i++) {
					try {
						boolean isin = PrincipalUtil.checkPermission(dataentry,
								currentprincipalname, currentuser,
								destListModel.getElementAt(i).toString());
						if (!isin) {
							dataentry.grantPermission(
									currentprincipal,
									Methods.valueOf(destListModel.getElementAt(
											i).toString()));
						}
					} catch (RemoteException ex) {
						ClientDataManager.logger.error(StackTraceUtil
								.getStackTrace(ex));
						ErrorDialog.showError(ex);
						// we should move the method to sourcelist
						sourcelist.add(destListModel.getElementAt(i));
					}
					catch(PrimaryDataEntityException ex)
					{
						// we should move the method to sourcelist
						sourcelist.add(destListModel.getElementAt(i));
					}
					catch (Exception ex) {
						ClientDataManager.logger.error(StackTraceUtil
								.getStackTrace(ex));
						ErrorDialog.showError(ex);
						// we should move the method to sourcelist
						sourcelist.add(destListModel.getElementAt(i));
					}

				}
				String msg = "Grant permissions finish!";

				if (destinationlist.size() > 0) {
					msg += "some methods:";
					for (Object obj : destinationlist) {
						msg += obj.toString();
						msg += ",";
						addDestinationElement(obj.toString());
						sourceListModel.removeElement(obj);
					}
					msg = msg.substring(0, msg.length() - 1);
					msg += " can't be revokePermission!";
				}

				if (sourcelist.size() > 0) {
					msg += "some methods:";
					for (Object obj : sourcelist) {
						msg += obj.toString();
						msg += ",";
						addSourceElement(obj.toString());
						destListModel.removeElement(obj);
					}
					msg = msg.substring(0, msg.length() - 1);
					msg += " can't be grantPermission!";
				}

				JOptionPane.showMessageDialog(null, msg, "EdalFileChooser",	JOptionPane.INFORMATION_MESSAGE);
				todoprincipals.remove(currentprincipal.getName() + ":" + currentprincipal.getClass().getSimpleName());				
			}

		}
	};

	private WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				returnvalue = CANCEL_OPTION;
				dispose();
			}
		};
	}
}

class SortedListModel extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	SortedSet<String> model;

	public SortedListModel() {
		model = new TreeSet<String>();
	}

	public int getSize() {
		return model.size();
	}

	public String getElementAt(int index) {
		return model.toArray(new String[0])[index];
	}

	public void add(String element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(String elements[]) {
		Collection<String> c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator<String> iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}
