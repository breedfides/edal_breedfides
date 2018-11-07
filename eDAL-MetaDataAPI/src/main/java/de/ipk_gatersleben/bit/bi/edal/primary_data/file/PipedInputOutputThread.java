/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;

/**
 * @author lange
 * @author arendd
 */
final class PipedInputOutputThread extends EdalThread {

	private InputStream pipedSource;

	private Collection<PipedOutputStream> pipedout;
	private long StreamSize;

	private PipedInputOutputThread() {
		super();
		this.StreamSize = 0;

	}

	PipedInputOutputThread(final InputStream pipedSource,
			final Collection<PipedInputStream> pipedDest) throws IOException {
		this();
		this.pipedout = new Vector<PipedOutputStream>(pipedDest.size());
		this.pipedSource = pipedSource;

		for (final PipedInputStream pipedInputStream : pipedDest) {
			final PipedOutputStream out = new PipedOutputStream(
					pipedInputStream);
			this.pipedout.add(out);
		}

	}

	long getSize() {
		return this.StreamSize;

	}

	@Override
	public void run() {
		int readbytes = 0;
		final byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];

		try {
			final Collection<PipedOutputStream> closedStreams = new ArrayList<PipedOutputStream>(
					this.pipedout.size());
			while ((readbytes = this.pipedSource.read(buffer)) != -1) {
				this.StreamSize += readbytes;
				DataManager.getImplProv().getLogger()
						.debug("read from piped stream: " + readbytes);
				this.pipedout.removeAll(closedStreams);
				for (final PipedOutputStream pipedOutputStream : this.pipedout) {
					try {
						pipedOutputStream.write(buffer, 0, readbytes);
					} catch (final IOException e) {
						DataManager
								.getImplProv()
								.getLogger()
								.debug("unable to write piped streams. Consumer closed inputstream: "
										+ e.getMessage());

						closedStreams.add(pipedOutputStream);
						try {
							pipedOutputStream.close();
						} catch (final IOException e1) {
							DataManager
									.getImplProv()
									.getLogger()
									.error("unable to close piped output streams: "
											+ e.getMessage());
						}
					}
				}
			}
		} catch (final IOException e) {
			DataManager
					.getImplProv()
					.getLogger()
					.error("unable to read from data streams: "
							+ e.getMessage());
		}
		for (final PipedOutputStream pipedOutputStream : this.pipedout) {

			try {
				pipedOutputStream.close();
			} catch (final IOException e) {
				DataManager
						.getImplProv()
						.getLogger()
						.error("unable to close piped output streams: "
								+ e.getMessage());
			}
		}

	}
}
