/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
public class PipedReadEdalFileThread extends EdalThread {

	private PrimaryDataFile currentFile = null;
	private PipedOutputStream pipedOut = null;

	/**
	 * Create the thread and set the {@link PrimaryDataFile} to send and the
	 * {@link PipedOutputStream} to write the file.
	 * 
	 * @param currentFile the {@link PrimaryDataFile} to transfer
	 * @param pipedOut    the {@link PipedOutputStream} to write the file
	 */
	public PipedReadEdalFileThread(final PrimaryDataFile currentFile, final PipedOutputStream pipedOut) {
		super();
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
