package de.ipk_gatersleben.bit.bi.edal.data;

import static org.junit.jupiter.api.Assertions.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

class PublicVersionIndexWriterTest extends EdalDefaultTestCase{

	private Path PATH = Paths.get(System.getProperty("user.home"), "TEST_DATASET");
	//@Test
	void testStoreAndIndexingLargeData() throws PrimaryDataDirectoryException, EdalException, EdalAuthenticateException {
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());
		
		
		//publish dataset
	}
	
	@BeforeEach
	void createDataset() throws IOException {
		File dir = PATH.toFile();
		if(!dir.exists()) {
			dir.mkdir();
		}		
		FileWriter myWriter = new FileWriter(Paths.get(PATH.toString(), "test.txt").toString());
		BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
		String s = "This is a test for indexing large text data.";
		int size = (Integer.MAX_VALUE-(200*1024*1024))/s.getBytes().length;
		for(int i = 0; i < size; i++) {
			bufferedWriter.write(s);
		}
		bufferedWriter.write("last words");	
		myWriter.close();
		for(int i = 0; i < 5; i++) {
			Files.copy(Paths.get("custom.txt"), Paths.get("copy_"+i+".txt"),StandardCopyOption.REPLACE_EXISTING);
		}
	}
	@AfterEach
	void deleteDataset() throws IOException {
	    FileUtils.deleteDirectory(PATH.toFile());
	}
	

}
