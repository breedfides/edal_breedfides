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
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.parallel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper.EdalDirectoryVisitorRmi;
import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.ClientServerTest;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class RmiUploadDownloadTwo {

	private int indent = 0;
	private final StringBuffer outputString = new StringBuffer("");
	private final SortedSet<String> outputSet = new TreeSet<>();
	private OutputStream outputStream;

	private final Path INPUT_PATH = Paths.get(System.getProperty("java.home"), "lib");

	private Path OUTPUT_PATH = Paths.get(System.getProperty("user.home"), "TEST_READ_2");

	/**
	 * List recursively all {@link PrimaryDataEntity} object in the current
	 * {@link PrimaryDataDirectory} an save names in the {@link StringBuffer} 
	 * 'outputSet'. Save complete structure of the {@link PrimaryDataDirectory}
	 * into the FileSystem.
	 * 
	 * @param currentDirectory
	 *            the {@link PrimaryDataDirectory} to list and read all
	 *            containing {@link PrimaryDataEntity}.
	 * @throws PrimaryDataDirectoryException
	 * @throws PrimaryDataFileException
	 * @throws FileNotFoundException
	 * @throws RemoteException
	 * @throws AccessControlException
	 */
	private void listDir(final ClientPrimaryDataDirectory currentDirectory) throws PrimaryDataDirectoryException, PrimaryDataFileException, FileNotFoundException, AccessControlException, RemoteException {

		this.indent += 1;
		final List<ClientPrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final ClientPrimaryDataEntity primaryDataEntity : list) {
				this.outputSet.add(primaryDataEntity.getPath());

				for (int j = 1; j < this.indent; j++) {
					this.outputString.append("  ");
				}
				this.outputString.append(primaryDataEntity + "\n");
				if (!primaryDataEntity.isDirectory()) {
					final ClientPrimaryDataFile file = (ClientPrimaryDataFile) primaryDataEntity;

					final File tmp = Paths.get(this.OUTPUT_PATH.toString(), primaryDataEntity.getName()).toFile();

					if (!tmp.exists()) {
						try {
							tmp.getParentFile().mkdirs();
							tmp.createNewFile();
						} catch (final IOException e) {
							System.out.println("couldn't create File");
						}
					}

					this.outputStream = new FileOutputStream(tmp);
					file.read(this.outputStream);

				}
				if (primaryDataEntity.isDirectory()) {
					this.OUTPUT_PATH = Paths.get(this.OUTPUT_PATH.toString(), primaryDataEntity.getName());
					this.listDir((ClientPrimaryDataDirectory) primaryDataEntity);
				}
			}
			this.indent -= 1;
			this.OUTPUT_PATH = this.OUTPUT_PATH.getParent();
		}
	}

//	@Test
	public void TestStoreRead() throws Exception {

		Authentication auth = new Authentication(EdalHelpers.authenticateUser("SampleUser_2", "password"));

		final ClientDataManager dataManagerClient = new ClientDataManager(ClientServerTest.HOST, ClientServerTest.REGISTRY_PORT, auth);

		final ClientPrimaryDataDirectory rootDirectory = dataManagerClient.getRootDirectory().createPrimaryDataDirectory("SampleUser_2");

		if (Files.notExists(this.INPUT_PATH, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("Given path do not exist");
			System.exit(0);
		}
		final EdalDirectoryVisitorRmi edalVisitor = new EdalDirectoryVisitorRmi(rootDirectory, this.INPUT_PATH, true);

		final long zeitVorher1 = System.currentTimeMillis();
		Files.walkFileTree(this.INPUT_PATH, edalVisitor);
		final long zeitNachher1 = System.currentTimeMillis();

		final long zeitVorher2 = System.currentTimeMillis();
		this.listDir(rootDirectory);
		final long zeitNachher2 = System.currentTimeMillis();

		// final SortedSet<String> inputSet = edalVisitor.getPathSet();

		System.out.println("\nZeit (Speichern): " + (zeitNachher1 - zeitVorher1) / 1000 + " sec");
		System.out.println("Zeit (Auflisten): " + (zeitNachher2 - zeitVorher2) / 1000 + " sec");

		// System.out.println(inputSet.equals(this.outputSet));
		//
		// Assert.assertEquals(inputSet, this.outputSet);

		Path START_INPUT_PATH = Paths.get(System.getProperty("java.home"), "lib");
		Path START_OUTPUT_PATH = Paths.get(System.getProperty("user.home"), "TEST_READ_2", "lib");

		if (Files.size(START_INPUT_PATH) == Files.size(START_OUTPUT_PATH)) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}

		Assert.assertEquals(Files.size(START_INPUT_PATH), Files.size(START_OUTPUT_PATH));

		FileUtils.deleteDirectory(START_OUTPUT_PATH.getParent().toFile());

	}
}
