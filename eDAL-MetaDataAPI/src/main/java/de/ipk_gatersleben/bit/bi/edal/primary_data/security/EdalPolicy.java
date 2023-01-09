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
package de.ipk_gatersleben.bit.bi.edal.primary_data.security;

import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Define our own {@link Policy} for the eDAL system, that use a
 * {@link PermissionProvider} to store and provide {@link Permission}s.
 * 
 * @author lange
 * @author arendd
 */
public class EdalPolicy extends Policy {

	/**
	 * Intern {@link PermissionProvider} to store and provide {@link Permission}
	 * .
	 */
	private PermissionProvider permissionProvider;

	/**
	 * Constructor for {@link EdalPolicy} with given {@link PermissionProvider}.
	 * 
	 * @param permissionProvider
	 *            a {@link PermissionProvider} object.
	 */
	public EdalPolicy(final PermissionProvider permissionProvider) {
		super();
		this.permissionProvider = permissionProvider;

	}

	/** {@inheritDoc} */
	@Override
	public PermissionCollection getPermissions(final CodeSource codesource) {
		// others may add to this, so use heterogeneous Permissions
		final Permissions perms = new Permissions();
		perms.add(new AllPermission());
		return perms;
	}

	/** {@inheritDoc} */
	@Override
	public PermissionCollection getPermissions(final ProtectionDomain domain) {
		final Permissions permissions = new Permissions();
		// Look up permissions
		final Set<Principal> principalList = new HashSet<Principal>();
		final Principal[] principals = domain.getPrincipals();

		if (principals != null && principals.length > 0) {
			for (final Principal p : principals) {
				final Principal principal = p;
				principalList.add(principal);
			}
			if (!principalList.isEmpty()) {
				try {
					final List<EdalPermission> perms = AccessController
							.doPrivileged(new PrivilegedExceptionAction<List<EdalPermission>>() {

								public List<EdalPermission> run() {
									return EdalPolicy.this.permissionProvider
											.findPermissions(principalList);
								}
							});
					for (final Permission perm : perms) {
						permissions.add(perm);
					}
				} catch (final PrivilegedActionException e) {
					e.printStackTrace();
				}
			}
		} else if (domain.getCodeSource() != null) {
			final PermissionCollection codeSrcPerms = this
					.getPermissions(domain.getCodeSource());
			for (final Enumeration<?> en = codeSrcPerms.elements(); en
					.hasMoreElements();) {
				final Permission p = (Permission) en.nextElement();
				permissions.add(p);
			}
		}
		return permissions;
	}

	/** {@inheritDoc} */
	@Override
	public boolean implies(final ProtectionDomain domain,
			final Permission permission) {
		final PermissionCollection perms = this.getPermissions(domain);
		return perms.implies(permission);
	}

	/** {@inheritDoc} */
	@Override
	public void refresh() {
		// does nothing for DB.
	}
}
