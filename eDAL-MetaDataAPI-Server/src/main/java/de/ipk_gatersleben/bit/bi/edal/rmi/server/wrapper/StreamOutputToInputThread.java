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
import java.io.PipedOutputStream;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;

public class StreamOutputToInputThread extends Thread {

	PrimaryDataFile file = null;
	PipedOutputStream pipedOut = null;

	public StreamOutputToInputThread(PrimaryDataFile file, PipedOutputStream pipedOut) {
		this.file = file;
		this.pipedOut = pipedOut;
	}

	@Override
	public void run() {
		try {
			file.read(pipedOut);
			
			pipedOut.close();
		} catch (PrimaryDataFileException | IOException e) {
			EdalServer.getLogger().error(e.getMessage());
		}
	}
}
