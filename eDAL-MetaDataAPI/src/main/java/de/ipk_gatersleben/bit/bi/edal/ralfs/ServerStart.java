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
package de.ipk_gatersleben.bit.bi.edal.ralfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.uhighlight.UnifiedHighlighter;
import org.apache.lucene.search.uhighlight.WholeBreakIterator;
import org.apache.lucene.util.BytesRef;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReferenceException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDCMIDataType;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelatedIdentifierType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.datacite.xml.types.RelationType;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class ServerStart {
	
	
	private static int HTTPS_PORT = 8443;
	private static int HTTP_PORT = 8080;
	private static final String ROOT_USER = "ralfs@ipk-gatersleben.de";
	private static final String EMAIL = "ralfs@ipk-gatersleben.de";
	private static final String DATACITE_PREFIX = "10.5072";
	private static final String DATACITE_PASSWORD = "";
	private static final String DATACITE_USERNAME = "";
	
	private static Path PATH = Paths.get(System.getProperty("user.home"), "TEST_DATASET");

	public static void main(String[] args) {
		try {
			PrimaryDataDirectory root = getRoot();
//			Thread.sleep(2000);
//			IndexSearcher searcher = DataManager.getSearchManager().acquire();
//			ScoreDoc[] hits = searcher.search(new TermQuery(new Term("Content","test")), 500000).scoreDocs;
//			int fgdf = 0;
//			for(ScoreDoc doc : hits) {
//				Document doc2 = searcher.doc(doc.doc);
//				for(String content : doc2.getValues("Content")) {
//					DataManager.getImplProv().getLogger().info("start "+content.substring(0, 80));
//					DataManager.getImplProv().getLogger().info("end "+content.substring(content.length()-80, content.length()));
//				}
//
//			
//			}
			
			//hightlightExample();

//			createDataset(400);
		    //uploadZip(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void hightlightExample() throws IOException, InvalidTokenOffsetsException {
			IndexSearcher searcher = DataManager.getSearchManager().acquire();
			Query query = new TermQuery(new Term("Content", "m�use"));
			TopDocs hits = searcher.search(new TermQuery(new Term("Content", "m�use")), 500000);
			Analyzer analyzer = ((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter()
					.getAnalyzer();
//			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
//			Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
//			for (int i = 0; i < hits.totalHits.value; i++) {
//				int id = hits.scoreDocs[i].doc;
////				TermsEnum enum2 =searcher.getIndexReader().getTermVector(id, "Content").iterator();
////				enum2.seekExact(new BytesRef("organic"));	
////				PostingsEnum posting = enum2.postings(null, PostingsEnum.FREQS);
////				int freq = posting.freq();
//				
//				
//				Document doc = searcher.doc(id);
//				System.out.println(("Doc title: "+doc.get(MetaDataImplementation.TITLE)));
//				String text = doc.get("Content");
//				TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), id, "Content",
//						analyzer);
//				TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);// highlighter.getBestFragments(tokenStream,
//																										// text, 3,
//																										// "...");
//				for (int j = 0; j < frag.length; j++) {
//					if ((frag[j] != null) && (frag[j].getScore() > 0)) {
//						System.out.println(("FRAGMENT--"));
//						System.out.println((frag[j].toString()));
//					}
//				}
//			}
//			UnifiedHighlighter unifiedHighlighter = new UnifiedHighlighter(searcher, analyzer) {
//				@Override
//				protected BreakIterator getBreakIterator(String field) {
//					return new WholeBreakIterator();
//				}
//			};
			UnifiedHighlighter unifiedHighlighter = new UnifiedHighlighter(searcher, analyzer);
			String[] snipets = unifiedHighlighter.highlight("Content", query, hits);
			String title = "NameV1.2";
			for(String snipet : snipets) {
//				title+=0;
//			    FileOutputStream outputStream = new FileOutputStream(title);
//			    byte[] strToBytes = snipet.getBytes();
//			    outputStream.write(strToBytes);
//
//			    outputStream.close();
				System.out.println("Snipet: ");
				System.out.println(snipet);
			}
			DataManager.getImplProv().getLogger().info("Snippets: "+snipets.length);
	}

	public static PrimaryDataDirectory getRoot() throws Exception {
		EdalConfiguration configuration = new EdalConfiguration(DATACITE_USERNAME,
				DATACITE_PASSWORD, DATACITE_PREFIX,
				new InternetAddress(EMAIL),
				new InternetAddress(EMAIL),
				new InternetAddress(EMAIL),
				new InternetAddress(ROOT_USER)
				,"imap.ipk-gatersleben.de","","");
		configuration.setDataPath(Paths.get("d:\\edal"));
		configuration.setMountPath(Paths.get("d:\\edal"));
		configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		return rootDirectory;
	}
	
	static void createDataset(int copies) throws IOException {
		File dir = PATH.toFile();
		if(!dir.exists()) {
			dir.mkdir();
		}		
		FileWriter myWriter = new FileWriter(Paths.get(PATH.toString(), "test.txt").toString());
		BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
		String s = "This is a test for indexing large text data.";
		int size = (500*1024*1024)/s.getBytes().length;
		for(int i = 0; i < size; i++) {
			bufferedWriter.write(s);
		}
		bufferedWriter.write("last words");	
		myWriter.close();
		for(int i = 0; i < copies; i++) {
			Files.copy(Paths.get(PATH.toString(), "test.txt"), Paths.get(PATH.toString(), "copy_"+i+".txt"),StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static void uploadZip(PrimaryDataDirectory currentDirectory)
			throws MetaDataException, PrimaryDataEntityVersionException, PrimaryDataFileException,
			PrimaryDataDirectoryException, CloneNotSupportedException, PrimaryDataEntityException, AddressException,
			PublicReferenceException, IOException {
		PrimaryDataDirectory entity = currentDirectory.createPrimaryDataDirectory("Individual_Bayesian_gene_trees_from_245_quality-filtered_loci 2");
		MetaData metadata = entity.getMetaData().clone();
		Persons persons = new Persons();
		NaturalPerson np = new NaturalPerson("Torben", "Rlafs",
				"2Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Seeland OT Gatersleben, Corrensstra�e 3",
				"06466", "Germany");
		persons.add(np);
		metadata.setElementValue(EnumDublinCoreElements.CREATOR, persons);
		metadata.setElementValue(EnumDublinCoreElements.PUBLISHER,
				new LegalPerson("e!DAL - Plant Genomics and Phenomics Research Data Repository (PGP)",
						"IPK Gatersleben, Seeland OT Gatersleben, Corrensstra�e 3", "06466", "Germany"));

		Subjects subjects = new Subjects();
		subjects.add(new UntypedData("wheat"));
		subjects.add(new UntypedData("transcriptional network"));
		subjects.add(new UntypedData("genie3"));
		subjects.add(new UntypedData("transcriptomics"));
		EdalLanguage lang = new EdalLanguage(Locale.ENGLISH);
		metadata.setElementValue(EnumDublinCoreElements.LANGUAGE, lang);
		metadata.setElementValue(EnumDublinCoreElements.SUBJECT, subjects);
		metadata.setElementValue(EnumDublinCoreElements.TITLE,
				new UntypedData("Individual_Bayesian_gene_trees_from_245_quality-filtered_loci 3"));
		metadata.setElementValue(EnumDublinCoreElements.DESCRIPTION, new UntypedData(
				"This file contains the detailed results of the gen34ie3 analysis for wheat gene expression67 networks. The result of the genie3 network construction are stored in a R data object containing a matrix with target genes in columns and transcription factor genes in rows. One folder provides GO term enrichments of the biological process category for each transcription factor. A second folder provides all transcription factors associated with each GO term."));
		entity.setMetaData(metadata);
		Path pathToRessource = Paths.get(System.getProperty("user.home") + File.separator + "TEST_DATASET");
		EdalDirectoryVisitorWithMetaData edalVisitor = new EdalDirectoryVisitorWithMetaData(entity, pathToRessource,
				metadata, true);
		Files.walkFileTree(pathToRessource, edalVisitor);
		entity.addPublicReference(PersistentIdentifier.DOI);
		entity.getCurrentVersion().setAllReferencesPublic(new InternetAddress("ralfs@ipk-gatersleben.de"));

	}

}
