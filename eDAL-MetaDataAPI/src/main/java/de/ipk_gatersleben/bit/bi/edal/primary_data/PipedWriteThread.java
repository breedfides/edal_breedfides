/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.io.EofException;

/**
 * Thread to write the {@link OutputStream} of a
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile} to
 * the {@link OutputStream} of the HTTP response with the help of a piped
 * stream.
 * 
 * @author arendd
 */
public class PipedWriteThread extends Thread {

	private PipedInputStream httpinstream = null;
	private OutputStream responseBody = null;
	private CountDownLatch countDownLatch = null;
	private long contentLength = 0;

	/**
	 * Create a new Thread and set the corresponding {@link PipedInputStream} and
	 * {@link OutputStream}.
	 * 
	 * @param httpinstream
	 *            the {@link PipedInputStream} to write the data.
	 * @param responseBody
	 *            the {@link OutputStream} to read the data.
	 * @param countDownLatch
	 */
	PipedWriteThread(final PipedInputStream httpinstream, final OutputStream responseBody,
			CountDownLatch countDownLatch, long contentLength) {
		this.httpinstream = httpinstream;
		this.responseBody = responseBody;
		this.countDownLatch = countDownLatch;
		this.contentLength = contentLength;
	}

	/**
	 * Start the {@link PipedWriteThread} and transfer the data form the
	 * {@link PipedInputStream} to the {@link OutputStream}.
	 */
	@Override
	public void run() {

		DataManager.getImplProv().getLogger()
				.debug("COUNTDOWNLATCH PipedThread before: " + this.countDownLatch.getCount());

		try {
			int readbytes = 0;
			long content = 0;
			final byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];
			boolean finish = false;

			while (!finish) {

				readbytes = this.httpinstream.read(buffer);

				DataManager.getImplProv().getLogger().debug("read from piped stream: " + readbytes);
				this.responseBody.write(buffer, 0, readbytes);
				DataManager.getImplProv().getLogger().debug("read finish");

				content += readbytes;
				if (content == this.contentLength) {
					finish = true;
				}
			}
		} catch (final EofException e) {
			DataManager.getImplProv().getLogger().warn("HTTP Request canceled by user!");
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().warn("Unable to write piped streams: " + e.getMessage());
		} finally {
			this.countDownLatch.countDown();
			DataManager.getImplProv().getLogger()
					.debug("COUNTDOWNLATCH PipedThread after: " + this.countDownLatch.getCount());
		}
	}
}