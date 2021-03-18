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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.util.concurrent.CountDownLatch;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;

/**
 * {@link Thread} to store a {@link InputStream} into the file system.
 * 
 * @author arendd
 * 
 */
class StoreFileThread extends Thread {

	private PipedInputStream pipedInputStream;
	private PrimaryDataFile file;
	private PrimaryDataEntityVersion version;

	private CountDownLatch stopLatch;

	StoreFileThread(PrimaryDataFile file, PipedInputStream pipedInputStrem,
			PrimaryDataEntityVersion version, CountDownLatch stopLatch) {

		this.pipedInputStream = pipedInputStrem;
		this.file = file;
		this.version = version;
		this.stopLatch = stopLatch;

	}

	@Override
	public void run() {
		try {
			this.file.storeImpl(this.pipedInputStream, this.version);
		} catch (PrimaryDataFileException e) {
			DataManager
					.getImplProv()
					.getLogger()
					.error("unable to store file from data stream: "
							+ e.getMessage());
			throw new RuntimeException(e);
		}
		this.stopLatch.countDown();
	}

}
