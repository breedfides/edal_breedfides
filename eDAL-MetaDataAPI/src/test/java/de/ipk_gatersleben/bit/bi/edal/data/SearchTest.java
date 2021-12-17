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
package de.ipk_gatersleben.bit.bi.edal.data;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

public class SearchTest extends EdalDefaultTestCase{
	
	private static Path PATH = Paths.get(System.getProperty("user.home"), "Search_Test");

	//@Test
	public void testSearch() throws Exception{
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true,
						this.configuration), EdalHelpers
						.authenticateWinOrUnixOrMacUser());
		
	}
	@AfterEach
	public void clearTestFiles() throws IOException {
		FileUtils.deleteDirectory(new File(PATH.toString()));
	}
	
	static void createDataset(int copies) throws IOException {
		File dir = PATH.toFile();
		if(!dir.exists()) {
			dir.mkdir();
		}		
		for(int j = 0; j < 3; j++) {
			FileWriter myWriter = new FileWriter(Paths.get(PATH.toString(), "content_"+Math.pow(10, j)+".txt").toString());
			BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
			String s = "This is a test for indexing the content of text files.";
			int size = (int) ((Math.pow(10, j)*1024*1024)/s.getBytes().length);
			for(int i = 0; i < size; i++) {
				bufferedWriter.write(s);
			}
			myWriter.close();
		}
		
		for(int i = 0; i < copies; i++) {
			Files.copy(Paths.get(PATH.toString(), "test.txt"), Paths.get(PATH.toString(), "copy_"+i+".txt"),StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
