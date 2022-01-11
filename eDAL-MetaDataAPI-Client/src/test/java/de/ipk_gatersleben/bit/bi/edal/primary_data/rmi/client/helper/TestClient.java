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
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class TestClient {

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072", new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("user@nodomain.com.invalid"), new InternetAddress("eDAL0815@ipk-gatersleben.de"));

		configuration.setUseSSL(false);

		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, true, false);

		Authentication testAuthentication = new Authentication(EdalHelpers.authenticateWinOrUnixOrMacUser());

		ClientDataManager clientDataManager = new ClientDataManager("localhost", EdalServer.DEFAULT_REGISTRY_PORT, testAuthentication);

		ClientPrimaryDataDirectory rootDirectory = clientDataManager.getRootDirectory();

		ClientPrimaryDataFile file = rootDirectory.createPrimaryDataFile("File.txt");

		System.out.println(file.getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM));

	
		// MetaData metadata = file.getMetaData().clone();
		//
		// metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new
		// UntypedData("Description"));
		//
		// file.setMetaData(metadata);
		//
		// System.out.println(file.getMetaData().getElementValue(EnumDublinCoreElements.CHECKSUM));

	}
}
