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
package de.ipk_gatersleben.bit.bi.edal.aspectj.security;

import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Calendar;

import javax.security.auth.Subject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.SuppressAjWarnings;

import de.ipk_gatersleben.bit.bi.edal.aspectj.security.GrantableMethods.Methods;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PermissionProviderImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * 
 * Permission Check for public methods
 * 
 * @author arendd
 * 
 */
public aspect PublicPermissionCheck {

	/**
	 * Check if the Subject that execute the method of this JoinPoint is allowed
	 * to use this method.
	 * <p>
	 * If the {@link Subject} is root user no security check necessary.
	 * Otherwise getting all values to check the permission from the getter
	 * methods of the class of the JoinPoint.
	 * 
	 * @param joinPoint
	 *            the current JoinPoint
	 * @throws AccessControlException
	 */

	public void checkPermission(JoinPoint joinPoint)
			throws AccessControlException {

		if (DataManager.getImplProv() == null) {
			throw new AccessControlException(
					"cannot find current ImplementationProvider for PermissionCheck");
		}

		DataManager.getImplProv().getLogger()
				.debug("WEAVED: check permission: " + joinPoint.toString());

		if (DataManager.getSubject() == null) {
			throw new AccessControlException(
					"cannot find current Subject for PermissionCheck");
		}

		Subject subject = DataManager.getSubject();

		/**
		 * get the first Principal of the current user --> loop breaks after
		 * first Principal
		 */

		Principal principal = null;
		for (final Principal p : subject.getPrincipals()) {
			principal = p;
			break;
		}
		DataManager.getImplProv().getLogger()
				.debug("WEAVED: check permission: " + principal);

		PermissionProvider permissionProvider = null;
		try {
			permissionProvider = DataManager.getImplProv()
					.getPermissionProvider().newInstance();
		} catch (Exception e) {
			throw new AccessControlException(
					"unable to load PermissionProvider");
		}

		if (!permissionProvider.isRoot(principal)) {

			String entity = (String) useGetterMethod(joinPoint, "getName");

			String uuid = (String) useGetterMethod(joinPoint, "getID");

			/* set ThreadLocalVariable for Entity-ID */
			permissionProvider.setPermissionObjectID(uuid);

			PrimaryDataEntityVersion entityVersion = (PrimaryDataEntityVersion) useGetterMethod(
					joinPoint, "getCurrentVersion");

			long version = entityVersion.getRevision();

			@SuppressWarnings("unchecked")
			Class<? extends PrimaryDataEntity> actionClass = (Class<? extends PrimaryDataEntity>) joinPoint
					.getSignature().getDeclaringType();

			Methods enumMethod = Methods.valueOf(joinPoint.getSignature()
					.getName());

			Method method = null;
			try {
				method = enumMethod.getImplClass(actionClass);
			} catch (Exception e) {
				throw new AccessControlException(
						"cannot load Method for security check: "
								+ e.getMessage());
			}

			if (!(entity.equals("/") && (enumMethod
					.equals(Methods.createPrimaryDataDirectory)
					|| enumMethod.equals(Methods.createPrimaryDataFile)
					|| enumMethod.equals(Methods.listPrimaryDataEntities)
					|| enumMethod.equals(Methods.getPrimaryDataEntity)
					|| enumMethod.equals(Methods.searchByDublinCoreElement)
					|| enumMethod.equals(Methods.searchByMetaData)
					|| enumMethod.equals(Methods.searchByPublicationStatus)
					|| enumMethod.equals(Methods.searchByKeyword) || enumMethod
						.equals(Methods.exist)))) {

				final EdalPermission edalPerm = new EdalPermission(uuid,
						version, actionClass, method);

				boolean checkedPermission = checkPerm(subject, edalPerm);

				DataManager
						.getImplProv()
						.getLogger()
						.debug("WEAVED: checkedpermission :"
								+ checkedPermission);

				if (!checkedPermission) {
					String name = (String) useGetterMethod(joinPoint, "getName");

					DataManager
							.getImplProv()
							.getLogger()
							.debug("WEAVED: forbidden Permission: "
									+ edalPerm
									+ "\nfor "
									+ principal
									+ "\t"
									+ PermissionProviderImplementation
											.getThreadlocalentityid().get());

					throw new AccessControlException(principal.getName() + "("
							+ principal.getClass().getSimpleName() + ")"
							+ " is not allowed to use method "
							+ method.getName() + " for Entity '" + name + "'");
				}
			}

		} else {
			DataManager.getImplProv().getLogger()
					.debug("WEAVED: permission allowed for root");
		}
	}

	/**
	 * method to call Getter methods from the Class of the JoinPoint
	 * 
	 * @param jp
	 *            current JoinPoint
	 * @param methodName
	 * @return Object the returned object of the getter method
	 */

	private Object useGetterMethod(JoinPoint joinPoint, String methodName) {

		Object obj = null;
		try {
			Method method = joinPoint.getThis().getClass()
					.getMethod(methodName, new Class<?>[0]);
			obj = method.invoke(joinPoint.getThis(), new Object[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Check if the permission for the {@link Subject}.
	 * 
	 * @param mySubject
	 *            the subject for call the doAS method
	 * @param edalPerm
	 *            the EDALPermission
	 * @return
	 */

	private boolean checkPerm(final Subject mySubject,
			final EdalPermission edalPerm) {
		try {
			Subject.doAs(mySubject, new PrivilegedAction<Object>() {
				public Object run() {
					final SecurityManager sm = System.getSecurityManager();
					if (sm != null) {
						sm.checkPermission(edalPerm);
					}
					return null;
				}
			});
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}

	/**
	 * Surround all public methods of PrimaryDataEntity and subclasses and check
	 * if they have permissions for the logged in subject.
	 * <p>
	 * no check of getRootDirectory, toString, getName and following advice no
	 * check of compareTo, hashCode, equals and following advice
	 * 
	 */

	pointcut checkPublicMethods(): execution(public * PrimaryDataEntity+.* (..)) 
	&& !execution(public * PrimaryDataEntity+.getRootDirectory (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getRootDirectory (..)))
	&& !execution(public * PrimaryDataEntity+.toString (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.toString (..)))
	&& !execution(public * PrimaryDataEntity+.getName (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getName (..)))
	&& !execution(public * PrimaryDataEntity+.getPath (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getPath (..)))
	&& !execution(public * PrimaryDataEntity+.getPermissions (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getPermissions (..)))
	&& !execution(public * PrimaryDataEntity+.getID (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getID (..)))
	&& !execution(public * PrimaryDataEntity+.isDirectory (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.isDirectory (..)))
	&& !execution(public * PrimaryDataEntity+.getMetaData (..))
	&& !execution(public * PrimaryDataEntity+.compareTo (..))
	&& !execution(public * PrimaryDataEntity+.hashCode (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.hashCode (..)))
	&& !execution(public * PrimaryDataEntity+.equals (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.equals (..)))
	&& !execution(public * PrimaryDataEntity+.getCurrentVersion (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getCurrentVersion (..)))
	&& !execution(public * PrimaryDataEntity+.getVersions (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getVersions (..)))
	&& !execution(public * PrimaryDataEntity+.getVersionByRevisionNumber(long))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getVersionByRevisionNumber(long)))
	&& !execution(public * PrimaryDataEntity+.getVersionByDate(Calendar))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.getVersionByDate(Calendar)))
	&& !execution(public * PrimaryDataEntity+.switchCurrentVersion (..))
	&& !cflowbelow(execution(public * PrimaryDataEntity+.switchCurrentVersion (..)))
	&& !execution(public * PrimaryDataDirectory+.getAllPublishedEntities (..))
	&& !cflowbelow(execution(public * PrimaryDataDirectory+.getAllPublishedEntities (..)))
    && !execution(public * PrimaryDataFileImplementation.* (..))
    && !execution(public * PrimaryDataDirectoryImplementation.* (..))
	&& !cflowbelow(adviceexecution());

	@SuppressAjWarnings({ "adviceDidNotMatch" })
	Object around() throws AccessControlException : checkPublicMethods(){
		checkPermission(thisJoinPoint);
		return proceed();
	}

}