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
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList;

public class MergingEntityOutputThread extends EdalThread {

	private String template = "";
	private String coding = "";
	private Context context = null;
	private OutputStreamWriter output = null;
	private CountDownLatch latch = null;
	private List<PrimaryDataEntity> list = null;

	public MergingEntityOutputThread(final String template, final String coding, final Context context,
			final OutputStreamWriter output, final CountDownLatch latch, final List<PrimaryDataEntity> list) {
		super();
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
			Velocity.mergeTemplate(this.template, this.coding, this.context, this.output);
		} catch (final VelocityException e) {
			DataManager.getImplProv().getLogger().warn("Parsing Template stopped !");
			DataManager.getImplProv().getLogger().debug("Parsing Template stopped: " + e.getMessage());
			if (this.list instanceof AsynchronList<?>) {
				((AsynchronList<PrimaryDataEntity>) this.list).setStopped();
			}
			try {
				this.output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		this.latch.countDown();
		if (this.list != null) {
			this.list.clear();
		}
	}
}
