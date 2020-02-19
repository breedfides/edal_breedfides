/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.login;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

public class EdalLoginConfiguration extends Configuration {

	private Map<String, String> map = new HashMap<String, String>();

	public EdalLoginConfiguration() {
	}
	
	public EdalLoginConfiguration(HashMap<String, String> parameter) {
		this.map = parameter;
	}

	@Override
	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {

		AppConfigurationEntry[] array = new AppConfigurationEntry[1];

		switch (name) {
		case "Sample":

			this.map.put("debug", "false");

			AppConfigurationEntry sampleLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.SampleUserLoginModule",
					LoginModuleControlFlag.REQUIRED, map);

			array[0] = sampleLoginModule;

			return array;

		case "Windows":

			this.map.put("debug", "false");

			AppConfigurationEntry windowsLoginModule = new AppConfigurationEntry(
					"com.sun.security.auth.module.NTLoginModule", LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = windowsLoginModule;

			return array;

		case "Unix":

			this.map.put("debug", "false");

			AppConfigurationEntry unixLoginModule = new AppConfigurationEntry(
					"com.sun.security.auth.module.UnixLoginModule", LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = unixLoginModule;

			return array;

		case "Kerberos":

			this.map.put("debug", "false");
			this.map.put("useTicketCache", "false");
			this.map.put("storeKey", "true");

			AppConfigurationEntry kerberosLoginModule = new AppConfigurationEntry(
					"com.sun.security.auth.module.Krb5LoginModule", LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = kerberosLoginModule;

			return array;
		case "LDAP":

//			this.map.put("debug", "false");
//			this.map.put("useTicketCache", "false");
//			this.map.put("storeKey", "true");

			AppConfigurationEntry ldapLoginModule = new AppConfigurationEntry(
					"com.sun.security.auth.module.LdapLoginModule", LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = ldapLoginModule;

			return array;
		case "User":

			AppConfigurationEntry userLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.UserLoginModule",
					LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = userLoginModule;

			return array;

		case "Google":

			AppConfigurationEntry newGoogleLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.GoogleLoginModule",
					LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = newGoogleLoginModule;

			return array;
		case "ORCID":

			AppConfigurationEntry orcidLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.ORCIDLoginModule",
					LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = orcidLoginModule;

			return array;
		case "Elixir":

			AppConfigurationEntry elixirLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.ElixirLoginModule",
					LoginModuleControlFlag.REQUIRED, this.map);

			array[0] = elixirLoginModule;

			return array;
		default:
			return array;
		}

	}

}
