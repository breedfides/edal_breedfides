/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.concurrent.CountDownLatch;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;

class GuessMimeTypeThread extends EdalThread {

	private PipedInputStream pipedInputStream;

	private DataFormat dataFormat;

	private CountDownLatch stopLatch;

	GuessMimeTypeThread(PipedInputStream pipedInputStream,
			CountDownLatch stopLatch) {
		super();
		this.pipedInputStream = pipedInputStream;
		this.stopLatch = stopLatch;

	}

	@Override
	public void run() {
		this.setDataFormat(DataFormat.guessDataFormat(pipedInputStream));
		try {
			this.pipedInputStream.close();
		} catch (IOException e) {
			DataManager
					.getImplProv()
					.getLogger()
					.error("unable to close pipedInputStream after guessing data format: "
							+ e.getMessage());
			throw new RuntimeException(e);
		}
		this.stopLatch.countDown();
	}

	/**
	 * @return the dataFormat
	 */
	DataFormat getDataFormat() {
		return dataFormat;
	}

	/**
	 * @param dataFormat
	 *            the dataFormat to set
	 */
	private void setDataFormat(DataFormat dataFormat) {
		this.dataFormat = dataFormat;
	}
}
