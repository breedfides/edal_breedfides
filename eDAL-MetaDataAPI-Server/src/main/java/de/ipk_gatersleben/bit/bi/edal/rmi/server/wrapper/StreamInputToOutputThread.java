/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Server/Wrapper
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