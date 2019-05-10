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
import java.io.InputStream;
import java.io.PipedInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;

class CalculateCheckSumSHAThread extends EdalThread {

	private PipedInputStream pipedInputStream;

	private CheckSum checksum;

	private CountDownLatch stopLatch;

	CalculateCheckSumSHAThread(PipedInputStream pipedInputStream,
			CountDownLatch stopLatch) {
		super();
		this.pipedInputStream = pipedInputStream;
		this.stopLatch = stopLatch;

	}

	@Override
	public void run() {
		try {

			CheckSum cs = new CheckSum();
			cs.add(new CheckSumType("SHA-256", generateMD5(this.pipedInputStream)));

			this.setChecksum(cs);

		} catch (PrimaryDataEntityVersionException e) {
			DataManager
					.getImplProv()
					.getLogger()
					.error("unable to compute checksum from data stream: "
							+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
		this.stopLatch.countDown();
	}

	private String generateMD5(final InputStream inputStream)
			throws PrimaryDataEntityVersionException {
		if (inputStream == null) {
			throw new PrimaryDataEntityVersionException("inputstream is null");
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			final byte[] buff = new byte[EdalConfiguration.STREAM_BUFFER_SIZE];
			int read;
			while ((read = inputStream.read(buff)) != -1) {

				md.update(buff, 0, read);

			}
			final byte[] hashValue = md.digest();

			final StringBuffer sb = new StringBuffer();
			for (final byte element : hashValue) {
				sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
			}
			final String md5 = sb.toString();

			return md5;

		} catch (NoSuchAlgorithmException | IOException e) {
			throw new PrimaryDataEntityVersionException(
					"error creating SHA-256 checksum: " + e.getMessage(), e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException e) {
				throw new PrimaryDataEntityVersionException(
						"error closing inputstream: " + e.getMessage(), e);

			}
		}
	}

	/**
	 * @return the checksum
	 */
	CheckSum getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum
	 *            the checksum to set
	 */
	private void setChecksum(CheckSum checksum) {
		this.checksum = checksum;
	}
}
