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
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import javax.mail.internet.InternetAddress;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class CreateTestDatabase {
	
	public static void main(String[] args) throws Exception {
		PrimaryDataDirectory rootDirectory = getRoot();
    	try {
			process(rootDirectory, 8000);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	DataManager.shutdown();
	}

	public static void process(PrimaryDataDirectory rootDirectory, int size) throws Exception {
		ArrayList<String> names = getList("src/test/resources/names.txt");
		ArrayList<String> words = getList("src/test/resources/words.txt");
		Path pathToRessource = Paths.get("src/test/resources/_TEST.zip");
		FileInputStream fin = new FileInputStream(pathToRessource.toFile());
		Locale[] locals = Locale.getAvailableLocales();
		int countNames =names.size();
		int countWords =words.size();
		Random random = new Random();
		Identifier referenceIdentifier = new Identifier(words.get(random.nextInt(countWords)));
		ArrayList<PrimaryDataFile> files = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			PrimaryDataFile entity = rootDirectory.createPrimaryDataFile("PrimFileNumero0."+i);
			entity.store(fin);
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
			EdalLanguage lang = new EdalLanguage(locals[random.nextInt(locals.length)]);
			metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(words.get(random.nextInt(countWords))));
			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(words.get(random.nextInt(countWords))));
			metadata.setElementValue(EnumDublinCoreElements.IDENTIFIER, referenceIdentifier);
			entity.setMetaData(metadata);	
			log("Directory "+i+"/"+size+" Saved");
		}
		fin.close();
	}
	
	
	public static PrimaryDataDirectory getRoot() throws Exception{
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("scientific_reviewer@mail.com"),
				new InternetAddress("substitute_reviewer@mail.com"), new InternetAddress("managing_reviewer@mail.com"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),"imap.ipk-gatersleben.de","","");
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		return rootDirectory;
	}
	
	public static String getSubfileTitle(PrimaryDataDirectory root) throws Exception {
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
	
	
	public static ArrayList<String> getList(String pathName) throws FileNotFoundException{
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
	
	
//	public static void generateRandomDic() throws Exception {
//		File file = new File(System.getProperty("user.home")+"/dict/german.txt");
//	    ArrayList<String> words = new ArrayList<String>();
//	    Scanner in = new Scanner(file);
//	    while (in.hasNextLine()){
//	        words.add(in.nextLine());
//	    }
//	    int size = words.size();
//	    Random rand = new Random();
//	    FileWriter writer = new FileWriter(System.getProperty("user.home")+"/dict/newgerman.txt"); 
//	    for(int i= 0; i < 4000;i++) {
//	    	writer.write(words.get(rand.nextInt(size)) + System.lineSeparator());
//	    }
//    	writer.close();
//	    
//	}


}
