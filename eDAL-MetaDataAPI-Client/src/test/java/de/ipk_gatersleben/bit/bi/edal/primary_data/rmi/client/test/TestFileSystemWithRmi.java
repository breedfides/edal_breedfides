/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper.EdalDirectoryVisitorRmi;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class TestFileSystemWithRmi extends ClientServerTest {

	private int indent = 0;
	private StringBuffer outputString = new StringBuffer("");
	private SortedSet<String> outputSet = new TreeSet<>();

	private final Path inputPath = Paths.get(System.getProperty("java.home"), "bin");

	private void listDir(final ClientPrimaryDataDirectory currentDirectory) throws PrimaryDataDirectoryException, AccessControlException, RemoteException {

		indent += 1;
		final List<ClientPrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();
		if (list != null) {
			for (final ClientPrimaryDataEntity primaryDataEntity : list) {
				outputSet.add(primaryDataEntity.getPath());
				for (int j = 1; j < indent; j++) {
					outputString.append("  ");
				}
				outputString.append(primaryDataEntity + "\n");
				if (primaryDataEntity.isDirectory()) {
					listDir((ClientPrimaryDataDirectory) primaryDataEntity);
				} else {
				}
			}
			indent -= 1;
		}
	}

	@Test
	public void TestClient() throws Exception {

		Authentication auth = new Authentication(EdalHelpers.authenticateUser("SampleUser", "password"));

		final ClientDataManager dataManagerClient = new ClientDataManager(ClientServerTest.HOST, ClientServerTest.REGISTRY_PORT, auth);

		final ClientPrimaryDataDirectory rootDirectory = dataManagerClient.getRootDirectory();

		if (Files.notExists(inputPath, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("Given path do not exist");
			System.exit(0);
		}
		EdalDirectoryVisitorRmi edalVisitor = new EdalDirectoryVisitorRmi(rootDirectory, inputPath, false);

		final long zeitVorher1 = System.currentTimeMillis();
		Files.walkFileTree(inputPath, edalVisitor);
		final long zeitNachher1 = System.currentTimeMillis();

		final long zeitVorher2 = System.currentTimeMillis();
		this.listDir(rootDirectory);
		final long zeitNachher2 = System.currentTimeMillis();

		SortedSet<String> inputSet = edalVisitor.getPathSet();

		System.out.println("\n" + "Zeit (Speichern): " + (zeitNachher1 - zeitVorher1) / 1000 + " sec");
		System.out.println("Zeit (Auflisten): " + (zeitNachher2 - zeitVorher2) / 1000 + " sec");

		System.out.println(outputSet.equals(inputSet));

		Assertions.assertEquals(outputSet, inputSet);

	}
}
