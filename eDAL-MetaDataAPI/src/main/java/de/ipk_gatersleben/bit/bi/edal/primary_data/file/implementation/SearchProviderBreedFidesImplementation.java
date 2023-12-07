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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager.SearcherAndTaxonomy;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.ipk_gatersleben.bit.bi.edal.breedfides.rest.EdalFunctions;
import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.SearchProviderBreedFides;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Person;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Persons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Subjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

public class SearchProviderBreedFidesImplementation implements SearchProviderBreedFides {

	@Override
	@SuppressWarnings("unchecked")
	public JSONArray getFacets(String query) {

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
		return null;

	}

	public HashSet<PrimaryDataDirectory> getAllUserDatasets(List<PrimaryDataDirectory> userDirectories) {

		HashSet<PrimaryDataDirectory> userDatasets = new HashSet<>();

		Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		for (PrimaryDataDirectory userDirectory : userDirectories) {

			CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
					.createQuery(PrimaryDataDirectoryImplementation.class);

			Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
					.from(PrimaryDataDirectoryImplementation.class);

			directoryCriteria.where(
					builder.and(builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class)),
					builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
							userDirectory));

			List<PrimaryDataDirectoryImplementation> list = session.createQuery(directoryCriteria).list();

			userDatasets.addAll(list);

		}
		session.close();

		return userDatasets;
	}

	public HashSet<Integer> getVersionListWithHitsFromIndex(String keyword, boolean fuzzy) {

		HashSet<Integer> versionIdList = new HashSet<Integer>();

		BooleanQuery.setMaxClauseCount(10000);

		IndexReader reader = null;

		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(
					((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
					FileSystemImplementationProvider.MASTER_INDEX)));
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().error("Error opening the Lucene Index: " + e.getMessage());
		}

		IndexSearcher searcher = new IndexSearcher(reader);

		String[] fields = { EnumIndexField.TITLE.value(), EnumIndexField.DESCRIPTION.value(),
				EnumIndexField.COVERAGE.value(), EnumIndexField.IDENTIFIER.value(), EnumIndexField.SIZE.value(),
				EnumIndexField.TYPE.value(), EnumIndexField.LANGUAGE.value(), EnumIndexField.CREATOR.value(),EnumIndexField.CONTRIBUTOR.value(),
				EnumIndexField.LEGALPERSON.value(), EnumIndexField.ALGORITHM.value(), EnumIndexField.CHECKSUM.value(),
				EnumIndexField.SUBJECT.value(), EnumIndexField.RELATION.value(), EnumIndexField.MIMETYPE.value(),
				EnumIndexField.STARTDATE.value(), EnumIndexField.ENDDATE.value() };

		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());

		parser.setDefaultOperator(QueryParser.OR_OPERATOR);

		org.apache.lucene.search.Query luceneQuery = null;
		try {
			if (fuzzy) {
				luceneQuery = parser.parse(keyword + SearchProviderImplementation.TILDE);
			} else {
				luceneQuery = parser.parse(keyword);
			}
		} catch (ParseException e) {
			DataManager.getImplProv().getLogger().error("Lucene parsing error: " + e.getMessage());
		}

		try {
			ScoreDoc[] hits = searcher.search(luceneQuery, 50000).scoreDocs;

			versionIdList = new HashSet<Integer>();
			
			System.out.println(hits.length);

			for (int i = 0; i < hits.length; i++) {

				Document doc = searcher.doc(hits[i].doc);

				versionIdList.add(Integer.parseInt(doc.get(EnumIndexField.VERSIONID.value())));

			}
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().info("IO error while searching: " + e.getMessage());
		}

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.info("Found in the index: " + versionIdList.size() + " values");

		return versionIdList;
	}

	@Override
	public String breedFidesKeywordSearch(PrimaryDataDirectory searchDirectory, String keyword, boolean fuzzy,
			boolean recursiveIntoSubdirectories, int page, int pageSize) throws Exception {

		PrimaryDataDirectory rootDirectory = EdalFunctions.getRootDirectory();

		List<PrimaryDataDirectory> userDirectories = EdalFunctions.getUserDirectories(rootDirectory);

		HashSet<PrimaryDataDirectory> userDatasets = getAllUserDatasets(userDirectories);

		HashSet<Integer> versionIds = getVersionListWithHitsFromIndex(keyword, fuzzy);

		final HashSet<String> versionsIds = new HashSet<String>();
		final HashSet<PrimaryDataDirectory> resultSet = new HashSet<PrimaryDataDirectory>();

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		for (final Integer version : versionIds) {
			versionsIds.add(session.get(PrimaryDataEntityVersionImplementation.class, version).getPrimaryEntityId());
		}
		session.close();

		for (PrimaryDataDirectory dataset : userDatasets) {
			if (versionsIds.contains(dataset.getID())) {

				resultSet.add(dataset);
			}
		}

		String jsonString = createMetadataJson(page, pageSize, resultSet);

		return jsonString;
	}

	private String createMetadataJson(int page, int pageSize, final HashSet<PrimaryDataDirectory> resultSet)
			throws MetaDataException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		ObjectNode rootNode = objectMapper.createObjectNode();

		rootNode.set("pagination",
				createPaginationNode(rootNode, page, pageSize, resultSet.size(), (resultSet.size() / pageSize)));

		ArrayNode resultsArrayNode = rootNode.arrayNode();

		rootNode.set("results", resultsArrayNode);

		for (PrimaryDataEntity entity : resultSet) {

			MetaData metadata = entity.getMetaData();

			ObjectNode resultNode = rootNode.objectNode();

			resultNode.put("datasetRoot", metadata.getElementValue(EnumDublinCoreElements.TITLE).toString());
			try {
				resultNode.put("datasetOwner", EdalFunctions.getParentDirectory(entity).getName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			ObjectNode metadataNode = resultNode.objectNode();

			resultNode.set("metadata", metadataNode);

			metadataNode.put("title", metadata.getElementValue(EnumDublinCoreElements.TITLE).toString());
			metadataNode.put("description", metadata.getElementValue(EnumDublinCoreElements.DESCRIPTION).toString());
			metadataNode.put("language", metadata.getElementValue(EnumDublinCoreElements.LANGUAGE).toString());
			metadataNode.put("license", metadata.getElementValue(EnumDublinCoreElements.RIGHTS).toString());
			metadataNode.put("size", metadata.getElementValue(EnumDublinCoreElements.SIZE).toString());

			ArrayNode subjectsArrayNode = resultNode.arrayNode();
			Subjects subjects = metadata.getElementValue(EnumDublinCoreElements.SUBJECT);
			for (UntypedData subject : subjects.getSubjects()) {
				subjectsArrayNode.add(subject.toString());
			}
			metadataNode.set("subjects", subjectsArrayNode);

			ArrayNode authorsArrayNode = resultNode.arrayNode();

			Persons creators = metadata.getElementValue(EnumDublinCoreElements.CREATOR);
			for (Person person : creators.getPersons()) {
				ObjectNode creatorNode = authorsArrayNode.objectNode();

				if (person instanceof NaturalPerson) {
					creatorNode.put("firstName", ((NaturalPerson) person).getGivenName());
					creatorNode.put("lastName", ((NaturalPerson) person).getSureName());
					if (((NaturalPerson) person).getOrcid() != null) {
						creatorNode.put("orcid", ((NaturalPerson) person).getOrcid().toString());
					}
				} else if (person instanceof LegalPerson) {
					creatorNode.put("legalName", ((LegalPerson) person).getLegalName());
				}

				creatorNode.put("zip", person.getZip());
				creatorNode.put("country", person.getCountry());
				creatorNode.put("address", person.getAddressLine());
				creatorNode.put("role", "Creator");
				authorsArrayNode.add(creatorNode);

			}
			Persons contributors = metadata.getElementValue(EnumDublinCoreElements.CONTRIBUTOR);
			for (Person person : contributors.getPersons()) {
				ObjectNode contributorNode = authorsArrayNode.objectNode();

				if (person instanceof NaturalPerson) {
					contributorNode.put("firstName", ((NaturalPerson) person).getGivenName());
					contributorNode.put("lastName", ((NaturalPerson) person).getSureName());
					if (((NaturalPerson) person).getOrcid() != null) {
						contributorNode.put("orcid", ((NaturalPerson) person).getOrcid().toString());
					}
				} else if (person instanceof LegalPerson) {
					contributorNode.put("legalName", ((LegalPerson) person).getLegalName());
				}

				contributorNode.put("zip", person.getZip());
				contributorNode.put("country", person.getCountry());
				contributorNode.put("address", person.getAddressLine());
				contributorNode.put("role", "Contributor");
				authorsArrayNode.add(contributorNode);

			}

			metadataNode.set("authors", authorsArrayNode);
			resultsArrayNode.add(resultNode);

		}

		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
	}

	private ObjectNode createPaginationNode(ObjectNode rootNode, int currentPage, int pageSize, int totalCount,
			int totalPages) {

		ObjectNode pagination = rootNode.objectNode();

		pagination.put("currentPage", currentPage);
		pagination.put("pageSize", pageSize);
		pagination.put("totalCount", totalCount);
		pagination.put("totalPages", totalPages);

		return pagination;

	}

	@Override
	public String getAllUserDatasets(int page, int pageSize) throws Exception {

		PrimaryDataDirectory rootDirectory = EdalFunctions.getRootDirectory();

		List<PrimaryDataDirectory> userDirectories = EdalFunctions.getUserDirectories(rootDirectory);

		HashSet<PrimaryDataDirectory> userDatasets = getAllUserDatasets(userDirectories);

		return createMetadataJson(page, pageSize, userDatasets);

	}

	@Override
	public boolean checkIfEntityIsDataset(String id) {

		PrimaryDataDirectory rootDirectory = EdalFunctions.getRootDirectory();

		List<PrimaryDataDirectory> userDirectories = EdalFunctions.getUserDirectories(rootDirectory);

		HashSet<PrimaryDataDirectory> userDatasets = getAllUserDatasets(userDirectories);

		for (PrimaryDataDirectory primaryDataDirectory : userDatasets) {
			if (primaryDataDirectory.getID().equals(id)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean indexNewEntity(PrimaryDataEntity entity) {
		
		BreedFidesIndexWriter breedFidesIndexWriter = ((FileSystemImplementationProvider)DataManager.getImplProv()).getBreedFidesIndexWriter();

		breedFidesIndexWriter.executeIndexing(entity);
		
		return false;
	}

}
