package ralfs.de.ipk_gatersleben.bit.bi.edal.examples;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.logging.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class FullExample {

	public static void main(String[] args) throws Exception {

		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("scientific_reviewer@mail.com"),
				new InternetAddress("substitute_reviewer@mail.com"), new InternetAddress("managing_reviewer@mail.com"),
				new InternetAddress("ralfs@ipk-gatersleben.de"));
		// startup the instance
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		Logger exampleLogger = ((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger();

		// get the root directory of e!DAL instance
//		Path pathToRessource = Paths.get("C://usr//unbenannt.png");
//		FileInputStream fin = new FileInputStream(pathToRessource.toFile());
//		entity.store(fin);
//		fin.close();

//		
//		PrimaryDataDirectory subDirectory = rootDirectory.createPrimaryDataDirectory("subDirectoryTest");
//		MetaData metadata = subDirectory.getMetaData();
//		metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData("PoweredByPlants"));
//		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("e!DAL Poster"));
//
//		/* set metadata generate automatically a new version for this object */
//		subDirectory.setMetaData(metadata);
		ArrayList<String> strings = StoreDataScript.process(rootDirectory,50);
//		MetaDataImplementation metadata2 = new MetaDataImplementation();
//		metadata2.setElementValue(EnumDublinCoreElements.TITLE, metadata.getElementValue(EnumDublinCoreElements.TITLE));
//		System.out.println(metadata.toString());
		
		
		List<PrimaryDataEntity> results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData(strings.get(0)), false, true);
		for(int i = 0; i < results1.size(); i++)
			exampleLogger.info("Title: "+results1.get(i).toString()+" Desc: "+results1.get(i).getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).toString());
		DataManager.shutdown();

	}
}