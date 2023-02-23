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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.search.backend.lucene.LuceneBackend;
import org.hibernate.search.backend.lucene.LuceneExtension;
import org.hibernate.search.engine.backend.Backend;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.Identifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.IdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.LegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyCheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyCheckSumType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDataType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyLegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyNaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPersons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MySubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;

/**
 * Implementation of {@link PrimaryDataDirectory}.
 * 
 * @author arendd
 */

@Entity
@Table(name = "ENTITIES")
@DiscriminatorColumn(columnDefinition = "character varying(1)", name = "TYPE", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("D")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "PrimaryDataDirectory")
public class PrimaryDataDirectoryImplementation extends PrimaryDataDirectory {

	private static final String STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION = "Unable to switch to current version";

	public static final String CACHE_REGION_SEARCH_ENTITY = "search.entity";

	public static final String STRING_ID = "ID";

	private static final String STRING_PARENT_DIRECTORY = "parentDirectory";

	private static final String SUPPRESS_UNCHECKED_WARNING = "unchecked";

//	private long startTime;

	/**
	 * Maximal number of search result, if the number is higher the user must
	 * specify a more detailed search request, otherwise the search function is to
	 * slow because of HIBERNATE.
	 */
	private static final int MAX_NUMBER_SEARCH_RESULTS = 900000;

	private SortedSet<PrimaryDataEntityVersionImplementation> versionList;

	/**
	 * Default constructor for {@link PrimaryDataDirectoryImplementation} is
	 * necessary for PojoInstantiator of <em>HIBERNATE</em>.
	 */
	protected PrimaryDataDirectoryImplementation() {

	}

	/**
	 * Constructor for PrimaryDataDirectoryImplementation.
	 * 
	 * @param path a {@link PrimaryDataDirectory} object.
	 * @param name a {@link String} object.
	 * @throws PrimaryDataEntityVersionException if can not set current
	 *                                           {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException     if no parent
	 *                                           {@link PrimaryDataDirectory} is
	 *                                           found.
	 * @throws MetaDataException                 if the {@link MetaData} object of
	 *                                           the parent
	 *                                           {@link PrimaryDataDirectory} is not
	 *                                           clone-able
	 */
	public PrimaryDataDirectoryImplementation(final PrimaryDataDirectory path, final String name)
			throws PrimaryDataEntityVersionException, PrimaryDataDirectoryException, MetaDataException {
		super(path, name);
	}

	private PrimaryDataEntity checkIfEntityExists(final String name) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		session.setDefaultReadOnly(true);

		final NativeQuery<PrimaryDataFileImplementation> fileQuery = session
				.createNativeQuery(
						"SELECT DISTINCT t4.ID, t4.TYPE, t4.PARENTDIRECTORY_ID FROM "
								+ "UNTYPEDDATA t1, METADATA_MAP t2, ENTITY_VERSIONS t3, ENTITIES t4 "
								+ "where t3.METADATA_ID=t2.METADATA_ID " + "and t1.id=t2.MYMAP_ID and t2.MYMAP_KEY=15 "
								+ "and t1.STRING=:name and t3.PRIMARYENTITYID=t4.ID "
								+ "and t4.TYPE='F' and t4.PARENTDIRECTORY_ID=:parent",
						PrimaryDataFileImplementation.class);

		fileQuery.setParameter("name", name);
		fileQuery.setParameter("parent", this.getID());

		final List<PrimaryDataFileImplementation> files = fileQuery.list();

		for (final PrimaryDataFileImplementation file : files) {

			if (file.getName().equals(name)) {
				session.close();
				return file;
			}
		}

		final NativeQuery<PrimaryDataDirectoryImplementation> directoryQuery = session.createNativeQuery(
				"SELECT DISTINCT t4.ID, t4.TYPE, t4.PARENTDIRECTORY_ID FROM "
						+ "UNTYPEDDATA t1, METADATA_MAP t2, ENTITY_VERSIONS t3, ENTITIES t4 "
						+ "where t3.METADATA_ID=t2.METADATA_ID " + "and t1.id=t2.MYMAP_ID and t2.MYMAP_KEY=15 "
						+ "and t1.STRING=:name and t3.PRIMARYENTITYID=t4.ID "
						+ "and t4.TYPE='D' and t4.PARENTDIRECTORY_ID=:parent",
				PrimaryDataDirectoryImplementation.class);

		directoryQuery.setParameter("name", name);
		directoryQuery.setParameter("parent", this.getID());

		final List<PrimaryDataDirectoryImplementation> dirs = directoryQuery.list();

		for (final PrimaryDataDirectoryImplementation dir : dirs) {
			if (dir.getName().equals(name)) {
				session.close();
				return dir;
			}
		}
		session.close();
		return null;
	}

	/**
	 * Check if the parent {@link PrimaryDataDirectory} is a parent directory of the
	 * child {@link PrimaryDataEntity}
	 * 
	 * @param parent
	 * @param child
	 * @return true if the parent is a parentDirectory of the child object.
	 */
	private boolean checkIfParentEntity(final PrimaryDataDirectory parent, final PrimaryDataEntity child) {

		if (parent.getID().equals(child.getID())) {
			return true;
		} else {
			PrimaryDataDirectory grandParent = null;
			try {
				grandParent = child.getParentDirectory();
			} catch (final PrimaryDataDirectoryException e) {
				e.printStackTrace();
			}

			if (grandParent == null) {
				return false;
			}
			return this.checkIfParentEntity(parent, grandParent);
		}
	}

	/**
	 * Check if the query string consists any special character of the LUCENE query
	 * parser syntax.
	 * 
	 * @param query
	 * @return <em>true</em>
	 */
	private boolean consistsQueryParserSyntax(final String query) {
		if (query.contains("+") || query.contains("*") || query.contains("?") || query.contains("~")
				|| query.contains(":") || query.contains("{") || query.contains("}") || query.contains("[")
				|| query.contains("]") || query.contains("^") || query.contains("-") || query.contains(" not ")
				|| query.contains(" or ") || query.contains(" and ") || query.contains(" OR ")
				|| query.contains(" AND ") || query.contains(" NOT ")) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean existImpl(final String path) throws PrimaryDataDirectoryException {

		try {
			if (this.checkIfEntityExists(path) == null) {
				return false;
			}

		} catch (final Exception e) {
			throw new PrimaryDataDirectoryException("unable to check if the Entity exist", e);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em> HIBERNATE : constant length cause it is an {@link java.util.UUID}</em>
	 */
	@Override
	@Id
	@Column(columnDefinition = "character varying(40)")
	public String getID() {
		return super.getID();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em> HIBERNATE : FetchType.EAGER for getPath()</em>
	 */
	@Override
	@OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	public PrimaryDataDirectoryImplementation getParentDirectory() {
		try {
			return (PrimaryDataDirectoryImplementation) super.getParentDirectory();
		} catch (final PrimaryDataDirectoryException e) {
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	protected Map<Principal, List<EdalPermission>> getPermissionsImpl() throws PrimaryDataEntityException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<EdalPermissionImplementation> criteria = builder.createQuery(EdalPermissionImplementation.class);
		Root<EdalPermissionImplementation> root = criteria.from(EdalPermissionImplementation.class);

		criteria.where(builder.and(builder.equal(root.get("internId"), this.getID()),
				builder.equal(root.get("internVersion"), this.getCurrentVersion().getRevision())));

		final List<EdalPermissionImplementation> privatePerms = session.createQuery(criteria).list();

		final Map<Principal, List<EdalPermission>> publicMap = new HashMap<>();

		try {
			for (final EdalPermissionImplementation p : privatePerms) {

				if (!publicMap.containsKey(p.getPrincipal().toPrincipal())) {

					CriteriaQuery<EdalPermissionImplementation> tmpCriteria = builder
							.createQuery(EdalPermissionImplementation.class);
					Root<EdalPermissionImplementation> tmpRoot = tmpCriteria.from(EdalPermissionImplementation.class);

					tmpCriteria.where(builder.and(
							builder.and(builder.equal(tmpRoot.get("internId"), this.getID()),
									builder.equal(tmpRoot.get("internVersion"),
											this.getCurrentVersion().getRevision())),
							builder.equal(tmpRoot.get("principal"), p.getPrincipal())));

					final List<EdalPermissionImplementation> userPerms = session.createQuery(tmpCriteria).list();
					final List<EdalPermission> publicPerms = new ArrayList<EdalPermission>(privatePerms.size());

					for (final EdalPermissionImplementation permission : userPerms) {
						publicPerms.add(permission.toEdalPermission());
					}
					publicMap.put(p.getPrincipal().toPrincipal(), publicPerms);
				}
			}
		} catch (final Exception e) {
			session.close();
			throw new PrimaryDataEntityException("Unable to load permissions !", e);
		}
		session.close();

		return publicMap;
	}

	/** {@inheritDoc} */
	@Override
	protected PrimaryDataEntity getPrimaryDataEntityImpl(final String name) throws PrimaryDataDirectoryException {
		return this.checkIfEntityExists(name);
	}

	/**
	 * Getter for the field <code>versionList</code>.
	 * 
	 * @return a {@link SortedSet} object.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "primaryEntityId")
	@SortNatural
	protected SortedSet<PrimaryDataEntityVersionImplementation> getVersionList() {
		return this.versionList;
	}

	/** {@inheritDoc} */
	@Override
	@Transient
	protected SortedSet<PrimaryDataEntityVersion> getVersionsImpl() {

		if (this.getVersionList() == null) {
			return Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersion>());
		} else {
			return Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersion>(this.getVersionList()));
		}

	}

	/** {@inheritDoc} */
	@Override
	protected List<PrimaryDataEntity> listPrimaryDataEntitiesImpl(final Calendar currentVersionDate,
			final Calendar nextVersionDate) throws PrimaryDataDirectoryException {

		final ListThread thread = new ListThread(this, currentVersionDate, nextVersionDate);

		DataManager.getListExecutorService().execute(thread);

		return thread.getAsynchronList();

	}

	/** {@inheritDoc} */
	@Override
	protected void moveImpl(final PrimaryDataDirectory destinationDirectory) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final Transaction transaction = session.beginTransaction();

		this.setParentDirectory(destinationDirectory);

		session.update(this);
		transaction.commit();
		session.close();
	}

	/**
	 * Internal function to search for a {@link DataFormat}.
	 * 
	 * @param dataFormat
	 * @param element
	 * @return List<MyDataFormat>
	 * @throws ParseException                If unable to parse query string with
	 *                                       <em>LUCENE<em>.
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByDataFormat(final DataFormat dataFormat, EnumDublinCoreElements element,
			final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {
		if (dataFormat.getMimeType().equals("")) {
			return new ArrayList<Integer>();
		}

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			// org.apache.lucene.search.Query query = null;

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyDataFormat> searchQuery = null;

			if (fuzzy) {
				// query =
				// queryBuilder.keyword().fuzzy().onField("mimeType").matching(dataFormat.getMimeType())
				// .createQuery();
				searchQuery = ftSession.search(MyDataFormat.class)
						.where(f -> f.match().field("mimeType").matching(dataFormat.getMimeType()).fuzzy()).fetch(200);
			} else {
				// query =
				// queryBuilder.keyword().onField("mimeType").matching(dataFormat.getMimeType()).createQuery();
				searchQuery = ftSession.search(MyDataFormat.class)
						.where(f -> f.match().field("mimeType").matching(dataFormat.getMimeType())).fetch(200);
			}
			final List<MyDataFormat> untypedDataList = searchQuery.hits();

			session.close();

			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			String luceneString;
			if (fuzzy) {
				luceneString = dataFormat.getMimeType() + "~";
			} else {
				luceneString = dataFormat.getMimeType();
			}
			QueryParser parser = new QueryParser(EnumIndexField.MIMETYPE.value(), new StandardAnalyzer());
			org.apache.lucene.search.Query luceneQuery = parser.parse(QueryParser.escape(luceneString));
			ScoreDoc[] hits;
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}
	}

	/**
	 * Internal function to search for a {@link DataType}.
	 * <p>
	 * No fuzzy search possible, because it is an EnumValue
	 * 
	 * @param dataType
	 * @param element
	 * @return List<MyDataType>
	 * @throws ParseException
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByDataType(final DataType dataType, EnumDublinCoreElements element)
			throws ParseException, PrimaryDataDirectoryException {

		if (dataType.getDataType() == null) {
			return new ArrayList<Integer>();
		}

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyDataType> searchQuery = ftSession.search(MyDataType.class)
					.where(f -> f.match().field("string").matching(dataType.getDataType().toString())).fetch(200);
			final List<MyDataType> untypedDataList = searchQuery.hits();
			session.close();

			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			QueryParser parser = new QueryParser(EnumIndexField.TYPE.value(), new StandardAnalyzer());
			org.apache.lucene.search.Query luceneQuery = parser.parse(dataType.getDataType().toString());
			ScoreDoc[] hits;
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}

	}

	/**
	 * Internal function to search for a {@link DateEvents}.
	 * 
	 * @param dateEvents
	 * @param element
	 * @return List<MyDateEvents>
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByDateEvents(final DateEvents dateEvents, EnumDublinCoreElements element)
			throws PrimaryDataDirectoryException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		List<Integer> result = new ArrayList<Integer>();

		final Set<EdalDate> set = dateEvents.getSet();

		if (set.size() > 1) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("no DateEvents with multiple BasicDates allowed");
		}

		else if (set.size() == 1) {

			for (final EdalDate edalDate : set) {

				if (edalDate instanceof EdalDateRange) {
					result = this.searchByEDALDateRange((EdalDateRange) edalDate, element);

//					for (final Integer integer : idlist) {
//						result.add(session.get(MyDateEvents.class, integer));
//					}
				}

				else if (edalDate instanceof EdalDate) {

					result = this.searchByEDALDate(edalDate, element);

//					for (final Integer integer : idlist) {
//						result.add(session.get(MyDateEvents.class, integer));
//					}
				}

			}

		}

		session.close();

		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected List<PrimaryDataEntity> searchByDublinCoreElementImpl(final EnumDublinCoreElements element,
			final UntypedData data, final boolean fuzzy, final boolean recursiveIntoSubdirectories)
			throws PrimaryDataDirectoryException {

		long startTime = System.currentTimeMillis();

		List<Integer> versionIDList = new ArrayList<Integer>();
		try {
			if (data.getClass().equals(UntypedData.class)) {
				versionIDList = this.searchByUntypedData(data, element, fuzzy);
			} else if (data.getClass().equals(NaturalPerson.class)) {
				versionIDList = this.searchByNaturalPerson((NaturalPerson) data, element, fuzzy);
			} else if (data.getClass().equals(LegalPerson.class)) {
				versionIDList = this.searchByLegalPerson((LegalPerson) data, element, fuzzy);
			} else if (data.getClass().equals(Identifier.class)) {
				versionIDList = this.searchByIdentifier((Identifier) data, element, fuzzy);
			} else if (data.getClass().equals(DataType.class)) {
				versionIDList = this.searchByDataType((DataType) data, element);
			} else if (data.getClass().equals(DataFormat.class)) {
				versionIDList = this.searchByDataFormat((DataFormat) data, element, fuzzy);
			} else if (data.getClass().equals(DateEvents.class)) {
				versionIDList = this.searchByDateEvents((DateEvents) data, element);
			} else if (data.getClass().equals(IdentifierRelation.class)) {
				versionIDList = this.searchByIdentifierRelation((IdentifierRelation) data, element, fuzzy);
			} else if (data.getClass().equals(CheckSumType.class)) {
				versionIDList = this.searchByCheckSum((CheckSumType) data, element, fuzzy);
			} else if (data.getClass().equals(EdalLanguage.class)) {
				versionIDList = this.searchByEdalLanguage((EdalLanguage) data, element, fuzzy);
			} else if (data.getClass().equals(DataSize.class)) {
				versionIDList = this.searchByDataSize((DataSize) data, element, fuzzy);
			}

		} catch (final ParseException e) {
			throw new PrimaryDataDirectoryException("Unable to find the UntypedData values", e);
		}

		if (versionIDList.isEmpty()) {
			return new ArrayList<PrimaryDataEntity>();
		}
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final HashSet<PrimaryDataEntity> resultSet = new HashSet<PrimaryDataEntity>();

		final long startEntityQuery = System.currentTimeMillis();

		if (!recursiveIntoSubdirectories) {
			for (final Integer version : versionIDList) {
				final PrimaryDataEntityVersionImplementation currentVersion = session
						.get(PrimaryDataEntityVersionImplementation.class, version);

				final CriteriaBuilder builder = session.getCriteriaBuilder();

				CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
						.createQuery(PrimaryDataFileImplementation.class);

				Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);

				fileCriteria.where(builder.and(
						builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
								builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
										currentVersion.getPrimaryEntityId())),
						builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY), this)));

				final PrimaryDataFileImplementation primaryDataFile = session.createQuery(fileCriteria)
						.setCacheable(false)
						.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();

				CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
						.createQuery(PrimaryDataDirectoryImplementation.class);

				Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
						.from(PrimaryDataDirectoryImplementation.class);

				directoryCriteria.where(builder.and(
						builder.and(builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class),
								builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
										currentVersion.getPrimaryEntityId())),
						builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
								this)));

				final PrimaryDataDirectoryImplementation primaryDataDirectory = session.createQuery(directoryCriteria)
						.setCacheable(false)
						.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();

				if (primaryDataFile != null) {
					try {
						if (!primaryDataFile.getCurrentVersion().isDeleted()) {
							primaryDataFile.switchCurrentVersion(currentVersion);
						}
					} catch (final PrimaryDataEntityVersionException e) {
						throw new PrimaryDataDirectoryException(
								PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION, e);
					}
					resultSet.add(primaryDataFile);
				} else {
					if (primaryDataDirectory != null) {
						try {
							if (!primaryDataDirectory.getCurrentVersion().isDeleted()) {
								primaryDataDirectory.switchCurrentVersion(currentVersion);
							}
						} catch (final PrimaryDataEntityVersionException e) {
							throw new PrimaryDataDirectoryException(
									PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION, e);
						}
						resultSet.add(primaryDataDirectory);
					}
				}
			}
		} else {
			/** save all object that are found in the directory */
			final List<PrimaryDataEntityVersionImplementation> maybeInSubDirectoriesList = new ArrayList<PrimaryDataEntityVersionImplementation>();

			for (final Integer version : versionIDList) {
				final PrimaryDataEntityVersionImplementation currentVersion = session
						.get(PrimaryDataEntityVersionImplementation.class, version);
				try {
					if (!currentVersion.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).toString()
							.equals(MetaData.DIRECTORY.toString())) {

						final CriteriaBuilder builder = session.getCriteriaBuilder();

						CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
								.createQuery(PrimaryDataFileImplementation.class);

						Root<PrimaryDataFileImplementation> fileRoot = fileCriteria
								.from(PrimaryDataFileImplementation.class);

						fileCriteria.where(builder.and(
								builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
										builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
												currentVersion.getPrimaryEntityId())),
								builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
										this)));

						final PrimaryDataFileImplementation pdf = session.createQuery(fileCriteria).setCacheable(false)
								.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY)
								.uniqueResult();

						if (pdf != null) {
							try {
								if (!pdf.getCurrentVersion().isDeleted()) {
									pdf.switchCurrentVersion(currentVersion);
								}
							} catch (final PrimaryDataEntityVersionException e) {
								throw new PrimaryDataDirectoryException("Unable to switch version", e);
							}
							resultSet.add(pdf);
						} else {
							maybeInSubDirectoriesList.add(currentVersion);
						}
					} else {
						final CriteriaBuilder builder = session.getCriteriaBuilder();

						CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
								.createQuery(PrimaryDataDirectoryImplementation.class);

						Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
								.from(PrimaryDataDirectoryImplementation.class);

						directoryCriteria.where(builder.and(
								builder.and(
										builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class),
										builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
												currentVersion.getPrimaryEntityId())),
								builder.equal(
										directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
										this)));

						final PrimaryDataDirectoryImplementation pdd = session.createQuery(directoryCriteria)
								.setCacheable(false)
								.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY)
								.uniqueResult();

						if (pdd != null) {
							try {
								if (!pdd.getCurrentVersion().isDeleted()) {
									pdd.switchCurrentVersion(currentVersion);
								}
							} catch (final PrimaryDataEntityVersionException e) {
								throw new PrimaryDataDirectoryException("Unable to switch version", e);
							}
							resultSet.add(pdd);
						} else {
							maybeInSubDirectoriesList.add(currentVersion);
						}
					}
				} catch (final MetaDataException e) {
					throw new PrimaryDataDirectoryException("Unable to check object type", e);
				}
			}
			session.close();

			for (final PrimaryDataEntityVersionImplementation version : maybeInSubDirectoriesList) {
				final PrimaryDataEntity entity = this.searchIntoSubdirectories(this, version);
				if (entity != null) {
					if (entity.isDirectory()) {
						/**
						 * prevent that object itself will be add to the list
						 */
						if (!((PrimaryDataDirectory) entity).getID().equals(this.getID())) {
							resultSet.add(entity);
						}
					} else {
						resultSet.add(entity);
					}
				}
			}
		}

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.debug("Zeit (Search Entity)    : " + (System.currentTimeMillis() - startEntityQuery) + " msec");

		final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(resultSet);

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.info("Zeit (Search by Element): " + (System.currentTimeMillis() - startTime) + " msec");

		if (session.isOpen()) {
			session.close();
		}
		return results;

	}

	private List<Integer> searchByDataSize(DataSize data, EnumDublinCoreElements element, boolean fuzzy)
			throws PrimaryDataDirectoryException, ParseException {

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyDataSize> searchQuery = null;
			long size = data.getFileSize();
			if (fuzzy) {
				searchQuery = ftSession.search(MyDataSize.class)
						.where(f -> f.range().field("size").between(size - 1, size + 1)).fetch(200);
			} else {
				searchQuery = ftSession.search(MyDataSize.class).where(f -> f.match().field("size").matching(size))
						.fetch(200);
			}
			final List<MyDataSize> untypedDataList = searchQuery.hits();
			session.close();

			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			QueryParser parser = new QueryParser(EnumIndexField.SIZE.value(), new StandardAnalyzer());
			String luceneString;
			if (fuzzy) {
				luceneString = Long.toString(data.getFileSize()) + "~";
			} else {
				luceneString = Long.toString(data.getFileSize());
			}
			org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
			ScoreDoc[] hits;
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}
	}

	private List<Integer> searchByEdalLanguage(EdalLanguage data, EnumDublinCoreElements element, boolean fuzzy)
			throws PrimaryDataDirectoryException, ParseException {
		if (data.getLanguage() == null) {
			return new ArrayList<Integer>();
		}

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchScope<MyEdalLanguage> scope = ftSession.scope(MyEdalLanguage.class);
			SearchResult<MyEdalLanguage> searchQuery = null;
			if (fuzzy) {
				searchQuery = ftSession.search(MyEdalLanguage.class)
						.where(f -> f.match().field("language").matching(data.getLanguage()).fuzzy()).fetch(200);
			} else {
				searchQuery = ftSession.search(MyEdalLanguage.class)
						.where(f -> f.match().field("language").matching(data.getLanguage())).fetch(200);
			}
			final List<MyEdalLanguage> untypedDataList = searchQuery.hits();
			session.close();

			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			QueryParser parser = new QueryParser(EnumIndexField.LANGUAGE.value(), new StandardAnalyzer());
			String luceneString;
			if (fuzzy) {
				luceneString = data.getLanguage().toString() + "~";
			} else {
				luceneString = data.getLanguage().toString();
			}
			org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
			ScoreDoc[] hits;
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}
	}

	private List<? extends MyUntypedData> mapCollections(List<? extends MyUntypedData> datatypeList,
			Class<?> collectionClass, String setName) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<?> query = builder.createQuery(collectionClass);
		Root<?> root = query.from(collectionClass);
		Join<?, ?> join = root.join(setName);
		ParameterExpression<Collection> set = builder.parameter(Collection.class);
		query.where(join.in(set));
		TypedQuery<?> tq = session.createQuery(query);
		List<?> resultList = tq.setParameter(set, datatypeList).getResultList();
		List<? extends MyUntypedData> finalresult = (List<? extends MyUntypedData>) resultList;

		session.close();

		return finalresult;
	}

	private List<Integer> searchByCheckSum(CheckSumType data, EnumDublinCoreElements element, boolean fuzzy)
			throws PrimaryDataDirectoryException, ParseException {
		if (data.getAlgorithm().equals("") && data.getCheckSum().equals("")) {
			return new ArrayList<Integer>();
		}

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyCheckSumType> searchQuery = null;
			if (fuzzy) {
				searchQuery = ftSession.search(MyCheckSumType.class)
						.where(f -> f.bool().must(f.match().fields("algorithm").matching(data.getAlgorithm()).fuzzy())
								.must(f.match().fields("checkSum").matching(data.getCheckSum()).fuzzy()))
						.fetch(200);
			} else {
				searchQuery = ftSession.search(MyCheckSumType.class)
						.where(f -> f.bool().must(f.match().fields("algorithm").matching(data.getAlgorithm()))
								.must(f.match().fields("checkSum").matching(data.getCheckSum())))
						.fetch(200);
			}
			final List<MyCheckSumType> result = searchQuery.hits();
			List<? extends MyUntypedData> untypedDataList = this.mapCollections(result, MyCheckSum.class, "dataSet");
			session.close();
			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
			QueryParser queryAlgorithm = new QueryParser(EnumIndexField.ALGORITHM.value(), standardAnalyzer);
			QueryParser queryCheckSum = new QueryParser(EnumIndexField.CHECKSUM.value(), standardAnalyzer);
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			booleanQuery.add(queryAlgorithm.parse(data.getAlgorithm()), BooleanClause.Occur.MUST);
			booleanQuery.add(queryCheckSum.parse(data.getCheckSum()), BooleanClause.Occur.MUST);
			IndexSearcher searcher = new IndexSearcher(reader);
//	    	
//	    	//Search Documents with Parsed Query
//			QueryParser parser = new QueryParser(MetaDataImplementation.CHECKSUM, new StandardAnalyzer());
//			String luceneString = "";
//			if(fuzzy) {
//				if(data.getAlgorithm().equals("")) {
//					
//				}else if(data.getCheckSum().equals("")) {
//					
//				}
//				luceneString = MetaDataImplementation.ALGORITHM+":"+data.getAlgorithm()+"~ "+MetaDataImplementation.CHECKSUM+":"+data.getCheckSum()+"~";
//			}else {
//				luceneString = MetaDataImplementation.ALGORITHM+":"+data.getAlgorithm()+"~ "+MetaDataImplementation.CHECKSUM+":"+data.getCheckSum()+"~";
//			}
//	        org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
			ScoreDoc[] hits;
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			try {
				hits = searcher.search(booleanQuery.build(), 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}

//		org.apache.lucene.search.Query queryAlgorithm = null;
//		org.apache.lucene.search.Query queryChecksum = null;
//
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyCheckSumType.class).get();
//
//		queryAlgorithm = queryBuilder.keyword().onField("algorithm").matching(data.getAlgorithm().toString().toLowerCase())
//				.createQuery();
//		queryChecksum = queryBuilder.keyword().onField("checkSum").matching(data.getCheckSum().toString().toLowerCase())
//				.createQuery();
//		
//		org.apache.lucene.search.Query combinedQuery = queryBuilder.bool().must(queryAlgorithm).must(queryChecksum).createQuery();
//
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyCheckSumType> hibernateQuery = ftSession.createFullTextQuery(combinedQuery, MyCheckSumType.class);
//		final List<MyCheckSumType> result = hibernateQuery.list();
//		
//		List<? extends MyUntypedData> datatypeList = this.mapCollections(result, MyCheckSum.class, "dataSet");
//
//		return datatypeList;
	}

	/**
	 * Internal function to search for a {@link EdalDate}.
	 * 
	 * @param edalDate
	 * @return List<MyEDALDate>
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByEDALDate(final EdalDate edalDate, EnumDublinCoreElements element)
			throws PrimaryDataDirectoryException {

		final int precission = edalDate.getStartPrecision().ordinal();
		final Calendar date = edalDate.getStartDate();

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<MyEdalDate> dataCriteria = builder.createQuery(MyEdalDate.class);
			Root<MyEdalDate> rootDate = dataCriteria.from(MyEdalDate.class);

			ArrayList<Predicate> predicates = new ArrayList<>();
			if (precission == EdalDatePrecision.CENTURY.ordinal()) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
						.warn("no Dates with CENTURY Precission allowed");
				return new ArrayList<Integer>();

			} else if (precission >= EdalDatePrecision.DECADE.ordinal()) {

				/** note: use DECADE(date) if the database-SQL support */

				Expression<String> yearExpression = builder.function("YEAR", String.class, rootDate.get("startDate"));
				predicates.add(builder.equal(builder.substring(yearExpression, 1, 3),
						Integer.toString(date.get(Calendar.YEAR)).substring(0, 3)));

				if (precission >= EdalDatePrecision.YEAR.ordinal()) {

					predicates.add(builder.equal(yearExpression, date.get(Calendar.YEAR)));

					if (precission >= EdalDatePrecision.MONTH.ordinal()) {
						/** very important: Calendar count months from 0-11 */
						Expression<String> monthExpression = builder.function("MONTH", String.class,
								rootDate.get("startDate"));

						predicates.add(builder.equal(monthExpression, (date.get(Calendar.MONTH) + 1)));

						if (precission >= EdalDatePrecision.DAY.ordinal()) {

							Expression<String> dayExpression = builder.function("DAY", String.class,
									rootDate.get("startDate"));

							predicates.add(builder.equal(dayExpression, date.get(Calendar.DAY_OF_MONTH)));

							if (precission >= EdalDatePrecision.HOUR.ordinal()) {

								Expression<String> hourExpression = builder.function("HOUR", String.class,
										rootDate.get("startDate"));

								predicates.add(builder.equal(hourExpression, date.get(Calendar.HOUR_OF_DAY)));

								if (precission >= EdalDatePrecision.MINUTE.ordinal()) {

									Expression<String> minuteExpression = builder.function("MINUTE", String.class,
											rootDate.get("startDate"));

									predicates.add(builder.equal(minuteExpression, date.get(Calendar.MINUTE)));

									if (precission >= EdalDatePrecision.SECOND.ordinal()) {

										Expression<String> secondExpression = builder.function("SECOND", String.class,
												rootDate.get("startDate"));

										predicates.add(builder.equal(secondExpression, date.get(Calendar.SECOND)));

										// if (precission >= EdalDatePrecision.MILLISECOND.ordinal()) {
										//
										// Expression<String> millisecondExpression = builder.function("MILISECOND",
										// String.class, rootDate.get("startDate"));
										//
										// dataCriteria.where(
										// builder.equal(millisecondExpression, date.get(Calendar.MILLISECOND)));
										// }
									}
								}
							}
						}
					}
				}
			}
			Predicate finalQuery = builder.and(predicates.toArray(new Predicate[0]));
			dataCriteria.where(finalQuery);
			final List<MyEdalDate> list = session.createQuery(dataCriteria).list();
			@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
			final Query<Integer> metaDataQuery = session
					.createSQLQuery("select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

			metaDataQuery.setParameterList("list", list);
			List<Integer> finalResult = metaDataQuery.list();
			final Query<Integer> versionSQLQuery = session
					.createSQLQuery("SELECT DISTINCT v.ID " + "FROM ENTITY_VERSIONS v , metadata_map m , "
							+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_key=:key "
							+ "AND m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

			versionSQLQuery.setParameterList("list", finalResult);
			versionSQLQuery.setParameter("key", element.ordinal());
			return versionSQLQuery.list();
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			// create the BooleanQuery query object
			org.apache.lucene.search.Query query = LongPoint.newExactQuery(EnumIndexField.STARTDATE.value(),
					date.getTimeInMillis());

			// do the search
			try {
				ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}

	}

	/**
	 * Internal function to search for a {@link EdalDateRange}.
	 * 
	 * @param edalDateRange
	 * @param element
	 * @return List<MyEDALDateRange>
	 */
	private List<Integer> searchByEDALDateRange(final EdalDateRange edalDateRange, EnumDublinCoreElements element) {

		final int precissionStart = edalDateRange.getStartPrecision().ordinal();
		final Calendar dateStart = edalDateRange.getStartDate();

		final int precissionEnd = edalDateRange.getEndPrecision().ordinal();
		final Calendar dateEnd = edalDateRange.getEndDate();

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {

			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<MyEdalDateRange> dataCriteria = builder.createQuery(MyEdalDateRange.class);
			Root<MyEdalDateRange> rootDate = dataCriteria.from(MyEdalDateRange.class);

			if (precissionStart == EdalDatePrecision.CENTURY.ordinal()
					|| precissionEnd == EdalDatePrecision.CENTURY.ordinal()) {
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
						.warn("no DateRanges with CENTURY Precission allowed");
				return new ArrayList<Integer>();

			}
			ArrayList<Predicate> predicates = new ArrayList<>();
			predicates
					.add(builder.lessThanOrEqualTo(rootDate.<Calendar>get("startDate"), edalDateRange.getStartDate()));
			predicates.add(builder.greaterThanOrEqualTo(rootDate.<Calendar>get("endDate"), edalDateRange.getEndDate()));
			//
			// if (precissionStart >= EdalDatePrecision.DECADE.ordinal()) {
			//
			// /** note: use DECADE(date) if the database-SQL support */
			//
			// Expression<String> yearExpression = builder.function("YEAR", String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(builder.substring(yearExpression, 1, 3),
			// Integer.toString(dateStart.get(Calendar.YEAR)).substring(0, 3)));
			//
			// if (precissionStart >= EdalDatePrecision.YEAR.ordinal()) {
			//
			// dataCriteria.where(builder.equal(yearExpression,
			// dateStart.get(Calendar.YEAR)));
			//
			// if (precissionStart >= EdalDatePrecision.MONTH.ordinal()) {
			// /** very important: Calendar count months from 0-11 */
			// Expression<String> monthExpression = builder.function("MONTH", String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(monthExpression,
			// (dateStart.get(Calendar.MONTH) + 1)));
			//
			// if (precissionStart >= EdalDatePrecision.DAY.ordinal()) {
			//
			// Expression<String> dayExpression = builder.function("DAY", String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(dayExpression,
			// dateStart.get(Calendar.DAY_OF_MONTH)));
			//
			// if (precissionStart >= EdalDatePrecision.HOUR.ordinal()) {
			//
			// Expression<String> hourExpression = builder.function("HOUR", String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(hourExpression,
			// dateStart.get(Calendar.HOUR_OF_DAY)));
			//
			// if (precissionStart >= EdalDatePrecision.MINUTE.ordinal()) {
			//
			// Expression<String> minuteExpression = builder.function("MINUTE",
			// String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(minuteExpression,
			// dateStart.get(Calendar.MINUTE)));
			//
			// if (precissionStart >= EdalDatePrecision.SECOND.ordinal()) {
			//
			// Expression<String> secondExpression = builder.function("SECOND",
			// String.class,
			// rootDate.get("startDate"));
			//
			// dataCriteria.where(builder.equal(secondExpression,
			// dateStart.get(Calendar.SECOND)));
			//
			//// if (precissionStart >= EdalDatePrecision.MILLISECOND.ordinal()) {
			////
			//// Expression<String> millisecondExpression = builder.function("MILLISECOND",
			//// String.class, rootDate.get("startDate"));
			////
			//// dataCriteria.where(builder.equal(millisecondExpression,
			//// dateStart.get(Calendar.MILLISECOND)));
			//// }
			// }
			// }
			// }
			// }
			// }
			// }
			// }
			//
			// if (precissionEnd >= EdalDatePrecision.DECADE.ordinal()) {
			//
			// /** note: use DECADE(date) if the database-SQL support */
			//
			// Expression<String> yearExpression = builder.function("YEAR", String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(builder.substring(yearExpression, 1, 3),
			// Integer.toString(dateEnd.get(Calendar.YEAR)).substring(0, 3)));
			//
			// if (precissionEnd >= EdalDatePrecision.YEAR.ordinal()) {
			//
			// dataCriteria.where(builder.equal(yearExpression,
			// dateEnd.get(Calendar.YEAR)));
			//
			// if (precissionEnd >= EdalDatePrecision.MONTH.ordinal()) {
			// /** very important: Calendar count months from 0-11 */
			// Expression<String> monthExpression = builder.function("MONTH", String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(monthExpression,
			// (dateEnd.get(Calendar.MONTH) + 1)));
			//
			// if (precissionEnd >= EdalDatePrecision.DAY.ordinal()) {
			//
			// Expression<String> dayExpression = builder.function("DAY", String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(dayExpression,
			// dateEnd.get(Calendar.DAY_OF_MONTH)));
			//
			// if (precissionEnd >= EdalDatePrecision.HOUR.ordinal()) {
			//
			// Expression<String> hourExpression = builder.function("HOUR", String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(hourExpression,
			// dateEnd.get(Calendar.HOUR_OF_DAY)));
			//
			// if (precissionEnd >= EdalDatePrecision.MINUTE.ordinal()) {
			//
			// Expression<String> minuteExpression = builder.function("MINUTE",
			// String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(minuteExpression,
			// dateEnd.get(Calendar.MINUTE)));
			//
			// if (precissionEnd >= EdalDatePrecision.SECOND.ordinal()) {
			//
			// Expression<String> secondExpression = builder.function("SECOND",
			// String.class,
			// rootDate.get("endDate"));
			//
			// dataCriteria.where(builder.equal(secondExpression,
			// dateEnd.get(Calendar.SECOND)));
			//
			//// if (precissionEnd >= EdalDatePrecision.MILLISECOND.ordinal()) {
			////
			//// Expression<String> millisecondExpression = builder.function("MILLISECOND",
			//// String.class, rootDate.get("endDate"));
			////
			//// dataCriteria.where(builder.equal(millisecondExpression,
			//// dateEnd.get(Calendar.MILLISECOND)));
			//// }
			// }
			// }
			// }
			// }
			// }
			// }
			// }

			dataCriteria.where(predicates.toArray(new Predicate[0]));
			final List<MyEdalDateRange> list = session.createQuery(dataCriteria).list();

			session.close();

			@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
			final Query<Integer> metaDataQuery = session
					.createQuery("select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

			metaDataQuery.setParameterList("list", list);
			List<Integer> finalResult = metaDataQuery.list();
			final Query<Integer> versionSQLQuery = session
					.createSQLQuery("SELECT DISTINCT v.ID " + "FROM ENTITY_VERSIONS v , metadata_map m , "
							+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_key=:key "
							+ "AND m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

			versionSQLQuery.setParameterList("list", finalResult);
			versionSQLQuery.setParameter("key", element.ordinal());
			return versionSQLQuery.list();
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			// create the BooleanQuery query object
			org.apache.lucene.search.Query start = LongPoint.newExactQuery(EnumIndexField.STARTDATE.value(),
					dateStart.getTimeInMillis());
			org.apache.lucene.search.Query end = LongPoint.newExactQuery(EnumIndexField.ENDDATE.value(),
					dateEnd.getTimeInMillis());
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			booleanQuery.add(start, BooleanClause.Occur.MUST);
			booleanQuery.add(end, BooleanClause.Occur.MUST);

			// do the search
			try {
				ScoreDoc[] hits = searcher.search(booleanQuery.build(), 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}
	}

	/**
	 * Internal function to search for a {@link Identifier}.
	 * 
	 * @param identifier
	 * @param element
	 * @param fuzzy
	 * @return List<MyIdentifier>
	 * @throws ParseException If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<Integer> searchByIdentifier(final Identifier identifier, EnumDublinCoreElements element,
			final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyIdentifier> searchQuery = null;
			if (fuzzy) {
				searchQuery = ftSession.search(MyIdentifier.class)
						.where(f -> f.bool()
								.must(f.match().field("identifier").matching(identifier.getIdentifier()).fuzzy())
								.must(f.match().field("relatedIdentifierType")
										.matching(identifier.getRelatedIdentifierType()).fuzzy())
								.must(f.match().field("relationType").matching(identifier.getRelationType()).fuzzy()))
						.fetch(200);
			} else {
				searchQuery = ftSession.search(MyIdentifier.class)
						.where(f -> f.bool().must(f.match().field("identifier").matching(identifier.getIdentifier()))
								.must(f.match().field("relatedIdentifierType")
										.matching(identifier.getRelatedIdentifierType()))
								.must(f.match().field("relationType").matching(identifier.getRelationType())))
						.fetch(200);
			}
			final List<MyIdentifier> untypedDataList = searchQuery.hits();
			session.close();
			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			QueryParser parser = new QueryParser(EnumIndexField.IDENTIFIER.value(), new StandardAnalyzer());
			String luceneString;
			if (fuzzy) {
				luceneString = identifier.getIdentifier() + "~";
			} else {
				luceneString = identifier.getIdentifier();
			}
			org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
			ScoreDoc[] hits;
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}
			return versionIDList;
		}

//
//		org.apache.lucene.search.Query query = null;
//
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyIdentifier.class)
//				.get();
//		if (fuzzy) {
//			query = queryBuilder.keyword().fuzzy().onField("identifier").matching(identifier.getID())
//					.createQuery();
//		} else {
//			query = queryBuilder.keyword().onField("identifier").matching(identifier.getID()).createQuery();
//		}
//
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyIdentifier> hibernateQuery = ftSession.createFullTextQuery(query, MyIdentifier.class);
//		final List<MyIdentifier> result = hibernateQuery.list();
//
//		session.close();
//
//		return result;

	}

	/**
	 * Internal function to search for a {@link IdentifierRelation}.
	 * 
	 * @param identifierRelation
	 * @param element
	 * @param fuzzy
	 * @return List<MyIdentifierRelation>
	 * @throws ParseException                If unable to parse query string with
	 *                                       <em>LUCENE<em>.
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByIdentifierRelation(final IdentifierRelation identifierRelation,
			EnumDublinCoreElements element, final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final List<MyIdentifierRelation> untypedDataList = new ArrayList<MyIdentifierRelation>();

		if (identifierRelation.getRelations().size() > 1) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("only IdentifierRelations with only one Identifier allowed");
			return new ArrayList<Integer>();
		} else if (identifierRelation.getRelations().size() < 1) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("empty IdentifierRelations are not allowed");
			return new ArrayList<Integer>();
		} else {
			if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
					.isHibernateSearchIndexingEnabled()) {

				final Identifier identifier = identifierRelation.getRelations().iterator().next();

				final SearchSession ftSession = Search.session(session);
				SearchResult<MyIdentifier> searchQuery = null;
				if (fuzzy) {
					searchQuery = ftSession.search(MyIdentifier.class).where(f -> f.bool()
							.must(f.match().field("identifier").matching(identifier.getIdentifier()).fuzzy())
							.must(f.match().field("relatedIdentifierType")
									.matching(identifier.getRelatedIdentifierType()).fuzzy())
							.must(f.match().field("relationType").matching(identifier.getRelationType()).fuzzy()))
							.fetch(200);
				} else {
					searchQuery = ftSession.search(MyIdentifier.class)
							.where(f -> f.bool()
									.must(f.match().field("identifier").matching(identifier.getIdentifier()))
									.must(f.match().field("relatedIdentifierType")
											.matching(identifier.getRelatedIdentifierType()))
									.must(f.match().field("relationType").matching(identifier.getRelationType())))
							.fetch(200);
				}
				final List<MyIdentifier> myIdentifierList = searchQuery.hits();

				if (!myIdentifierList.isEmpty()) {
					@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
					final Query<Integer> metaDataQuery = session.createQuery(
							"select D.id from MyIdentifierRelation D join D.relations V where V in (:list)");

					metaDataQuery.setParameterList("list", myIdentifierList);

					final List<Integer> idlist = metaDataQuery.list();

					for (final Integer integer : idlist) {
						untypedDataList.add(session.get(MyIdentifierRelation.class, integer));

					}
				}
				return this.retrieveVersionIds(untypedDataList, element);
			} else {

				Identifier id = null;
				for (Identifier identifier : identifierRelation) {
					id = identifier;
				}
				IndexReader reader = null;
				// Create IndexSearcher from IndexDirectory
				try {
					Directory indexDirectory = FSDirectory
							.open(Paths.get(((FileSystemImplementationProvider) DataManager.getImplProv())
									.getIndexDirectory().toString(), "Master_Index"));
					reader = DirectoryReader.open(indexDirectory);
				} catch (IOException e) {
					DataManager.getImplProv().getLogger()
							.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
				}
				IndexSearcher searcher = new IndexSearcher(reader);

				// Search Documents with Parsed Query
				QueryParser parser = new QueryParser(EnumIndexField.RELATION.value(), new StandardAnalyzer());
				String luceneString;
				if (fuzzy) {
					luceneString = id.getIdentifier() + "~";
				} else {
					luceneString = id.getIdentifier();
				}
				org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
				ScoreDoc[] hits;
				final ArrayList<Integer> versionIDList = new ArrayList<>();
				try {
					hits = searcher.search(luceneQuery, 10).scoreDocs;
					for (int i = 0; i < hits.length; i++) {
						Document doc = searcher.doc(hits[i].doc);
						versionIDList.add(Integer.parseInt(doc.get("versionID")));
					}
				} catch (IOException e) {
					DataManager.getImplProv().getLogger()
							.debug("Error when searching for a specifc document: " + e.getMessage());
				}
				return versionIDList;
			}
		}
	}

	@Override
	protected List<? extends PrimaryDataEntity> searchByKeywordImpl(final String keyword, final boolean fuzzy,
			final boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException {

		List<Integer> versionIdList = new ArrayList<>();
		Long searchStartTime = System.currentTimeMillis();
		final Session mainSession = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final long startTime = System.currentTimeMillis();

			org.apache.lucene.search.Query query = null;

			// final FullTextSession ftSession = Search.getFullTextSession(session);
			final SearchSession ftSession = Search.session(mainSession);
//			QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyUntypedData.class)
//					.get();
			//
//			SearchFactory searchFactory = ftSession.getSearchFactory();
			// org.hibernate.Query fullTextQuery = null;
			SearchMapping mapping = Search.mapping(mainSession.getSessionFactory());
			Backend backend = mapping.backend();
			LuceneBackend luceneBackend = backend.unwrap(LuceneBackend.class);
			Optional<? extends Analyzer> analyzer = luceneBackend.analyzer("default");
			org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "string", "givenName", "sureName", "country", "zip", "adressLine", "legalName", "id",
							"identifier", "mimeType", "checkSum", "algorithm", "size", "language" },
					analyzer.get());
			parser.setDefaultOperator(QueryParser.OR_OPERATOR);
			SearchResult<MyUntypedData> searchResult = null;
			try {
				final org.apache.lucene.search.Query luceneQuery;
				if (fuzzy) {
					luceneQuery = parser.parse(keyword + '~');
				} else {
					luceneQuery = parser.parse(keyword);
				}

				searchResult = ftSession.search(MyUntypedData.class).extension(LuceneExtension.get())
						.where(f -> f.fromLuceneQuery(luceneQuery)).fetch(10000);
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
						.info("Lucenequery: " + luceneQuery.toString());
			} catch (ParseException e) {
				// handle parsing failure
			}

			List<? extends MyUntypedData> datatypes = searchResult.hits(); // return a list of managed objects
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.info("DatatypeList Size: " + datatypes.size());

			// Checksummentypen, MyNaturalPerson, MyLEgalPerson mappen und neue Liste bauen

			// session.close();
			Collection<MyUntypedData> datatypeList = new ArrayList<>();
			/** if no results found return empty List */
			if (datatypes.isEmpty()) {
				return new ArrayList<PrimaryDataEntity>();
			}
			if (datatypes.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
				throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
			} else {
				// filter Collection-associated Objects for mapping
				ArrayList<MyNaturalPerson> naturalPersons = new ArrayList<>();
				ArrayList<MyCheckSumType> checksumTypes = new ArrayList<>();
				ArrayList<MyUntypedData> maybeSubjects = new ArrayList<>();
				// Subjects - MyUntypedData
				for (MyUntypedData data : datatypes) {
					if (data instanceof MyNaturalPerson) {
						naturalPersons.add((MyNaturalPerson) data);
					} else if (data instanceof MyCheckSumType) {
						checksumTypes.add((MyCheckSumType) data);
					} else {
						datatypeList.add(data);
					}
				}
				// Values getting deleted, is that a problem?
				datatypeList.addAll(this.mapCollections(maybeSubjects, MySubjects.class, "subjects"));
				if (naturalPersons.size() > 0) {
					datatypeList.addAll(this.mapCollections(naturalPersons, MyPersons.class, "persons"));
				}
				if (checksumTypes.size() > 0) {
					datatypeList.addAll(this.mapCollections(checksumTypes, MyCheckSum.class, "dataSet"));

				}
			}

			final List<Integer> datatypeIDList = new ArrayList<Integer>(datatypeList.size());

			for (final MyUntypedData myUntypedData : datatypeList) {
				datatypeIDList.add(myUntypedData.getId());
			}

			final Session session2 = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
			final Query<Integer> versionSQLQuery = session2.createSQLQuery("SELECT DISTINCT v.ID "
					+ "FROM ENTITY_VERSIONS v , metadata_map m , "
					+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

			versionSQLQuery.setParameterList("list", datatypeIDList);

			versionIdList = versionSQLQuery.list();
		} 
		else {
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
					EnumIndexField.TYPE.value(), EnumIndexField.LANGUAGE.value(), EnumIndexField.CREATOR.value(),
					EnumIndexField.LEGALPERSON.value(), EnumIndexField.ALGORITHM.value(),
					EnumIndexField.CHECKSUM.value(), EnumIndexField.SUBJECT.value(), EnumIndexField.RELATION.value(),
					EnumIndexField.MIMETYPE.value(), EnumIndexField.STARTDATE.value(), EnumIndexField.ENDDATE.value() };

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

				versionIdList = new ArrayList<>();

				for (int i = 0; i < hits.length; i++) {

					Document doc = searcher.doc(hits[i].doc);

					versionIdList.add(Integer.parseInt(doc.get(EnumIndexField.VERSIONID.value())));

				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger().info("IO error while searching: " + e.getMessage());
			}

			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.info("Found while searching: " + versionIdList.size() + " values");
		}

		final HashSet<PrimaryDataEntity> resultSet = new HashSet<PrimaryDataEntity>();

		final long startEntityQuery = System.currentTimeMillis();

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.info("Time for search in index: " + (startEntityQuery - searchStartTime) + " msec");

		final Session sessionForCheckRecursiveObjects = ((FileSystemImplementationProvider) DataManager.getImplProv())
				.getSession();

		if (!recursiveIntoSubdirectories) {
			for (final Integer version : versionIdList) {
				final PrimaryDataEntityVersionImplementation currentVersion = sessionForCheckRecursiveObjects
						.get(PrimaryDataEntityVersionImplementation.class, version);

				final CriteriaBuilder builder = sessionForCheckRecursiveObjects.getCriteriaBuilder();

				CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
						.createQuery(PrimaryDataFileImplementation.class);

				Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);

				fileCriteria.where(builder.and(
						builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
								builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
										currentVersion.getPrimaryEntityId())),
						builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY), this)));

				final PrimaryDataFileImplementation primaryDataFile = sessionForCheckRecursiveObjects
						.createQuery(fileCriteria).setCacheable(false)
						.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();

				CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
						.createQuery(PrimaryDataDirectoryImplementation.class);

				Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
						.from(PrimaryDataDirectoryImplementation.class);

				directoryCriteria.where(builder.and(
						builder.and(builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class),
								builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
										currentVersion.getPrimaryEntityId())),
						builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
								this)));

				final PrimaryDataDirectoryImplementation primaryDataDirectory = sessionForCheckRecursiveObjects
						.createQuery(directoryCriteria).setCacheable(false)
						.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY).uniqueResult();

				if (primaryDataFile != null) {
					try {
						if (!primaryDataFile.getCurrentVersion().isDeleted()) {
							primaryDataFile.switchCurrentVersion(currentVersion);
						}
					} catch (final PrimaryDataEntityVersionException e) {
						throw new PrimaryDataDirectoryException(
								PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION, e);
					}
					resultSet.add(primaryDataFile);
				} else {
					if (primaryDataDirectory != null) {
						try {
							if (!primaryDataDirectory.getCurrentVersion().isDeleted()) {
								primaryDataDirectory.switchCurrentVersion(currentVersion);
							}
						} catch (final PrimaryDataEntityVersionException e) {
							throw new PrimaryDataDirectoryException(
									PrimaryDataDirectoryImplementation.STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION, e);
						}
						resultSet.add(primaryDataDirectory);
					}
				}
			}
		} else {
			/** save all object that are found in the directory */
			final List<PrimaryDataEntityVersionImplementation> maybeInSubDirectoriesList = new ArrayList<PrimaryDataEntityVersionImplementation>();

			for (final Integer version : versionIdList) {
				final PrimaryDataEntityVersionImplementation currentVersion = sessionForCheckRecursiveObjects
						.get(PrimaryDataEntityVersionImplementation.class, version);
				try {
					if (!currentVersion.getMetaData().getElementValue(EnumDublinCoreElements.TYPE).toString()
							.equals(MetaData.DIRECTORY.toString())) {

						final CriteriaBuilder builder = sessionForCheckRecursiveObjects.getCriteriaBuilder();

						CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
								.createQuery(PrimaryDataFileImplementation.class);

						Root<PrimaryDataFileImplementation> fileRoot = fileCriteria
								.from(PrimaryDataFileImplementation.class);

						fileCriteria.where(builder.and(
								builder.and(builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class),
										builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
												currentVersion.getPrimaryEntityId())),
								builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
										this)));

						final PrimaryDataFileImplementation pdf = sessionForCheckRecursiveObjects
								.createQuery(fileCriteria).setCacheable(false)
								.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY)
								.uniqueResult();

						if (pdf != null) {
							try {
								if (!pdf.getCurrentVersion().isDeleted()) {
									pdf.switchCurrentVersion(currentVersion);
								}
							} catch (final PrimaryDataEntityVersionException e) {
								throw new PrimaryDataDirectoryException("Unable to switch version", e);
							}
							resultSet.add(pdf);
						} else {
							maybeInSubDirectoriesList.add(currentVersion);
						}
					} else {

						final CriteriaBuilder builder = sessionForCheckRecursiveObjects.getCriteriaBuilder();

						CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
								.createQuery(PrimaryDataDirectoryImplementation.class);

						Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
								.from(PrimaryDataDirectoryImplementation.class);

						directoryCriteria.where(builder.and(
								builder.and(
										builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class),
										builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
												currentVersion.getPrimaryEntityId())),
								builder.equal(
										directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_PARENT_DIRECTORY),
										this)));

						final PrimaryDataDirectoryImplementation pdd = sessionForCheckRecursiveObjects
								.createQuery(directoryCriteria).setCacheable(false)
								.setCacheRegion(PrimaryDataDirectoryImplementation.CACHE_REGION_SEARCH_ENTITY)
								.uniqueResult();

						if (pdd != null) {
							try {
								if (!pdd.getCurrentVersion().isDeleted()) {
									pdd.switchCurrentVersion(currentVersion);
								}
							} catch (final PrimaryDataEntityVersionException e) {
								throw new PrimaryDataDirectoryException("Unable to switch version", e);
							}
							resultSet.add(pdd);
						} else {
							maybeInSubDirectoriesList.add(currentVersion);
						}
					}
				} catch (final MetaDataException e) {
					throw new PrimaryDataDirectoryException("Unable to check object type", e);
				}
			}
			mainSession.close();
			sessionForCheckRecursiveObjects.close();

			for (final PrimaryDataEntityVersionImplementation version : maybeInSubDirectoriesList) {
				final PrimaryDataEntity entity = this.searchIntoSubdirectories(this, version);
				if (entity != null) {
					if (entity.isDirectory()) {
						/**
						 * prevent that object itself will be add to the list
						 */
						if (!((PrimaryDataDirectory) entity).getID().equals(this.getID())) {
							resultSet.add(entity);
						}
					} else {
						resultSet.add(entity);
					}
				}
			}
		}

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.debug("Time to search for Entities : " + (System.currentTimeMillis() - startEntityQuery) + " msec");

		final List<PrimaryDataEntity> results = new ArrayList<PrimaryDataEntity>(resultSet);

		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
				.info("Time for search by keyword : " + (System.currentTimeMillis() - searchStartTime) + " msec");
		
		return results;
	}

	private void checkParentDirectory(PrimaryDataEntityVersionImplementation version, List<Integer> versionIDList)
			throws PrimaryDataDirectoryException {
		PrimaryDataDirectory parent = version.getEntity().getParentDirectory();
		SortedSet<PrimaryDataEntityVersion> vs = parent.getVersions();
		List<PublicReference> publicrefs = null;
		for (PrimaryDataEntityVersion ver : vs) {
			publicrefs = ver.getPublicReferences();
			if (!publicrefs.isEmpty()) {
				for (PublicReference ref : publicrefs) {
					if (ref.getIdentifierType().equals(PersistentIdentifier.DOI)) {
						versionIDList.add(version.getId());
						return;
					}
				}
			}
		}
		while (parent.getParentDirectory() != null) {
			parent = parent.getParentDirectory();
			vs = parent.getVersions();
			for (PrimaryDataEntityVersion ver : vs) {
				publicrefs = ver.getPublicReferences();
				if (!ver.getPublicReferences().isEmpty()) {
					for (PublicReference ref : publicrefs) {
						if (ref.getIdentifierType().equals(PersistentIdentifier.DOI)) {
							versionIDList.add(version.getId());
							return;
						}
					}
				}
			}
		}

	}

	/**
	 * Internal function to search for a {@link LegalPerson}.
	 * 
	 * @param person
	 * @param fuzzy
	 * @return List<MyLegalPerson>
	 * @throws ParseException                If unable to parse query string with
	 *                                       <em>LUCENE<em>.
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByLegalPerson(final LegalPerson person, EnumDublinCoreElements element,
			final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			final SearchSession ftSession = Search.session(session);
			SearchResult<MyLegalPerson> searchQuery = null;
			if (fuzzy) {
				searchQuery = ftSession.search(MyLegalPerson.class)
						.where(f -> f.bool().must(f.match().fields("legalName").matching(person.getLegalName()).fuzzy())
								.must(f.match().fields("addressLine").matching(person.getAddressLine()).fuzzy())
								.must(f.match().fields("zip").matching(person.getZip()).fuzzy())
								.must(f.match().fields("country").matching(person.getCountry()).fuzzy()))
						.fetch(200);
			} else {
				searchQuery = ftSession.search(MyLegalPerson.class)
						.where(f -> f.bool().must(f.match().fields("legalName").matching(person.getLegalName()))
								.must(f.match().fields("addressLine").matching(person.getAddressLine()))
								.must(f.match().fields("zip").matching(person.getZip()))
								.must(f.match().fields("country").matching(person.getCountry())))
						.fetch(200);
			}
			final List<MyLegalPerson> untypedDataList = searchQuery.hits();
			session.close();
			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			StringBuilder builder = new StringBuilder();
			if (!person.getLegalName().equals("")) {
				builder.append(person.getLegalName());
				builder.append(" ");
			}
			if (!person.getAddressLine().equals("")) {
				builder.append(person.getAddressLine());
				builder.append(" ");
			}
			if (!person.getZip().equals("")) {
				builder.append(person.getZip());
				builder.append(" ");
			}
			if (!person.getCountry().equals("")) {
				builder.append(person.getCountry());
			}
			if (fuzzy) {
				builder.append('~');
			}
			String buildedStr = builder.toString();
			if (buildedStr.length() == 0) {
				return new ArrayList<Integer>();
			}
			QueryParser parser = new QueryParser(EnumIndexField.LEGALPERSON.value(), new StandardAnalyzer());
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			ScoreDoc[] hits;
			try {
				hits = searcher.search(parser.parse(builder.toString()), 5000).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.info("Error while searching a specific Document: " + e.getMessage());
			}

			return versionIDList;
		}

	}

//	/** {@inheritDoc} */
//	@Override
//	protected List<PrimaryDataEntity> searchByMetaDataImpl(final MetaData query, final boolean fuzzy,
//			final boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException, MetaDataException {
//
//		//final HashSet<PrimaryDataEntity> hashSet = new HashSet<PrimaryDataEntity>();
//
//		//filter empty Elements/Attributes
//		ArrayList<HashSet<PrimaryDataEntity>> resultsList = new ArrayList<>();
//		for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
//			final HashSet<PrimaryDataEntity> hashSet = new HashSet<PrimaryDataEntity>();
//			final List<PrimaryDataEntity> tempList = this.searchByDublinCoreElement(element,
//			query.getElementValue(element), fuzzy, recursiveIntoSubdirectories);
//			hashSet.addAll(tempList);
//			if(!hashSet.isEmpty())
//				resultsList.add(hashSet);		
////			final List<PrimaryDataEntity> tempList = this.searchByDublinCoreElement(element,
////					query.getElementValue(element), fuzzy, recursiveIntoSubdirectories);
////			hashSet.addAll(tempList);
//		}
//		int size = resultsList.size();
//		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Size before search end"+resultsList.get(0).size());
//
//		HashSet<PrimaryDataEntity> entitySet = new HashSet<>();
//		if(size > 0) {
//		//boolean temp = resultsList.get(1).iterator().next().equals(resultsList.get(3).iterator().next());
//			 entitySet = resultsList.get(0);
//		}
//		for(int i = 0; i < size; i++) {
//			entitySet.retainAll(resultsList.get(i));
//		}
//		if(entitySet.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
//			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
//		}
////		if (hashSet.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
////			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
////		}
//		final List<PrimaryDataEntity> entityList = new ArrayList<PrimaryDataEntity>(entitySet);
//		return entityList;
//	}

	/** {@inheritDoc} */
	@Override
	protected List<PrimaryDataEntity> searchByMetaDataImpl(final MetaData query, final boolean fuzzy,
			final boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException, MetaDataException {

		final HashSet<PrimaryDataEntity> hashSet = new HashSet<PrimaryDataEntity>();

		for (final EnumDublinCoreElements element : EnumDublinCoreElements.values()) {
			final List<PrimaryDataEntity> tempList = this.searchByDublinCoreElement(element,
					query.getElementValue(element), fuzzy, recursiveIntoSubdirectories);
			hashSet.addAll(tempList);
		}

		if (hashSet.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
		}
		final List<PrimaryDataEntity> entityList = new ArrayList<PrimaryDataEntity>(hashSet);

		return entityList;
	}

	/**
	 * Internal function to search for a {@link NaturalPerson}.
	 * 
	 * @param person
	 * @param fuzzy
	 * @return List<MyNaturalPerson>
	 * @throws ParseException                If unable to parse query string with
	 *                                       <em>LUCENE<em>.
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByNaturalPerson(final NaturalPerson person, EnumDublinCoreElements element,
			final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {
		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
			final SearchSession ftSession = Search.session(session);
			SearchResult<MyNaturalPerson> searchQuery = null;

			if (fuzzy) {
				searchQuery = ftSession.search(MyNaturalPerson.class)
						.where(f -> f.bool().must(f.match().fields("givenName").matching(person.getGivenName()).fuzzy())
								.must(f.match().fields("sureName").matching(person.getSureName()).fuzzy())
								.must(f.match().fields("addressLine").matching(person.getAddressLine()).fuzzy())
								.must(f.match().fields("zip").matching(person.getZip()).fuzzy())
								.must(f.match().fields("country").matching(person.getCountry()).fuzzy()))
						.fetch(200);
			} else {
				searchQuery = ftSession.search(MyNaturalPerson.class)
						.where(f -> f.bool().must(f.match().fields("givenName").matching(person.getGivenName()))
								.must(f.match().fields("sureName").matching(person.getSureName()))
								.must(f.match().fields("addressLine").matching(person.getAddressLine()))
								.must(f.match().fields("zip").matching(person.getZip()))
								.must(f.match().fields("country").matching(person.getCountry())))
						.fetch(200);
			}

			final List<MyNaturalPerson> result = searchQuery.hits();
			List<? extends MyUntypedData> untypedDataList = this.mapCollections(result, MyPersons.class, "persons");
			return this.retrieveVersionIds(untypedDataList, element);
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query

			String luceneString;
			StringBuilder builder = new StringBuilder();

			builder.append(person.getGivenName());
			builder.append(" ");
			builder.append(person.getSureName());
			builder.append(" ");
			builder.append(person.getAddressLine());
			builder.append(" ");
			builder.append(person.getZip());
			builder.append(" ");
			builder.append(person.getCountry());
			if (fuzzy) {
				builder.append('~');
			}
			QueryParser parser = new QueryParser(EnumIndexField.CREATOR.value(), new StandardAnalyzer());
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			org.apache.lucene.search.Query luceneQuery = parser.parse(builder.toString());
			ScoreDoc[] hits;
			try {
				hits = searcher.search(luceneQuery, 5000).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}

			return versionIDList;
		}

	}

	/** {@inheritDoc} */
	@Override
	protected List<? extends PrimaryDataEntity> searchByPublicationStatusImpl(final PublicationStatus publicationStatus)
			throws PrimaryDataDirectoryException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final List<PrimaryDataEntity> results = new ArrayList<>();

		final List<PrimaryDataEntityVersionImplementation> maybeResults = new ArrayList<>();

		final CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PublicReferenceImplementation> referenceCriteria = builder
				.createQuery(PublicReferenceImplementation.class);
		Root<PublicReferenceImplementation> referenceRoot = referenceCriteria.from(PublicReferenceImplementation.class);

		referenceCriteria.where(builder.equal(referenceRoot.get("publicationStatus"), publicationStatus));

		final List<PublicReferenceImplementation> notRequestedList = session.createQuery(referenceCriteria)
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

		for (final PublicReferenceImplementation publicReferenceImplementation : notRequestedList) {
			final PrimaryDataEntity entity = publicReferenceImplementation.getVersion().getEntity();
			try {
				if (entity.getParentDirectory().equals(this)) {
					results.add(entity);
				} else {
					maybeResults.add(publicReferenceImplementation.getVersion());
				}
			} catch (final PrimaryDataDirectoryException e) {
				throw new PrimaryDataDirectoryException("unable to search in directory", e);
			}

		}

		for (final PrimaryDataEntityVersionImplementation version : maybeResults) {
			try {
				final PrimaryDataEntity entity = this.searchIntoSubdirectories(this, version);

				if (entity != null) {
					results.add(entity);
				}
			} catch (final PrimaryDataDirectoryException e) {
				throw new PrimaryDataDirectoryException("unable to search into sub directories", e);
			}

		}

		return results;
	}

	private List<Integer> retrieveVersionIds(List<? extends MyUntypedData> untypedDataList,
			EnumDublinCoreElements element) throws PrimaryDataDirectoryException {
		if (element == EnumDublinCoreElements.SUBJECT) {
			untypedDataList = this.mapCollections(untypedDataList, MySubjects.class, "subjects");
		}

		/** if no results found return empty List */
		if (untypedDataList.isEmpty()) {
			return new ArrayList<Integer>();
		}
		if (untypedDataList.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
		}

		final List<Integer> datatypeIDList = new ArrayList<Integer>(untypedDataList.size());

		for (final MyUntypedData myUntypedData : untypedDataList) {
			datatypeIDList.add(myUntypedData.getId());
		}

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
//		final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//		CriteriaQuery<PrimaryDataEntityVersionImplementation> query = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
//		Root<PrimaryDataEntityVersionImplementation> root = query.from(PrimaryDataEntityVersionImplementation.class);
//		Join<PrimaryDataEntityVersionImplementation, MetaDataImplementation> join = root.join("metaData");
//		Join<MetaDataImplementation, MyUntypedData> chainedJoin = join.join("myMap");
//		ParameterExpression<Collection> persons = criteriaBuilder.parameter(Collection.class);
//		query.where(chainedJoin.in(persons));
//		TypedQuery<PrimaryDataEntityVersionImplementation> tq = session.createQuery(query);
//		List<PrimaryDataEntityVersionImplementation> resultList = tq.setParameter(persons, datatypeList).getResultList();
//		List<PrimaryDataEntityVersionImplementation> finalresult = (List<PrimaryDataEntityVersionImplementation>)resultList;
//		List<Integer> versionIDList = new ArrayList<>();
//		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Criteria Time for Mapping in MS - Mapping IDs to Versions: "+timeElapsed);
//		for(PrimaryDataEntityVersionImplementation version : finalresult) {
//			versionIDList.add(version.getId());
//		}
		final Query<Integer> versionSQLQuery = session
				.createSQLQuery("SELECT DISTINCT v.ID " + "FROM ENTITY_VERSIONS v , metadata_map m , "
						+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_key=:key "
						+ "AND m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

		versionSQLQuery.setParameterList("list", datatypeIDList);
		versionSQLQuery.setParameter("key", element.ordinal());

		return versionSQLQuery.list();
	}

	/**
	 * Internal function to search for a {@link UntypedData}.
	 * 
	 * @param data
	 * @param fuzzy
	 * @return List<MyUntypedData>
	 * @throws ParseException                If unable to parse query string with
	 *                                       <em>LUCENE<em>.
	 * @throws PrimaryDataDirectoryException
	 */
	private List<Integer> searchByUntypedData(final UntypedData data, EnumDublinCoreElements element,
			final boolean fuzzy) throws ParseException, PrimaryDataDirectoryException {

		if (((FileSystemImplementationProvider) DataManager.getImplProv()).getConfiguration()
				.isHibernateSearchIndexingEnabled()) {
			final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

			final SearchSession ftSession = Search.session(session);
			SearchResult<MyUntypedData> searchQuery = null;

			if (fuzzy) {
				searchQuery = ftSession.search(MyUntypedData.class)
						.where(f -> f.match().field("string").matching(data.getString()).fuzzy()).fetch(200);
			} else {
				searchQuery = ftSession.search(MyUntypedData.class)
						.where(f -> f.phrase().field("string").matching(data.getString())).fetch(200);
			}

			final List<MyUntypedData> result = searchQuery.hits();

			session.close();

			return this.retrieveVersionIds(result, element);
		} else {
			final ArrayList<Integer> versionIDList = new ArrayList<>();
			IndexReader reader = null;

			// Create IndexSearcher from IndexDirectory
			try {
				Directory indexDirectory = FSDirectory.open(Paths.get(
						((FileSystemImplementationProvider) DataManager.getImplProv()).getIndexDirectory().toString(),
						"Master_Index"));
				reader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug(e.getMessage() + " \n (tried to open FSDirectory/creating IndexReader)");
			}
			IndexSearcher searcher = new IndexSearcher(reader);

			// Search Documents with Parsed Query
			org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { EnumIndexField.TITLE.value(), EnumIndexField.DESCRIPTION.value(),
							EnumIndexField.TYPE.value(), EnumIndexField.COVERAGE.value(),
							EnumIndexField.SUBJECT.value() },
					new StandardAnalyzer());
			parser.setDefaultOperator(QueryParser.OR_OPERATOR);

			String luceneString;
			if (fuzzy) {
				luceneString = data.getString() + "~";
			} else {
				luceneString = data.getString();
			}
			org.apache.lucene.search.Query luceneQuery = parser.parse(luceneString);
			ScoreDoc[] hits;
			try {
				hits = searcher.search(luceneQuery, 10).scoreDocs;
				for (int i = 0; i < hits.length; i++) {
					Document doc = searcher.doc(hits[i].doc);
					versionIDList.add(Integer.parseInt(doc.get("versionID")));
				}
			} catch (IOException e) {
				DataManager.getImplProv().getLogger()
						.debug("Error when searching for a specifc document: " + e.getMessage());
			}

			return versionIDList;
		}
	}

	/**
	 * Search for the version in the sub directories of the current directory
	 * 
	 * @param entity
	 * @param version
	 * @return the founded object
	 * @throws PrimaryDataDirectoryException
	 */
	private PrimaryDataEntity searchIntoSubdirectories(final PrimaryDataDirectory entity,
			final PrimaryDataEntityVersionImplementation version) throws PrimaryDataDirectoryException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<PrimaryDataDirectoryImplementation> directoryCriteria = builder
				.createQuery(PrimaryDataDirectoryImplementation.class);
		Root<PrimaryDataDirectoryImplementation> directoryRoot = directoryCriteria
				.from(PrimaryDataDirectoryImplementation.class);

		directoryCriteria.where(
				builder.and(builder.equal(directoryRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
						version.getPrimaryEntityId())),
				builder.equal(directoryRoot.type(), PrimaryDataDirectoryImplementation.class));

		final PrimaryDataDirectory directory = session.createQuery(directoryCriteria).uniqueResult();

		if (directory != null && this.checkIfParentEntity(entity, directory)) {
			try {
				/**
				 * no switchCurrentVersion if the object is marked as deleted
				 */
				if (!directory.getCurrentVersion().isDeleted()) {
					directory.switchCurrentVersion(version);
				}
			} catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException("Unable to switch version", e);
			}
			session.close();
			return directory;
		}

		CriteriaQuery<PrimaryDataFileImplementation> fileCriteria = builder
				.createQuery(PrimaryDataFileImplementation.class);
		Root<PrimaryDataFileImplementation> fileRoot = fileCriteria.from(PrimaryDataFileImplementation.class);

		fileCriteria.where(
				builder.and(builder.equal(fileRoot.get(PrimaryDataDirectoryImplementation.STRING_ID),
						version.getPrimaryEntityId())),
				builder.equal(fileRoot.type(), PrimaryDataFileImplementation.class));

		final PrimaryDataFile file = session.createQuery(fileCriteria).uniqueResult();

		if (file != null && this.checkIfParentEntity(entity, file)) {
			try {
				/**
				 * no switchCurrentVersion if the object is marked as deleted
				 */
				if (!file.getCurrentVersion().isDeleted()) {
					file.switchCurrentVersion(version);
				}
			} catch (final PrimaryDataEntityVersionException e) {
				throw new PrimaryDataDirectoryException("Unable to switch version", e);
			}
			session.close();
			return file;
		}
		session.close();
		return null;
	}

	/**
	 * Setter for the field <code>versionList</code>.
	 * 
	 * @param versionList a {@link SortedSet} object.
	 */
	protected void setVersionList(final SortedSet<PrimaryDataEntityVersionImplementation> versionList) {

		this.versionList = Collections.synchronizedSortedSet(versionList);
		this.setCurrentVersion(this.versionList.last());
	}

	/** {@inheritDoc} */
	@Override
	protected void storeVersion(final PrimaryDataEntityVersion publicVersion) throws PrimaryDataEntityVersionException {

		final MetaDataImplementation metadata = (MetaDataImplementation) publicVersion.getMetaData();

		/** create new version */
		final PrimaryDataEntityVersionImplementation privateVersion = new PrimaryDataEntityVersionImplementation();

		privateVersion.setCreationDate(publicVersion.getCreationDate());
		privateVersion.setPrimaryEntityId(this.getID());
		privateVersion.setMetaData(metadata);
		privateVersion.setRevision(publicVersion.getRevision());
		privateVersion.setDeleted(publicVersion.isDeleted());

		final List<PublicReferenceImplementation> list = new ArrayList<PublicReferenceImplementation>();

		for (final PublicReference publicReference : publicVersion.getPublicReferences()) {
			final PublicReferenceImplementation privateReference = new PublicReferenceImplementation(publicReference);
			privateReference.setVersion(privateVersion);
			list.add(privateReference);
		}
		privateVersion.setInternReferences(list);

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final Transaction transaction = session.beginTransaction();

		try {
			/** saveOrUpdate the finished directory */
			session.saveOrUpdate(this);
			/**
			 * saveOrUpdate the version --> Cascade.ALL --> saves automatically MetaData
			 */
			session.saveOrUpdate(privateVersion);
			transaction.commit();
		} catch (final Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			session.close();
			throw new PrimaryDataEntityVersionException("Unable to store PrimaryDataEntityVersion : " + e.getMessage(),
					e);
		}

		if (this.versionList == null) {
			this.versionList = Collections.synchronizedSortedSet(new TreeSet<PrimaryDataEntityVersionImplementation>());
			this.versionList.add(privateVersion);
			Collections.synchronizedSortedSet(this.versionList);
		} else {
			this.versionList.add(privateVersion);
			Collections.synchronizedSortedSet(this.versionList);
		}

		this.setCurrentVersion(privateVersion);

		try {
			this.setDefaultPermissions();
		} catch (final PrimaryDataEntityException e) {
			throw new PrimaryDataEntityVersionException("Unable to store default permissions : " + e.getMessage(), e);
		}

		for (final Principal principal : DataManager.getSubject().getPrincipals()) {

			final Transaction transaction2 = session.beginTransaction();

			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<PrincipalImplementation> criteria = builder.createQuery(PrincipalImplementation.class);
			Root<PrincipalImplementation> root = criteria.from(PrincipalImplementation.class);

			criteria.where(builder.and(builder.equal(root.get("name"), principal.getName())),
					builder.equal(root.get("type"), principal.getClass().getSimpleName()));

			PrincipalImplementation existingPrincipal = session.createQuery(criteria).uniqueResult();

			if (existingPrincipal != null) {
				privateVersion.setOwner(existingPrincipal);
			} else {
				throw new PrimaryDataEntityVersionException("Unable to load existing Principal");
			}

			/** version to add owner */
			session.saveOrUpdate(privateVersion);
			transaction2.commit();
			session.close();
			break;
		}

		this.setCurrentVersion(privateVersion);

	}
}