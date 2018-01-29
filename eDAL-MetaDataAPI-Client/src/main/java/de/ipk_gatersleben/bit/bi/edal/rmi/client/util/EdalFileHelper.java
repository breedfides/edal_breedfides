/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
