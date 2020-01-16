/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
