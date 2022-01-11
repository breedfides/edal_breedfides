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
package de.ipk_gatersleben.bit.bi.edal.rmi.test;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;

public class TestRmiServer {

	public static void main(String[] args) throws AddressException, EdalConfigurationException {
		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("eDAL0815@ipk-gatersleben.de")
				,"imap.ipk-gatersleben.de","","");

		configuration.setUseSSL(false);

		configuration.setPublisherURL("https://www.ipk-gatersleben.de");
		
		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, false,
				false);

	}

}
