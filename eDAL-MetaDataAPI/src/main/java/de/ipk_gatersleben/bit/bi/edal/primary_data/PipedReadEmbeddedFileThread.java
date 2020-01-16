/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;

import javax.persistence.Embedded;

/**
 * Thread to read the {@link InputStream} of a an embedded file to the
 * {@link OutputStream} of the HTTP response with the help of a piped stream.
 * 
 * @author arendd
 */
public class PipedReadEmbeddedFileThread extends EdalThread {

	private InputStream inputStream = null;
	private PipedOutputStream pipedOut = null;
	private String fileName = null;

	/**
	 * Create a new Thread and set the corresponding {@link InputStream} and the
	 * {@link PipedOutputStream} to transfer the embedded file.
	 * 
	 * @param fileName    the name of the embedded file
	 * @param inputStream the {@link InputStream} of the embedded file
	 * @param pipedOut    the {@link PipedOutputStream} to send the {@link Embedded}
	 *                    file
	 */
	public PipedReadEmbeddedFileThread(final String fileName, final InputStream inputStream,
			final PipedOutputStream pipedOut) {
		super();
		this.inputStream = inputStream;
		this.pipedOut = pipedOut;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];
		int length;
		try {
			while ((length = inputStream.read(buffer)) != -1) {
				pipedOut.write(buffer, 0, length);
			}
			pipedOut.flush();
		} catch (IOException e) {
			DataManager.getImplProv().getLogger()
					.debug("Unable to send embedded file '" + fileName + "': " + e.getMessage());
		}

	}

}
