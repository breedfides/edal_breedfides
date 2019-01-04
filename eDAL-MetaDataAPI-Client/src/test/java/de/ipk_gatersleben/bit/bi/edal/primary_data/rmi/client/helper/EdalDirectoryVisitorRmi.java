/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessControlException;
import java.util.SortedSet;
import java.util.TreeSet;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

/**
 * Implementation of {@link FileVisitor} to walk a file system tree and store
 * the object into eDAL.
 * 
 * @author arendd
 */
public class EdalDirectoryVisitorRmi implements FileVisitor<Path> {

	private SortedSet<String> pathSet = new TreeSet<String>();
	private ClientPrimaryDataDirectory currentDirectory = null;
	private StringBuffer pathString = new StringBuffer("");
	private int numberOfRootElements = 0;
	private boolean store = false;
	private String indent = "";

	public EdalDirectoryVisitorRmi(ClientPrimaryDataDirectory currentDirectory,
			Path path, boolean store) {
		this.currentDirectory = currentDirectory;
		this.numberOfRootElements = path.getNameCount() - 1;
		this.store = store;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {

		pathString.append(indent + dir.getFileName() + "\n");
		indent += "  ";
		try {
			ClientPrimaryDataDirectory newDir = currentDirectory
					.createPrimaryDataDirectory(dir.getFileName().toString());
			this.currentDirectory = newDir;
		} catch (AccessControlException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
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

		pathString.append(indent + file.getFileName() + "\n");

		try {
			ClientPrimaryDataFile pdf = currentDirectory.createPrimaryDataFile(file
					.getFileName().toString());
			if (store) {
				pdf.store(new FileInputStream(file.toFile()));
			}

		} catch (AccessControlException | PrimaryDataDirectoryException
				| PrimaryDataFileException | PrimaryDataEntityVersionException e) {
			e.printStackTrace();
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
		} catch (AccessControlException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}

		indent = indent.substring(2);
		return FileVisitResult.CONTINUE;
	}

	public StringBuffer getInput() {
		return pathString;
	}

	public SortedSet<String> getPathSet() {
		return pathSet;
	}

}
