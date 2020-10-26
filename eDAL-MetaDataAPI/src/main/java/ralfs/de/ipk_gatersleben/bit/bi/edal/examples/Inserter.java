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
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;


import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

public class Inserter {
	
	private PrimaryDataDirectory rootDirectory;
	private MetaData searchable = null;
	public Inserter(PrimaryDataDirectory root) throws PrimaryDataDirectoryException {
		this.rootDirectory = root;
		List<PrimaryDataEntity> result = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData("perspiciatis"), false, true);
		if(!result.isEmpty()) {
			searchable = result.get(0).getMetaData();
		}
	}

	public void process(PrimaryDataDirectory rootDirectory, int size) throws Exception {
		ArrayList<String> names = getList("src/test/resources/names.txt");
		ArrayList<String> words = getList("src/test/resources/words.txt");
		Path pathToRessource = Paths.get("src/test/resources/_TEST.zip");
		FileInputStream fin = new FileInputStream(pathToRessource.toFile());
		Locale[] locals = Locale.getAvailableLocales();
		int length = locals.length-1;
		int countNames =names.size();
		int countWords =words.size();
		Random random = new Random();
		Identifier referenceIdentifier = new Identifier(words.get(random.nextInt(countWords)));
		ArrayList<PrimaryDataFile> files = new ArrayList<>();
		PrimaryDataDirectory currentDirectory = null;
		String archiveName = "";
		for(int i = 0; i < size; i++) {
			if(i%10000 == 0) {
				archiveName = "Lib000."+(i/10000);
				currentDirectory = rootDirectory.createPrimaryDataDirectory(archiveName+names.get(random.nextInt(countNames)));
			}
			PrimaryDataFile entity = currentDirectory.createPrimaryDataFile("Entity86.."+i);
			MetaData metadata = entity.getMetaData();
			Persons persons = new Persons();
			NaturalPerson np = new NaturalPerson(names.get(random.nextInt(countNames)),
					names.get(random.nextInt(countNames)),words.get(random.nextInt(countWords)),
					words.get(random.nextInt(countWords)),words.get(random.nextInt(countWords)));
			persons.add(np);
			metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
			metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,new LegalPerson(
					names.get(random.nextInt(countNames)),words.get(random.nextInt(countWords)),
					words.get(random.nextInt(countWords)),words.get(random.nextInt(countWords))));
			
			Subjects subjects = new Subjects();
			subjects.add(new UntypedData("Subject"+words.get(random.nextInt(countWords))));
			EdalLanguage lang = new EdalLanguage(locals[random.nextInt(length)]);
			metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(words.get(random.nextInt(countWords))));
			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(words.get(random.nextInt(countWords))));
			metadata.setElementValue(EnumDublinCoreElements.IDENTIFIER, referenceIdentifier);
			entity.setMetaData(metadata);
			//entity.store(fin);
			log(archiveName+i+"/"+size+" Saved");
		}
		insertSearchable(currentDirectory);
		fin.close();
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
		PrimaryDataDirectory parentDir = rootDirectory.getParentDirectory();
		List<PrimaryDataEntity> result = parentDir.searchByDublinCoreElement(EnumDublinCoreElements.TITLE, new UntypedData("perspiciatis"), false, true);
		//if(result.isEmpty()) {
			log("Inserting new searchable");
			PrimaryDataFile entity = rootDirectory.createPrimaryDataFile("searchEntity");
			searchable = entity.getMetaData();
			Persons persons = new Persons();
			NaturalPerson np = new NaturalPerson("surename,Eric","lastname-Ralfs","adress.Halberstadt","zip;38820","country/DE");
			persons.add(np);
			searchable.setElementValue(EnumDublinCoreElements.CREATOR, persons);
			searchable.setElementValue(EnumDublinCoreElements.PUBLISHER,new LegalPerson("IBM","DC","543771","USA"));		
			Subjects subjects = new Subjects();
			subjects.add(new UntypedData("Sed ut perspiciatis"));
			EdalLanguage lang = new EdalLanguage(Locale.US);
			searchable.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
			searchable.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			searchable.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData("title_perspiciatis"));
			searchable.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData("Lorem"));
			searchable.setElementValue(EnumDublinCoreElements.IDENTIFIER, new Identifier("reference"));
			entity.setMetaData(searchable);
//		}else {
//			searchable = result.get(0).getMetaData();
//		}
	}
	
	public MetaData getSearchable() {
		return searchable;
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

