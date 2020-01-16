/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.ImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.CalculateDirectorySizeThread;

/**
 * Interface to provide some general function for {@link ImplementationProvider}
 * 
 * @author arendd
 *
 */
public interface ServiceProvider {

	/**
	 * Function to calculate the size of {@link PrimaryDataDirectory}s via
	 * {@link CalculateDirectorySizeThread}
	 */
	void calculateDirectorySizes();

	/**
	 * Clean up function to remove objects, if a upload failed
	 * 
	 * @param root
	 *            the initial directory to start clean process
	 */
	void cleanUpForBrokenEntities(PrimaryDataDirectory root);

	/**
	 * Clean up function to remove objects, if a request for a persistent identifier
	 * was rejected
	 */
	void cleanUpForRejectedEntities();

	/**
	 * Getter for the available space in the mount path of eDAL.
	 * 
	 * @return available space
	 * @throws EdalException
	 *             if no path is specified.
	 */
	Long getAvailableStorageSpace() throws EdalException;

	/**
	 * Function to get the current number of users, who have ever login to the
	 * current instance
	 * 
	 * @return the number of logged users
	 */
	int getNumberOfUsers();

	/**
	 * Getter for the used space in the mount path of eDAL.
	 * 
	 * @return used space
	 * @throws EdalException
	 *             if no path is specified.
	 */
	Long getUsedStorageSpace() throws EdalException;

	/**
	 * Get flag for rebuild Lucene Index after a cleanup.
	 * 
	 * @return the flag
	 */
	boolean isCleaned();

	/**
	 * Set flag for rebuild Lucene Index after a cleanup.
	 * 
	 * @param flag
	 *            the flag to set
	 */
	void setCleaned(boolean flag);
}