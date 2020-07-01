/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
