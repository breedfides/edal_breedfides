/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;

/**
 * {@link Thread} for generating a ZIP file that contains all object within a
 * {@link PrimaryDataDirectory}. All file types in the
 * {@link ZipThread.FILE_TYPES_WITH_COMPRESSION_SUPPORT} list will be compressed
 * with compression level 1. All other file types will just stored (compression
 * level 0) in the ZIP file.
 * 
 * @author arendd
 *
 */
public class ZipThread extends EdalThread {

	/**
	 * List of all mime file type groups that are supported to get compressed to
	 * reduce zip file size.
	 * 
	 * @author arendd
	 * 
	 */
	private enum FILE_TYPES_WITH_COMPRESSION_SUPPORT {
		TEXT;
	}

	private static List<String> ENUM_LIST = null;

	static {
		ENUM_LIST = Stream.of(FILE_TYPES_WITH_COMPRESSION_SUPPORT.values()).map(Enum::name)
				.collect(Collectors.toList());
	}

	private ZipOutputStream zout = null;
	private PrimaryDataDirectory directory = null;
	private CountDownLatch countDownLatch = null;
	private List<PrimaryDataEntity> list = null;

	public ZipThread(CountDownLatch countDownLatch, ZipOutputStream zout, PrimaryDataDirectory directory) {
		super();
		this.countDownLatch = countDownLatch;
		this.zout = zout;
		this.directory = directory;
	}

	@Override
	public void run() {
		try {
			readPrimaryDataDirectoryIntoZipOutputStream(this.zout, this.directory, true);
		} catch (PrimaryDataFileException | PrimaryDataDirectoryException | MetaDataException | IOException e) {
			DataManager.getImplProv().getLogger()
					.warn("Generating Zip File for '" + this.directory.getName() + "' failed: " + e.getMessage());
		} finally {
			this.countDownLatch.countDown();
		}
	}

	private void readPrimaryDataDirectoryIntoZipOutputStream(ZipOutputStream zipOutputStream,
			PrimaryDataDirectory entity, boolean log)
			throws PrimaryDataDirectoryException, PrimaryDataFileException, MetaDataException, IOException {

		if (log) {
			DataManager.getImplProv().getLogger().info("Preparing Zip File for '" + entity.getName() + "'");
		}

		list = entity.listPrimaryDataEntities();

		DataManager.getImplProv().getLogger().debug("Adding directory " + entity.getPath());

		for (PrimaryDataEntity primaryDataEntity : list) {

			// if the file is directory, call the function recursively
			if (primaryDataEntity.isDirectory()) {
				readPrimaryDataDirectoryIntoZipOutputStream(zipOutputStream, (PrimaryDataDirectory) primaryDataEntity,
						false);
				continue;
			}

			DataManager.getImplProv().getLogger().debug("Adding file " + primaryDataEntity.getPath());

			ZipEntry zipEntry = new ZipEntry(primaryDataEntity.getPath().substring(2));

			DateEvents dateEvents = primaryDataEntity.getMetaData().getElementValue(EnumDublinCoreElements.DATE);

			Set<EdalDate> set = dateEvents.getSet();

			long time = 0;

			for (EdalDate edalDate : set) {
				if (edalDate.getEvent().equals(EdalDate.STANDART_EVENT_TYPES.CREATED.toString())) {
					time = edalDate.getStartDate().getTimeInMillis();
					break;
				}
			}

			zipEntry.setTime(time);

			String mimetype = ((DataFormat) primaryDataEntity.getCurrentVersion().getMetaData()
					.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType();

			String mimeTypeGroup = mimetype.substring(0, mimetype.indexOf("/")).toUpperCase();

			zipOutputStream.setMethod(ZipEntry.DEFLATED);

			if (ENUM_LIST.contains(mimeTypeGroup)) {
				zipOutputStream.setLevel(1);
			} else {
				zipOutputStream.setLevel(0);
			}

			zipOutputStream.putNextEntry(zipEntry);

			((PrimaryDataFile) primaryDataEntity).read(zipOutputStream);

			zipOutputStream.flush();
			zipOutputStream.closeEntry();

		}
		if (log) {
			DataManager.getImplProv().getLogger().info("ZipOutputStream for '" + entity.getName() + "' finished");
		}

	}

	public void stopListThread() {

		if (this.list instanceof AsynchronList<?>) {
			((AsynchronList<PrimaryDataEntity>) this.list).setStopped();
		}
	}
}
