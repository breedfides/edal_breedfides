/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList;

public class MergingEntityOutputThread extends Thread {

    private String template = "";
    private String coding = "";
    private Context context = null;
    private OutputStreamWriter output = null;
    private CountDownLatch latch = null;
    private List<PrimaryDataEntity> list = null;

    public MergingEntityOutputThread(final String template, final String coding,
	    final Context context, final OutputStreamWriter output,
	    final CountDownLatch latch, final List<PrimaryDataEntity> list) {
	this.template = template;
	this.coding = coding;
	this.context = context;
	this.output = output;
	this.latch = latch;
	this.list = list;

    }

    @Override
    public void run() {
	try {
	    Velocity.mergeTemplate(this.template, this.coding, this.context,
		    this.output);
	} catch (final VelocityException e) {
	    DataManager.getImplProv().getLogger()
		    .warn("Parsing Template stopped !");
	    DataManager.getImplProv().getLogger()
		    .debug("Parsing Template stopped: " + e.getMessage());

	    if (this.list instanceof AsynchronList<?>) {
		((AsynchronList<PrimaryDataEntity>) this.list).setStopped();
	    }
	}

	this.latch.countDown();
	if (this.list != null) {
	    this.list.clear();
	}
    }
}
