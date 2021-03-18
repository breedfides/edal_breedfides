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
package de.ipk_gatersleben.bit.bi.edal.rmi.server.wrapper;

import java.io.IOException;
import java.io.PipedInputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;

public class StreamInputToOutputThread extends Thread {

	PrimaryDataFile file = null;
	PipedInputStream pipedInt = null;

	public StreamInputToOutputThread(PrimaryDataFile file, PipedInputStream pipedIn) {
		this.file = file;
		this.pipedInt = pipedIn;
	}

	@Override
	public void run() {
		try {
			file.store(pipedInt);
			pipedInt.close();
		} catch (PrimaryDataFileException | IOException | PrimaryDataEntityVersionException e) {
			EdalServer.getLogger().error(e.getMessage());
		}
	}
}