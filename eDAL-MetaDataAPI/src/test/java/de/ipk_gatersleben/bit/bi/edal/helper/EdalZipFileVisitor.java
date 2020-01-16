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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedSet;
import java.util.TreeSet;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;

/**
 * Implementation of {@link FileVisitor} to walk a zip file tree and store the
 * object into eDAL.
 * 
 * @author arendd
 */
public class EdalZipFileVisitor implements FileVisitor<Path> {

	private PrimaryDataDirectory currentDirectory = null;
	private SortedSet<String> pathSet = new TreeSet<>();

	public EdalZipFileVisitor(PrimaryDataDirectory currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

		if (!dir.toString().equals("/")) {

			String directoryName = dir.getFileName().toString();

			if (directoryName.contains("/")) {
				directoryName = directoryName.substring(0, directoryName.indexOf("/"));
			}

//			System.out.println(directoryName);
			try {
				PrimaryDataDirectory newDirectory = currentDirectory.createPrimaryDataDirectory(directoryName);
				this.currentDirectory = newDirectory;
			} catch (PrimaryDataDirectoryException e) {
				throw new IOException(e);
			}

			StringBuffer tmpBuffer = new StringBuffer("/");

			for (int i = 0; i < dir.getNameCount(); i++) {
//				System.out.println(dir.getName(i));
				if (dir.getName(i).endsWith("/")) {
				} else {
					tmpBuffer.append(dir.getName(i) + "/");
				}
			}
			/* cut last "/" or "//" -symbols */
			String add;

			if (tmpBuffer.toString().endsWith("//")) {
				add = "/" + tmpBuffer.substring(0, tmpBuffer.lastIndexOf("//"));
			} else if (tmpBuffer.toString().endsWith("/")) {
				add = "/" + tmpBuffer.substring(0, tmpBuffer.lastIndexOf("/"));
			} else {
				add = "/" + tmpBuffer;
			}
			pathSet.add(add);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

//		System.out.println(file.getFileName().toString());

		try {
			currentDirectory.createPrimaryDataFile(file.getFileName().toString());
		} catch (PrimaryDataDirectoryException e) {
			throw new IOException(e);
		}

		StringBuffer tmpBuffer = new StringBuffer("/");

		for (int i = 0; i < file.getNameCount(); i++) {
			tmpBuffer.append(file.getName(i) + "/");
		}

		/* cut last "/"-symbol */
		pathSet.add("/" + tmpBuffer.toString().substring(0, tmpBuffer.toString().length() - 1));

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (!dir.toString().equals("/")) {
			try {
				currentDirectory = currentDirectory.getParentDirectory();
			} catch (PrimaryDataDirectoryException e) {
				throw new IOException(e);
			}
		}
		return FileVisitResult.CONTINUE;
	}

	public SortedSet<String> getPathSet() {
		return pathSet;
	}
}