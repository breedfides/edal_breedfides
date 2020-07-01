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
