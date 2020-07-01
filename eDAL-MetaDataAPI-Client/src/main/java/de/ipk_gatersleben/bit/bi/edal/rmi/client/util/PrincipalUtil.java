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
