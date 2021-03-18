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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.DriverManager;

import org.h2.tools.RunScript;
import org.h2.tools.Script;

@SuppressWarnings("unused")
public class DatabaseBackupAndRepair {

	public static void main(String[] args) throws Exception {

		/** Export the whole database into a SQL Script File **/
//
//		Script.execute("jdbc:h2:split:30:C:/Users/arendd/edal/edaldb;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE", "sa", "",
//				new FileOutputStream(Paths.get(System.getProperty("user.home"), "backup.sql").toFile()));

		
		
		Script.process("jdbc:h2:split:30:C:/Users/arendd/edal/edaldb;DB_CLOSE_ON_EXIT=FALSE", "sa", "", 
				Paths.get(System.getProperty("user.home"), "backup.sql").toString(),
						"", "");
		
//		/** Rebuild the database file from a previously created SQL Script **/
//		
//		 RunScript.execute(DriverManager.getConnection("jdbc:h2:split:30:F:/database/edal_new_version_mv_2/edaldb"),
//		 new FileReader(Paths.get(System.getProperty("user.home"),
//		 "backup2.sql").toFile()));

	}

}
