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
