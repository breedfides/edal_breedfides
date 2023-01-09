/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
/**
 * A class used to get ClientPrimaryDataEntity object with path and rootDirectory.
 *
 * @version 1.0
 * @author Jinbo Chen
 */
public class EdalFileHelper {
	public static ClientPrimaryDataEntity getEntity(final String path,final ClientPrimaryDataDirectory rootDirectory)
			throws RemoteException, PrimaryDataDirectoryException,
			NotBoundException {
		final String[] dirs = path.split("\\/");
		ClientPrimaryDataDirectory dir = rootDirectory;
		ClientPrimaryDataEntity entity = dir;

		if (dirs.length > 1) {
			for (int i = 1; i < dirs.length; i++) {
				if (!dirs[i].isEmpty()) {
					if (dir.exist(dirs[i])) {
						entity = dir.getPrimaryDataEntity(dirs[i]);

						if (entity.isDirectory()) {
							dir = (ClientPrimaryDataDirectory) entity;
						} else {
							return entity;
						}
					}
				}
			}

			return entity;
		} else {
			return dir;
		}
	}
}
