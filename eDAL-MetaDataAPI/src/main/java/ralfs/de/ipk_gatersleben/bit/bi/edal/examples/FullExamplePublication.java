package ralfs.de.ipk_gatersleben.bit.bi.edal.examples;
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
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import javax.mail.internet.InternetAddress;
import javax.swing.JOptionPane;

import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.*;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class FullExamplePublication {

	public static void main(String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration("dataCiteUserName", "dataCitePassword",
				"dataCitePrefix", new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"), new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"));

		// startup the instance
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(true, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());

		// get the root directory of e!DAL instance
		PrimaryDataDirectory directory = rootDirectory.createPrimaryDataDirectory("directory");
		PrimaryDataFile entity = directory.createPrimaryDataFile("imageTest.png");
		Path pathToRessource = Paths.get("C://usr//unbenannt.png");
		FileInputStream fin = new FileInputStream(pathToRessource.toFile());
		entity.store(fin);
		fin.close();

		MetaData metadata = entity.getMetaData();

		NaturalPerson np = new NaturalPerson("Daniel", "Arend", "Gatersleben", "06566", "Germany");
		Persons persons = new Persons();
		persons.add(np);
		// creator
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);

		// publisher
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, new LegalPerson("IPK Gatersleben", "Gatersleben", "06466", "Germany"));
		
		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("roots, hordeum vulgare, protein analysis, salinity stress"));
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);

		// set the new (changed) meta data
		entity.setMetaData(metadata);

		// add a new DOI reference
		entity.addPublicReference(PersistentIdentifier.DOI);
		// apply to publish all references
		entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress("ralfs@ipk-gatersleben.de"));

		// if you like to wait for application result
		//JOptionPane.showMessageDialog(null, "Continue");
		// or basicly continue with your code, because the applicant will get informed
		// by email

		// print the DOI and the URL
		System.out.println(entity.getCurrentVersion().getPublicReference(PersistentIdentifier.DOI));

		DataManager.shutdown();

	}
}