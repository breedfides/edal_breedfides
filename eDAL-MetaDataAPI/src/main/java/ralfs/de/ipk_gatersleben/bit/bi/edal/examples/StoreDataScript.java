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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

public class StoreDataScript {
	
	private static final String[] algorithms = {"MD5","SHA1","SHA2"};
	private static final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final java.util.Random rand = new java.util.Random();

	public static ArrayList<PrimaryDataFile> process(PrimaryDataDirectory rootDirectory, int size) throws Exception {
		ArrayList<String> names = getList(System.getProperty("user.home")+"/dict/names.txt");
		ArrayList<String> words = getList(System.getProperty("user.home")+"/dict/german4000.txt");
		int countNames =names.size();
		int countWords =words.size();
		Random random = new Random();
		long startTime = System.nanoTime();
		ArrayList<PrimaryDataFile> files = new ArrayList<>();
		MetaData firstMetadata = null;
		for(int i = 0; i < size; i++) {
	        String title = words.get(random.nextInt(countWords));
	        String description = words.get(random.nextInt(countWords));
	        String firstName = names.get(random.nextInt(countNames));
	        String lastName = names.get(random.nextInt(countNames));
			PrimaryDataFile entity = rootDirectory.createPrimaryDataFile("PrimaryDataFileNR1."+i);
			MetaData metadata = entity.getMetaData();
			NaturalPerson np = new NaturalPerson(firstName, lastName, "Gatersleben", "06566", "Germany");
			Persons persons = new Persons();
			persons.add(np);
			metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
			metadata.setElementValue(EnumDublinCoreElements.PUBLISHER, new LegalPerson("IPK Gatersleben", description, "06466", "Germany"));
//			
			Subjects subjects = new Subjects();
			subjects.add(new UntypedData("roots, hordeum vulgare, protein analysis, salinity stress"));
			metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
			metadata.setElementValue(EnumDublinCoreElements.TITLE, new UntypedData(title));
			metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(description));
	        if(i == 0) {
	        	firstMetadata = metadata;
	        	System.out.println("Inserted_-> Ttitle: "+title+" Desc: "+description);
	        }
			entity.setMetaData(metadata);
			files.add(entity);
			
		}
		long stopTime = System.nanoTime()-startTime;
		System.out.println("Zeit zum speichern: "+((double)stopTime / 1_000_000_000.0));
		return files;
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
	
	private static String randomIdentifier() {
		StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(5)+5;
	        for(int j = 0; j < length; j++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	    }
	    String result = builder.toString();
		return result;
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
