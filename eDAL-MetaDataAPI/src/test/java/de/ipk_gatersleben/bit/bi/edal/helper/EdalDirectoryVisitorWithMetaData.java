/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;
import java.util.TreeSet;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;

/**
 * Implementation of {@link FileVisitor} to walk a file system tree and optional
 * store the object and metadata into eDAL.
 * 
 * @author arendd
 */
public class EdalDirectoryVisitorWithMetaData implements FileVisitor<Path> {

	private SortedSet<String> pathSet = new TreeSet<>();
	private PrimaryDataDirectory currentDirectory = null;
	private int numberOfRootElements = 0;
	private boolean store = false;
	private MetaData metaData = null;

	public EdalDirectoryVisitorWithMetaData(
			PrimaryDataDirectory currentDirectory, Path path,
			MetaData metaData, boolean store) {
		this.currentDirectory = currentDirectory;
		this.numberOfRootElements = path.getNameCount() - 1;
		this.store = store;
		this.metaData = metaData;

		if (this.metaData != null) {
			setMetaData(this.currentDirectory);
		}
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {

		try {
			PrimaryDataDirectory newCurrentDirectory = currentDirectory
					.createPrimaryDataDirectory(dir.getFileName().toString());

			this.currentDirectory = newCurrentDirectory;
		} catch (PrimaryDataDirectoryException e) {
			throw new IOException(e);
		}
		/* else rootDirectory */
		if (numberOfRootElements < dir.getNameCount()) {

			Path tmpPath = dir
					.subpath(numberOfRootElements, dir.getNameCount());

			StringBuffer tmpBuffer = new StringBuffer("//");

			for (int i = 0; i < tmpPath.getNameCount(); i++) {
				tmpBuffer.append(tmpPath.getName(i) + "/");
			}
			/* cut last "/"-symbol */
			pathSet.add(tmpBuffer.toString().substring(0,
					tmpBuffer.toString().length() - 1));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {

		try {
			PrimaryDataFile pdf = currentDirectory.createPrimaryDataFile(file
					.getFileName().toString());

			if (store) {
				pdf.store(new FileInputStream(file.toFile()));
			}

		} catch (PrimaryDataDirectoryException | PrimaryDataFileException
				| PrimaryDataEntityVersionException e) {
			throw new IOException(e);
		}
		Path tmpPath = file.subpath(numberOfRootElements, file.getNameCount());

		StringBuffer tmpBuffer = new StringBuffer("//");

		for (int i = 0; i < tmpPath.getNameCount(); i++) {
			tmpBuffer.append(tmpPath.getName(i) + "/");
		}

		/* cut last "/"-symbol */
		pathSet.add(tmpBuffer.toString().substring(0,
				tmpBuffer.toString().length() - 1));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		try {
			currentDirectory = currentDirectory.getParentDirectory();
		} catch (PrimaryDataDirectoryException e) {
			throw new IOException(e);
		}

		return FileVisitResult.CONTINUE;
	}

	public SortedSet<String> getPathSet() {
		return pathSet;
	}

	private void setMetaData(PrimaryDataEntity entity) {

		try {
			MetaData m = entity.getMetaData().clone();

			m.setElementValue(EnumDublinCoreElements.CREATOR, this.metaData
					.getElementValue(EnumDublinCoreElements.CREATOR));
			m.setElementValue(EnumDublinCoreElements.CONTRIBUTOR, this.metaData
					.getElementValue(EnumDublinCoreElements.CONTRIBUTOR));
			m.setElementValue(EnumDublinCoreElements.SUBJECT, this.metaData
					.getElementValue(EnumDublinCoreElements.SUBJECT));
			m.setElementValue(EnumDublinCoreElements.LANGUAGE, this.metaData
					.getElementValue(EnumDublinCoreElements.LANGUAGE));
			m.setElementValue(EnumDublinCoreElements.DESCRIPTION, this.metaData
					.getElementValue(EnumDublinCoreElements.DESCRIPTION));
			m.setElementValue(EnumDublinCoreElements.PUBLISHER, this.metaData
					.getElementValue(EnumDublinCoreElements.PUBLISHER));
			m.setElementValue(EnumDublinCoreElements.RIGHTS, this.metaData
					.getElementValue(EnumDublinCoreElements.RIGHTS));
			m.setElementValue(EnumDublinCoreElements.SOURCE, this.metaData
					.getElementValue(EnumDublinCoreElements.SOURCE));

			entity.setMetaData(m);

		} catch (CloneNotSupportedException | MetaDataException
				| PrimaryDataEntityVersionException e) {
			e.printStackTrace();
		}
	}

}