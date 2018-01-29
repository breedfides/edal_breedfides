/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;

/**
 * A utility class used to manage Principal
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class PrincipalUtil {

	/**
	 * From the <code>classname</code> and <code>username</code> to get a
	 * Principal Instance
	 * 
	 * @param classname
	 *            the class name for {@link Principal}
	 * @param username
	 *            the name of the user
	 * @return {@link Principal}
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Principal getInstance(String classname, String username) {
		Class c = null;
		Constructor con = null;
		Principal principal = null;
		try {
			c = Class.forName(classname);
			con = c.getConstructor(String.class);
			principal = (Principal) con.newInstance(username);
			return principal;
		} catch (Exception e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
	}

	public static boolean checkPermission(ClientPrimaryDataEntity dataentry, String principalname, String username,
			String methodname) {

		try {
			Map<Principal, List<Methods>> permissionmap = dataentry.getPermissions();
			if (permissionmap != null) {
				Iterator<Map.Entry<Principal, List<Methods>>> it = permissionmap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Principal, List<Methods>> entry = (Map.Entry<Principal, List<Methods>>) it.next();
					Principal key = entry.getKey();
					if (key.getName().equals(username) && key.getClass().getName().equals(principalname)) {
						List<Methods> value = entry.getValue();
						for (Methods method : value) {
							if (method.toString().equals(methodname)) {
								return true;
							}
						}
					}
				}
			}
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (PrimaryDataEntityException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
		return false;
	}

	public static boolean checkPermission(ClientPrimaryDataEntity dataentry, String username, String methodname) {

		try {
			Map<Principal, List<Methods>> permissionmap = dataentry.getPermissions();
			if (permissionmap != null) {
				Iterator<Map.Entry<Principal, List<Methods>>> it = permissionmap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Principal, List<Methods>> entry = (Map.Entry<Principal, List<Methods>>) it.next();
					Principal key = entry.getKey();
					if (key.getName().equals(username)) {
						List<Methods> value = entry.getValue();
						for (Methods method : value) {
							if (method.toString().equals(methodname)) {
								return true;
							}
						}
					}
				}
			}
		} catch (RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (PrimaryDataEntityException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
		return false;
	}
}
