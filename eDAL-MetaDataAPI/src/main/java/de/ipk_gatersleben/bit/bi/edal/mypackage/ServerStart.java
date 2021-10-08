package de.ipk_gatersleben.bit.bi.edal.mypackage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.MetaDataImplementation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PublicVersionIndexWriterThread;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class ServerStart {

	public static void main(String[] args) throws IOException, ParseException {
		try {
			PrimaryDataDirectory root = getRoot();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		IndexSearcher searcher = DataManager.getSearchManager().acquire();
//		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(Paths.get(((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexDirectory().toString(),"Facets")));
//		FacetsConfig config = new FacetsConfig();
//		config.setMultiValued(MetaDataImplementation.CREATORNAME, true);
//		config.setMultiValued(MetaDataImplementation.CONTRIBUTORNAME, true);
//		config.setMultiValued(MetaDataImplementation.SUBJECT, true);
//		config.setMultiValued(MetaDataImplementation.TITLE, true);
//		config.setMultiValued(MetaDataImplementation.DESCRIPTION, true);
//	    
//	    CharArraySet defaultStopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
//		final CharArraySet stopSet = new CharArraySet(
//				FileSystemImplementationProvider.STOPWORDS.size() + defaultStopWords.size(), false);
//		stopSet.addAll(defaultStopWords);
//		stopSet.addAll(FileSystemImplementationProvider.STOPWORDS);
//		StandardAnalyzer analyzer = new StandardAnalyzer(stopSet);
//	    QueryParser parser = new QueryParser(MetaDataImplementation.CREATORNAME, analyzer);
//		parser.setDefaultOperator(Operator.AND);
//
//
//	    // MatchAllDocsQuery is for "browsing" (counts facets
//	    // for all non-deleted docs in the index); normally
//	    // you'd use a "normal" query:
//	    Query q  = new TermQuery(
//				new Term(MetaDataImplementation.ENTITYTYPE, PublicVersionIndexWriterThread.PUBLICREFERENCE));
//	    String query = q+" "+MetaDataImplementation.CREATORNAME+":Daniel Arend";
//	    DrillDownQuery drillQuery = new DrillDownQuery(config, q);
//	    FacetsCollector fc = new FacetsCollector();
//	    FacetsCollector.search(searcher,drillQuery, 50000, fc);
//
//	    // Retrieve results
//	    List<FacetResult> results = new ArrayList<>();
//
//	    // Count both "Publish Date" and "Author" dimensions
//	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
//	    results.add(facets.getTopChildren(150, MetaDataImplementation.CREATORNAME));
//	    results.add(facets.getTopChildren(150, MetaDataImplementation.CONTRIBUTORNAME));
//	    results.add(facets.getTopChildren(150, MetaDataImplementation.SUBJECT));
//	    results.add(facets.getTopChildren(10, MetaDataImplementation.TITLE));
//	    results.add(facets.getTopChildren(150, MetaDataImplementation.DESCRIPTION));
//	    DataManager.getImplProv().getLogger().info(results.get(0).childCount);
//	    DataManager.getImplProv().getLogger().info(results.get(0));
//	    DataManager.getImplProv().getLogger().info(results.get(1).childCount);
//	    DataManager.getImplProv().getLogger().info(results.get(1));
//	    DataManager.getImplProv().getLogger().info(results.get(2).childCount);
//	    DataManager.getImplProv().getLogger().info(results.get(2));
//	    DataManager.getImplProv().getLogger().info(results.get(3).childCount);
//	    DataManager.getImplProv().getLogger().info(results.get(3));
//	    DataManager.getImplProv().getLogger().info(results.get(4).childCount);
//	    DataManager.getImplProv().getLogger().info(results.get(4));
	}

	public static PrimaryDataDirectory getRoot() throws Exception{
		EdalConfiguration configuration = new EdalConfiguration("", "", "10.5072",
				new InternetAddress("ralfs@erics.smtp.2805"),
				new InternetAddress("ralfs@erics.smtp.2805"), new InternetAddress("ralfs@erics.smtp.2805"),
				new InternetAddress("ralfs@erics.smtp.2805"),"localhost","","");
		configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);
		PrimaryDataDirectory rootDirectory = DataManager.getRootDirectory(
				EdalHelpers.getFileSystemImplementationProvider(false, configuration),
				EdalHelpers.authenticateWinOrUnixOrMacUser());
		return rootDirectory;
	}

}
