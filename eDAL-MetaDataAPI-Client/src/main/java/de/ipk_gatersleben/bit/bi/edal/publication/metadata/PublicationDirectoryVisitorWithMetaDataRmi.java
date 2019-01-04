/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JProgressBar;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

/**
 * Implementation of {@link FileVisitor} to walk a file system tree and store
 * the object into eDAL.
 * 
 * @author arendd
 */
public class PublicationDirectoryVisitorWithMetaDataRmi implements FileVisitor<Path> {

	private static final List<String> FORBIDDEN_FILES = Arrays.asList("thumbs.db", ".DS_Store", "desktop.ini");
	private ClientPrimaryDataDirectory currentDirectory = null;
	private MetaData metaData;
	private ClientPrimaryDataDirectory directoryToPublish = null;
	private JProgressBar overallProgressBar;
	private JProgressBar fileProgressBar;
	private boolean updateEdalObject = false;
	private boolean createSeparateFolder = false;
	private CountDownLatch latch;
	private boolean alreadyVisitedRootFolder = false;

	public ClientPrimaryDataDirectory getRootDirectoryToPublish() {
		return directoryToPublish;
	}

	public PublicationDirectoryVisitorWithMetaDataRmi(JProgressBar overallProgressBar, JProgressBar fileProgressBar,
			ClientPrimaryDataDirectory currentDirectory, Path path, MetaData metaData, boolean updateEdalObject,
			boolean createSeparateFolder, CountDownLatch latch) {

		this.currentDirectory = currentDirectory;
		this.metaData = metaData;
		this.overallProgressBar = overallProgressBar;
		this.fileProgressBar = fileProgressBar;
		this.updateEdalObject = updateEdalObject;
		this.createSeparateFolder = createSeparateFolder;
		this.latch = latch;

		try {

			ClientPrimaryDataDirectory newCurrentDirectory = null;

			if (updateEdalObject && this.currentDirectory
					.exist(this.metaData.getElementValue(EnumDublinCoreElements.TITLE).toString())) {

				newCurrentDirectory = (ClientPrimaryDataDirectory) this.currentDirectory
						.getPrimaryDataEntity(this.metaData.getElementValue(EnumDublinCoreElements.TITLE).toString());

				setMetaData(newCurrentDirectory);
			}

			else {

				if (this.createSeparateFolder) {
					newCurrentDirectory = this.currentDirectory.createPrimaryDataDirectory(
							this.metaData.getElementValue(EnumDublinCoreElements.TITLE).toString());
					setMetaData(newCurrentDirectory);
				} else {
					newCurrentDirectory = this.currentDirectory
							.createPrimaryDataDirectory(path.getFileName().toString());
					setMetaData(newCurrentDirectory);
				}
			}

			this.currentDirectory = newCurrentDirectory;

			this.directoryToPublish = newCurrentDirectory;

		} catch (RemoteException | PrimaryDataDirectoryException | MetaDataException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

		if (this.alreadyVisitedRootFolder) {

			try {
				ClientPrimaryDataDirectory newCurrentDirectory = null;

				if (updateEdalObject && this.currentDirectory.exist(dir.getFileName().toString())) {
					newCurrentDirectory = (ClientPrimaryDataDirectory) this.currentDirectory
							.getPrimaryDataEntity(dir.getFileName().toString());
				} else {
					newCurrentDirectory = this.currentDirectory
							.createPrimaryDataDirectory(dir.getFileName().toString());
				}

				this.currentDirectory = newCurrentDirectory;

			} catch (AccessControlException | PrimaryDataDirectoryException e) {
				e.printStackTrace();
			}

			return FileVisitResult.CONTINUE;
		} else {
			// ignore rootFolder of selected directory
			this.alreadyVisitedRootFolder = true;

			return FileVisitResult.CONTINUE;
		}

	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

		try {
			ClientPrimaryDataFile clientPrimaryDataFile = null;

			if (FORBIDDEN_FILES.contains(file.getFileName().toString())) {
				// skip temporary file system cache files
				this.overallProgressBar.setValue(this.overallProgressBar.getValue() + 1);
				this.latch.countDown();
				return FileVisitResult.CONTINUE;
			} else {

				if (updateEdalObject && this.currentDirectory.exist(file.getFileName().toString())) {

					clientPrimaryDataFile = (ClientPrimaryDataFile) this.currentDirectory
							.getPrimaryDataEntity(file.getFileName().toString());

				} else {
					clientPrimaryDataFile = this.currentDirectory
							.createPrimaryDataFile((file.getFileName().toString()));
				}

				FileStoreSwingWorker worker = new FileStoreSwingWorker(this.fileProgressBar, this.overallProgressBar,
						file, clientPrimaryDataFile, this.latch);

				worker.execute();
			}

		} catch (AccessControlException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		try {
			this.currentDirectory = this.currentDirectory.getParentDirectory();
		} catch (AccessControlException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}

		/* set the overall bar +1 after stored a directory */

		this.overallProgressBar.setValue(this.overallProgressBar.getValue() + 1);
		this.latch.countDown();

		return FileVisitResult.CONTINUE;
	}

	private void setMetaData(ClientPrimaryDataEntity entity) {

		try {
			MetaData m = entity.getMetaData().clone();

			m.setElementValue(EnumDublinCoreElements.CREATOR,
					this.metaData.getElementValue(EnumDublinCoreElements.CREATOR));
			m.setElementValue(EnumDublinCoreElements.CONTRIBUTOR,
					this.metaData.getElementValue(EnumDublinCoreElements.CONTRIBUTOR));
			m.setElementValue(EnumDublinCoreElements.COVERAGE,
					this.metaData.getElementValue(EnumDublinCoreElements.COVERAGE));
			m.setElementValue(EnumDublinCoreElements.SUBJECT,
					this.metaData.getElementValue(EnumDublinCoreElements.SUBJECT));
			m.setElementValue(EnumDublinCoreElements.LANGUAGE,
					this.metaData.getElementValue(EnumDublinCoreElements.LANGUAGE));
			m.setElementValue(EnumDublinCoreElements.DESCRIPTION,
					this.metaData.getElementValue(EnumDublinCoreElements.DESCRIPTION));
			m.setElementValue(EnumDublinCoreElements.PUBLISHER,
					this.metaData.getElementValue(EnumDublinCoreElements.PUBLISHER));
			m.setElementValue(EnumDublinCoreElements.RIGHTS,
					this.metaData.getElementValue(EnumDublinCoreElements.RIGHTS));
			m.setElementValue(EnumDublinCoreElements.SOURCE,
					this.metaData.getElementValue(EnumDublinCoreElements.SOURCE));

			entity.setMetaData(m);

		} catch (RemoteException | CloneNotSupportedException | MetaDataException
				| PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		}

	}
}