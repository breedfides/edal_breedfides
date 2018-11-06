/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalHttpHandler;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThreadPoolExcecutor;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;

/**
 * Implementaion of the {@link ServiceProvider} interface
 * 
 * @author arendd
 *
 */

public class ServiceProviderImplementation implements ServiceProvider {

	public static boolean cleaned = false;

	private static final int DaysAfterCleanBrokenEntities = -14;

	public static final Path PATH_FOR_DIRECTORY_SIZE_MAP = Paths
			.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "directory_size_map.dat");

	public static final Path PATH_FOR_DIRECTORY_FILE_MAP = Paths
			.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "directory_file_map.dat");

	public static final Path PATH_FOR_TOTAL_FILE_NUMBER = Paths
			.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "number_of_files.dat");

	public static final Path PATH_FOR_TOTAL_VOLUME = Paths
			.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "total_volume.dat");

	public static final Path PATH_FOR_REFERENCE_CONTENT = Paths
			.get(DataManager.getImplProv().getConfiguration().getMountPath().toString(), "reference_content.dat");

	public static Long totalNumberOfFiles;
	public static Long numberOfReferenceFiles;
	public static Long numberOfReferenceDirectories;
	public static Long volumeOfReference;

	private static final int MIN_NUMBER_OF_THREADS_IN_POOL = 2;
	private static final int MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE = 30;
	private static final int EXCUTOR_THREAD_KEEP_ALIVE_SECONDS = 60;
	private static ThreadPoolExecutor executor;

	static {

		if ((Runtime.getRuntime().availableProcessors() / 2) > MIN_NUMBER_OF_THREADS_IN_POOL) {
			executor = new EdalThreadPoolExcecutor(Runtime.getRuntime().availableProcessors() / 2,
					Runtime.getRuntime().availableProcessors() / 2, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE));
		} else {
			executor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL, MIN_NUMBER_OF_THREADS_IN_POOL,
					EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE));
		}

	}

	/** {@inheritDoc} */
	@Override
	public Long getUsedStorageSpace() throws EdalException {

		if (DataManager.getImplProv().getConfiguration().getDataPath() == null || Files
				.notExists(DataManager.getImplProv().getConfiguration().getDataPath(), LinkOption.NOFOLLOW_LINKS)) {
			throw new EdalException("No mount path defined, please run getRootDirectory first");
		}
		try {
			final AtomicLong size = new AtomicLong();
			Files.walkFileTree(DataManager.getImplProv().getConfiguration().getDataPath(),
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
								throws IOException {
							size.addAndGet(attrs.size());
							return FileVisitResult.CONTINUE;
						}
					});
			return size.longValue();
		} catch (final IOException e) {
			throw new EdalException("Unable to request used space", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Long getAvailableStorageSpace() throws EdalException {

		if (DataManager.getImplProv().getConfiguration().getDataPath() == null || Files
				.notExists(DataManager.getImplProv().getConfiguration().getDataPath(), LinkOption.NOFOLLOW_LINKS)) {
			throw new EdalException("No mount path defined, please run getRootDirectory first");
		}
		try {
			return Files.getFileStore(DataManager.getImplProv().getConfiguration().getDataPath()).getUsableSpace();
		} catch (final IOException e) {
			throw new EdalException("Unable to request available space", e);
		}
	}

	@Override
	public synchronized void cleanUpForRejectedEntities() {

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("CLEAN UP initiated");

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PublicReferenceImplementation> referenceCriteria = builder
				.createQuery(PublicReferenceImplementation.class);
		Root<PublicReferenceImplementation> referenceRoot = referenceCriteria.from(PublicReferenceImplementation.class);
		referenceCriteria.where(builder.equal(referenceRoot.get("publicationStatus"), PublicationStatus.REJECTED));

		List<PublicReferenceImplementation> references = session.createQuery(referenceCriteria).list();

		session.getTransaction().commit();
		session.close();

		boolean foundReferenceToDelete = false;

		if (references != null) {
			foundReferenceToDelete = true;
		}

		for (PublicReferenceImplementation reference : references) {

			PrimaryDataEntity entity = reference.getVersion().getEntity();

			if (entity.getVersions().size() == 3 && !entity.isDirectory()) {
				deleteFileAndPermissions((PrimaryDataFileImplementation) entity);
			}

			else if (entity.getVersions().size() == 3 && entity.isDirectory()) {
				deleteRecursiveDirectory((PrimaryDataDirectoryImplementation) entity);
			}
		}

		if (foundReferenceToDelete) {
			this.setCleaned(true);
		}

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("CLEAN UP finished");

	}

	private void deleteRecursiveDirectory(PrimaryDataDirectoryImplementation directory) {

		try {

			Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session.beginTransaction();
			deleteFilesRecursively(session, directory);
			session.getTransaction().commit();
			session.close();

			Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session2.beginTransaction();
			deleteDirectoriesRecursively(session2, directory);
			session2.getTransaction().commit();
			session2.close();
		} catch (PrimaryDataDirectoryException e) {
			e.printStackTrace();
		}

		deleteDirectoryAndPermissions(directory);

	}

	public void deleteDirectoriesRecursively(Session session, final PrimaryDataDirectory currentDirectory)
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

	public void deleteFilesRecursively(Session session, final PrimaryDataDirectory currentDirectory)
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

	private void deleteFileAndPermissions(PrimaryDataFileImplementation file) {

		DataManager.getImplProv().getLogger().info("Deleting concrete date file '" + file.getName() + "'");

		List<Integer> versionIds = new ArrayList<Integer>();

		for (PrimaryDataEntityVersion version : file.getVersions()) {

			versionIds.add(((PrimaryDataEntityVersionImplementation) version).getId());

			Path path = file.getPathToLocalFile(version);

			Thread tread = new DeleteThread(path);

			if (executor.isShutdown()) {
				if ((Runtime.getRuntime().availableProcessors() / 2) > MIN_NUMBER_OF_THREADS_IN_POOL) {
					executor = new EdalThreadPoolExcecutor(Runtime.getRuntime().availableProcessors() / 2,
							Runtime.getRuntime().availableProcessors() / 2, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS,
							TimeUnit.SECONDS,
							new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE));
				} else {
					executor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL, MIN_NUMBER_OF_THREADS_IN_POOL,
							EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
							new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE));
				}
			}

			executor.execute(tread);

		}

		DataManager.getImplProv().getLogger().info("Deleting PrimaryDataFile '" + file.getName() + "'");

		Session session1 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session1.beginTransaction();

		CriteriaBuilder builder = session1.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataEntityVersionImplementation> versionCriteria = builder
				.createQuery(PrimaryDataEntityVersionImplementation.class);
		Root<PrimaryDataEntityVersionImplementation> versionRoot = versionCriteria
				.from(PrimaryDataEntityVersionImplementation.class);
		versionCriteria.where(builder.equal(versionRoot.get("primaryEntityId"), file.getID()));

		List<PrimaryDataEntityVersionImplementation> allExistingVersionsInDB = session1.createQuery(versionCriteria)
				.list();

		session1.getTransaction().commit();
		session1.close();

		for (PrimaryDataEntityVersionImplementation version : allExistingVersionsInDB) {

			if (!versionIds.contains(version.getId())) {

				System.out.println("Needless version found " + version.getId());

				Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
				session2.beginTransaction();
				session2.delete(version);
				session2.getTransaction().commit();
				session2.close();

			}
		}

		try {

			Session session3 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session3.beginTransaction();

			CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder
					.createQuery(EdalPermissionImplementation.class);
			Root<EdalPermissionImplementation> permissionRoot = permissionCriteria
					.from(EdalPermissionImplementation.class);
			permissionCriteria.where(builder.equal(permissionRoot.get("internId"), file.getID()));

			List<EdalPermissionImplementation> permissions = session3.createQuery(permissionCriteria).list();

			for (EdalPermissionImplementation permission : permissions) {
				session3.delete(permission);
			}

			session3.getTransaction().commit();
			session3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Session session4 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session4.beginTransaction();
			session4.delete(file);
			session4.getTransaction().commit();
			session4.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteDirectoryAndPermissions(PrimaryDataDirectoryImplementation directory) {

		DataManager.getImplProv().getLogger().info("Deleting PrimaryDataDirectory " + directory.getName());

		List<Integer> versionIds = new ArrayList<Integer>();

		for (PrimaryDataEntityVersion version : directory.getVersions()) {
			versionIds.add(((PrimaryDataEntityVersionImplementation) version).getId());
		}

		Session session1 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session1.beginTransaction();

		CriteriaBuilder builder = session1.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataEntityVersionImplementation> versionCriteria = builder
				.createQuery(PrimaryDataEntityVersionImplementation.class);
		Root<PrimaryDataEntityVersionImplementation> principalRoot = versionCriteria
				.from(PrimaryDataEntityVersionImplementation.class);
		versionCriteria.where(builder.equal(principalRoot.get("primaryEntityId"), directory.getID()));

		List<PrimaryDataEntityVersionImplementation> allExistingVersionsInDB = session1.createQuery(versionCriteria)
				.list();

		session1.getTransaction().commit();
		session1.close();

		for (PrimaryDataEntityVersionImplementation version : allExistingVersionsInDB) {

			if (!versionIds.contains(version.getId())) {

				System.out.println("Needless version found " + version.getId());

				Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
				session2.beginTransaction();
				session2.delete(version);
				session2.getTransaction().commit();
				session2.close();

			}
		}

		try {
			Session session3 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session3.beginTransaction();

			CriteriaQuery<EdalPermissionImplementation> permissionCriteria = builder
					.createQuery(EdalPermissionImplementation.class);
			Root<EdalPermissionImplementation> permissionRoot = permissionCriteria
					.from(EdalPermissionImplementation.class);
			permissionCriteria.where(builder.equal(permissionRoot.get("internId"), directory.getID()));

			List<EdalPermissionImplementation> permissions = session3.createQuery(permissionCriteria).list();

			for (EdalPermissionImplementation permission : permissions) {
				session3.delete(permission);
			}
			session3.getTransaction().commit();
			session3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Session session4 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			session4.beginTransaction();
			session4.delete(directory);
			session4.getTransaction().commit();
			session4.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Long listDirectory(final PublicReference reference, PrimaryDataDirectory currentDirectory)
			throws PrimaryDataDirectoryException, MetaDataException {

		if (CalculateDirectorySizeThread.directorySizes
				.containsKey(reference.getInternalID() + "/" + currentDirectory.getID())) {
			return CalculateDirectorySizeThread.directorySizes
					.get(reference.getInternalID() + "/" + currentDirectory.getID());
		}

		final List<PrimaryDataEntity> list = currentDirectory.listPrimaryDataEntities();
		Long volumeOfCurrentDirectory = new Long(0);
		Long numberOfFilesInCurrentDirectory = new Long(0);
		Long numberOfDirectoriesInCurrentDirectory = new Long(0);
		if (list != null) {

			for (final PrimaryDataEntity primaryDataEntity : list) {

				DataSize mySize = primaryDataEntity.getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
				volumeOfCurrentDirectory = volumeOfCurrentDirectory + mySize.getFileSize();
				volumeOfReference = volumeOfReference + mySize.getFileSize();
				if (primaryDataEntity.isDirectory()) {
					volumeOfCurrentDirectory = volumeOfCurrentDirectory
							+ listDirectory(reference, (PrimaryDataDirectory) primaryDataEntity);
					numberOfReferenceDirectories += 1;
					numberOfDirectoriesInCurrentDirectory += 1;
				} else {
					if (reference.getPublicationStatus().equals(PublicationStatus.ACCEPTED)) {
						numberOfReferenceFiles += 1;
						totalNumberOfFiles += 1;
						numberOfFilesInCurrentDirectory += 1;
					}
				}
			}
			CalculateDirectorySizeThread.directorySizes.put(reference.getInternalID() + "/" + currentDirectory.getID(),
					volumeOfCurrentDirectory);
			CalculateDirectorySizeThread.directoryFiles.put(reference.getInternalID() + "/" + currentDirectory.getID(),
					numberOfDirectoriesInCurrentDirectory.toString() + ","
							+ numberOfFilesInCurrentDirectory.toString());
		}
		return volumeOfCurrentDirectory;
	}

	private void storeValuesToDisk() {

		File file = ServiceProviderImplementation.PATH_FOR_DIRECTORY_SIZE_MAP.toFile();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(CalculateDirectorySizeThread.directorySizes);
			oos.close();
		} catch (Exception e) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
		}

		file = ServiceProviderImplementation.PATH_FOR_DIRECTORY_FILE_MAP.toFile();

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(CalculateDirectorySizeThread.directoryFiles);
			oos.close();
		} catch (Exception e) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
		}

		file = ServiceProviderImplementation.PATH_FOR_TOTAL_FILE_NUMBER.toFile();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(ServiceProviderImplementation.totalNumberOfFiles);
			oos.close();
		} catch (Exception e) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
		}

		file = ServiceProviderImplementation.PATH_FOR_TOTAL_VOLUME.toFile();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(CalculateDirectorySizeThread.totalVolumeDataStock);
			oos.close();
		} catch (Exception e) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
		}

		file = ServiceProviderImplementation.PATH_FOR_REFERENCE_CONTENT.toFile();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(CalculateDirectorySizeThread.referenceContent);
			oos.close();
		} catch (Exception e) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().error(e);
		}

	}

	@Override
	public synchronized void calculateDirectorySizes() {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PublicReferenceImplementation> referenceCriteria = builder
				.createQuery(PublicReferenceImplementation.class);
		Root<PublicReferenceImplementation> permissionRoot = referenceCriteria
				.from(PublicReferenceImplementation.class);
		referenceCriteria.select(permissionRoot);

		List<PublicReferenceImplementation> references = session.createQuery(referenceCriteria).list();

		session.close();

		boolean updated = false;

		for (PublicReferenceImplementation reference : references) {

			numberOfReferenceFiles = new Long(0);
			numberOfReferenceDirectories = new Long(0);
			volumeOfReference = new Long(0);

			if (!CalculateDirectorySizeThread.directorySizes
					.containsKey(reference.getInternalID() + "/" + reference.getVersion().getEntity().getID())) {

				try {
					Long size = listDirectory(reference, ((PrimaryDataDirectory) reference.getVersion().getEntity()));

					if (reference.getPublicationStatus().equals(PublicationStatus.ACCEPTED)) {
						CalculateDirectorySizeThread.totalVolumeDataStock += size;
					}

					CalculateDirectorySizeThread.referenceContent.put(reference.getInternalID(),
							numberOfReferenceDirectories + "," + numberOfReferenceFiles + "," + volumeOfReference);

					storeValuesToDisk();
					updated = true;
				} catch (PrimaryDataDirectoryException | MetaDataException e) {
					e.printStackTrace();
				}
			}
		}

		if (updated) {
			DataManager.getImplProv().getLogger().info("Cleaning Webpage_Cache...");
			EdalHttpHandler.contentPageCache.clean();
			DataManager.getImplProv().getLogger().info("Webpage_Cache cleaned");
		}
	}

	@Override
	public int getNumberOfUsers() {
		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.select(principalRoot);

		List<PrincipalImplementation> numberOfUsers = session.createQuery(principalCriteria).list();

		session.getTransaction().commit();
		session.close();

		return numberOfUsers.size();

	}

	@Override
	public void cleanUpForBrokenEntities(PrimaryDataDirectory root) {

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrincipalImplementation> principalCriteria = builder.createQuery(PrincipalImplementation.class);
		Root<PrincipalImplementation> principalRoot = principalCriteria.from(PrincipalImplementation.class);
		principalCriteria.select(principalRoot);

		List<PrincipalImplementation> users = session.createQuery(principalCriteria).list();

		session.getTransaction().commit();
		session.close();

		for (PrincipalImplementation principal : users) {
			String user = principal.getName();
			try {
				for (PrimaryDataEntity entity : root.listPrimaryDataEntities()) {

					if (entity.getName().equals(user)) {
						/* check if a directory with the user name exist */
						for (PrimaryDataEntity ent : ((PrimaryDataDirectory) entity).listPrimaryDataEntities()) {

							if (ent.isDirectory() && ent.getPublicReferences().size() == 0) {
								/*
								 * check if a PublicReference for the Entity exist, if not: upload failed
								 */

								Calendar today = Calendar.getInstance();
								today.add(Calendar.DAY_OF_YEAR, DaysAfterCleanBrokenEntities);
								Calendar c = ent.getCurrentVersion().getCreationDate();

								if (c.before(today)) {
									/* older -> CLEAN */
									((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
											.info("CLEAN UP for broken Entities initiated");
									deleteRecursiveDirectory((PrimaryDataDirectoryImplementation) ent);
									((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
											.info("CLEAN UP for broken Entities finished");

									this.setCleaned(true);
								} else {
									/* younger -> NO CLEAN */
								}

							}
						}
					}

				}
			} catch (PrimaryDataDirectoryException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isCleaned() {
		return ServiceProviderImplementation.cleaned;
	}

	@Override
	public void setCleaned(boolean flag) {
		ServiceProviderImplementation.cleaned = flag;
	}

	class DeleteThread extends Thread {

		private Path path;

		public DeleteThread(Path path) {
			this.path = path;
		}

		@Override
		public void run() {
			try {
				Files.deleteIfExists(this.path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
