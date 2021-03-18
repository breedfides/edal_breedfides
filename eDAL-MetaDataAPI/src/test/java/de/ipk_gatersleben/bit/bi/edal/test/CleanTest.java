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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.util.List;

import javax.mail.internet.InternetAddress;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.EdalPermissionImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class CleanTest {

	private static void deleteRecursiveDirectory(PrimaryDataDirectoryImplementation directory) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		try {
			deleteFilesRecursively(session, directory);
			deleteDirectoriesRecursively(session, directory);
		} catch (PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}

		deleteDirectoryAndPermissions(directory);

		session.getTransaction().commit();
		session.close();

	}

	public static void deleteDirectoriesRecursively(Session session, final PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				if (primaryDataEntity.isDirectory()) {
					if (((PrimaryDataDirectory) primaryDataEntity).listPrimaryDataEntities().size() == 0) {
						deleteDirectoryAndPermissions((PrimaryDataDirectoryImplementation) primaryDataEntity);
					} else {
						deleteDirectoriesRecursively(session, (PrimaryDataDirectory) primaryDataEntity);
						deleteDirectoryAndPermissions((PrimaryDataDirectoryImplementation) primaryDataEntity);
					}
				}
			}
		}
	}

	public static void deleteFilesRecursively(Session session, final PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException {

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();

		if (list != null) {
			for (final PrimaryDataEntity primaryDataEntity : list) {
				if (primaryDataEntity.isDirectory()) {
					deleteFilesRecursively(session, (PrimaryDataDirectory) primaryDataEntity);
				} else {
					deleteFileAndPermissions((PrimaryDataFileImplementation) primaryDataEntity);

				}
			}
		}
	}

	private static void deleteFileAndPermissions(PrimaryDataFileImplementation file) {

		System.out.println("Deleting local File");

		// for (PrimaryDataEntityVersion version : file.getVersions()) {
		//
		// Path path = file.getPathToLocalFile(version);
		//
		// try {
		// Files.delete(path);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }

		System.out.println("Deleting PrimaryDataFile '" + file.getName() + "'");

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		session.delete(file);

		@SuppressWarnings("unchecked")
		List<EdalPermissionImplementation> permissions = (List<EdalPermissionImplementation>) session
				.createCriteria(EdalPermissionImplementation.class).add(Restrictions.eq("internId", file.getID()))
				.list();

		for (EdalPermissionImplementation permission : permissions) {
			session.delete(permission);
		}

		session.getTransaction().commit();
		session.close();

	}

	private static void deleteDirectoryAndPermissions(PrimaryDataDirectoryImplementation directory) {

		System.out.println("Deleting PrimaryDataDirectory '" + directory.getName() + "'");

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		session.delete(directory);

		@SuppressWarnings("unchecked")
		List<EdalPermissionImplementation> permissions = (List<EdalPermissionImplementation>) session
				.createCriteria(EdalPermissionImplementation.class).add(Restrictions.eq("internId", directory.getID()))
				.list();

		for (EdalPermissionImplementation permission : permissions) {
			session.delete(permission);
		}

		session.getTransaction().commit();
		session.close();
	}

	private static final String ROOT_USER = "eDAL0815@ipk-gatersleben.de";
	private static final String EMAIL = "user@nodomain.com.invalid";

	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";

	public static void main(final String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration(DATACITE_USERNAME, DATACITE_PASSWORD, DATACITE_PREFIX,
				new InternetAddress(EMAIL), new InternetAddress(EMAIL), new InternetAddress(EMAIL),
				new InternetAddress(ROOT_USER));
		configuration.setUseSSL(true);

		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		PrimaryDataDirectory directoryToDelete = (PrimaryDataDirectory) rootDirectory.getPrimaryDataEntity("***");

		System.out.println(directoryToDelete);

		deleteRecursiveDirectory((PrimaryDataDirectoryImplementation) rootDirectory);

		DataManager.shutdown();

	}

}
