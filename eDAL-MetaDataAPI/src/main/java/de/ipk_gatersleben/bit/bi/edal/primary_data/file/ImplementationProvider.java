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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.HttpServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.ServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ApprovalServiceProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.PermissionProvider;

/**
 * Interface that provide all necessary classes of a eDAL implementations.
 * 
 * @author lange
 * @author arendd
 */
public interface ImplementationProvider {

	/**
	 * Create an new instance of the {@link MetaData} implementation class.
	 * 
	 * @return a instance of the implementing class of {@link MetaData}.
	 */
	MetaData createMetaDataInstance();

	/**
	 * Getter for the {@link ApprovalServiceProvider} implementation class
	 * 
	 * @return the implementing class of {@link ApprovalServiceProvider}
	 */
	Class<? extends ApprovalServiceProvider> getApprovalServiceProvider();

	/**
	 * Getter for the {@link ServiceProvider} implementation class.
	 * 
	 * @return the implementing class of {@link ServiceProvider}.
	 */
	Class<? extends ServiceProvider> getServiceProvider();

	/**
	 * Getter for the configuration class with all parameter to start the eDAL
	 * system.
	 * 
	 * @return an {@link EdalConfiguration} class.
	 */
	EdalConfiguration getConfiguration();

	/**
	 * Getter for the current logger for system output
	 * 
	 * @return Logger
	 */
	Logger getLogger();

	/**
	 * Getter for the {@link PermissionProvider} implementation class.
	 * 
	 * @return the instance of {@link PermissionProvider} to be use.
	 */
	Class<? extends PermissionProvider> getPermissionProvider();

	/**
	 * Getter for the {@link PrimaryDataDirectory} implementation class.
	 * 
	 * @return the implementing class of {@link PrimaryDataDirectory}.
	 */
	Class<? extends PrimaryDataDirectory> getPrimaryDataDirectoryProvider();

	/**
	 * Getter for the {@link HttpServiceProvider} implementation class.
	 * 
	 * @return the implementing class of {@link HttpServiceProvider}.
	 */
	Class<? extends HttpServiceProvider> getHttpServiceProvider();

	/**
	 * Getter for the {@link PrimaryDataFile} implementation class.
	 * 
	 * @return the implementing class of {@link PrimaryDataFile}.
	 */
	Class<? extends PrimaryDataFile> getPrimaryDataFileProvider();

	/**
	 * Check if a root {@link PrimaryDataDirectory} exists and return it or
	 * create an new root {@link PrimaryDataDirectory}
	 * 
	 * @param supportedPrincipals
	 *            the supported {@link Principal}s
	 * 
	 * @return the root {@link PrimaryDataDirectory}.
	 * @throws PrimaryDataDirectoryException
	 *             if unable to mount with the eDAL system.
	 */
	PrimaryDataDirectory mount(List<Class<? extends Principal>> supportedPrincipals)
			throws PrimaryDataDirectoryException;

	/**
	 * Internal Function to reload a {@link PrimaryDataEntity} with his
	 * {@link UUID}.
	 * 
	 * @param uuid
	 *            the {@link UUID} of the {@link PrimaryDataEntity}
	 * @param versionNumber
	 *            the number of the {@link PrimaryDataEntityVersion}
	 * @return the {@link PrimaryDataEntity}
	 * @throws EdalException
	 *             if unable to find a {@link PrimaryDataEntity}
	 */
	PrimaryDataEntity reloadPrimaryDataEntityByID(String uuid, long versionNumber) throws EdalException;

	/**
	 * Cleanup all resources used by the particular implementation provider
	 */
	void shutdown();

}