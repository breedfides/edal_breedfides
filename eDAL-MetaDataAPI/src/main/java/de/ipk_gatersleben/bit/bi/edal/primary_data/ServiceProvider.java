/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
	
	String getLatestPersistentIdentifierStatus();
}