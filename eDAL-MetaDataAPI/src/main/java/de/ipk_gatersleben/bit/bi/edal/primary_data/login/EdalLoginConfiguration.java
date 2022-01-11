/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

			this.map.put("authentication","simple");
			this.map.put("useSSL", "false");
			this.map.put("debug", "true");
			
			AppConfigurationEntry ldapLoginModule = new AppConfigurationEntry(
					"de.ipk_gatersleben.bit.bi.edal.primary_data.login.MyLDAP", LoginModuleControlFlag.REQUIRED, this.map);

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
