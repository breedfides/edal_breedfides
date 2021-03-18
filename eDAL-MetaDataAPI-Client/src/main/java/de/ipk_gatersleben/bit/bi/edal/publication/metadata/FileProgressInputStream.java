/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JProgressBar;

public class FileProgressInputStream extends FilterInputStream {

	private FileProgressMonitor monitor;
	private long nread = 0;
	private long size = 0;
	private JProgressBar fileProgressBar;
	private long steps = 0;
	private String filename;

	/**
	 * Constructs an object to monitor the progress of an input stream.
	 * 
	 * @param inputStream
	 *            The input stream to be monitored.
	 * @param fileProgressBar
	 *            the corresponding {@link JProgressBar}
	 * @param filename
	 *            the name of the file to stream
	 * @param length
	 *            the file length
	 */
	public FileProgressInputStream(JProgressBar fileProgressBar, InputStream inputStream, String filename,
			long length) {
		super(inputStream);
		this.fileProgressBar = fileProgressBar;
		this.filename = filename;
		this.size = length;

		this.monitor = new FileProgressMonitor(this.fileProgressBar, this.size);
	}

	/**
	 * Get the ProgressMonitor object being used by this stream. Normally this isn't
	 * needed unless you want to do something like change the descriptive text
	 * partway through reading the file.
	 * 
	 * @return the ProgressMonitor object used by this object
	 */
	public FileProgressMonitor getProgressMonitor() {
		return this.monitor;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress monitor
	 * after the read.
	 */
	public int read() throws IOException {
		int c = in.read();
		if (c >= 0) {
			this.fileProgressBar.setString(this.filename);
			this.monitor.setProgress(++nread);
		}
		return c;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress monitor
	 * after the read.
	 */
	public int read(byte b[]) throws IOException {
		int nr = in.read(b);
		if (nr > 0) {
			this.fileProgressBar.setString(this.filename);
			this.monitor.setProgress(nread += nr);
		}
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress monitor
	 * after the read.
	 */
	public int read(byte b[], int off, int len) throws IOException {

		String prepareCopy = "Prepare Copying : ";

		if (in.available() == size) {

			String label = this.fileProgressBar.getString();
			if (label.indexOf(prepareCopy) == -1) {
				this.fileProgressBar.setString(prepareCopy + label);
			}
			monitor.setProgress((int) (steps));

		} else {
			String label = this.fileProgressBar.getString();

			if (label.indexOf(prepareCopy) != -1) {
				this.fileProgressBar
						.setString(label.substring(label.indexOf(prepareCopy) + prepareCopy.length(), label.length()));

			}
			monitor.setProgress(size - in.available());
		}

		int nr = in.read(b, off, len);
		// if (nr > 0) {
		// monitor.setProgress(nread += nr);
		// }
		steps++;
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.skip</code> to update the progress monitor
	 * after the skip.
	 */
	public long skip(long n) throws IOException {
		long nr = in.skip(n);
		if (nr > 0)
			monitor.setProgress(nread += nr);
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.close</code> to close the progress monitor
	 * as well as the stream.
	 */
	public void close() throws IOException {
		in.close();
		monitor.close();
	}

	/**
	 * Overrides <code>FilterInputStream.reset</code> to reset the progress monitor
	 * as well as the stream.
	 */
	public synchronized void reset() throws IOException {
		in.reset();
		nread = size - in.available();
		monitor.setProgress(nread);
	}
}