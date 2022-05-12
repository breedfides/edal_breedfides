package de.ipk_gatersleben.bit.bi.edal.sample;

import javax.mail.internet.InternetAddress;
import javax.swing.JOptionPane;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

public class main {

	public static void main(String[] args) throws Exception {
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"));

		// get the root directory of e!DAL instance
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		
				

	}

}
