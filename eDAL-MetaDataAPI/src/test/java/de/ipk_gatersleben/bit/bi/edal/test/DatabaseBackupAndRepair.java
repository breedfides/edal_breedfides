/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.DriverManager;

import org.h2.tools.RunScript;
import org.h2.tools.Script;

public class DatabaseBackupAndRepair {

	public static void main(String[] args) throws Exception {

		/** Export the whole database into a SQL Script File **/

		Script.execute("jdbc:h2:split:30:C:/Users/arendd/edal_working/edaldb;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE", "sa", "",
				new FileOutputStream(Paths.get(System.getProperty("user.home"), "backup.sql").toFile()));

		/** Rebuild the database file from a previously created SQL Script **/
		//
		 RunScript.execute(DriverManager.getConnection("jdbc:h2:split:30:C:/Users/arendd/edal_new_version/edaldb;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE"),
		 new FileReader(Paths.get(System.getProperty("user.home"),
		 "backup.sql").toFile()));

	}

}
