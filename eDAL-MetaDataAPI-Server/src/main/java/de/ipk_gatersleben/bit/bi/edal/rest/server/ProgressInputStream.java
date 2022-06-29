package de.ipk_gatersleben.bit.bi.edal.rest.server;
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

public class ProgressInputStream extends FilterInputStream {

	private String key;
	private long nread = 0;
	private long size = 0;
	private ConcurrentHashMap<String, Integer> progressMap;
	private long steps = 0;


	public ProgressInputStream(ConcurrentHashMap<String, Integer> progressMap, InputStream in, long length,
			String key) {
		super(in);
		this.progressMap = progressMap;
		this.size = length;
		this.key = key;
	}


	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress monitor
	 * after the read.
	 */
	public int read() throws IOException {
		System.out.println("read1");
		int c = in.read();
		if (c >= 0) {
			progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * (++nread)));
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
			//System.out.println("read2: "+nr);
			progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * (nread += nr)));
		}
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.read</code> to update the progress monitor
	 * after the read.
	 */
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		System.out.println("read3");
		String prepareCopy = "Prepare Copying : ";

		if (in.available() == size) {

//			String label = this.fileProgressBar.getString();
//			if (label.indexOf(prepareCopy) == -1) {
//				this.fileProgressBar.setString(prepareCopy + label);
//			}
			progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * (int)steps));

		} else {
//			String label = this.fileProgressBar.getString();
//
//			if (label.indexOf(prepareCopy) != -1) {
//				this.fileProgressBar
//						.setString(label.substring(label.indexOf(prepareCopy) + prepareCopy.length(), label.length()));
//
//			}
			progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * (size - in.available())));
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
		System.out.println("read4");
		long nr = in.skip(n);
		if (nr > 0)
			progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * (nread += nr)));
		return nr;
	}

	/**
	 * Overrides <code>FilterInputStream.close</code> to close the progress monitor
	 * as well as the stream.
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Overrides <code>FilterInputStream.reset</code> to reset the progress monitor
	 * as well as the stream.
	 */
	public synchronized void reset() throws IOException {
		in.reset();
		nread = size - in.available();
		progressMap.put(this.key, (int) Math.ceil(100.0 / this.size * nread));
	}
}