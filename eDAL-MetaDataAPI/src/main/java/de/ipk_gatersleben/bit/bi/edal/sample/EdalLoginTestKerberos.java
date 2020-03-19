/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */

package de.ipk_gatersleben.bit.bi.edal.sample;

import java.security.Principal;

import javax.security.auth.Subject;

public class EdalLoginTestKerberos {

	public static void main(String[] args) throws Exception {

		Subject s = EdalHelpers.authenticateSubjectWithJKIKerberos("null");

		for (Principal p : s.getPrincipals()) {
			System.out.println(p);
			System.out.println();
		}

	}

}