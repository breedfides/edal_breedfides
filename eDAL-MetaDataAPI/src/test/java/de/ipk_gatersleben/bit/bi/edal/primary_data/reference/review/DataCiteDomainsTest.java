/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.reference.review;

import java.net.InetAddress;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpServer;

public class DataCiteDomainsTest {

	private static final String EXAMPLE = "example.de";
	private static final String IPK = "ipk-gatersleben.de";
	private static final String DOI_IPK = "doi.ipk-gatersleben.de";
	private static final String BIT_253 = "bit-253.ipk-gatersleben.de";
	private static final String BIT_252 = "bit-252.ipk-gatersleben.de";

	@Test
	public void testIfLocalhostIsInDomain() throws Exception {

		Assert.assertTrue("localhost is the registrated domain", EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), BIT_252));

		Assert.assertFalse("localhost is not the registrated domain", EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), BIT_253));

		Assert.assertTrue("all host domians are allowed", EdalHttpServer.checkIfLocalhostIsInDomain(InetAddress.getByName(BIT_252), "*"));

	}

	@Test
	public void testIfDomainIsAliasForLocalhost() throws Exception {

		Assert.assertTrue("domain is an alias for localhost", EdalHttpServer.checkIfDomainIsAliasForLocalhost(InetAddress.getByName(BIT_252), DOI_IPK));

		Assert.assertTrue("localhost is an alias for domain", EdalHttpServer.checkIfDomainIsAliasForLocalhost(InetAddress.getByName(DOI_IPK), BIT_252));

	}

	@Test
	public void testIfLocalhostHasSubDomain() throws Exception {

		Assert.assertTrue("localhost is in subdomain", EdalHttpServer.checkIfLocalhostHasSubDomain(InetAddress.getByName(BIT_252), IPK));

		Assert.assertFalse("localhost is not in subdomain", EdalHttpServer.checkIfLocalhostHasSubDomain(InetAddress.getByName(BIT_252), EXAMPLE));

	}

}
