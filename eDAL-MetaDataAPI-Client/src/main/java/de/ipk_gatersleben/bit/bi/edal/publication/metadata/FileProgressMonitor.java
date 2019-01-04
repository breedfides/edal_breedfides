/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.metadata;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class FileProgressMonitor {

	private JProgressBar fileProgressBar;
	private long max;

	/**
	 * Constructs a graphic object that shows progress, typically by filling in a
	 * rectangular bar as the process nears completion.
	 * 
	 * @param fileProgressBar
	 *            the {@link JProgressBar} to monitor
	 * @param max
	 *            the upper bound of the range
	 */
	public FileProgressMonitor(JProgressBar fileProgressBar, long max) {
		this.max = max;
		this.fileProgressBar = fileProgressBar;

	}

	/**
	 * Indicate the progress of the operation being monitored. If the specified
	 * value is greater/equal than the maximum, the progress monitor is closed.
	 * 
	 * @param newValue
	 *            the value specifying the current value, between the maximum and
	 *            minimum specified for this component
	 */
	public void setProgress(final long newValue) {

		if (newValue >= max) {
			try {
				// if (sun.awt.AppContext.getAppContext() == null) {
				// sun.awt.SunToolkit.createNewAppContext();
				// }
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						fileProgressBar.setValue((int) Math.ceil(100.0 / max * newValue));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			close();
		} else if (this.fileProgressBar != null) {
			try {
				// if (sun.awt.AppContext.getAppContext() == null) {
				// sun.awt.SunToolkit.createNewAppContext();
				// }
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						fileProgressBar.setValue((int) Math.ceil(100.0 / max * newValue));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Indicate that the operation is complete. This happens automatically when the
	 * value set by setProgress is greater/equal than max, but it may be called
	 * earlier if the operation ends early.
	 */
	public void close() {
		fileProgressBar = null;
	}

	/**
	 * Returns the maximum value - the higher end of the progress value.
	 * 
	 * @return an int representing the maximum value
	 * @see #setMaximum
	 */
	public long getMaximum() {
		return max;
	}

	/**
	 * Specifies the maximum value.
	 * 
	 * @param m
	 *            an int specifying the maximum value
	 * @see #getMaximum
	 */
	public void setMaximum(int m) {
		if (fileProgressBar != null) {
			fileProgressBar.setMaximum(m);
		}
		max = m;
	}

}
