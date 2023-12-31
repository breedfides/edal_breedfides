/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessControlException;
import java.util.SortedSet;
import java.util.TreeSet;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;

/**
 * Implementation of {@link FileVisitor} to walk a zip file tree and store the
 * object into eDAL.
 * 
 * @author arendd
 */
public class EdalZipFileVisitorRmi implements FileVisitor<Path> {

	private ClientPrimaryDataDirectory currentDirectory = null;
	private SortedSet<String> pathSet = new TreeSet<>();
	private StringBuffer pathString = new StringBuffer("");

	public EdalZipFileVisitorRmi(ClientPrimaryDataDirectory currentDirectory) {
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
				ClientPrimaryDataDirectory newDirectory = currentDirectory.createPrimaryDataDirectory(directoryName);
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

		try {
			currentDirectory.createPrimaryDataFile(file.getFileName().toString());
		} catch (AccessControlException | PrimaryDataDirectoryException e) {
			e.printStackTrace();
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
			} catch (AccessControlException | PrimaryDataDirectoryException e) {
				e.printStackTrace();
			}

		}
		return FileVisitResult.CONTINUE;
	}

	public StringBuffer getInput() {
		return pathString;
	}

	public SortedSet<String> getPathSet() {
		return pathSet;
	}

}
