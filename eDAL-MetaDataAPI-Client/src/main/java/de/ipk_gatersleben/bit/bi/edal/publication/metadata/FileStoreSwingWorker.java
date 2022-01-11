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
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;

public class FileStoreSwingWorker extends SwingWorker<Object, Object> {

	private Path file;
	private ClientPrimaryDataFile clientPrimaryDataFile;
	private JProgressBar fileProgressBar;
	private JProgressBar overallProgressBar;
	private CountDownLatch latch;

	public FileStoreSwingWorker(JProgressBar fileProgressbar, JProgressBar overallProgressbar, Path file,
			ClientPrimaryDataFile clientPrimaryDataFile, CountDownLatch latch) {

		this.clientPrimaryDataFile = clientPrimaryDataFile;
		this.file = file;
		this.fileProgressBar = fileProgressbar;
		this.overallProgressBar = overallProgressbar;
		this.latch = latch;

	}

	@Override
	protected Integer doInBackground() throws Exception {

		try {

			this.fileProgressBar.setString(this.file.getFileName().toString());

			FileInputStream fis = new FileInputStream(this.file.toFile());

			FileProgressInputStream progressInputStream = new FileProgressInputStream(this.fileProgressBar, fis,
					this.file.getFileName().toString(), this.file.toFile().length());

			clientPrimaryDataFile.store(progressInputStream);

			progressInputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	@Override
	protected void done() {
		this.overallProgressBar.setValue(this.overallProgressBar.getValue() + 1);
		latch.countDown();
	}

}