/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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