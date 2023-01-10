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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Function to change SQL Script for H2 database to work with new H2 version 2.x
 * For Switching from Version 1.4.x to 2.x it is necessary to change some persistence relevant annotations
 * Change all "char(xxx)" columns to "character varying(xxx)" 
 * Change all attributes called "year" because it is a reserved name
 * 
 * @author arendd
 *
 */

public class FixSQLScript {

	public static void main(String[] args) throws IOException {

		Path filePath = Paths.get(System.getProperty("user.home"), "backup_h2_1.sql");

//		File file = filePath.toFile();
//
//		if (!file.canRead() || !file.isFile())
//			System.exit(0);
//
//		BufferedReader in = null;
//		try {
//			in = new BufferedReader(new FileReader(file));
//			String zeile = null;
//
//			int linecounter = 0;
//
//			while ((zeile = in.readLine()) != null) {
//
////				if (zeile.contains("CREATE")) {
////					System.out.println("Gelesene Zeile: '" + zeile + "'");
////					linecounter=1;
////				}
////				if (linecounter > 0) {
////					System.out.println("Gelesene Zeile: '" + zeile + "'");
////					linecounter++;
////				}
////				if (linecounter == 10) {
////					linecounter = 0;
////				}
//				
//				if (zeile.contains("YEAR")) {
//					System.out.println("Gelesene Zeile: '" + zeile+"'");
//				}
//				
//				if (zeile.contains(" CHAR(")) {
//					System.out.println("Gelesene Zeile: '" + zeile+"'");
//				}
//
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (in != null)
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//		}

		Path filePathNew = Paths.get(System.getProperty("user.home"), "backup_h2_2.sql");

		BufferedReader inputStream = null;
		PrintWriter outputStream = null;

		try {
			inputStream = new BufferedReader(new FileReader(filePath.toFile()));
			outputStream = new PrintWriter(new FileWriter(filePathNew.toFile()));

			String l;
			while ((l = inputStream.readLine()) != null) {
				
				if(l.contains("YEAR")) {
					System.out.println(l);
					l= l.replace("YEAR", "YEAROFASSIGNMENT");
				}
				
				if(l.contains(" CHAR(")) {
					System.out.println(l);
					l= l.replace(" CHAR(", " VARCHAR(");
				}
				
				outputStream.println(l);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}

	}
}
