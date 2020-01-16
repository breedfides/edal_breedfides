/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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

import org.apache.logging.log4j.core.util.Loader;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper.EdalZipFileVisitorRmi;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class TestZipFileRmi extends ClientServerTest {
	private int indent = 0;
	private final StringBuffer output = new StringBuffer("");
	private final SortedSet<String> outputSet = new TreeSet<>();

	public void listDir(final ClientPrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException, AccessControlException,
			RemoteException {
		this.indent += 1;
		final List<ClientPrimaryDataEntity> list = currentDirectory
				.listPrimaryDataEntities();

		if (list != null) {
			for (final ClientPrimaryDataEntity primaryDataEntity : list) {
				this.outputSet.add(primaryDataEntity.getPath());

				for (int j = 1; j < this.indent; j++) {
					this.output.append("  ");
				}
				this.output.append(primaryDataEntity + "\n");
				if (primaryDataEntity.isDirectory()) {
					this.listDir((ClientPrimaryDataDirectory) primaryDataEntity);
				} else {
				}
			}
			this.indent -= 1;
		}
	}

	@Test
	public void testzip() throws Exception {

		Authentication auth = new Authentication(EdalHelpers.authenticateUser(
				"SampleUser", "password"));

		final ClientDataManager dataManagerClient = new ClientDataManager(
				ClientServerTest.HOST, ClientServerTest.REGISTRY_PORT, auth);

		final ClientPrimaryDataDirectory rootDirectory = dataManagerClient
				.getRootDirectory();

		final Path path = Paths.get(Loader.getResource("bmc_article.zip",null)
				.toURI());

		if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
			System.out.println("Given path do not exist");
			System.exit(0);
		}

		try (FileSystem fs = FileSystems.newFileSystem(path, null)) {

			final EdalZipFileVisitorRmi edalVisitor = new EdalZipFileVisitorRmi(
					rootDirectory);
			final long zeitVorher1 = System.currentTimeMillis();
			Files.walkFileTree(fs.getPath("/"), edalVisitor);
			final long zeitNachher1 = System.currentTimeMillis();

			final long zeitVorher2 = System.currentTimeMillis();
			this.listDir(rootDirectory);
			final long zeitNachher2 = System.currentTimeMillis();

			final SortedSet<String> inputSet = edalVisitor.getPathSet();

			System.out.println("\n" + "Zeit (Speichern): "
					+ (zeitNachher1 - zeitVorher1) / 1000 + " sec");
			System.out.println("Zeit (Auflisten): "
					+ (zeitNachher2 - zeitVorher2) / 1000 + " sec");

			System.out.println(inputSet.equals(this.outputSet));

			Assertions.assertEquals(inputSet, this.outputSet);
		}

	}

}
