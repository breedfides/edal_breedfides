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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThreadPoolExcecutor;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * <p>
 * Abstract PrimaryDataFile class.
 * </p>
 * 
 * @author lange
 * @author arendd
 */
public abstract class PrimaryDataFile extends PrimaryDataEntity {

	private static final int MIN_NUMBER_OF_THREADS_IN_POOL = 4;
//	private static final int MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE = 1200;
	private static final int EXCUTOR_THREAD_KEEP_ALIVE_SECONDS = 120;
	private static final ConcurrentLinkedQueue<Runnable> NON_BLOCKING_QUEUE = new ConcurrentLinkedQueue<>();
	private static ThreadPoolExecutor executor;
	private static final String EXECUTOR_NAME = "PrimaryDataFileExecutor";

	private static final int USEABLE_CORES = (int) Math.ceil(Runtime.getRuntime().availableProcessors() * 3 / 4);

	static {
		
		if (MIN_NUMBER_OF_THREADS_IN_POOL < USEABLE_CORES) {
			executor = new EdalThreadPoolExcecutor(USEABLE_CORES, USEABLE_CORES, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS,
					TimeUnit.SECONDS, new LinkedBlockingQueue<>(NON_BLOCKING_QUEUE),
					EXECUTOR_NAME);
		} else {

			executor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL, MIN_NUMBER_OF_THREADS_IN_POOL,
					EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(NON_BLOCKING_QUEUE), EXECUTOR_NAME);
		}
	}

	/**
	 * Default constructor create a {@link PrimaryDataFile}
	 */
	protected PrimaryDataFile() {
		super();
	}

	/**
	 * 
	 * Construct a {@link PrimaryDataFile} and set the file version to the latest
	 * one
	 * 
	 * @param path the directory of the file
	 * @param name the file name
	 * @throws PrimaryDataFileException          if unable to set data type.
	 * @throws PrimaryDataEntityVersionException if unable to store initial version.
	 * @throws PrimaryDataDirectoryException     if no parent
	 *                                           {@link PrimaryDataDirectory} is
	 *                                           found.
	 * @throws MetaDataException                 if the {@link MetaData} object of
	 *                                           the parent
	 *                                           {@link PrimaryDataDirectory} is not
	 *                                           clone-able.
	 */
	public PrimaryDataFile(final PrimaryDataDirectory path, final String name) throws PrimaryDataFileException,
			PrimaryDataEntityVersionException, PrimaryDataDirectoryException, MetaDataException {
		super(path, name);

		try {
			this.getMetaData().setElementValue(EnumDublinCoreElements.TYPE, new DataType(EnumDCMIDataType.TEXT));
		} catch (final MetaDataException e) {
			throw new PrimaryDataFileException(e.getMessage());
		}

		final InputStream dataInputStream = new ByteArrayInputStream(new byte[] {});
		this.store(dataInputStream);
	}

	/**
	 * Abstract function to check if the current {@link PrimaryDataEntityVersion} of
	 * this {@link PrimaryDataFile} has stored data.
	 * 
	 * @return <code>true</code> if the current {@link PrimaryDataEntityVersion} has
	 *         stored data;<code>false</code> otherwise.
	 */

	protected abstract boolean existData();

	/**
	 * Load the data of the latest {@link PrimaryDataEntityVersion} of this
	 * {@link PrimaryDataFile} as stream.
	 * 
	 * @param dataOutputStream the loaded data.
	 * @throws PrimaryDataFileException if no data is stored.
	 */
	public void read(final OutputStream dataOutputStream) throws PrimaryDataFileException {

		final WriteLock writelock = this.getCurrentVersion().getReadWriteLock().writeLock();

		if (writelock.tryLock()) {

			final PrimaryDataEntityVersion oldVersion = this.getCurrentVersion();
			final SortedSet<PrimaryDataEntityVersion> versions = this.getVersions();

			/* generate Set with all versions until the current version */
			final SortedSet<PrimaryDataEntityVersion> headSet = versions.headSet(oldVersion);

			/* generate Array from Set to iterate reverse through the array */
			final PrimaryDataEntityVersion[] versionArray = new PrimaryDataEntityVersion[headSet.size()];

			int i = 0;
			for (final PrimaryDataEntityVersion primaryDataEntityVersion : headSet) {
				versionArray[i] = primaryDataEntityVersion;
				i++;
			}

			/* go backwards through the version list to find the last version */

			for (int p = versionArray.length - 1; p >= 0; p--) {

				if (!this.existData()) {
					try {
						this.switchCurrentVersion(versionArray[p]);
					} catch (final PrimaryDataEntityVersionException e) {
						throw new PrimaryDataFileException("no data stored" + e.getMessage());
					}
				} else {
					break;
				}
			}

			this.readImpl(dataOutputStream);

			try {
				this.switchCurrentVersion(oldVersion);
			} catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataFileException(e.getMessage());
			}
			writelock.unlock();
		}
	}

	/**
	 * Abstract function for implementation the
	 * {@link PrimaryDataFile#read(OutputStream)} function.
	 * 
	 * @param dataOutputStream the loaded data.
	 * @throws PrimaryDataFileException if no data is stored.
	 */
	protected abstract void readImpl(final OutputStream dataOutputStream) throws PrimaryDataFileException;

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em>Check before if the {@link EnumDublinCoreElements#TYPE} is not a
	 * {@link MetaData#DIRECTORY} object.</em>
	 */
	@Override
	public void setMetaData(final MetaData newMetadata) throws PrimaryDataEntityVersionException, MetaDataException {

		if (newMetadata.getElementValue(EnumDublinCoreElements.TYPE).toString().equals(MetaData.DIRECTORY.toString())) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.TYPE);

			throw new MetaDataException("valid value for meta data element in PrimaryDataFile: "
					+ EnumDublinCoreElements.TYPE.name() + " is not allowed ! Rollback to " + originalData);
		}
		if (newMetadata.getElementValue(EnumDublinCoreElements.FORMAT).toString().equals(MetaData.EMPTY.toString())) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.FORMAT);

			throw new MetaDataException(
					"valid value for meta data element in PrimaryDataFile: " + EnumDublinCoreElements.FORMAT.name()
							+ " is not allowed to be empty ! Rollback to " + originalData);
		}
		if (newMetadata.getElementValue(EnumDublinCoreElements.FORMAT).toString()
				.equals(MetaData.DIRECTORY.toString())) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.FORMAT);

			throw new MetaDataException(
					"valid value for meta data element in PrimaryDataFile: " + EnumDublinCoreElements.FORMAT.name()
							+ " is not allowed to be directory ! Rollback to " + originalData);
		}

		MetaData oldMetaData = null;
		try {
			oldMetaData = DataManager.getImplProv()
					.reloadPrimaryDataEntityByID(this.getID(), this.getCurrentVersion().getRevision()).getMetaData();
		} catch (EdalException e) {
			throw new PrimaryDataEntityVersionException("unable to reload meta data: " + e.getMessage(), e);
		}

		if (oldMetaData.getElementValue(EnumDublinCoreElements.SIZE)
				.compareTo(newMetadata.getElementValue(EnumDublinCoreElements.SIZE)) != 0) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.SIZE);

			throw new MetaDataException(
					"it is not allowed to overwrite the data size of a PrimaryDataFile ! Rollback to " + originalData);
		}
		if (!oldMetaData.getElementValue(EnumDublinCoreElements.CHECKSUM)
				.equals(newMetadata.getElementValue(EnumDublinCoreElements.CHECKSUM))) {

			UntypedData originalData = reloadOldDataType(EnumDublinCoreElements.CHECKSUM);

			throw new MetaDataException(
					"it is not allowed to overwrite the checksum of a PrimaryDataFile ! Rollback to " + originalData);
		}
		super.setMetaData(newMetadata);
	}

	/**
	 * Store data and generate a new {@link PrimaryDataEntityVersion} for this
	 * {@link PrimaryDataFile}.
	 * 
	 * @param dataInputStream the date to store in this
	 *                        {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataFileException          if storing of data fails.
	 * @throws PrimaryDataEntityVersionException if provided version conflicts with
	 *                                           existing versions.
	 */
	public void store(final InputStream dataInputStream)
			throws PrimaryDataFileException, PrimaryDataEntityVersionException {
		
		if (this.getCurrentVersion().isDeleted()) {
			throw new PrimaryDataEntityVersionException(
					"file already deleted at: " + this.getCurrentVersion().getRevisionDate());
		}

		PrimaryDataEntityVersion newFileVersion = null;
		try {
			newFileVersion = new PrimaryDataEntityVersion(this, false, this.getCurrentVersion().getMetaData().clone());
		} catch (CloneNotSupportedException e) {
			throw new PrimaryDataFileException("unable to clone: " + e.getMessage(), e);
		}

		final List<PipedInputStream> pipedInputStreams = new ArrayList<PipedInputStream>();
		// store function
		pipedInputStreams.add(new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE));
		// md5 checksum
		pipedInputStreams.add(new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE));
		// format guessing
		pipedInputStreams.add(new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE));
		// thread list of all consumer of the above pipes

		CountDownLatch countDownLatch = new CountDownLatch(pipedInputStreams.size());

		StoreFileThread storeFileThread = new StoreFileThread(this, pipedInputStreams.get(0), newFileVersion,
				countDownLatch);

		CalculateCheckSumSHAThread calculateCheckSumThread = new CalculateCheckSumSHAThread(pipedInputStreams.get(1),
				countDownLatch);

		GuessMimeTypeThread guessMimeTypeThread = new GuessMimeTypeThread(pipedInputStreams.get(2), countDownLatch);

		PipedInputOutputThread pipedInputOutputThread = null;
		try {
			pipedInputOutputThread = new PipedInputOutputThread(dataInputStream, pipedInputStreams);
		} catch (final IOException e) {
			throw new PrimaryDataFileException("exception while multiplexing input stream: " + e.getMessage(), e);
		}

		if (executor.isShutdown()) {
			if (MIN_NUMBER_OF_THREADS_IN_POOL < USEABLE_CORES) {
				executor = new EdalThreadPoolExcecutor(USEABLE_CORES, USEABLE_CORES, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS,
						TimeUnit.SECONDS, new LinkedBlockingQueue<>(NON_BLOCKING_QUEUE),
						EXECUTOR_NAME);
			} else {
				executor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL, MIN_NUMBER_OF_THREADS_IN_POOL,
						EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
						new LinkedBlockingQueue<>(NON_BLOCKING_QUEUE), EXECUTOR_NAME);
			}
		}
		executor.execute(pipedInputOutputThread);
		executor.execute(storeFileThread);
		executor.execute(calculateCheckSumThread);
		executor.execute(guessMimeTypeThread);

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			throw new PrimaryDataFileException("exception while waiting for close all data stream: " + e.getMessage(),
					e);
		}

		try {
			for (final PipedInputStream pipedInputStream : pipedInputStreams) {
				pipedInputStream.close();
			}
		} catch (final IOException e) {
			throw new PrimaryDataFileException("exception while closing multiplexed piped stream: " + e.getMessage(),
					e);
		}

		try {

			newFileVersion.getMetaData().setElementValue(EnumDublinCoreElements.SIZE,
					new DataSize(pipedInputOutputThread.getSize()));
			newFileVersion.getMetaData().setElementValue(EnumDublinCoreElements.CHECKSUM,
					calculateCheckSumThread.getChecksum());
			newFileVersion.getMetaData().setElementValue(EnumDublinCoreElements.FORMAT,
					guessMimeTypeThread.getDataFormat());
		} catch (final MetaDataException e) {
			throw new PrimaryDataFileException("exception while compute metadata from data stream: " + e.getMessage(),
					e);
		}

		this.commitVersion(newFileVersion);

	}

	/**
	 * Abstract function for implementation the
	 * {@link PrimaryDataFile#store(InputStream)} function.
	 * 
	 * @param dataInputStream the data to store in this
	 *                        {@link PrimaryDataEntityVersion}
	 * @param newFileVersion  the new {@link PrimaryDataEntityVersion} to store
	 * @throws PrimaryDataFileException if storing of data fails
	 */
	protected abstract void storeImpl(InputStream dataInputStream, PrimaryDataEntityVersion newFileVersion)
			throws PrimaryDataFileException;
}