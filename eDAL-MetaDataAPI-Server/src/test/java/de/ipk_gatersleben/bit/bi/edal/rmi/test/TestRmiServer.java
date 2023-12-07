/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager.SearcherAndTaxonomy;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.TopDocs;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfigurationException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.EnumIndexField;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.FileSystemImplementationProvider;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.EdalServer;

public class TestRmiServer {

	public static void main(String[] args) throws AddressException, EdalConfigurationException {
		EdalConfiguration configuration = new EdalConfiguration("dummy", "dummy", "10.5072",
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("arendd@ipk-gatersleben.de"),
				new InternetAddress("arendd@ipk-gatersleben.de"), new InternetAddress("eDAL0815@ipk-gatersleben.de"),
				"imap.ipk-gatersleben.de", "", "");

		configuration.setUseSSL(false);
		configuration.setHibernateIndexing(EdalConfiguration.NATIVE_LUCENE_INDEXING);

		configuration.setPublisherURL("https://www.ipk-gatersleben.de");

		configuration.setStaticServerAddress("localhost");
		configuration.setStaticServerPort(80);

		EdalServer.startServer(configuration, EdalServer.DEFAULT_REGISTRY_PORT, EdalServer.DEFAULT_DATA_PORT, false,
				false);
		
		
		
		
//		drillDownNative("+Creator:manuel AND +EntityType:dataset");
//		drillDownPublic("+Creator:manuel +EntityType:dataset");
//		drillDownNative("+Creator:arend +EntityType:dataset");
//		drillDownPublic("+Creator:arend +EntityType:dataset");
//		drillDownNative("+Creator:manuel");
//		drillDownPublic("+Creator:manuel +EntityType:dataset");
//		drillDownNative("+Creator:arend");
//		drillDownPublic("+Creator:arend");
		

	}

	public static void drillDownNative(String query) {
		
		System.out.println("DrillDown Native Facets: " + query);

		try {
			QueryParser queryParser = new QueryParser(EnumIndexField.ALL.value(),
					((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer());
			queryParser.setDefaultOperator(Operator.AND);
			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider) DataManager.getImplProv())
					.getSearcherTaxonomyManagerForNative().acquire();

			FacetsConfig config = ((FileSystemImplementationProvider) DataManager.getImplProv()).getFacetsConfig();
			DrillDownQuery drillQuery = new DrillDownQuery(config, queryParser.parse(query));
			FacetsCollector fc = new FacetsCollector();
			FacetsCollector.search(manager.searcher, drillQuery, 50000, fc);

			TopDocs tds = FacetsCollector.search(manager.searcher, drillQuery, 50000, fc);

			System.out.println(tds.totalHits);

			List<FacetResult> results = new ArrayList<>();

			try {
				Facets facets = new FastTaxonomyFacetCounts(manager.taxonomyReader, config, fc);
				results.add(facets.getTopChildren(5000, EnumIndexField.CREATORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.CONTRIBUTORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.SUBJECT.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.TITLE.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.DESCRIPTION.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.FILETYPE.value()));

				System.out.println("++++");

				FacetResult fr = facets.getTopChildren(5000, EnumIndexField.CREATORNAME.value());
				if (fr != null) {
					for (LabelAndValue labelValue : fr.labelValues) {
						System.out.println(labelValue.label + "\t" + labelValue.value);
					}
				}
				System.out.println("++++");

			} catch (Exception e) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getSearcherTaxonomyManagerForNative()
						.release(manager);
			}
			((FileSystemImplementationProvider) DataManager.getImplProv()).getSearcherTaxonomyManagerForNative()
					.release(manager);

		} catch (IOException ioError) {
			DataManager.getImplProv().getLogger()
					.debug("Low level index error when retrieving Facets: " + ioError.getMessage());
		} catch (ParseException parserError) {
			DataManager.getImplProv().getLogger().debug("Parsing error occured: " + parserError.getMessage());
		}

	}

	public static void drillDownPublic(String query) {

		System.out.println("DrillDown Public Facets: " + query);

		try {
			QueryParser queryParser = new QueryParser(EnumIndexField.ALL.value(),
					((FileSystemImplementationProvider) DataManager.getImplProv()).getWriter().getAnalyzer());
			queryParser.setDefaultOperator(Operator.AND);

			SearcherAndTaxonomy manager = ((FileSystemImplementationProvider) DataManager.getImplProv())
					.getSearcherTaxonomyManagerForPublicReferences().acquire();

			FacetsConfig config = ((FileSystemImplementationProvider) DataManager.getImplProv()).getFacetsConfig();
			DrillDownQuery drillQuery = new DrillDownQuery(config, queryParser.parse(query));
			FacetsCollector fc = new FacetsCollector();
			TopDocs tds = FacetsCollector.search(manager.searcher, drillQuery, 50000, fc);

			System.out.println(tds.totalHits);

			List<FacetResult> results = new ArrayList<>();

			try {
				Facets facets = new FastTaxonomyFacetCounts(manager.taxonomyReader, config, fc);
				results.add(facets.getTopChildren(5000, EnumIndexField.CREATORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.CONTRIBUTORNAME.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.SUBJECT.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.TITLE.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.DESCRIPTION.value()));
				results.add(facets.getTopChildren(5000, EnumIndexField.FILETYPE.value()));

				System.out.println("++++");

				FacetResult fr = facets.getTopChildren(5000, EnumIndexField.CREATORNAME.value());
				if (fr != null) {
					for (LabelAndValue labelValue : fr.labelValues) {
						System.out.println(labelValue.label + "\t" + labelValue.value);
					}
				}
				FacetResult fr2 = facets.getTopChildren(5000, EnumIndexField.FILETYPE.value());
				if (fr2 != null) {
					for (LabelAndValue labelValue : fr2.labelValues) {
						System.out.println(labelValue.label + "\t" + labelValue.value);
					}
				}
				System.out.println("++++");

			} catch (Exception e) {
				e.printStackTrace();
				((FileSystemImplementationProvider) DataManager.getImplProv())
						.getSearcherTaxonomyManagerForPublicReferences().release(manager);
			}
			((FileSystemImplementationProvider) DataManager.getImplProv())
					.getSearcherTaxonomyManagerForPublicReferences().release(manager);

		} catch (IOException ioError) {
			DataManager.getImplProv().getLogger()
					.debug("Low level index error when retrieving Facets: " + ioError.getMessage());
		} catch (ParseException parserError) {
			DataManager.getImplProv().getLogger().debug("Parsing error occured: " + parserError.getMessage());
		}

	}
}
