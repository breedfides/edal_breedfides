/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.review;

import java.net.InetAddress;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;

public class DataCiteDomainsTest {

	private static final String EXAMPLE = "example.de";
	private static final String IPK = "ipk-gatersleben.de";
	private static final String DOI_IPK = "doi.ipk-gatersleben.de";
	private static final String BIT_253 = "bit-253.ipk-gatersleben.de";
	private static final String BIT_252 = "bit-252.ipk-gatersleben.de";

	@Test
	@Disabled("Not working any longer for bit-252 due to new DNS configuration")
	public void testIfLocalhostIsInDomain() throws Exception {

		Assertions.assertTrue(EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), BIT_252),
				"localhost is the registrated domain");

		Assertions.assertFalse(EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), BIT_253),
				"localhost is not the registrated domain");

		Assertions.assertTrue(EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), "*"),
				"all host domians are allowed");

	}

	@Test
	@Disabled("Not working any longer for bit-252 due to new DNS configuration")
	public void testIfDomainIsAliasForLocalhost() throws Exception {

		Assertions.assertTrue(EdalHttpServer.checkIfDomainIsAliasForLocalhost(InetAddress.getByName(BIT_252), DOI_IPK),
				"domain is an alias for localhost");

		Assertions.assertTrue(EdalHttpServer.checkIfDomainIsAliasForLocalhost(InetAddress.getByName(DOI_IPK), BIT_252),
				"localhost is an alias for domain");

	}

	@Test
	@Disabled("Not working any longer for bit-252 due to new DNS configuration")
	public void testIfLocalhostHasSubDomain() throws Exception {

		Assertions.assertTrue(EdalHttpServer.checkIfLocalhostHasSubDomain(InetAddress.getByName(BIT_252), IPK),
				"localhost is in subdomain");

		Assertions.assertFalse(EdalHttpServer.checkIfLocalhostHasSubDomain(InetAddress.getByName(BIT_252), EXAMPLE),
				"localhost is not in subdomain");

	}

}
