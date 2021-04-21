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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

public class Inserter {
	
	private PrimaryDataDirectory rootDirectory;
	private MetaData metadata = null;
	Path path = Paths.get("src/test/resources/Beispiel_Titel");
	public Inserter(PrimaryDataDirectory root) throws PrimaryDataDirectoryException {
		this.rootDirectory = root;
	}

	public void process(int size) throws Exception {
		PrimaryDataDirectory currentDirectory = null;
		String archiveName = "";
		for(int i = 0; i < size; i++) {
			if(i%10000 == 0) {
				currentDirectory = rootDirectory.createPrimaryDataDirectory(new StringBuilder("FolderXK.").append(i/10000).toString());
			}
			PrimaryDataFile entity = currentDirectory.createPrimaryDataFile(new StringBuilder("FileXKK.").append(i).toString());
			MetaData metadata = entity.getMetaData().clone();
//			metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
//			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("Test_BA_Fuzzy"));
//			metadata.setElementValue(EnumDublinCoreElements.IDENTIFIER, referenceIdentifier);
			entity.setMetaData(metadata);
//			entity.store(fin);
			log(archiveName+" |"+i+"| /"+size+" Saved");
		}
		//insertSearchable(currentDirectory);
	}
	
	public void insertOne() throws PrimaryDataDirectoryException, CloneNotSupportedException, MetaDataException, PrimaryDataEntityVersionException, IOException, InterruptedException, PrimaryDataEntityException, AddressException, PublicReferenceException {
		log("Inserting new searchable");
		metadata = rootDirectory.getMetaData().clone();
		Persons persons = new Persons();
		NaturalPerson np = new NaturalPerson("Torben", "Ralfs",
				"Wernigerode", "38855", "Deutschland");
		persons.add(np);
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("Lucene"));
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,new LegalPerson("IPK","Gatersleben","06466","Deutschland"));		
		metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData("Information-Retrieval"));
		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("Zu den Aufgaben der Arbeit gehört zum"
				+ " einen der Entwurf und die Realisierung  von Information-Retrieval-Funktionen für eine Nutzerfreundliche"
				+ " Suche der heterogenen Daten in E!DAL. Zum anderen der Entwurf von Schnittstellen zur Verwendung der"
				+ " entwickelten Suchfunktionen. Die Realisierung der Suche teilt sich in die Implementierung einer geeigneten"
				+ " Indexstruktur und in die Erweiterung bestehender Suchfunktionen auf. Die Realisierung muss schließlich anhand"
				+ " geeigneter Kriterien bewertet werden."));
		//searchable.setElementValue(EnumDublinCoreElements.IDENTIFIER, new Identifier("Test_Referenz"));
		MyFileVisitor edalVisitor = new MyFileVisitor(rootDirectory, path, metadata, true);
		Files.walkFileTree(path, edalVisitor);
	}
	
	public String getSubfileTitle(PrimaryDataDirectory root) throws Exception {
		ArrayList<String> names = getList("src/test/resources/names.txt");
		PrimaryDataDirectory subdir;
		try {
			subdir = root.createPrimaryDataDirectory("subdirectory");
		} catch (PrimaryDataDirectoryException e) {
			subdir = (PrimaryDataDirectory) root.searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData("subdirectory"), false, true).get(0);
		}
		Random random = new Random();
		String name = "subfile"+names.get(random.nextInt(names.size()));
		PrimaryDataFile subfile = subdir.createPrimaryDataFile("subfile"+name);
		MetaData submetadata = subfile.getMetaData();
		submetadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(name));
		subfile.setMetaData(submetadata);
		return name;
	}
	
	private void insertSearchable(PrimaryDataDirectory rootDirectory) throws Exception {
		//if(result.isEmpty()) {
			log("Inserting new searchable");
			PrimaryDataFile entity = rootDirectory.createPrimaryDataFile("searchEntity");
			metadata = entity.getMetaData().clone();
			Persons persons = new Persons();
			NaturalPerson np = new NaturalPerson("needspace180","En","adress.Halberstadt","zip;38820","country/DE");
			persons.add(np);
			metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
			metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,new LegalPerson("IBM","DC","543771","USA"));		
			Subjects subjects = new Subjects();
			subjects.add(new UntypedData("snickers"));
			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			EdalLanguage lang = new EdalLanguage(Locale.US);
			metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
			metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData("title_perspiciatis"));
			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("Lorem"));
			entity.setMetaData(metadata);
			MyFileVisitor edalVisitor = new MyFileVisitor(rootDirectory, path, metadata, true);
			Files.walkFileTree(path, edalVisitor);
//		}else {
//			searchable = result.get(0).getMetaData();
//		}
	}
	
	public MetaData getSearchable() {
		return metadata;
	}
	
	
	public ArrayList<String> getList(String pathName) throws FileNotFoundException{
	File file = new File(pathName);
    ArrayList<String> strings = new ArrayList<String>();
    Scanner in = new Scanner(file);
    while (in.hasNextLine()){
        strings.add(in.nextLine());
    }
    return strings;
	}
	
    private static void log(String msg) {
    	DataManager.getImplProv().getLogger().info(msg);
    }


}

