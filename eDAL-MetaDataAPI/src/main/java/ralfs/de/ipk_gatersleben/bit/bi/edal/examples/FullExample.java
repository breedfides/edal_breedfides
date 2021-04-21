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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.apache.lucene.store.Directory;
import org.hibernate.query.NativeQuery;

import de.ipk_gatersleben.bit.bi.edal.primary_data.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataDirectoryImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataEntityVersionImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PrimaryDataFileImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicReferenceImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.*;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class FullExample {


	public static void main(String[] args) throws Exception {		
		PrimaryDataDirectory root = getRoot();
//		Directory index = FSDirectory.open(Paths.get(((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexDirectory().toString(),"Master_Index"));
//		IndexReader reader = DirectoryReader.open(((FileSystemImplementationProvider)DataManager.getImplProv()).getWriter());
//		IndexSearcher searcher = new IndexSearcher(reader);
		
//		final CriteriaBuilder builder = session.getCriteriaBuilder();
//		CriteriaQuery<?> query = builder.createQuery(PrimaryDataEntityVersionImplementation.class);
//		Root<PrimaryDataEntityVersionImplementation> queryRoot = query.from(PrimaryDataEntityVersionImplementation.class);
//		Join<PrimaryDataFileImplementation,PrimaryDataEntityVersionImplementation> join = queryRoot.join("versionList");
//		ParameterExpression<PrimaryDataEntityVersionImplementation> set = builder.parameter(PrimaryDataEntityVersionImplementation.class);
//		query.where(builder.equal(join.get("parentDirectory"), "5c6a8522-012f-41b3-9866-39bd32e10250"));
//		TypedQuery<?> tq = session.createQuery(query);
//		List<?> resultList = tq.setParameter(set, datatypeList).getResultList();
		//log(((FileSystemImplementationProvider)DataManager.getImplProv()).getConfiguration().getStaticServerAddress());
//		Inserter inserter = new Inserter(root);
//		inserter.insertOne();
//	
//		Thread.sleep(10000);
//		testKeyword(root);
//		log("start Search");
//		long start = System.currentTimeMillis();
//		List<PrimaryDataEntity> results = DataManager.searchByKeyword("Eric", false, "singleData");
//		for(PrimaryDataEntity entity : results) {
//			log(entity.getID());
//		}
//		log("Finsihed in: "+(System.currentTimeMillis()-start));
		Thread.sleep(90000000);	
		//Thread.sleep(60000);
//		List<PrimaryDataEntity> results = DataManager.searchByKeyword("Torben", false, "singleData");
//		log("public data: "+results.size());
		DataManager.shutdown();
			
//		Thread.sleep(5000);
//		Session session = ((FileSystemImplementationProvider)DataManager.getImplProv()).getSessionFactory().openSession();
	//	
//		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//		//this.implementationProviderLogger.info("Starting execute Index task: lastIndexedID: " + lastIndexedID);
//		CriteriaQuery<PublicReferenceImplementation> criteria = criteriaBuilder.createQuery(PublicReferenceImplementation.class);
//		Root<PublicReferenceImplementation> root = criteria.from(PublicReferenceImplementation.class);
//		criteria.where(criteriaBuilder.equal(root.get("id"), 1));
//		PublicReferenceImplementation impl = session.createQuery(criteria).uniqueResult();
	//
	//	
//		IndexReader reader = null;
	//	
//		//Create IndexSearcher from IndexDirectory
//		try {
//			Directory indexDirectory = FSDirectory.open(Paths.get(((FileSystemImplementationProvider)DataManager.
//					getImplProv()).getIndexDirectory().toString(),"Master_Index"));
//			reader = DirectoryReader.open(indexDirectory);
//		} catch (IOException e) {
//			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
//			.debug(e.getMessage()+" \n (tried to open FSDirectory/creating IndexReader)");
//			e.printStackTrace();
//		}
//		//to be sure it's not a File
//		String hql = "from PrimaryDataFileImplementation where id = :fileId";
//		PrimaryDataFileImplementation test = session.createQuery(hql,PrimaryDataFileImplementation.class).setParameter("fileId", impl.getVersion().getPrimaryEntityId()).uniqueResult();
//		if(!test.isDirectory()) {
//			//index
//			int o = 1;
//		}else {
//			
//			
//			IndexSearcher searcher = new IndexSearcher(reader);	
//			//Search Documents with Parsed Query
//			QueryParser parser = new QueryParser(MetaDataImplementation.VERSIONID, new StandardAnalyzer());
//			
//			hql = "from PrimaryDataFileImplementation s where s.parentDirectory = :dir";
//			List<PrimaryDataFileImplementation> directory = session.createQuery(hql,PrimaryDataFileImplementation.class)
//					.setParameter("dir", impl.getVersion().getEntity())
//					.list();
//			Stack<PrimaryDataFileImplementation> stack = new Stack<>();
//			for(PrimaryDataFileImplementation file : directory) {
//				if(file.isDirectory()) {
//					stack.add(file);
//				}else {
//					//this.implementationProviderLogger.info("Starting execute Index task: lastIndexedID: " + lastIndexedID);
//					CriteriaQuery<PrimaryDataEntityVersionImplementation> versionCriteria = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
//					Root<PrimaryDataEntityVersionImplementation> versionRoot = versionCriteria.from(PrimaryDataEntityVersionImplementation.class);
//					versionCriteria.where(criteriaBuilder.equal(versionRoot.get("primaryEntityId"), file.getID()));
//					versionCriteria.orderBy(criteriaBuilder.desc(versionRoot.get("revision")));
//					List<PrimaryDataEntityVersionImplementation> versions = session.createQuery(versionCriteria).setMaxResults(1).list();
//					for(PrimaryDataEntityVersionImplementation version : versions) {
//				    org.apache.lucene.search.Query luceneQuery = parser.parse(Integer.toString(version.getId()));
//				    ScoreDoc[] hits2;
//					try {
//						hits2 = searcher.search(luceneQuery, 1).scoreDocs;
//				        for(int i = 0; i < hits2.length; i++) {
//				        	Document doc = searcher.doc(hits2[i].doc);
//				        	
//				        	//versionIDList.add(Integer.parseInt(doc.get("versionID")));
//				        }
//					} catch (IOException e) {
//						e.printStackTrace();
//					}		
//					}
//				}
//			}
//			while(!stack.isEmpty()) {
//				PrimaryDataFileImplementation dir = stack.pop();
//				
//				
//				//index current Version of dir
//				List<PrimaryDataFileImplementation> files = session.createQuery(hql)
//						.setParameter("dir", dir)
//						.list();
//				for(PrimaryDataFileImplementation file : files) {
//					if(file.isDirectory()) {
//						stack.add(file);
//					}else {
//						//indexen
//					}
//				}
//			}
//		}
		

		
		
		/**
		 * ScrollableResults will avoid loading too many objects in memory
		 */
		//MetaData searchable = inserter.getSearchable();
		//List<PrimaryDataEntity> en = rootDirectory.searchByMetaData(DataManager.getImplProv().createMetaDataInstance(), false, false);
		//testSearchByDublin(rootDirectory);
		//testMetaDataSearch(rootDirectory, searchable);
		//DataManager.shutdown();
		//testKeyword();
	}
	
	private static void testKeyword(PrimaryDataDirectory rootDirectory) throws Exception {
		long start = System.currentTimeMillis();
    	try {
        	List<PrimaryDataEntity> results =  rootDirectory.searchByKeyword("Torben AND Beispiel_Titel", false, true);
        	for(PrimaryDataEntity entity : results) {
    			//log("\n\n#### Entity: "+entity.toString()+" ####");
//        		for(EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
//        			log(element.toString()+": "+entity.getMetaData().getElementValue(element));
//        		}
//        		SortedSet<PrimaryDataEntityVersion> versions = entity.getVersions();
//    			log("######## Entity Versions: ########\n");
//        		for(PrimaryDataEntityVersion version : versions) {
//        			List<PublicReference> refs = version.getPublicReferences();
//	            	for(PublicReference ref : refs) {
//	            		log(" Reference: "+ref.toString());
//	            		log(ref.getPublicationStatus().toString());
//	            	}
//	    			MetaData temp = version.getMetaData();
//	    			log("######## Entity: ########\n");
//		    		for(EnumDublinCoreElements element : EnumDublinCoreElements.values()){
//		    			//if(temp.getElementValue(element) != null && element.equals(EnumDublinCoreElements.CREATOR))
//		    				log(element.toString()+": "+temp.getElementValue(element).toString());
//		    		}
//        		}
//        		if(((NaturalPerson)((Persons)entity.getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).iterator().next()).getGivenName().equals("Orben")){
        		entity.addPublicReference(PersistentIdentifier.DOI);
        		entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress("ralfs@ipk-gatersleben.de"));
//        		Thread.sleep(40000);
//        			log("###########found zoe and testing_dir");
//        		}
        	}
        	log("\n#### Result Size: "+results.size()+" ####\n");
        	log(" TIME: "+(System.currentTimeMillis()-start));
    	}catch(Exception e) {
    		log(e.getMessage());;
    	}
	}
	
	public static PrimaryDataDirectory getRoot() throws Exception{
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"), new InternetAddress("ralfs@ipk-gatersleben.de"),
				new InternetAddress("ralfs@ipk-gatersleben.de"),"imap.ipk-gatersleben.de","","");
		configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		return rootDirectory;
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
    
    static void testMetaDataSearch(PrimaryDataDirectory rootDirectory, MetaData metaData) throws Exception {
		for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
			log("Searched MEtadata key: "+element.toString()+" value: "+metaData.getElementValue(element));
		}
    	List<PrimaryDataEntity> results =  rootDirectory.searchByMetaData(metaData, false, true);
    	for(PrimaryDataEntity primEntity : results) {
    		MetaData curMetaData = primEntity.getMetaData();
	    	if(!results.isEmpty()) {
				for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
					log("Found MEtadata key: "+element.toString()+" value: "+curMetaData.getElementValue(element));
				}
	    	}else {
	    		log("No identical Metadata found!");
	    	}
    	}
    }
    
    private static void testSearchByDublin(PrimaryDataDirectory rootDirectory) throws Exception {
		ArrayList<PrimaryDataFile> entities = StoreDataScript.process(rootDirectory,1);
		MetaData storedMetaData = entities.get(0).getMetaData();
		//Test Search by Title
		String expected = storedMetaData.getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\nTITLE\nINSERTED -> Title: "+expected);
		Thread.sleep(4000);
		List<PrimaryDataEntity> results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.TITLE,
				new UntypedData(expected), false, true);
		String actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.TITLE).getString();
		log("\n___FOUND -> Title: "+actual);
		
		//Test Search by Description
		expected = storedMetaData.getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		log("\nDESCRIPTION\nINSERTED -> Description: "+expected);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DESCRIPTION,
				new UntypedData(expected), false, true);
    	for(PrimaryDataEntity primEntity : results1) {
    		MetaData curMetaData = primEntity.getMetaData();
				for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
					log("Found MEtadata key: "+element.toString()+" value: "+curMetaData.getElementValue(element));
				}
    	}
		
		actual = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DESCRIPTION).getString();
		log("\n___FOUND ->  Description: "+actual);
		//Test Search by Creator
		NaturalPerson expPerson = (NaturalPerson) ((Persons)storedMetaData.getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		log("\nCreator\nINSERTED -> Creator: "+expPerson.toString());
		Persons persons = new Persons();
		persons.add(expPerson);
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CREATOR,
				expPerson, false, true);
		NaturalPerson actPerson = (NaturalPerson) ((Persons)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.CREATOR)).getPersons().iterator().next();
		log("\nCreator\nFOUND -> Creator: "+actPerson.toString());
		//Test for Publisher
		LegalPerson expLp = storedMetaData.getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nINSERTED: ->"+expLp.toString());
		results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.PUBLISHER, expLp, true, true);
		LegalPerson actLp = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.PUBLISHER);
		log("\nPublisher\nFOUND: ->"+expLp.toString());
		log("\n___FOUND ->  Publisher: "+actLp.toString());
			//Test for Identifier
			Identifier expIdent = storedMetaData.getElementValue(EnumDublinCoreElements.IDENTIFIER);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.IDENTIFIER,expIdent, false, true);
			Identifier actIdent = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.IDENTIFIER);
			
			CheckSumType expChecksum = ((CheckSum)storedMetaData.getElementValue(EnumDublinCoreElements.CHECKSUM)).iterator().next();
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.CHECKSUM, expChecksum, false, true);
			
			EdalLanguage expLang = (EdalLanguage)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.LANGUAGE, expLang, false, true);
			EdalLanguage actLang = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.LANGUAGE);
			
			//Datasize
			DataSize expSize = (DataSize)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SIZE, expSize, false, true);
			DataSize actSize = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SIZE);
			
//			Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
//			UntypedData expSubj = subjects.iterator().next();
//			log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
//			results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, true, true);
//			UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
//			log("\nFOUND -> Subject: "+actSubj.toString());
//			Assertions.assertEquals(expSubj, actSubj);
			try {
							
			//RELATION
				
				IdentifierRelation relation = (IdentifierRelation)entities.get(entities.size()-1).getMetaData().getElementValue(EnumDublinCoreElements.RELATION);
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.RELATION, relation, false, true);
				String searchedRelation = relation.getRelations().iterator().next().getIdentifier();
				String foundRelation =((IdentifierRelation)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.RELATION)).getRelations().iterator().next().getIdentifier();
				log("\n STORED: "+searchedRelation+"\n FOUND: "+foundRelation);
				
//				Calendar lastEvents = (Calendar)date.getStartDate().clone();
//				lastEvents.set(Calendar.HOUR, 9);
//				EdalDateRange range = new EdalDateRange(lastEvents, EdalDatePrecision.SECOND, date.getStartDate(), EdalDatePrecision.SECOND, actual);
//				log("\nManipluiertes Date: "+range.toString());
//				expdate = new DateEvents("dates");
//				expdate.add(range);
//				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
//				actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
//				for(EdalDate e : actDates)
//					log("\nDATE: "+e.toString());
				//DATE
				
//				DateEvents expdate = new DateEvents("dates");
//				expdate = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE));
//				EdalDateRange date = null;
//				while(expdate.iterator().hasNext()) {
//					EdalDate temp = expdate.iterator().next();
//					if(temp instanceof EdalDateRange) {
//						date = (EdalDateRange) temp;
//					}
//				}
				EdalDate date = ((DateEvents)storedMetaData.getElementValue(EnumDublinCoreElements.DATE)).iterator().next();
				DateEvents expdate = new DateEvents("dates");
				expdate.add(date);
				for(EdalDate e : expdate)
					log("\nSearched DATE ->"+e.toString());
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE, expdate, false, true);
				DateEvents actDates = results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.DATE);
				for(EdalDate e : actDates)
					log("\nFOUND DATE ->"+e.toString());
				log("result: "+actDates.compareTo(expdate));
//				expdate = storedMetaData.getElementValue(EnumDublinCoreElements.DATE);
				
				//results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.DATE);
				//Test for Identifier
				Subjects subjects = (Subjects)storedMetaData.getElementValue(EnumDublinCoreElements.SUBJECT);
				UntypedData expSubj = subjects.iterator().next();
				log("\nSubjects\nFOUND -> Subject: "+expSubj.toString());
				results1 = rootDirectory.searchByDublinCoreElement(EnumDublinCoreElements.SUBJECT, expSubj, false, true);
				UntypedData actSubj = ((Subjects)results1.get(0).getMetaData().getElementValue(EnumDublinCoreElements.SUBJECT)).getSubjects().iterator().next();
				log("\nFOUND -> Subject: "+actSubj.toString());

			}catch(Exception e) {
				e.printStackTrace();
				log("\n####FAIL###### ----------->"+e.getMessage());
			}
			DataManager.shutdown();
    }
}