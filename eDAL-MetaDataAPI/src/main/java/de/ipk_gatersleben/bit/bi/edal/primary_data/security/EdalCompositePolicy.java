/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.security;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * JAAS policy for merge existing set {@link Policy} and {@link EdalPolicy}
 * 
 * @author lange
 */
public class EdalCompositePolicy extends Policy {

	private List<Policy> policies = Collections.emptyList();

	/**
	 * Constructor for {@link EdalCompositePolicy} with specified {@link List}
	 * of {@link Policy}s.
	 * 
	 * @param policies
	 *            {@link Policy} {@link List}.
	 */
	public EdalCompositePolicy(final List<Policy> policies) {
		this.policies = new ArrayList<Policy>(policies);
	}

	/** {@inheritDoc} */
	@Override
	public PermissionCollection getPermissions(final CodeSource codesource) {
		final Permissions perms = new Permissions();
		for (final Iterator<Policy> itr = this.policies.iterator(); itr
				.hasNext();) {
			final Policy p = (Policy) itr.next();
			final PermissionCollection permsCol = p.getPermissions(codesource);
			for (final Enumeration<Permission> en = permsCol.elements(); en
					.hasMoreElements();) {
				final Permission p1 = (Permission) en.nextElement();
				perms.add(p1);
			}
		}
		return perms;
	}

	/** {@inheritDoc} */
	@Override
	public PermissionCollection getPermissions(final ProtectionDomain domain) {
		final Permissions perms = new Permissions();
		for (final Iterator<Policy> itr = this.policies.iterator(); itr
				.hasNext();) {
			final Policy p = (Policy) itr.next();
			final PermissionCollection permCol = p.getPermissions(domain);
			for (final Enumeration<Permission> en = permCol.elements(); en
					.hasMoreElements();) {
				final Permission p1 = (Permission) en.nextElement();
				perms.add(p1);
			}
		}
		return perms;
	}

	/** {@inheritDoc} */
	@Override
	public boolean implies(final ProtectionDomain domain,
			final Permission permission) {
		for (final Iterator<Policy> itr = this.policies.iterator(); itr
				.hasNext();) {
			final Policy p = (Policy) itr.next();
			if (p.implies(domain, permission)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void refresh() {
		for (final Iterator<Policy> itr = this.policies.iterator(); itr
				.hasNext();) {
			final Policy p = (Policy) itr.next();
			p.refresh();
		}
	}
}
