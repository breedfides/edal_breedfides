package de.ipk_gatersleben.bit.bi.edal.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

class PublicVersionIndexWriterTest extends EdalDefaultTestCase{

	//@Test
	void testStoreAndIndexingLargeData() throws PrimaryDataDirectoryException, EdalException, EdalAuthenticateException {
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());

	}

}
