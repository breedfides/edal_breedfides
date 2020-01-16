/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.security.Principal;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;

public class PermissionUtil {
	public static boolean cannewdirectory(ClientPrimaryDataEntity dataentry,Subject subject)
	{
		Set<Principal> principals = subject.getPrincipals();
		Iterator<Principal> iterator = principals.iterator();
		while (iterator.hasNext()) {
			//Principal principal = (Principal)iterator.next();
		}
		
		return true;
	}
}
