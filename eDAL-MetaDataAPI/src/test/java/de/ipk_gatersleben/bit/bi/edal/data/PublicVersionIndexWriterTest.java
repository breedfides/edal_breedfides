/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.ralfs.EdalDirectoryVisitorWithMetaData;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;
import de.ipk_gatersleben.bit.bi.edal.test.EdalDefaultTestCase;

class PublicVersionIndexWriterTest extends EdalDefaultTestCase{

	private Path PATH = Paths.get(System.getProperty("user.home"), "TEST_DATASET");
	//@Test
	void testStoreAndIndexingLargeData() throws PrimaryDataDirectoryException, EdalException, EdalAuthenticateException, CloneNotSupportedException, MetaDataException, PrimaryDataEntityVersionException, IOException, PrimaryDataEntityException, AddressException, PublicReferenceException {
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(EdalHelpers.getFileSystemImplementationProvider(true, this.configuration), EdalHelpers.authenticateWinOrUnixOrMacUser());
		
		PrimaryDataDirectory entity = rootDirectory.createPrimaryDataDirectory("MyPrimleTi6343769");
		MetaData metadata = entity.getMetaData().clone();
		Persons persons = new Persons();
		NaturalPerson np = new NaturalPerson("Andrea", "Br‰utigam",
				"Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstraﬂe 3",
				"06466", "Germany");
		persons.add(np);
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
						"IPK Gatersleben, Seeland OT Gatersleben, Corrensstraﬂe 3", "06466", "Germany"));

		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("wheat"));
		subjects.add(new UntypedData("transcriptional network"));
		subjects.add(new UntypedData("genie3"));
		subjects.add(new UntypedData("transcriptomics"));
		EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
		metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
		metadata.setElementValue(EnumDublinCoreElements.TITLE,
				new UntypedData("Results of genie3 analyis for 329452343239"));
		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
				"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
		entity.setMetaData(metadata);
		Path pathToRessource = Paths.get(System.getProperty("user.home") + File.separator + "textfiles23");
		EdalDirectoryVisitorWithMetaData edalVisitor = new EdalDirectoryVisitorWithMetaData(entity, pathToRessource,
				metadata, true);
		Files.walkFileTree(PATH, edalVisitor);
		entity.addPublicReference(PersistentIdentifier.DOI);
		entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress("ralfs@erics.smtp.2805"));
		
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
	    //FileUtils.deleteDirectory(PATH.toFile());
	}
	

}
