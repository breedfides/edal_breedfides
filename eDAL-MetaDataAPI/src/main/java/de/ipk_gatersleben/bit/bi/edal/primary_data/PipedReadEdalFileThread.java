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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;

/**
 * Thread to read the {@link InputStream} of a
 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile} to
 * the {@link OutputStream} of the HTTP response with the help of a piped
 * stream.
 * 
 * @author arendd
 */
public class PipedReadEdalFileThread extends Thread {

	private PrimaryDataFile currentFile = null;
	private PipedOutputStream pipedOut = null;

	/**
	 * Create the thread and set the {@link PrimaryDataFile} to send and the
	 * {@link PipedOutputStream} to write the file.
	 * 
	 * @param currentFile
	 *            the {@link PrimaryDataFile} to transfer
	 * @param pipedOut
	 *            the {@link PipedOutputStream} to write the file
	 */
	public PipedReadEdalFileThread(final PrimaryDataFile currentFile, final PipedOutputStream pipedOut) {
		this.currentFile = currentFile;
		this.pipedOut = pipedOut;
	}

	@Override
	public void run() {
		try {
			currentFile.read(pipedOut);
		} catch (PrimaryDataFileException e) {
			DataManager.getImplProv().getLogger().warn("Unable to send file: " + e.getMessage());
		}
	}

}
