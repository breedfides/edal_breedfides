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
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;
import java.util.Arrays;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.search.backend.lucene.LuceneBackend;
import org.hibernate.search.backend.lucene.LuceneExtension;
import org.hibernate.search.backend.lucene.index.LuceneIndexManager;
import org.hibernate.search.backend.lucene.search.query.LuceneSearchQuery;
import org.hibernate.search.engine.backend.Backend;
import org.hibernate.search.engine.backend.index.IndexManager;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
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
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyDateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalDateRange;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyEdalLanguage;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifier;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyIdentifierRelation;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyLegalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyNaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyPersons;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MySubjects;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.implementation.MyUntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PublicationStatus;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalPermission;

/**
 * Implementation of {@link PrimaryDataDirectory}.
 * 
 * @author arendd
 */

@Entity
@Table(name = "ENTITIES")
@DiscriminatorColumn(columnDefinition = "char(1)", name = "TYPE", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("D")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "PrimaryDataDirectory")
public class PrimaryDataDirectoryImplementation extends PrimaryDataDirectory {

	private static final String STRING_UNABLE_TO_SWITCH_TO_CURRENT_VERSION = "Unable to switch to current version";

	private static final String CACHE_REGION_SEARCH_ENTITY = "search.entity";

	private static final String STRING_ID = "ID";

	private static final String STRING_PARENT_DIRECTORY = "parentDirectory";

	private static final String SUPPRESS_UNCHECKED_WARNING = "unchecked";

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
	 * @param path
	 *            a {@link PrimaryDataDirectory} object.
	 * @param name
	 *            a {@link String} object.
	 * @throws PrimaryDataEntityVersionException
	 *             if can not set current {@link PrimaryDataEntityVersion}.
	 * @throws PrimaryDataDirectoryException
	 *             if no parent {@link PrimaryDataDirectory} is found.
	 * @throws MetaDataException
	 *             if the {@link MetaData} object of the parent
	 *             {@link PrimaryDataDirectory} is not clone-able
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
	@Column(columnDefinition = "char(40)")
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
	 * @return List<MyDataFormat>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyDataFormat> searchByDataFormat(final DataFormat dataFormat, final boolean fuzzy)
			throws ParseException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		//org.apache.lucene.search.Query query = null;

		final SearchSession ftSession = Search.session(session);
		SearchResult<MyDataFormat> searchQuery = null;

		if (fuzzy) {
//			query = queryBuilder.keyword().fuzzy().onField("mimeType").matching(dataFormat.getMimeType())
//					.createQuery();
			searchQuery = ftSession.search( MyDataFormat.class ) 
	        .where( f -> f.match() 
	                .field( "mimeType" )
	                .matching( dataFormat.getMimeType() )
	                .fuzzy() )
	        .fetch( 200 ); 
		} else {
			//query = queryBuilder.keyword().onField("mimeType").matching(dataFormat.getMimeType()).createQuery();
			searchQuery = ftSession.search( MyDataFormat.class ) 
	        .where( f -> f.match() 
	                .field( "mimeType" )
	                .matching( dataFormat.getMimeType() ) )
	        .fetch( 200 ); 
		}

		//@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		//final Query<MyDataFormat> hibernateQuery = ftSession.createFullTextQuery(query, MyDataFormat.class);
		final List<MyDataFormat> result = searchQuery.hits();

		session.close();

		return result;
	}

	/**
	 * Internal function to search for a {@link DataType}.
	 * <p>
	 * No fuzzy search possible, because it is an EnumValue
	 * 
	 * @param dataType
	 * @return List<MyDataType>
	 */
	private List<MyDataType> searchByDataType(final DataType dataType) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final SearchSession ftSession = Search.session(session);
		SearchResult<MyDataType> searchQuery = 
			ftSession.search( MyDataType.class ) 
	        .where( f -> f.match() 
	                .field( "string" )
	                .matching( dataType.getDataType().toString().toLowerCase() ) )
	        .fetch( 200 ); 
		final List<MyDataType> result = searchQuery.hits();
		session.close();
		
		return result;
//
//		org.apache.lucene.search.Query query = null;
//
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyDataType.class).get();
//
//		query = queryBuilder.keyword().onField("string").matching(dataType.getDataType().toString().toLowerCase())
//				.createQuery();
//
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyDataType> hibernateQuery = ftSession.createFullTextQuery(query, MyDataType.class);
//		final List<MyDataType> result = hibernateQuery.list();
//
//		session.close();
//
//		return result;

	}

	/**
	 * Internal function to search for a {@link DateEvents}.
	 * 
	 * @param dateEvents
	 * @return List<MyDateEvents>
	 */
	private List<MyDateEvents> searchByDateEvents(final DateEvents dateEvents) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final List<MyDateEvents> result = new ArrayList<MyDateEvents>();

		final Set<EdalDate> set = dateEvents.getSet();

		if (set.size() > 1) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("no DateEvents with multiple BasicDates allowed");
		}

		else if (set.size() == 1) {

			for (final EdalDate edalDate : set) {

				if (edalDate instanceof EdalDateRange) {
					final List<MyEdalDateRange> list = this.searchByEDALDateRange((EdalDateRange) edalDate);
					@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
					final Query<Integer> metaDataQuery = session.createQuery(
							"select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

					metaDataQuery.setParameterList("list", list);
					final List<Integer> idlist = metaDataQuery.list();

					for (final Integer integer : idlist) {
						result.add(session.get(MyDateEvents.class, integer));
					}
				}

				else if (edalDate instanceof EdalDate) {

					final List<MyEdalDate> list = this.searchByEDALDate(edalDate);
					@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
					final Query<Integer> metaDataQuery = session.createSQLQuery(
							"select D.UNTYPEDDATA_ID from UNTYPEDDATA_MYEDALDATE D where D.SET_ID in (:list)");

					metaDataQuery.setParameterList("list", list);
					final List<Integer> idlist = metaDataQuery.list();

					for (final Integer integer : idlist) {
						result.add(session.get(MyDateEvents.class, integer));
					}
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

		final long startTime = System.currentTimeMillis();

		List<? extends MyUntypedData> datatypeList = new ArrayList<MyUntypedData>();
		try {
			if (data.getClass().equals(UntypedData.class)) {
				datatypeList = this.searchByUntypedData(data, fuzzy);
			} else if (data.getClass().equals(NaturalPerson.class)) {
				datatypeList = this.searchByNaturalPerson((NaturalPerson) data, fuzzy);
			} else if (data.getClass().equals(LegalPerson.class)) {
				datatypeList = this.searchByLegalPerson((LegalPerson) data, fuzzy);
			} else if (data.getClass().equals(Identifier.class)) {
				datatypeList = this.searchByIdentifier((Identifier) data, fuzzy);
			} else if (data.getClass().equals(DataType.class)) {
				datatypeList = this.searchByDataType((DataType) data);
			} else if (data.getClass().equals(DataFormat.class)) {
				datatypeList = this.searchByDataFormat((DataFormat) data, fuzzy);
			} else if (data.getClass().equals(DateEvents.class)) {
				datatypeList = this.searchByDateEvents((DateEvents) data);
			} else if (data.getClass().equals(IdentifierRelation.class)) {
				datatypeList = this.searchByIdentifierRelation((IdentifierRelation) data, fuzzy);
			}else if(data.getClass().equals(CheckSumType.class)) {
				datatypeList = this.searchByCheckSum((CheckSumType)data,fuzzy);
			}else if(data.getClass().equals(EdalLanguage.class)) {
				datatypeList = this.searchByEdalLanguage((EdalLanguage)data,fuzzy);
			}else if(data.getClass().equals(DataSize.class)) {
				datatypeList = this.searchByDataSize((DataSize)data,fuzzy);
			}

		} catch (final ParseException e) {
			throw new PrimaryDataDirectoryException("Unable to find the UntypedData values", e);
		}
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().debug("Zeit (Search "
				+ data.getClass().getSimpleName() + "): " + (System.currentTimeMillis() - startTime) + " msec");

		
		if(element == EnumDublinCoreElements.SUBJECT) {
			datatypeList = this.mapCollections(datatypeList, MySubjects.class, "subjects");
		}
			
		/** if no results found return empty List */
		if (datatypeList.isEmpty()) {
			return new ArrayList<PrimaryDataEntity>();
		}
		if (datatypeList.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
		}

		final List<Integer> datatypeIDList = new ArrayList<Integer>(datatypeList.size());

		for (final MyUntypedData myUntypedData : datatypeList) {
			datatypeIDList.add(myUntypedData.getId());
		}

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		long s = System.currentTimeMillis();
		final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PrimaryDataEntityVersionImplementation> query = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
		Root<PrimaryDataEntityVersionImplementation> root = query.from(PrimaryDataEntityVersionImplementation.class);
		Join<PrimaryDataEntityVersionImplementation, MetaDataImplementation> join = root.join("metaData");
		Join<MetaDataImplementation, MyUntypedData> chainedJoin = join.join("myMap");
		ParameterExpression<Collection> persons = criteriaBuilder.parameter(Collection.class);
		query.where(chainedJoin.in(persons));
		TypedQuery<PrimaryDataEntityVersionImplementation> tq = session.createQuery(query);
		List<PrimaryDataEntityVersionImplementation> resultList = tq.setParameter(persons, datatypeList).getResultList();
		List<PrimaryDataEntityVersionImplementation> finalresult = (List<PrimaryDataEntityVersionImplementation>)resultList;
		List<Integer> versionIDList = new ArrayList<>();
		long f = System.currentTimeMillis();
		long timeElapsed = f - s;
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Criteria Time for Mapping in MS - Mapping IDs to Versions: "+timeElapsed);
		for(PrimaryDataEntityVersionImplementation version : finalresult) {
			versionIDList.add(version.getId());
		}
		s = System.currentTimeMillis();
		final Query<Integer> versionSQLQuery = session
				.createSQLQuery("SELECT DISTINCT v.ID " + "FROM ENTITY_VERSIONS v , metadata_map m , "
						+ "TABLE(id BIGINT=(:list))virtual1 WHERE m.mymap_key=:key "
						+ "AND m.mymap_id=virtual1.id AND v.METADATA_ID =m.metadata_id ");

		versionSQLQuery.setParameterList("list", datatypeIDList);
		versionSQLQuery.setParameter("key", element.ordinal());

		versionIDList = versionSQLQuery.list();
		f = System.currentTimeMillis();
		timeElapsed = f - s;
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("SQL Time for Mapping in MS - Mapping IDs to Versions: "+timeElapsed);
		

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

	private List<? extends MyUntypedData> searchByDataSize(DataSize data, boolean fuzzy) {
		
		
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final SearchSession ftSession = Search.session(session);
		SearchResult<MyDataSize> searchQuery = null;
		long size = data.getFileSize();
		if(fuzzy) {
			searchQuery = ftSession.search( MyDataSize.class ) 
	        .where( f -> f.range().field("size").between(size-1, size+1) )
	        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyDataSize.class ) 
	        .where( f -> f.match() 
	                .field( "size" )
	                .matching( size ) )
	        .fetch( 200 ); 
		}
		final List<MyDataSize> result = searchQuery.hits();
		session.close();
		
		return result;
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyDataSize.class)
//				.get();
//		org.apache.lucene.search.Query query = null;
//		long size = data.getFileSize();
//		if (fuzzy && size > 0) {
//			query = queryBuilder.range().onField("size").from(size-1).to(size+1).createQuery();
//		} else {
//			query = queryBuilder.range().onField("size").from(size).to(size).createQuery();
//		}
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyDataSize> hibernateQuery = ftSession.createFullTextQuery(query, MyDataSize.class);
//
//		final List<MyDataSize> result = hibernateQuery.list();
//
//		session.close();
//		return result;
	}
	private Boolean testBool() {
		return true;
	}

	private List<? extends MyUntypedData> searchByEdalLanguage(EdalLanguage data, boolean fuzzy) {
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final SearchSession ftSession = Search.session(session);
		SearchScope<MyEdalLanguage> scope = ftSession.scope( MyEdalLanguage.class );
		SearchResult<MyEdalLanguage> searchQuery = null;
		if(fuzzy) {
//			SearchPredicate pred = scope.predicate().match().field( "title" )
//            .matching( "robot" )
//            .toPredicate();
			searchQuery = ftSession.search( MyEdalLanguage.class ) 
	        .where( f -> f.match() 
	                .field( "language" )
	                .matching( data.getLanguage() )	            
	                .fuzzy())
	        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyEdalLanguage.class ) 
	        .where( f -> f.match() 
	                .field( "language" )
	                .matching( data.getLanguage() ) )
	        .fetch( 200 ); 
		}
		final List<MyEdalLanguage> result = searchQuery.hits();
		session.close();
		
		return result;
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyEdalLanguage.class)
//				.get();
//		org.apache.lucene.search.Query query = null;
//		
//		if (fuzzy) {
//			query = queryBuilder.keyword().fuzzy().onField("language").matching(data.toString())
//					.createQuery();
//		} else {
//			query = queryBuilder.keyword().onField("language").matching(data.toString()).createQuery();
//		}
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyEdalLanguage> hibernateQuery = ftSession.createFullTextQuery(query, MyEdalLanguage.class);
//
//		final List<MyEdalLanguage> result = hibernateQuery.list();
//
//		session.close();
//	
//		return result;
	}

	private List<? extends MyUntypedData> mapCollections(List<? extends MyUntypedData> datatypeList, Class<?> collectionClass, String setName) {
		
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();	
		final CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<?> query = builder.createQuery(collectionClass);
		Root<?> root = query.from(collectionClass);
		Join<?, ?> join = root.join(setName);
		ParameterExpression<Collection> set = builder.parameter(Collection.class);
		query.where(join.in(set));
		TypedQuery<?> tq = session.createQuery(query);
		List<?> resultList = tq.setParameter(set, datatypeList).getResultList();
		List<? extends MyUntypedData> finalresult = (List<? extends MyUntypedData>)resultList;

		session.close();

		return finalresult;
	}

	private List<? extends MyUntypedData> searchByCheckSum(CheckSumType data, boolean fuzzy) {
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		
		final SearchSession ftSession = Search.session(session);
		SearchResult<MyCheckSumType> searchQuery = null;
		if(fuzzy) {
			searchQuery = ftSession.search( MyCheckSumType.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "algorithm" ).matching( data.getAlgorithm() ).fuzzy())
		        		.must(f.match().fields( "checkSum" ).matching( data.getCheckSum() ).fuzzy()))
		        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyCheckSumType.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "algorithm" ).matching( data.getAlgorithm() ).fuzzy())
		        		.must(f.match().fields( "checkSum" ).matching( data.getCheckSum() )))
		        .fetch( 200 ); 
		}
		final List<MyCheckSumType> result = searchQuery.hits();
		List<? extends MyUntypedData> datatypeList = this.mapCollections(result, MyCheckSum.class, "dataSet");
		session.close();
		
		return datatypeList;
		
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
	 */
	private List<MyEdalDate> searchByEDALDate(final EdalDate edalDate) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<MyEdalDate> dataCriteria = builder.createQuery(MyEdalDate.class);
		Root<MyEdalDate> rootDate = dataCriteria.from(MyEdalDate.class);

		final int precission = edalDate.getStartPrecision().ordinal();
		final Calendar date = edalDate.getStartDate();
		
		ArrayList<Predicate> predicates = new ArrayList<>();
		if (precission == EdalDatePrecision.CENTURY.ordinal()) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("no Dates with CENTURY Precission allowed");
			return new ArrayList<MyEdalDate>();

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

//									if (precission >= EdalDatePrecision.MILLISECOND.ordinal()) {
//
//										Expression<String> millisecondExpression = builder.function("MILISECOND",
//												String.class, rootDate.get("startDate"));
//
//										dataCriteria.where(
//												builder.equal(millisecondExpression, date.get(Calendar.MILLISECOND)));
//									}
								}
							}
						}
					}
				}
			}
		}
		Predicate finalQuery = builder.and(predicates.toArray(new Predicate[0]));
		dataCriteria.where(finalQuery);
		final List<MyEdalDate> result = session.createQuery(dataCriteria).list();
		session.close();

		return result;

	}

	/**
	 * Internal function to search for a {@link EdalDateRange}.
	 * 
	 * @param edalDateRange
	 * @return List<MyEDALDateRange>
	 */
	private List<MyEdalDateRange> searchByEDALDateRange(final EdalDateRange edalDateRange) {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<MyEdalDateRange> dataCriteria = builder.createQuery(MyEdalDateRange.class);
		Root<MyEdalDateRange> rootDate = dataCriteria.from(MyEdalDateRange.class);

		final int precissionStart = edalDateRange.getStartPrecision().ordinal();
		final Calendar dateStart = edalDateRange.getStartDate();

		final int precissionEnd = edalDateRange.getEndPrecision().ordinal();
		final Calendar dateEnd = edalDateRange.getEndDate();

		if (precissionStart == EdalDatePrecision.CENTURY.ordinal()
				|| precissionEnd == EdalDatePrecision.CENTURY.ordinal()) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("no DateRanges with CENTURY Precission allowed");
			return new ArrayList<MyEdalDateRange>();

		}
		ArrayList<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.lessThanOrEqualTo(rootDate.<Calendar>get("startDate"), edalDateRange.getStartDate()));
		predicates.add(builder.greaterThanOrEqualTo(rootDate.<Calendar>get("endDate"), edalDateRange.getEndDate()));
//
//		if (precissionStart >= EdalDatePrecision.DECADE.ordinal()) {
//
//			/** note: use DECADE(date) if the database-SQL support */
//
//			Expression<String> yearExpression = builder.function("YEAR", String.class, rootDate.get("startDate"));
//
//			dataCriteria.where(builder.equal(builder.substring(yearExpression, 1, 3),
//					Integer.toString(dateStart.get(Calendar.YEAR)).substring(0, 3)));
//
//			if (precissionStart >= EdalDatePrecision.YEAR.ordinal()) {
//
//				dataCriteria.where(builder.equal(yearExpression, dateStart.get(Calendar.YEAR)));
//
//				if (precissionStart >= EdalDatePrecision.MONTH.ordinal()) {
//					/** very important: Calendar count months from 0-11 */
//					Expression<String> monthExpression = builder.function("MONTH", String.class,
//							rootDate.get("startDate"));
//
//					dataCriteria.where(builder.equal(monthExpression, (dateStart.get(Calendar.MONTH) + 1)));
//
//					if (precissionStart >= EdalDatePrecision.DAY.ordinal()) {
//
//						Expression<String> dayExpression = builder.function("DAY", String.class,
//								rootDate.get("startDate"));
//
//						dataCriteria.where(builder.equal(dayExpression, dateStart.get(Calendar.DAY_OF_MONTH)));
//
//						if (precissionStart >= EdalDatePrecision.HOUR.ordinal()) {
//
//							Expression<String> hourExpression = builder.function("HOUR", String.class,
//									rootDate.get("startDate"));
//
//							dataCriteria.where(builder.equal(hourExpression, dateStart.get(Calendar.HOUR_OF_DAY)));
//
//							if (precissionStart >= EdalDatePrecision.MINUTE.ordinal()) {
//
//								Expression<String> minuteExpression = builder.function("MINUTE", String.class,
//										rootDate.get("startDate"));
//
//								dataCriteria.where(builder.equal(minuteExpression, dateStart.get(Calendar.MINUTE)));
//
//								if (precissionStart >= EdalDatePrecision.SECOND.ordinal()) {
//
//									Expression<String> secondExpression = builder.function("SECOND", String.class,
//											rootDate.get("startDate"));
//
//									dataCriteria.where(builder.equal(secondExpression, dateStart.get(Calendar.SECOND)));
//
////									if (precissionStart >= EdalDatePrecision.MILLISECOND.ordinal()) {
////
////										Expression<String> millisecondExpression = builder.function("MILLISECOND",
////												String.class, rootDate.get("startDate"));
////
////										dataCriteria.where(builder.equal(millisecondExpression,
////												dateStart.get(Calendar.MILLISECOND)));
////									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//		if (precissionEnd >= EdalDatePrecision.DECADE.ordinal()) {
//
//			/** note: use DECADE(date) if the database-SQL support */
//
//			Expression<String> yearExpression = builder.function("YEAR", String.class, rootDate.get("endDate"));
//
//			dataCriteria.where(builder.equal(builder.substring(yearExpression, 1, 3),
//					Integer.toString(dateEnd.get(Calendar.YEAR)).substring(0, 3)));
//
//			if (precissionEnd >= EdalDatePrecision.YEAR.ordinal()) {
//
//				dataCriteria.where(builder.equal(yearExpression, dateEnd.get(Calendar.YEAR)));
//
//				if (precissionEnd >= EdalDatePrecision.MONTH.ordinal()) {
//					/** very important: Calendar count months from 0-11 */
//					Expression<String> monthExpression = builder.function("MONTH", String.class,
//							rootDate.get("endDate"));
//
//					dataCriteria.where(builder.equal(monthExpression, (dateEnd.get(Calendar.MONTH) + 1)));
//
//					if (precissionEnd >= EdalDatePrecision.DAY.ordinal()) {
//
//						Expression<String> dayExpression = builder.function("DAY", String.class,
//								rootDate.get("endDate"));
//
//						dataCriteria.where(builder.equal(dayExpression, dateEnd.get(Calendar.DAY_OF_MONTH)));
//
//						if (precissionEnd >= EdalDatePrecision.HOUR.ordinal()) {
//
//							Expression<String> hourExpression = builder.function("HOUR", String.class,
//									rootDate.get("endDate"));
//
//							dataCriteria.where(builder.equal(hourExpression, dateEnd.get(Calendar.HOUR_OF_DAY)));
//
//							if (precissionEnd >= EdalDatePrecision.MINUTE.ordinal()) {
//
//								Expression<String> minuteExpression = builder.function("MINUTE", String.class,
//										rootDate.get("endDate"));
//
//								dataCriteria.where(builder.equal(minuteExpression, dateEnd.get(Calendar.MINUTE)));
//
//								if (precissionEnd >= EdalDatePrecision.SECOND.ordinal()) {
//
//									Expression<String> secondExpression = builder.function("SECOND", String.class,
//											rootDate.get("endDate"));
//
//									dataCriteria.where(builder.equal(secondExpression, dateEnd.get(Calendar.SECOND)));
//
////									if (precissionEnd >= EdalDatePrecision.MILLISECOND.ordinal()) {
////
////										Expression<String> millisecondExpression = builder.function("MILLISECOND",
////												String.class, rootDate.get("endDate"));
////
////										dataCriteria.where(builder.equal(millisecondExpression,
////												dateEnd.get(Calendar.MILLISECOND)));
////									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}

		dataCriteria.where(predicates.toArray(new Predicate[0]));
		final List<MyEdalDateRange> result = session.createQuery(dataCriteria).list();

		session.close();

		return result;
	}

	/**
	 * Internal function to search for a {@link Identifier}.
	 * 
	 * @param identifier
	 * @param fuzzy
	 * @return List<MyIdentifier>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyIdentifier> searchByIdentifier(final Identifier identifier, final boolean fuzzy)
			throws ParseException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		

		final SearchSession ftSession = Search.session(session);
		SearchResult<MyIdentifier> searchQuery = null;
		if(fuzzy) {
			searchQuery = ftSession.search( MyIdentifier.class ) 
	        .where( f -> f.match() 
	                .field( "identifier" )
	                .matching( identifier.getID() )
	                .fuzzy())
	        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyIdentifier.class ) 
	        .where( f -> f.match() 
	                .field( "identifier" )
	                .matching( identifier.getID() ) )
	        .fetch( 200 ); 
		}
		final List<MyIdentifier> result = searchQuery.hits();
		session.close();
		
		return result;
		
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
	 * @param fuzzy
	 * @return List<MyIdentifierRelation>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyIdentifierRelation> searchByIdentifierRelation(final IdentifierRelation identifierRelation,
			final boolean fuzzy) throws ParseException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		final List<MyIdentifierRelation> result = new ArrayList<MyIdentifierRelation>();

		if (identifierRelation.getRelations().size() > 1) {
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger()
					.warn("only IdentifierRelations with only one Identifier allowed");
		}

		else if (identifierRelation.getRelations().size() == 1) {

			Identifier id = null;
			for (final Identifier identifier : identifierRelation) {
				id = identifier;
			}

			final List<MyIdentifier> myIdentifierList = this.searchByIdentifier(id, fuzzy);

			if (!myIdentifierList.isEmpty()) {
				@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
				final Query<Integer> metaDataQuery = session
						.createQuery("select D.id from MyIdentifierRelation D join D.relations V where V in (:list)");

				metaDataQuery.setParameterList("list", myIdentifierList);

				final List<Integer> idlist = metaDataQuery.list();

				for (final Integer integer : idlist) {
					result.add(session.get(MyIdentifierRelation.class, integer));

				}
			}

		}
		return result;

	}

	@Override
	protected List<? extends PrimaryDataEntity> searchByKeywordImpl(final String keyword, final boolean fuzzy,
			final boolean recursiveIntoSubdirectories) throws PrimaryDataDirectoryException {


		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		org.apache.lucene.search.Query query = null;

		//final FullTextSession ftSession = Search.getFullTextSession(session);
		final SearchSession ftSession = Search.session(session);
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyUntypedData.class)
//				.get();
//
//		SearchFactory searchFactory = ftSession.getSearchFactory();
		//org.hibernate.Query fullTextQuery = null;
		SearchResult<MyNaturalPerson> searchQuery = null;
		//final FullTextSession ftSession = Search.getFullTextSession(session);

//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyLegalPerson.class)
//				.get();
//
//		org.apache.lucene.search.Query combinedQuery = null;

		List<MyUntypedData> hits = ftSession.search( Arrays.asList(MyUntypedData.class,MyNaturalPerson.class) ) 
	        .where( f -> f.bool()
	        		.must(f.match().fields( "string" ).matching( "titanfall"))
	        		.must(f.match().fields( "givenName" ).matching( "Asterix")))
	        .fetchHits(20);
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Hits: "+hits.size());


//		List<MyUntypedData> hits = ftSession.search( MyUntypedData.class )
//		        .where( f -> f.match()
//		                .field( "givenName" ).field( "string" )
//		                .matching( keyword ) )
//		        .fetchHits( 20 );



		//@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		//final Query<MyLegalPerson> hibernateQuery = ftSession.createFullTextQuery(combinedQuery, MyLegalPerson.class);

		//final List<MyNaturalPerson> result = searchQuery.hits();
		
		SearchMapping mapping = Search.mapping(session.getSessionFactory()); 
		Backend backend = mapping.backend(); 
		LuceneBackend luceneBackend = backend.unwrap( LuceneBackend.class ); 
		Optional<? extends Analyzer> analyzer = luceneBackend.analyzer( "default" );
		final long startTime = System.currentTimeMillis();
		org.apache.lucene.queryparser.classic.MultiFieldQueryParser parser =
			    new MultiFieldQueryParser(new String[]{"string","givenName",
			    		"sureName","country","zip","addressLine","legalName",
			    		"id","identifier","mimeType","checkSum","algorithm",
			    		"size","language"}, analyzer.get());
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		SearchResult<MetaDataImplementation> searchResult = null;
			try {
				org.apache.lucene.search.Query luceneQuery = parser.parse(keyword);
				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Parsed Query: "+luceneQuery.toString());

				searchResult = ftSession.search(ftSession.scope(MetaDataImplementation.class))//Searches all Indexes of that type and Sub Types!
				        .extension( LuceneExtension.get() ) 
				        .where( f -> f.fromLuceneQuery( 
				                luceneQuery ))
				        .fetch(200);
//				fullTextQuery = ftSession.
//				((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Lucenequery: "+fullTextQuery.toString());
			}
			catch (ParseException e) {
			    //handle parsing failure
			}




			List<? extends MetaDataImplementation> datatypes = searchResult.hits(); //return a list of managed objects
			((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("DatatypeList Size: "+datatypes.size());
			
			//Checksummentypen, MyNaturalPerson, MyLEgalPerson mappen und neue Liste bauen
			
//		if (fuzzy) {
//			if (this.consistsQueryParserSyntax(keyword)) {
//				query = queryBuilder.keyword().wildcard().onFields("string", "givenName", "sureName", "country", "zip",
//						"addressLine", "id", "identifier", "mimeType").matching(keyword).createQuery();
//			} else {
//				query = queryBuilder
//						.keyword().fuzzy().onFields("string", "givenName", "sureName", "country", "zip",
//								"addressLine", "id", "identifier", "mimeType")
//						.matching(keyword).createQuery();
//			}
//		} else {
//			query = queryBuilder.keyword().wildcard().onFields("string", "givenName", "sureName", "country", "zip",
//					"addressLine", "id", "identifier", "mimeType").matching(keyword).createQuery();
//		}
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyUntypedData> hibernateQuery = ftSession.createFullTextQuery(query, UntypedData.class);
//
//		final List<MyUntypedData> datatypeList = hibernateQuery.list();

		//session.close();
		Collection<MyUntypedData> datatypeList = new ArrayList<>();
		/** if no results found return empty List */
		if (datatypes.isEmpty()) {
			return new ArrayList<PrimaryDataEntity>();
		}
		if (datatypes.size() > PrimaryDataDirectoryImplementation.MAX_NUMBER_SEARCH_RESULTS) {
			throw new PrimaryDataDirectoryException("find to much result please repeat query with more details");
		}else {
		//filter Collection-associated Objects for mapping
			ArrayList<MyNaturalPerson> naturalPersons = new ArrayList<>();
			ArrayList<MyCheckSumType> checksumTypes = new ArrayList<>();
			ArrayList<MyUntypedData> maybeSubjects = new ArrayList<>();
			//Subjects - MyUntypedData
//			for(MetaDataImplementation data: datatypes) {
//				if(data instanceof MyNaturalPerson) {
//					naturalPersons.add((MyNaturalPerson) data);
//				}else if(data instanceof MyCheckSumType) {
//					checksumTypes.add((MyCheckSumType)data);
//				}else {
//					datatypeList.add(data);
//				}
//			}
			//Values getting deleted, is that a problem?
			datatypeList.addAll(this.mapCollections(maybeSubjects, MySubjects.class, "subjects"));
			if(naturalPersons.size() > 0) {
				datatypeList.addAll(this.mapCollections(naturalPersons, MyPersons.class, "persons"));
			}
			if(checksumTypes.size() > 0) {

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

		final List<Integer> versionIDList = versionSQLQuery.list();
//		long s = System.currentTimeMillis();
//		final CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//		CriteriaQuery<PrimaryDataEntityVersionImplementation> cirQuery = criteriaBuilder.createQuery(PrimaryDataEntityVersionImplementation.class);
//		Root<PrimaryDataEntityVersionImplementation> root = cirQuery.from(PrimaryDataEntityVersionImplementation.class);
//		Join<PrimaryDataEntityVersionImplementation, MetaDataImplementation> join = root.join("metaData");
//		Join<MetaDataImplementation, MyUntypedData> chainedJoin = join.join("myMap");
//		ParameterExpression<Collection> persons = criteriaBuilder.parameter(Collection.class);
//		cirQuery.where(chainedJoin.in(persons));
//		TypedQuery<PrimaryDataEntityVersionImplementation> tq = session.createQuery(cirQuery);
//		List<PrimaryDataEntityVersionImplementation> resultList = tq.setParameter(persons, datatypeList).getResultList();
//		List<PrimaryDataEntityVersionImplementation> finalresult = (List<PrimaryDataEntityVersionImplementation>)resultList;
//		List<Integer> versionIDList = new ArrayList<>();
//		long f = System.currentTimeMillis();
//		long timeElapsed = f - s;
//		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Criteria Time for Mapping in MS: "+timeElapsed);
//		for(PrimaryDataEntityVersionImplementation version : finalresult) {
//			versionIDList.add(version.getId());
//		}

		final HashSet<PrimaryDataEntity> resultSet = new HashSet<PrimaryDataEntity>();

		final long startEntityQuery = System.currentTimeMillis();

		if (!recursiveIntoSubdirectories) {
			for (final Integer version : versionIDList) {
				final PrimaryDataEntityVersionImplementation currentVersion = session2
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
				final PrimaryDataEntityVersionImplementation currentVersion = session2
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
			session2.close();

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
				.info("Zeit (Search by keyword): " + (System.currentTimeMillis() - startTime) + " msec");
		return results;
	}

	/**
	 * Internal function to search for a {@link LegalPerson}.
	 * 
	 * @param legalPerson
	 * @param fuzzy
	 * @return List<MyLegalPerson>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyLegalPerson> searchByLegalPerson(final LegalPerson legalPerson, final boolean fuzzy)
			throws ParseException {

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final SearchSession ftSession = Search.session(session);
		SearchResult<MyLegalPerson> searchQuery = null;
		//final FullTextSession ftSession = Search.getFullTextSession(session);

//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyLegalPerson.class)
//				.get();
//
//		org.apache.lucene.search.Query combinedQuery = null;

		if(fuzzy) {
			searchQuery = ftSession.search( MyLegalPerson.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "legalName" ).matching( legalPerson.getLegalName() ).fuzzy())
		        		.must(f.match().fields( "addressLine" ).matching( legalPerson.getAddressLine() ).fuzzy())
		        		.must(f.match().fields( "zip" ).matching( legalPerson.getZip() ).fuzzy())
						.must(f.match().fields( "country" ).matching( legalPerson.getCountry()).fuzzy()))
		        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyLegalPerson.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "legalName" ).matching( legalPerson.getLegalName() ))
		        		.must(f.match().fields( "addressLine" ).matching( legalPerson.getAddressLine() ))
		        		.must(f.match().fields( "zip" ).matching( legalPerson.getZip() ))
						.must(f.match().fields( "country" ).matching( legalPerson.getCountry())))
		        .fetch( 200 ); 
		}

		//@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		//final Query<MyLegalPerson> hibernateQuery = ftSession.createFullTextQuery(combinedQuery, MyLegalPerson.class);

		final List<MyLegalPerson> result = searchQuery.hits();

		session.close();
		return result;

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
	 * @param naturalPerson
	 * @param fuzzy
	 * @return List<MyNaturalPerson>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyNaturalPerson> searchByNaturalPerson(final NaturalPerson naturalPerson, final boolean fuzzy)
			throws ParseException {
		
		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
		final SearchSession ftSession = Search.session(session);
		SearchResult<MyNaturalPerson> searchQuery = null;
		//final FullTextSession ftSession = Search.getFullTextSession(session);

//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyLegalPerson.class)
//				.get();
//
//		org.apache.lucene.search.Query combinedQuery = null;

		if(fuzzy) {
			searchQuery = ftSession.search( MyNaturalPerson.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "givenName" ).matching( naturalPerson.getGivenName() ).fuzzy())
		        		.must(f.match().fields( "sureName" ).matching( naturalPerson.getSureName() ).fuzzy())
		        		.must(f.match().fields( "addressLine" ).matching( naturalPerson.getAddressLine() ).fuzzy())
		        		.must(f.match().fields( "zip" ).matching( naturalPerson.getZip() ).fuzzy())
						.must(f.match().fields( "country" ).matching( naturalPerson.getCountry()).fuzzy()))
		        .fetch( 200 ); 
		}else {
			searchQuery = ftSession.search( MyNaturalPerson.class ) 
		        .where( f -> f.bool()
		        		.must(f.match().fields( "givenName" ).matching( naturalPerson.getGivenName() ))
		        		.must(f.match().fields( "sureName" ).matching( naturalPerson.getSureName() ))
		        		.must(f.match().fields( "addressLine" ).matching( naturalPerson.getAddressLine() ))
		        		.must(f.match().fields( "zip" ).matching( naturalPerson.getZip() ))
						.must(f.match().fields( "country" ).matching( naturalPerson.getCountry())))
		        .fetch( 200 ); 
		}

		//@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
		//final Query<MyLegalPerson> hibernateQuery = ftSession.createFullTextQuery(combinedQuery, MyLegalPerson.class);

		final List<MyNaturalPerson> result = searchQuery.hits();

		
		
		
//
//		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();
//		
//
//		final FullTextSession ftSession = Search.getFullTextSession(session);
//
//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyNaturalPerson.class)
//				.get();
//
//		org.apache.lucene.search.Query combinedQuery = null;
//
//		if (fuzzy) {
//			org.apache.lucene.search.Query queryA = queryBuilder.keyword().fuzzy().onField("givenName")
//					.matching(naturalPerson.getGivenName()).createQuery();
//			org.apache.lucene.search.Query queryB = queryBuilder.keyword().fuzzy().onField("sureName")
//					.matching(naturalPerson.getSureName()).createQuery();
//			org.apache.lucene.search.Query queryC = queryBuilder.keyword().fuzzy().onField("addressLine")
//					.matching(naturalPerson.getAddressLine()).createQuery();
//			org.apache.lucene.search.Query queryD = queryBuilder.keyword().fuzzy().onField("zip")
//					.matching(naturalPerson.getZip()).createQuery();
//			org.apache.lucene.search.Query queryE = queryBuilder.keyword().fuzzy().onField("country")
//					.matching(naturalPerson.getCountry()).createQuery();
//
//			combinedQuery = queryBuilder.bool().must(queryA).must(queryB).must(queryC).must(queryD)
//					.must(queryE).createQuery();
//
//		} else {
//			org.apache.lucene.search.Query queryA = queryBuilder.phrase().onField("givenName")
//					.sentence(naturalPerson.getGivenName()).createQuery();
//			org.apache.lucene.search.Query queryB = queryBuilder.keyword().onField("sureName")
//					.matching(naturalPerson.getSureName()).createQuery();
//			org.apache.lucene.search.Query queryC = queryBuilder.keyword().onField("addressLine")
//					.matching(naturalPerson.getAddressLine()).createQuery();
//			org.apache.lucene.search.Query queryD = queryBuilder.keyword().onField("zip")
//					.matching(naturalPerson.getZip()).createQuery();
//			org.apache.lucene.search.Query queryE = queryBuilder.keyword().onField("country")
//					.matching(naturalPerson.getCountry()).createQuery();
//
//			combinedQuery = queryBuilder.bool().must(queryA).must(queryB).must(queryC).must(queryD)
//					.must(queryE).createQuery();
//		}
//
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyNaturalPerson> hibernateQuery = ftSession.createFullTextQuery(combinedQuery,
//				MyNaturalPerson.class);
//
//		final List<MyNaturalPerson> result = hibernateQuery.list();
//		
		long s = System.currentTimeMillis();
		ArrayList<Integer> ids = new ArrayList<>();
		for(MyNaturalPerson mp : result) {
			ids.add(mp.getId());		
		}
		
		final Query<Integer> versionSQLQuery = session.createSQLQuery(
				"Select distinct UNTYPEDDATA_ID FROM UNTYPEDDATA_PERSONS up, TABLE(id BIGINT=(:list))list WHERE up.PERSONS_ID = list.id");

		versionSQLQuery.setParameterList("list", ids);

		final List<Integer> versionIDList = versionSQLQuery.list();
		final List<MyNaturalPerson> result2 = new ArrayList<>();
		for(Integer i : versionIDList) {
			MyNaturalPerson np = new MyNaturalPerson();
			np.setId(i);
			result2.add(np);
		}
		long f = System.currentTimeMillis();
		long timeElapsed = f - s;
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("SQL Time for Mapping in MS - NaturalPerson "+timeElapsed);
		
		s = System.currentTimeMillis();
		//search for untypeddata Reports with related PersonSets		
		final CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<MyPersons> query = builder.createQuery(MyPersons.class);
		Root<MyPersons> root = query.from(MyPersons.class);
		Join<MyPersons, MyNaturalPerson> join = root.join("persons");
		ParameterExpression<Collection> persons = builder.parameter(Collection.class);
		query.where(join.in(persons));
		TypedQuery<MyPersons> tq = session.createQuery(query);
		List<?> resultList = tq.setParameter(persons, result).getResultList();
		List<MyNaturalPerson> finalresult = (List<MyNaturalPerson>)resultList;
		f = System.currentTimeMillis();
		timeElapsed = f - s;
		((FileSystemImplementationProvider) DataManager.getImplProv()).getLogger().info("Criteria Query Time in MS - NaturalPerson: "+timeElapsed);
		
		
		session.close();
		return finalresult;

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

	/**
	 * Internal function to search for a {@link UntypedData}.
	 * 
	 * @param data
	 * @param fuzzy
	 * @return List<MyUntypedData>
	 * @throws ParseException
	 *             If unable to parse query string with <em>LUCENE<em>.
	 */
	private List<MyUntypedData> searchByUntypedData(final UntypedData data, final boolean fuzzy) throws ParseException {
		long start = System.currentTimeMillis();

		final Session session = ((FileSystemImplementationProvider) DataManager.getImplProv()).getSession();

		//org.apache.lucene.search.Query query = null;
		
		final SearchSession ftSession = Search.session(session);
		SearchResult<MyUntypedData> searchQuery = null;

		if (fuzzy) {
//			query = queryBuilder.keyword().fuzzy().onField("mimeType").matching(dataFormat.getMimeType())
//					.createQuery();
			searchQuery = ftSession.search( MyUntypedData.class ) 
	        .where( f -> f.match() 
	                .field( "string" )
	                .matching( data.getString() )
	                .fuzzy() )
	        .fetch( 200 ); 
		} else {
			//query = queryBuilder.keyword().onField("mimeType").matching(dataFormat.getMimeType()).createQuery();
			searchQuery = ftSession.search( MyUntypedData.class ) 
	        .where( f -> f.phrase() 
	                .field( "string" )
	                .matching( data.getString() ) )
	        .fetch( 200 ); 
		}

//		QueryBuilder queryBuilder = ftSession.getSearchFactory().buildQueryBuilder().forEntity(MyUntypedData.class)
//				.get();
//		if (fuzzy) {
//			query = queryBuilder.keyword().fuzzy().onField("string").matching(data.getString())
//					.createQuery();
//		} else {
//			query = queryBuilder.keyword().onField("string").matching(data.getString()).createQuery();
//		}
//		@SuppressWarnings(PrimaryDataDirectoryImplementation.SUPPRESS_UNCHECKED_WARNING)
//		final Query<MyUntypedData> hibernateQuery = ftSession.createFullTextQuery(query, MyUntypedData.class);

		final List<MyUntypedData> result = searchQuery.hits();

		session.close();

		return result;
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
	 * @param versionList
	 *            a {@link SortedSet} object.
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
			publicVersion.getMetaData().getWrapper().setVersionId(privateVersion.getId());
//			java.nio.file.Path indexPath = Paths.get(((FileSystemImplementationProvider)DataManager.getImplProv()).getIndexDirectory().toString(),"MyUntypedDataWrapper");
//			Directory indexDirectory = FSDirectory.open(indexPath);
//			StandardAnalyzer analyzer = new StandardAnalyzer();
//		    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//		    IndexWriter writer = new IndexWriter(indexDirectory, iwc);
//		    Document doc = new Document();
//		    doc.add(new TextField("string", privateVersion.getMetaData().getWrapper().getStrings(),Store.YES));
//		    if( privateVersion.getMetaData().getWrapper().getGivenName() != null)
//		    	doc.add(new TextField("givenName", privateVersion.getMetaData().getWrapper().getGivenName(),Store.YES));
//		    writer.addDocument(doc);
//		    writer.close();
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